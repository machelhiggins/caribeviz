package com.uwiseismic.ergo.buildingclassifier.ratiofunc;

import java.util.Random;

import com.uwiseismic.ergo.buildingclassifier.ProbConstants;
import com.uwiseismic.ergo.buildingclassifier.StructureProbabilities;

public class RatioFunction {

	//private StructureProbabilities sp = new StructureProbabilities();
	private double ratio;
	private Random rng;
	
	
	public RatioFunction(double ratio){
		this.ratio = ratio;
		rng = new Random(System.currentTimeMillis());
	}
	
	public StructureProbabilities evaluate(Double value){
		double w1 = 1;
		double rm2 = 1;
		double w1Selected = rng.nextDouble();
		if(w1Selected <= ratio){			
			w1 = 0.9;			
			rm2 = 1 - ratio;
		}				
		StructureProbabilities sp = new StructureProbabilities();
		sp.setStructureProbability(ProbConstants.RM, 0);
		sp.setStructureProbability(ProbConstants.RM1, 0);
		sp.setStructureProbability(ProbConstants.RM2, rm2);
		sp.setStructureProbability(ProbConstants.C1, 0);
		sp.setStructureProbability(ProbConstants.C2, 0);
		sp.setStructureProbability(ProbConstants.PC1, 0);
		sp.setStructureProbability(ProbConstants.W1, w1);		
		sp.setStructureProbability(ProbConstants.S1, 0);		
		return sp;
	}
	
}
