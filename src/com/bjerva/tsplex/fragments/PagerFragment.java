package com.bjerva.tsplex.fragments;

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

import java.util.Locale;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bjerva.tsplex.FlashActivity;
import com.bjerva.tsplex.MainActivity;
import com.bjerva.tsplex.R;
import com.bjerva.tsplex.models.SimpleGson;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.viewpagerindicator.TabPageIndicator;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PagerFragment extends Fragment {

	@SuppressWarnings("unused")
	private static final String TAG = "PagerFragment";

	private SignAlternativesAdapter mAdapter;
	//private ViewPager mPager;
	private ViewPager mJazzy;
	private TabPageIndicator mIndicator;
	private View mView;
	private Menu mMenu = null;
	private MainActivity ma;
	private EditText search;
	private int previousFrag;

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		mView = inflater.inflate(R.layout.fragment_pager, container, false);
		setHasOptionsMenu(true);
		return mView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();

		mGaInstance = GoogleAnalytics.getInstance(ma);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");

		mAdapter = new SignAlternativesAdapter(this);

		mJazzy = (ViewPager) ma.findViewById(R.id.jazzy_pager);
		//mJazzy.setTransitionEffect(TransitionEffect.CubeOut);
		mJazzy.setAdapter(mAdapter);
		mJazzy.setPageMargin(40);
		mJazzy.setOffscreenPageLimit(3);

		mIndicator = (TabPageIndicator) ma.findViewById(R.id.indicator);
		mIndicator.setViewPager(mJazzy);
		mIndicator.setOnPageChangeListener(mOnPageChangeListener);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);

		mMenu = menu;
		if(previousFrag == 0){
			mMenu.add(0, MainActivity.ID_SEARCH_BUTTON, 1, R.string.search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.search_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		} else if (previousFrag == 1){
			mMenu.add(0, MainActivity.ID_COLLAPSE_BUTTON, 1, R.string.edit_favs).setIcon(R.drawable.ic_media_group_collapse).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		} else if (previousFrag == 2){
			mMenu.add(0, MainActivity.ID_EDIT_BUTTON, 0, R.string.edit_favs).setIcon(R.drawable.ic_menu_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			mMenu.add(0, MainActivity.ID_FLASH_BUTTON, 1, R.string.flash_cards).setIcon(R.drawable.light_bulb).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		final Locale swedishLocale = new Locale("sv", "SE");
		InputMethodManager imm;
		switch (item.getItemId()) {
		case MainActivity.ID_SEARCH_BUTTON:
			search = (EditText) item.getActionView();

			final SignListFragment signListFragment = (SignListFragment) mAdapter.getItem(0);
			search.requestFocus();
			search.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s) {
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}
				public void onTextChanged(CharSequence cs, int start, int before, int count) {
					try{
						signListFragment.getmAdapter().getFilter().filter(cs);
						try {
							String word = ((SimpleGson) signListFragment.getListView().getItemAtPosition(0)).getWord();
							signListFragment.getTextHeader().setText(word.substring(0, 1).toUpperCase(swedishLocale));
						} catch (IndexOutOfBoundsException e){
							Log.w("IndexErr", "IndexErr after change text");
						}
						signListFragment.setOldSearch(cs.toString());
					} catch (NullPointerException e){
						Log.w("NullPointer", "Probably the search was clicked too early.");
					}
				}

			});
			search.setText("");
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			break;
		case MainActivity.ID_COLLAPSE_BUTTON:
			((SignCategoryFragment) mAdapter.getItem(1)).collapseaAll();
			break;
		case MainActivity.ID_EDIT_BUTTON:
			FavouritesFragment favFrag = (FavouritesFragment) mAdapter.getItem(2);
			mMenu.clear();
			if(!favFrag.checkBoxesVisible()){
				mMenu.add(0, MainActivity.ID_EDIT_BUTTON, 1, R.string.edit_favs).setIcon(R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			} else {
				favFrag.deleteChecked();
				mMenu.add(0, MainActivity.ID_EDIT_BUTTON, 1, R.string.edit_favs).setIcon(R.drawable.ic_menu_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			}
			mMenu.add(0, MainActivity.ID_FLASH_BUTTON, 1, R.string.flash_cards).setIcon(R.drawable.light_bulb).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			ma.onPrepareOptionsMenu(mMenu);
			favFrag.toggleCheckBoxes();
			break;
		case MainActivity.ID_FLASH_BUTTON:
			Intent intent = new Intent(ma, FlashActivity.class);
			ma.startActivity(intent);
			break;
		}
		return true;
	}

	@Override
	public void onPause(){
		if(search != null){
			InputMethodManager imm = (InputMethodManager) ma.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
		}
		previousFrag = mJazzy.getCurrentItem();
		super.onPause();
	}

	@Override
	public void onDestroy(){
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
	public SignAlternativesAdapter getPager(){
		return mAdapter;
	}

	class SignAlternativesAdapter extends FragmentPagerAdapter {
		private SignListFragment signListFrag = null;
		private SignCategoryFragment signCatFrag = null;
		private FavouritesFragment favFrag = null;

		public SignAlternativesAdapter(Fragment fragment){
			super(fragment.getChildFragmentManager());
		}

		public void setFavFrag(FavouritesFragment fav){
			favFrag = fav;
		}

		public void setCatFrag(SignCategoryFragment cat){
			signCatFrag = cat;
		}

		public void setListFrag(SignListFragment lst){
			signListFrag = lst;
		}

		@Override
		public Fragment getItem(int position) {
			if(position==0){
				if(signListFrag == null){
					signListFrag = new SignListFragment();
				}
				return signListFrag;
			} else if(position==1){
				if(signCatFrag == null){
					signCatFrag = new SignCategoryFragment();
				}
				return signCatFrag;
			} else if(position==2){
				if(favFrag == null){
					favFrag = new FavouritesFragment();
				}
				return favFrag;
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			final Locale swedishLocale = new Locale("sv", "SE");
			if(MainActivity.LANGUAGE == MainActivity.NORWEGIAN){
				return MainActivity.CONTENT_NORWEGIAN[position % MainActivity.CONTENT_NORWEGIAN.length].toUpperCase(swedishLocale);
			} else {
				return MainActivity.CONTENT_SWEDISH[position % MainActivity.CONTENT_SWEDISH.length].toUpperCase(swedishLocale);
			}
		}

		@Override
		public int getCount() {
			return MainActivity.CONTENT_SWEDISH.length;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			Object obj = super.instantiateItem(container, position);
			//mJazzy.setObjectForPosition(obj, position);
			return obj;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			try{
				return ((Fragment)object).getView() == view;
			} catch (NullPointerException e){
				return false;
			}
		}
	}

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener(){
		public void onPageSelected(int position){

			if(MainActivity.LANGUAGE == MainActivity.NORWEGIAN){
				mGaTracker.sendEvent(MainActivity.LANG_STR, "page_swipe", MainActivity.CONTENT_NORWEGIAN[position], 1L);
			} else {
				mGaTracker.sendEvent(MainActivity.LANG_STR, "page_swipe", MainActivity.CONTENT_SWEDISH[position], 1L);
			}

			if(position==0){
				mMenu.clear();
				mMenu.add(0, MainActivity.ID_SEARCH_BUTTON, 1, R.string.search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.search_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
				ma.onPrepareOptionsMenu(mMenu);
			} else if (position==1){
				mMenu.clear();
				mMenu.add(0, MainActivity.ID_COLLAPSE_BUTTON, 1, R.string.collapse).setIcon(R.drawable.ic_media_group_collapse).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
				ma.onPrepareOptionsMenu(mMenu);
			} else if(position==2){
				// Update favourites
				FavouritesFragment favFrag = (FavouritesFragment) mAdapter.getItem(2);
				favFrag.notifyChange();
				if(favFrag.getAdapter() != null && favFrag.getAdapter().getCount() == 0){
					Crouton.makeText(getActivity(), getString(R.string.no_favourites), Style.INFO, (ViewGroup) getView()).show();
				}
				mMenu.clear();
				mMenu.add(0, MainActivity.ID_EDIT_BUTTON, 1, R.string.edit_favs).setIcon(R.drawable.ic_menu_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
				mMenu.add(0, MainActivity.ID_FLASH_BUTTON, 1, R.string.flash_cards).setIcon(R.drawable.light_bulb).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

				ma.onPrepareOptionsMenu(mMenu);
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	};
}
