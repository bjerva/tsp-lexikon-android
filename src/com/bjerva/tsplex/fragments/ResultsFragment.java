package com.bjerva.tsplex.fragments;

import java.util.ArrayList;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bjerva.tsplex.R;

public class ResultsFragment extends ListFragment {

	private View myView;
	private ArrayList<String> mFailedSigns;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.fragment_results, container, false);
		return myView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		showFailed();
	}

	public void setFailedSigns(ArrayList<String> failedSigns){
		mFailedSigns = failedSigns;
	}
	
	private void showFailed(){
		Log.e("here yo", "here here");
		String[] failed = new String[mFailedSigns.size()];
		for(int i=0; i<mFailedSigns.size(); i++){
			failed[i] = mFailedSigns.get(i);
		}
		Log.e("test", failed[0]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, failed);
		setListAdapter(adapter);
	}
}
