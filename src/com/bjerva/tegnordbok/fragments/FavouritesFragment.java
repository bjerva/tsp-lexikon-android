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

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

import com.bjerva.tegnordbok.MainActivity;
import com.bjerva.tegnordbok.R;
import com.bjerva.tegnordbok.adapters.FavouritesAdapter;
import com.cocosw.undobar.UndoBarController;
import com.cocosw.undobar.UndoBarController.UndoListener;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.mobeta.android.dslv.DragSortListView;

public class FavouritesFragment extends Fragment {

	private static final String TAG = "FavouritesFragment";

	private FavouritesAdapter adapter;
	private View myView;
	private ArrayList<String> list;
	private DragSortListView lv;
	private MainActivity ma;
	private SharedPreferences sharedPref;
	private boolean showCheckBoxes = false;
	private HashMap<String, Integer> toDelete;

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.favourites_fragment, container, false);
		//setHasOptionsMenu(true);
		return myView;
	}

	public void onResume(){
		super.onResume();
		// XXX: Should not be necessary...
		((PagerFragment) getParentFragment()).getPager().setFavFrag(this);
	}

	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		ma = (MainActivity) getActivity();

		mGaInstance = GoogleAnalytics.getInstance(ma);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");

		lv = (DragSortListView) ma.findViewById(R.id.drag_list);

		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		lv.setDragScrollProfile(ssProfile);

		list = new ArrayList<String>();

		sharedPref = ma.getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
		for(Object key : sharedPref.getAll().keySet()){
			list.add((String) key);
		}

		Collections.sort(list, new CaseIgnoreComparator());
		adapter = new FavouritesAdapter(ma, R.layout.list_handle, R.id.list_drag_title, list);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "CLICKED: "+adapter.getItem(position));
				ma.showLoader();
				ma.checkConnection();

				mGaTracker.sendEvent("sign", "favourite_click", adapter.getItem(position), 1L);

				//Update position
				ma.loadSingleJson(sharedPref.getInt(adapter.getItem(position), -1));

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

	public void notifyChange(){
		Log.d(TAG, "Preparing change");
		if(adapter != null){
			Log.d(TAG, "Getting shared");
			sharedPref = ma.getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
			Log.d(TAG, "Changing");
			list.clear();
			for(Object key : sharedPref.getAll().keySet()){
				list.add((String) key);
			}
			Collections.sort(list, new CaseIgnoreComparator());
			adapter = new FavouritesAdapter(ma, R.layout.list_handle, R.id.list_drag_title, list);
			lv.setAdapter(adapter);
		} else {
			Log.e(TAG, "Null adapter!");
		}
	}

	public void deleteChecked(){
		if(adapter.getChecked().size() == 0){
			return;
		}
		toDelete = new HashMap<String, Integer>(adapter.getChecked().size());

		sharedPref = ma.getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
		for(Integer i : adapter.getChecked()){
			String entry = adapter.getItem(i);
			toDelete.put(entry, sharedPref.getInt(entry, -1));
		}

		for(String entry : toDelete.keySet()){
			adapter.remove(entry);
			SharedPreferences.Editor prefEditor = sharedPref.edit();
			prefEditor.remove(entry);
			prefEditor.apply();
		}
		adapter.clearChecked();
		UndoBarController.show(getActivity(), getString(R.string.undo_descr), mUndoListener);
	}

	private UndoListener mUndoListener = new UndoListener(){
		@Override
		public void onUndo(Parcelable token) {
			for(String entry : toDelete.keySet()){
				SharedPreferences.Editor prefEditor = sharedPref.edit();
				prefEditor.putInt(entry, toDelete.get(entry));
				prefEditor.apply();
			}
			toDelete.clear();
			notifyChange();
		}
	};

	public void toggleCheckBoxes(){
		// XXX: This is bad.
		showCheckBoxes = !showCheckBoxes;
		adapter = new FavouritesAdapter(ma, R.layout.list_handle, R.id.list_drag_title, list);
		adapter.showCheckBoxes(showCheckBoxes);
		lv.setAdapter(adapter);
	}

	public boolean checkBoxesVisible(){
		return showCheckBoxes;
	}
	
	public FavouritesAdapter getAdapter(){
		return adapter;
	}

	private DragSortListView.DropListener onDrop =
			new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item=adapter.getItem(from);

			adapter.notifyDataSetChanged();
			adapter.remove(item);
			adapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = 
			new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			if(sharedPref.getAll().containsKey(adapter.getItem(which))){
				SharedPreferences.Editor prefEditor = sharedPref.edit();
				prefEditor.remove(adapter.getItem(which));
				prefEditor.commit();
			}
			adapter.remove(adapter.getItem(which));
		}
	};

	private DragSortListView.DragScrollProfile ssProfile =
			new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) adapter.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};
	
	private class CaseIgnoreComparator implements Comparator<String>  {
		@Override
		public int compare(String o1, String o2) {
			return o1.compareToIgnoreCase(o2);
		}
	}
}
