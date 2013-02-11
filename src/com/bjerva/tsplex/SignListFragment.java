package com.bjerva.tsplex;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SignListFragment extends ListFragment {

	private View myView;
	private MainActivity ma;
	SignAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.sign_list_fragment, container, false);
		return myView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();
		if(ma.gsonSigns != null){
			loadSigns();
		}
	}
	
	public void onResume(){
		super.onResume();
		ma.getSupportActionBar().show();
	}
	
	void loadSigns(){
		//Create and set adapter
		final List<GsonSign> tmpSigns = new ArrayList<GsonSign>();
		for(int i = 0, l = ma.gsonSigns.size(); i < l; i++){
			GsonSign currSign = ma.gsonSigns.get(i);
			tmpSigns.add(currSign);
		}
		
		mAdapter = new SignAdapter(ma, android.R.layout.simple_list_item_1, tmpSigns);
		
		getListView().setAdapter(mAdapter);

		//Set listener
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				ma.showLoader();
				
				//Update position
				ma.currentSign = tmpSigns.get(position);//Integer.valueOf(String.valueOf(id));

				//Hide keyboard
				if(ma.getSearch() != null){
					InputMethodManager imm = (InputMethodManager) ma.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(ma.getSearch().getWindowToken(), 0);
				}
				
				if(ma.getDetFragment() == null){
					ma.getSupportActionBar().hide();
					//Create detail fragment
					SignDetailFragment newFragment = new SignDetailFragment();

					//Add to container
					FragmentTransaction transaction = ma.getSupportFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.slide_fragment_in_on_replace, R.anim.slide_fragment_out_on_replace);
					transaction.replace(R.id.fragment_container, newFragment);
					transaction.addToBackStack(null);
					transaction.commit();
				} else {
					ma.getDetFragment().startUpHelper(ma.currentSign);
				}
			}
		});
	}
}
