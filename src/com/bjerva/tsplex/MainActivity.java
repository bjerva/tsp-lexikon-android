package com.bjerva.tsplex;

/*
 * Copyright (C) 2013, Johannes Bjerva
 *
 * Permission is hereby granted, free of charge, 
 * to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.holoeverywhere.app.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.bjerva.tsplex.fragments.PagerFragment;
import com.bjerva.tsplex.fragments.SignDetailFragment;
import com.bjerva.tsplex.fragments.SignListFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = "Main Activity";
	
	public static final int ID_SEARCH_BUTTON = 1;
	public static final int ID_COLLAPSE_BUTTON = 2;
	public static final int ID_EDIT_BUTTON = 3;
	public static final int ID_FAV_BUTTON = 4;

	private boolean doneLoading = false;

	private SignListFragment listFragment;
	private SignDetailFragment detFragment;
	private ProgressDialog pbarDialog;

	private ArrayList<SimpleGson> gsonSignsLite = null;
	private GsonSign currentSign = null;

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	private boolean isOnline;

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(new Bundle()); //XXX: Simple ugly fix.
		
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");

		setContentView(R.layout.activity_sign_listing);

		int screenSize = getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;

		switch(screenSize) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			break;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			break;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		default:
			break;
		}

		//Load local json
		new LoadHelper().execute();

		getSupportFragmentManager().beginTransaction().add(
				R.id.fragment_container, new PagerFragment()).commit();

		if(screenSize==Configuration.SCREENLAYOUT_SIZE_XLARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE){
			detFragment = new SignDetailFragment();
			getSupportFragmentManager().beginTransaction().add(
					R.id.sign_detail, detFragment).commit();
		}
		/*
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
		 */
	}

	public boolean isDoneLoading(){
		return doneLoading;
	}
	
	public void onBackPressed(){
		Crouton.cancelAllCroutons();
		super.onBackPressed();
	}

	@SuppressLint("NewApi") 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		int screenSize = getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;

		switch(screenSize) {
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			return;
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			return;
		default:
			break;
		}


		if(screenSize==Configuration.SCREENLAYOUT_SIZE_XLARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE){
			//Tablet
			showLoader();
			detFragment = new SignDetailFragment();
			getSupportFragmentManager().beginTransaction().replace(
					R.id.sign_detail, detFragment).commit();
			hideLoader();
		} /*else {
			//Handset
			Class<? extends Fragment> c = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass();
			if(c.equals(SignDetailFragment.class)){
				showLoader();
				detFragment = new SignDetailFragment();
				//Add to container
				getSupportFragmentManager().popBackStack();
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, detFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				hideLoader();
			}
		}*/

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

	public GsonSign getCurrentSign(){
		return currentSign;
	}

	public void showLoader(){
		//Remove old spinner if exists
		if(pbarDialog != null){
			hideLoader();
		}

		//Show loading spinner
		pbarDialog = new ProgressDialog(this);
		pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pbarDialog.setMessage(getString(R.string.sign_load));
		pbarDialog.setCancelable(false);
		pbarDialog.show();
	}
	
	public void onDestroy(){
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}

	public void hideLoader(){
		pbarDialog.dismiss();
	}

	public void errorPlayingVideo(){
		Crouton.makeText(this, getString(R.string.play_error), Style.ALERT).show();
	}

	public void serverError(){
		Crouton.makeText(this, getString(R.string.serv_error), Style.ALERT).show();
	}

	public void connectionError(){
		Crouton.makeText(this, getString(R.string.conn_error), Style.ALERT).show();
	}

	private void loadGSONfromStringLite() throws IOException{
		Log.i("Load Local JSON", "Loading...");
		final InputStream is = getAssets().open("words2.json");
		final Reader reader = new InputStreamReader(is);

		final Gson gson = new Gson();
		final Type collectionType = new TypeToken<ArrayList<SimpleGson>>(){}.getType();
		gsonSignsLite = gson.fromJson(reader, collectionType);

		is.close();
		Log.i("Load Local GSON", "Loaded!");
	}

	public void loadSingleJson(int id){
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

	public void checkConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		// Check that we have connectivity
		if (netInfo != null && netInfo.isConnected()) {
			isOnline = true;
			/* 
			// Make sure we also have proper internet connectivity (in case of e.g. VPN)
			connectionThread = new Thread(){
				@Override
				public void run(){
					try {
						InetAddress.getByName("google.com").isReachable(1000);
						isOnline = true;
						return;
					} catch (UnknownHostException e){
						isOnline = false;
						return;
					} catch (IOException e){
						isOnline = false;
						return;
					}
				}
			};
			connectionThread.start();
			*/
		} else {
			isOnline = false;
		}
	}
	
	public boolean isOnline(){
		/*
		try {
			connectionThread.join();
		} catch (InterruptedException e) {}*/
		return isOnline;
	}

	private class LoadHelper extends AsyncTask<String, Void, Void>{
		private long timeConsumed;
		
		@Override
		protected Void doInBackground(String... url) {
			try {
				loadGSONfromStringLite();
			} catch (IOException e) {}

			// Sort signs
			Collections.sort(gsonSignsLite, new CustomComparator());
			// Put signs containing numbers at the end
			for(int i=0; i<16; ++i){
				gsonSignsLite.add(gsonSignsLite.remove(0));
			}
			return null;
		}

		@Override
		protected void onPreExecute(){
			//Show loading spinner
			Log.d("AsyncDBLoad", "Loading local signs");
			timeConsumed = System.currentTimeMillis();
			showLoader();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result){
			super.onPostExecute(result);
			doneLoading = true;
			timeConsumed = System.currentTimeMillis()-timeConsumed;
			mGaTracker.sendTiming("LOAD", timeConsumed, "Local loading", "gson");
			hideLoader();
			Log.d("AsyncDBLoad", "Loaded local signs");
		}
	}

	public SignDetailFragment getDetFragment() {
		return detFragment;
	}

	ProgressDialog getPbarDialog() {
		return pbarDialog;
	}


	public ArrayList<SimpleGson> getGsonSignsLite() {
		return gsonSignsLite;
	}

	private class CustomComparator implements Comparator<SimpleGson>  {
		@Override
		public int compare(SimpleGson o1, SimpleGson o2) {
			return o1.getWord().compareToIgnoreCase(o2.getWord());
		}
	}
}