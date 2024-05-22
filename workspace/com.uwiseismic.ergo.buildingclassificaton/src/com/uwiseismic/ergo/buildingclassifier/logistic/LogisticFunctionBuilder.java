package com.uwiseismic.ergo.buildingclassifier.logistic;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

//import org.jdom.DataConversionException;
//import org.jdom.Element;

import com.uwiseismic.ergo.buildingclassifier.logictree.IllegalLogicTreeOperationException;

public class LogisticFunctionBuilder {
	
	public static LogisticFunction createLogisticFunctions(Element logisticElement)throws IllegalLogisticFuncParameterException{
		LogisticFunction l = new LogisticFunction();
		List structTypes = logisticElement.element("structures_prob")
				.elements("structure_type");		
		for(Iterator <Element> i = structTypes.iterator(); i.hasNext();){
			Element t = i.next();
			try {
				l.setStructureProbability(t.attributeValue("type"),
						Double.parseDouble(t.attributeValue("x0")),
						Double.parseDouble(t.attributeValue("k")),
						Double.parseDouble(t.attributeValue("C")),
								Boolean.parseBoolean(t.attributeValue("flip")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new IllegalLogisticFuncParameterException("There was an error parsing logistic function parameter.");				
			}
		}
		return l;
	}
	
}
