package com.bjerva.tegnordbok.models;

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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GsonSign{
	
	public GsonSign(String word, String fileName, 
			String category, String[] ex_decs, String[] ex_urls, int id){
		this.words = new ArrayList<Word>(1);
		this.words.add(new Word(word));
		this.video_url = fileName;
		this.examples = new ArrayList<Example>(ex_decs.length);
		for(int i=0; i<ex_decs.length; i++){
			this.examples.add(new Example(ex_urls[i], ex_decs[i]));
		}
		this.tags = new ArrayList<Tag>(1);
		this.tags.add(new Tag(category));
		this.versions = new ArrayList<Version>();
		this.unusual = false;
		this.id = id;
	}
	
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
	
	public int getId() {
		return id;
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
	
	@SerializedName("id")
	private int id;
	
	public class Example {
		@SerializedName("video_url")
		private String video_url;

		@SerializedName("description")
		private String description;
		
		public Example(String url, String desc){
			this.video_url = url;
			this.description = desc;
		}
		
		public String getVideo_url() {
			return video_url;
		}

		public String getDescription() {
			return description;
		}
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
		
		public Word(String word){
			this.word = word;
		}

		public String getWord() {
			return word;
		}
	}
	
	public class Tag {
		@SerializedName("tag")
		private String tag;
		
		public Tag(String tag){
			this.tag = tag;
		}
		public String getTag() {
			return tag;
		}
	}
}

