package com.uwiseismic.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.dom4j.DocumentException;

import com.uwiseismic.ergo.buildingclassifier.BuildingProbParameterBuilder;
import com.uwiseismic.ergo.buildingclassifier.BuildingProbParameters;
import com.uwiseismic.ergo.buildingclassifier.NoProbabilityFunctionException;
import com.uwiseismic.ergo.buildingclassifier.ProbConstants;
import com.uwiseismic.ergo.buildingclassifier.StructureProbabilities;
import com.uwiseismic.ergo.buildingclassifier.logictree.IllegalLogicTreeOperationException;
import com.uwiseismic.ergo.buildingclassifier.logistic.IllegalLogisticFuncParameterException;
import com.uwiseismic.ergo.buildingclassifier.ratiofunc.IllegalRatioFuncParameterException;

public class TestLogicTree {

	public static void main(String[] args) {
		double gsqm = 200;
		double gsqmEDMean = 210;
		double std = 40;        
        HashMap <String, Double>gsqmVarsValues = new HashMap <String, Double>(); 
        gsqmVarsValues.put(ProbConstants.MEAN, new Double(gsqmEDMean));
        gsqmVarsValues.put(ProbConstants.SIGMA, new Double(std));
		gsqmVarsValues.put(ProbConstants.MEAN_PLUS_SIGMA, new Double(gsqmEDMean+std));
		gsqmVarsValues.put(ProbConstants.MEAN_MINUS_SIGMA, new Double(gsqmEDMean-std));
		File xmlFile = new File("C:\\temp\\portmore\\Portmore Building Classification Parameters no wood.xml");
        BuildingProbParameters probParams;
		try {
			probParams = BuildingProbParameterBuilder.create(xmlFile);
			StructureProbabilities sp = 
					probParams.getProbabilitiesFromMetric(ProbConstants.BA, gsqm, gsqmVarsValues, true);
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalLogicTreeOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalLogisticFuncParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalRatioFuncParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoProbabilityFunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}

}
