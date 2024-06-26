package com.uwiseismic.ergo.buildingclassifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.geohash.GeoHash;

public class BCMC {

	private BuildingClassification bcs[];
	private Random rng;
	private int iters; 
	
	public BCMC(GeoHash buildingClassifications, int iterations){
		int n = 0;
		bcs = new BuildingClassification[buildingClassifications.getHashables().size()];
		for(Iterator i = buildingClassifications.getHashables().iterator(); i.hasNext();){
			bcs[n] = (BuildingClassification)i.next();	
			n++;
			this.iters = iterations;
		}
		rng = new Random(System.currentTimeMillis());
	}
	
	public void performMC(IProgressMonitor monitor, int totalWorkUnits) throws StructureNotInEdException, NoProbabilityFunctionException{
		/*
		 * Assuming that the BuildingClassification objects have been properly initialized,
		 * Will wiggle the following metrics by 0.2, 500sqm for area :
		 * EDStructTypeRankingFunction
		   Gsqm
		   IsoperimetricQ
           MajorRoadIndex
           NoOfVertices*** not used anymore...should revisit TODO
		 * 
		 */
		double wiggle = 0.5;
		double areaWiggle = 200;
		double anneallingWeight;
		EDStructTypeRankingFunction ed;
		double val;
		int valInt;
		
		/*
		 * Randomly select buildings for bcs.length times instead of iters
		 */
		for(int i = 0; i < iters; i++){
			anneallingWeight = (double)(iters -i)/(double)iters;
//			System.out.println("ITER "+i);
			for(int n=0; n < bcs.length; n++){
			//for(int z=0; z < bcs.length; z++){
			
			//int n = rng.nextInt(bcs.length);
			//for(int n=0; n < bcs.length; n++){
				bcs[n].resetIndices();
				//ed = bcs[n].getED();
				//bcs[n].setED(ed);
	
				// gsqm
				val = bcs[n].getGsqm();
				val = rng.nextBoolean() ? val+(rng.nextDouble()*areaWiggle*anneallingWeight) : val- (rng.nextDouble()*areaWiggle*anneallingWeight); 			
				bcs[n].setGsqm(val);
	
				//isoperimetric Q
				val = bcs[n].getIsoperimetricQ();
				val = rng.nextBoolean() ? val+(rng.nextDouble()*wiggle*anneallingWeight) : val - anneallingWeight*(rng.nextDouble()*wiggle);
				if(val > 1)
					val = 1;
				if(val <= 0)
					val = Double.MIN_NORMAL;
				bcs[n].setIsoperimetricQ(val);
	
				// Major Road index
				val = bcs[n].getMajorRoadIndex();
				val = rng.nextBoolean() ? val+(rng.nextDouble()*wiggle*anneallingWeight) : val - anneallingWeight*(rng.nextDouble()*wiggle);
				if(val > 1)
					val = 1;
				if(val <= 0)
					val = Double.MIN_NORMAL;
				bcs[n].setMajorRoadIndex(val);
				
				// Income zone via mean unimproved land values
				//** hard coding this to the add/subtract a range between 0 to upper 90th percentile of UIL for the ED this building
				//** belongs
				if(bcs[n].getED() != null){
					val = bcs[n].getMeanUnImpLandValue();
					val = rng.nextBoolean() ? val + (rng.nextDouble()*bcs[n].getED().getUpper70PercentileUnimpLandVal())
							: val - (rng.nextDouble()*bcs[n].getED().getUpper70PercentileUnimpLandVal());
					
					if(val > 1)
						val = 1;
					if(val <= 0)
						val = Double.MIN_NORMAL;
					bcs[n].setMeanUnImpLandValue(val);;					
				}
				// Number of vertices (not used)
				valInt = bcs[n].getNoOfVertices();
				bcs[n].setNoOfVertices(valInt);
				try{
					//** since 
					bcs[n].determineStructure(false, true);
				}catch(StructureNotInEdException noED){
					//noED.printStackTrace();
				}
				bcs[n].saveCurrentStructureMemory();
			}
			
			 if(iters % 10 == 0 && monitor != null){
		        	monitor.worked((int)Math.floor((double)totalWorkUnits*(double)i/(double)iters));
		        	if(iters % 50 == 0 ){
		        		System.err.println("DEBUG "+this.getClass().getCanonicalName()+" iter = "+i+" worked done ="+
		        				(int)Math.floor((double)totalWorkUnits*(double)i/(double)iters));
		        	}
			 }
		}
		
		
		//**TODO: DEBUG 
		int debugRM1Count = 0;//** DEBUG
		int debugRM2Count = 0;//** DEBUG
		int debugC1Count = 0;//** DEBUG
		int debugC2Count = 0;//** DEBUG
		int debugW1Count = 0;//** DEBUG
		int debugS1Count = 0;//** DEBUG
		
		try{
//			BufferedWriter writer = new BufferedWriter(new FileWriter("debug_bmcmc_output/last_bcmcmc_output.txt"));//DEBUG
			
//			java.text.DecimalFormat formatter = new java.text.DecimalFormat("0.000#");//DEBUG
			//double structProbs[]; 
			int structFreq[];
			double structProbs[];
			int total = 0;
			for(int n=0; n < bcs.length; n++){
				System.out.println("Iteration "+n+1);
				structProbs = bcs[n].getLikelyStructureProbMemory();
				structFreq = bcs[n].getLikelyStructureMemory();
				//** whats the winning structure
				int winningStruct = -1;
				total = 0;
				double maxProb = -1*Double.MAX_VALUE;
				for(int i = 0; i < ProbConstants.STRUCT_TYPES.length; i++){
					total += structFreq[i];
					if(structFreq[i] > maxProb){
						maxProb = structFreq[i];
						winningStruct = i;
					}
				}
	
				//TODO: IF MOST LIKELY STRUCTURE IS RM LETS CHOOSE second most likely, RM is for debugging purposes
				// and was intended to be used if RM1 and RM2 cannot be separated
				if(winningStruct == ProbConstants.RM){
					//** search for next best
					maxProb = -1*Double.MAX_VALUE;
					for(int i = 0; i < ProbConstants.STRUCT_TYPES.length; i++){
						if(i != winningStruct && structFreq[i] > maxProb){
							maxProb = structFreq[i];
							winningStruct = i;
						}
					}
				}
					
				//** its probability of being correct
				double structProb =  (double)structFreq[winningStruct]/(double)total;				
				bcs[n].setMostLikelyStructure(winningStruct);
				bcs[n].setStructuresProbability(winningStruct, structProb);
				bcs[n].setFeatureStructTypeProb(bcs[n].getMostLikelyStructureString(), structProb);
								
				
				postMCOps(bcs[n]);
				
				if(bcs[n].getMostLikelyStructure() == ProbConstants.RM1)
					debugRM1Count++;
				if(bcs[n].getMostLikelyStructure() == ProbConstants.RM2)
					debugRM2Count++;
				if(bcs[n].getMostLikelyStructure() == ProbConstants.C1)
					debugC1Count++;
				if(bcs[n].getMostLikelyStructure() == ProbConstants.C2)
					debugC2Count++;
				if(bcs[n].getMostLikelyStructure() == ProbConstants.W1)
					debugW1Count++;
				if(bcs[n].getMostLikelyStructure() == ProbConstants.S1)
					debugS1Count++;
				

				//** START DEBUG
//				String likelyStructureStr = bcs[n].getMostLikelyStructureString();
//				if(likelyStructureStr.matches("PC1") && structProb < 1){
//					System.out.println("found PC1 with int "+winningStruct);
//				}
//					
////				writer.write(bcs[n].getBldgID()+"\t"+likelyStructureStr);
//				try{
//					writer.write("("+formatter.format(+structProb)+")  >>\t");
//				}catch(java.lang.IllegalArgumentException iax){
//					System.err.println(structProb);
//					iax.printStackTrace();
//				}
//				
//				for(int i = 0; i < structProbs.length; i++)
//					writer.write(structFreq[i]+"\t");
//				writer.write("\n");
				//** END DEBUG
			}
//			System.err.println("RM1 count: "+debugRM1Count+"\tRM2 Count: "+debugRM2Count //** DEBUG
//					+"\tC1 Count: "+debugC1Count//** DEBUG
//					+"\tC2 Count: "+debugC2Count//** DEBUG
//					+"\tW1 Count: "+debugW1Count//** DEBUG
//					+"\tS1 Count: "+debugS1Count);//** DEBUG
//			writer.flush();//** DEBUG
//			writer.close();//** DEBUG
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void postMCOps(BuildingClassification bc){
		
		
		/**
		 *  Due to error in building footpritns, a higher than normal amount of RM2 are classified as RM1. If the 
		 *  BuildingClassification object statisically represents a RM1 then it will be come an RM2 if P(RM1) < 0.7 and not P(C1 | C2) > 0.25
		 * 
		 */

		bc.determineIfRM1CouldBeRM2();	

		/**
		 * 
	     * This facilitates the selection of W1 after outside processes (like the monte carlo simulation)
	     *  because the RM2 probability will always be higher than the user specified P(W1).This is simply because 
	     *  of the dominating prevalence of RM2. 		
		 * 
		 */
		bc.determineIfWinningStructCouldBeW1();
		
		
		/**
		 * Region might be dominated by PC1 structures instead of RM2 for residential buildings. PC1 and RM2 indicators are the same.
		 * After MC, if PC1 dominates, will reassign structures that are RM2 to PC1
		 */
		bc.determineRMorRM2isPC1();
		
		bc.determineFullHASUZStuctureType();
		
	}
	
	
	public BuildingClassification[] getAllBuildings(){
		return bcs;
	}
}
