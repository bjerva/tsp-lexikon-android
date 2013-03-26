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

import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.SharedPreferences;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.VideoView;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator.AnimatorListener;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bjerva.tsplex.MainActivity;
import com.bjerva.tsplex.R;
import com.bjerva.tsplex.adapters.SeparatedListAdapter;
import com.bjerva.tsplex.models.GsonSign;
import com.bjerva.tsplex.models.GsonSign.Word;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

public class SignDetailFragment extends Fragment {

	private static final String TAG = "DetailFragment";

	private View myView;
	private VideoView myVideoView;
	private MainActivity ma;
	private String lastPlayed = "";
	private boolean wasDisconnected = false;
	private boolean firstErr;
	private Menu mMenu = null;
	private GsonSign currSign;
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

    private HoloCircularProgressBar bufferBar;
    private boolean animateBufferBar;

	VideoView getVideoView(){
		return myVideoView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.sign_detail_fragment, container, false);
		firstErr = true;
		return myView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){		
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();

		mGaInstance = GoogleAnalytics.getInstance(ma);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");


        bufferBar = (HoloCircularProgressBar) myView.findViewById(R.id.holoCircularProgressBar1);
        animateBufferBar = true;
        
		myVideoView = (VideoView) myView.findViewById(R.id.myVideoView);
		myVideoView.setZOrderOnTop(true);
		final int width = ma.getScreenWidth();
		final int height = (int) (width*0.75);
		myVideoView.setLayoutParams(new LayoutParams(width, height));

		currSign = ma.getCurrentSign();

		if(ma.getScreenSize() != Configuration.SCREENLAYOUT_SIZE_XLARGE && 
				ma.getScreenSize() != Configuration.SCREENLAYOUT_SIZE_LARGE){
			setHasOptionsMenu(true);
			ma.onPrepareOptionsMenu(mMenu);  // Refresh the options menu now that we have our sign
		}

		if(currSign == null){
			Log.w(TAG, "NULL SIGN");
		} else {
			startUpHelper(currSign);
		}
		
		//showBufferBar();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);

		if(mMenu == null){
			mMenu = menu;
			mMenu.add(0, MainActivity.ID_FAV_BUTTON, 1, R.string.favourite).setIcon(R.drawable.my_star_off).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
		if(ma != null && currSign != null){
			mMenu.clear();
			SharedPreferences sharedPref = ma.getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
			if(sharedPref.contains(currSign.getWords().get(0).getWord())){
				Log.d(TAG, "Setting on");
				mMenu.add(0, MainActivity.ID_FAV_BUTTON, 1, R.string.favourite).setIcon(R.drawable.my_star_on).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			} else {
				Log.d(TAG, "Setting off");
				mMenu.add(0, MainActivity.ID_FAV_BUTTON, 1, R.string.favourite).setIcon(R.drawable.my_star_off).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			}
		}

		super.onCreateOptionsMenu(mMenu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		//TODO: Clean this up
		switch (item.getItemId()) {
		case MainActivity.ID_FAV_BUTTON:
			SharedPreferences sharedPref = ma.getSharedPreferences("SignDetails", Activity.MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = sharedPref.edit();
			if(!sharedPref.contains(currSign.getWords().get(0).getWord())){
				Log.d(TAG, "Adding");
				prefEditor.putInt(currSign.getWords().get(0).getWord(), currSign.getId());
			} else {
				Log.d(TAG, "Removing");
				prefEditor.remove(currSign.getWords().get(0).getWord());
			}
			prefEditor.apply();
			mMenu.clear();
			if(sharedPref.contains(currSign.getWords().get(0).getWord())){
				Log.d(TAG, "Setting on");
				mMenu.add(0, MainActivity.ID_FAV_BUTTON, 1, R.string.favourite).setIcon(R.drawable.my_star_on).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			} else {
				Log.d(TAG, "Setting off");
				mMenu.add(0, MainActivity.ID_FAV_BUTTON, 1, R.string.favourite).setIcon(R.drawable.my_star_off).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			}
			ma.onPrepareOptionsMenu(mMenu);
			break;
		}
		return true;
	}

	void startUpHelper(final GsonSign currSign){
		String fileName = currSign.getVideo_url().substring(0, currSign.getVideo_url().length()-3)+"3gp";
		Log.i("SignDetail", fileName);

		myVideoView.setVideoURI(Uri.parse(fileName));
		lastPlayed = fileName;

		myVideoView.setMediaController(new MediaController(ma));
		myVideoView.requestFocus();

		SeparatedListAdapter adapter = new SeparatedListAdapter(ma);

		if (currSign.getWords() != null) {
			List<Word> words = currSign.getWords();
			String word = words.get(0).getWord();
			for(int j=1; j<words.size(); ++j){
				word += ", "+words.get(j).getWord();
			}

			adapter.addSection(getString(R.string.word), new ArrayAdapter<String>(ma,
					R.layout.list_item, new String[] { word }));
		}

		if(currSign.getDescription() != null){
			adapter.addSection(getString(R.string.desc), new ArrayAdapter<String>(ma,
					R.layout.list_item, new String[] { currSign.getDescription() }));
		} else {
			adapter.addSection(getString(R.string.desc), new ArrayAdapter<String>(ma,
					R.layout.list_item, new String[] { getString(R.string.no_desc) }));
		}

		if(currSign.getExamples().size() > 0){
			String[] tmpEx = new String[currSign.getExamples().size()];
			for(int i=0; i<currSign.getExamples().size(); i++){
				tmpEx[i] = currSign.getExamples().get(i).getDescription();
			}
			adapter.addSection(getString(R.string.example), new ArrayAdapter<String>(ma,
					R.layout.list_item, tmpEx));
		} 

		if(currSign.getVersions().size() > 0){
			String[] tmpVer = new String[currSign.getVersions().size()];
			for(int i=0; i<currSign.getVersions().size(); ++i){
				tmpVer[i] = currSign.getVersions().get(i).getDescription();
			}
			adapter.addSection(getString(R.string.ver), new ArrayAdapter<String>(ma,
					R.layout.list_item, tmpVer));
		} 

		if(currSign.getTags().size() > 0){
			String[] tmpTags = new String[currSign.getTags().size()];
			for(int i=0; i<currSign.getTags().size(); ++i){
				tmpTags[i] = currSign.getTags().get(i).getTag();
			}
			adapter.addSection(getString(R.string.cat), new ArrayAdapter<String>(ma,
					R.layout.list_item, tmpTags));
		}

		if(currSign.isUnusual()) {
			adapter.addSection(getString(R.string.unusual), new ArrayAdapter<String>(ma,
					R.layout.list_item, new String[] { getString(R.string.is_unusual) }));
		}

		//Create and set adapter
		final ListView listView = (ListView) myView.findViewById(R.id.metaList);

		listView.setAdapter(adapter);

		//Set listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				final String url;
				firstErr = true;
				if(position<=3){
					Log.i("VideoView", "Play sign");
					url = currSign.getVideo_url();
					if(url.equals(lastPlayed)){
						replay();
					} else {
						loadFilm(url);
						lastPlayed = url;
					}
				} else if(position < 5+currSign.getExamples().size()){
					url = currSign.getExamples().get(position-5).getVideo_url();
					Log.i("VideoView", "Play example: "+url);
					mGaTracker.sendEvent("example", "favourite_click", currSign.getExamples().get(position-5).getDescription(), 1L);
					if(url.equals(lastPlayed)){
						replay();
					} else {
						loadFilm(url);
						lastPlayed = url;
					}
				} else if(currSign.getExamples().size() > 0 && position < 6+currSign.getVersions().size()+currSign.getExamples().size()){
					url = currSign.getVersions().get(position-currSign.getExamples().size()-6).getVideo_url();
					Log.i("VideoView", "Play variant: "+url);
					mGaTracker.sendEvent("example", "variant_click", currSign.getVersions().get(position-currSign.getExamples().size()-6).getDescription(), 1L);
					if(url.equals(lastPlayed)){
						replay();
					} else {
						loadFilm(url);
						lastPlayed = url;
					}
				} else if(position < 5+currSign.getVersions().size()+currSign.getExamples().size()){
					url = currSign.getVersions().get(position-currSign.getExamples().size()-5).getVideo_url();
					Log.i("VideoView", "Play variant: "+url);
					if(url.equals(lastPlayed)){
						replay();
					} else {
						loadFilm(url);
						lastPlayed = url;
					}
				} else {
					Log.i("VideoView", "I do naaaathing");
				}
			}
		});

		myVideoView.setOnErrorListener(new OnErrorListener(){
			@Override
			public boolean onError(MediaPlayer mp, int arg1, int arg2){
				if (firstErr){
					firstErr = false;
					String fileName = lastPlayed.substring(0, lastPlayed.length()-3)+"mp4";
					fileName = "http://130.237.171.46/system/videos/"+fileName.substring(22);
					Log.i("SignDetail", fileName);
					myVideoView.setVideoURI(Uri.parse(fileName));
					playNormal();
					return true;
				}
				ma.hideLoader();
				if(arg2 == -1004){
					ma.serverError();
				} else {
					ma.errorPlayingVideo();
				}
				return true;
			}
		});

		myVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				ma.hideLoader();
			}
		});

		playNormal();
	}

	private void loadFilm(String url){
		checkConnection();
		final String fileName = url.substring(0, url.length()-3)+"3gp";
		Log.i("SignDetail", url);
		Log.i("Loading new film", fileName);
		myVideoView.setMediaController(new MediaController(ma));
		myVideoView.setVideoURI(Uri.parse(fileName));
		myVideoView.requestFocus();
		checkConnection();
		myVideoView.start();
	}

	private void replay(){
		//checkConnection();
		myVideoView.seekTo(1);
		myVideoView.start();
	}

	private void checkConnection(){
		ConnectivityManager connectivityManager = (ConnectivityManager) ma.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if(activeNetworkInfo == null){
			wasDisconnected = true;
		} else if(wasDisconnected) {
			//myView.requestLayout();
		}
	}
	/*
	private void playSlowMo(){
		Timer timer = new Timer();
		timer.schedule( new TimerTask() {

			@Override
			public void run() {
				myVideoView.start();
			}
		}, 0, 200);

		timer.schedule( new TimerTask() {
			@Override
			public void run() {
				myVideoView.pause();
			}
		}, 100, 200);
	}
	 */

	private void playNormal(){
		myVideoView.start();
	}

	/**
	 * Make the buffer bar visible and start animation
	 */
	public void showBufferBar(){
		Log.d(TAG, "Showing bar");
		bufferBar.setVisibility(View.VISIBLE);
		animateBufferBar = true;
		animate(bufferBar, mAnimatorListener);
	}

	/**
	 * Make the buffer bar invisible and stop animation
	 */
	public void hideBufferBar(){
		Log.d(TAG, "Hiding bar");
		bufferBar.setVisibility(View.GONE);
		animateBufferBar = false;
	}

	private AnimatorListener mAnimatorListener = new AnimatorListener() {
		@Override
		public void onAnimationEnd(final Animator animation) {
			// Repeat the animation as long as necessary
			if(animateBufferBar){
				animate(bufferBar, this);
			}
		}

		@Override
		public void onAnimationCancel(final Animator animation) {}
		@Override
		public void onAnimationRepeat(final Animator animation) {}
		@Override
		public void onAnimationStart(final Animator animation) {}
	};

	/**
	 * Animate.
	 * 
	 * @param progressBar
	 *            the progress bar
	 * @param listener
	 *            the listener
	 */
	private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener) {
		progressBar.setProgress(0.0f);
		final float progress = 10;                                      // Animation loops
		final ObjectAnimator progressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
		progressBarAnimator.setDuration(10000);         // Animation duration
		progressBarAnimator.addListener(listener);
		progressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
		progressBarAnimator.start();
	}

	/*
	public class FTPRequest extends AsyncTask<String, Void, String>{


	    protected String doInBackground(String... fileinfo) {
			FTPClient client = new FTPClient();
			BufferedOutputStream fos = null;

			try {
				client.connect(mhost);
				client.login(muser, mpass);

				client.enterLocalPassiveMode(); // important!
				client.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
				fos = new BufferedOutputStream(
						getActivity().openFileOutput("test_file_local3.mp4", Context.MODE_PRIVATE));
						//new FileOutputStream(fileinfo[1]));
				//fos.write(buffer)
				client.retrieveFile("droid/"+fileinfo[1], fos);

				for(FTPFile file: client.listFiles()){
					Log.w("Files", file.toString());
				}
				//fos.write();
				Log.w("FTP", "Success: "+fileinfo[1]);
			}//try 
			catch (IOException e) {
				Log.e("FTP", "Error Getting File");
				e.printStackTrace();
			}//catch
			finally {
				try {
					if (fos != null) fos.close();
					client.disconnect();
				}//try
				catch (IOException e) {
					Log.e("FTP", "Disconnect Error");
					e.printStackTrace();
				}//catch
			}//finally
			Log.v("FTP", "Done");  
	        return "Done";
	    }
	    protected void onPostExecute(){
	    	Log.i("Async", "Finished FTP request");
	    }
	}
	 */
}