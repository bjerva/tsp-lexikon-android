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

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

public class SignAdapter extends ArrayAdapter<SimpleGson> implements Filterable{

	public SignAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private List<SimpleGson> originalItems;
	private List<SimpleGson> filteredItems;
	private SignFilter filter;

	public SignAdapter(Context context, int resource, List<SimpleGson> items) {
		super(context, resource, items);
		this.originalItems = new ArrayList<SimpleGson>();
		this.filteredItems = new ArrayList<SimpleGson>();

		for(int i = 0, l = items.size(); i < l; i++){
			filteredItems.add(items.get(i));
			originalItems.add(items.get(i));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SignInfoViewHolder viewHolder;
		LinearLayout ll = (LinearLayout) convertView;

		if (ll == null) {
			final LayoutInflater vi = LayoutInflater.from(getContext());
			ll = (LinearLayout) vi.inflate(R.layout.list_complex, null, false);
			viewHolder = new SignInfoViewHolder();
			viewHolder.title = (TextView) ll.findViewById(R.id.list_complex_title);
			viewHolder.caption = (TextView) ll.findViewById(R.id.list_complex_caption);
			ll.setTag(viewHolder);
		} else {
			viewHolder = (SignInfoViewHolder) ll.getTag();
		}

		final SimpleGson sMod = filteredItems.get(position);
		if (sMod != null) {
			viewHolder.title.setText(sMod.getWord());
			viewHolder.caption.setText(sMod.getTag());
		}

		return ll;
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

				for(int i=0, l=originalItems.size(); i<l; i++)
				{
					SimpleGson sign = originalItems.get(i);
					String word = sign.getWord();
					if(word.toString().toLowerCase(swedishLocale).contains(constraint))
						tempFiltered.add(sign);
				}
				result.count = tempFiltered.size();
				result.values = tempFiltered;
			}
			else
			{
				Log.i("SignAdapter", "Refilling: "+originalItems.size());
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
			Log.i("SignAdapter", "Filter count: "+filteredItems.size());

			for(int i = 0, l = filteredItems.size(); i < l; i++)
				add(filteredItems.get(i));

			notifyDataSetInvalidated();
		}
	}

	static class SignInfoViewHolder {
		TextView title;
		TextView caption;
	}
}
