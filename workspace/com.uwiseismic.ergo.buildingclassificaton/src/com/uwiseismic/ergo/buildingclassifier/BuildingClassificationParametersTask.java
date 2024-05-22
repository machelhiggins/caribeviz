package com.uwiseismic.ergo.buildingclassifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.geotools.data.DataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.osgi.framework.Bundle;

import com.uwiseismic.ergo.datasets.XMLDataset;

import edu.illinois.ncsa.ergo.core.analysis.elf.tasks.AnalysisBaseTask;
import ncsa.tools.elf.core.exceptions.ScriptExecutionException;
import ncsa.tools.ogrescript.exceptions.EnvironmentAccessException;

public class BuildingClassificationParametersTask extends AnalysisBaseTask {

	//**output
	private Element classificationParams;
	//**input
	private final String RM = "Reinforced Masonry";
	private final String PC = "Precast";
	private double ratioWtoRM = 0;
	private double avgGSQM = 0;
	private String dominantStruct;
	private String resultName;


	protected void wrappedExecute(IProgressMonitor monitor) throws ScriptExecutionException {

        for(Iterator <Element> i = classificationParams.elementIterator("prob_function");i.hasNext();){
        	Element t = i.next();

        	//** set ratio of wood structures
        	if(t.attributeValue("name").equals(ProbConstants.WR_STR)){
        		Element z = t.element("ratio");
        		z.setAttributeValue("value", ""+(ratioWtoRM/100)); //** percentage to fraction
        	}
        	//** set avarage ground square meters
        	else if(t.attributeValue("name").equals(ProbConstants.BA_STR)){
        		Element z = t.element("logic_tree");
        		for(Iterator <Element> ii = z.elementIterator("if"); ii.hasNext();){
        			Element h = ii.next();
        			if(h.attributeValue("condition").equals("ba GT 290")){
        				h.setAttributeValue("condition", "ba GT "+avgGSQM);
        				break;
        			}
        		}
        	}
        }
        Element t = classificationParams.element("dominant_struct");
        if(dominantStruct.equals(RM))
        		t.setText("RM2");
        else if(dominantStruct.equals(PC))
    		t.setText("PC1");
        else
        	t.setText("RM2");
	}


	public double getRatioWtoRM() {
		return ratioWtoRM;
	}

	public void setRatioWtoRM(double ratioWtoRM) {
		this.ratioWtoRM = ratioWtoRM;
	}

	public double getAvgGSQM() {
		return avgGSQM;
	}

	public void setAvgGSQM(double avgGSQM) {
		this.avgGSQM = avgGSQM;
	}

	public String getDominantStruct() {
		return dominantStruct;
	}

	public void setDominantStruct(String dominantStruct) {
		this.dominantStruct = dominantStruct;
	}


	public String getResultName() {
		return resultName;
	}

	public void setResultName(String resultName) {
		this.resultName = resultName;
	}

	public Element getClassificationParams() {
		return classificationParams;
	}

	public void setClassificationParams(Element classificationParams) {
		this.classificationParams = classificationParams;
	}


}
