package com.bjerva.tsplex;

import java.util.ArrayList;

import org.holoeverywhere.app.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bjerva.tsplex.fragments.ResultsFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class ResultsActivity extends Activity{
	@SuppressWarnings("unused")
	private static final String TAG = "Results Activity";

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

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
		Log.e(TAG, "Here we are");

		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");

		setContentView(R.layout.activity_results);
		
		Intent intent = getIntent();
		ArrayList<String> failedSigns = intent.getStringArrayListExtra("test");
		
		ResultsFragment mResultsFragment = new ResultsFragment();
		mResultsFragment.setFailedSigns(failedSigns);
		
		getSupportFragmentManager().beginTransaction().add(
				R.id.fragment_container, mResultsFragment).commit();
	}
}
