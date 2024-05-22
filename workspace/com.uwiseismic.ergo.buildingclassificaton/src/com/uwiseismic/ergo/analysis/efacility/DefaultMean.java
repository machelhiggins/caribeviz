/*
 * Created on Jan 22, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.analysis.efacility;

import java.util.ArrayList;
import java.util.HashMap;

import com.uwiseismic.ergo.graph.ErgoGraph;

public abstract class DefaultMean implements Mean{

    private String id = "";
    private double meanValue = 0;
    private double x = 0;
    private double y = 0;
    protected FacilityRanker ranker;
    protected ArrayList<Facility> facilityOrdered;
    protected ArrayList <Facility>facilities;
    HashMap<Facility, Integer> popDist = new HashMap<Facility, Integer>();
   

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#setMeanValue(double)
     */
    public void setMeanValue(double meanValue){
       this.meanValue = meanValue;
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#getMeanValue()
     */
    public double getMeanValue(){
        return meanValue;
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#getID()
     */
    public String getID() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#setID(java.lang.String)
     */
    public void setID(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#setFacilitiyRanker(com.uwiseismic.analyis.efacility.FacilityRanker)
     */
    public void setFacilityRanker(FacilityRanker ranker) {
        this.ranker = ranker;
    }

    public FacilityRanker getFacilityRanker(){
        return ranker;
    }


    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#setX(double)
     */
    public void setX(double x) {
        this.x = x;
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#getX()
     */
    public double getX() {
        return x;
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#setY(double)
     */
    public void setY(double y) {
        this.y = y;

    }

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#getY()
     */
    public double getY() {
        return y;
    }


    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#setFacilities(java.util.ArrayList)
     */
    public void setFacilities(ArrayList<Facility> facilities) {
        this.facilities = facilities;
    }

	/* (non-Javadoc)
	 * @see com.uwiseismic.analyis.efacility.Facility#getFacilities()
	 */
	public ArrayList<Facility> getFacilities() {
		return facilities;
	}

	@Override
	public HashMap<Facility, Integer> getPopulationDistribution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getUncateredForPop() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setGraph(ErgoGraph graph) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ErgoGraph getGraph() {
		// TODO Auto-generated method stub
		return null;
	}

}
