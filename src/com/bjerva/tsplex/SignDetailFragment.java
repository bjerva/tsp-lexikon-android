package com.bjerva.tsplex;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.VideoView;

public class SignDetailFragment extends Fragment {

	private View myView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		myView = inflater.inflate(R.layout.sign_detail_fragment, container, false);
		return myView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){		
		super.onActivityCreated(savedInstanceState);
		MainActivity ma = (MainActivity) getActivity();

		VideoView myVideoView = (VideoView) myView.findViewById(R.id.myVideoView);
		Log.i("SignDetail", ma.getCurrentSign().video_url);
		//myVideoView.setVideoURI(Uri.parse(ma.getCurrentSign().video_url));
		//myVideoView.setVideoURI(Uri.parse("ftp://130.237.171.78/V00080a.mov"));
		//String uriPath = "android.resource://com.bjerva.tsplex/raw/v01539";
		//myVideoView.setVideoURI(Uri.parse(uriPath));
		
		GetFileFTP("", "T01539.mp4");
		String test = "fisk";
		try {
			test = new FTPRequest().execute("", "T01539.mp4").get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			test = "FAILUUUUURE";
		}
		Log.w("Yarr", test);
		
		//myVideoView.setVideoPath(path);
		String[] files = getActivity().getApplication().fileList();
		for(String file: files){
			Log.w("Files", file);
		}
		
		myVideoView.setVideoPath(getActivity().getApplication().getFilesDir()+"/test_file_local3.mp4");
		myVideoView.setMediaController(new MediaController(ma));
		myVideoView.requestFocus();
		myVideoView.start();

		ArrayList<String> adapterItems = new ArrayList<String>();
		adapterItems.add("Description:\t"+ma.getCurrentSign().description);
		adapterItems.add("Updated:\t"+ma.getCurrentSign().updated);
		adapterItems.add("Unusual:\t"+ma.getCurrentSign().unusual);

		//Create and set adapter
		ListView listView = (ListView) myView.findViewById(R.id.metaList);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ma, android.R.layout.simple_list_item_1, adapterItems);
		listView.setAdapter(mAdapter);

		//Set listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){

			}
		});

	}
	
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
				client.retrieveFile(fileinfo[1], fos);
				
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

	
	public void GetFileFTP(String srcFileSpec, String filename) {
  
	}//getfileFTP

}
