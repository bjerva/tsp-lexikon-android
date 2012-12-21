package com.bjerva.tsplex;

import org.json.JSONException;
import org.json.JSONObject;

public class SignModel {
	public final boolean deleted;
	public final boolean unusual;
	
	public final int ref_id;
	public final int id;
	
	public final String video_url;
	public final String updated;
	public final String description;
	/*
	public final String[] snapshots;
	public final String[] versions;
	public final String[] examples;
	public final String[] tags;
	public final String[] words;*/

	public SignModel(JSONObject signInfo) throws JSONException{
		this.deleted 	 = (Boolean) signInfo.get("deleted");
		this.unusual 	 = (Boolean) signInfo.get("unusual");
		
		this.ref_id 	 = Integer.valueOf(((String) signInfo.get("ref_id")));
		this.id 		 = (Integer) signInfo.get("id");
		
		this.video_url 	 = (String) signInfo.get("video_url");
		this.updated 	 = (String) signInfo.get("updated_at");
		this.description = (String) signInfo.get("description");
		/*
		this.snapshots 	 = (String[]) signInfo.get("snapshots");
		this.versions	 = (String[]) signInfo.get("versions");
		this.examples 	 = (String[]) signInfo.get("examples");
		this.tags 		 = (String[]) signInfo.get("tags");
		this.words 		 = (String[]) signInfo.get("words");*/
	}
	
	public String toString(){
		String str = "Deleted:\t"+ String.valueOf(this.deleted);
		str += "\nUnusual:\t"+ String.valueOf(this.unusual);
		str += "\nRef id:\t"+String.valueOf(this.ref_id);
		str += "\nId:\t"+String.valueOf(this.id);
		str += "\nURL:\t"+this.video_url;
		str += "\nUpdated:\t"+this.updated;
		str += "\nDescription:\t"+this.description;
		return str;		
	}
	
	private class Example{
		
	}
	
	private class Tag{
		
	}
	
	private class Word{
		
	}
}
