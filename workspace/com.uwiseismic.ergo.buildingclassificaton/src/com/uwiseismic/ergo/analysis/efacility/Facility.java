/*
 * Created on Jan 26, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.analysis.efacility;

import java.util.ArrayList;

public interface Facility{


    /**
     * Set facility ID
     * @param id
     */
    public void setID(String id);

    /**
     * Get facility ID
     *
     * @return
     */
    public String getID();


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
	 * Add occupancy to this facility
	 * @param addition
	 * @return Less than <code>addition</code> if maxOccupancy is exceed and zero if addition was accepted
	 */
	public double addOccupancy(double addition, boolean allowOvercapcity);

	/**
	 * Get total occupancy of this facility
	 *
	 * @param addition
	 * @return
	 */
	public double getOccupancy();

	/**
	 * Set the occupancy of this facility
	 *
	 * @param occupancy
	 */
	public void setOccupancy(double occupancy);

	  /**
     * @return the maxCapacity
     */
    public int getMaxCapacity();

    /**
     * @param maxCapacity the maxCapacity to set
     */
    public void setMaxCapacity(int maxCapacity);


    /**
     * Returns amount by which this facility is over capacity
     * @return
     */
    public double getOverCapacityAmount();

    
	/**
	 * Returns if you can add occupants to this facility
	 * @return
	 */
	public boolean isDecommissioned();

	/**
	 * If facility is damaged, decommission it.
	 * @param decommissioned
	 */
	public void setDecommissioned(boolean decommissioned);
	
    
    /**
     * Constant occupancy to for this facility when decommissioned 
     */
    public static int DECOMMISSIONED_OCC = -1;
}
