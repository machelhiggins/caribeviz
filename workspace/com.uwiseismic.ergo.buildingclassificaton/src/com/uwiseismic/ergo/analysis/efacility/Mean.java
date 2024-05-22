/*
 * Created on Jan 26, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.analysis.efacility;

import java.util.ArrayList;
import java.util.HashMap;

import com.uwiseismic.ergo.graph.ErgoGraph;

/**
 * @author <a href="mailto:machelhiggins.hotmail.com">Machel Higgins </a>
 *
 * TODO
 */
public interface Mean {

    /**
     * Get facility ID
     * @return
     */
    public String getID();

    /**
     * Set facility ID
     * @param id
     */
    public void setID(String id);

    /**
     * Add all facilities to determine ranking to the mean this object represents
     *
     * @param facilities
     */
    public void setFacilities(ArrayList<Facility> facilities);


	/**
	 * Return facilities being used to determine ranking to the facility this object represents
	 *
	 * @return
	 */
	public ArrayList<Facility> getFacilities();


    /**
     * Implementation of FacilitiyRanker will consider all rankings in spatial relation to the mean this object represents
     *
     * @param ranker
     */
    public void setFacilityRanker(FacilityRanker ranker);


    /**
     * FacilityRanker being used by this mean
     * @return
     */
    public FacilityRanker getFacilityRanker();

    /**
     * Returns the distrubtion of the population of this mean to facilities.
     *
     * @return
     */
    public HashMap<Facility, Integer>getPopulationDistribution();


    /**
     * Set scalar value this mean represents
     *
     * @param meanValue
     */
    public void setMeanValue(double meanValue);

    /**
     * get scalar value this mean represents
     * @return
     */
    public double getMeanValue();

    /**
     * For 2D spatial ranking
     * @param x
     */
    public void setX(double x);

    /**
     * For 2D spatial ranking
     * @return
     */
    public double getX();

    /**
     * For 2D spatial ranking
     */
    public void setY(double y);

    /**
     * For 2D spatial ranking
     * @return
     */
    public double getY();

    /**
     *
     * After calling <code> getPopulationDistribution() </code> this method
     * returns the population that were not accomodated by a facility
     * @return
     */
    public double getUncateredForPop();
    
    
    /**
     * Does not have to be implemented but any routing that might required for this mean should implement an ErgoGraph
     */
    public void setGraph(ErgoGraph graph);
    
    /**
     * Does not have to be implemented but any routing that might required for this mean should implement an ErgoGraph
     * @return
     */
    public ErgoGraph getGraph();
    


}
