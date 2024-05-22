package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.uwiseismic.ergo.buildingclassifier.ProbConstants;
import com.uwiseismic.ergo.buildingclassifier.StructureProbabilities;

public abstract class LogicStatement {
	

	protected Comparable val1;
	protected int operator; 
	protected Collection <LogicStatement> statements;
	protected LogicStatement childNested;
	protected LogicStatement parentNested;
	protected ArrayList <String>statisticOperands = new ArrayList<String>();
	
	protected StructureProbabilities structProb;
	
	
	public ArrayList <String> getStatisticOperand() {
		return statisticOperands;
	}

	public void addStatisticOperand(String statisticOperand){
		if(!statisticOperands.contains(statisticOperand))
			statisticOperands.add(statisticOperand);		
		if(parentNested != null)
			parentNested.addStatisticOperand(statisticOperand);		
	}


	public LogicStatement getNestedParent() {		
		return parentNested;
	}
	
	public void setNestedParent(LogicStatement parentNested) {		
		this.parentNested = parentNested;
	}	 

	public LogicStatement getNestedChild() {
		return childNested;
	}
	
	public void setNestedChild(LogicStatement childNested) {
		this.childNested = childNested;
	}
		
	/**
	 * 
	 * The second parameter, <code>varsValues</code> is meant to accommodate statistical values that are calculated like mean, mode etc. 
	 * Keys are those found in ProbConstants
	 * 
	 * If we're just comparing against the specific metric, set to null. It will be ignored by LogicStatements that do not require 
	 * this variable.
	 * 
	 * @param val
	 * @param val2
	 * @return
	 */
	public StructureProbabilities evaluate(Double val, HashMap <String, Double>  varsValues) {
		return null;
	}		
	
	public void setVal1(Comparable val1){
		this.val1 = val1;
	}
	
	public Comparable getVal1(){
		return val1;
	}
	
	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	public Collection<LogicStatement> getStatements() {
		return statements;
	}

	public void setStatements(Collection<LogicStatement> statements) {
		this.statements = statements;
		if(statements != null){
			for(Iterator <LogicStatement> i = statements.iterator();i.hasNext();){
				LogicStatement t = i.next();		
				ArrayList <String> theirs = t.getStatisticOperand();
				if(theirs != null){					
					for(Iterator <String>z = theirs.iterator();z.hasNext();)
						this.addStatisticOperand(z.next());
				}
			}
		}		
	}

	public StructureProbabilities getStructProb() {
		return structProb;
	}

	public void setStructProb(StructureProbabilities structProb) {
		this.structProb = structProb;
	}

	protected boolean debug = false;
	public void enableDebug(boolean debug){
		this.debug = debug;
		if(this.childNested != null)
			this.childNested.enableDebug(debug);
	}
	
}
