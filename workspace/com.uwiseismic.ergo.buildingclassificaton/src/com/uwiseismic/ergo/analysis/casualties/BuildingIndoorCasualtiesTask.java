/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Contributors:
 *     Shawn Hampton, Jong Lee, Chris Navarro, Nathan Tolbert (NCSA) - initial API and implementation and/or initial documentation
 *******************************************************************************/
package com.uwiseismic.ergo.analysis.casualties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.ObjectToReal;

import edu.illinois.ncsa.ergo.core.analysis.ogrescript.tasks.core.SimpleFeatureTask;
import edu.illinois.ncsa.ergo.eq.buildings.Building;
import edu.illinois.ncsa.ergo.eq.socialscience.Population;
import edu.illinois.ncsa.ergo.gis.datasets.TableDataset;
import ncsa.tools.common.eclipse.descriptors.exceptions.UnknownExtensionException;
import ncsa.tools.common.eclipse.descriptors.types.BaseDescriptorHelper;
import ncsa.tools.elf.core.exceptions.ScriptExecutionException;

/**
 * Uses EKACDM Mandated population disaggregation to computer casualties
 * 
 * @author
 * 
 *        TODO add license header
 */
public class BuildingIndoorCasualtiesTask extends SimpleFeatureTask
{
	// private final org.apache.log4j.Logger logger =
	// org.apache.log4j.Logger.getLogger( this.getClass() );
	private TableDataset indoorCasualtyTable;
	private TableDataset collapseRateTable;
	private String timeOfEvent;
	private String populationEstimateType;
	private double severity1;
	private double severity2;
	private double severity3;
	private double severity4;
	private double childSeverity1;
	private double childSeverity2;
	private double childSeverity3;
	private double childSeverity4;
	private double maleSeverity1;
	private double maleSeverity2;
	private double maleSeverity3;
	private double maleSeverity4;
	private double femaleSeverity1;
	private double femaleSeverity2;
	private double femaleSeverity3;
	private double femaleSeverity4;
	
	private final String DAY = "Day"; 
	private final String NIGHT = "Night";
	private final String AVERAGE = "Average";
	private final int MALE = 0;
	private final int FEMALE = 1;
	private final int CHILD = 2;
	private final int EVERYBODY = 3;
	
	
	protected void preProcess(IProgressMonitor monitor) throws ScriptExecutionException
	{
		// Nothing to do here
	}

	/**
	 * 
	 * @param monitor
	 * @throws ScriptExecutionException
	 */
	protected void handleFeature(IProgressMonitor monitor) throws ScriptExecutionException
	{
//		try {
//			Population population = (Population) BaseDescriptorHelper.createNew(populationEstimateType, Population.EXT_PT);

			EKACDDMIndoorCasualties indoorCasualties = new EKACDDMIndoorCasualties();
			indoorCasualties.setCasualtyFractionTable(indoorCasualtyTable);
			indoorCasualties.setCollapseRateTable(collapseRateTable);
//			indoorCasualties.setPopulation(population);


			severity1 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_ONE_COL, feature, getOccupancy(timeOfEvent, EVERYBODY, feature));
			severity2 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_TWO_COL, feature, getOccupancy(timeOfEvent, EVERYBODY, feature));
			severity3 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_THREE_COL, feature, getOccupancy(timeOfEvent, EVERYBODY, feature));
			severity4 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_FOUR_COL, feature, getOccupancy(timeOfEvent, EVERYBODY, feature));
			maleSeverity1 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_ONE_COL, feature, getOccupancy(timeOfEvent, MALE, feature));
			maleSeverity2 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_TWO_COL, feature, getOccupancy(timeOfEvent, MALE, feature));
			maleSeverity3 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_THREE_COL, feature, getOccupancy(timeOfEvent, MALE, feature));
			maleSeverity4 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_FOUR_COL, feature, getOccupancy(timeOfEvent, MALE, feature));
			femaleSeverity1 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_ONE_COL, feature, getOccupancy(timeOfEvent, FEMALE, feature));
			femaleSeverity2 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_TWO_COL, feature, getOccupancy(timeOfEvent, FEMALE, feature));
			femaleSeverity3 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_THREE_COL, feature, getOccupancy(timeOfEvent, FEMALE, feature));
			femaleSeverity4 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_FOUR_COL, feature, getOccupancy(timeOfEvent, FEMALE, feature));
			childSeverity1 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_ONE_COL, feature, getOccupancy(timeOfEvent, CHILD, feature));
			childSeverity2 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_TWO_COL, feature, getOccupancy(timeOfEvent, CHILD, feature));
			childSeverity3 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_THREE_COL, feature, getOccupancy(timeOfEvent, CHILD, feature));
			childSeverity4 = indoorCasualties.computeMeanExpectedCasualties(BuildingSeverity.SEVERITY_FOUR_COL, feature, getOccupancy(timeOfEvent, CHILD, feature));

//		} catch (UnknownExtensionException e) {
//			e.printStackTrace();
//		}

		resultMap.put(BuildingSeverity.SEVERITY_ONE_COL, severity1);
		resultMap.put(BuildingSeverity.SEVERITY_TWO_COL, severity2);
		resultMap.put(BuildingSeverity.SEVERITY_THREE_COL, severity3);
		resultMap.put(BuildingSeverity.SEVERITY_FOUR_COL, severity4);
		
		resultMap.put(BuildingSeverity.SEVERITY_ONE_COL_M, maleSeverity1);
		resultMap.put(BuildingSeverity.SEVERITY_TWO_COL_M, maleSeverity2);
		resultMap.put(BuildingSeverity.SEVERITY_THREE_COL_M, maleSeverity3);
		resultMap.put(BuildingSeverity.SEVERITY_FOUR_COL_M, maleSeverity4);
		
		resultMap.put(BuildingSeverity.SEVERITY_ONE_COL_F, femaleSeverity1);
		resultMap.put(BuildingSeverity.SEVERITY_TWO_COL_F, femaleSeverity2);
		resultMap.put(BuildingSeverity.SEVERITY_THREE_COL_F, femaleSeverity3);
		resultMap.put(BuildingSeverity.SEVERITY_FOUR_COL_F, femaleSeverity4);

		resultMap.put(BuildingSeverity.SEVERITY_ONE_COL_C, childSeverity1);
		resultMap.put(BuildingSeverity.SEVERITY_TWO_COL_C, childSeverity2);
		resultMap.put(BuildingSeverity.SEVERITY_THREE_COL_C, childSeverity3);
		resultMap.put(BuildingSeverity.SEVERITY_FOUR_COL_C, childSeverity4);
	}
	

	
	private double getOccupancy(String timeOfEvent, int who, SimpleFeature buildingFeature){
		Double occ = null;
		if(timeOfEvent.equals(NIGHT)){
			if(who == MALE)
				occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_nite_m"));
			else if(who == FEMALE)
			occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_nite_f"));
			else if(who == CHILD)
				occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_nite_c"));
			else if(who == EVERYBODY){
				double errbody = 0;
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_nite_m")).doubleValue();}catch(Exception ex){}
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_nite_f")).doubleValue();}catch(Exception ex){}
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_nite_c")).doubleValue();}catch(Exception ex){}								
				occ = new Double(errbody);
			}
		}
		else if(timeOfEvent.equals(DAY)){
			if(who == MALE)
				occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_day_m"));
			else if(who == FEMALE)
			occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_day_f"));
			else if(who == CHILD)
				occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_day_c"));
			else if(who == EVERYBODY){
				double errbody = 0;
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_day_m")).doubleValue();}catch(Exception ex){}
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_day_f")).doubleValue();}catch(Exception ex){}
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_day_c")).doubleValue();}catch(Exception ex){}								
				occ = new Double(errbody);
			}
		}
		else if(timeOfEvent.equals(AVERAGE)){
			if(who == MALE)
				occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_avg_m"));
			else if(who == FEMALE)
			occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_avg_f"));
			else if(who == CHILD)
				occ = ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_avg_c"));
			else if(who == EVERYBODY){
				double errbody = 0;
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_avg_m")).doubleValue();}catch(Exception ex){}
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_avg_f")).doubleValue();}catch(Exception ex){}
				try{ errbody += ObjectToReal.getMeDouble(buildingFeature.getAttribute("occ_avg_c")).doubleValue();}catch(Exception ex){}								
				occ = new Double(errbody);
			}
		}

		if(occ == null)
			return 0;
		else 
			return occ.doubleValue();
	}
	

	public TableDataset getIndoorCasualtyTable()
	{
		return indoorCasualtyTable;
	}

	public void setIndoorCasualtyTable(TableDataset indoorCasualtyTable)
	{
		this.indoorCasualtyTable = indoorCasualtyTable;
	}

	public TableDataset getCollapseRateTable()
	{
		return collapseRateTable;
	}

	public void setCollapseRateTable(TableDataset collapseRateTable)
	{
		this.collapseRateTable = collapseRateTable;
	}

	public String getTimeOfEvent()
	{
		return timeOfEvent;
	}

	public void setTimeOfEvent(String timeOfEvent)
	{
		this.timeOfEvent = timeOfEvent;
	}

	public String getPopulationEstimateType()
	{
		return populationEstimateType;
	}

	public void setPopulationEstimateType(String populationEstimateType)
	{
		this.populationEstimateType = populationEstimateType;
	}

}
