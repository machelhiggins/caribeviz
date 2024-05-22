package com.uwiseismic.ergo.buildingclassifier;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import edu.illinois.ncsa.ergo.core.analysis.elf.tasks.AnalysisBaseTask;
import ncsa.tools.elf.core.exceptions.ScriptExecutionException;
import ncsa.tools.ogrescript.exceptions.EnvironmentAccessException;

public class DefineXMLDataset extends AnalysisBaseTask
{

	public static final String CLASSIFICATION_PARAMS = "classification.params";

	@Override
	protected void wrappedExecute(IProgressMonitor monitor) throws ScriptExecutionException
	{
		// Here we should create an object that will contain the xml data (e.g. maybe Element)
		// We need to then add this object to the environment so it can be passed to the analysis task
		// This part could be as simple as Element element = new Element();
		
		try {
//			URL local = Platform.asLocalURL(bundle.getEntry(filename))
			Bundle bundle = Platform.getBundle("com.uwiseismic.ergo.buildingclassificaton");
			URL fileURL = Platform.asLocalURL(bundle.getEntry("probabilities/probability_thresholds.xml"));
			File xmlFile = new File(FileLocator.resolve(fileURL).toURI());;					
			System.out.println(xmlFile.getAbsolutePath());
			SAXReader reader = new SAXReader();       
		    Document document = reader.read(xmlFile);
		    Element classification = document.getRootElement();
			environment.addEntry(CLASSIFICATION_PARAMS, classification, true, true);
		} catch (EnvironmentAccessException e) {
			throw new ScriptExecutionException(e);
		} catch (URISyntaxException e) {
			throw new ScriptExecutionException(e);
		} catch (IOException e) {
			throw new ScriptExecutionException(e);
		} catch (DocumentException e) {
			throw new ScriptExecutionException(e);
		}
	}

}
