package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.uwiseismic.ergo.buildingclassifier.StructureProbabilities;

public class Else extends LogicStatement{
	

	public StructureProbabilities evaluate(Double val, HashMap <String, Double>  varsValues) {
		//** if there is a nested if, evaluate it
		 if(this.getNestedChild() != null){
			 if(debug)
				 System.err.println("DEBUG: ELSE with children. Evaluating children.");
			 return this.getNestedChild().evaluate(val, varsValues);
		 }
		//** otherwise return this Else's structural probabilities
		else{
			if(debug)
				System.err.println("DEBUG: ELSE without children. RETURN Struct Prob"+this.getStructProb());
			return this.getStructProb();
		}						
	}
	
}
