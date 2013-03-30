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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.SharedPreferences;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bjerva.tegnordbok.R;
import com.bjerva.tegnordbok.models.SimpleGson;

public class SignAdapter extends ArrayAdapter<SimpleGson> implements Filterable{

	public SignAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private List<SimpleGson> originalItems;
	private List<SimpleGson> filteredItems;
	private SignFilter filter;
	private SharedPreferences sharedPref;
	//private Map<String, ?> favourites;

	public SignAdapter(Context context, int resource, List<SimpleGson> items) {
		super(context, resource, items);
		this.originalItems = new ArrayList<SimpleGson>();
		this.filteredItems = new ArrayList<SimpleGson>();
		int size = items.size();
		for(int i = 0, l = size; i < l; i++){
			filteredItems.add(items.get(i));
			originalItems.add(items.get(i));
		}

		sharedPref = ((Activity) context).getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SignInfoViewHolder viewHolder;
		RelativeLayout rl = (RelativeLayout) convertView;

		if (rl == null) {
			final LayoutInflater vi = LayoutInflater.from(getContext());
			rl = (RelativeLayout) vi.inflate(R.layout.list_complex, null, false);
			viewHolder = new SignInfoViewHolder();
			viewHolder.title = (TextView) rl.findViewById(R.id.list_complex_title);
			viewHolder.caption = (TextView) rl.findViewById(R.id.list_complex_caption);
			viewHolder.star = (CheckBox) rl.findViewById(R.id.star);
			rl.setTag(viewHolder);
		} else {
			viewHolder = (SignInfoViewHolder) rl.getTag();
		}

		final SimpleGson sMod = filteredItems.get(position);
		if (sMod != null) {
			viewHolder.title.setText(sMod.getWord());
			viewHolder.caption.setText(sMod.getTag());
			viewHolder.star.setOnCheckedChangeListener(new OnCheckedChangeListener(){
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
				viewHolder.star.setChecked(true);
			} else {
				viewHolder.star.setChecked(false);
			}
		}

		return rl;
	}

	@Override
	public Filter getFilter() {
		if (filter == null){
			filter  = new SignFilter();
		}
		return filter;
	}

	private class SignFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			Locale swedishLocale = new Locale("sv", "SE");

			constraint = constraint.toString().toLowerCase(swedishLocale);
			final FilterResults result = new FilterResults();
			if(constraint != null && constraint.toString().length() > 0)
			{
				ArrayList<SimpleGson> tempFiltered = new ArrayList<SimpleGson>();
				int size = originalItems.size();
				for(int i=0, l=size; i<l; i++)
				{
					SimpleGson sign = originalItems.get(i);
					String word = sign.getWord();
					if(word.toString().toLowerCase(swedishLocale).startsWith(constraint.toString())){
						tempFiltered.add(sign);
					}
				}
				result.count = tempFiltered.size();
				result.values = tempFiltered;
			}
			else
			{
				Log.d("SignAdapter", "Refilling: "+originalItems.size());
				synchronized(this)
				{
					result.values = originalItems;
					result.count = originalItems.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, 
				FilterResults results) {

			filteredItems = (ArrayList<SimpleGson>) results.values;
			notifyDataSetChanged();
			clear();
			Log.d("SignAdapter", "Filter count: "+filteredItems.size());
			int size = filteredItems.size();
			for(int i = 0, l = size; i < l; i++)
				add(filteredItems.get(i));

			notifyDataSetInvalidated();
		}
	}

	static class SignInfoViewHolder {
		TextView title;
		TextView caption;
		CheckBox star;
	}
}
