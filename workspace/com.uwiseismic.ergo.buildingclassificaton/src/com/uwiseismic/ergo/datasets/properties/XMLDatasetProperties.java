package com.uwiseismic.ergo.datasets.properties;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentException;

import edu.illinois.ncsa.ergo.gis.datasets.properties.MappedDatasetProperties;
import ncsa.tools.common.util.XmlUtils;

public class XMLDatasetProperties extends MappedDatasetProperties{

	@SuppressWarnings("unused")
	//private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
	public final static String TAG_ATTRIBS = "attributes"; //$NON-NLS-1$
	public final static String TAG_SELF = "xml-dataset-properties"; //$NON-NLS-1$
	private Map<String, String> attribs = new HashMap<String, String>();
	/**
	 * 
	 */
	public XMLDatasetProperties()
	{
		super();
	}

	/**
	 * 
	 * @param stream
	 * @throws DocumentException
	 */
	public XMLDatasetProperties(InputStream stream) throws DocumentException{
		initializeFromElement(XmlUtils.dom4jElementFromStream(stream));
	}

	/**
	 * 
	 * @param key
	 */
	public String getAttrib(String key)
	{
		return attribs.get(key);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setAttrib(String key, String value)
	{
		attribs.put(key, value);
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getAttribs()
	{
		return attribs;
	}

	
	public void setFilename(String s){
		s = s.replace('(','-');
		s = s.replace(')','-');
		super.setFilename(s);
	}

	
}
