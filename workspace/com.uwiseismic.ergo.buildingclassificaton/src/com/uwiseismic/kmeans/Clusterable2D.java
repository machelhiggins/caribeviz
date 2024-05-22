package com.uwiseismic.kmeans;
/**
 * Inteface class for 2D and value data-poiunts to perform Kmeans clustering on
 * 
 * 
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public interface Clusterable2D extends Comparable{

	/**
	 * The CentroidGroup that this data point belongs to
	 * 
	 * @return
	 */
	public CentroidGroup2D getCentroidGroup();
	
	/**
	 * Set the CentroidGroup that this data point belongs to
	 */
	public void setCentroidGroup(CentroidGroup2D centroidGroup);
	
		
	/**
	 * Returns value at this x,y
	 * 
	 * @return
	 */
	public double getValue();
	
	/**
	 * Get x coord of this data point
	 * 
	 * @return
	 */
	public double getX();
	
	/**
	 * Get y coord of this data point
	 * 
	 * @return
	 */
	public double getY();
	
    /**
     * To contrast data, log values can be used
     * 
     * @return the logValue
     */
    public boolean isLogValue();
    
    /**
     * To contrast data, log values can be used
     * @param logValue the logValue to set
     */
    public void setLogValue(boolean logValue);
	
}
