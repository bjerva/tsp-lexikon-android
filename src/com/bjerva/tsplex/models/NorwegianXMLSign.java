package com.bjerva.tsplex.models;

public class NorwegianXMLSign {
	private String word;
	private String fileName;
	private String category;
	private Example[] examples;

	public NorwegianXMLSign(String word, String fileName, 
			String category, Example[] examples){
		this.word = word;
		this.fileName = fileName;
		this.category = category;
		this.examples = examples;
	}

	public String getWord() {
		return word;
	}

	public String getCategory() {
		return category;
	}

	public String getFileName() {
		return fileName;
	}

	public Example[] getExamples() {
		return examples;
	}
}


