package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.HashMap;

import com.uwiseismic.ergo.buildingclassifier.StructureProbabilities;

public class Root  extends LogicStatement{
	
	private LogicStatement stmt;	
	public void setStatement(LogicStatement stmt){
		this.stmt = stmt;
	}
	
	public StructureProbabilities evaluate(Double val, HashMap <String, Double>  varsValues) {		
		//** if there is a nested if, evaluate it
		if(stmt != null && stmt instanceof If){
			return stmt.evaluate(val, varsValues);
			
		}
		return null;
	}
}
