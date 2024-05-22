package com.uwiseismic.ergo.datasets;

import java.net.URL;

import org.dom4j.Element;

import edu.illinois.ncsa.ergo.gis.Dataset;
import edu.illinois.ncsa.ergo.gis.metadata.Metadata;
import edu.illinois.ncsa.ergo.gis.metadata.MetadataFactory;
import edu.illinois.ncsa.ergo.gis.types.DatasetId;

public class XMLDataset implements Dataset{

	
	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

	// INPUT
	protected DatasetId dataId;
	protected String friendlyName;
	protected String originalFriendlyName;
	protected String typeId;
	protected Metadata metadata;
	private Element xmlDoc;
	private URL xmlFileURL;
	
	public static final String TYPE_ID = "com.uwiseismic.ergo.buildingclassification.gisSchemas.anonymousXML.v1.0";	
	
	protected final static String FORMAT = "xml";
	/**
	 * 
	 * @return
	 */
	public DatasetId getDataId()
	{
		return dataId;
	}

	/**
	 * 
	 * @param dataId
	 */
	public void setDataId(DatasetId dataId)
	{
		this.dataId = dataId;
	}

	/**
	 * 
	 * @return
	 */
	public String getFriendlyName()
	{
		return friendlyName;
	}

	/**
	 * 
	 * @param friendlyName
	 */
	public void setFriendlyName(String friendlyName)
	{
		this.friendlyName = friendlyName;
		this.originalFriendlyName = friendlyName;
	}

	/**
	 * 
	 * @return
	 */
	public String getOriginalFriendlyName()
	{
		return originalFriendlyName;
	}

	/**
	 * 
	 * @param localFriendlyName
	 */
	public void renameLocally(String localFriendlyName)
	{
		friendlyName = localFriendlyName;
	}

	/**
	 * 
	 * @return
	 */
	public Metadata getMetadata()
	{
		return metadata;
	}

	/**
	 * 
	 * @param metadata
	 */
	public void setMetadata(Metadata metadata)
	{
		this.metadata = metadata;
	}

	/**
	 * 
	 * @return
	 */
	public String getTypeId()
	{
		return typeId;
	}
	
	
	/**
	 * TODO: change this 
	 * @param typeId
	 */
	public void setTypeId(String typeId)
	{
		this.typeId = typeId;

		// if there's no metadata yet, we'll pull in the default metadata when they set the type id
		if (getMetadata() == null) {
			try {
				Metadata standardMetadata = MetadataFactory.getStandardMetadataForSchema(typeId);
				if (standardMetadata != null) {
					setMetadata(standardMetadata);
				}
			} catch (Throwable t) {
				logger.warn("Failed to get metadata");
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String toString()
	{
		StringBuffer b = new StringBuffer();

		b.append("(").append(getFriendlyName()).append(", "); //$NON-NLS-1$ //$NON-NLS-2$
		b.append(getTypeId()).append(")"); //$NON-NLS-1$

		return b.toString();
	}

	/**
	 * TODO: change this
	 * @param arg0
	 */
	@Override
	public boolean equals(Object arg0)
	{
		if (super.equals(arg0))
			return true;

		if (getDataId() == null) {
			if (arg0 instanceof Dataset) {
				if (((Dataset) arg0).getDataId() == null)
					return super.equals(arg0);
				else
					return false;
			} else
				return false;
		}
		if (arg0 instanceof Dataset) {
			if (((Dataset) arg0).getFriendlyName().equals(getFriendlyName())) {
				return getDataId().equals(((Dataset) arg0).getDataId());
			}
			return false;
		}
		return false;
	}

	public String getDataFormat() {		
		return FORMAT;
	}
	
	public void setXMLDocument(Element xmlDoc){
		this.xmlDoc = xmlDoc;
	}
	
	public Element getXMLDocument(){
		return xmlDoc;
	}

	public URL getXmlFileURL() {
		return xmlFileURL;
	}

	public void setXmlFileURL(URL xmlFileURL) {
		this.xmlFileURL = xmlFileURL;
	}
	
	
}
