package com.bjerva.tsplex;

/*
 * Copyright (C) 2013, Johannes Bjerva
 *
 * Permission is hereby granted, free of charge, 
 * to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GsonSign{
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
	
	public class Example {
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
	
	public class Version {
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
	
	public class Word {
		@SerializedName("word")
		private String word;

		public String getWord() {
			return word;
		}
	}
	
	public class Tag {
		@SerializedName("tag")
		private String tag;

		public String getTag() {
			return tag;
		}
	}
}

