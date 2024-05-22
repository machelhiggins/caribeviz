package com.uwiseismic.ergo.datasets.scripthandlers;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.uwiseismic.ergo.buildingclassifier.DefineXMLDataset;

import edu.illinois.ncsa.ergo.core.analysis.exceptions.HandlerException;
import edu.illinois.ncsa.ergo.core.analysis.extensionpoints.ResultCreationHandler;
import edu.illinois.ncsa.ergo.core.analysis.ogrescript.tasks.core.DefineFeatureType;
import edu.illinois.ncsa.ergo.core.analysis.types.OutputDescription;
import edu.illinois.ncsa.ergo.core.analysis.types.ParameterDescription;
import edu.illinois.ncsa.ergo.core.analysis.types.ParameterNode;
import edu.illinois.ncsa.ergo.core.analysis.utils.AnalysisUtils;
import ncsa.tools.common.types.Property;

public class BCParametersResultCreationHandler implements ResultCreationHandler{
	/**
	 * 
	 * @param parent
	 * @param outputConn
	 * @param output
	 */
	public void createResultElements(Element parent, ParameterNode outputConn, OutputDescription output) throws HandlerException
	{
		Element r = createResult(outputConn, output);
		parent.add(r);
	}

	/**
	 * 
	 * @param outputConn
	 * @param output
	 * @return
	 */
	private Element createResult(ParameterNode outputConn, OutputDescription output)
	{
		if (!output.getPhylum().equals(ParameterDescription.DATASET)) {
			return null;
		}

		String outputKey = null;
		if (outputConn == null) {
			outputKey = output.getKey();
		} else {
			outputKey = outputConn.getDescription().getKey();
		}

		Property schema = output.getPropertyByType(DefineFeatureType.OUTPUT_PROP_TYPE_SCHEMA);

		String schemaValue = schema != null ? schema.getValue() : null;

		Element e = new DefaultElement("create-xml-dataset-result");

		e.addAttribute("result-key", "output." + outputKey);

		e.addAttribute("result-name", "${param." + outputKey + ".resultName}");		
		e.addAttribute("classificationParams", AnalysisUtils.variableize(null, DefineXMLDataset.CLASSIFICATION_PARAMS)); //$NON-NLS-1$	
		
		return e;
	
	}
}
