package com.uwiseismic.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.uwiseismic.kmeans.Centroid2D;
import com.uwiseismic.kmeans.CentroidGroup2D;
import com.uwiseismic.kmeans.Clusterable2D;
import com.uwiseismic.kmeans.KMeansClustering;


/**
 * This class extends KMeansClustering by creating a bounding box for the kmeans
 * from the upper and lower coordiates of the weights array passed in the constructor.
 * 
 * @author <a href="mailto:machelhiggins.hotmail.com">Machel Higgins </a>
 *
 * TODO 
 */
public class KMeansClusteringBoundingMeanEuclidDist extends KMeansClustering{
		
	private Clusterable2D weights[];
	private final int HALTING_NUMBER = 2;
	private final int HALTING_SUM_OF_SQUARE_ITER= 3;
	//private final int HALTING_SUM_OF_SQUARE_ITER= 1000;
	private final double SUM_OF_SQUARE_TOLERANCE = 0.000001;
	//private final double SUM_OF_SQUARE_TOLERANCE = 0.01;
	private int currentUnchangedIter = 0;
	private int currentUnchangedIerSoS = 0;
	private Centroid2D oldMeans[];
	private int numberOfNonMatching = 0;	
	
	public KMeansClusteringBoundingMeanEuclidDist(ArrayList <Clusterable2D>weightsArr, int k, double threshold, int iter, Random rng){
	    
		this.k = k;		
		this.threshold = threshold;
		this.iter = iter;
		this.groups = new CentroidGroup2DMeanEuclidDistValue[k];
		if(rng == null)
			this.rng = new Random(System.currentTimeMillis());
		else
			this.rng = rng;
		
		weights = new Clusterable2D[weightsArr.size()];
		weightsArr.toArray(weights);
		
		//** find a bounding box for this section of SOM map ***
		//** AND max of all weights
		double lowerX = Double.MAX_VALUE;
		double lowerY = Double.MAX_VALUE;
		double upperX = -1*Double.MAX_VALUE;
		double upperY = -1*Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double mean = 0;
        double val;
		Clusterable2D comp;
		for(Iterator <Clusterable2D>i = weightsArr.iterator(); i.hasNext();){
			comp = i.next();
			if(comp.getX() > upperX)
				upperX = comp.getX();
			if(comp.getX() < lowerX)
				lowerX = comp.getX();
			if(comp.getY() > upperY)
				upperY = comp.getY();
			if(comp.getY() < lowerY)
				lowerY = comp.getY();
			
			val =  comp.getValue();
            mean +=val;
            if(val > max)
                max = val;
           
		}
		mean /= weightsArr.size();

		
		//** Initialize centroids				
	
		double a = (upperX - lowerX)/Math.sqrt(k);
		double b = (upperY - lowerY)/Math.sqrt(k);
		int i = 0;
		double y = lowerY;
		double x = lowerX;
		double initMean;
		while(i < k){	    
	        groups[i] = new CentroidGroup2DMeanEuclidDistValue();		 
	        groups[i].setMeanX(x);
	        groups[i].setMeanY(y);
	        initMean = this.rng.nextDouble() > 0.5 ? -1*mean : mean;
	        groups[i].setMeanValue(this.rng.nextDouble()*initMean);
	        System.err.println("i = "+i+"\t"+groups[i].getMeanX()+", "+groups[i].getMeanY()+" "+groups[i].getMeanValue());
	        i++;
	        x+=a;	        
	        if(x > upperX){
	        	x = lowerX;
	        	y+=b;
	        }
	        if(y > upperY) y = lowerY;
		}	
	}
	
	public void startAnalysis(){
	    int matchingCount;
	    numOfCPUs = Runtime.getRuntime().availableProcessors() - 2; //** leave one available
	    double lastSoS = 0;
		for(int z =0; z < iter; z++){

		    int n = 0;
		    FMRthreads = new FindMeanRunnerThread[numOfCPUs];

		    int num = weights.length/numOfCPUs;		   		   
		    int i;
		    for(i = 0;  i+num < weights.length; i+=num){
		        FMRthreads[n] = new FindMeanRunnerThread();
		        FMRthreads[n].startIndex = i;
		        FMRthreads[n].endIndexExclusive = i+num;
		        n++;		        		        
		    }		    
		    FMRthreads[n] = new FindMeanRunnerThread();
		    FMRthreads[n].startIndex = i;
		    FMRthreads[n].endIndexExclusive = weights.length;                                                                        
//		    try{
	        for(i = 0;  i < FMRthreads.length; i++)
	            FMRthreads[i].start();
//		    }catch(NullPointerException np){
//		    	np.printStackTrace();
//		    }
	        while(!findMeanRunnerThreadDone()){
	            try{Thread.sleep(100);}catch(Exception ex){}
	        }
	            
			recalculateMeans();

            
			//** check if halting conditions met
	        matchingCount = 0;	        
            Centroid2D cents[] = getMeansCentroids();
            for(i =0; i < groups.length; i++){
                if(oldMeans != null 
                        && oldMeans[i].compareTo(cents[i]) == 0){                    
                    matchingCount++;
                }
            }                       
            if(matchingCount == cents.length){            
                currentUnchangedIter++;
                if(currentUnchangedIter == HALTING_NUMBER)
                    z = iter;                   
            }
            else
                currentUnchangedIter = 0;            
            
            if(oldMeans == null)
                oldMeans = new Centroid2D[cents.length];
            
            double sumOfSquares = 0;
            double N = 0;
            for(i =0; i < groups.length; i++)
                sumOfSquares += groups[i].getSquares();
            //sumOfSquares /= weights.length;
            double rem = lastSoS > sumOfSquares ? lastSoS%sumOfSquares : sumOfSquares%lastSoS;
            if(Double.isNaN(rem))rem = 0;
            System.out.println(rem);
            if(rem < SUM_OF_SQUARE_TOLERANCE){
                currentUnchangedIerSoS++;
                System.out.println("Sum of squares is below threshold");
                if(currentUnchangedIerSoS == HALTING_SUM_OF_SQUARE_ITER){
                    z = iter;
                }
            }else
                currentUnchangedIerSoS = 0;
            lastSoS = sumOfSquares;
            System.out.println("DEBUG kmeans iteration "+z+"\tUnchanged means "+matchingCount+"\t Sum of Squares = \t"+sumOfSquares);
            
            System.arraycopy(cents, 0, oldMeans, 0, cents.length);
            
		}
		discardEmptyGroups();
		done = true;		
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
				System.err.println("Value within threshold = "+groups[i].getMeanValue() + " - " + dp.getValue());
				continue;
			}			
			dist = Math.sqrt(Math.pow(groups[i].getMeanX() - dp.getX(),2)
				+ Math.pow(groups[i].getMeanY() - dp.getY(),2)
				+ Math.pow(groups[i].getMeanValue() - dp.getValue(),2));
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
	 * Speed up finding mean. Getting bogged down with 50k clusterables
	 * @author <a href="mailto:machelhiggins.hotmail.com">Machel Higgins </a>
	 *
	 * TODO 
	 */
	private class FindMeanRunnerThread extends Thread{
	    public int startIndex;
	    public int endIndexExclusive;
	    public boolean jobDone = false;
	    public void run(){
           for(int i = startIndex; i <endIndexExclusive; i++)
               findMean(weights[i]);               
           jobDone = true;           
	    }
	}
	private FindMeanRunnerThread FMRthreads[];
	private int numOfCPUs  = -1;
	private int frInd = 0;
	
	private boolean findMeanRunnerThreadDone(){
	    for(frInd =0; frInd < FMRthreads.length; frInd++)
	        if(!FMRthreads[frInd].jobDone)
	            return false;
	    return true;	        
	}
}
