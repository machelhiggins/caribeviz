/*
 * Created on Jan 22, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.analysis.efacility;

import java.util.ArrayList;

public abstract class DefaultFacility implements Facility{

	protected double rankings[];
	protected String id;
	private double x = 0;
	private double y = 0;
	protected double occupancy = 0;
	protected int maxCapacity = 0;
	protected double overCapacityAmount = 0;	
	private boolean decommissioned = false;

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Facility#setID(java.lang.String)
     */
    public void setID(String id) {
        this.id = id;
    }


    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Facility#getID()
     */
    public String getID() {
        return id;
    }


    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Facility#setX(double)
     */
    public void setX(double x) {
        this.x = x;
    }


    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Facility#getX()
     */
    public double getX() {
        return x;
    }


    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Facility#setY(double)
     */
    public void setY(double y) {
        this.y = y;
    }


    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Facility#getY()
     */
    public double getY() {
        return y;
    }

    /**
     *
     * Add occupancy to this facility
     * @param addition
     * @return Less than <code>addition</code> if maxOccupancy is exceed and zero if addition was accepted
     */
    public double addOccupancy(double addition, boolean allowOvercapcity){
    	if(isDecommissioned())
    		return addition;
        if(addition+occupancy > maxCapacity){        	
            double overCapAmnt =  (addition+occupancy) - maxCapacity;
            occupancy = maxCapacity;
            if(allowOvercapcity){
            	overCapacityAmount += overCapAmnt;
            	return 0;
    		}else
            	return overCapAmnt;
        }

        occupancy+=addition;
        return 0;
    }

    /**
     * Get total occupancy of this facility
     *
     * @param addition
     * @return
     */
    public double getOccupancy(){
    	if(isDecommissioned())
			return DECOMMISSIONED_OCC;
        return occupancy;
    }


    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Facility#setOccupancy(double)
     */
    public void setOccupancy(double occupancy){
        this.occupancy = occupancy;
    }

    /**
     * @return the maxCapacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * @param maxCapacity the maxCapacity to set
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

	/* (non-Javadoc)
	 * @see com.uwiseismic.analyis.efacility.Facility#getOverCapacityAmount()
	 */
	public double getOverCapacityAmount() {
		return overCapacityAmount;
	}
	
	/* (non-Javadoc)
	 * @see com.uwiseismic.ergo.analysis.efacility.Facility#isDecommissioned()
	 */
	public boolean isDecommissioned() {
		return decommissioned;
	}

	/* (non-Javadoc)
	 * @see com.uwiseismic.ergo.analysis.efacility.Facility#setDecommissioned(boolean)
	 */
	public void setDecommissioned(boolean decommissioned) {
		this.decommissioned = decommissioned;
	}
}
