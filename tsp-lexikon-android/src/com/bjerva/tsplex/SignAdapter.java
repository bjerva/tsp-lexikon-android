package com.bjerva.tsplex;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bjerva.tsplex.GsonSign.Word;

public class SignAdapter extends ArrayAdapter<GsonSign> implements Filterable{

	public SignAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
	}

	private List<GsonSign> originalItems;
	private List<GsonSign> filteredItems;
	private SignFilter filter;

	public SignAdapter(Context context, int resource, List<GsonSign> items) {
		super(context, resource, items);
		this.originalItems = new ArrayList<GsonSign>();
		this.filteredItems = new ArrayList<GsonSign>();
		
		for(int i = 0, l = items.size(); i < l; i++){
			filteredItems.add(items.get(i));
			originalItems.add(items.get(i));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView v = (TextView) convertView;
		if (v == null) {
			LayoutInflater vi = LayoutInflater.from(getContext());
			v = (TextView) vi.inflate(android.R.layout.simple_list_item_1, null);
		}

		final GsonSign sMod = filteredItems.get(position);
		if (sMod != null) {
			if (v != null) {
				List<Word> words = sMod.words;
				String word = words.get(0).word;
				for(int j=1; j<words.size(); ++j){
					word += ", "+words.get(j).word;
				}
				v.setText(word);
			}
		}

		return v;
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
				ArrayList<GsonSign> tempFiltered = new ArrayList<GsonSign>();

				for(int i = 0, l = originalItems.size(); i < l; i++)
				{
					GsonSign sign = originalItems.get(i);
					List<Word> words;
					String word = null;
					words = sign.words;
					word = words.get(0).word;
					if(word.toString().toLowerCase(swedishLocale).contains(constraint))
						tempFiltered.add(sign);
				}
				//synchronized(this)
				//{
				result.count = tempFiltered.size();
				result.values = tempFiltered;
				//}
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
			
			filteredItems = (ArrayList<GsonSign>) results.values;
			notifyDataSetChanged();
			clear();
			Log.i("SignAdapter", "Filter count: "+filteredItems.size());
			
			for(int i = 0, l = filteredItems.size(); i < l; i++)
				add(filteredItems.get(i));
			
			notifyDataSetInvalidated();
		}
	}

}
