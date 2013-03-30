package com.bjerva.tegnordbok.adapters;

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

import java.util.HashSet;
import java.util.List;

import org.holoeverywhere.ArrayAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bjerva.tegnordbok.R;

public class FavouritesAdapter extends ArrayAdapter<String> {

	private static final String TAG = "FavAdapter";
	private boolean showCheckBoxes = false;
	private HashSet<Integer> checked;
	private List<String> mItems;

	public FavouritesAdapter(Context context, int resource, int arg0, List<String> items) {
		super(context, resource, arg0, items);
		mItems = items;
		checked = new HashSet<Integer>();
	}

	public void showCheckBoxes(boolean status){
		showCheckBoxes = status;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		FavouritesViewHolder viewHolder;
		RelativeLayout rl = (RelativeLayout) convertView;

		if (rl == null) {
			final LayoutInflater vi = LayoutInflater.from(getContext());
			rl = (RelativeLayout) vi.inflate(R.layout.list_handle, null, false);
			viewHolder = new FavouritesViewHolder();
			viewHolder.title = (TextView) rl.findViewById(R.id.list_drag_title);
			viewHolder.deleteBox = (CheckBox) rl.findViewById(R.id.favourite_check);
			if(showCheckBoxes){
				viewHolder.deleteBox.setVisibility(View.VISIBLE);
			} else {
				viewHolder.deleteBox.setVisibility(View.GONE);
			}
			rl.setTag(viewHolder);
		} else {
			viewHolder = (FavouritesViewHolder) rl.getTag();
		}

		final String key = mItems.get(position);
		viewHolder.title.setText(key);

		viewHolder.deleteBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(checked.contains(position) && !isChecked){
					Log.d(TAG, "Removing "+key);
					checked.remove(position);
				} else if(!checked.contains(position) && isChecked) {
					Log.d(TAG, "Added "+key);
					checked.add(position);
				}
			}
		});

		if(checked.contains(position)){
			viewHolder.deleteBox.setChecked(true);
		} else {
			viewHolder.deleteBox.setChecked(false);
		}

		return rl;
	}

	public HashSet<Integer> getChecked(){
		return checked;
	}

	public void clearChecked(){
		checked = new HashSet<Integer>();
	}

	public void updateChecked(int oldVal, int newVal){
		//TODO: Implement
	}

	static class FavouritesViewHolder {
		TextView title;
		CheckBox deleteBox;
	}
}
