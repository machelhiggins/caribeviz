package com.uwiseismic.kmeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;


/**
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public class KMeansClustering extends Thread{
	
	protected int k = 0;	
	protected double threshold = 0;
	protected int iter = 500;
	protected Clusterable2D weights[];
	protected CentroidGroup2D groups[];
	private int numberOfNonMatching = 0;	
	protected Random rng;
	protected boolean done = false;
	
	public KMeansClustering(){
		//no op
	}
	
	/**
	 * @param weightsArr array of weights.
	 * @param k Number of means
	 * @param threshold Threshold to test if Cluserable2D belongs to a kmean
	 * @param iter Number of iterations
	 * @param rng
	 */
	public KMeansClustering(ArrayList <Clusterable2D>weightsArr, int k, double threshold, int iter, Random rng){
		
		if(rng == null)
			rng = new Random(System.currentTimeMillis());
		
		this.weights = new Clusterable2D[weightsArr.size()];
        weightsArr.toArray(weights);
		this.k = k;		
		this.threshold = threshold;
		this.iter = iter;
		groups = new CentroidGroup2D[k];
		
		//** max of all weights
		double max = Double.MIN_VALUE;
		double val;
		for(Iterator <Clusterable2D>i = weightsArr.iterator(); i.hasNext();){
		    val =  i.next().getValue();
		    if(val > max)
		        max = val;
		}		
				
		//** Initialize centroids
		double coords[][] = new double[weightsArr.size()][2];
		int n =0;
		Clusterable2D comp;
        for(Iterator <Clusterable2D>i = weightsArr.iterator(); i.hasNext();){
            comp = i.next();            
            coords[n][0] = comp.getX();
            coords[n][1] = comp.getY();
            n++;
        }
		
        for(int i = 0; i < k; i++){
            n = rng.nextInt(coords.length);
            groups[i] = new CentroidGroup2D();          
            groups[i].setMeanX(coords[n][0]);
            groups[i].setMeanY(coords[n][1]);
            groups[i].setMeanValue(max*(double)i/(double)k); //** values can only be between 0 and 1    
        }					
	}
	
	/**
	 * Find kmeans
	 */
	public void startAnalysis(){
		for(int z =0; z < iter; z++){		    
			for(int i = 0; i < weights.length; i++){
				findMean(weights[i]);								
			}		
			recalculateMeans();
		}
		discardEmptyGroups();
		done = true;
	}
	
	/** Calls <cod> startAnalysis() <code> in separate thread
	 * 
	 */
	public void run(){
		startAnalysis();
	}
	
	/**
	 * @param dp
	 */
	protected void findMean(Clusterable2D dp){
		double minDist = Double.MAX_VALUE;
		int minDistIndex = -1;
		double dist;
		double valDiff;
		for(int i =0; i < groups.length; i++){			
			valDiff = Math.abs(groups[i].getMeanValue() - dp.getValue());
			//** if value is within threshold
			if(valDiff > threshold){		
				continue;
			}			
			dist = Math.sqrt(Math.pow(groups[i].getMeanX() - dp.getX(),2)
				+ Math.pow(groups[i].getMeanY() - dp.getY(),2));
			if(dist < minDist){
				minDist = dist;
				minDistIndex = i;
			}												
		}
		if( minDistIndex == -1){
			numberOfNonMatching++;
		}
		else{			
			groups[minDistIndex].addDataPoint(dp);
		}
	}

	
	/**
	 * 
	 */
	protected void recalculateMeans(){
		for(int i =0; i < k; i++)
			groups[i].calculateMean();		
	}

	/**
	 * Return number of discarded kmeans
	 * 
	 * @return
	 */
	public int getNumberOfNonMatching() {
		return numberOfNonMatching;
	}
	
	/**
	 * Returns centre of gravity for each cluster as <code> Centroid2D</code> objects
	 * @return
	 */
	public Centroid2D[] getMeansCentroids(){        
		Centroid2D centroids[] = new Centroid2D[groups.length];
		for(int i =0; i < groups.length; i++){		 
		    
			centroids[i] = new Centroid2D();
			centroids[i].setX(groups[i].getMeanX());
			centroids[i].setY(groups[i].getMeanY());
			centroids[i].setDoubleValue(groups[i].getMeanValue());
			centroids[i].setValue(getMeanObject(groups[i]));
			centroids[i].setTotalValue(getGroup2DTotalValue(groups[i]));
		}
			
		return centroids;
	}
	
	/**
	 * Returns modes for each cluster as <code> Centroid2D</code> objects
	 * @return
	 */
	public Centroid2D[] getModesCentroids(){
		Centroid2D centroids[] = new Centroid2D[groups.length];
		for(int i =0; i < groups.length; i++){
			centroids[i] = new Centroid2D();
			centroids[i].setDoubleValue(groups[i].getModeValue());
			centroids[i].setValue(getModeObject(groups[i]));
		}
		return centroids;
	}
	
	/**
	 * Returns Clusterable2D object that is centre of gravity of cluster 
	 * 
	 * @param group
	 * @return
	 */
	public Clusterable2D getMeanObject(CentroidGroup2D group){
	    Vector<Clusterable2D> nodes= group.getDataPoints();
		double meanDoubleValue = group.getMeanValue();
		int indexOfNearest = -1;
		double minDist = Double.MAX_VALUE;
		double testDist;
		for(int i=0; i <  nodes.size(); i++){
			Clusterable2D test = nodes.get(i);
			testDist = Math.abs(test.getValue() - meanDoubleValue);			
			if(testDist < minDist){
				indexOfNearest = i;
				minDist = testDist;
			}
		}			
		if(indexOfNearest == -1){			
			return null;
		}
		return nodes.get(indexOfNearest);		
	}

	/**
	 * The total scalar value of the cluster
	 * 
	 * @param group
	 * @return
	 */
	public double getGroup2DTotalValue(CentroidGroup2D group){		
		return group.getGroupTotalValue();
	}
	
	
	
	/**
	 * Returns mode for cluster as <code> Centroid2D</code> objects
	 * 
	 * @param group
	 * @return
	 */
	public Clusterable2D getModeObject(CentroidGroup2D group){
	    Vector<Clusterable2D> nodes= group.getDataPoints();
		double modeDoubleValue = group.getModeValue();
		int indexOfNearest = -1;
		double minDist = Double.MAX_VALUE;
		double testDist;
		for(int i=0; i <  nodes.size(); i++){
			Clusterable2D test = nodes.get(i);
			testDist = Math.abs(test.getValue() - modeDoubleValue);			
			if(testDist < minDist){
				indexOfNearest = i;
				minDist = testDist;
			}
		}			
		if(indexOfNearest == -1){			
			return null;
		}
		return nodes.get(indexOfNearest);		
	}
	
	/**
	 * If performing analysis in separate thread, you'll want to know when its done.
	 * 
	 * @return
	 */
	public boolean getDone(){
		return done;
	}
	
	/**
	 * Discard unmatched kmeans
	 */
	protected void discardEmptyGroups(){
		CentroidGroup2D temp[] = new CentroidGroup2D[k];
		int n =0 ;
		for(int i =0; i < groups.length ; i++){
			if(groups[i].getDataPoints().size() != 0){
				temp[n] = groups[i];
				n++;
			}
		}
		groups = new CentroidGroup2D[n];
		System.arraycopy(temp, 0, groups, 0, n);
	}
	
}





