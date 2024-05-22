package com.uwiseismic.ergo.analysis.efacility;

import java.util.ArrayList;

import com.uwiseismic.ergo.graph.ErgoGraph;

/**
 * Interface for performing ranking on objects inheriting <code>Facilty</code>
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public interface FacilityRanker extends Cloneable{

	
	
	/**
	 * Set Facilities to be ranked
	 * 
	 * @param facilities
	 */
	public void setFacilities(ArrayList<Facility> facilities);
	
	/**
	 * Returns ordered (lowest to highest) facilities after performRanking is called
	 * 
	 * @return
	 */
	public ArrayList<Facility> getOrderFacilities();
	
	/**
	 * Returns facility ranking in order <code>getOrderFacilities()</code>
	 * 
	 * @return
	 */
	public double[] getFacilitiesRanking();
	
	/**
	 * Perform Ranking
	 */
	public void performRanking() throws Exception ;
	
	/**
	 * Sets endpoint that to compare the weighting of the many facilities 
	 * 
	 * @param e
	 * ClassCastException Exception If Object is an invalid class representing an endpoint in implementation of this method
	 */
	public void setEndPoint(Object e)throws ClassCastException;
	
	
	/**
	 * helpful function to clone this Facility to make small changes for new uses (like changing end point)
	 * 
	 * @return
	 */
	public FacilityRanker clone();
	
    /**
     * Does not have to be implemented but any routing that might be required should implement an ErgoGraph
     */
    public void setGraph(ErgoGraph graph);
    
    /**
     * Does not have to be implemented but any routing that might be required should implement an ErgoGraph
     * @return
     */
    public ErgoGraph getGraph();
	
}
