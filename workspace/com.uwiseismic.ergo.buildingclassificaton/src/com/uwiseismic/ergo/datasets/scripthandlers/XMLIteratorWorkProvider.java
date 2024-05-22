package com.uwiseismic.ergo.datasets.scripthandlers;

import com.uwiseismic.ergo.datasets.XMLDataset;

import ncsa.tools.ogrescript.ITask;
import ncsa.tools.ogrescript.IWorkIncrementProvider;

public class XMLIteratorWorkProvider implements IWorkIncrementProvider{
	//private String classificationParams;
	private XMLDataset classificationParams;
	private String resultType; 
	private String resultKey;
	
	
	
	/**
	 * @param task
	 * @return
	 */
	public int getWorkIncrement(ITask task)
	{
//		System.err.println("Getting work increment");
		return 1;
//		try {
//			return featureSource.getFeatures().size();
//		} catch (IOException e) {
//			return 0;
//		}
	}
	public XMLDataset getClassificationParams() {
		return classificationParams;
	}
	
	public void setClassificationParams(XMLDataset classificationParams) {
		this.classificationParams = classificationParams;
	}
	
	public String getResultType() {
		return resultType;
	}
	
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getResultKey() {
		return resultKey;
	}
	public void setResultKey(String resultKey) {
		this.resultKey = resultKey;
	}

//	public String getClassificationParams() {
//		return classificationParams;
//	}
//
//	public void setClassificationParams(String classificationParams) {
//		this.classificationParams = classificationParams;
//	}

	
	

	

}
