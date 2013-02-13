package com.bjerva.tsplex;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends SherlockFragmentActivity {
	private SignListFragment listFragment;
	private SignDetailFragment detFragment;

	private ProgressDialog pbarDialog;

	private EditText search;

	//ArrayList<GsonSign> gsonSigns = null;
	ArrayList<SimpleGson> gsonSignsLite = null;
	GsonSign currentSign = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(new Bundle()); //XXX: Simple ugly fix.

		setContentView(R.layout.activity_sign_listing);

		//Load local json
		new LoadHelper(this).execute();

		SignListFragment listFrag = (SignListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.list_frag);
		if (listFrag == null) {
			listFragment = new SignListFragment();
			getSupportFragmentManager().beginTransaction().add(
					R.id.fragment_container, listFragment).commit();
		} else {
			detFragment = new SignDetailFragment();
			getSupportFragmentManager().beginTransaction().add(
					R.id.details_container, detFragment).commit();
		}
	}

/*
 * public long insertRecord(SQLiteDatabase db, Serializable myObject) { 
		try { 
			db.beginTransaction(); 
			ContentValues values = new ContentValues(); 
			values.put("MyColumn", SignSQLiteHelper.getSerializedObject(myObject)); 
			long id = db.insert("MyTable", null, values); 
			if (id>=0) db.setTransactionSuccessful(); 
			return id; 
		} catch (Exception e) { 
			Logger.e(loggerTag, e.getMessage(), e); 
			// ignore this and roll back the transaction 
		} finally { 
			try { 
				db.endTransaction(); 
			} catch (Exception e) { 
				return -1; 
			} 
		} 
		return -1; 
	} 
*/
	
	public void onBackPressed(){
		for(int i=getSupportFragmentManager().getBackStackEntryCount(); i>1; i--){
			getSupportFragmentManager().popBackStack();
		}
		detFragment = null;
		super.onBackPressed();
	}

	@SuppressLint("NewApi") 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		SignListFragment listFrag = (SignListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.list_frag);

		if (listFrag != null) {
			//Tablet
			showLoader();
			detFragment = new SignDetailFragment();
			getSupportFragmentManager().beginTransaction().replace(
					R.id.details_container, detFragment).commit();
			hideLoader();
		} else {
			//Handset
			Class<? extends Fragment> c = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass();
			if(c.equals(SignDetailFragment.class)){
				showLoader();
				detFragment = new SignDetailFragment();
				//Add to container
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, detFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				hideLoader();
			}
		}

		ListView metaList = (ListView) findViewById(R.id.metaList);
		if(metaList != null){
			RelativeLayout.LayoutParams params;
			if(newConfig.orientation == 1){
				params = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.BELOW, R.id.myVideoView);
			} else if (newConfig.orientation == 2) {
				params = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				params.addRule(RelativeLayout.RIGHT_OF, R.id.myVideoView);
			} else {
				Log.e("MA", "Should not be here...");
				params = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			}
			metaList.setLayoutParams(params);
		} else {
			Log.i("MA", "metaList is null");
		}
	}

	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		menu.add(0, 1, 1, R.string.search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.search_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		Log.i("OPTIONS", ""+item.getItemId());
		InputMethodManager imm;
		switch (item.getItemId()) {
		case 1:
			search = (EditText) item.getActionView();
			search.requestFocus();
			search.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s) {
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				public void onTextChanged(CharSequence cs, int start, int before, int count) {
					SignListFragment listFrag = (SignListFragment) getSupportFragmentManager()
							.findFragmentById(R.id.list_frag);
					if(listFrag == null){
						listFrag = listFragment;
					}
					listFrag.mAdapter.getFilter().filter(cs);
				}

			});
			search.setText("");
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}
		return true;
	}       

	GsonSign getCurrentSign(){
		return currentSign;
	}

	void showLoader(){
		//Show spinner
		pbarDialog = new ProgressDialog(this);
		pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pbarDialog.setMessage(getString(R.string.sign_load));
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
		Toast.makeText(this, getString(R.string.play_error), Toast.LENGTH_LONG).show();
	}

	void networkError() {
		SignListFragment listFrag = (SignListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.list_frag);
		if(listFrag == null){
			super.onBackPressed();   
		} 
		Toast.makeText(this, getString(R.string.conn_error), Toast.LENGTH_LONG).show();
	}

	/*
	private void loadGSONfromString() throws IOException, JSONException{
		Log.i("Load Local JSON", "Loading...");
		final InputStream is = getAssets().open("signs2.json");
		final Reader reader = new InputStreamReader(is);

		final Gson gson = new Gson();
		final Type collectionType = new TypeToken<ArrayList<GsonSign>>(){}.getType();
		//gsonSigns = gson.fromJson(reader, collectionType);

		is.close();
		Log.i("Load Local GSON", "Loaded!");
	}
	*/
	
	private void loadGSONfromStringLite() throws IOException, JSONException{
		Log.i("Load Local JSON", "Loading...");
		final InputStream is = getAssets().open("words2.json");
		final Reader reader = new InputStreamReader(is);

		final Gson gson = new Gson();
		final Type collectionType = new TypeToken<ArrayList<SimpleGson>>(){}.getType();
		gsonSignsLite = gson.fromJson(reader, collectionType);

		is.close();
		Log.i("Load Local GSON", "Loaded!");
	}
	
	void loadSingleJson(int id){
		Log.i("Load Single GSON", "Loading...");
		InputStream is;
		try {
			is = getAssets().open("split_json/"+String.valueOf(id)+".json");
			final Reader reader = new InputStreamReader(is);
			final Gson gson = new Gson();
			currentSign = gson.fromJson(reader, GsonSign.class);
		} catch (IOException e) {
			Log.e("Single GSON", "Error loading single gson");
		}
		Log.i("Load Single GSON", "Loaded!");
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
				//loadGSONfromString();
				loadGSONfromStringLite();
				//loadData();
			} catch (IOException e) {
			} catch (JSONException e) {}
			Log.i("AsyncFileLoad", "Loaded");

			Collections.sort(gsonSignsLite, new CustomComparator());
			if(gsonSignsLite.size()>100){
				for(int i=0; i<16; ++i){
					gsonSignsLite.add(gsonSignsLite.remove(0));
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
			pbarDialog.setMessage(getString(R.string.list_load));
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



	public SignDetailFragment getDetFragment() {
		return detFragment;
	}

	public ProgressDialog getPbarDialog() {
		return pbarDialog;
	}

	public EditText getSearch() {
		return search;
	}

	public ArrayList<SimpleGson> getGsonSigns() {
		return gsonSignsLite;
	}

	private class CustomComparator implements Comparator<SimpleGson>  {
		@Override
		public int compare(SimpleGson o1, SimpleGson o2) {
			return o1.getWord().compareToIgnoreCase(o2.getWord());
		}
	}	
	/*
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
	//	new JSONParser(this).execute(jsonURL);

		saveRetrievalDate();
	}
	 */
	/*
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
	 */

	/*
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
	    	pbarDialog.setMessage("H�mtar teckeninfo fr�n server...");
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
	    	//
	    	Log.i("AsyncServ", "Downloaded all signs");
	    }
	}

	 */
}