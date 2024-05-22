package com.uwiseismic.ergo.buildingclassifier;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import edu.illinois.ncsa.ergo.core.analysis.exceptions.HandlerException;
import edu.illinois.ncsa.ergo.core.analysis.extensionpoints.IterationHandler;
import edu.illinois.ncsa.ergo.core.analysis.types.AnalysisNode;
import edu.illinois.ncsa.ergo.core.analysis.types.ParameterDescription;
import edu.illinois.ncsa.ergo.core.analysis.utils.AnalysisUtils;
import ncsa.tools.common.types.Property;

public class BuildingClassificationParameterIterator extends IterationHandler {

	@Override
	public void wrappedCreateIterationElements(Element parent, AnalysisNode node) throws HandlerException {
		
		String taskTag = AnalysisUtils.getTagFromId(node.getAnalysisDescription().getId());
		Element taskElement = new DefaultElement(taskTag);

		taskElement.addAttribute("analysis-id", node.getUniqueId()); //$NON-NLS-1$

		Property iteratingDataset = node.getAnalysisDescription().getProperty("iteratingDatasetKey"); //$NON-NLS-1$
		String key = iteratingDataset != null ? iteratingDataset.getValue() : "invalid"; //$NON-NLS-1$

		for (ParameterDescription d : node.getAnalysisDescription().getParameters()) {
			// Don't add if this is the iterating type...as it will be passed via a single feature
			// Also, resultName is a parameter but not handled the same way...
			if (!d.getKey().equals(key) && (!d.getKey().endsWith(ParameterDescription.RESULT_NAME))) {
				// TODO if the historical data becomes an input dataset we need this to handle this case
				if (!d.isMap()) {
					AnalysisUtils.addParam(taskElement, d.getKey());
				}
			}
		}
		
		// Here you need to variableize the object that the dataset will store
		taskElement.addAttribute("classificationParams", AnalysisUtils.variableize(null, DefineXMLDataset.CLASSIFICATION_PARAMS)); //$NON-NLS-1$

		parent.add(taskElement);
		
		
	}

}
