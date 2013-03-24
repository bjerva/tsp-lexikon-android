package com.bjerva.tsplex.fragments;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.bjerva.tsplex.MainActivity;
import com.bjerva.tsplex.R;
import com.bjerva.tsplex.SignAdapter;
import com.bjerva.tsplex.SimpleGson;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class SignListFragment extends ListFragment {
	
	static final String TAG = "SignListFragment";

	private View myView;
	private MainActivity ma;
	private SignAdapter mAdapter;

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	private EditText search;
	private TextView tv;

	private int index = -1;
	private int top = 0;

	private String oldSearch = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.sign_list_fragment, container, false);
		//setHasOptionsMenu(true);
		return myView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();

		mGaInstance = GoogleAnalytics.getInstance(ma);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");
		
		if(ma.isDoneLoading()){
			Log.d(TAG, "Loading signs here");
			loadSigns();
		} else {
			final Handler handler = new Handler();
			final Runnable r = new Runnable(){
			    public void run(){
			    	if(ma.isDoneLoading()){
			    		loadSigns();
			    	} else {
			    		handler.postDelayed(this, 150);
			    	}
			    }
			};
			handler.postDelayed(r, 1000);
		}
	}

	public TextView getTextHeader(){
		return tv;
	}
	
	public void onResume(){
		super.onResume();
		// XXX: Should not be necessary...
		((PagerFragment) getParentFragment()).getPager().setListFrag(this);
		if(index!=-1){
			this.getListView().setSelectionFromTop(index, top);
		}
		if(mAdapter != null){
			mAdapter.getFilter().filter(oldSearch);
		}
	}

	public void onPause(){
		super.onPause();
		try{
			index = this.getListView().getFirstVisiblePosition();
			View v = this.getListView().getChildAt(0);
			top = (v == null) ? 0 : v.getTop();
		} catch(Exception e) {
			Log.w("OldListPosErr", "Error when fetching old listpos");
		}
	}
	
	public void setOldSearch(String search){
		oldSearch = search;
	}

	void loadSigns(){
		//Create and set adapter
		if(ma == null){
			Log.d(TAG, "Null activity");
			return;
		}
		tv = (TextView) ma.findViewById(R.id.alphabetic_header);
		tv.setText("A");
		final List<SimpleGson> tmpSigns = new ArrayList<SimpleGson>();
		final Locale swedishLocale = new Locale("sv", "SE");

		for(int i = 0, l = ma.getGsonSignsLite().size(); i < l; i++){
			SimpleGson currSign = ma.getGsonSignsLite().get(i);
			tmpSigns.add(currSign);
		}

		mAdapter = new SignAdapter(ma, android.R.layout.simple_list_item_1, tmpSigns);

		getListView().setAdapter(mAdapter);

		//Set scroll listener
		getListView().setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem>0){
					String word = ((SimpleGson) view.getItemAtPosition(firstVisibleItem)).getWord();
					tv.setText(word.substring(0, 1).toUpperCase(swedishLocale));
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
		});

		//Set listener
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id){
				Log.d(TAG, "CLICKED");
				ma.showLoader();
				ma.checkConnection();

				mGaTracker.sendEvent("sign", "list_click", tmpSigns.get(position).getWord(), 1L);

				//Update position
				ma.loadSingleJson(tmpSigns.get(position).getId());

				//Hide keyboard
				if(search != null){
					InputMethodManager imm = (InputMethodManager) ma.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
				}

				if(!ma.isOnline()){
					ma.connectionError();
					ma.hideLoader();
					return;
				}

				if(ma.getDetFragment() == null){
					//Create detail fragment
					SignDetailFragment newFragment = new SignDetailFragment();

					//Add to container
					FragmentTransaction transaction = ma.getSupportFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.slide_fragment_in_on_replace, R.anim.slide_fragment_out_on_replace);
					transaction.replace(R.id.fragment_container, newFragment);
					transaction.addToBackStack(null);
					transaction.commit();
				} else {
					ma.getDetFragment().startUpHelper(ma.getCurrentSign());
				}
			}

		});
	}

	public SignAdapter getmAdapter() {
		return mAdapter;
	}

	/*
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, MenuInflater inflater) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		menu.add(0, 1, 1, R.string.search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.search_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		super.onCreateOptionsMenu(menu, inflater);
	}*/       
}
