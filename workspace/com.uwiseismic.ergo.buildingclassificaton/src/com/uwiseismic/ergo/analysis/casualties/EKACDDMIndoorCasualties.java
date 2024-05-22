/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Contributors:
 *     Shawn Hampton, Jong Lee, Chris Navarro, Nathan Tolbert (NCSA) - initial API and implementation and/or initial documentation
 *******************************************************************************/
package com.uwiseismic.ergo.analysis.casualties;

import javax.swing.table.DefaultTableModel;

import org.opengis.feature.simple.SimpleFeature;

import edu.illinois.ncsa.ergo.eq.buildings.Building;
import edu.illinois.ncsa.ergo.eq.socialscience.Population;
import edu.illinois.ncsa.ergo.gis.datasets.TableDataset;
import edu.illinois.ncsa.ergo.gis.util.FeatureUtils;

/**
 * Uses EKACDM Mandated population disaggregation to computer casualties
 *         TODO add license header
 */
public class EKACDDMIndoorCasualties
{
	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
	private TableDataset casualtyFractionTable;
	private TableDataset collapseRateTable;
	//private Population population;

	public EKACDDMIndoorCasualties()
	{
		// Nothing to initialize
	}

	/**
	 * 
	 * @param severity
	 *            Casaulty level (Severity 1 - injuries requiring basic medical
	 *            aid, Severity 2 - injuries requiring greater degree of medical
	 *            care, Severity 3 - injuries that pose immediate life threating
	 *            condition if not treated adequately and expeditiously,
	 *            Severity 4 - Instantaneously killed or mortally injured)
	 * @param timeOfEvent
	 *            Time of event (Morning, Evening, Average), used to determine
	 *            population occupying structure
	 * @param buildingFeature
	 *            Building we are computing casualties for
	 * @return
	 */
	public double computeMeanExpectedCasualties(String severity, SimpleFeature buildingFeature, double numOccupants){
	

		if (buildingFeature == null)
			return 0.0;

		int areaCol = buildingFeature.getFeatureType().indexOf(Building.AREA);
		double area = Double.parseDouble(buildingFeature.getAttribute(areaCol).toString());
		int hazusStrTypeCol = buildingFeature.getFeatureType().indexOf(Building.HAZUS_STR_TYPE_COL);
		if (hazusStrTypeCol == -1) {
			hazusStrTypeCol = buildingFeature.getFeatureType().indexOf("STR_TYP2"); //$NON-NLS-1$
		}
		String strtype = buildingFeature.getAttribute(hazusStrTypeCol).toString();
		String[] damageStates = { "Insignificant", "Moderate", "Heavy", "Complete" };

		double probInsignificant = (Double) buildingFeature.getAttribute(FeatureUtils.findColumn(buildingFeature, Building.INSIGNIFICANT));
		double probModerate = (Double) buildingFeature.getAttribute(FeatureUtils.findColumn(buildingFeature, Building.MODERATE));

		double probHeavy = (Double) buildingFeature.getAttribute(FeatureUtils.findColumn(buildingFeature, Building.HEAVY));

		double probCollapse = (Double) buildingFeature.getAttribute(FeatureUtils.findColumn(buildingFeature, Building.COMPLETE));

		double casualtyFractionI = findCasualtyFraction(severity, strtype, "No", Building.INSIGNIFICANT);
		double casualtyFractionM = findCasualtyFraction(severity, strtype, "No", Building.MODERATE);
		double casualtyFractionH = findCasualtyFraction(severity, strtype, "No", Building.HEAVY);
		double casualtyFractionC = findCasualtyFraction(severity, strtype, "No", Building.COMPLETE);
		double casualtyFractionCCollapsed = findCasualtyFraction(severity, strtype, "Yes", Building.COMPLETE);
		// double deathFraction = findDeathFraction( casualtyFractionTable,
		// strtype, collapse, damageState );//0.00001;
		double collapseRate = this.findCollapseRate(strtype);
		double totalCasualties = (area * numOccupants / 1000.0)
				* (casualtyFractionI * probInsignificant + casualtyFractionM * probModerate + casualtyFractionH * probHeavy + probCollapse
						* collapseRate * casualtyFractionCCollapsed + probCollapse * (1 - collapseRate) * casualtyFractionC);

		// return numOccupants * area / 1000.0 * deathFraction;
		return totalCasualties;
	}

	/**
	 * 
	 * @param severity
	 * @param strtype
	 * @param collapse
	 * @param damageState
	 * @return
	 */
	private double findCasualtyFraction(String severity, String strtype, String collapse, String damageState)
	{

		int column = casualtyFractionTable.findColumn(damageState);
		int severityColumn = casualtyFractionTable.findColumn("severity");
		if (column != -1) {
			DefaultTableModel table = casualtyFractionTable.getTableModel();
			for (int i = 0; i < table.getRowCount(); i++) {
				String strtypeAtRow = (String) table.getValueAt(i, 0);
				if (strtypeAtRow.equalsIgnoreCase(strtype)) {
					String severityAtRow = (String) table.getValueAt(i, severityColumn);
					if (severityAtRow.equalsIgnoreCase(severity)) {
						String collapseAtRow = (String) table.getValueAt(i, 1);
						if (collapseAtRow.equalsIgnoreCase(collapse)) {
							double casualtyFraction = Double.parseDouble(table.getValueAt(i, column).toString());
							return casualtyFraction / 100.0; // return the
																// fraction;
						}
					}
				}
			}
		}

		logger.debug("ERROR: This structure type does not have a death fraction: " + strtype); //$NON-NLS-1$
		return 0.0;
	}

	/**
	 * 
	 * @param strtype
	 * @return
	 */
	private double findCollapseRate(String strtype)
	{
		double collapseProbability = 0.0;
		DefaultTableModel table = collapseRateTable.getTableModel();
		int collapseRateCol = collapseRateTable.findColumn("collapserate");
		int strTypeCol = collapseRateTable.findColumn("str_type");
		for (int i = 0; i < table.getRowCount(); i++) {
			String rowStrType = (String) table.getValueAt(i, strTypeCol);
			if (rowStrType.equalsIgnoreCase(strtype)) {
				collapseProbability = Double.parseDouble(table.getValueAt(i, collapseRateCol).toString());
				return collapseProbability;
			}
			// if ( table.getColumnName( i ).equalsIgnoreCase( columnName ) ) {
			// return i;
			// }

		}
		return collapseProbability;
	}

	public TableDataset getCasualtyFractionTable()
	{
		return casualtyFractionTable;
	}

	public void setCasualtyFractionTable(TableDataset casualtyFractionTable)
	{
		this.casualtyFractionTable = casualtyFractionTable;
	}

	public TableDataset getCollapseRateTable()
	{
		return collapseRateTable;
	}

	public void setCollapseRateTable(TableDataset collapseRateTable)
	{
		this.collapseRateTable = collapseRateTable;
	}

//	public void setPopulation(Population population)
//	{
//		this.population = population;
//	}
}
