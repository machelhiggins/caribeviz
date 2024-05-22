package com.uwiseismic.ergo.buildingclassifier.logistic;

import com.uwiseismic.ergo.buildingclassifier.ProbConstants;
import com.uwiseismic.ergo.buildingclassifier.StructureProbabilities;

public class LogisticFunction {
	
	private LogisticFuncParams rm;;
	private LogisticFuncParams rm1;
	private LogisticFuncParams rm2;
	private LogisticFuncParams c1;
	private LogisticFuncParams c2;
	private LogisticFuncParams w1;
	private LogisticFuncParams s1;
	private LogisticFuncParams pc1;	
	
	public void setStructureProbability(String name, double k, double x0, double C, boolean flip){
		if(name.matches("rm1"))
			rm1 = new LogisticFuncParams(k, x0, C, flip);
		else if(name.matches("rm2"))
			rm2 = new LogisticFuncParams(k, x0, C, flip);
		else if(name.matches("rm"))
			rm = new LogisticFuncParams(k, x0, C, flip);
		else if(name.matches("c1"))
			c1 = new LogisticFuncParams(k, x0, C, flip);
		else if(name.matches("c2"))
			c2 = new LogisticFuncParams(k, x0, C, flip);
		else if(name.matches("pc1"))
			pc1 = new LogisticFuncParams(k, x0, C, flip);
		else if(name.matches("w1"))
			w1 = new LogisticFuncParams(k, x0, C, flip);
		else if(name.matches("s1"))
			s1 = new LogisticFuncParams(k, x0, C, flip);
	}
	
	public void setLogicFunctionParameters(int struct, double k, double x0, double C, boolean flip){
		if(struct == ProbConstants.RM)
			rm = new LogisticFuncParams(k, x0, C, flip);
		else if(struct == ProbConstants.RM1)
			rm1 = new LogisticFuncParams(k, x0, C, flip);
		else if(struct == ProbConstants.RM2)
			rm2 = new LogisticFuncParams(k, x0, C, flip);
		else if(struct == ProbConstants.C1)
			c1 = new LogisticFuncParams(k, x0, C, flip);
		else if(struct == ProbConstants.C2)
			c2 = new LogisticFuncParams(k, x0, C, flip);
		else if(struct == ProbConstants.PC1)
			pc1 = new LogisticFuncParams(k, x0, C, flip);
		else if(struct == ProbConstants.W1)
			w1 = new LogisticFuncParams(k, x0, C, flip);
		else if(struct == ProbConstants.S1)
			s1 = new LogisticFuncParams(k, x0, C, flip);
	}
	
	public StructureProbabilities evaluate(Double value){
		//** TODO : How often is this to be done, probably should make sp global and calculate once
		StructureProbabilities sp = new StructureProbabilities();
		sp.setStructureProbability(ProbConstants.RM, rm.evaluate(value));
		sp.setStructureProbability(ProbConstants.RM1, rm1.evaluate(value));
		sp.setStructureProbability(ProbConstants.RM2, rm2.evaluate(value));
		sp.setStructureProbability(ProbConstants.C1, c1.evaluate(value));
		sp.setStructureProbability(ProbConstants.C2, c2.evaluate(value));
		sp.setStructureProbability(ProbConstants.PC1, pc1.evaluate(value));
		sp.setStructureProbability(ProbConstants.W1, w1.evaluate(value));		
		sp.setStructureProbability(ProbConstants.S1, s1.evaluate(value));		
		return sp;
	}
	
	/**
	 * Logistic function f(x) = C+ [(1-C)/exp(-k(x-xo)))]
	 * where:
	 * 
	 * C = is y intercept of sigmoid function
	 * k = curve
	 * x0 = midppoint on x-axis
	 * 
	 * @param x
	 * @param k
	 * @param x0
	 * @param C
	 * @return
	 */
	public static double logisticFunction(double x, double k,  double x0, double C, boolean flip){
		if(flip)
			return (1-((1-C)/(1+Math.exp(-k*(x-x0))))) + C;
		else
			return ((1-C)/(1+Math.exp(-k*(x-x0)))) + C;
	}
	
	class LogisticFuncParams{
		
		public double k;
		public double x0;
		public double C; 
		public boolean flip = false;
		public LogisticFuncParams(double k, double x0, double C, boolean flip){
			this.k = k;
			this.x0 = x0;
			this.C = C;
			this.flip = flip;
		}
		
		public double evaluate(double value){
			return LogisticFunction.logisticFunction(value,k,x0,C,flip);
		}
	}
}
