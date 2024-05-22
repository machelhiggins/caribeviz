package com.uwiseismic.ergo.buildingclassifier.logictree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import com.uwiseismic.ergo.buildingclassifier.ProbConstants;
import com.uwiseismic.ergo.buildingclassifier.StructureProbabilities;

public class LogicTreeBuilder {
	
	public static LogicStatement traverseLogicTree(Element logicTree, LogicStatement root) throws NumberFormatException, IllegalLogicTreeOperationException{
		Element ifStmt = logicTree.element("if");
		If newIf = new If();
		root.setNestedChild(newIf);
		newIf.setNestedParent(root);
			
		
		if(root instanceof Root)
			((Root)root).setStatement(newIf);
		String conditions = ifStmt.attributeValue("condition");		
		if(conditions.matches(".*"+ProbConstants.AND+".*")){
			String ops[] = conditions.split(ProbConstants.AND); 
			String rh = ops[0].trim();			
			OperatorImpl op1 = parseOperation(rh);					
			String lh = ops[1].trim();
			OperatorImpl op2 = parseOperation(lh);
			ArrayList <LogicStatement> arr = new ArrayList<LogicStatement>();
			arr.add(op1);
			arr.add(op2);
			And andOp = new And(arr);			
			newIf.setStatement(andOp);
		}
		else if(conditions.matches(".*"+ProbConstants.OR+".*")){
			String ops[] = conditions.split(ProbConstants.OR); 
			String rh = ops[0].trim();			
			OperatorImpl op1 = parseOperation(rh);					
			String lh = ops[1].trim();
			OperatorImpl op2 = parseOperation(lh);
			ArrayList <LogicStatement> arr = new ArrayList<LogicStatement>();
			arr.add(op1);
			arr.add(op2);
			Or orOp = new Or(arr);
			newIf.setStatement(orOp);
		}
		else{
			OperatorImpl op1 = parseOperation(conditions);						
			newIf.setStatement(op1);
		}
		

		if(ifStmt.element("structures_prob") != null){
			newIf.setStructProb(assignProbabilitlites(ifStmt.element("structures_prob")));			
		}

		//** test if there is an else to this if
		Element elseElement = ifStmt.element("else");
		if(elseElement !=  null ){
			Else elseStatement = new Else();
			newIf.setMyElse(elseStatement);			
			elseStatement.setNestedParent(newIf);
			//if there is a structiral_prob here then there's nothing else
			 if(elseElement.element("structures_prob") != null){					
				elseStatement.setStructProb(assignProbabilitlites(elseElement.element("structures_prob")));				
			 }
			 //otherwise its a nested if
			 else if(elseElement.element("if") != null){
				 traverseLogicTree(elseElement, elseStatement);				 
			 }
		}


		//** test if there is A (JUST ONE) nested if
		if(ifStmt.element("if") != null){
			traverseLogicTree(ifStmt, newIf);
		}		
		
		ArrayList <LogicStatement> arroot = new ArrayList<LogicStatement>();
		arroot.add(newIf);	
		root.setStatements(arroot);

		return newIf;
	}
	
	public static OperatorImpl parseOperation(String operation)throws IllegalLogicTreeOperationException{
		OperatorImpl op = null;
		int operator = -1;
		
		if(operation.matches(".*"+ProbConstants.LTEQ+".*")){				
			operator  = Operator.LTEQ;
			op = parseOperands(operation, ProbConstants.LTEQ,  operator);
		}
		else if(operation.matches(".*"+ProbConstants.GTEQ+".*")){				
			operator  = Operator.GTEQ;			
			op = parseOperands(operation, ProbConstants.GTEQ,  operator);
		}
		else if(operation.matches(".* "+ProbConstants.GT+" .*")){				
			operator  = Operator.GT;
			op = parseOperands(operation, ProbConstants.GT, operator);
		}
		else if(operation.matches(".* "+ProbConstants.LT+" .*")){				
			operator  = Operator.LT;
			op = parseOperands(operation, ProbConstants.LT,  operator);
		}
		else if(operation.matches(".* "+ProbConstants.EQ+" .*")){				
			operator  = Operator.EQ;
			op = parseOperands(operation, ProbConstants.EQ,  operator);
		}
		else {
			new IllegalLogicTreeOperationException("Could not understand the operation: "+operation);
		}
		return op;
	}
	
	public static OperatorImpl parseOperands(String operands, String operatorStr, int operator)throws NumberFormatException,IllegalLogicTreeOperationException{
		String subops[] = operands.split(operatorStr);
		if(subops.length != 2)
			throw new IllegalLogicTreeOperationException("Did not understand one or more operands of the following: "+operands);
		OperatorImpl op = null;
		if(operands.matches(".*"+ProbConstants.SIGMA+".*")){
			if(operands.matches(".*\\+.*")){
				op = new OperatorImpl(true, operator);
				op.addStatisticOperand(ProbConstants.MEAN_PLUS_SIGMA);
			}
			else if(operands.matches(".*-.*")){
				op = new OperatorImpl(true, operator);
				op.addStatisticOperand(ProbConstants.MEAN_MINUS_SIGMA);
			}
			else{
				op = new OperatorImpl(true, operator);
				op.addStatisticOperand(ProbConstants.SIGMA);
			}
			op.setExpectTwoValuesToCompare(true);
		}
		else if(operands.matches(".*"+ProbConstants.MEAN+".*")){
			op = new OperatorImpl(true, operator);
			op.setExpectTwoValuesToCompare(true);
			op.addStatisticOperand(ProbConstants.MEAN);
		}
		else if(operands.matches(".*"+ProbConstants.MODE+".*")){
			op = new OperatorImpl(true, operator);
			op.setExpectTwoValuesToCompare(true);
			op.addStatisticOperand(ProbConstants.MODE);
		}
		else if(operands.matches(".*"+ProbConstants.UPPER_NINETY_PERCENTILE+".*")){
			op = new OperatorImpl(true, operator);
			op.setExpectTwoValuesToCompare(true);
			op.addStatisticOperand(ProbConstants.UPPER_NINETY_PERCENTILE);
		}
		else if(operands.matches(".*"+ProbConstants.UPPER_EIGHTY_PERCENTILE+".*")){
			op = new OperatorImpl(true, operator);
			op.setExpectTwoValuesToCompare(true);
			op.addStatisticOperand(ProbConstants.UPPER_EIGHTY_PERCENTILE);			
		}
		else if(operands.matches(".*"+ProbConstants.UPPER_SEVENTY_PERCENTILE+".*")){
			op = new OperatorImpl(true, operator);
			op.setExpectTwoValuesToCompare(true);
			op.addStatisticOperand(ProbConstants.UPPER_SEVENTY_PERCENTILE);
		}
		//** its just a regular number
		else {
			Double num;
			try{
				num = Double.parseDouble(subops[0]);
			}catch(NumberFormatException npfe){				
				try{
					num = Double.parseDouble(subops[1]);
				}catch(NumberFormatException npfe2){
					throw npfe2;
				}				
			}
			op = new OperatorImpl(num, operator);
		}
		return op;
	}
		
	
	public static ArrayList<String> getAllStatistics(LogicStatement root){ 
		ArrayList<String> ops = root.getStatisticOperand();
		LogicStatement child = root.getNestedChild();
		if(child != null){
			ArrayList<String> childOps = getAllStatistics(child);
			for(Iterator <String> i = childOps.iterator(); i.hasNext();){
				String operand = i.next();
				if(!ops.contains(operand))
					ops.add(operand);
			}				
		}
		return ops;
			
	}
	public  static StructureProbabilities assignProbabilitlites(Element structProbs) throws NumberFormatException{
		StructureProbabilities structProb = new StructureProbabilities();
		List structs = structProbs.elements();
		for(Iterator i = structs.iterator(); i.hasNext();){
			Element t = (Element)i.next();
			structProb.setStructureProbability(t.attributeValue("type"),
					Double.parseDouble(t.attributeValue("probability")));
		}
		
		return structProb;
	}
}
