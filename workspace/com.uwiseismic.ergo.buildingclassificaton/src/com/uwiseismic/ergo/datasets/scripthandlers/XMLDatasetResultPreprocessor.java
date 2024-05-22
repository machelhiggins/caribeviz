package com.uwiseismic.ergo.datasets.scripthandlers;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import edu.illinois.ncsa.ergo.core.analysis.extensionpoints.ResultPreprocessingHandler;
import edu.illinois.ncsa.ergo.core.analysis.types.OutputDescription;
import edu.illinois.ncsa.ergo.core.analysis.utils.AnalysisUtils;

public class XMLDatasetResultPreprocessor extends ResultPreprocessingHandler{

	/**
	 * 
	 * @param parent
	 * @param output
	 */
	@SuppressWarnings("nls")
	@Override
	public void createResultPreprocessingElements(Element parent, OutputDescription output){
		Element e = new DefaultElement("define-xml");
		parent.add(e); 
	}

}
