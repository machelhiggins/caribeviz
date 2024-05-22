package com.uwiseismic.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import com.uwiseismic.kmeans.CentroidGroup2D;
import com.uwiseismic.kmeans.Clusterable2D;

/**
 * This class is mostly used by <code>KMeansClustering></code>
 * 
 * 
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public class CentroidGroup2DMeanEuclidDistValue extends CentroidGroup2D{	

	
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
	    System.out.println("Mean value for this B is :"+meanValue+ " cuz group size = "+nodesGrp.size());
	    for(Iterator <Clusterable2D>i = nodesGrp.iterator(); i.hasNext();){
            sos += Math.pow(meanValue - i.next().getValue(),2);
        }
	    return sos;
	}

}
