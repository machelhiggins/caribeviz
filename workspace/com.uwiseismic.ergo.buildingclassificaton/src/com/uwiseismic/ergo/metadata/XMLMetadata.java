package com.uwiseismic.ergo.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import edu.illinois.ncsa.ergo.gis.exceptions.BadUnitException;
import edu.illinois.ncsa.ergo.gis.metadata.BaseMetadata;
import edu.illinois.ncsa.ergo.gis.metadata.Metadata;

//public class XMLMetatdata implements Metadata{
public class XMLMetadata extends BaseMetadata{

	public final static String TAG_SELF = "xml-metadata"; //$NON-NLS-1$

	private Element xmlDoc;

	/**
	 * 
	 * @return
	 */
	public String getMetadataType(){
		return TAG_SELF;
	}

	public XMLMetadata(){
	}

	public XMLMetadata(Element xmlDoc){
		this.xmlDoc = xmlDoc;
	}
	
	public void setXMLDoc(Element xmlDoc){
		this.xmlDoc = xmlDoc;
	}
	
	public Element getXMLDoc(){
		return xmlDoc;
	}

	/**
	 * 
	 * @param newData
	 * @param overwrite
	 */
	public void applyMetadata(Metadata newData, boolean overwrite){
		if (newData instanceof XMLMetadata) {
			XMLMetadata newXML = (XMLMetadata) newData;
			this.setXMLDoc(newXML.getXMLDoc());
		}		
	}

	/**
	 * 
	 * @return
	 */
	public String toString(){
		return asElement().asXML();
	}

	/**
	 * 
	 * @return
	 */
	public Element asElement(){
		Element e = new DefaultElement(TAG_NAME);
		e.setName(TAG_SELF);
		e.add(xmlDoc);
		
		return e;
	}

	
	/**
	 * 
	 * @param element
	 */
	public void initializeFromElement(Element element){
		xmlDoc = element;

	}

	//*** implementations of Metadata
	
	/* 
	 * String <code>id</code> should encode the relationships of elements
	 * eg: <some_element>
	 * 		<child_element>
	 * 			<child_of_child childattrib="1">
	 * Should be encoded as some_elemnent
	 */
	public String getValue(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setValue(String id, String value) {
		// TODO Auto-generated method stub
		
	}

	
	public Map<String, String> getData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addChild(String id, Metadata child) {
		// TODO Auto-generated method stub
		
	}

	
	public Metadata getChild(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Map<String, Metadata> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
