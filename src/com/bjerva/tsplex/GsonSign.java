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

	private List<Example> examples;
	private List<Version> versions;
	private List<Word> words;
	private List<Tag> tags;
	
	@SerializedName("unusual")
	private boolean unusual;
	
	@SerializedName("video_url")
	private String video_url;
	
	@SerializedName("description")
	private String description;
	
	class Example {
		@SerializedName("video_url")
		private String video_url;
		
		public String getVideo_url() {
			return video_url;
		}

		public String getDescription() {
			return description;
		}

		@SerializedName("description")
		private String description;
	}
	
	class Version {
		@SerializedName("video_url")
		private String video_url;
		
		public String getVideo_url() {
			return video_url;
		}

		public String getDescription() {
			return description;
		}

		@SerializedName("description")
		private String description;
	}
	
	class Word {
		@SerializedName("word")
		private String word;

		public String getWord() {
			return word;
		}
	}
	
	class Tag {
		@SerializedName("tag")
		private String tag;

		public String getTag() {
			return tag;
		}
	}
}

