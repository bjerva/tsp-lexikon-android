package com.bjerva.tsplex.norwegian;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.util.Log;

import com.bjerva.tsplex.models.Example;
import com.bjerva.tsplex.models.NorwegianXMLSign;


public class ParseSignXML {

	private static final String TAG = "XMLParse";

	@SuppressLint("NewApi")
	public static NorwegianXMLSign[] parseXML(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(inputStream));
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("leksem");

		Log.d(TAG, "tegnordbok: "+nodeList.getLength());

		int size = nodeList.getLength();

		NorwegianXMLSign[] norwegianSigns = new NorwegianXMLSign[size];

		String currentWord;
		String currentFileName;
		String currentCategory;
		Example[] currentExamples;
		String currentExampleFile;
		String currentExampleDescr;
		for(int i=0; i < size; i++){
			Element currElement = (Element) nodeList.item(i);
			currentWord = currElement.getAttribute("visningsord");
			currentFileName = currElement.getAttribute("filnavn");

			NodeList categories = currElement.getElementsByTagName("grupper");
			if(categories.getLength() > 0){
				currentCategory = ((Element) categories.item(0)).getAttribute("gruppe");
			} else {
				currentCategory = "";
			}

			NodeList examples = currElement.getElementsByTagName("kontekstform");
			currentExamples = new Example[examples.getLength()];
			for(int j=0; j < examples.getLength(); j++){
				currentExampleFile = ((Element) examples.item(j)).getAttribute("filnavn");
				currentExampleDescr = ((Element) examples.item(j)).getAttribute("kommentar");
				currentExamples[j] = new Example(currentExampleFile, currentExampleDescr);
			}

			norwegianSigns[i] = new NorwegianXMLSign(currentWord, currentFileName, currentCategory, currentExamples);
		}

		return norwegianSigns;
	}
}
