/*
 * Created on Jan 22, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.analysis.efacility;

public class ShelterFacility extends DefaultFacility{

	private int meanOccupancyRate = 0;


    public ShelterFacility(double lat, double longi, int maxCapacity, int meanOccupancyRate){
        this.setX(longi);
        this.setY(lat);
        setMaxCapacity(maxCapacity);
        this.meanOccupancyRate = meanOccupancyRate;
    }

	/**
	 * @return
	 */
	public int getMeanOccupancyRate() {	
		if(isDecommissioned())
			return DECOMMISSIONED_OCC;
		return meanOccupancyRate;
	}
	  
	
	/**
	 * @param meanOccupancyRate
	 */
	public void setMeanOccupancyRate(int meanOccupancyRate) {
		this.meanOccupancyRate = meanOccupancyRate;
	}



}
