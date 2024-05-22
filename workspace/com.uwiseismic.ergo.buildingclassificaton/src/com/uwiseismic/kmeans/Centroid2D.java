package com.uwiseismic.kmeans;

/**
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public class Centroid2D implements Comparable{
	
	private double x;
	private double y;
	private double value;
	private double totalValue;
	private Clusterable2D valueObj;
	
	/**
	 * @return
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * @return
	 */
	public double getY() {
		return y;
	}
	/**
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Returns double value if clustering using scalar
	 * 
	 * @return
	 */
	public double getDoubleValue() {
		return value;
	}
	
	/**
	 * Set double value if clustering using scalar
	 * @param value
	 */
	public void setDoubleValue(double value) {
		this.value = value;
	}

	/**
	 * 
	 * Set the Clusterable2D implementation value of this mean
	 * @param valueObj
	 */
	public void setValue(Clusterable2D valueObj){
		this.valueObj = valueObj;
	}
	
	/**
	 * Returns the scalar total value of elements in cluster this mean represents(MUST IMPLEMENT FOR Clusterable2D values)
	 * 
	 * @return
	 */
	public double getTotalValue(){
		return totalValue;
	}
	
	/**
	 * Set the scalar total value of elements in cluster this mean represents(MUST IMPLEMENT FOR Clusterable2D values)
	 * 
	 * @param totalValue
	 */
	public void setTotalValue(double totalValue){
		this.totalValue = totalValue;
	}
	
	/**
	 * returns the Clusterable2D implementation value of this mean
	 * @return
	 */
	public Clusterable2D getValue(){
		return valueObj;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
	    return x+","+y+","+value;
	}

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        Centroid2D a = (Centroid2D)arg0;
        if(a.getX() == getX() && a.getY() == getY() && a.getValue() == getValue())
            return 0;
        else if (a.getDoubleValue() > value)
            return -1;
        else 
            return 1;
    }
	
}
