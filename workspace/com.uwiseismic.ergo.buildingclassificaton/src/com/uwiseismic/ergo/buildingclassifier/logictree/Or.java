package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Or extends LogicStatement implements Operator{
	
	public Or(Collection <LogicStatement> statements){
		setStatements(statements);
	}
	
	

	public boolean getExpectTwoValuesToCompare() {
		//no op
		return false;
	}


	public void setExpectTwoValuesToCompare(boolean expectTwoValuesToComp) {
		//no op
	}

	public boolean evaluateOp(Double val, HashMap <String, Double>  varsValues) {
		if(statements != null && statements.size() > 0){			
			for(Iterator<LogicStatement>i = statements.iterator();i.hasNext();){
				LogicStatement t =i.next();
				if(t instanceof Operator){
					if(((Operator) t).evaluateOp(val, varsValues)){
						return true;
					}
				}
			}
			return false;
		}		
		return false;
	}

}
