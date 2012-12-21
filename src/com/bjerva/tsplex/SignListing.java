package com.bjerva.tsplex;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class SignListing extends Activity {
	//private final String jsonURL = "https://teckensprak.zanmato.se/signs.json";
	private final String jsonURL = "http://130.237.171.46/signs.json?changed_at=2012-03-28";
    public static ProgressDialog pbarDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_sign_listing);
        
        pbarDialog = new ProgressDialog(this);
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pbarDialog.setMessage("Loading signs...");
        pbarDialog.setCancelable(false);
        pbarDialog.show();
        
        JSONArray json;
		try {
			json = new JSONParser().execute(jsonURL).get();
			for(int i=0; i<json.length(); ++i){
	        	Log.i("JSON:", (String) json.getString(i));
	        	Thread.sleep(250);
	        }
			pbarDialog.hide();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_sign_listing, menu);
        return true;
    }
    
}
