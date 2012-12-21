package com.bjerva.tsplex;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SignListFragment extends ListFragment {

	private View myView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.sign_list_fragment, container, false);
		return myView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		MainActivity ma = (MainActivity) getActivity();
		
		ArrayList<String> adapterItems = new ArrayList<String>();
		for(SignModel sign: ma.signs){
			adapterItems.add(sign.words[0].word);
		}
		
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ma, android.R.layout.simple_list_item_1, adapterItems);
		getListView().setAdapter(mAdapter);
	}
}
