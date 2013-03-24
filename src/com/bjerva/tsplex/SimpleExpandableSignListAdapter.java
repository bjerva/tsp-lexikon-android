package com.bjerva.tsplex;

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
	private List<List<Map<String, ?>>> mChildData;
	private String[] mChildFrom;
	private SharedPreferences sharedPref;
	private int prefSize;
	private int mChildLayout;
	private int mLastChildLayout;
	private LayoutInflater mInflater;

	@SuppressWarnings("unchecked")
	public SimpleExpandableSignListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
					String[] groupFrom, int[] groupTo,
					List<? extends List<? extends Map<String, ?>>> childData,
							int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData,
				childLayout, childFrom, childTo);
		mChildData = (List<List<Map<String, ?>>>) childData;
		mChildLayout = childLayout;
		mChildFrom = childFrom;
		mContext = context;
		mLastChildLayout = mChildLayout;
		sharedPref = ((Activity) mContext).getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
		prefSize = sharedPref.getAll().size();
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

	private void bindView(View view, Map<String, ?> data, String[] from) {
		final TextView v = (TextView)view.findViewById(R.id.list_child_title);
		final CheckBox star = (CheckBox) view.findViewById(R.id.child_star);
		final String title = (String)data.get(from[0]);
		
		if (v != null) {
			v.setText(title);
		}
		if (star != null){
			star.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Log.d("StarClick", title+" Checked: "+isChecked);
					SharedPreferences.Editor prefEditor = sharedPref.edit();
					if(sharedPref.getAll().containsKey(title)){
						if(!isChecked){
							prefEditor.remove(title);
							prefSize--;
						}
					} else {
						if(isChecked){
							prefEditor.putInt(title, prefSize);
							prefSize++;
						}
					}
					prefEditor.commit();
				}
			});

			if(sharedPref.getAll().containsKey(title)){
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
