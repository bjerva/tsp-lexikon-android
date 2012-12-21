package com.bjerva.tsplex;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.os.AsyncTask;
import android.util.Log;
 
public class JSONParser extends AsyncTask<String, Void, JSONArray>{
 
    static InputStream is = null;
    static JSONArray jArray = null;
    static String json = "";
 	
    protected JSONArray doInBackground(String... url) {
    	
        // Making HTTP request
        try {
        	
        	HttpClient client = new DefaultHttpClient();
        	HttpGet httpGet = new HttpGet(url[0]);
        	
            // defaultHttpClient
            HttpResponse httpResponse = client.execute(httpGet);
             
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            
            //XXX: This is the bottleneck.
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jArray = new JSONArray(sb.toString());
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        
        return jArray;
 
    }
    
    protected void onPostExecute(){
    	Log.i("Async", "Downloaded all signs");
    }


}