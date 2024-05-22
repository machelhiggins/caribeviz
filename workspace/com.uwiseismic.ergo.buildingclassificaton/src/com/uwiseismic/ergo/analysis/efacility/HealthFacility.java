/*
 * Created on Jan 22, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.analysis.efacility;

import java.util.ArrayList;
import java.util.HashMap;

public class HealthFacility extends DefaultFacility implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -2196845384956106513L;
	private int meanOccupancyRate = 0;
    private double largeFacilityFactor = 1.0;
    private double smallFacilityFactor = 0.30;

    public static final int LARGE_FACILITY = 0;
    public static final int SMALL_FACILITY = 1;

    private static final int BEDS_FOR_SMALL_FACILITY = 30;

    private double rankings[];
    private ArrayList <Facility> orderedFacils;
    private boolean alreadyRanked = false;


    public HealthFacility(double lat, double longi, int maxCapacity, int meanOccupancyRate){
        this.setX(longi);
        this.setY(lat);
        setMaxCapacity(maxCapacity);
        this.meanOccupancyRate = meanOccupancyRate;
    }

////*** Methods for dealing with facilities neighbors and ranking them
//	public HashMap<Facility, Integer> redistributeOverCapacity() {
//	       // TODO IProgressMonitor TO THIS !!!!!!!!!!!
//
//	    try{
//	     // TODO : check if this test should be done.
//	        //** Ranking may not be one-off but is EXTREMELY expensive.
//	        if(!alreadyRanked)
//	        	performRankingOfNeighbors();
//	        alreadyRanked = true;
//	    }catch(Exception ex){
//	        ex.printStackTrace();
//	        return null;
//	    }
//	    HashMap<Facility, Integer> popDist = new HashMap<Facility, Integer>();
//	    //** only using top three facilities. will distribute fractionally based on total ranking
//	    double totalRank = rankings[rankings.length -1] + rankings[rankings.length -2] +rankings[rankings.length -3];
//        orderedFacils.get(rankings.length -1).addOccupancy(this.getOverCapacityAmount()*(rankings[rankings.length -1 ]/totalRank),true);
//        orderedFacils.get(rankings.length -2).addOccupancy(this.getOverCapacityAmount()*(rankings[rankings.length -2 ]/totalRank),true);
//        orderedFacils.get(rankings.length -3).addOccupancy(this.getOverCapacityAmount()*(rankings[rankings.length -3 ]/totalRank),true);
//        popDist.put(orderedFacils.get(rankings.length -1), new Integer((int)Math.ceil(orderedFacils.get(rankings.length -1).getOccupancy())));
//        popDist.put(orderedFacils.get(rankings.length -2), new Integer((int)Math.ceil(orderedFacils.get(rankings.length -2).getOccupancy())));
//        popDist.put(orderedFacils.get(rankings.length -3), new Integer((int)Math.ceil(orderedFacils.get(rankings.length -3).getOccupancy())));
//
//
//		return popDist;
//	}


// /**
//  * ADD IProgressMonitor TO THIS !!!!!!!!!!!
//  *
//  * @throws Exception
//  */
//	 private void performRankingOfNeighbors()throws Exception{
//	 	// TODO IProgressMonitor TO THIS !!!!!!!!!!!
//	     if(getFacilityRanker() == null){
//	         this.setFacilityRanker(new HealthFacilityRanker());
//	     }
//	     getFacilityRanker().setEndPoint(this);
//	     getFacilityRanker().setFacilities(getFacilities());
//	     getFacilityRanker().performRanking();
//	     rankings = getFacilityRanker().getFacilitiesRanking();
//	     orderedFacils = getFacilityRanker().getOrderFacilities();
//	     alreadyRanked = true;
//	 }

    /**
     * @return the meanOccupancyRate
     */
    public int getMeanOccupancyRate() {
        return meanOccupancyRate;
    }

    /**
     * @param meanOccupancyRate the meanOccupancyRate to set
     */
    public void setMeanOccupancyRate(int meanOccupancyRate) {
        this.meanOccupancyRate = meanOccupancyRate;
    }


    public int getHealthcareFacilityCategory(){
        return getMaxCapacity() <= BEDS_FOR_SMALL_FACILITY ? SMALL_FACILITY : LARGE_FACILITY;
    }

    /**
     * This is the factor that used reweight this facility if facility is a </code> LARGE_FACILITY </code>.
     *
     * @return the largeFacilityFactor
     */
    public double getLargeFacilityFactor() {
        return largeFacilityFactor;
    }

    /**
     * Set the factor that used reweight this facility if facility is a </code> LARGE_FACILITY </code>.
     *
     * returns <code> SMALL_FACILITY</code> or </code> LARGE_FACILITY </code>.
     * Currently the threshold is hardcorded to 50 beds.
     * @param largeFacilityFactor the largeFacilityFactor to set
     */
    public void setLargeFacilityFactor(double largeFacilityFactor) {
        this.largeFacilityFactor = largeFacilityFactor;
    }

    /**
     * This is the factor that used reweight this facility if facility is a <code> SMALL_FACILITY</code>
     *
     * @return the smallFacilityFactor
     */
    public double getSmallFacilityFactor() {
        return smallFacilityFactor;
    }

    /**
     *  Set the factor that used reweight this facility if facility is a <code> SMALL_FACILITY</code>
     *
     * @param smallFacilityFactor the smallFacilityFactor to set
     */
    public void setSmallFacilityFactor(double smallFacilityFactor) {
        this.smallFacilityFactor = smallFacilityFactor;
    }

    public double getFacilitySizeFactor(){
        if(getHealthcareFacilityCategory() == LARGE_FACILITY)
            return getLargeFacilityFactor();
        return getSmallFacilityFactor();

    }


   public String toString(){
       return this.getID()+"\tmax_occ="+this.getMaxCapacity()+"\tocc="+this.getOccupancy()+"\tover_occ="+this.getOverCapacityAmount();
   }


}
