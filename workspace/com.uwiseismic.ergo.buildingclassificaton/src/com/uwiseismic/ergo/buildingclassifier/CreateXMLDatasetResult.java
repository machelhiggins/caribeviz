package com.uwiseismic.ergo.buildingclassifier;

import org.dom4j.Element;
import org.eclipse.core.runtime.IProgressMonitor;

import com.uwiseismic.ergo.datasets.XMLDataset;

import edu.illinois.ncsa.ergo.core.analysis.elf.tasks.AnalysisBaseTask;
import edu.illinois.ncsa.ergo.gis.types.DatasetId;
import ncsa.tools.elf.core.exceptions.ScriptExecutionException;
import ncsa.tools.ogrescript.ITask;
import ncsa.tools.ogrescript.exceptions.EnvironmentAccessException;

//public class CreateXMLDatasetResult extends AnalysisBaseTask implements IWorkIncrementProvider
public class CreateXMLDatasetResult extends AnalysisBaseTask{
	
	private String resultKey;
	private String typeName;
	private String typeID;
	private String resultName;
	
	// You need to add another Object/setter/getter that will pass in your xml data to be stored
	private Element classificationParams;
	//Output
	private XMLDataset outputDataset;
	
	@Override
	protected void wrappedExecute(IProgressMonitor monitor) throws ScriptExecutionException
	{
//		System.err.println("result name = "+resultName);
//		System.err.println(classificationParams.getText() );
//		System.err.println("type id "+typeID);
		// Here is where you should create the XMLDataset which will get stored by the factory		
		
		//** MH this is what I did.
		outputDataset = new XMLDataset();
		outputDataset.setFriendlyName(resultName != null ? resultName : "Building Classification Parameters");
		outputDataset.setXMLDocument(classificationParams);
		outputDataset.setDataId(new DatasetId());
		//String key = DatasetId.generateSemiUniqueKey("Building Classification Parameters");
		String key = DatasetId.generateSemiUniqueKey(outputDataset.getFriendlyName());
		outputDataset.getDataId().setKey(key);
		
//		outputDataset.setTypeId(typeID);
		outputDataset.setTypeId(XMLDataset.TYPE_ID);
				
		try {
			environment.addEntry(resultKey, outputDataset, true, false);
		} catch (EnvironmentAccessException e) {
			// TODO better error reporting 
			e.printStackTrace();
		}
	}

	public String getResultKey()
	{
		return resultKey;
	}

	public void setResultKey(String resultKey)
	{
		this.resultKey = resultKey;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public String getResultName()
	{
		return resultName;
	}

	public void setResultName(String resultName)
	{
		this.resultName = resultName;
	}

	public Element getClassificationParams() {
		return classificationParams;
	}

	public void setClassificationParams(Element classificationParams) {
		this.classificationParams = classificationParams;
	}

	public XMLDataset getOutputDataset() {
		return outputDataset;
	}

	public void setOutputDataset(XMLDataset outputDataset) {
		this.outputDataset = outputDataset;
	}


	public int getWorkIncrement(ITask task){
//		System.err.println("Getting work increment");
		return 1;
//		try {
//			return featureSource.getFeatures().size();
//		} catch (IOException e) {
//			return 0;
	}

	public String getTypeID() {
		return typeID;
	}

	public void setTypeID(String typeID) {
		this.typeID = typeID;
	}


	
}
