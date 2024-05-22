package com.uwiseismic.ergo.occupancy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.ergo.buildingclassifier.BuildingClassification;
import com.uwiseismic.ergo.buildingclassifier.EDStructTypeRankingFunction;
import com.uwiseismic.ergo.buildingclassifier.ProbConstants;
import com.uwiseismic.gis.util.ObjectToReal;

public class Occupancy {
	
	private final org.apache.log4j.Logger logger =
			 org.apache.log4j.Logger.getLogger( this.getClass() );
	/**
	 * Will set the occupancy type (occ_type) of the feature contained
	 * in the BuildingClassification object 
	 * 
	 * @param bc
	 */
	public void determineOccupancyType(BuildingClassification bc){
		SimpleFeature feat = bc.getFeature();
		
		if(ObjectToReal.getMeBoolean(feat.getAttribute("u_set_occt")).booleanValue()){
			
			//** determine occupancy type and rates
			if(bc.getMostLikelyStructure() == ProbConstants.RM1){
				EDStructTypeRankingFunction ed = bc.getED();
				if(ed == null){
					feat.setAttribute("occ_type", "RES1");//** default to RES1	
				}
				else if(bc.getGsqm() > ed.getNonNormalizedMeanGSQFT()*0.092903 //** convert to sqmeters
						&& bc.getGsqm() < (ed.getNonNormalizedMeanGSQFT()*0.092903 + ed.getNonNormalizedStdGSQFT()*0.092903)){
					feat.setAttribute("occ_type", "RES3");
				}
				else
					feat.setAttribute("occ_type", "COM4");
			}
			else if(bc.getMostLikelyStructure() == ProbConstants.RM2){
				feat.setAttribute("occ_type", "RES1");
			}
			else if(bc.getMostLikelyStructure() == ProbConstants.C1 
					|| bc.getMostLikelyStructure() == ProbConstants.C2){									
				feat.setAttribute("occ_type", "COM4");
				
			}
			else if(bc.getMostLikelyStructure() == ProbConstants.PC1){
				
				feat.setAttribute("occ_type", "RES1");
				
			}
			else if(bc.getMostLikelyStructure() == ProbConstants.PC2){
				
				feat.setAttribute("occ_type", "RES3");
				
			}
			else if(bc.getMostLikelyStructure() == ProbConstants.W1){
				
				feat.setAttribute("occ_type", "RES1");
				
			}
			else if(bc.getMostLikelyStructure() == ProbConstants.S1){
				
				feat.setAttribute("occ_type", "IND2");
				
			}				
			else
				logger.warn("Didn't set occupancy type for feature (building id "+feat.getAttribute("bldg_id")+" ) of stuct type "+feat.getAttribute("struct_typ") 
					+ "\t"+bc.getMostLikelyStructure());
			feat.setAttribute("u_set_occt", new Boolean(false).toString());
		}
	}
	
	public void determineOccupancyRates(EDStructTypeRankingFunction ed){
		
		//** Check that ED attributes set
		if(ed.getFeature().getAttribute("male") == null)
			logger.error("ED had no males attribute is null or missing");
		if(ed.getFeature().getAttribute("female") == null)
			logger.error("ED had no female attribute is null or missing");
		if(ed.getFeature().getAttribute("male_u16") == null)
			logger.error("ED had no male_u16 attribute is null or missing");
		if(ed.getFeature().getAttribute("female_u16") == null)
			logger.error("ED had no female_u16 attribute is null or missing");
		
		int totalMale = ObjectToReal.getMeInteger(ed.getFeature().getAttribute("male")).intValue();
		int totalFemale = ObjectToReal.getMeInteger(ed.getFeature().getAttribute("female")).intValue();
		int totalMaleChildren = ObjectToReal.getMeInteger(ed.getFeature().getAttribute("male_u16")).intValue();
		int totalFemaleChildren = ObjectToReal.getMeInteger(ed.getFeature().getAttribute("female_u16")).intValue();
		ArrayList <BuildingClassification> bcs = ed.getBuildings();
		
		
		ArrayList <SimpleFeature> res1Structs = new ArrayList<SimpleFeature>();		
		ArrayList <SimpleFeature> res2Structs = new ArrayList<SimpleFeature>();
		ArrayList <SimpleFeature> largeRes1Res2Structs = new ArrayList<SimpleFeature>();		
		int userSetMaleNightRates = 0;
		int userSetFemaleNightRates = 0;
		int userSetChildNightRates = 0;						

		//** group res1 and res2 structures and set occupany rate for COM4 and IND2
		//** and count user set values
		for(Iterator <BuildingClassification> i = bcs.iterator(); i.hasNext();){
			BuildingClassification bc = i.next();
			SimpleFeature feat = bc.getFeature();
			String occType = (String)feat.getAttribute("occ_type");
			if(occType == null){
				return;
			}
			occType = occType.trim();
			
			if(ObjectToReal.getMeBoolean(feat.getAttribute("u_set_occr")).booleanValue()){	
				
				if(occType.matches("RES1")
						|| occType.matches("RES3")){
				
					if(feat.getAttribute("occ_nite_m") != null)
						userSetMaleNightRates += ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_m")).intValue();
					if(feat.getAttribute("occ_nite_f") != null)
						userSetFemaleNightRates += ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_f")).intValue();
					if(feat.getAttribute("occ_nite_c") != null)
						userSetChildNightRates += ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_c")).intValue();
					
				}								
			}
			else if(occType != null){
				if(occType.matches("RES1")){	
					if(bc.getGsqm() > (ed.getNonNormalizedMeanGSQFT()+ed.getNonNormalizedStdGSQFT())*0.092903
							&& bc.getGsqm() > 2000)
						
						largeRes1Res2Structs.add(feat);					
					else
						res1Structs.add(feat);
					
				}
				else if(occType.matches("RES3")){

					if(bc.getGsqm() > (ed.getNonNormalizedMeanGSQFT()+ed.getNonNormalizedStdGSQFT())*0.092903)
						largeRes1Res2Structs.add(feat);					
					else
						res2Structs.add(feat);					
					
				}
				/** set COM4 and IND2 at this point since its not reliant on pop data of ED
				 * 
				 */
				else if(occType.matches("COM4")){
					
					//** Day/Night time from HAZUS tables  
					if(bc.getGsqm() > 5000){
						//** from docs, hard coded to 1000 adults
						feat.setAttribute("occ_day_m", new Integer(250));
						feat.setAttribute("occ_day_f", new Integer(250));
						feat.setAttribute("occ_day_c", new Integer(0));
						feat.setAttribute("occ_nite_m", new Integer((int) ((double)250*0.1)));
						feat.setAttribute("occ_nite_f", new Integer((int) ((double)250*0.1)));
						feat.setAttribute("occ_nite_c", new Integer(0));
					}
					else{
						feat.setAttribute("occ_day_m", new Integer(20));
						feat.setAttribute("occ_day_f", new Integer(20));
						feat.setAttribute("occ_day_c", new Integer(0));
						feat.setAttribute("occ_nite_m", new Integer((int) ((double)50*0.2)));
						feat.setAttribute("occ_nite_f", new Integer((int) ((double)50*0.2)));
						feat.setAttribute("occ_nite_c", new Integer(0));
					}								
					//** Average Rate
					feat.setAttribute("occ_avg_m", new Integer(
							(
									(ObjectToReal.getMeInteger(feat.getAttribute("occ_day_m"))).intValue()
									+(ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_m"))).intValue()
							)/2));
					feat.setAttribute("occ_avg_f", new Integer(
							(
							ObjectToReal.getMeInteger(feat.getAttribute("occ_day_f")).intValue()
							+ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_f")).intValue()
							)/2));
					feat.setAttribute("occ_avg_c", new Integer(
							(
							ObjectToReal.getMeInteger(feat.getAttribute("occ_day_c")).intValue()
							+ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_c")).intValue()
							)/2));
					feat.setAttribute("u_set_occr", new Boolean(false).toString());
				}
				else if(occType.matches("IND2")){

					//** Day/Night time from HAZUS tables (USING QUARTER OF THE VALUES)
					double scaleDown = 0.25;
					if(bc.getGsqm() > 5000){
						feat.setAttribute("occ_day_m", new Integer((int)(800*scaleDown)));						
						feat.setAttribute("occ_day_f", new Integer((int)(200*scaleDown)));
						feat.setAttribute("occ_day_c", new Integer(0));
						feat.setAttribute("occ_nite_m", new Integer((int) ((double)800*scaleDown*0.2)));
						feat.setAttribute("occ_nite_f", new Integer((int) ((double)200*scaleDown*0.2)));
						feat.setAttribute("occ_nite_c", new Integer(0));
					}
					else{
						feat.setAttribute("occ_day_m", new Integer((int)(80*scaleDown)));
						feat.setAttribute("occ_day_f", new Integer((int)(20*scaleDown)));
						feat.setAttribute("occ_day_c", new Integer(0));
						feat.setAttribute("occ_nite_m", new Integer((int) ((double)80*0.2*scaleDown)));
						feat.setAttribute("occ_nite_f", new Integer((int) ((double)20*0.2*scaleDown)));
						feat.setAttribute("occ_nite_c", new Integer(0));
					}					
					//** Average Rate
					feat.setAttribute("occ_avg_m", new Integer(
							(
									ObjectToReal.getMeInteger(feat.getAttribute("occ_day_m")).intValue()
									+ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_m")).intValue()
							)/2));
					feat.setAttribute("occ_avg_f", new Integer(
							(
							ObjectToReal.getMeInteger(feat.getAttribute("occ_day_f")).intValue()
							+ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_f")).intValue()
							)/2));
					feat.setAttribute("occ_avg_c", new Integer(
							(
							ObjectToReal.getMeInteger(feat.getAttribute("occ_day_c")).intValue()
							+ObjectToReal.getMeInteger(feat.getAttribute("occ_nite_c")).intValue()
							)/2));
					feat.setAttribute("u_set_occr", new Boolean(false).toString());
				}
			}
		}
		

		
		//** remove from ED building occupancy rate that were set by the user
		int maleToAssign = totalMale - userSetMaleNightRates;
		int femaleToAssign = totalFemale - userSetFemaleNightRates;			
		int childrenToAssign = totalMaleChildren + totalFemaleChildren - userSetChildNightRates;		
		if(maleToAssign < 0)
			maleToAssign = 0;
		if(femaleToAssign < 0)
			femaleToAssign = 0;
		if(childrenToAssign < 0)
			childrenToAssign = 0;

		
		int debugLmale = 0;
		int debugLfemale = 0;
		int debugLchild = 0;
			

		/*
		 * Assign population to  large RES1 or RES2
		 */	
		for(Iterator <SimpleFeature> i = largeRes1Res2Structs.iterator(); i.hasNext();){
			SimpleFeature largeRES1RES2 = i.next();
			if(!ObjectToReal.getMeBoolean(largeRES1RES2.getAttribute("u_set_occr")).booleanValue()){
				//** below from Kingston, Portmore and BDOS. SHOULD BE DOCUMENTED SOMEWHERE
				//** OR not since these buildings represent a very small fraction of building stock
				double factor = ((Double)largeRES1RES2.getAttribute("area_sqm")).doubleValue()/(ed.getNonNormalizedMeanGSQFT()*0.092903); 
				int malepop = (int)(1.0*factor);
				int femalepop = (int)(1.0*factor);
				int childpop = (int)(1.0*factor);
				largeRES1RES2.setAttribute("occ_day_m", new Integer( (int)Math.ceil(malepop*0.75)));						
				largeRES1RES2.setAttribute("occ_day_f", new Integer((int)Math.ceil(femalepop*0.75)));
				largeRES1RES2.setAttribute("occ_day_c", new Integer((int)Math.ceil(childpop*0.75)));
				largeRES1RES2.setAttribute("occ_nite_m", new Integer(malepop));
				largeRES1RES2.setAttribute("occ_nite_f", new Integer(femalepop));
				largeRES1RES2.setAttribute("occ_nite_c", new Integer(childpop));
				largeRES1RES2.setAttribute("occ_avg_m", new Integer(											
						(
								ObjectToReal.getMeInteger(largeRES1RES2.getAttribute("occ_day_m")).intValue()
								+ObjectToReal.getMeInteger(largeRES1RES2.getAttribute("occ_nite_m")).intValue()
								)/2));
				largeRES1RES2.setAttribute("occ_avg_f", new Integer(
						(
								ObjectToReal.getMeInteger(largeRES1RES2.getAttribute("occ_day_f")).intValue()
								+ObjectToReal.getMeInteger(largeRES1RES2.getAttribute("occ_nite_f")).intValue()
								)/2));
				largeRES1RES2.setAttribute("occ_avg_c", new Integer(
						(
								ObjectToReal.getMeInteger(largeRES1RES2.getAttribute("occ_day_c")).intValue()
								+ObjectToReal.getMeInteger(largeRES1RES2.getAttribute("occ_nite_c")).intValue()
								)/2));
				largeRES1RES2.setAttribute("u_set_occr", new Boolean(false).toString());
				maleToAssign -= malepop;
				femaleToAssign -= femalepop;
				childrenToAssign -= childpop;
				
				debugLmale += malepop;
				debugLfemale += femalepop;
				debugLchild += childpop;
			}
		}

		/*
		 * First order remaining RES1 and RES2 by size to assign population - largest to smallest.
		 * 
		 */
		ArrayList <BuildingSizeWrapperComparator> res1StructsComp = new ArrayList<BuildingSizeWrapperComparator>();		
		ArrayList <BuildingSizeWrapperComparator> res2StructsComp = new ArrayList<BuildingSizeWrapperComparator>();
		for(SimpleFeature bldg_res1 : res1Structs)
			res1StructsComp.add(new BuildingSizeWrapperComparator(bldg_res1));
		for(SimpleFeature bldg_res2 : res2Structs)
			res2StructsComp.add(new BuildingSizeWrapperComparator(bldg_res2));
		Collections.sort(res1StructsComp);
		Collections.reverse(res1StructsComp);
		res1Structs.clear();
		for(BuildingSizeWrapperComparator bldg_res1_wrapper : res1StructsComp)
			res1Structs.add(bldg_res1_wrapper.bldg);
		Collections.sort(res2StructsComp);
		Collections.reverse(res2StructsComp);
		res2Structs.clear();
		for(BuildingSizeWrapperComparator bldg_res2_wrapper : res2StructsComp)
			res2Structs.add(bldg_res2_wrapper.bldg);
		
		
		//** Assign remaining RES1 and RES2 small structures equally
		int totalStructs = 0;
		for(SimpleFeature feat : res1Structs)
			if(!ObjectToReal.getMeBoolean(feat.getAttribute("u_set_occr")).booleanValue())
				totalStructs++;
		for(SimpleFeature feat : res2Structs)
			if(!ObjectToReal.getMeBoolean(feat.getAttribute("u_set_occr")).booleanValue())
				totalStructs++;
				
		double malePerStruct = Math.ceil((double)maleToAssign/(double)totalStructs);
		double femalePerStruct = Math.ceil((double)femaleToAssign/(double)totalStructs);
		double childPerStruct = Math.ceil((double)childrenToAssign/(double)totalStructs);
		if(totalStructs == 0){
			malePerStruct = 0;
			femalePerStruct = 0;
			childPerStruct = 0;	
		}				
		int day_m = 0;
		int day_f = 0;
		int day_c = 0;
		int nite_m = 0;
		int nite_f = 0;
		int nite_c = 0;
		int avg_m = 0;
		int avg_f = 0;
		int avg_c = 0;
		for(SimpleFeature feat : res2Structs){
			if(!ObjectToReal.getMeBoolean(feat.getAttribute("u_set_occr")).booleanValue()){
				// ** not sure why this is done separately but...whatever
				day_m =  (int)(malePerStruct*0.75);
				day_f = (int)(femalePerStruct*0.75);
				day_c = (int)(childPerStruct*0.75);
				nite_m = (int)Math.ceil(malePerStruct);
				nite_f = (int)Math.ceil(femalePerStruct);
				nite_c = (int)Math.ceil(childPerStruct);
				feat.setAttribute("occ_day_m", new Integer(day_m));						
				feat.setAttribute("occ_day_f", new Integer(day_f));
				feat.setAttribute("occ_day_c", new Integer(day_c));
				if(maleToAssign > 0)
					feat.setAttribute("occ_nite_m", new Integer(nite_m));
				else
					feat.setAttribute("occ_nite_m", new Integer(0));
				if(femaleToAssign > 0)
					feat.setAttribute("occ_nite_f", new Integer(nite_f));
				else
					feat.setAttribute("occ_nite_f", new Integer(0));
				if(childrenToAssign > 0)
					feat.setAttribute("occ_nite_c", new Integer(nite_c));
				else
					feat.setAttribute("occ_nite_c", new Integer(0));
				avg_m = (day_m + nite_m)/2;			
				avg_f = (day_f + nite_f)/2;
				if(avg_f == 0 && avg_m == 0){
					avg_f = 1;
					avg_m = 1;
				}
				avg_c = (int)Math.ceil((double)day_c/2.0);
				feat.setAttribute("occ_avg_m", new Integer(avg_m));
				feat.setAttribute("occ_avg_f", new Integer(avg_f));
				feat.setAttribute("occ_avg_c", new Integer(avg_c));
				feat.setAttribute("u_set_occr", new Boolean(false).toString());
				
				maleToAssign -= nite_m ;
				femaleToAssign -= nite_f;
				childrenToAssign -= nite_c;						
			}
		}
		for(SimpleFeature feat : res1Structs){
			if(!ObjectToReal.getMeBoolean(feat.getAttribute("u_set_occr")).booleanValue()){
				day_m =  (int)(malePerStruct*0.75);
				day_f = (int)(femalePerStruct*0.75);
				day_c = (int)(childPerStruct*0.75);
				nite_m = (int)Math.ceil(malePerStruct);
				nite_f = (int)Math.ceil(femalePerStruct);
				nite_c = (int)Math.ceil(childPerStruct);
				feat.setAttribute("occ_day_m", new Integer(day_m));						
				feat.setAttribute("occ_day_f", new Integer(day_f));
				feat.setAttribute("occ_day_c", new Integer(day_c));
				if(maleToAssign > 0)
					feat.setAttribute("occ_nite_m", new Integer(nite_m));
				else
					feat.setAttribute("occ_nite_m", new Integer(0));
				if(femaleToAssign > 0)
					feat.setAttribute("occ_nite_f", new Integer(nite_f));
				else
					feat.setAttribute("occ_nite_f", new Integer(0));
				if(childrenToAssign > 0)
					feat.setAttribute("occ_nite_c", new Integer(nite_c));
				else
					feat.setAttribute("occ_nite_c", new Integer(0));
				avg_m = (day_m + nite_m)/2;			
				avg_f = (day_f + nite_f)/2;
				if(avg_f == 0 && avg_m == 0){
					avg_f = 1;
					avg_m = 1;
				}
				avg_c = (int)Math.ceil((double)day_c/2.0);
				feat.setAttribute("occ_avg_m", new Integer(avg_m));
				feat.setAttribute("occ_avg_f", new Integer(avg_f));
				feat.setAttribute("occ_avg_c", new Integer(day_c));
				feat.setAttribute("u_set_occr", new Boolean(false).toString());
				maleToAssign -= nite_m ;
				femaleToAssign -= nite_f;
				childrenToAssign -= nite_c;
			}
		}

	}
	
	class BuildingSizeWrapperComparator implements Comparable<BuildingSizeWrapperComparator>{
		
		public SimpleFeature bldg;
		public double size = 0;
		public  BuildingSizeWrapperComparator(SimpleFeature bldg){
			this.bldg = bldg;
			size = ObjectToReal.getMeDouble(bldg.getAttribute("gsq_feet"))*
					ObjectToReal.getMeDouble(bldg.getAttribute("no_stories"));
			if(size == 0)
				size = Double.MIN_NORMAL;
		}
		
	    public int compareTo(BuildingSizeWrapperComparator in) {	    
	    	if(size == in.size)
	    		return 0;
	    	if(size < in.size)
	    		return -1;
	        return 1;
	    }
	}
}
