package com.bjerva.tsplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.VideoView;

import com.bjerva.tsplex.GsonSign.Example;
import com.bjerva.tsplex.GsonSign.Word;

public class SignDetailFragment extends Fragment {

	private View myView;
	private VideoView myVideoView;
	private MainActivity ma;
	
	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.sign_detail_fragment, container, false);
		return myView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){		
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();

		myVideoView = (VideoView) myView.findViewById(R.id.myVideoView);
		//myVideoView.setDimensions(640, 480);
		
		GsonSign currSign = ma.getCurrentSign();
		if(currSign == null){
			Log.w("NULL SIGN", "NULL SIGN");
		} else {
			startUpHelper(currSign);
		}
	}
	
	void startUpHelper(final GsonSign currSign){
		String fileName = currSign.video_url.substring(0, currSign.video_url.length()-3)+"3gp";
		Log.i("SignDetail", fileName);
		//String uriPath = "http://130.237.171.78/"+fileName;//T01811.3gp
		//String uriPath = "http://130.237.171.46/system/videos/"+fileName;
		
		myVideoView.setVideoURI(Uri.parse(fileName));
		
		//Uri video = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.testfil2);	
		//myVideoView.setVideoURI(video);
		
		myVideoView.setMediaController(new MediaController(ma));
		myVideoView.requestFocus();

		//ArrayList<String> adapterItems = new ArrayList<String>();
		SeparatedListAdapter adapter = new SeparatedListAdapter(ma);
		
		if (currSign.words != null) {
			List<Word> words = currSign.words;
			String word = words.get(0).word;
			for(int j=1; j<words.size(); ++j){
				word += ", "+words.get(j).word;
			}
			
			adapter.addSection("Ord", new ArrayAdapter<String>(ma,
					R.layout.list_item, new String[] { word }));
			//adapterItems.add(word);
		}

		if(currSign.description != null){
			adapter.addSection("Beskrivning", new ArrayAdapter<String>(ma,
					R.layout.list_item, new String[] { currSign.description }));
			//adapterItems.add(currSign.description);
		} else {
			adapter.addSection("Beskrivning", new ArrayAdapter<String>(ma,
					R.layout.list_item, new String[] { "Tecknet har ingen beskrivning" }));
			//adapterItems.add("Tecknet har ingen beskrivning");
		}

		if(currSign.examples.size() > 0){
			String[] tmpEx = new String[currSign.examples.size()];
			for(int i=0; i<currSign.examples.size(); i++){
				tmpEx[i] = currSign.examples.get(i).description;
			}
			adapter.addSection("Exempel", new ArrayAdapter<String>(ma,
					R.layout.list_item, tmpEx));
		}
		
		if(currSign.versions.size() > 0){
			String[] tmpVer = new String[currSign.versions.size()];
			for(int i=0; i<currSign.versions.size(); ++i){
				tmpVer[i] = currSign.versions.get(i).description;
			}
			adapter.addSection("Varianter", new ArrayAdapter<String>(ma,
					R.layout.list_item, tmpVer));
		}

		if(currSign.tags.size() > 0){
			String[] tmpTags = new String[currSign.tags.size()];
			for(int i=0; i<currSign.tags.size(); ++i){
				tmpTags[i] = currSign.tags.get(i).tag;
			}
			adapter.addSection("Kategori", new ArrayAdapter<String>(ma,
					R.layout.list_item, tmpTags));
		}
		
		if(currSign.unusual) {
			adapter.addSection("Ovanligt", new ArrayAdapter<String>(ma,
					R.layout.list_item, new String[] { "Tecknet Šr ovanligt" }));
		}

		//Create and set adapter
		final ListView listView = (ListView) myView.findViewById(R.id.metaList);
		//final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ma, android.R.layout.simple_list_item_1, adapterItems);
		//listView.setAdapter(mAdapter);
		
				
		// create our list and custom adapter
		
		
		listView.setAdapter(adapter);
	

		//Set listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				if(position<=1){
					Log.i("VideoView", "Play sign");
					loadFilm(currSign.video_url);
				} else if(position < 2+currSign.examples.size()){
					Log.i("VideoView", "Play example: "+currSign.examples.get(position-2).video_url);
					loadFilm(currSign.examples.get(position-2).video_url);
				} else if(position < 2+currSign.versions.size()+currSign.examples.size()){
					Log.i("VideoView", "Play variant: "+currSign.versions.get(position-currSign.examples.size()-2));
					loadFilm(currSign.versions.get(position-currSign.examples.size()-2).video_url);
				} else {
					Log.i("VideoView", "I do naaaathing");
				}
			}
		});

		myVideoView.setOnErrorListener(new OnErrorListener(){
			@Override
			public boolean onError(MediaPlayer mp, int arg1, int arg2){
				ma.hideLoader();
				if(arg2 == -1004){
					ma.networkError();
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
		
		myVideoView.start();
	}
	
	private void loadFilm(String url){
		final String fileName = url.substring(0, url.length()-3)+"3gp";
		Log.i("SignDetail", url);
		Log.i("Loading new film", fileName);
		myVideoView.setMediaController(new MediaController(ma));
		myVideoView.setVideoURI(Uri.parse(fileName));
		myVideoView.requestFocus();
		myVideoView.start();
	}
	
	public Map<String,?> createItem(String title, String caption) {
		Map<String,String> item = new HashMap<String,String>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		return item;
	}
	
	/*
	public class FTPRequest extends AsyncTask<String, Void, String>{
		 
		String mhost = "130.237.171.46";
		String muser = "ftp";
		String mpass = "bjerva@ling.su.se";

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