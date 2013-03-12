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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SignListFragment extends ListFragment {

	private View myView;
	private MainActivity ma;
	private SignAdapter mAdapter;

	private EditText search;
	private TextView tv;

	private int index = -1;
    private int top = 0;
    
    private String oldSearch = "";
    
    public static SignListFragment newInstance() {
    	SignListFragment frag = new SignListFragment();
        return frag;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		Log.e("INF", "INFLATIIING");
		myView = inflater.inflate(R.layout.sign_list_fragment, container, false);
		setHasOptionsMenu(true);
		return myView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();
		if(ma.getGsonSignsLite() != null){
			loadSigns();
		} else {
			Log.e("Not loading", "Gson null");
		}
		
		if(ma == null){
			Log.e("Activity null", "Activity null");
		}
	}
	
	public void onResume(){
		super.onResume();
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
		
		try{
			oldSearch = search.getText().toString();
		} catch(Exception e) {
			oldSearch = "";
			Log.w("OldSearchErr", "Error when fetching old search");
		}
	}

	void loadSigns(){
		//Create and set adapter
		tv = (TextView) getActivity().findViewById(R.id.alphabetic_header);
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
				ma.showLoader();
				
				//Update position
				ma.loadSingleJson(tmpSigns.get(position).getId());

				//Hide keyboard
				if(search != null){
					InputMethodManager imm = (InputMethodManager) ma.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
				}
				
				if(ma.getDetFragment() == null){
					//ma.getSupportActionBar().hide();
					//Create detail fragment
					SignDetailFragment newFragment = new SignDetailFragment();

					//Add to container
					FragmentTransaction transaction = ma.getSupportFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.slide_fragment_in_on_replace, R.anim.slide_fragment_out_on_replace);
					//transaction.replace(R.id.fragment_container, newFragment);
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
	
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, MenuInflater inflater) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		menu.add(0, 1, 1, R.string.search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.search_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		final Locale swedishLocale = new Locale("sv", "SE");
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
					mAdapter.getFilter().filter(cs);
					try {
						String word = ((SimpleGson) getListView().getItemAtPosition(0)).getWord();
						tv.setText(word.substring(0, 1).toUpperCase(swedishLocale));
					} catch (IndexOutOfBoundsException e){
						Log.w("IndexErr", "IndexErr after change text");
					}
				}

			});
			search.setText("");
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}
		return true;
	}       
}
