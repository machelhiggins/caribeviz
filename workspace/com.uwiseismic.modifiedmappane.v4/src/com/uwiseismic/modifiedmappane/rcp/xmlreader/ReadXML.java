package com.uwiseismic.modifiedmappane.rcp.xmlreader;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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

	private HashMap<String, String[]> attributes = new HashMap<String, String[]>();
	private HashMap<String, String> descriptions = new HashMap<String, String>();
	private HashMap<String, String> types = new HashMap<String, String>();
	
	private String url = "";
	
	public ReadXML(){}

	public HashMap<String, String[]> getAttr(){
		return attributes;
	}
	
	public HashMap<String, String> getDeacriptions(){
		return descriptions;
	}	
	
	public HashMap<String, String> getTypes() {
		return types;
	}

	public String getURL(){
		return url;
	}

	public void read(){
		try {	
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane.v4");
			URL fileURL = bundle.getEntry("resources/EditableAttributes.xml");
			URL stream = FileLocator.toFileURL(fileURL);

			InputStream in = stream.openStream();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(in);
			doc.getDocumentElement().normalize();
			NodeList urlList = doc.getElementsByTagName("url");
			
			Element t = (Element)urlList.item(0);		
			url = t.getAttribute("val");

			

			NodeList nList = doc.getElementsByTagName("attribs");
			String name;
			String desc;
			String type;
			String selT[];
			for (int n = 0; n < nList.getLength(); n++) {
				Node nNode = nList.item(n);				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element ele = (Element) nNode;
					name = ele.getAttribute("name");
					desc = ele.getAttribute("desc");
					type = ele.getAttribute("type");
					NodeList list = ele.getChildNodes();
					if(list.getLength() > 1){
						ArrayList<String> types = new ArrayList<String>();
						for(int i = 0; i < list.getLength(); i++){
							String na = list.item(i).getTextContent().trim();
							if(!na.equals("")){
								types.add(na);	
							}							
						}
						selT = new String[types.size()];
						types.toArray(selT);
					}else
						selT = null;
					
					attributes.put(name, selT);
					descriptions.put(name, desc);
					types.put(name, type);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
