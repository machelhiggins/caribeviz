package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class And extends LogicStatement implements Operator{
		
	public And(Collection <LogicStatement> statements){
		setStatements(statements);
	}
	
	public boolean evaluateOp(Double val, HashMap <String, Double>  varsValues) {
		if(debug)
			System.err.println("DEBUG: Evaluating AND OP ");
		if(statements != null && statements.size() > 0){			
			for(Iterator<LogicStatement>i = statements.iterator();i.hasNext();){
				LogicStatement t =i.next();
				if(t instanceof Operator){
					if(!((Operator) t).evaluateOp(val, varsValues)){
						if(debug)
							System.err.println("DEBUG: AND possesses FALSE OP");
						return false;
					}
					else{
						if(debug)
							System.err.println("DEBUG: AND OP possesses TRUE OP ");
					}
				}
			}
			if(debug)
				System.err.println("DEBUG: AND is TRUE ");
			return true;
		}		
		if(debug)
			System.err.println("DEBUG: AND OP does not have a statement !!!!!!");
		return false;			
	}

	public boolean getExpectTwoValuesToCompare() {
		// no op
		return false;
	}

	public void setExpectTwoValuesToCompare(boolean expectTwoValuesToComp) {
		// no op		
	}

}
