package com.bjerva.tsplex;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
		final MainActivity ma = (MainActivity) getActivity();
		
		ArrayList<String> adapterItems = new ArrayList<String>();
		for(SignModel sign: ma.signs){
			adapterItems.add(sign.words[0].word);
		}
		
		//Create and set adapter
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ma, android.R.layout.simple_list_item_1, adapterItems);
		getListView().setAdapter(mAdapter);
		
		//Set listener
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				ma.currentSign = position;
				
				//Create detail fragment
				SignDetailFragment newFragment = new SignDetailFragment();
				
				//Add to container
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
	}
}
