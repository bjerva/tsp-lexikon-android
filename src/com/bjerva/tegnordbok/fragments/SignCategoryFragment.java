package com.bjerva.tegnordbok.fragments;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.LayoutInflater;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;

import com.bjerva.tegnordbok.MainActivity;
import com.bjerva.tegnordbok.R;
import com.bjerva.tegnordbok.adapters.SimpleExpandableSignListAdapter;
import com.bjerva.tegnordbok.models.SimpleGson;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class SignCategoryFragment extends ExpandableListFragment {
	static final String TAG = "SignCategoryFragment";
	private static final String NAME = "NAME";
	private static final String IS_EVEN = "IS_EVEN";

	private View myView;
	private MainActivity ma;
	private ExpandableListAdapter mAdapter;

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	private ArrayList<SimpleGson> gsonCats;

	private int index = -1;
	private int top = 0;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.sign_list_fragment, container, false);
		setHasOptionsMenu(true);
		return myView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();

		mGaInstance = GoogleAnalytics.getInstance(ma);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");

		if(ma.getGsonSignsLite() != null){
			Log.d(TAG, "Loading signs here");
			loadCategories();
		} else {
			final Handler handler = new Handler();
			final Runnable r = new Runnable(){
				public void run(){
					if(ma.isDoneLoading()){
						Log.d("Runnable", "Loading successful!");
						loadCategories();
					} else {
						Log.d("Runnable", "Loading failed...");
						handler.postDelayed(this, 500);
					}
				}
			};
			handler.postDelayed(r, 1500);
		}
	}

	public void onResume(){
		super.onResume();
		// XXX: Should not be necessary...
		((PagerFragment) getParentFragment()).getPager().setCatFrag(this);
		if(index!=-1){
			this.mList.setSelectionFromTop(index, top);
		}
	}

	public void onPause(){
		super.onPause();
		try{
			index = getExpandableListView().getFirstVisiblePosition();
			View v = getExpandableListView().getChildAt(0);
			top = (v == null) ? 0 : v.getTop();
		} catch(Exception e) {
			Log.w("OldListPosErr", "Error when fetching old listpos");
		}
	}

	public void collapseaAll(){
		int count =  mAdapter.getGroupCount();
		for (int i = 0; i <count ; i++){
			getExpandableListView().collapseGroup(i);
		}
	}

	private void loadCategories(){
		if(ma == null){
			Log.d(TAG, "Null activity");
			return;
		}
		Log.d(TAG, "Loading categories!");

		gsonCats = new ArrayList<SimpleGson>(ma.getGsonSignsLite().size());
		for(SimpleGson simpleG : ma.getGsonSignsLite()){
			gsonCats.add(simpleG);
		}
		Collections.sort(gsonCats, new CustomComparator());

		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, SimpleGson>>> childData = new ArrayList<List<Map<String, SimpleGson>>>();
		final ArrayList<Integer> groupSizes = new ArrayList<Integer>();

		groupSizes.add(0);
		int count = 0;
		int size = gsonCats.size();
		int groupCount = 0;
		String currCat = "";
		String innerCat = "";
		while(count<size) {
			currCat = gsonCats.get(count).getTag();
			innerCat = gsonCats.get(count).getTag();
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			if(currCat.equals("")){
				curGroupMap.put(NAME, getString(R.string.no_category));
			} else {
				curGroupMap.put(NAME, currCat);
			}

			List<Map<String, SimpleGson>> children = new ArrayList<Map<String, SimpleGson>>();
			do {
				Map<String, SimpleGson> curChildMap = new HashMap<String, SimpleGson>();
				children.add(curChildMap);
				curChildMap.put(IS_EVEN, gsonCats.get(count));
				count++;
				groupCount++;
				if(count < size){
					innerCat = gsonCats.get(count).getTag();
				}
			} while(currCat.equals(innerCat) && count<size);

			childData.add(children);
			groupSizes.add(groupCount);
		}

		// Set up our adapter
		mAdapter = new SimpleExpandableSignListAdapter(
				getActivity(),
				groupData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { NAME },
				new int[] { android.R.id.text1, android.R.id.text2 },
				childData,
				R.layout.list_child,
				new String[] { IS_EVEN },
				new int[] { R.id.list_child_title }
				);

		setListAdapter(mAdapter);

		//Set listener
		getExpandableListView().setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(android.widget.ExpandableListView parent, View v, 
					int groupPosition, int childPosition, long id) {
				ma.showLoader();
				ma.checkConnection();

				int signPosition = groupSizes.get(groupPosition)+childPosition;
				mGaTracker.sendEvent(MainActivity.LANG_STR, "category_click", gsonCats.get(signPosition).getWord(), 1L);

				//Update position
				ma.loadSingleJson(gsonCats.get(signPosition).getId());

				if(!ma.isOnline()){
					ma.connectionError();
					ma.hideLoader();
					return true;
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
				return true;
			}
		});

		getExpandableListView().setFastScrollEnabled(true);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		SimpleGson clickedSign = (SimpleGson) getExpandableListView().getItemAtPosition(position);
		Log.d(TAG, clickedSign.getWord());
	}

	private class CustomComparator implements Comparator<SimpleGson>  {
		@Override
		public int compare(SimpleGson o1, SimpleGson o2) {
			if(o1.getTag().equals(o2.getTag())){
				return 0;
			} else if (o1.getTag().equals("")){
				return 1;
			} else if (o2.getTag().equals("")){
				return -1;
			}
			return o1.getTag().compareToIgnoreCase(o2.getTag());
		}
	}
}
