package com.bjerva.tsplex;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

public class SignDetailFragment extends Fragment {
	
	private View myView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.sign_detail_fragment, container, false);
		return myView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		MainActivity ma = (MainActivity) getActivity();
		
		VideoView myVideoView = (VideoView) myView.findViewById(R.id.myVideoView);
		Log.i("SignDetail", ma.getCurrentSign().video_url);
		myVideoView.setVideoURI(Uri.parse(ma.getCurrentSign().video_url));
		myVideoView.setMediaController(new MediaController(ma));
		myVideoView.requestFocus();
		myVideoView.start();
		
		ArrayList<String> adapterItems = new ArrayList<String>();
		adapterItems.add("Description:\t"+ma.getCurrentSign().description);
		adapterItems.add("Updated:\t"+ma.getCurrentSign().updated);
		adapterItems.add("Unusual:\t"+ma.getCurrentSign().unusual);
		
		//Create and set adapter
		ListView listView = (ListView) myView.findViewById(R.id.metaList);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ma, android.R.layout.simple_list_item_1, adapterItems);
		listView.setAdapter(mAdapter);
		
		//Set listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				
			}
		});
		
	}
}
