/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Contributors:
 *     Shawn Hampton, Jong Lee, Chris Navarro, Nathan Tolbert (NCSA) - initial API and implementation and/or initial documentation
 *******************************************************************************/
package com.uwiseismic.ergo.populationdislocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ncsa.tools.common.eclipse.descriptors.exceptions.UnknownExtensionException;
import ncsa.tools.common.eclipse.descriptors.types.BaseDescriptorHelper;
import ncsa.tools.elf.core.exceptions.ScriptExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

import edu.illinois.ncsa.ergo.core.analysis.ogrescript.tasks.core.SimpleFeatureTask;
import edu.illinois.ncsa.ergo.eq.socialscience.SocialVulnerability;
import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;

/**
 * Computes population dislocation based on physical damage
 * 
 * @author cnavarro
 * 
 *         TODO add license header
 */
public class PopulationDislocationTask extends SimpleFeatureTask
{

	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
	/** This should match the tag given at the extension point for the method */
	
	private double maleDislocation = 0;
	private double femaleDislocation = 0;
	private double childDislocation = 0;
	private double popDislocation = 0;
		



	/**
	 * 
	 * @throws ScriptExecutionException
	 */
	protected void preProcess() throws ScriptExecutionException
	{
		popDislocation = 0.0;
		childDislocation = 0;
		femaleDislocation = 0;
		maleDislocation = 0;

	}

	/**
	 * 
	 * @param monitor
	 * @throws ScriptExecutionException
	 */
	protected void handleFeature(IProgressMonitor monitor) throws ScriptExecutionException{
		 
		EKACDMModifiedHASUZPopulationDislocation dislocationMethodology = new EKACDMModifiedHASUZPopulationDislocation(feature);
								
		maleDislocation = dislocationMethodology.getMaleDislocation();
		femaleDislocation = dislocationMethodology.getFemaleDislocation();
		childDislocation = dislocationMethodology.getChildDislocation();
		popDislocation = dislocationMethodology.getPopDislocation();
		
		resultMap.put(EKACDMModifiedHASUZPopulationDislocation.POP_DISLOCATION, popDislocation);
		resultMap.put(EKACDMModifiedHASUZPopulationDislocation.POP_DISLOCATEION_MALE, maleDislocation);
		resultMap.put(EKACDMModifiedHASUZPopulationDislocation.POP_DISLOCATEION_FEMALE, femaleDislocation);
		resultMap.put(EKACDMModifiedHASUZPopulationDislocation.POP_DISLOCATEION_CHILD, childDislocation);

	}

}
