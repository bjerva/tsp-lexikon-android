package com.bjerva.tsplex;

import java.util.ArrayList;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortListView;

public class FavouritesFragment extends Fragment {
	
	private static final String TAG = "FavouritesFragment";
	
	private ArrayAdapter<String> adapter;

	private View myView;

	private String[] array;
	private ArrayList<String> list;
	
	private DragSortListView lv;

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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.favourites_fragment, container, false);
		setHasOptionsMenu(true);
		return myView;
	}

	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		MainActivity ma = (MainActivity) getActivity();

		lv = (DragSortListView) ma.findViewById(R.id.drag_list);

		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		lv.setDragScrollProfile(ssProfile);
		
		list = new ArrayList<String>();


		SharedPreferences sharedPref = ma.getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
		for(String key : sharedPref.getAll().keySet()){
			list.add(key);
		}

		adapter = new ArrayAdapter<String>(ma, R.layout.list_handle, R.id.list_drag_title, list);
		lv.setAdapter(adapter);
	}
}
