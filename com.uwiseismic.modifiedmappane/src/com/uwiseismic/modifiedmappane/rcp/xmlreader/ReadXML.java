package com.uwiseismic.modifiedmappane.rcp.xmlreader;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Read the editable attributes xml file to know which attributes of 
 * selected features can be edited by users.
 * 
 * Read the URL to post the committed edited features.
 * @author Sterling Ramroach (UWI)
 */
public class ReadXML {

	private ArrayList<String> attributes = new ArrayList<String>();

	public ReadXML(){}

	public ArrayList<String> getAttr(){
		return attributes;
	}

	public void read(){
		try {	
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane");
			URL fileURL = bundle.getEntry("resources/EditableAttributes.xml");
			URL stream = FileLocator.toFileURL(fileURL);

			InputStream i = stream.openStream();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(i);
			doc.getDocumentElement().normalize();
			NodeList urlList = doc.getElementsByTagName("url");
			String url = urlList.item(0).getTextContent();

			NodeList nList = doc.getElementsByTagName("attribs");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					attributes.add(eElement
							.getElementsByTagName("a")
							.item(0)
							.getTextContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
