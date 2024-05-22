package com.uwiseismic.ergo.buildingclassifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.ergo.buildingclassifier.logistic.LogisticFunction;
import com.uwiseismic.gis.util.ObjectToReal;
import com.uwiseismic.gis.util.geohash.GeoHash;
import com.uwiseismic.gis.util.geohash.GeoHashable;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class BuildingClassification implements GeoHashable {

	private String bldgID;
	private SimpleFeature feature;
	private GeoHash allBuildings;	

	/*
	 *  Probabilities, metrics and required statistics for prob functions
	 */
	private int noOfVertices = 0;
	//** circle that encompassses this structure's neighbors
	private Geometry circle;
	//** this structure's neighbours
	private Collection neighbors;
	//** this is for neighbours on the 100 meter level
	private Geometry fixedRadiusCircle;
	//** this structure's neighbours on the 100 meter level
		private Collection fixedRadiusNeighbors;
	//** Isoperimetric Quotient 
	private double isoperimetricQ = 0;
	private double isoperimetricQOrig = 0;
	private ArrayList<String> isoperimetricQStatsNeeded;//** required statistics for isoperimetric value
	private HashMap <String, Double>isoperimetricQVarsValues = new HashMap <String, Double>(); //** variables and their values the probability function MAY require
	
	//** Isoperimetric Quotient similarity to neighbours
	private double isoperimetricQNeighborhood = 0;
	private ArrayList<String> isoPQNStatsNeeded;//** required statistics for isoperimetricQ of neighbors
	private HashMap <String, Double>isoPQNVarsValues = new HashMap <String, Double>(); //** variables and their values the probability function MAY require	
	
	//** major road proxmity metric
	private double majorRoadIndex = 0;
	private double majorRoadIndexOrig = 0;
	private ArrayList<String> mriStatsNeeded;//** required statistics for major road proximity index
	private HashMap <String, Double>mriVarsValues = new HashMap <String, Double>(); //** variables and their values the probability function MAY require	
	
	//** building ground floor area	
	private double gsqm = 0;
	private double gsqmOrig = 0;
	private ArrayList<String> gsqmStatsNeeded = new ArrayList<String>();//** required statistics for gsqm
	private HashMap <String, Double>gsqmVarsValues = new HashMap <String, Double>(); //** variables and their values the probability function MAY require

	//** building ground floor area similarirty to neighbors
	private double gsqmNeighborhood = 0;
	private ArrayList<String> gsqmNStatsNeeded;//** required statistics for gsqm  of neighbors
	private HashMap <String, Double>gsqmNeighVarsValues = new HashMap <String, Double>(); //** variables and their values the probability function MAY require	

	//** Income zone of the ED we're in 
	private double meanUnImpLandValue = 0;
	private ArrayList<String> inzStatsNeeded;//** required statistics for income zone (NONE!)
	private HashMap <String, Double>inzVarsValues = new HashMap <String, Double>(); //** variables and their values the probability function MAY require	
	
	//** Ratio of structure types for surrounding structures
	private double neighborStructs[] = new double[ProbConstants.STRUCT_TYPES.length];
	private StructureProbabilities neighborStructProb =  new StructureProbabilities(); //** not so much of a probability but a weight	
	
	//** ED Structure Ranking function
	private EDStructTypeRankingFunction ed;
	//** Probability parameters for each metric
	private BuildingProbParameters probParams;
	
	//** structure type probability
	private String likelyStructureStrHAZUS;
	private String likelyStructureStrGeneral;
	private int likelyStructure;
	private double likelyStructureProb = 0;
	private double structProbs[] = new double[ProbConstants.STRUCT_TYPES.length];
	private boolean calculatedStructuresProb = false;
	private boolean userSetStructProb = false;
	//** occupancy type probability	
	private final double NOT_USER_SET_PROB_THRESHOLD = 0.95; //** arbritrary value to check if user, calculated or default prob
	
	//** for MC simulation
	private int likelyStructureMemory[] = new int[ProbConstants.STRUCT_TYPES.length];
	private double[] likelyStructureProbMemory = new double[ProbConstants.STRUCT_TYPES.length];
	
	
	/**
	 * @param feature The feature (building) this object represents. Must have gis schema from gisSchemas/ergo-uwiBuildingClassification_1.0.xsd
	 */
	public BuildingClassification(SimpleFeature feature){
		this.feature = feature;
		
		Object id = feature.getAttribute("bldg_id");
		if(id == null)
			bldgID = "0";
		else if(id instanceof String)
			bldgID = (String)id;
		else if(id instanceof Integer)
			bldgID = ((Integer)id).toString();
		else if(id instanceof Long)
			bldgID = ((Long)id).toString();
		
		noOfVertices = ObjectToReal.getMeInteger(feature.getAttribute("n_vertices")).intValue();
		isoperimetricQ = ObjectToReal.getMeDouble(feature.getAttribute("isop_q")).doubleValue();
		majorRoadIndex = ObjectToReal.getMeDouble(feature.getAttribute("road_index")).doubleValue();		
		gsqm = ObjectToReal.getMeDouble(feature.getAttribute("area_sqm")).doubleValue();		
		likelyStructureStrHAZUS = (String)feature.getAttribute("str_typ2");
		likelyStructureStrGeneral = (String)feature.getAttribute("struct_typ");		
		
		if(feature.getAttribute("str_prob") != null)
			likelyStructureProb = ObjectToReal.getMeDouble(feature.getAttribute("str_prob")).doubleValue();
		else{
			try{
				likelyStructureProb = ObjectToReal.getMeDouble(feature.getAttribute("STR_PROB")).doubleValue();
			}catch(NullPointerException np){
				likelyStructureProb = 0;
			}
		}
			
		if(likelyStructureProb > NOT_USER_SET_PROB_THRESHOLD){
			userSetStructProb = true;			
		}
		
		//** Pull existing structure from shapefile
		if(likelyStructureStrHAZUS != null && !likelyStructureStrHAZUS.equals("")){
			//** We're not using full hazus classes with heights, eg RM1L will be handled as RM1
			if((likelyStructureStrHAZUS.endsWith("H")
					|| likelyStructureStrHAZUS.endsWith("M")
							|| likelyStructureStrHAZUS.endsWith("L"))){
				if(userSetStructProb)
					likelyStructureStrHAZUS = likelyStructureStrHAZUS.substring(0, likelyStructureStrHAZUS.length()-1);
				else //** then this might be a bogus classification. Defaulting to an RM2
					likelyStructureStrHAZUS = ProbConstants.getStructureType(ProbConstants.RM2);
			}
			
			likelyStructure = ProbConstants.getStructureType(likelyStructureStrHAZUS);
		}
		//** HAZUS detail structure type not set
		else{
			//** User set
			if(userSetStructProb){				
				likelyStructure = ProbConstants.getStructureType(likelyStructureStrGeneral);	
				likelyStructureStrHAZUS = likelyStructureStrGeneral;
			}
			// ** not user set
			else{
				likelyStructure = ProbConstants.RM;
				likelyStructureStrHAZUS = ProbConstants.getStructureType(ProbConstants.RM);
			}
		}
		
			
		for(int i = 0;  i < likelyStructureProbMemory.length; i++)
			likelyStructureProbMemory[i] = 0;
					
		isoperimetricQOrig = isoperimetricQ;
		majorRoadIndexOrig = majorRoadIndex;
		gsqmOrig = gsqm;	
	}	
	
	private void DEBUG() {//**DELETE ME**********************
		for (int z = 0; z < structProbs.length; z++) {//**DELETE ME****************
			System.out.print(structProbs[z] + "\t");//**DELETE ME****************************													 
		} /******************************************** DELETE ME ************/
		System.out.println();//***DELETE ME**************
		for (int i = 0; i < ProbConstants.STRUCT_TYPES.length; i++) {//*** REMOVE ME PLEASE, KiiiIIILL ME ******
			structProbs[i] = 1; //**DELETE ME *****************
		} /******************************************** DELETE ME ************/
	}
	public boolean DEBUG = false;//** DELETE ME ******************************


	/**
	 * @param probParams
	 */
	public void setProbParams(BuildingProbParameters probParams) {				
		this.probParams = probParams;		
		mriStatsNeeded = probParams.getRequiredStatistics(ProbConstants.RP);
		isoperimetricQStatsNeeded = probParams.getRequiredStatistics(ProbConstants.BSR);
		gsqmStatsNeeded = probParams.getRequiredStatistics(ProbConstants.BA);
		gsqmNStatsNeeded = probParams.getRequiredStatistics(ProbConstants.BAN);		
		isoPQNStatsNeeded = probParams.getRequiredStatistics(ProbConstants.BSRN);		
		inzStatsNeeded = probParams.getRequiredStatistics(ProbConstants.INZ);
	}
	
	/**
	 * Reset indices that contribute to weight\probability
	 */
	public void resetIndices(){
		//TODO: Add new indices here
		if(ed != null)
			meanUnImpLandValue = ed.getMeanUnimpLandVal();
		isoperimetricQ = isoperimetricQOrig;
		majorRoadIndex = majorRoadIndexOrig;
		gsqm = gsqmOrig;	
	}

	/**
	 * Determine structure type this object represents
	 * @param calculateLocalIndices If true will only calculate all indices related to this object that determine weight\probability
	 * @param calculateNeighborhoodIndices If true will only recalculate indices related to spatially neighboring structures that determine weight\probability
	 * @throws StructureNotInEdException
	 * @throws NoProbabilityFunctionException
	 */
	public void determineStructure(boolean calculateLocalIndices, boolean calculateNeighborhoodIndices) throws StructureNotInEdException, NoProbabilityFunctionException{
		
		if(probParams == null)
			throw new NoProbabilityFunctionException("Required object "+BuildingProbParameters.class.getName()+" was not set for this class");
		if(ed == null){
			if(likelyStructureStrHAZUS == null)
				likelyStructure = ProbConstants.RM; //** most dominant type in caribbean
			if(!userSetStructProb)
				likelyStructureProb = 0.00001;
//			throw new StructureNotInEdException("Required object "+EDStructTypeRankingFunction.class.getName()+" was not set for this class (building id = "+bldgID+")");
			System.err.println("Required object "+EDStructTypeRankingFunction.class.getName()+" was not set for this class (building id = "+bldgID+")");
			
			return;
		}
		if(circle == null)
			createNeighborhoodCircle();				
		if(neighbors == null)
			neighbors = allBuildings.getContaining(circle);
		
		if(fixedRadiusCircle == null)
			createFixedRadiusCircle(0.00182);//~200m
		if(fixedRadiusNeighbors == null)
			fixedRadiusNeighbors = allBuildings.getContaining(fixedRadiusCircle);
		
		//** Calculate indices
		if(calculateLocalIndices){
			if(!userSetStructProb){
				this.doGsqm();
				this.doMajorRoadIndex();
				this.doIsoperimetricQ();
			}
			this.doIncomeZone();
		}
		if(!userSetStructProb && calculateNeighborhoodIndices){
			this.doGsqmNeighborhood();
			this.doIsoperimetricQNeighborhood();
			this.doNeighbouringStructTypes();
		}
		
		//** reset structure probability
		for(int i = 0; i < ProbConstants.STRUCT_TYPES.length; i++){
			if(userSetStructProb && ProbConstants.STRUCT_TYPES[i].equals(this.getMostLikelyStructureString())){
				structProbs[i] = likelyStructureProb;
			}
			else
				structProbs[i] = 1;		
		}
			
		
		//** Evaluate for each index			
		if(!userSetStructProb){
					
			//** EDStructRanking
			if(ed != null){
				applyStructureProb(ed.getStructureScore(),1, false, 0);
				if(DEBUG){
					System.out.println("RM\tRM1\tRM2\tC1\tC2\tW1\tS1\tPC1\tURM\tPC2");//*** DEBUG********
					System.out.println("ED "+ed.getID());
					DEBUG();	
				}
			}
			
			//** Major Road Proximity Index
			applyStructureProb(probParams.getProbabilitiesFromMetric(ProbConstants.RP, majorRoadIndex, mriVarsValues, false),1, false, 0);
			if(DEBUG){
				System.out.println("RP "+this.majorRoadIndex);
				DEBUG();
			}
			
			//** Building Footprint Area Metric
			applyStructureProb(probParams.getProbabilitiesFromMetric(ProbConstants.BA, gsqm, gsqmVarsValues, false),1, false, 0);
			if(DEBUG){
			System.out.println("BA "+this.gsqm+"\t Neighbor mean = "+gsqmVarsValues.get(ProbConstants.MEAN)+ "\tmean+sigma = "+gsqmVarsValues.get(ProbConstants.MEAN_PLUS_SIGMA));
			DEBUG();
			}
			
			//** Building Footprint Area Similarity to Neighbors Metric
			applyStructureProb(probParams.getProbabilitiesFromMetric(ProbConstants.BAN, gsqmNeighborhood, gsqmNeighVarsValues, false),1, false, 0);
			if(DEBUG){
			System.out.println("BAN "+this.gsqmNeighborhood+"\t Neighbor mode = "+gsqmNeighVarsValues.get(ProbConstants.MODE));
			DEBUG();
			}
			
			//** Building Footprint Shape Regularity Metric
			applyStructureProb(probParams.getProbabilitiesFromMetric(ProbConstants.BSR, isoperimetricQ, isoperimetricQVarsValues, false),1, false, 0);
			if(DEBUG){
			System.out.println("BSR "+this.isoperimetricQ);
			DEBUG();
			}
			//** Building Footprint Shape Regulartiy Similarity to Neighbors Metric
			applyStructureProb(probParams.getProbabilitiesFromMetric(ProbConstants.BSRN, isoperimetricQNeighborhood, isoPQNVarsValues, false),1, false, 0);
			if(DEBUG){
			System.out.println("BSRN "+this.isoperimetricQNeighborhood+"\t Neighbor sigma = "+isoPQNVarsValues.get(ProbConstants.SIGMA));
			DEBUG();
			}
			
			
			//** Income zone metric
			if(DEBUG){
				//applyStructureProb(probParams.getProbabilitiesFromMetric(ProbConstants.INZ, meanUnImpLandValue, inzVarsValues, true),1, false, 0);
				applyStructureProb(probParams.getProbabilitiesFromMetric(ProbConstants.INZ, meanUnImpLandValue, inzVarsValues, false),1, false, 0);
				probParams.debugDisableLogicTreeDebug();				
			}
			else{
				if(inzVarsValues.size() ==  0){
					System.err.print("Values for INZ are definitely zero. Could Ed be null : ");
					if(ed == null)
						System.err.println("yes");
					else
						System.err.println("no");
					
				}
				applyStructureProb(probParams.getProbabilitiesFromMetric(ProbConstants.INZ, meanUnImpLandValue, inzVarsValues, false),1, false, 0);
			}
			if(DEBUG){
				System.out.println("INZ "+this.meanUnImpLandValue+"\t 90 percentile = "+inzVarsValues.get(ProbConstants.UPPER_NINETY_PERCENTILE)
					+"\t 80 percentile = "+inzVarsValues.get(ProbConstants.UPPER_EIGHTY_PERCENTILE)
					+"\t 70 percentile = "+inzVarsValues.get(ProbConstants.UPPER_SEVENTY_PERCENTILE));
				DEBUG();
			}
			
			//** DIRTY LAST MINUTE HACK TO TAKE INTO ACCOUNT THE DISTRIBUTION OF AREA FOR THE STRUCTURES IN AN ED
			applyStructureProb(getEDAreaDistribution(),1, false, 0);
			
			//** neighbouring structure type WEIGHTING, NOT PROBABILITY
			if(calculatedStructuresProb){
				applyStructureProb(neighborStructProb,1, false, 0);
				if(DEBUG){
					System.out.println("Neighbor Ratios");
					DEBUG();
				}
			}
			structProbs[ProbConstants.URM] = 0.0000000001;//** TODO: Not estimating for now, OR EVER
			structProbs[ProbConstants.PC2] = 0.0000000001;//** TODO: Not estimating for now, OR EVER	

			determineWinningStruct();		
			
			//** check if structure should be S1 since scheme currently out-competes S1 with RM1
			determineIfReallyS1();			
			determineReallyC1orC2();
			setFeatureStructTypeProb(likelyStructureStrHAZUS, new Double(likelyStructureProb));			
			
			calculatedStructuresProb = true;
		}		

	}
	
	/**
	 * 
	 * From structures' probabilities will select the the most likely structure and its probability
	 * @return
	 */
	private int determineWinningStruct(){
		//** whats the winning structure
		int winningStruct = -1;
		double maxProb = -Double.MAX_VALUE;
		for(int i = 0; i < ProbConstants.STRUCT_TYPES.length; i++){
			if(structProbs[i] > maxProb){
				maxProb = structProbs[i];
				winningStruct = i;
			}				
		}
		if(DEBUG){
			System.out.println("Winning structure is : "+winningStruct);
		}
		//IF MOST LIKELY STRUCTURE IS RM LETS CHOOSE second most likely, RM is for debugging purposes
		// and should be used if RM1 and RM2 cannot be separated
		if(winningStruct == ProbConstants.RM){
//			//** search for next best
//			maxProb = -1*Double.MAX_VALUE;
//			for(int i = 0; i < ProbConstants.STRUCT_TYPES.length; i++){
//				if(i != winningStruct && structProbs[i] > maxProb){
//					maxProb = structProbs[i];
//					winningStruct = i;
//				}				
//			}
			//** select between RM1 or RM2		
			if(structProbs[ProbConstants.RM1] > structProbs[ProbConstants.RM2]){
				winningStruct = ProbConstants.RM1;
			}
			else
				winningStruct = ProbConstants.RM2;
		}
		this.likelyStructure = winningStruct;
		this.likelyStructureStrHAZUS = ProbConstants.getStructureType(winningStruct);		
		this.likelyStructureProb = structProbs[winningStruct];
		return winningStruct;
	}
	
	/**
	 * Quick dirty hack to take into account the guessed scoring per structure depending
	 * on shape of the distribution of its area. If the half width of the normal distribution (P = 2.335*std)
	 * is less than the 2/3 of the range of area in ED them its most likely similar residential structures.
	 * 
	 * @return
	 */
	public StructureProbabilities getEDAreaDistribution(){
		StructureProbabilities sp = new StructureProbabilities();
		sp.rm = 1;
		sp.rm1 = 1;
		sp.rm2 = 1;
		sp.c1 = 1;
		sp.c2 = 1;
		sp.pc1 = 1;
		sp.s1 = 1;
		sp.w1 = 1;
		if(ed != null){
			if(2.335*ed.getStdSQM() <= 2*ed.getSqmRange()/3){
				sp.rm = 0.9;
				sp.rm1 = 0.7;
				sp.rm2 = 0.9;
				if(this.getGsqm() >= 700){
					sp.c1 = 0.5;
					sp.c2 = 0.5;
				}else{
					sp.c1 = 0.1;
					sp.c2 = 0.1;
				}
				sp.pc1 = 0.9;
				sp.s1 = 0.5;
				sp.w1 = 0.9;
			}else{
				sp.rm = 0.8;
				sp.rm1 = 0.7;
				sp.rm2 = 0.8;
				sp.c1 = 0.99;
				sp.c2 = 0.99;
				sp.pc1 = 0.2;
				sp.s1 = 0.9;
				sp.w1 = 0.8;
			}
				
		}
		return sp;
	}
	
	public void determineReallyC1orC2(){
		if( likelyStructure == ProbConstants.C1
				||  likelyStructure == ProbConstants.C2
				&& gsqm < 700){
			structProbs[ProbConstants.C1] = structProbs[ProbConstants.C1]*0.009;
			structProbs[ProbConstants.C2] = structProbs[ProbConstants.C2]*0.009;
		}
	}
	
	/**
	 * 
     * This method is to facilitate the selection of W1 after outside processes (like the monte carlo simulation)
     *  because the RM2 probability will always be higher than the user specified P(W1).This is simply because 
     *  of the dominating prevalence of RM2. Intended to be called after determineWiningStructure(boolean)
	 * 
	 */
	public void determineIfWinningStructCouldBeW1(){
		if(!userSetStructProb && probParams.getMostPrevalentyStructureType() == ProbConstants.RM2
				&& likelyStructure == ProbConstants.RM2
				&& ed != null){			
			//** first structure must be in low income ed's  AND is DEFINITELY less that 215 sqm (the mean according to survey)
//			if(ed.getMeanUnimpLandVal() < ed.getUpper70PercentileUnimpLandVal()
			if(ed.getMeanUnimpLandVal() < ed.getUpper80PercentileUnimpLandVal()
					&& gsqm < 190){
				//** The WR index uses RatioFunction which will probabilistically determine if it should be W1 
				StructureProbabilities sp;
				try {
					sp = probParams.getProbabilitiesFromMetric(ProbConstants.WR, 0, null, false);									
					if(sp.rm2 < sp.w1){
						structProbs[ProbConstants.W1] = structProbs[ProbConstants.RM2];
						structProbs[ProbConstants.RM2] = structProbs[ProbConstants.RM2]*0.8777;
					}					
					determineWinningStruct();
					setFeatureStructTypeProb(likelyStructureStrHAZUS,
							getStructuresProbability(getMostLikelyStructure()));
				} catch (NoProbabilityFunctionException e) {
					e.printStackTrace();
				}
			}									
		}		
	}
	
	/**
	 * Due to error in building footprints, a higher than normal amount of RM2 are classified as RM1. If this object statistically represents
	 * a RM1 then it will be come an RM2 if P(RM1) < 0.7 and not P(C1 | C2) > 0.25.
	 * Intended to be called after determineWiningStructure(boolean)
	 */
	public void determineIfRM1CouldBeRM2(){
		if(likelyStructure == ProbConstants.RM1){		
			//** if structure is smaller than average gsqm (215 m^2) then just flip it to RM2
			if(gsqm <= 300){
				likelyStructure = ProbConstants.RM2;
				likelyStructureStrHAZUS = ProbConstants.getStructureType(ProbConstants.RM2);
				setFeatureStructTypeProb(likelyStructureStrHAZUS,
						getStructuresProbability(getMostLikelyStructure()));
			}
			else{
				double total = 0;
				for(int i = 0; i < likelyStructureMemory.length; i++)
					total += likelyStructureMemory[i];
				if((double)likelyStructureMemory[ProbConstants.RM1]/total <  0.7
						&& !((double)likelyStructureMemory[ProbConstants.C1]/total > 0.25
								|| (double)likelyStructureMemory[ProbConstants.C2]/total > 0.25 )){
					likelyStructure = ProbConstants.RM2;
					likelyStructureStrHAZUS = ProbConstants.getStructureType(ProbConstants.RM2);
					structProbs[ProbConstants.RM1] = structProbs[ProbConstants.RM2];
					structProbs[ProbConstants.RM2] = structProbs[ProbConstants.RM1];
					setFeatureStructTypeProb(likelyStructureStrHAZUS,
							getStructuresProbability(getMostLikelyStructure()));
				}
			}
		}
	}
	
	
	/**
	 * 
	 * This method contains hardcoded conditions to determine if a S1 strcuture is really an S1 structure.<br>
	 * Conditiions are if structure sqm is less greater than 2000 m^2 and structure should have at most 6 vertices
	 * 
	 */
	private void determineIfReallyS1(){
		if(!userSetStructProb && likelyStructure == ProbConstants.S1){		
			if(noOfVertices > 8){
				structProbs[ProbConstants.S1] = 0.1*structProbs[ProbConstants.S1];//** weigh it down
			}
			if(this.gsqm <  3000){
				structProbs[ProbConstants.S1] = 0.01*structProbs[ProbConstants.S1];//** weigh it down
			}
			determineWinningStruct();
		}
	}
	
	private void applyStructureProb(StructureProbabilities sp, double weight, boolean average, int nBeforeAverage){

		if(average){
			structProbs[ProbConstants.RM]  = (structProbs[ProbConstants.RM]*nBeforeAverage+sp.rm)/(nBeforeAverage+1);
			structProbs[ProbConstants.RM1] = (structProbs[ProbConstants.RM1]*nBeforeAverage+sp.rm1)/(nBeforeAverage+1);
			structProbs[ProbConstants.RM2] = (structProbs[ProbConstants.RM2]*nBeforeAverage+sp.rm2)/(nBeforeAverage+1);
			structProbs[ProbConstants.C1]  = (structProbs[ProbConstants.C1]*nBeforeAverage+sp.c1)/(nBeforeAverage+1);
			structProbs[ProbConstants.C2]  = (structProbs[ProbConstants.C2]*nBeforeAverage+sp.c2)/(nBeforeAverage+1);
			structProbs[ProbConstants.PC1] = (structProbs[ProbConstants.PC1]*nBeforeAverage+sp.pc1)/(nBeforeAverage+1);
			structProbs[ProbConstants.S1]  = (structProbs[ProbConstants.S1]*nBeforeAverage+sp.s1)/(nBeforeAverage+1);
			structProbs[ProbConstants.W1]  = (structProbs[ProbConstants.W1]*nBeforeAverage+sp.w1)/(nBeforeAverage+1);
		}
		//TODO: weigh a probability, you idiot?????
		structProbs[ProbConstants.RM] *= sp.rm;
		structProbs[ProbConstants.RM1] *= sp.rm1;
		structProbs[ProbConstants.RM2] *= sp.rm2;
		structProbs[ProbConstants.C1] *= sp.c1;
		structProbs[ProbConstants.C2] *= sp.c2;
		structProbs[ProbConstants.PC1] *= sp.pc1;
		structProbs[ProbConstants.S1] *= sp.s1;
		structProbs[ProbConstants.W1] *= sp.w1;
		
		
	}	
	
	public void determineRMorRM2isPC1(){
		
		if((!userSetStructProb && probParams.getMostPrevalentyStructureType() == ProbConstants.PC1)
				&& (likelyStructure == ProbConstants.RM || likelyStructure == ProbConstants.RM1 || likelyStructure == ProbConstants.RM2)){			
			//** if there area few neighbours
			if((fixedRadiusNeighbors != null && fixedRadiusNeighbors.size() > 15)
					|| gsqm < 300){
				if(likelyStructure == ProbConstants.RM2){
					structProbs[ProbConstants.PC1] = structProbs[ProbConstants.RM2];
					structProbs[ProbConstants.RM] = structProbs[ProbConstants.RM]*0.01;
					structProbs[ProbConstants.RM2] = structProbs[ProbConstants.RM2]*0.01;
					structProbs[ProbConstants.RM1] = structProbs[ProbConstants.RM1]*0.01;	
				}
				else if(likelyStructure == ProbConstants.RM1 && this.gsqm < 800){
					structProbs[ProbConstants.PC1] = structProbs[ProbConstants.RM1];
					structProbs[ProbConstants.RM] = structProbs[ProbConstants.RM]*0.01;
					structProbs[ProbConstants.RM2] = structProbs[ProbConstants.RM2]*0.01;
					structProbs[ProbConstants.RM1] = structProbs[ProbConstants.RM1]*0.01;
				}
				else if(likelyStructure == ProbConstants.RM){
					structProbs[ProbConstants.PC1] = structProbs[ProbConstants.RM];
					structProbs[ProbConstants.RM] = structProbs[ProbConstants.RM]*0.01;
					structProbs[ProbConstants.RM2] = structProbs[ProbConstants.RM2]*0.01;
					structProbs[ProbConstants.RM1] = structProbs[ProbConstants.RM1]*0.01;
				}
				determineWinningStruct();
				setFeatureStructTypeProb(likelyStructureStrHAZUS,
						getStructuresProbability(getMostLikelyStructure()));
			}
		
		}
	}
	
	/**
	 * This method attempts to determined number of stories and should be called AFTER final classification is made.
	 * 
	 * 
	 */
	public void determineFullHASUZStuctureType(){
		int numOfStories = ObjectToReal.getMeInteger(feature.getAttribute("no_stories")).intValue();
		
		if(numOfStories == 0){
			if(likelyStructure == ProbConstants.RM1){
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
				numOfStories = 2;
				feature.setAttribute("no_stories", new Integer(numOfStories)); //** average height of RM1 structures
			}
			else if(likelyStructure == ProbConstants.RM2){
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";					
				if(probParams.getRM2MinStoreys() != 0)
					numOfStories = probParams.getRM2MinStoreys();					
				else
					numOfStories = 1;//** average height of RM1 structures
				feature.setAttribute("no_stories", new Integer(numOfStories));
			}
			else if(likelyStructure == ProbConstants.C1 
					|| likelyStructure == ProbConstants.C2){
				
				/*
				 * Determine from neighbours, if there are many C1 or C2 around me (more than 50%) then set to 5 stories
				 */
				int numberOfC1orC2 = 0;
				for(Iterator i = neighbors.iterator();i.hasNext();){
					BuildingClassification bc = (BuildingClassification)i.next();					
					if(bc.getMostLikelyStructure() == ProbConstants.C1
							|| bc.getMostLikelyStructure() == ProbConstants.C2)
						numberOfC1orC2++;
				}
				
				if(((double)numberOfC1orC2/(double)neighbors.size()) > 0.5)
					numOfStories = 5;
				else{
					if(likelyStructure == ProbConstants.C1 && probParams.getC1MinStoreys() != 0)
						numOfStories = probParams.getC1MinStoreys();
					else if(likelyStructure == ProbConstants.C2 && probParams.getC1MinStoreys() != 0)
						numOfStories = probParams.getC1MinStoreys();
					else
						numOfStories = 3;
				}
				feature.setAttribute("no_stories", new Integer(numOfStories)); 
				if(numOfStories <= 3)
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
				else if(numOfStories > 3 && numOfStories <= 7)
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"M";
				else
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"H";
			}			
			else if(likelyStructure == ProbConstants.W1){
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
				numOfStories = 1;
				feature.setAttribute("no_stories", new Integer(numOfStories)); //** average height of W1 structures
			}
			else if(likelyStructure == ProbConstants.S1){
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
				numOfStories = 1;
				feature.setAttribute("no_stories", new Integer(numOfStories)); //** average height of S structures
			}
			else if(likelyStructure == ProbConstants.PC1){
				// nothing to do here				
			}
			else if(likelyStructure == ProbConstants.PC2){				
				numOfStories = 4;
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";				
				feature.setAttribute("no_stories", new Integer(numOfStories)); //** This is arbritrary, these buildings are usually 4 stories in residential developments
			}
			else if(likelyStructure == ProbConstants.URM){
				numOfStories = 1;
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
				feature.setAttribute("no_stories", new Integer(numOfStories)); //** average height of URM structures
			}
		}
		else{
			if(likelyStructure == ProbConstants.RM1){
				if(numOfStories <= 3)
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
				else
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"M";
			}
			else if(likelyStructure == ProbConstants.RM2){
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";					 
			}
			else if(likelyStructure == ProbConstants.C1 
					|| likelyStructure == ProbConstants.C2){
				if(numOfStories <= 3)
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
				else if(numOfStories > 3 && numOfStories <= 7)
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"M";
				else
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"H";
			}			
			else if(likelyStructure == ProbConstants.W1){
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
			}
			else if(likelyStructure == ProbConstants.S1){
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
			}
			else if(likelyStructure == ProbConstants.PC1){
				// nothing to do here
			}
			else if(likelyStructure == ProbConstants.PC2){
				if(numOfStories <= 3)
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
				else if(numOfStories > 3 && numOfStories <= 7)
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"M";
				else
					likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"H";
			}
			else if(likelyStructure == ProbConstants.URM){
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
			}
		}
		feature.setAttribute("str_typ2", likelyStructureStrHAZUS);
		
		//** set other attributes that rely on number of storeys, total square feet and appraied building value
		feature.setAttribute("sq_foot",numOfStories*((Double)feature.getAttribute("gsq_feet")).doubleValue());
		/************************ N.B. FOR appr_bldg ******************************************************************************
		************************ N.B. FOR appr_bldg ******************************************************************************
		************************ N.B. FOR appr_bldg ******************************************************************************
		* What's done for appr_bldg is that if its null, set with cost_sqft attrib which is now required since Dominica assessment
		* IF cost_sqft attribute is not set, will used the normalized mean UIL from ED times USD$250 which is the maximum typical construction
		* cost plus $50 in the Caribbean as of 2022. 
		/************************ FIX: Make this more streamlined or required cost_sqft to be set !!!!!!!!!!!!!!!!!!!!  */
		if(feature.getAttribute("appr_bldg") == null){
			if(feature.getAttribute("cost_sqft") != null){
				feature.setAttribute("appr_bldg",numOfStories*((Double)feature.getAttribute("gsq_feet")).doubleValue()
						*((Double)feature.getAttribute("cost_sqft")).doubleValue());   
			}
			//** following assumes that ED has normalized UIL from construction cost/sqft AND will *USD250
			else{
				feature.setAttribute("appr_bldg",numOfStories*((Double)feature.getAttribute("gsq_feet")).doubleValue()
						*ed.getMeanUnimpLandVal()*250);
				//System.err.println("[SPECIAL DEBUG] No cost_sqft using ED Value x USD250 = "+(ed.getMeanUnimpLandVal()*250));				
			}						
		}
	}
	
	
	/**
	 * Use constants from ProbConstants
	 * 
	 * @param structType
	 * @param structProb
	 */
	public void setFeatureStructTypeProb(String structType, double structProb){
		if(!this.userSetStructProb){			
			feature.setAttribute("str_typ2", structType);
			if(structType.matches("RM.*"))
				feature.setAttribute("struct_typ", ProbConstants.getStructureType(ProbConstants.RM));
			else
				feature.setAttribute("struct_typ", structType);
			feature.setAttribute("str_prob", new Double(structProb));
		}
		//else //** in the case where struct_typ is set but not str_typ2
			//feature.setAttribute("str_typ2", feature.getAttribute("struct_typ"));
	}
	
	/**
	 * Saves the current structure type and probability 
	 */
	public void saveCurrentStructureMemory(){
		if(likelyStructure != ProbConstants.UNKNOWN){ //** possibly a user set class type so leave it be	
			likelyStructureMemory[likelyStructure]++;		
			likelyStructureProbMemory[likelyStructure] += likelyStructureProb;
		}
	}

	
	
	/**
	 * Does as advertised.
	 * @return
	 */
	public int[] getLikelyStructureMemory() {
		return likelyStructureMemory;
	}

	//TODO: ************ FOR NOW WE'RE JUST AVERAGING THE PROBABILITIES
	/**
	 * Does as advertised.
	 * @return
	 */
	public double[] getLikelyStructureProbMemory() {
		double avgPr[] = new double[likelyStructureProbMemory.length];
		for(int i = 0;  i < likelyStructureProbMemory.length; i++)
			avgPr[i] = likelyStructureProbMemory[i]/likelyStructureProbMemory.length;
		return avgPr;
	}

	private void doMajorRoadIndex(){

		if(mriStatsNeeded == null){
			mriStatsNeeded = probParams.getRequiredStatistics(ProbConstants.RP);
			if(mriStatsNeeded == null){
				mriVarsValues = null;
				return;
			}
		}		
		boolean needMean = true;
		boolean needSigma = false;
		boolean needMeanPlusSigma = false;
		boolean needMeanMinusSigma = false;
		boolean needMode = false;
		
		if(mriStatsNeeded.contains(ProbConstants.SIGMA))
			needSigma = true;
		if(mriStatsNeeded.contains(ProbConstants.MEAN_PLUS_SIGMA))
			needMeanPlusSigma = true;
		if(mriStatsNeeded.contains(ProbConstants.MEAN_MINUS_SIGMA))
			needMeanMinusSigma = true;
		if(mriStatsNeeded.contains(ProbConstants.MODE))
			needMode = true;
	
		ArrayList<BuildingClassification> edBuildings = ed.getBuildings();
		double rp[] = new double[edBuildings.size()];
		double maxVal = -1*Double.MAX_VALUE;
		double minVal = Double.MIN_VALUE;
		int n = 0;
		for(Iterator <BuildingClassification> i = edBuildings.iterator(); i.hasNext();){
			rp[n]= i.next().getMajorRoadIndex();
			if(needMode){
				if(maxVal < rp[n])
					maxVal = rp[n];
				if(minVal > rp[n])
					minVal = rp[n];
			}
			n++;
		}		

		//** determine bins and freq and normalize area
		double bins[] = new double[50];
		double rpMean = 0;
		int freq[] = new int[50];
		double inc = (maxVal - minVal)/50;
		if(needMode){
			for(int z = 1; z < bins.length; z++){
				bins[z-1] = (inc*(double)z);//** linspace(min:max, 50)
			}
		}
		int maxFreq = -1;
		int maxFreqIndex = 0;
		for(int i=0; i < rp.length; i++){
			if(needMean){
				rpMean += rp[i];
			}
			
			if(needMode){
				for(int h = 0; h < bins.length;h++){
					if(h == 0){
						if(rp[i] < bins[0]){
							freq[0]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;
						}
					}
					if(bins[h] <= rp[i]){
						if( h+1 < bins.length && rp[i] < bins[h+1]){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;	
						}
						else if( h+1 == bins.length){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
						}							
					}					
				}
			}			
		}
		
		if(needMean || needSigma || needMeanPlusSigma|| needMeanMinusSigma){	
			rpMean /= rp.length;
			mriVarsValues.put(ProbConstants.MEAN, new Double(rpMean));
		}
		if(needMode){
			mriVarsValues.put(ProbConstants.MODE, new Double(bins[maxFreqIndex]));
		}			
		
		
				
		double std = 0;
	
		if(needSigma || needMeanPlusSigma|| needMeanMinusSigma){
			for(int i=0; i < rp.length; i++)			
				std += Math.pow(rpMean - rp[i], 2);
			std = Math.sqrt(std/rp.length);
			if(needSigma)
				mriVarsValues.put(ProbConstants.SIGMA, new Double(std));
			if(needMeanPlusSigma)
				mriVarsValues.put(ProbConstants.MEAN_PLUS_SIGMA, new Double(rpMean+std));
			if(needMeanMinusSigma)
				mriVarsValues.put(ProbConstants.MEAN_MINUS_SIGMA, new Double(rpMean-std));
		}
	}
	
	private void doGsqm(){

		if(gsqmStatsNeeded == null){
			gsqmStatsNeeded = probParams.getRequiredStatistics(ProbConstants.BA);
			if(gsqmStatsNeeded == null){
				gsqmVarsValues = null;
				return;
			}
		}		
		boolean needMean = true;
		boolean needSigma = false;
		boolean needMeanPlusSigma = false;
		boolean needMeanMinusSigma = false;
		boolean needMode = false;
		
		if(gsqmStatsNeeded.contains(ProbConstants.SIGMA))
			needSigma = true;
		if(gsqmStatsNeeded.contains(ProbConstants.MEAN_PLUS_SIGMA))
			needMeanPlusSigma = true;
		if(gsqmStatsNeeded.contains(ProbConstants.MEAN_MINUS_SIGMA))
			needMeanMinusSigma = true;
		if(gsqmStatsNeeded.contains(ProbConstants.MODE))
			needMode = true;
	
		ArrayList<BuildingClassification> edBuildings = ed.getBuildings();
		double ba[] = new double[edBuildings.size()];
		int n = 0;
		double maxVal = -1*Double.MAX_VALUE;
		double minVal = Double.MIN_VALUE;
		for(Iterator <BuildingClassification> i = edBuildings.iterator(); i.hasNext();){
			ba[n]= i.next().getGsqm();
			if(needMode){
				if(maxVal < ba[n])
					maxVal = ba[n];
				if(minVal > ba[n])
					minVal = ba[n];
			}
			n++;
		}		

		//** determine bins and freq and normalize area
		double bins[] = new double[50];
		double gsqmEDMean = 0;
		int freq[] = new int[50];
		double inc = (maxVal - minVal)/50;
		if(needMode){
			for(int z = 1; z < bins.length; z++){
				bins[z-1] = (inc*(double)z);//** linspace(min:max, 50)
			}
		}
		int maxFreq = -1;
		int maxFreqIndex = 0;
		for(int i=0; i < ba.length; i++){
			if(needMean){
				gsqmEDMean += ba[i];
			}
			
			if(needMode){
				for(int h = 0; h < bins.length;h++){
					if(h == 0){
						if(ba[i] < bins[0]){
							freq[0]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;
						}
					}
					if(bins[h] <= ba[i]){
						if( h+1 < bins.length && ba[i] < bins[h+1]){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;	
						}
						else if( h+1 == bins.length){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
						}							
					}					
				}
			}
		}
		
		if(needMean || needSigma || needMeanPlusSigma|| needMeanMinusSigma){	
			gsqmEDMean /= ba.length;
			gsqmVarsValues.put(ProbConstants.MEAN, new Double(gsqmEDMean));
		}
		if(needMode){
			gsqmVarsValues.put(ProbConstants.MODE, new Double(bins[maxFreqIndex]));
		}						
				
		double std = 0;
	
		if(needSigma || needMeanPlusSigma|| needMeanMinusSigma){
			for(int i=0; i < ba.length; i++)			
				std += Math.pow(gsqmEDMean - ba[i], 2);
			std = Math.sqrt(std/ba.length);
			if(needSigma)
				gsqmVarsValues.put(ProbConstants.SIGMA, new Double(std));
			if(needMeanPlusSigma)
				gsqmVarsValues.put(ProbConstants.MEAN_PLUS_SIGMA, new Double(gsqmEDMean+std));
			if(needMeanMinusSigma)
				gsqmVarsValues.put(ProbConstants.MEAN_MINUS_SIGMA, new Double(gsqmEDMean-std));
		}							
	}
	
	
	private void doIsoperimetricQ(){

		if(isoperimetricQStatsNeeded == null){
			isoperimetricQStatsNeeded = probParams.getRequiredStatistics(ProbConstants.BSR);
			if(isoperimetricQStatsNeeded == null){
				isoperimetricQVarsValues = null;
				return;
			}
		}		
		boolean needMean = true;
		boolean needSigma = false;
		boolean needMeanPlusSigma = false;
		boolean needMeanMinusSigma = false;
		boolean needMode = false;
		
		if(isoperimetricQStatsNeeded.contains(ProbConstants.SIGMA))
			needSigma = true;
		if(isoperimetricQStatsNeeded.contains(ProbConstants.MEAN_PLUS_SIGMA))
			needMeanPlusSigma = true;
		if(isoperimetricQStatsNeeded.contains(ProbConstants.MEAN_MINUS_SIGMA))
			needMeanMinusSigma = true;
		if(isoperimetricQStatsNeeded.contains(ProbConstants.MODE))
			needMode = true;
	
		ArrayList<BuildingClassification> edBuildings = ed.getBuildings();
		double q[] = new double[edBuildings.size()];
		double maxVal = -1*Double.MAX_VALUE;
		double minVal = Double.MIN_VALUE;
		int n = 0;
		for(Iterator <BuildingClassification> i = edBuildings.iterator(); i.hasNext();){
			q[n]= i.next().getIsoperimetricQ();
			if(needMode){
				if(maxVal < q[n])
					maxVal = q[n];
				if(minVal > q[n])
					minVal = q[n];
			}
			n++;
		}		

		//** determine bins and freq and normalize area
		double bins[] = new double[50];
		double qMean = 0;
		int freq[] = new int[50];
		double inc = (maxVal - minVal)/50;
		if(needMode){
			for(int z = 1; z < bins.length; z++){
				bins[z-1] = (inc*(double)z);//** linspace(min:max, 50)
			}
		}
		int maxFreq = -1;
		int maxFreqIndex = 0;
		for(int i=0; i < q.length; i++){
			if(needMean){
				qMean += q[i];
			}
			
			if(needMode){
				for(int h = 0; h < bins.length;h++){
					if(h == 0){
						if(q[i] < bins[0]){
							freq[0]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;
						}
					}
					if(bins[h] <= q[i]){
						if( h+1 < bins.length && q[i] < bins[h+1]){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;	
						}
						else if( h+1 == bins.length){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
						}							
					}					
				}
			}			
		}
		
		if(needMean || needSigma || needMeanPlusSigma|| needMeanMinusSigma){	
			qMean /= q.length;
			isoperimetricQVarsValues.put(ProbConstants.MEAN, new Double(qMean));
		}
		if(needMode){
			isoperimetricQVarsValues.put(ProbConstants.MODE, new Double(bins[maxFreqIndex]));
		}			
		
		
				
		double std = 0;
	
		if(needSigma || needMeanPlusSigma|| needMeanMinusSigma){
			for(int i=0; i < q.length; i++)			
				std += Math.pow(qMean - q[i], 2);
			std = Math.sqrt(std/q.length);
			if(needSigma)
				isoperimetricQVarsValues.put(ProbConstants.SIGMA, new Double(std));
			if(needMeanPlusSigma)
				isoperimetricQVarsValues.put(ProbConstants.MEAN_PLUS_SIGMA, new Double(qMean+std));
			if(needMeanMinusSigma)
				isoperimetricQVarsValues.put(ProbConstants.MEAN_MINUS_SIGMA, new Double(qMean-std));
		}
	}
	
	
	private void doIsoperimetricQNeighborhood(){
		//** TODO: if we're not gonna shrink circle with t then do this ONCE with global variables!!!! 

		//ArrayList<String> isoPQNStatsNeeded;
		if(isoPQNStatsNeeded == null){
			isoPQNStatsNeeded = probParams.getRequiredStatistics(ProbConstants.BSRN);
			if(isoPQNStatsNeeded == null){
				isoPQNVarsValues = null;
				return;
			}
		}		
		boolean needMean = true;
		boolean needSigma = false;
		boolean needMeanPlusSigma = false;
		boolean needMeanMinusSigma = false;
		boolean needMode = false;
		
		if(isoPQNStatsNeeded.contains(ProbConstants.SIGMA))
			needSigma = true;
		if(isoPQNStatsNeeded.contains(ProbConstants.MEAN_PLUS_SIGMA))
			needMeanPlusSigma = true;
		if(isoPQNStatsNeeded.contains(ProbConstants.MEAN_MINUS_SIGMA))
			needMeanMinusSigma = true;
		if(isoPQNStatsNeeded.contains(ProbConstants.MODE))
			needMode = true;		
	
		double q[] = new double[neighbors.size()];
		double maxVal = -1*Double.MAX_VALUE;
		double minVal = Double.MIN_VALUE;
		int n = 0;
		for(Iterator <BuildingClassification> i = neighbors.iterator(); i.hasNext();){
			BuildingClassification t = i.next();
			q[n]= t.getIsoperimetricQ();
			if(needMode){
				if(maxVal < q[n])
					maxVal = q[n];
				if(minVal > q[n])
					minVal = q[n];
			}
			n++;
		}
		

		//** determine bins and freq and normalize area
		double bins[] = new double[50];
		double qNMean = 0;
		int freq[] = new int[50];
		double inc = (maxVal - minVal)/50;
		if(needMode){
			for(int z = 1; z < bins.length; z++){
				bins[z-1] = (inc*(double)z);//** linspace(min:max, 50)
			}
		}
		int maxFreq = -1;
		int maxFreqIndex = 0;
		for(int i=0; i < q.length; i++){
			if(needMean){
				qNMean += q[i];
			}
			
			if(needMode){
				for(int h = 0; h < bins.length;h++){
					if(h == 0){
						if(q[i] < bins[0]){
							freq[0]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;
						}
					}
					if(bins[h] <= q[i]){
						if( h+1 < bins.length && q[i] < bins[h+1]){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;	
						}
						else if( h+1 == bins.length){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
						}							
					}					
				}
			}			
		}
		
		if(needMean || needSigma || needMeanPlusSigma|| needMeanMinusSigma){	
			qNMean /= q.length;
			isoPQNVarsValues.put(ProbConstants.MEAN, new Double(qNMean));
		}
		if(needMode){
			isoPQNVarsValues.put(ProbConstants.MODE, new Double(bins[maxFreqIndex]));
		}			
		
		
				
		double std = 0;
		isoperimetricQNeighborhood = 0;
		//** see documentation for explanation of the following which measures simlarity of the neighbors
		for(int i=0; i < q.length; i++){
			if(needSigma || needMeanPlusSigma|| needMeanMinusSigma){
				std += Math.pow(qNMean - q[i], 2);
			}
			isoperimetricQNeighborhood += Math.abs(q[i]-isoperimetricQ);	
		}
		if(needSigma || needMeanPlusSigma|| needMeanMinusSigma){
			std = Math.sqrt(std/q.length);
			if(needSigma)
				isoPQNVarsValues.put(ProbConstants.SIGMA, new Double(std*0.75));//** why 0.75 ?? PROVE IT TODO
			if(needMeanPlusSigma)
				isoPQNVarsValues.put(ProbConstants.MEAN_PLUS_SIGMA, new Double(qNMean+std));
			if(needMeanMinusSigma)
				isoPQNVarsValues.put(ProbConstants.MEAN_MINUS_SIGMA, new Double(qNMean-std));
		}
		isoperimetricQNeighborhood = isoperimetricQNeighborhood/(q.length-1);
							
		
	}
	
	private void doGsqmNeighborhood(){
		//** TODO: if we're not gonna shrink circle with t then do this ONCE with global variables!!!!

		if(gsqmNStatsNeeded == null){
			gsqmNStatsNeeded = probParams.getRequiredStatistics(ProbConstants.BAN);
			if(gsqmNStatsNeeded == null){
				gsqmNeighVarsValues = null;
				return;
			}
		}		
		boolean needMean = true;
		boolean needSigma = false;
		boolean needMeanPlusSigma = false;
		boolean needMeanMinusSigma = false;
		boolean needMode = false;

		if(gsqmNStatsNeeded.contains(ProbConstants.SIGMA)){
			needSigma = true;
		}
		if(gsqmNStatsNeeded.contains(ProbConstants.MEAN_PLUS_SIGMA))
			needMeanPlusSigma = true;
		if(gsqmNStatsNeeded.contains(ProbConstants.MEAN_MINUS_SIGMA))
			needMeanMinusSigma = true;
		if(gsqmNStatsNeeded.contains(ProbConstants.MODE))
			needMode = true;
		
	
		double gsqmN[] = new double[neighbors.size()];
		int n = 0;
		double maxVal = -1*Double.MAX_VALUE;
		double minVal = Double.MIN_VALUE;
		for(Iterator <BuildingClassification> i = neighbors.iterator(); i.hasNext();){
			BuildingClassification t = i.next();
			gsqmN[n]= t.getGsqm();
			if(needMode){
				if(maxVal < gsqmN[n])
					maxVal = gsqmN[n];
				if(minVal > gsqmN[n])
					minVal = gsqmN[n];
			}
			n++;
		}
		

		//** determine bins and freq and normalize area
		double bins[] = new double[50];
		double gsqmNMean = 0;
		int freq[] = new int[50];
		double inc = (maxVal - minVal)/50;
		if(needMode){
			for(int z = 1; z <= bins.length; z++){
				bins[z-1] = (inc*(double)z);//** linspace(min:max, 50)
			}
		}
		int maxFreq = -1;
		int maxFreqIndex = 0;
		for(int i=0; i < gsqmN.length; i++){
			if(needMean){
				gsqmNMean += gsqmN[i];
			}
			if(needMode){
				for(int h = 0; h < bins.length;h++){
					if(h == 0){
						if(gsqmN[i] < bins[0]){
							freq[0]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;
						}
					}
					if(bins[h] <= gsqmN[i]){
						if( h+1 < bins.length && gsqmN[i] < bins[h+1]){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
							break;	
						}
						else if( h+1 == bins.length){
							freq[h]++;
							if(freq[h] > maxFreq){
								maxFreq = freq[h];
								maxFreqIndex = h;
							}
						}							
					}					
				}
			}			
		}
			
		if(needMean || needSigma || needMeanPlusSigma|| needMeanMinusSigma){	
			gsqmNMean /= gsqmN.length;
			gsqmNeighVarsValues.put(ProbConstants.MEAN, new Double(gsqmNMean));
		}
		if(needMode){	
			gsqmNeighVarsValues.put(ProbConstants.MODE, new Double(bins[maxFreqIndex]));
		}			
				
				
		double std = 0;
		gsqmNeighborhood = 0;
		//** see documentation for explanation of the following which measures simlarity of the neighbors
		for(int i=0; i < gsqmN.length; i++){
			if(needSigma || needMeanPlusSigma|| needMeanMinusSigma){
				std += Math.pow(gsqmNMean - gsqmN[i], 2);
			}
			
			gsqmNeighborhood += Math.abs(gsqmN[i]-gsqm);	
		}
		if(needSigma || needMeanPlusSigma|| needMeanMinusSigma){
			std = Math.sqrt(std/gsqmN.length);
			if(needSigma)
				gsqmNeighVarsValues.put(ProbConstants.SIGMA, new Double(std));
			if(needMeanPlusSigma)
				gsqmNeighVarsValues.put(ProbConstants.MEAN_PLUS_SIGMA, new Double(gsqmNMean+std));
			if(needMeanMinusSigma)
				gsqmNeighVarsValues.put(ProbConstants.MEAN_MINUS_SIGMA, new Double(gsqmNMean-std));
		}
		gsqmNeighborhood = gsqmNeighborhood/(gsqmN.length-1);
		
	}
	
	private void doIncomeZone(){

		if(inzStatsNeeded == null){				
			inzStatsNeeded = probParams.getRequiredStatistics(ProbConstants.INZ);			
			if(inzStatsNeeded == null){
				return;
			}
		}	
	
		
		if(inzStatsNeeded.contains(ProbConstants.UPPER_NINETY_PERCENTILE)){
			inzVarsValues.put(ProbConstants.UPPER_NINETY_PERCENTILE, new Double(ed.getUpper90PercentileUnimpLandVal()));
		}							
		if(inzStatsNeeded.contains(ProbConstants.UPPER_EIGHTY_PERCENTILE)){
			inzVarsValues.put(ProbConstants.UPPER_EIGHTY_PERCENTILE, new Double(ed.getUpper80PercentileUnimpLandVal()));
		}			
		if(inzStatsNeeded.contains(ProbConstants.UPPER_SEVENTY_PERCENTILE)){
			inzVarsValues.put(ProbConstants.UPPER_SEVENTY_PERCENTILE, new Double(ed.getUpper70PercentileUnimpLandVal()));
		}
		if(meanUnImpLandValue == 0 && ed != null)
			meanUnImpLandValue = ed.getMeanUnimpLandVal();
	}
	
	private void doNeighbouringStructTypes(){
		//
		int structCount[]  = new int[neighborStructs.length];
		for(int i = 0; i < structCount.length; i++)
			structCount[i] = 0;
		for(Iterator <BuildingClassification> i = neighbors.iterator(); i.hasNext();){
			BuildingClassification n = i.next(); 
			if(n.getMostLikelyStructure() > 0)
				structCount[n.getMostLikelyStructure()]++;
		}
		
		/** We want these ratios to boost already high probabilities, not squash them.
		 *  Values will be between 0.8 and 1 and will be scaled by a sigmoid function. 
		 */
		for(int i = 0; i < structCount.length; i++){
			// k = 40, x0 = 0.85, C = 0.8
			neighborStructs[i] = LogisticFunction.logisticFunction( 
			(double)structCount[i]/(double)neighbors.size(),
			40, 0.85, 0.8, false);
		}
		
		//** this is a catch for PC1 structures in Portmore that are joined together and have a large building footprint
		//** If RM2 or PC1 is greater than other structures we'll supress all other structures but PC1 and RM2
		if(probParams.getMostPrevalentyStructureType() == ProbConstants.PC1){
			double PC1orRM2Weight = 0;
			double maxWeight = 0;
			for(int i = 0; i < structCount.length; i++){
				if(neighborStructs[i] == ProbConstants.PC1
						|| neighborStructs[i] == ProbConstants.RM2){
					PC1orRM2Weight = neighborStructs[i];
				}
				else if(neighborStructs[i] >  maxWeight){
					maxWeight = neighborStructs[i];
				}
				
			}
			if(PC1orRM2Weight > maxWeight){
				neighborStructs[ProbConstants.C1] = neighborStructs[ProbConstants.C1]*0.8;
				neighborStructs[ProbConstants.C2] = neighborStructs[ProbConstants.C2]*0.8;
				neighborStructs[ProbConstants.RM] = neighborStructs[ProbConstants.RM];
				neighborStructs[ProbConstants.RM1] = neighborStructs[ProbConstants.RM1]*0.8;
				neighborStructs[ProbConstants.RM2] = neighborStructs[ProbConstants.RM2];
				neighborStructs[ProbConstants.PC1] = neighborStructs[ProbConstants.PC1];
				neighborStructs[ProbConstants.W1] = neighborStructs[ProbConstants.W1]*0.8;
				neighborStructs[ProbConstants.S1] = neighborStructs[ProbConstants.S1]*0.8;
				
			}
		}
		
		neighborStructProb.c1 = neighborStructs[ProbConstants.C1];
		neighborStructProb.c2 = neighborStructs[ProbConstants.C2];
		neighborStructProb.rm = neighborStructs[ProbConstants.RM];
		neighborStructProb.rm1 = neighborStructs[ProbConstants.RM1];
		neighborStructProb.rm2 = neighborStructs[ProbConstants.RM2];
		neighborStructProb.pc1 = neighborStructs[ProbConstants.PC1];
		neighborStructProb.w1 = neighborStructs[ProbConstants.W1];
		neighborStructProb.s1 = neighborStructs[ProbConstants.S1];

	}
	
	private void createNeighborhoodCircle(){
		//** lets define neighborhoodRadius as a quarter the longest edge of the bounding box of the ed this building is in
		double edy = ed.getFeature().getBounds().getHeight();
		double edx = ed.getFeature().getBounds().getWidth();
		double neighborhoodRadius = -1;
		if( edx > edy)
			neighborhoodRadius = edy/3;	
		else
			neighborhoodRadius = edx/3;

		
		//** build circle geometry
		Point p  = ((Geometry)this.getFeature().getDefaultGeometry()).getCentroid();		
		
        Coordinate coords[] = new Coordinate[50]; //** hard code to use only 50 points   
        double inc = 2*Math.PI/50;
        int n = -1;
        for(double a = -Math.PI;a < Math.PI; a+=inc){        
            n++;
            coords[n] = new Coordinate(p.getX()+(neighborhoodRadius*Math.sin(a)),
            		p.getY()+(neighborhoodRadius*Math.cos(a)));
        }       
        coords[n] = coords[0];//close circle   

        GeometryFactory fact = new GeometryFactory();
        try{
            LinearRing linear = new GeometryFactory().createLinearRing(coords);
            circle = new Polygon(linear, null, fact);
        }catch(IllegalArgumentException iaex){        	
        	iaex.printStackTrace();
        }
	}
	
	private void createFixedRadiusCircle(double neighborhoodRadius){

		//** build circle geometry
		Point p  = ((Geometry)this.getFeature().getDefaultGeometry()).getCentroid();		
		
        Coordinate coords[] = new Coordinate[50]; //** hard code to use only 50 points   
        double inc = 2*Math.PI/50;
        int n = -1;
        for(double a = -Math.PI;a < Math.PI; a+=inc){        
            n++;
            coords[n] = new Coordinate(p.getX()+(neighborhoodRadius*Math.sin(a)),
            		p.getY()+(neighborhoodRadius*Math.cos(a)));
        }       
        coords[n] = coords[0];//close circle   

        GeometryFactory fact = new GeometryFactory();
        try{
            LinearRing linear = new GeometryFactory().createLinearRing(coords);
            fixedRadiusCircle = new Polygon(linear, null, fact);
        }catch(IllegalArgumentException iaex){        	
        	iaex.printStackTrace();
        }
	}
	
		
		
	//** GETTERS AND SETTERS BEYOND HERE **	
	public GeoHash getAllBuildings() {
		return allBuildings;
	}

	public void setAllBuildings(GeoHash allBuildings) {
		this.allBuildings = allBuildings;
	}
	
	public EDStructTypeRankingFunction getED() {
		return ed;
	}


	public void setED(EDStructTypeRankingFunction ed) {
		this.ed = ed;		
	}
	

	public BuildingProbParameters getProbParams() {
		return probParams;
	}	
		
    public int getNoOfVertices() {
		return noOfVertices;
	}

	public void setNoOfVertices(int noOfVertices) {
		this.noOfVertices = noOfVertices;
	}

	public double getIsoperimetricQ() {
		return isoperimetricQ;
	}

	public void setIsoperimetricQ(double isoperimetricQ) {	
		this.isoperimetricQ = isoperimetricQ;
	}

	public double getMajorRoadIndex() {
		return majorRoadIndex;
	}

	public void setMajorRoadIndex(double majorRoadIndex) {
		this.majorRoadIndex = majorRoadIndex;
	}

	public double getGsqm() {
		return gsqm;
	}


	public void setGsqm(double gsqm) {
		this.gsqm = gsqm;
	}	
	
	public double getMeanUnImpLandValue() {
		return meanUnImpLandValue;
	}

	public void setMeanUnImpLandValue(double meanUnImpLandValue) {
		this.meanUnImpLandValue = meanUnImpLandValue;
	}

	/**
	 * 
	 * Will return SimpleFeature with gis schema of gisSchemas/ergo-uwiBuildingClassification_1.0.xsd
	 * @return
	 */
	public SimpleFeature getFeature() {
		return feature;
	}

	public double getStructuresProbability(int struct){
		return structProbs[struct];
	}
	
	public void setStructuresProbability(int struct, double probability){
		likelyStructureProb = probability;
		structProbs[struct] = probability;
	}
	
	public double[] getAllStructuresProbabilities(){
		return structProbs;
	}
	

	/**
	 * Returns constants from ProbConstants
	 * @return
	 */
	public int getMostLikelyStructure(){		
		return likelyStructure;
	}
	
	/**
	 * 
	 * Accepts constants from ProbConstants
	 * @param likelyStructure
	 */
	public void setMostLikelyStructure(int likelyStructure){		
		this.likelyStructure = likelyStructure;
		this.likelyStructureStrHAZUS = ProbConstants.getStructureType(likelyStructure);
		if(likelyStructureStrHAZUS.matches("C1")
				|| likelyStructureStrHAZUS.matches("C2"))
			System.out.println("is a "+likelyStructureStrHAZUS+" but structype is "+likelyStructure );
	}
	
	public String getMostLikelyStructureString(){
		return likelyStructureStrHAZUS;
	}
	
	
	public String getBldgID() {
		return bldgID;
	}

	public void setBldgID(String bldgID) {
		this.bldgID = bldgID;
	}

	//** GeoHashable methods
	/* (non-Javadoc)
     * @see com.uwiseismic.util.geohash.GeoHashable#getCentroid()
     */
    public Point getCentroid() {
        return ((Geometry)feature.getDefaultGeometry()).getCentroid();
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.util.geohash.GeoHashable#getEnvelope()
     */
    public Geometry getEnvelope() {
        return ((Geometry)feature.getDefaultGeometry()).getEnvelope();
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.util.geohash.GeoHashable#getGeometry()
     */
    public Geometry getGeometry() {
        return (Geometry)feature.getDefaultGeometry();
    }
	
}
