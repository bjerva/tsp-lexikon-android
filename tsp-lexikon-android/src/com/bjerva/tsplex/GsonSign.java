package com.bjerva.tsplex;


import java.util.List;

import com.google.gson.annotations.SerializedName;

class GsonSign {

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

