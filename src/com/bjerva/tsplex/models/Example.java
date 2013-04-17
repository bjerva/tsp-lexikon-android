package com.bjerva.tsplex.models;

public class Example {
	private String fileName;
	private String description;

	public Example(String fileName, String description){
		this.fileName = fileName;
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public String getDescription() {
		return description;
	}
}