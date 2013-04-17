package com.bjerva.tsplex;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;
import android.util.Log;

import com.bjerva.tsplex.fragments.FlashFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class FlashActivity extends Activity{
	
	@SuppressWarnings("unused")
	private static final String TAG = "Flash Activity";
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	private Object[] flashList;
	private SharedPreferences sharedPref;

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

		setContentView(R.layout.activity_flash);

		getSupportFragmentManager().beginTransaction().add(
				R.id.fragment_container, new FlashFragment()).commit();
		
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Log.d(TAG, "items: "+sharedPref.getAll().keySet().size());
		flashList = sharedPref.getAll().keySet().toArray();
	}
	
	public Object[] getFlashList(){
		return flashList;
	}
}
