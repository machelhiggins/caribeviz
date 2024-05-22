package com.uwiseismic.ergo.buildingclassifier.ratiofunc;

import org.dom4j.Element;

public class RatioFunctionBuilder {
	
	public static RatioFunction createRatioFunctions(Element rfElement)throws IllegalRatioFuncParameterException{
		RatioFunction rf = null;
		try {
			double value = Double.parseDouble(rfElement.attributeValue("value"));
			rf = new RatioFunction(value);
		}catch(NumberFormatException nex){
			throw new IllegalRatioFuncParameterException("There was an error parsing ratio function paramter");
		}		
		return rf;
	}
}
