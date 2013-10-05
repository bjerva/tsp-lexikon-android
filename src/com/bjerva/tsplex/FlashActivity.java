package com.bjerva.tsplex;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;

import com.bjerva.tsplex.fragments.FlashFragment;
import com.bjerva.tsplex.models.GsonSign;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.gson.Gson;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FlashActivity extends Activity{

	@SuppressWarnings("unused")
	private static final String TAG = "Flash Activity";

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	private Object[] flashList;
	private int[] idList;
	private SharedPreferences sharedPref;

	private GsonSign currentSign;

	private int screenWidth;

	private ProgressDialog pbarDialog;

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

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(new Bundle()); //XXX: Simple ugly fix.

		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");

		setContentView(R.layout.activity_flash);

		Display display = getWindowManager().getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT >= 13){
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
		} else {
			screenWidth = display.getWidth();
		}

		getSupportFragmentManager().beginTransaction().add(
				R.id.fragment_container, new FlashFragment()).commit();

		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Log.d(TAG, "items: "+sharedPref.getAll().keySet().size());
		flashList = sharedPref.getAll().keySet().toArray();
		idList = new int[flashList.length];
		for(int i = 0; i<flashList.length; i++){
			idList[i] = (Integer) sharedPref.getAll().get(flashList[i]);
		}
	}

	public Object[] getFlashList(){
		return flashList;
	}

	public int[] getIdList(){
		return idList;
	}

	public void loadSingleJson(int id){

		Log.i("Load Single GSON", "Loading...");
		InputStream is;
		try {
			is = getAssets().open("swedish/split_json/"+String.valueOf(id)+".json");
			final Reader reader = new InputStreamReader(is);
			final Gson gson = new Gson();
			currentSign = gson.fromJson(reader, GsonSign.class);
		} catch (IOException e) {
			Log.e("Single GSON", "Error loading single gson");
		}
		Log.i("Load Single GSON", "Loaded!");
	}

	public GsonSign getCurrentSign(){
		return currentSign;
	}

	public int getScreenWidth(){
		return screenWidth;
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
		pbarDialog.setCancelable(true);
		pbarDialog.show();
	}

	public void hideLoader(){
		pbarDialog.dismiss();
	}
	
	public void errorPlayingVideo(){
		Crouton.makeText(this, getString(R.string.play_error), Style.ALERT, (ViewGroup) findViewById(R.id.fragment_container)).show();
	}
	
	public void serverError(){
		Crouton.makeText(this, getString(R.string.serv_error), Style.ALERT, (ViewGroup) findViewById(R.id.fragment_container)).show();
	}

	public void connectionError(){
		Crouton.makeText(this, getString(R.string.conn_error), Style.ALERT, (ViewGroup) findViewById(R.id.fragment_container)).show();
	}
}
