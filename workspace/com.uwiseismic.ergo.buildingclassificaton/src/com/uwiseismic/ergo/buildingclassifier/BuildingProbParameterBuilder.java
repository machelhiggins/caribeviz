package com.uwiseismic.ergo.buildingclassifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.uwiseismic.ergo.buildingclassifier.logictree.IllegalLogicTreeOperationException;
import com.uwiseismic.ergo.buildingclassifier.logictree.LogicTreeBuilder;
import com.uwiseismic.ergo.buildingclassifier.logictree.Root;
import com.uwiseismic.ergo.buildingclassifier.logistic.IllegalLogisticFuncParameterException;
import com.uwiseismic.ergo.buildingclassifier.logistic.LogisticFunction;
import com.uwiseismic.ergo.buildingclassifier.logistic.LogisticFunctionBuilder;
import com.uwiseismic.ergo.buildingclassifier.ratiofunc.IllegalRatioFuncParameterException;
import com.uwiseismic.ergo.buildingclassifier.ratiofunc.RatioFunction;
import com.uwiseismic.ergo.buildingclassifier.ratiofunc.RatioFunctionBuilder;

public class BuildingProbParameterBuilder{

	public static BuildingProbParameters create(File xmlFile) throws 
	IOException, NullPointerException, IllegalLogicTreeOperationException, 
	IllegalLogisticFuncParameterException, DocumentException, IllegalRatioFuncParameterException{
				
        SAXReader reader = new SAXReader();       
        Document document = reader.read(xmlFile);				
		
		Element root = document.getRootElement();
		
		return create(root);
	}
	
	public static BuildingProbParameters create(Element root) throws 
		IOException, NullPointerException, IllegalLogicTreeOperationException, 
		IllegalLogisticFuncParameterException, DocumentException, IllegalRatioFuncParameterException{
		
		BuildingProbParameters probParams = new BuildingProbParameters();
		
		//prob functions
		for(Iterator i = root.elementIterator("prob_function");i.hasNext();){
			Element t = (Element)i.next();
			String metricName = ProbConstants.verifyMetricType(t.attributeValue("name"));
			if(metricName == null)
				throw new NullPointerException("Unknown probability function in XML file.");					
			String funcType = ProbConstants.verifyFunctionType(t.attributeValue("function_type"));
			if(funcType == null)
				throw new NullPointerException("Unknown function type in XML file.");
			//System.out.println("Name :: "+metricName+"\tFunc Type :: "+funcType);					
			if(funcType.equals(ProbConstants.LOGIC_TREE)){			
				Root rootStatement = new Root();			
				LogicTreeBuilder.traverseLogicTree(t.element("logic_tree"), rootStatement);
				probParams.setProbabiityFunction(ProbConstants.getMetricConstant(metricName), 
						rootStatement, ProbConstants.LOGIC_TREE_FUNCTION_TYPE);
			}
			else if(funcType.equals(ProbConstants.LOGISTIC_FUNCTION)){
				LogisticFunction lf = LogisticFunctionBuilder.createLogisticFunctions(t.element("logistic"));
				probParams.setProbabiityFunction(ProbConstants.getMetricConstant(metricName), 
						lf, ProbConstants.LOGISTIC_FUNCTION_TYPE);
			}
			else if(funcType.equals(ProbConstants.RATIO_FUNCTION)){
				RatioFunction lf = RatioFunctionBuilder.createRatioFunctions(t.element("ratio"));
				probParams.setProbabiityFunction(ProbConstants.getMetricConstant(metricName), 
						lf, ProbConstants.RATIO_FUNCTION_TYPE);
			}else
				System.err.println("Didn't understand function type for metric "+metricName);	
			
		}
		//** get dominant structures
		Element t = root.element("dominant_struct");
		probParams.setMostPrevalentyStructureType(ProbConstants.getStructureType(t.getText()));
		//** get minimum floors for RM2,C1, & C2
		t = root.element("RM2_min_storeys");
		if(t != null)
			probParams.setRM2MinStoreys(Integer.parseInt(t.getText()));
		t = root.element("C1_min_storeys");
		if(t != null)
			probParams.setC1MinStoreys(Integer.parseInt(t.getText()));
		t = root.element("C2_min_storeys");
		if(t != null)
			probParams.setC2MinStoreys(Integer.parseInt(t.getText()));
		return probParams;
	}
}
