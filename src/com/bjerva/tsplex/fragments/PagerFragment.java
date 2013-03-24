package com.bjerva.tsplex.fragments;

import java.util.Locale;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bjerva.tsplex.MainActivity;
import com.bjerva.tsplex.R;
import com.viewpagerindicator.TabPageIndicator;

public class PagerFragment extends Fragment {
	private static final String TAG = "PagerFragment";
	private static final String[] CONTENT = new String[] { "Tecken", "Kategorier", "Favoriter"};

	private FragmentPagerAdapter mAdapter;
	private ViewPager mPager;
	private View mView;
	private Menu mMenu = null;
	private MenuInflater mMenuInflater = null;
	private MainActivity ma;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		mView = inflater.inflate(R.layout.pager_fragment, container, false);
		setHasOptionsMenu(true);
		return mView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();

		mAdapter = new SignAlternativesAdapter(this);

		mPager = (ViewPager) ma.findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setOffscreenPageLimit(3);

		TabPageIndicator indicator = (TabPageIndicator) ma.findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setOnPageChangeListener(mOnPageChangeListener);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "CreateOptions");
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);

		if(mMenu == null || mMenuInflater == null){
			mMenu = menu;
			mMenuInflater = inflater;
			mMenu.add(0, 1, 1, R.string.search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.search_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	class SignAlternativesAdapter extends FragmentPagerAdapter {
		private SignListFragment signListFrag = null;
		private SignCategoryFragment signCatFrag = null;
		private FavouritesFragment favFrag = null;

		public SignAlternativesAdapter(android.support.v4.app.Fragment fragment){
			super(fragment.getChildFragmentManager());
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
			return CONTENT[position % CONTENT.length].toUpperCase(swedishLocale);
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener(){
		public void onPageSelected(int position){
			if(position==0){
				mMenu.clear();
				mMenu.add(0, 1, 1, R.string.search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.search_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
				ma.onPrepareOptionsMenu(mMenu);
			} else if(position==2){
				((FavouritesFragment)mAdapter.getItem(2)).notifyChange();
				mMenu.clear();
				mMenu.add(0, 1, 1, R.string.edit_favs).setIcon(R.drawable.edit_query).setActionView(R.layout.search_view).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
				ma.onPrepareOptionsMenu(mMenu);
			}
			Log.d(TAG, ""+position);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	};
}
