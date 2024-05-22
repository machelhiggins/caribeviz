package com.uwiseismic.kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class is mostly used by <code>KMeansClustering></code>
 * 
 * 
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public class CentroidGroup2D {	
	//private ArrayList<Clusterable2D> nodesGrp = new ArrayList<Clusterable2D>();
	protected Vector<Clusterable2D> nodesGrp = new Vector<Clusterable2D>();
	
	protected double meanX;
	protected double meanY;
	
	protected double modeX;
	protected double modeY;
	protected double meanValue;
	protected double modeValue = -1;
	
	/**
	 * @param newDataPoint
	 */
	public void addDataPoint(Clusterable2D newDataPoint){

		nodesGrp.add(newDataPoint);
		if(newDataPoint.getCentroidGroup() != null)
		    newDataPoint.getCentroidGroup().removeDataPoint(newDataPoint);
		newDataPoint.setCentroidGroup(this);		     			
	}
	
	/**
	 * @param dataPoint
	 */
	public void removeDataPoint(Clusterable2D dataPoint){
		nodesGrp.remove(dataPoint);
	}
	
	/**
	 * @return
	 */
	public Vector <Clusterable2D> getDataPoints(){
		return nodesGrp;
	}

	/**
	 * @return
	 */
	public double getMeanX() {
		return meanX;
	}

	/**
	 * @param meanX
	 */
	public void setMeanX(double meanX) {
		this.meanX = meanX;
	}

	/**
	 * @return
	 */
	public double getMeanY() {
		return meanY;
	}

	/**
	 * @param meanY
	 */
	public void setMeanY(double meanY) {
		this.meanY = meanY;
	}

	/**
	 * @return
	 */
	public double getMeanValue() {
		return meanValue;
	}

	/**
	 * @param meanValue
	 */
	public void setMeanValue(double meanValue) {
		this.meanValue = meanValue;
	}
	
	/**
	 * 
	 */
	public void calculateMean(){
		if(nodesGrp.size() == 0)
			return;	
		meanX = 0;		
		meanY = 0;
		meanValue = 0;	

		for(Iterator <Clusterable2D>i = nodesGrp.iterator(); i.hasNext();){
			Clusterable2D obj = i.next();
    			meanX += obj.getX();
    			meanY += obj.getY();
    			meanValue += obj.getValue();
		}		
		meanX /= nodesGrp.size();
		meanY /= nodesGrp.size();
		meanValue /= nodesGrp.size();
	}		
	
	/**
	 * @return
	 */
	public double getModeValue() {
		if(modeValue != -1)
			return modeValue;
		double freq[] = new double[nodesGrp.size()];
		double vals [] = new double[nodesGrp.size()];
		modeValue = 0;	
		if(nodesGrp.size() == 0)
			return -1;	

		int n = 0;
		for(Iterator <Clusterable2D>i = nodesGrp.iterator(); i.hasNext();n++)
			vals[n] = i.next().getValue();			
		
		Arrays.sort(vals);
		int c = 0;
		double lastVal = -1;
		for(n =0; n < vals.length;n++){
			if(vals[n] != lastVal){
				lastVal = vals[n];
				c = 0;
			}
			c++;
			freq[n] = c;
		}
		
		lastVal = 0;
		c = 0;
		for(n =0; n < vals.length;n++){
			if(freq[n] > lastVal){
				lastVal = freq[n];
				c = n; 
			}
		}
		modeValue = vals[c];
		return modeValue;
	}
	
	/**
	 * @return
	 */
	public double getSquares(){
	    double sos = 0;
	    calculateMean();
	    for(Iterator <Clusterable2D>i = nodesGrp.iterator(); i.hasNext();){
            sos += Math.pow(meanValue - i.next().getValue(),2);
        }
	    return sos;
	}

	/**
	 * @return
	 */
	public double getNumberOfWeights(){
	    return nodesGrp.size();
	}
	
	public double getGroupTotalValue(){
	    double totes = 0;
	    for(Iterator <Clusterable2D>i = nodesGrp.iterator(); i.hasNext();){
            Clusterable2D t = i.next();
            if(t.isLogValue())
                totes += Math.exp(t.getValue());
            else
                totes += t.getValue();
        }       
	    
	    return totes;
	}
}
