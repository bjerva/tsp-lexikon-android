package com.bjerva.tsplex;

import org.json.JSONArray;
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
	
	public final Example[] examples;
	public final Tag[] tags;
	public final Word[] words;
	/*
	public final String[] snapshots;
	public final String[] versions;
	*/

	public SignModel(JSONObject signInfo) throws JSONException{
		this.deleted 	 = (Boolean) signInfo.get("deleted");
		this.unusual 	 = (Boolean) signInfo.get("unusual");
		
		this.ref_id 	 = Integer.valueOf(((String) signInfo.get("ref_id")));
		this.id 		 = (Integer) signInfo.get("id");
		
		this.video_url 	 = (String) signInfo.get("video_url");
		this.updated 	 = (String) signInfo.get("updated_at");
		this.description = (String) signInfo.get("description");
		
		this.examples 	 = getExamples((JSONArray) signInfo.get("examples"));
		this.tags 		 = getTags((JSONArray) signInfo.get("tags"));
		this.words 		 = getWords((JSONArray) signInfo.get("words"));
		/*
		this.snapshots 	 = (String[]) signInfo.get("snapshots");
		this.versions	 = (String[]) signInfo.get("versions");
		*/
	}
	
	private Example[] getExamples(JSONArray tempExamples) throws JSONException{
		Example[] examples = new Example[tempExamples.length()];
		for(int i=0; i<tempExamples.length(); ++i){
			examples[i] = new Example(tempExamples.getJSONObject(i));
		}
		return examples;
	}
	
	private Tag[] getTags(JSONArray tempTags) throws JSONException{
		Tag[] tags = new Tag[tempTags.length()];
		for(int i=0; i<tempTags.length(); ++i){
			tags[i] = new Tag(tempTags.getJSONObject(i));
		}
		return tags;
	}
	
	private Word[] getWords(JSONArray tempWords) throws JSONException{
		Word[] words = new Word[tempWords.length()];
		for(int i=0; i<tempWords.length(); ++i){
			words[i] = new Word(tempWords.getJSONObject(i));
		}
		return words;
	}
	
	public String toString(){
		String str = "Deleted:\t"+ String.valueOf(this.deleted);
		str += "\nUnusual:\t"+ String.valueOf(this.unusual);
		str += "\nRef id:\t"+String.valueOf(this.ref_id);
		str += "\nId:\t"+String.valueOf(this.id);
		str += "\nURL:\t"+this.video_url;
		str += "\nUpdated:\t"+this.updated;
		str += "\nDescription:\t"+this.description;
		str += "\nExamples:";
		for(Example ex: examples){
			str += "\nID:\t"+ex.id;
			str += "\nURL:\t"+ex.video_url;
			str += "\nDescription:\t"+ex.description;
		}
		str += "\nTags:";
		for(Tag ex: tags){
			str += "\nID:\t"+ex.id;
			str += "\nTag:\t"+ex.tag;
		}
		str += "\nWords:";
		for(Word ex: words){
			str += "\nID:\t"+ex.id;
			str += "\nTag:\t"+ex.word;
		}
		return str;		
	}
	
	class Example{
		public final String video_url;
		public final String description;
		public final String id;
		
		public Example(JSONObject signInfo) throws JSONException{
			this.id 		 = (String) signInfo.get("id");
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
		
		public Word(JSONObject signInfo) throws JSONException{
			this.word	= (String) signInfo.get("word");
			this.id  	= (Integer) signInfo.get("id");
		}
	}
}
