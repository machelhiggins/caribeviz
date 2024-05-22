package com.uwiseismic.ergo.buildingclassifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.uwiseismic.ergo.buildingclassifier.logictree.LogicStatement;
import com.uwiseismic.ergo.buildingclassifier.logistic.LogisticFunction;
import com.uwiseismic.ergo.buildingclassifier.ratiofunc.RatioFunction;

public class BuildingProbParameters implements Serializable {

	private static final long serialVersionUID = -4609134013945909785L;

	
	private Object functions[] = new Object[ProbConstants.METRICS_STRINGS.length];
	private int functionTypes[] = new int[ProbConstants.METRICS_STRINGS.length];
	private ArrayList requiredStatistics[] = new ArrayList[ProbConstants.METRICS_STRINGS.length];
	
	private int mostPrevalentyStructureType = ProbConstants.RM2;
	private int RM2MinStoreys = 0;
	private int C1MinStoreys = 0;
	private int C2MinStoreys = 0;
	
	/**
	 * @param metric ONLY USE ProbConstants.RP, BA, BAN, BSR, BSRN, INZ
	 * @param function accepts LogicTree or LogisiticFunction
	 * @param functionType accepts ProbConstants.LOGISTIC_FUNCTION_TYPE, LOGIC_TREE_FUNCTION_TYPE
	 */
	public void setProbabiityFunction(int metric, Object function, int functionType){
		functions[metric] =  function;
		functionTypes[metric] = functionType;
		if(functionType == ProbConstants.LOGIC_TREE_FUNCTION_TYPE){
			requiredStatistics[metric] = ((LogicStatement)function).getStatisticOperand();
		}
	}
	

	/**
	 * @param metric ProbConstants.RP, BA, BAN, BSR, BSRN, INZ
	 * @param value
	 * @param varsValues
	 * @return
	 * @throws Exception
	 */
	public StructureProbabilities getProbabilitiesFromMetric(int metric, double value, HashMap <String, Double>varsValues, 
			boolean debugLogicTree)throws NoProbabilityFunctionException{
		StructureProbabilities prob;
		if(functionTypes[metric] == ProbConstants.LOGIC_TREE_FUNCTION_TYPE){
			LogicStatement lt = (LogicStatement)functions[metric];
			if(debugLogicTree)
				lt.enableDebug(debugLogicTree);
			prob = lt.evaluate(value, varsValues);
		}else if(functionTypes[metric] == ProbConstants.LOGISTIC_FUNCTION_TYPE){
			LogisticFunction lf = (LogisticFunction)functions[metric];
			prob = lf.evaluate(value);
		}
		else if(functionTypes[metric] == ProbConstants.RATIO_FUNCTION_TYPE){
			RatioFunction rf = (RatioFunction)functions[metric];
			prob = rf.evaluate(value);
		}
		else{
			throw new NoProbabilityFunctionException("No probability function was set for '"+ProbConstants.METRICS_DESCRIPTIONS[metric]+"'");
		}
		return prob;
	}	
	
	public ArrayList<String> getRequiredStatistics(int metric){
		if(functionTypes[metric] == ProbConstants.LOGIC_TREE_FUNCTION_TYPE){
			LogicStatement lt = (LogicStatement)functions[metric];
			return lt.getStatisticOperand();
		}else
			return null;
	}
	
	
	
	public int getMostPrevalentyStructureType() {
		return mostPrevalentyStructureType;
	}


	public void setMostPrevalentyStructureType(int mostPrevalentyStructureType) {
		this.mostPrevalentyStructureType = mostPrevalentyStructureType;
	}	

	public int getRM2MinStoreys() {
		return RM2MinStoreys;
	}


	public void setRM2MinStoreys(int rM2MinStoreys) {
		this.RM2MinStoreys = rM2MinStoreys;
	}


	public int getC1MinStoreys() {
		return C1MinStoreys;
	}


	public void setC1MinStoreys(int c1MinStoreys) {
		this.C1MinStoreys = c1MinStoreys;
	}


	public int getC2MinStoreys() {
		return C2MinStoreys;
	}


	public void setC2MinStoreys(int c22MinStoreys) {
		this.C2MinStoreys = c22MinStoreys;
	}


	/**
	 * DEBUG
	 */	
	public void debugDisableLogicTreeDebug(){
		//** TODO: REMOVE!!!!!!!!!!!
			LogicStatement lt = (LogicStatement)functions[ProbConstants.LOGIC_TREE_FUNCTION_TYPE];		
			lt.enableDebug(false);
	}

	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
