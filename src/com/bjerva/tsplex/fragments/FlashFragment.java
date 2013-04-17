package com.bjerva.tsplex.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bjerva.tsplex.FlashActivity;
import com.bjerva.tsplex.R;
import com.capricorn.ArcMenu;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class FlashFragment extends Fragment{

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	private FlashActivity fa;
	private TextView tv;

	private int count = 0;

	private View myView;
	
	private static final int[] ITEM_DRAWABLES = new int[] {R.drawable.rating_0, R.drawable.rating_1, 
		R.drawable.rating_2, R.drawable.rating_3, 
		R.drawable.rating_4, R.drawable.rating_5};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.fragment_flash, container, false);
		tv = (TextView) myView.findViewById(R.id.flash_text);
		return myView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		fa = (FlashActivity) getActivity();

		mGaInstance = GoogleAnalytics.getInstance(fa);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");

		tv.setText((String) fa.getFlashList()[0]);

		setupArcMenu();
	}

	private void setupArcMenu(){
		final ArcMenu menu = (ArcMenu) myView.findViewById(R.id.arc_menu);
		final Handler mHandler = new Handler();
		final Runnable r = new Runnable(){
			public void run(){
				if(!menu.getArcLayout().isExpanded()){
					menu.getArcLayout().switchState(true);
				}
			}
		};
		
		final int itemCount = ITEM_DRAWABLES.length;
		for (int i = 0; i < itemCount; i++) {
			ImageView item = new ImageView(fa);
			item.setImageResource(ITEM_DRAWABLES[i]);

			menu.addItem(item, new OnClickListener() {
				@Override
				public void onClick(View v) {
					count++;
					if(count < fa.getFlashList().length){
						updateFlashText();
						mHandler.postDelayed(r, 1000);
					}
				}
			});
		}
		
		mHandler.postDelayed(r, 1000);
	}
	
	private void updateFlashText(){
		tv.setText((String) fa.getFlashList()[count]);
	}

}


