package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.HashMap;

public class OperatorImpl  extends LogicStatement implements Operator{

	private boolean expectTwoValuesToComp;
	
	public OperatorImpl(Comparable val1, int operator){
		setVal1(val1);
		setOperator(operator);
	}

	public OperatorImpl(boolean expectTwoValuesToComp, int operator){
		this.expectTwoValuesToComp = expectTwoValuesToComp;	
		setOperator(operator);
	}

	public boolean evaluateOp(Double value, HashMap <String, Double>  varsValues) {
		if(debug)
			System.err.println("DEBUG: Evaluating OP:: expecting two values ["+expectTwoValuesToComp+"]? val = "+value.toString());
		if(expectTwoValuesToComp){
			Double val2 = varsValues.get(this.getStatisticOperand().get(0));
			if(debug)
				System.err.println("DEBUG: OP Required Stats"+this.getStatisticOperand().get(0));
			int r = value.compareTo(val2);
			if(debug)
				System.err.println("DEBUG: OP Expecting two values:: "+value+" <> "+val2+" r="+r);
			
			if(r ==0 && (operator == EQ ||
					operator == GTEQ ||
					operator == LTEQ )){
				return true;
			}
			else if ((operator == GT || operator == GTEQ) && r > 0){
				return true;
			}		
			else if ((operator == LT || operator == LTEQ) && r < 0){
				return true;
			}
			return false;
		}
		else{
			if(val1 != null){			
				int r = ((Comparable)value).compareTo(val1);
				if(debug)
					System.err.println("DEBUG: OP NOT Expecting 2 values:: "+value+" <> "+val1+" r="+r+" operator = "+operator);
				if((operator == EQ ||
						operator == GTEQ ||
						operator == LTEQ )&& r ==0){
					return true;
				}
				else if ((operator == GTEQ || operator == GT) && r > 0){
					return true;
				}		
				else if ((operator == LTEQ || operator == LT) && r < 0){
					return true;
				}
			}
			return false;
		}
	}



	public boolean getExpectTwoValuesToCompare() {
		return expectTwoValuesToComp;
	}

	public void setExpectTwoValuesToCompare(boolean expectTwoValuesToComp) {
		this.expectTwoValuesToComp = expectTwoValuesToComp;		
	}	
}
