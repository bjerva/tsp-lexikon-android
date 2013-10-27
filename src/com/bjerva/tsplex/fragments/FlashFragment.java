package com.bjerva.tsplex.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.VideoView;

import com.bjerva.tsplex.FlashActivity;
import com.bjerva.tsplex.MainActivity;
import com.bjerva.tsplex.R;
import com.bjerva.tsplex.models.GsonSign;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class FlashFragment extends Fragment{

	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	private FlashActivity fa;
	private TextView tv;
	private Button mResponse0, mResponse1, mResponse2;

	private int count = 0;
	private int currentQuestion = 0;
	private int correct = 0;
	private int incorrect = 0;
	private int correctResponse;
	private VideoView mVideoView;
	private GsonSign currSign;

	private List<Integer> viewOrder;

	private ArrayList<String> errorSigns = new ArrayList<String>();

	private Random mR = new Random();

	private View myView;

	private boolean firstErr = true;
	
	private Drawable buttonBackground;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.fragment_flash, container, false);
		tv = (TextView) myView.findViewById(R.id.flash_text);
		mResponse0 = (Button) myView.findViewById(R.id.response_0);
		mResponse1 = (Button) myView.findViewById(R.id.response_1);
		mResponse2 = (Button) myView.findViewById(R.id.response_2);
		mVideoView = (VideoView) myView.findViewById(R.id.myFlashVideoView);
		
		buttonBackground = mResponse0.getBackground();
		return myView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		fa = (FlashActivity) getActivity();

		mGaInstance = GoogleAnalytics.getInstance(fa);
		mGaTracker = mGaInstance.getTracker("UA-39295928-1");

		mVideoView.setZOrderOnTop(true);
		final int width = ((FlashActivity) getActivity()).getScreenWidth();
		final int height = (int) (width*0.75);
		mVideoView.setLayoutParams(new LayoutParams(width, height));

		mResponse0.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				submitSelectionAnswer(0);
			}
		});
		mResponse1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				submitSelectionAnswer(1);
			}
		});
		mResponse2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				submitSelectionAnswer(2);
			}
		});

		viewOrder = new ArrayList<Integer>(fa.getFlashList().length);
		for (int i = 0; i<fa.getFlashList().length; i++){
			viewOrder.add(i);
		}
		Collections.shuffle(viewOrder, mR);

		getNextQuestion();
	}

	private void getNextQuestion(){
		resetButtons();
		firstErr = true;
		if (fa.getFlashList().length < 3){
			String txt = "Du måste ha minst 3 tecken i din favoritlista för att starta quizet";
			tv.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.GONE);
			tv.setText(txt);
			mResponse0.setVisibility(View.GONE);
			mResponse1.setVisibility(View.GONE);
			mResponse2.setVisibility(View.GONE);
		} else if (currentQuestion < fa.getFlashList().length){
			fa.showLoader();
			fa.loadSingleJson(fa.getIdList()[viewOrder.get(currentQuestion)]);
			currSign = fa.getCurrentSign();

			if(currSign == null){
				Log.w("..", "NULL SIGN");
			} else {
				startUpHelper(currSign);
			}

			//tv.setText((String) fa.getFlashList()[currentQuestion]);

			correctResponse = mR.nextInt(2);
			String alt0 = "";
			String alt1 = "";
			String alt2 = "";

			switch (correctResponse){
			case 0:
				alt0 = (String) fa.getFlashList()[viewOrder.get(currentQuestion)];
				alt1 = getRandomString(new HashSet<String>(Arrays.asList(alt0)));
				alt2 = getRandomString(new HashSet<String>(Arrays.asList(alt0, alt1)));
				break;
			case 1:
				alt1 = (String) fa.getFlashList()[viewOrder.get(currentQuestion)];
				alt0 = getRandomString(new HashSet<String>(Arrays.asList(alt1)));
				alt2 = getRandomString(new HashSet<String>(Arrays.asList(alt0, alt1)));
				break;	
			case 2:
				alt2 = (String) fa.getFlashList()[viewOrder.get(currentQuestion)];
				alt1 = getRandomString(new HashSet<String>(Arrays.asList(alt2)));
				alt0 = getRandomString(new HashSet<String>(Arrays.asList(alt2, alt1)));
				break;
			}

			mResponse0.setText(alt0);
			mResponse1.setText(alt1);
			mResponse2.setText(alt2);

			currentQuestion++;
		} else {
			String result = "Quizzet är klart!\nRätta svar: "+correct+"\nFelaktiga svar: "+incorrect;
			tv.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.GONE);
			tv.setText(result);
			mResponse0.setVisibility(View.GONE);
			mResponse1.setVisibility(View.GONE);
			mResponse2.setVisibility(View.GONE);
		}
	}

	private String getRandomString(HashSet<String> alreadyUsed){
		String randomWord = "";
		int count = 0;

		do{
			randomWord = (String) fa.getFlashList()[mR.nextInt(fa.getFlashList().length)];
			count++;
		} while (alreadyUsed.contains(randomWord) && count < 500);

		return randomWord;
	}

	private void submitBooleanAnswer(boolean response){
		if(response){
			correct++;
		} else {
			incorrect++;
		}
		getNextQuestion();
	}

	private void submitSelectionAnswer(int selection){
		if(correctResponse == selection){
			if(firstErr){
				correct++;
			}
			getNextQuestion();
		} else {
			highlightButton(selection);
			if (firstErr){
				firstErr = false;
				incorrect++;
				errorSigns.add((String) fa.getFlashList()[viewOrder.get(currentQuestion-1)]);
			}
		}
	}

	private void highlightButton(int num){
		switch (num){
		case 0:
			mResponse0.setBackgroundResource(R.color.holo_red_dark);
			break;
		case 1:
			mResponse1.setBackgroundResource(R.color.holo_red_dark);
			break;	
		case 2:
			mResponse2.setBackgroundResource(R.color.holo_red_dark);
			break;
		}
	}

	private void resetButtons(){
		mResponse0.setBackgroundDrawable(buttonBackground);
		mResponse1.setBackgroundDrawable(buttonBackground);
		mResponse2.setBackgroundDrawable(buttonBackground);
	}

	void startUpHelper(final GsonSign currSign){
		final String fileName;
		if(MainActivity.LANGUAGE == MainActivity.NORWEGIAN){
			fileName = "http://www.minetegn.no/Tegnordbok-HTML/video_/"+currSign.getVideo_url()+".mp4";
		} else {
			fileName = currSign.getVideo_url().substring(0, currSign.getVideo_url().length()-3)+"3gp";
		}
		Log.i("SignDetail", fileName);

		mVideoView.setVideoURI(Uri.parse(fileName));

		mVideoView.setMediaController(new MediaController(fa));
		mVideoView.requestFocus();

		mVideoView.setOnErrorListener(new OnErrorListener(){
			@Override
			public boolean onError(MediaPlayer mp, int arg1, int arg2){
				//if (firstErr){
				//firstErr = false;
				//String fileName = "T003243";//;
				String tmpFileName = "http://130.237.171.46/system/videos/"+fileName.substring(22, fileName.length()-3)+"mp4";
				Log.i("SignDetail", tmpFileName);
				mVideoView.setVideoURI(Uri.parse(tmpFileName));
				playNormal();
				//return true;
				//}
				//fa.hideLoader();
				if(arg2 == -1004){
					//fa.serverError();
				} else {
					//fa.errorPlayingVideo();
				}
				return true;
			}
		});

		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				fa.hideLoader();
			}
		});

		playNormal();
	}

	private void loadFilm(String url){
		final String fileName;
		if(MainActivity.LANGUAGE == MainActivity.NORWEGIAN){
			fileName = "http://www.minetegn.no/Tegnordbok-HTML/video_/"+url+".mp4";
		} else {
			fileName = url.substring(0, url.length()-3)+"3gp";
		}
		Log.i("SignDetail", url);
		Log.i("Loading new film", fileName);
		mVideoView.setMediaController(new MediaController(getActivity()));
		mVideoView.setVideoURI(Uri.parse(fileName));
		mVideoView.requestFocus();
		mVideoView.start();
	}

	private void playNormal(){
		mVideoView.start();
	}

	/*
	public void errorPlayingVideo(){
		Crouton.makeText(this, getString(R.string.play_error), Style.ALERT, (ViewGroup) findViewById(R.id.fragment_container)).show();
	}

	public void serverError(){
		Crouton.makeText(this, getString(R.string.serv_error), Style.ALERT, (ViewGroup) findViewById(R.id.fragment_container)).show();
	}

	public void connectionError(){
		Crouton.makeText(this, getString(R.string.conn_error), Style.ALERT, (ViewGroup) findViewById(R.id.fragment_container)).show();
	}

	public void hideLoader(){
		pbarDialog.dismiss();
	}
	 */

}


