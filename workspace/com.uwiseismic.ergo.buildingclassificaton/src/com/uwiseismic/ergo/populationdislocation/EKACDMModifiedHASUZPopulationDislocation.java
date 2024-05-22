package com.uwiseismic.ergo.populationdislocation;

import java.util.Locale;

import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.ObjectToReal;

import edu.illinois.ncsa.ergo.eq.buildings.Building;
import edu.illinois.ncsa.ergo.eq.socialscience.SocialVulnerability;
//import edu.illinois.ncsa.ergo.eq.socialscience.extensionpoints.PopulationDislocationMethodology;

//public class EKACDMModifiedHASUZPopulationDislocation extends PopulationDislocationMethodology{
public class EKACDMModifiedHASUZPopulationDislocation{
			
	public final static String POP_DISLOCATION = "popdisloc";
	public final static String POP_DISLOCATEION_MALE = "popdislo_m";
	public final static String POP_DISLOCATEION_FEMALE = "popdislo_f";
	public final static String POP_DISLOCATEION_CHILD = "popdislo_c";
	
	private double maleDisloc = 0;
	private double femaleDisloc = 0;
	private double childDisloc = 0;
	private double popDislocation = 0;
	
	private SimpleFeature building;
	
	
	public EKACDMModifiedHASUZPopulationDislocation( SimpleFeature building){
		this.building = building;
		computeDislocatedPopulation();		
	}
	
	/**
	 * 
	 * @return
	 */
	public double computeDislocatedHouseholds(){
		//no op
		return 0;
	}

	
	
	
	/**
	 * 
	 * @return
	 */
	public double computeDislocatedPopulation(){	

		String occType = null;
		int occType_col = building.getFeatureType().indexOf(Building.OCCUPANCY_TYPE);
		if (occType_col != -1) {
			occType = (String) building.getAttribute(occType_col);
		}

		if (occType != null) {
			occType = occType.toLowerCase(Locale.ENGLISH);
			int household_type = this.getHouseholdType(occType);
			if (household_type != -1) {
				
				double popMale = ObjectToReal.getMeDouble(building.getAttribute("occ_nite_m")).doubleValue();
				double popFemale = ObjectToReal.getMeDouble(building.getAttribute("occ_nite_f")).doubleValue();
				double popChild = ObjectToReal.getMeDouble(building.getAttribute("occ_nite_c")).doubleValue();
				
				maleDisloc = computeNumberOfDislocatedHouseholds(building, occType, popMale, household_type);
				femaleDisloc = computeNumberOfDislocatedHouseholds(building, occType, popFemale, household_type);
				childDisloc = computeNumberOfDislocatedHouseholds(building, occType, popChild, household_type);
				if(maleDisloc < 1 && maleDisloc > 0.5)
					maleDisloc = 1;
				else if(maleDisloc > 1)
					maleDisloc = Math.round(maleDisloc);				
				if(femaleDisloc < 1 && femaleDisloc > 0.5)
					femaleDisloc = 1;
				else if(femaleDisloc > 1)
					femaleDisloc = Math.round(femaleDisloc);
				if(childDisloc < 1 && childDisloc > 0.5)
					childDisloc = 1;
				else if(childDisloc > 1)
					childDisloc = Math.round(childDisloc);
				popDislocation = childDisloc+femaleDisloc+maleDisloc;
				if(Double.isNaN(childDisloc)){
					System.err.println("childDisloc = NaN");
					
				}
				if(Double.isNaN(femaleDisloc)){
					System.err.println("femaleDisloc = NaN");
					
				}
				if(Double.isNaN(maleDisloc)){
					System.err.println("maleDisloc = NaN");
					
				}
				if(Double.isNaN(popDislocation)){
					System.err.println("popDislocation = NaN");
					
				}
			}
		}

		return 0;
	}

	/**
	 * 
	 * @param occType
	 * @return
	 */
	private int getHouseholdType(String occType)
	{
		// If anything other than residential, ignore that building
		int household_type = -1;
		occType = occType.toLowerCase();
		if (occType.equals("res1")) { //$NON-NLS-1$
			household_type = 0;
		} else if (occType.equals("res1_a")) { //$NON-NLS-1$
			household_type = 0;
		} else if (occType.equals("res2")) { //$NON-NLS-1$
			household_type = 1;
		} else if (occType.equals("res3")) { //$NON-NLS-1$
			household_type = 1;
		} else if (occType.equals("res4")) { //$NON-NLS-1$
			household_type = 2;
		} else if (occType.equals("res5")) { //$NON-NLS-1$
			household_type = 3;
		} else if (occType.equals("res6")) { //$NON-NLS-1$
			household_type = 4;
		}
		
		return household_type;
	}

	/**
	 * 
	 * @param feature
	 * @param occType
	 * @param avgHhDU_BG
	 * @param household_type
	 * @return
	 */
	private double computeNumberOfDislocatedHouseholds(SimpleFeature feature, String occType, double avgHhDU_BG, int household_type)
	{
		// int household_type = getHouseholdType( occType );

		if (household_type == 2 || household_type == 3 || household_type == 4) {
			return 0.0;
		}
		double[] df = this.getDislocationFactors(household_type);

		int prob_i_col = feature.getFeatureType().indexOf(Building.INSIGNIFICANT);

		double prob_i = 0.0;		
		if (prob_i_col != -1) {
			prob_i = (Double) feature.getAttribute(prob_i_col);
			if(Double.isNaN(prob_i))
				prob_i = 0;
		}

		int prob_m_col = feature.getFeatureType().indexOf(Building.MODERATE);
		double prob_m = 0.0;
		if (prob_m_col != -1) {
			prob_m = (Double) feature.getAttribute(prob_m_col);
			if(Double.isNaN(prob_m))
				prob_m = 0;
		}

		int prob_h_col = feature.getFeatureType().indexOf(Building.HEAVY);
		double prob_h = 0.0;
		if (prob_h_col != -1) {
			prob_h = (Double) feature.getAttribute(prob_h_col);
			if(Double.isNaN(prob_h))
				prob_h = 0;
		}

		int prob_c_col = feature.getFeatureType().indexOf(Building.COMPLETE);
		double prob_c = 0.0;
		if (prob_c_col != -1) {
			prob_c = (Double) feature.getAttribute(prob_c_col);
			if(Double.isNaN(prob_c))
				prob_c = 0;
		}

		//double dislocation = Math.round((df[0] * prob_i + df[1] * prob_m + df[2] * prob_h + df[3] * prob_c) * avgHhDU_BG);
		double dislocation = (df[0] * prob_i + df[1] * prob_m + df[2] * prob_h + df[3] * prob_c) * avgHhDU_BG;

		//** Don't need following since we use occupancy for entire building, regardless of number of dwelling units
//		if (household_type == 1) {
//			int dwell_unit_col = feature.getFeatureType().indexOf(Building.NUM_DWELLING_UNIT_COL);
//			int dwell_unit = 1;
//			if (dwell_unit_col != -1) {
//				dwell_unit = Integer.parseInt(feature.getAttribute(dwell_unit_col).toString());
//			}
//			dislocation *= dwell_unit;
//		}
		


		return dislocation;
	}

	/**
	 * Table 1. Dislocation Factors by Damage States
	 * 
	 * @param household_type
	 * @return
	 */
	private double[] getDislocationFactors(int household_type)
	{
		double[] dislocationFactors = new double[4];

		if (household_type == 0) {
			dislocationFactors[0] = 0.0;
			dislocationFactors[1] = 0.0;
			dislocationFactors[2] = 0.0;
			dislocationFactors[3] = 1.0;
		} else {
			dislocationFactors[0] = 0.0;
			dislocationFactors[1] = 0.0;
			dislocationFactors[2] = 0.9;
			dislocationFactors[3] = 1.0;
		}

		return dislocationFactors;
	}

	
	
	public double getMaleDislocation() {
		return maleDisloc;
	}

	public double getFemaleDislocation() {
		return femaleDisloc;
	}

	public double getChildDislocation() {
		return childDisloc;
	}

	
	public double getPopDislocation() {
		return popDislocation;
	}

	public void setBuilding(SimpleFeature building) {
		this.building = building;
	}

}
