package com.uwiseismic.ergo.buildingclassifier;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
//import org.dom4j.tree

import edu.illinois.ncsa.ergo.core.analysis.exceptions.HandlerException;
//import edu.illinois.ncsa.ergo.core.analysis.extensionpoints.Element;
import edu.illinois.ncsa.ergo.core.analysis.extensionpoints.IterationHandler;
import edu.illinois.ncsa.ergo.core.analysis.types.AnalysisNode;
import edu.illinois.ncsa.ergo.core.analysis.utils.AnalysisUtils;

public class BuildingClassificationIterator extends IterationHandler {

	@Override
	public void wrappedCreateIterationElements(Element parent, AnalysisNode node) throws HandlerException {
		
		// This must match the tag for the analysis and task
		Element e = new DefaultElement("buildingClassification"); 
		// Name of the result                                                             
		e.addAttribute("result-Type", AnalysisUtils.variableizeAsConstant("result.type", "buildingClassification"));
		// Input -must match "key" inside analysis description
		e.addAttribute("uwi-building-classification",AnalysisUtils.variableizeAsConstant(null,"uwiBuildingClassification"));
		e.addAttribute("osm-road-network",AnalysisUtils.variableizeAsConstant(null,"osmRoadNetwork"));
		e.addAttribute("uwi-enumeration-district",AnalysisUtils.variableizeAsConstant(null,"uwiEnumerationDistrict"));
		e.addAttribute("uwi-building-classification-params",AnalysisUtils.variableizeAsConstant(null,"uwiBuildingClassificationParams"));
		
		// Output -bldgresult must match to an output key in analysis description
		e.addAttribute("result-buildingClassification",AnalysisUtils.variableizeAsConstant("collection","buildingClassification"));
		e.addAttribute("analysis-id",node.getUniqueId());
//		System.err.println(e);
		parent.add(e);
		
	}
	
}
