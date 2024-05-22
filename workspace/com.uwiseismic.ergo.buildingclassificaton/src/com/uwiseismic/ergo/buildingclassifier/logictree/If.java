package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.uwiseismic.ergo.buildingclassifier.StructureProbabilities;

public class If extends LogicStatement{

	private Else myElse = null;
	private LogicStatement stmt;
	
	public void setStatement(LogicStatement stmt){
		this.stmt = stmt;
		if(stmt.getStatisticOperand() != null){
			ArrayList <String> theirs = stmt.getStatisticOperand();
			for(Iterator <String>z = theirs.iterator();z.hasNext();)
				this.addStatisticOperand(z.next());
		}
	}
	
	public StructureProbabilities evaluate(Double val, HashMap <String, Double>  varsValues) {	
		if(debug)
			System.err.println("DEBUG: Evaluating IF");
		if(stmt instanceof Operator){
			Operator op = (Operator)stmt;
			if(debug)
				((LogicStatement)stmt).enableDebug(debug);

			if(op.evaluateOp(val, varsValues)){
				if(debug)
					System.err.println("DEBUG: Evaluated IF operation returned true. Returning "+this.getStructProb());
				if(this.getStructProb() == null)
					return this.getNestedChild().evaluate(val, varsValues);
				else
					return this.getStructProb();
			}
			//** ELSE
			if(myElse != null){
				if(debug){
					System.err.println("DEBUG: Evaluated IF whose operator returned FALSE, now going to ELSE ");
					myElse.enableDebug(debug);
				}
				return myElse.evaluate(val, varsValues);
			}
		}
		
		return null;
	}

	public Else getMyElse() {
		return myElse;
	}

	public void setMyElse(Else myElse) {
		this.myElse = myElse;
	}
	
	
}
