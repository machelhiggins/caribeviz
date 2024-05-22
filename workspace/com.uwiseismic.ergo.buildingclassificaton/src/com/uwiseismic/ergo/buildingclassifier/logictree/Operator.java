package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.HashMap;

public interface Operator {

	public final int EQ = 0;
	public final int GT = 1;
	public final int LT = 2;
	public final int GTEQ = 3;
	public final int LTEQ = 4;
	
	public boolean getExpectTwoValuesToCompare();
	public void setExpectTwoValuesToCompare(boolean expectTwoValuesToComp);
	public boolean evaluateOp(Double val, HashMap <String, Double>  varsValues);
	
}
