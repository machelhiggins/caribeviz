package com.uwiseismic.ergo.analysis.efacility;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import edu.illinois.ncsa.ergo.core.analysis.exceptions.HandlerException;
import edu.illinois.ncsa.ergo.core.analysis.extensionpoints.IterationHandler;
import edu.illinois.ncsa.ergo.core.analysis.types.AnalysisNode;
import edu.illinois.ncsa.ergo.core.analysis.utils.AnalysisUtils;

public class ShelterFacilityOccupancyIterator extends IterationHandler {
	
	@Override
	public void wrappedCreateIterationElements(Element parent, AnalysisNode node) throws HandlerException {
		// This must match the tag for the analysis and task
		Element e = new DefaultElement("shelterFacilitiesOccupancy"); 
		// Name of the result
		e.addAttribute("result-Type", AnalysisUtils.variableizeAsConstant("result.type", "shelterFacilitiesOccupancy"));
		// Input -must match "key" inside analysis description
		e.addAttribute("pop-dislocated",AnalysisUtils.variableizeAsConstant(null,"popDislocated"));
		e.addAttribute("osm-road-network",AnalysisUtils.variableizeAsConstant(null,"osmRoadNetwork"));
		
		
		
		// Output -bldgresultmust match to an output key in analysis description
		e.addAttribute("shelter-facilities-occupancy",AnalysisUtils.variableizeAsConstant("collection","shelterFacilitiesOccupancy"));
		e.addAttribute("analysis-id",node.getUniqueId());
		parent.add(e);
		
	}
}
