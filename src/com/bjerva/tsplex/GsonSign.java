package com.bjerva.tsplex;


import java.util.List;

import com.google.gson.annotations.SerializedName;

class GsonSign {
	public List<Example> getExamples() {
		return examples;
	}

	public List<Version> getVersions() {
		return versions;
	}

	public List<Word> getWords() {
		return words;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public boolean isUnusual() {
		return unusual;
	}

	public String getVideo_url() {
		return video_url;
	}

	public String getDescription() {
		return description;
	}

	List<Example> examples;
	List<Version> versions;
	List<Word> words;
	List<Tag> tags;
	
	@SerializedName("unusual")
	boolean unusual;
	
	@SerializedName("video_url")
	String video_url;
	
	@SerializedName("description")
	String description;
	
	class Example {
		@SerializedName("video_url")
		String video_url;
		
		@SerializedName("description")
		String description;
	}
	
	class Version {
		@SerializedName("video_url")
		String video_url;
		
		@SerializedName("description")
		String description;
	}
	
	class Word {
		@SerializedName("word")
		String word;
	}
	
	class Tag {
		@SerializedName("tag")
		String tag;
	}
}

