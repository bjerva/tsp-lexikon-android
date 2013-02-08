package com.bjerva.tsplex;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends FragmentActivity {
	private String jsonURL = "http://130.237.171.46/signs.json";
	private final String FILENAME = "signUpdates.txt";
	private SignListFragment listFragment;
	//private SignDetailFragment detFragment;
	private ProgressDialog pbarDialog;
	private String dbName;
	private ObjectContainer db;
	
	ArrayList<GsonSign> gsonSigns = null;
	GsonSign currentSign = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(new Bundle()); //XXX: Simple ugly fix.

		setContentView(R.layout.activity_sign_listing);
		
		dbName = this.getDir("data", 0) + "/" + "signs.db4o";
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), dbName);
		
		final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		final NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isConnected()) {
			updateSigns();
		} else {
			Toast.makeText(this, "Hittade ingen WiFi-uppkoppling. Teckenlistan uppdateras inte.", Toast.LENGTH_LONG).show();
		}
		
		//Load local json
		new LoadHelper(this).execute();
		
		SignListFragment listFrag = (SignListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_frag);
		if (listFrag == null) {
			listFragment = new SignListFragment();
			getSupportFragmentManager().beginTransaction().add(
					R.id.fragment_container, listFragment).commit();
		}
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    LinearLayout linLay = (LinearLayout) findViewById(R.id.detailLayout);
	    linLay.setOrientation(LinearLayout.HORIZONTAL);
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sign_listing, menu);
		return true;
	}

	GsonSign getCurrentSign(){
		return currentSign;
	}
	
	void showLoader(){
		//Show spinner
		pbarDialog = new ProgressDialog(this);
		pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pbarDialog.setMessage("Hämtar tecken...");
		pbarDialog.setCancelable(false);
		pbarDialog.show();
	}
	
	void hideLoader(){
		pbarDialog.dismiss();
	}

    void errorPlayingVideo() {
    	SignListFragment listFrag = (SignListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_frag);
    	if(listFrag == null){
    		super.onBackPressed();   
    	}
        Toast.makeText(this, "Video could not be played.", Toast.LENGTH_LONG).show();
    }
    
    void networkError() {
    	SignListFragment listFrag = (SignListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_frag);
    	if(listFrag == null){
    		super.onBackPressed();   
    	} 
        Toast.makeText(this, "No internet connection found!", Toast.LENGTH_LONG).show();
    }
	
	private void loadData(){
		Log.i("SyncDBLoad", "Loading");
		final ObjectSet<GsonSign> dbList = db.queryByExample(new GsonSign());
		while(dbList.hasNext()){
			gsonSigns.add(dbList.next());
		}
		Log.i("SyncDBLoad", "Loaded");
	}
	
	private void updateSigns(){
		final String oldDate = getOldDate();
		if(oldDate.length()>0){
			jsonURL += "?changed_at="+oldDate;
		}
		
		Log.i("New JSON-url", jsonURL);
		
		//Load sign info from backend
		new JSONParser(this).execute(jsonURL);
		
		saveRetrievalDate();
	}
	
	private void saveRetrievalDate(){
		//Write retrieval date to file
		final Calendar c = Calendar.getInstance();
		String date = String.valueOf(c.get(Calendar.YEAR));
		if (c.get(Calendar.MONTH)+1 < 10){
			date += "-0"+(c.get(Calendar.MONTH)+1);
		} else {
			date += "-"+(c.get(Calendar.MONTH)+1);
		}

		date += "-"+c.get(Calendar.DATE);

		final FileOutputStream fos;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(date.getBytes());
			fos.close();
		} catch (IOException e1) {
			Log.e("ERROR", "Error saving new file");
		}
		
		Log.i("NEW DATE", date);
	}
	
	private String getOldDate(){
		//Load retrieval date to file
		final FileInputStream fis;
		final StringBuffer fileContent;
		String oldDate = "2013-01-30";
		try {
			fis = openFileInput(FILENAME);
			fileContent = new StringBuffer("");

			byte[] buffer = new byte[1];
			@SuppressWarnings("unused")
			int length;
			while ((length = fis.read(buffer)) != -1) {
				fileContent.append(new String(buffer));
			}
			oldDate = fileContent.toString().trim();
		} catch (IOException e1) {
			Log.e("ERROR", "Error retrieving old file");
		}
		
		return oldDate;
	}
	
	private void loadGSONfromString() throws IOException, JSONException{
		Log.i("Load Local JSON", "Loading...");
		final InputStream is = getAssets().open("signs.json");
		final Reader reader = new InputStreamReader(is);
		
		final Gson gson = new Gson();
		final Type collectionType = new TypeToken<ArrayList<GsonSign>>(){}.getType();
		gsonSigns = gson.fromJson(reader, collectionType);
		
		is.close();
		Log.i("Load Local GSON", "Loaded!");
	}
	
	private class JSONParser extends AsyncTask<String, Void, Void>{
		 
	    InputStream is = null;
	    final ProgressDialog pbarDialog;
	    
	    public JSONParser(MainActivity activity) {
	        this.pbarDialog = new ProgressDialog(activity);
	    }
	    
	    @Override
		protected Void doInBackground(String... url) {
	        // Making HTTP request
	    	Log.i("Load Remote GSON", "Loading...");
	        try {
	        	final HttpClient client = new DefaultHttpClient();
	        	final HttpGet httpGet = new HttpGet(url[0]);
	        	
	            // defaultHttpClient
	            final HttpResponse httpResponse = client.execute(httpGet);
	            final HttpEntity httpEntity = httpResponse.getEntity();
	            is = httpEntity.getContent();           
	 
	            final Reader reader = new InputStreamReader(is);
				
				final Gson gson = new Gson();
				final Type collectionType = new TypeToken<ArrayList<GsonSign>>(){}.getType();
				final ArrayList<GsonSign> tmpSigns = gson.fromJson(reader, collectionType);
				
				is.close();
				
				for(GsonSign sign: tmpSigns){
					Log.i("Load Remote GSON", "Added sign");
		        	gsonSigns.add(sign);
		        	db.store(sign);
		        }
				db.commit();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        Log.i("Load Remote GSON", "Loaded!");
	        return null;
	    }
	    
	    @Override
	    protected void onPreExecute(){
	    	//Show loading spinner
	    	Log.i("AsyncServ", "Downloading signs");
	    	//pbarDialog = new ProgressDialog(MainActivity.this);
	    	pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    	pbarDialog.setMessage("Hämtar teckeninfo från server...");
	    	pbarDialog.setCancelable(false);
	    	pbarDialog.show();
	    	super.onPreExecute();
	    }
	    
	    @Override
	    protected void onPostExecute(Void result){
	    	//Hide loading spinner
	    	super.onPostExecute(result);
	    	pbarDialog.dismiss();
	    	/*
	    	try{
	    		listFragment.loadSigns();
	    	} catch (NullPointerException e){
	    		((SignListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).loadSigns();
	    		Log.w("NullPointer @ ServerLoad", "No ListFragment found.");
	    	}
	    	*/
	    	Log.i("AsyncServ", "Downloaded all signs");
	    }
	}
	
	private class LoadHelper extends AsyncTask<String, Void, Void>{
		
	    final ProgressDialog pbarDialog;
	    
	    public LoadHelper(MainActivity activity) {
	        this.pbarDialog = new ProgressDialog(activity);
	    }
	    
	    @Override
		protected Void doInBackground(String... url) {
			Log.i("AsyncFileLoad", "Loading");
			try {
				loadGSONfromString();
				loadData();
			} catch (IOException e) {
			} catch (JSONException e) {}
			Log.i("AsyncFileLoad", "Loaded");
	        
			Collections.sort(gsonSigns, new CustomComparator());
			if(gsonSigns.size()>100){
				for(int i=0; i<16; ++i){
					gsonSigns.add(gsonSigns.remove(0));
				}
			}
	        return null;
	    }
	    
	    @Override
	    protected void onPreExecute(){
	    	//Show loading spinner
	    	Log.i("AsyncDBLoad", "Loading Local signs");
	    	//pbarDialog = new ProgressDialog(MainActivity.this);
	    	pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    	pbarDialog.setMessage("Laddar sparad teckeninfo...");
	    	pbarDialog.setCancelable(false);
	    	pbarDialog.show();
	    	
	    	super.onPreExecute();
	    }
	    
	    @Override
	    protected void onPostExecute(Void result){
	    	//Hide loading spinner
	    	super.onPostExecute(result);
	    	try{
	    		listFragment.loadSigns();
	    	} catch (NullPointerException e){
	    		
	    		SignListFragment listFrag = (SignListFragment) getSupportFragmentManager()
	                    .findFragmentById(R.id.list_frag);
	    		
	    		if (listFrag == null) {
	    			((SignListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).loadSigns();
	    		} else {
	    			listFrag.loadSigns();
	    		}
	    	}
	    	pbarDialog.dismiss();
	    	Log.i("AsyncDBLoad", "Loaded signs from DB");
	    }
	}
	

	private class CustomComparator implements Comparator<GsonSign>  {
	    @Override
	    public int compare(GsonSign o1, GsonSign o2) {
	        return o1.words.get(0).word.compareToIgnoreCase(o2.words.get(0).word);
	    }
	}
}