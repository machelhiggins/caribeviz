package com.uwiseismic.test;

import com.uwiseismic.kmeans.CentroidGroup2D;
import com.uwiseismic.kmeans.Clusterable2D;

public class InSARClusterable2D implements Clusterable2D{
	
    private CentroidGroup2D centroidGroup;
    private double x = Double.MAX_VALUE;
    private double y = Double.MAX_VALUE;
    private double value = 0;
    private boolean logValue = false;
	
    public InSARClusterable2D(double x, double y, double value){
    	this.x = x;
    	this.y = y;
    	this.value = value;
    }
    
    public CentroidGroup2D getCentroidGroup() {
        return centroidGroup;
    }

    public double getValue() {
        if(logValue){
            if(value == 0)
                return 0;
            return Math.log(value);
        }else
            return value;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setCentroidGroup(CentroidGroup2D centroidGroup) {
        this.centroidGroup = centroidGroup;
    }
	
    /**
     * @return the logValue
     */
    public boolean isLogValue() {
        return logValue;
    }

    /**
     * @param logValue the logValue to set
     */
    public void setLogValue(boolean logValue) {
        this.logValue = logValue;
    }
	
	
    public int compareTo(Object arg0) {
        Clusterable2D a = (Clusterable2D)arg0;
        if(a.getX() == getX() && a.getY() == getY() && a.getValue() == getValue())
            return 0;
        else if (a.getValue() > getValue())
            return -1;
        else
            return 1;
    }

}
