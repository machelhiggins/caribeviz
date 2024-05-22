package com.uwiseismic.ergo.buildingclassifier;

import java.util.ArrayList;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.geohash.GeoHashable;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class EDStructTypeRankingFunction implements GeoHashable{

	//** feature this represents
	private SimpleFeature feature;
	private ArrayList <BuildingClassification> buildings = new ArrayList<BuildingClassification>(); 
	
	private String ID;
	private double rmOneScore = 0;
	private double rmOneScoreMax = 1;
	private double rmTwoScore = 0;
	private double rmTwoScoreMax = 1;
	private double w1Score = 0;
	private double w1ScoreMax = 1;
	private double pcScore = 0;
	private double pcScoreMax = 1;
	private double cOneScore = 0;
	private double cOneScoreMax = 1;
	private double cTwoScore = 0;
	private double cTwoScoreMax = 1;
	private double cThreeScore = 0;
	private double cThreeScoreMax = 1;
	private double urmScore = 0;
	private double urmScoreMax = 1;
	private double sScore = 0;
	private double sScoreMax = 1;	
	
	
	private boolean recalculateScores = false;
	
	
	/** ALL OF THE FOLLOWING VARIABLES WILL GET NORMALIZED AT SOME POINT OUTSIDE THIS CLASS
	 */
	//** building count
	private double bldgCount = 0;
	//** pop / area
	private double popArea = 0;	
	//** area / building count
	private double areaBldgCount = 0;
	//** pop / building count
	private double popBldgCount = 0;	
	//** mean ground square feet of building footprints in this ED
	private double meanGSQFT = 0;
	//** STDEV of ground square feet of building footprints in this ED
	private double stdGSQFT = 0;
	//** the mean ground square meters	
	private double meanSQM = 0;
	//** the avearge of the ground square meters
	private double stdSQM = 0;
	//** the rand of the ground square meters for buildings for the ED
	private double sqmRange = 0;
	//** total population of this ED
	private double pop =0;
	//** mean unimproved land value for plots in this ED
	private double meanUnimpLandVal = 0;
	//** stdev unimproved land value for plots in this ED
	private double stdUnimpLandVal = 0;
	//** Upper 90% unimproved land value of all EDS
	private double upper90PercentileUnimpLandVal = 0;
	private double upper80PercentileUnimpLandVal = 0;
	private double upper70PercentileUnimpLandVal = 0;
	
	private double nonNormalizedMeanGSQFT = 0;
	//** STDEV of ground square feet of building footprints in this ED
	private double nonNormalizedStdGSQFT = 0;
	
	//** STRUCTURE RELATIONSHIP VARIABLES
	//**
	
	/**
	 * this is the weight added to the contributions of structure relationships.
	 * Its the 2 sigma in the function in the function 1 - exp(-x^2/2sigma)
	 */
	private double structureRelationWeight = 0.35;
			
	//** fraction of total rm1 for ED 
	private double fractionalRMOne = 0;	
	//** fraction of total rm2 for ED
	private double fractionalRMTwo = 0;
	//** fraction of total w1 for ED
	private double fractionalWOne = 0;
	//** fraction of total PC2 for ED
	private double fractionalPCOne= 0;
	//** fraction of total PC1 for ED
	private double fractionalPCTwo = 0;
	//** fraction of total c1
	private double fractionalCOne = 0;
	//** fraction of total c2
	private double fractionalCTwo = 0;
	//** fraction of total c3
	private double fractionalCThree = 0;
	//** fraction of total urml
	private double fractionalURM = 0;
	//** fraction of total s1
	private double fractionalS = 0;

	
	//** GETTERS AND SETTERS	
	public SimpleFeature getFeature() {
		return feature;
	}
	public void setFeature(SimpleFeature feature) {
		this.feature = feature;
	}
	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}
	public double getBldgCount() {
		return bldgCount;
	}
	public void setBldgCount(double bldgCount) {
		recalculateScores = true;
		this.bldgCount = bldgCount;
	}
	public double getPopArea() {
		return popArea;
	}
	public void setPopArea(double popArea) {
		recalculateScores = true;
		this.popArea = popArea;
	}
	public double getAreaBldgCount() {
		return areaBldgCount;
	}
	public void setAreaBldgCount(double areaBldgCount) {
		recalculateScores = true;
		this.areaBldgCount = areaBldgCount;
	}
	public double getPopBldgCount() {
		return popBldgCount;
	}
	public void setPopBldgCount(double popBldgCount) {
		recalculateScores = true;
		this.popBldgCount = popBldgCount;
	}
	public double getMeanGSQFT() {
		return meanGSQFT;
	}
	public void setMeanGSQFT(double meanGSQFT) {
		recalculateScores = true;
		this.meanGSQFT = meanGSQFT;
	}
	public double getStdGSQFT() {
		return stdGSQFT;
	}
	public void setStdGSQFT(double stdGSQFT) {
		recalculateScores = true;
		this.stdGSQFT = stdGSQFT;
	}
	
	public double getMeanSQM() {
		return meanSQM;
	}
	public void setMeanSQM(double meanSQM) {
		this.meanSQM = meanSQM;
	}
	public double getSqmRange() {
		return sqmRange;
	}
	public void setSqmRange(double sqmRange) {
		this.sqmRange = sqmRange;
	}
	public double getStdSQM() {
		return stdSQM;
	}
	public void setStdSQM(double stdSQM) {
		this.stdSQM = stdSQM;
	}
	public double getNonNormalizedMeanGSQFT() {
		return nonNormalizedMeanGSQFT;
	}
	public void setNonNormalizedMeanGSQFT(double nonNormalizedMeanGSQFT) {
		this.nonNormalizedMeanGSQFT = nonNormalizedMeanGSQFT;
	}
	public double getNonNormalizedStdGSQFT() {
		return nonNormalizedStdGSQFT;
	}
	public void setNonNormalizedStdGSQFT(double nonNormalizedStdGSQFT) {
		this.nonNormalizedStdGSQFT = nonNormalizedStdGSQFT;
	}
	
	public double getPop() {
		return pop;
	}
	public void setPop(double pop) {
		recalculateScores = true;
		this.pop = pop;
	}
	public double getMeanUnimpLandVal() {
		return meanUnimpLandVal;
	}
	public void setMeanUnimpLandVal(double meanUnimpLandVal) {
		recalculateScores = true;
		this.meanUnimpLandVal = meanUnimpLandVal;
	}
	public double getStdUnimpLandVal() {
		return stdUnimpLandVal;
	}
	public void setStdUnimpLandVal(double stdUnimpLandVal) {
		recalculateScores = true;
		this.stdUnimpLandVal = stdUnimpLandVal;
	}	
	public double getUpper90PercentileUnimpLandVal() {
		return upper90PercentileUnimpLandVal;
	}
	public void setUpper90PercentileUnimpLandVal(double upperPercentileUnimpLandVal) {
		this.upper90PercentileUnimpLandVal = upperPercentileUnimpLandVal;
	}
	public double getUpper80PercentileUnimpLandVal() {
		return upper80PercentileUnimpLandVal;
	}
	public void setUpper80PercentileUnimpLandVal(double upperPercentileUnimpLandVal) {
		this.upper80PercentileUnimpLandVal = upperPercentileUnimpLandVal;
	}
	public double getUpper70PercentileUnimpLandVal() {
		return upper70PercentileUnimpLandVal;
	}
	public void setUpper70PercentileUnimpLandVal(double upperPercentileUnimpLandVal) {
		this.upper70PercentileUnimpLandVal = upperPercentileUnimpLandVal;
	}
	public double getStructureRelationWeight() {
		return structureRelationWeight;
	}
	public void setStructureRelationWeight(double structureRelationWeight) {
		recalculateScores = true;
		this.structureRelationWeight = structureRelationWeight;
	}
	
	
	public boolean isRecalculateScores() {
		return recalculateScores;
	}
	public void setRecalculateScores(boolean recalculateScores) {
		this.recalculateScores = recalculateScores;
	}
	
	public void calculateScores(){
		//recalculateScores = true;
		getCOneScore();
		getcThreeScore();
		getcTwoScore();
		getPCScore();
		getRmOneScore();
		getRmTwoScore();
		getSScore();
		getURMScore();
		getW1Score();
	}
	
	public double getFractionalRMOne() {
		return fractionalRMOne;
	}
	public void setFractionalRMOne(double fractionalRMOne) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalRMOne))
			this.fractionalRMOne = fractionalRMOne;
		else
			this.fractionalRMOne = 0;
	}
	public double getFractionalRMTwo() {
		return fractionalRMTwo;
	}
	public void setFractionalRMTwo(double fractionalRMTwo) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalRMTwo))
			this.fractionalRMTwo = fractionalRMTwo;
		else
			this.fractionalRMTwo = 0;
	}
	public double getFractionalWOne() {
		return fractionalWOne;
	}
	public void setFractionalWOne(double fractionalWOne) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalWOne))
			this.fractionalWOne = fractionalWOne;
		else
			this.fractionalWOne = 0;
	}
	public double getFractionalPCOne() {
		return fractionalPCOne;
	}
	public void setFractionalPCOne(double fractionalPCOne) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalPCOne))
			this.fractionalPCOne = fractionalPCOne;
		else
			this.fractionalPCOne = 0;
		
	}
	public double getFractionalPCTwo() {
		return fractionalPCTwo;
	}
	public void setFractionalPCTwo(double fractionalPCTwo) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalPCTwo))
			this.fractionalPCTwo = fractionalPCTwo;
		else
			this.fractionalPCTwo = 0;
		
	}
	public double getFractionalCOne() {
		return fractionalCOne;
	}
	public void setFractionalCOne(double fractionalCOne) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalCOne))
			this.fractionalCOne = fractionalCOne;
		else
			this.fractionalCOne = 0;
	}
	public double getFractionalCTwo() {
		return fractionalCTwo;
	}
	public void setFractionalCTwo(double fractionalCTwo) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalCTwo))
			this.fractionalCTwo = fractionalCTwo;
		else
			this.fractionalCTwo = 0;
		
	}
	public double getFractionalCThree() {
		return fractionalCThree;
	}
	public void setFractionalCThree(double fractionalCThree) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalCThree))
			this.fractionalCThree = fractionalCThree;
		else
			this.fractionalCThree = 0;
	
	}
	public double getFractionalURML() {
		return fractionalURM;
	}
	public void setFractionalURM(double fractionalURM) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalURM))
			this.fractionalURM = fractionalURM;
		else
			this.fractionalURM = 0;
	}
	public double getFractionalS() {
		return fractionalS;
	}
	public void setFractionalS(double fractionalS) {
		recalculateScores = true;
		if(!Double.isNaN(fractionalS))
			this.fractionalS = fractionalS;
		else
			this.fractionalS = 0;
	}
	
	
	/**
	 * Calculuates RM1 score (0 - 1) for this ED
	 * TODO :  Properly document contribution of weighted structures' prevalence relationships
	 * @return
	 */
	public double getRmOneScore() {
		if(rmOneScore == 0 || recalculateScores){
			//coeff: -545.8302, 546.2434
			 
			rmOneScore = -545.8302*meanUnimpLandVal + 546.2434* stdUnimpLandVal; 
			
			//rmOneScore = -545.8302*stdUnimpLandVal + 546.2434* meanUnimpLandVal;
			rmOneScore = rmOneScore +  
					sigmoidFunc(((fractionalCOne*0.481+0.254177)
					+(fractionalCOne*0.443+0.000005)
					+(fractionalCOne*0.229	+1.059)
					)/3);
		}
		//TODO: this corellation might not be so valid. until we know R-squared we'll 
		//return validateScore(rmOneScore/rmOneScoreMax);
		return 0.2;
	}
	
	public double getRmTwoScore() {
		//TODO: Assume 90% of buildings are RM2 so hard code this one
		/*if(rmOneScore == 0 || recalculateScores){
			//coeff: 0.6507, -0.0088,   0.0244,    0.1634
			rmTwoScore = 0.6507*popArea -0.0088*popBldgCount+ 0.0244*stdGSQFT + 0.1634*meanUnimpLandVal;
		}		
		return validateScore(rmTwoScore/rmTwoScoreMax);
		*/
		//TODO: Assume 90% of buildings are RM2 so hard code this one
		return 0.6;
	}
	
	public double getW1Score() {
		if(w1Score == 0 || recalculateScores){
			//coeff: 0.0359, 0.4478, -0.1322
			w1Score = 0.0359*pop + 0.4478*popArea + -0.1322*meanUnimpLandVal;
			w1Score = w1Score +  
					sigmoidFunc(fractionalRMTwo*4.40528);
		}
		return validateScore(w1Score/w1ScoreMax);
	}
	
	public double getPCScore() {
		if(pcScore == 0 || recalculateScores){
			//0.0841, 0.2221 -0.0786
			pcScore = 0.0841*pop + 0.2221* meanGSQFT -0.0786*stdGSQFT;
			pcScore = pcScore +  
					sigmoidFunc(fractionalS*0.481+0.254177);
		}	
		return validateScore(pcScore/pcScoreMax);
	}
	
	public double getCOneScore() {
		if(cOneScore == 0 || recalculateScores){
			//coeff: 0.0438, 0.0664, -0.0233
			cOneScore =  0.0438* stdGSQFT + 0.0664* meanUnimpLandVal + -0.0233*pop;
			cOneScore = cOneScore +  
					sigmoidFunc(((fractionalCTwo*0.778)
					+(fractionalRMTwo*5.9524  - 0.04)
					+(fractionalRMOne*4.4652 - 0.25418)
					)/3);
		}		
		return validateScore(cOneScore/cOneScoreMax);
	}	
	
	public double getcTwoScore() {
		if(cTwoScore == 0 || recalculateScores){
			//coeff: 0.0432, 0.0199
			cTwoScore = 0.0432*stdGSQFT + 0.0199*meanUnimpLandVal;
			cTwoScore = cTwoScore +  
					sigmoidFunc(((fractionalCThree*0.778)
					+(fractionalRMTwo*7.51879)
					+(fractionalRMOne*6.21607)
					+(fractionalCOne*1.2853)
					)/4);	
		}
		return validateScore(cTwoScore/cTwoScoreMax);
	}
	
	public double getcThreeScore() {
		//coeff: 0.3712, 0.5145, -0.2543, 0.0080
		if(cThreeScore == 0 || recalculateScores){
			cThreeScore = 0.3712*areaBldgCount + 0.5145*popArea -0.2543*popBldgCount+ 0.0080*stdUnimpLandVal;
			cThreeScore = cThreeScore +  
					sigmoidFunc(((fractionalRMOne* 4.366812 - 1.0391)
					+(fractionalCOne*2.695)
					+(fractionalCTwo*2.941176 + 0.11878)
					)/3);	
		}
		return validateScore(cThreeScore/cThreeScoreMax);
	}
	public double getURMScore() {
		if(urmScore == 0 || recalculateScores){
			//coeff": 0.4436, 0.0032
			urmScore = 0.4436*popArea + 0.0032* meanGSQFT;
		}
		return validateScore(urmScore/urmScoreMax);
	}
	
	public double getSScore() {
		if(sScore == 0 || recalculateScores){
			sScore = bldgCount*0.187 + 0.189197;
			sScore = sScore +  
					sigmoidFunc(((fractionalURM*4.830918)
					+(fractionalPCTwo*2.213389)
					)/2);
		}
		return validateScore(sScore/sScoreMax);
	}
	
	
	public void setcOneScore(double cOneScore) {
		this.cOneScore = cOneScore;
	}
	public void setRmOneScore(double rmOneScore) {
		this.rmOneScore = rmOneScore;
	}
	public void setRmTwoScore(double rmTwoScore) {
		this.rmTwoScore = rmTwoScore;
	}
	public void setW1Score(double w1Score) {
		this.w1Score = w1Score;
	}
	public void setPCScore(double pcScore) {
		this.pcScore = pcScore;
	}
	public void setcTwoScore(double cTwoScore) {
		this.cTwoScore = cTwoScore;
	}
	public void setcThreeScore(double cThreeScore) {
		this.cThreeScore = cThreeScore;
	}
	public void setURMScore(double urmScore) {
		this.urmScore = urmScore;
	}
	public void setSScore(double sScore) {
		this.sScore = sScore;
	}

	public void setRmOneScoreMax(double rmOneScoreMax) {
		if(rmOneScoreMax == 0)
			rmOneScoreMax = 1;
		else
			this.rmOneScoreMax = rmOneScoreMax;
	}
	public void setRmTwoScoreMax(double rmTwoScoreMax) {
		if(rmTwoScoreMax == 0)
			rmTwoScoreMax = 1;
		else
			this.rmTwoScoreMax = rmTwoScoreMax;
	}
	public void setW1ScoreMax(double w1ScoreMax) {
		if(w1ScoreMax == 0)
			w1ScoreMax = 1;
		else
			this.w1ScoreMax = w1ScoreMax;
	}
	public void setPcScoreMax(double pcScoreMax) {
		if(pcScoreMax == 0)
			pcScoreMax = 1;
		else
			this.pcScoreMax = pcScoreMax;
	}
	public void setcOneScoreMax(double cOneScoreMax) {
		if(cOneScoreMax == 0)
			cOneScoreMax = 1;
		else
			this.cOneScoreMax = cOneScoreMax;
	}
	public void setcTwoScoreMax(double cTwoScoreMax) {
		if(cTwoScoreMax == 0)
			cTwoScoreMax = 1;
		else
			this.cTwoScoreMax = cTwoScoreMax;
	}
	public void setcThreeScoreMax(double cThreeScoreMax) {
		if(cThreeScoreMax == 0)
			cThreeScoreMax = 1;
		else
			this.cThreeScoreMax = cThreeScoreMax;
	}
	public void setUrmScoreMax(double urmScoreMax) {
		if(urmScoreMax == 0)
			urmScoreMax = 1;
		else
			this.urmScoreMax = urmScoreMax;
	}
	public void setsScoreMax(double sScoreMax) {
		if(sScoreMax == 0)
			sScoreMax = 1;
		else
			this.sScoreMax = sScoreMax;
	}
	
	
	
	public StructureProbabilities getStructureScore(){
    	StructureProbabilities sp = new StructureProbabilities();
    	//** if the maximums of the scores aren't set, return 1 for all structures
    	if(rmOneScoreMax== -Double.MAX_VALUE || rmTwoScoreMax== -Double.MAX_VALUE
    			|| w1ScoreMax== -Double.MAX_VALUE || pcScoreMax== -Double.MAX_VALUE
    			|| cOneScoreMax== -Double.MAX_VALUE || cTwoScoreMax== -Double.MAX_VALUE
    			|| cThreeScoreMax== -Double.MAX_VALUE || urmScoreMax== -Double.MAX_VALUE
    			|| sScoreMax== -Double.MAX_VALUE){
			sp.c1 = 1;
	    	sp.c2 = 1;
	    	sp.pc1 = 1;
	    	sp.rm = 1;
	    	sp.rm1 = 1;
	    	sp.rm2 = 1;
	    	sp.s1 = 1;
	    	sp.w1 = 1;
    	}else{    	
	    	sp.c1 = this.getCOneScore();
	    	sp.c2 = this.getcTwoScore();
	    	sp.pc1 = this.getPCScore();
	    	sp.rm = this.getRmOneScore()*this.getRmTwoScore();
	    	sp.rm1 = this.getRmOneScore();
	    	sp.rm2 = this.getRmTwoScore();
	    	sp.s1 = this.getSScore();
	    	sp.w1 = this.getW1Score();
	    	
	    	/**
	    	 * Will scale and translate these scores to 0.8 - 1.0 so that they don't heavily influence anything else
	    	 */
	    	//** first find the range of scores
	    	double min = (float)Double.MAX_VALUE;
			double max = -1*(float)Double.MAX_VALUE;
	    	if(sp.c1 > max)
	    		max = sp.c1;
	    	if(sp.c1 < min)
	    		min = sp.c1;
	    	if(sp.c2 > max)
	    		max = sp.c2;
	    	if(sp.c2 < min)
	    		min = sp.c2;
	    	if(sp.pc1 > max)
	    		max = sp.pc1;
	    	if(sp.pc1 < min)
	    		min = sp.pc1;
	    	if(sp.rm > max)
	    		max = sp.rm;
	    	if(sp.rm < min)
	    		min = sp.rm;
	    	if(sp.rm1 > max)
	    		max = sp.rm1;
	    	if(sp.rm1 < min)
	    		min = sp.rm1;
	    	if(sp.rm2 > max)
	    		max = sp.rm2;
	    	if(sp.rm2 < min)
	    		min = sp.rm2;
	    	if(sp.s1 > max)
	    		max = sp.s1;
	    	if(sp.s1 < min)
	    		min = sp.s1;
	    	if(sp.w1 > max)
	    		max = sp.w1;
	    	if(sp.w1 < min)
	    		min = sp.w1;
	    	//** scale and translate 
			double targetRange = 0.1;
			double scaleFactor = targetRange/(max - min);
			double translate = 0.8;
			sp.c1 = (sp.c1 * scaleFactor) + translate;
			sp.c2 = (sp.c2 * scaleFactor) + translate;
			sp.pc1 = (sp.pc1 * scaleFactor) + translate;
			sp.rm = (sp.rm * scaleFactor) + translate;
			sp.rm1 = (sp.rm1 * scaleFactor) + translate;
			sp.rm2 = (sp.rm2 * scaleFactor) + translate;
			sp.s1 = (sp.s1 * scaleFactor) + translate;
			sp.w1 = (sp.w1 * scaleFactor) + translate;
			
			//** because double precision ops often goes over 1.0 when it shouldn't
			if(sp.c1 > 1) sp.c1 = 1;
			if(sp.c2 > 1) sp.c2 = 1;
			if(sp.pc1 > 1) sp.pc1 = 1;
			if(sp.rm > 1) sp.rm = 1;
			if(sp.rm1 > 1) sp.rm1 = 1;
			if(sp.rm2 > 1) sp.rm2 = 1;
			if(sp.s1 > 1) sp.s1 = 1;
			if(sp.w1 > 1) sp.w1 = 1;
    	}
    	return sp;
    }
	
    
    public ArrayList<BuildingClassification> getBuildings() {
		return buildings;
	}
	public void addBuildings(BuildingClassification building) {
		buildings.add(building);
			
	}
	
	//** GeoHashable methods	    
	/* (non-Javadoc)
     * @see com.uwiseismic.util.geohash.GeoHashable#getCentroid()
     */
    public Point getCentroid() {
    	return ((Geometry)feature.getDefaultGeometry()).getCentroid();
        //return feature.getDefaultGeometry().getCentroid();
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

	private double validateScore(double score){
		if(score < 0)
			return 0;		
		if(Double.isNaN(score))
			return 0;
				
		//return score;
		return sigmoidFunc(score);
	}
    
	private double sigmoidFunc( double x){
		if(x <= 0){
			return 0;
		}
		return 1- Math.exp(-1*x*x/structureRelationWeight);
	}
	
	
}
