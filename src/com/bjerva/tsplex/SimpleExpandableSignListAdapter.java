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

import java.util.List;
import java.util.Map;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.SharedPreferences;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class SimpleExpandableSignListAdapter extends
SimpleExpandableListAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = "ExpandableAdapter";

	private final Context mContext;
	private List<List<Map<String, SimpleGson>>> mChildData;
	private String[] mChildFrom;
	private SharedPreferences sharedPref;
	private int mChildLayout;
	private int mLastChildLayout;
	private LayoutInflater mInflater;

	public SimpleExpandableSignListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
					String[] groupFrom, int[] groupTo,
					List<List<Map<String, SimpleGson>>> childData,
					int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData,
				childLayout, childFrom, childTo);
		mChildData = childData;
		mChildLayout = childLayout;
		mChildFrom = childFrom;
		mContext = context;
		mLastChildLayout = mChildLayout;
		sharedPref = ((Activity) mContext).getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getChildView (int groupPosition, int childPosition, 
			boolean isLastChild, View convertView, ViewGroup parent){
		View v;
		if (convertView == null) {
			v = newChildView(isLastChild, parent);
		} else {
			v = convertView;
		}
		bindView(v, mChildData.get(groupPosition).get(childPosition), mChildFrom);
		return v;
	}

	public View newChildView(boolean isLastChild, ViewGroup parent) {
		return mInflater.inflate((isLastChild) ? mLastChildLayout : mChildLayout, parent, false);
	}

	private void bindView(View view, Map<String, SimpleGson> data, String[] from) {
		final TextView v = (TextView)view.findViewById(R.id.list_child_title);
		final CheckBox star = (CheckBox) view.findViewById(R.id.child_star);
		final SimpleGson sMod = data.get(from[0]);

		if (v != null) {
			v.setText(sMod.getWord());
		}
		if (star != null){
			star.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Log.d("StarClick", sMod.getWord()+" Checked: "+isChecked);
					SharedPreferences.Editor prefEditor = sharedPref.edit();
					if(sharedPref.getAll().containsKey(sMod.getWord())){
						if(!isChecked){
							prefEditor.remove(sMod.getWord());
						}
					} else {
						if(isChecked){
							prefEditor.putInt(sMod.getWord(), sMod.getId());
						}
					}
					prefEditor.apply();
				}
			});

			if(sharedPref.getInt(sMod.getWord(), -1) == sMod.getId()){
				star.setChecked(true);
			} else {
				star.setChecked(false);
			}
		}
	}

	static class SignCatViewHolder {
		TextView title;
		CheckBox star;
	}
}
