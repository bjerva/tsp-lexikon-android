package com.bjerva.tsplex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @deprecated
 * @author Johannes
 */

public class SignModel {
	public final boolean deleted;
	public final boolean unusual;
	
	//public final int ref_id;
	//public final int id;
	
	public final String video_url;
	//public final String updated;
	public String description;
	
	public final Example[] examples;
	public final String[] tags;
	public final String[] words;
	public final String[] versions;
	/*
	public final String[] snapshots;
	*/
	
	
	public SignModel(){
		this.deleted 	 = false;
		this.unusual 	 = false;
		
		//this.ref_id 	 = 0;
		//this.id 		 = 0;
		
		this.video_url 	 = null;
		//this.updated 	 = "";
		this.description = null;
		
		this.examples 	 = null;
		this.tags 		 = null;
		this.words 		 = null;
		this.versions 	 = null;
	}

	public SignModel(JSONObject signInfo) throws JSONException{
		this.deleted 	 = (Boolean) signInfo.get("deleted");
		this.unusual 	 = (Boolean) signInfo.get("unusual");
		
		//this.ref_id 	 = Integer.valueOf(((String) signInfo.get("ref_id")));
		//this.id 		 = (Integer) signInfo.get("id");
		
		this.video_url 	 = (String) signInfo.get("video_url");
		//this.updated 	 = (String) signInfo.get("updated_at");
		
		
		/*
		try{
			this.description = new String[] {(String) signInfo.get("description")};
		} catch (ClassCastException e) {
			JSONArray tmpDescr = (JSONArray) signInfo.get("description");
			this.description = new String[tmpDescr.length()];
			for(int i=0; i<tmpDescr.length(); ++i){
				this.description[i] = (String) tmpDescr.getJSONObject(i).get("description");
			}
		}
		*/
		
		this.examples 	 = getExamples((JSONArray) signInfo.get("examples"));
		this.tags 		 = getTags((JSONArray) signInfo.get("tags"));
		this.words 		 = getWords((JSONArray) signInfo.get("words"));
		
		
		this.description = signInfo.get("description").toString();
		if(this.description == null){
			this.description = "Ingen beskrivning av tecknet hittades.";
		}
		
		/*
		this.snapshots 	 = (String[]) signInfo.get("snapshots");
		*/
		
		JSONArray tmpVersions = (JSONArray) signInfo.get("versions");
		this.versions	 =  new String[tmpVersions.length()];
		for(int i=0; i<tmpVersions.length(); ++i){
			this.versions[i] = (String) ((JSONObject) tmpVersions.get(i)).get("video_url");
		}
	}
	
	private Example[] getExamples(JSONArray tempExamples) throws JSONException{
		Example[] examples = new Example[tempExamples.length()];
		for(int i=0; i<tempExamples.length(); ++i){
			examples[i] = new Example(tempExamples.getJSONObject(i));
		}
		return examples;
	}
	
	private String[] getTags(JSONArray tempTags) throws JSONException{
		String[] tags = new String[tempTags.length()];
		for(int i=0; i<tempTags.length(); ++i){
			tags[i] = (String) tempTags.getJSONObject(i).get("tag");
		}
		return tags;
	}
	
	private String[] getWords(JSONArray tempWords) throws JSONException{
		String[] words = new String[tempWords.length()];
		for(int i=0; i<tempWords.length(); ++i){
			words[i] = (String) tempWords.getJSONObject(i).get("word");
		}
		return words;
	}
	
	@Override
	public String toString(){
		String str = "Deleted:\t"+ String.valueOf(this.deleted);
		str += "\nUnusual:\t"+ String.valueOf(this.unusual);
		//str += "\nRef id:\t"+String.valueOf(this.ref_id);
		//str += "\nId:\t"+String.valueOf(this.id);
		str += "\nURL:\t"+this.video_url;
		//str += "\nUpdated:\t"+this.updated;
		str += "\nDescription:\t"+this.description;
		str += "\nExamples:";
		/*for(Example ex: examples){
			str += "\nID:\t"+ex.id;
			str += "\nURL:\t"+ex.video_url;
			str += "\nDescription:\t"+ex.description;
		}*/
		str += "\nTags:";
		for(String ex: tags){
			str += "\n"+ex;
		}
		str += "\nWords:";
		for(String ex: words){
			str += "\n"+ex;
		}
		return str;		
	}
	
	class Example{
		public final String video_url;
		public final String description;
		
		public Example(JSONObject signInfo) throws JSONException{
			this.video_url 	 = (String) signInfo.get("video_url");
			this.description = (String) signInfo.get("description");
		}
	}
	
	class Tag{
		public final String tag;
		public final int id;
		
		public Tag(JSONObject signInfo) throws JSONException{
			this.tag	= (String) signInfo.get("tag");
			this.id  	= (Integer) signInfo.get("id");
		}
	}
	
	class Word{
		public final String word;
		public final int id;
		
		public Word(){
			this.word = "Temp";
			this.id = 0;
		}
		
		public Word(JSONObject signInfo) throws JSONException{
			this.word	= (String) signInfo.get("word");
			this.id  	= (Integer) signInfo.get("id");
		}
	}
}
