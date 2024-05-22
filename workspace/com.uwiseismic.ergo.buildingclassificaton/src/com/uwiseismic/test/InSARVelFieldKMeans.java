package com.uwiseismic.test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.uwiseismic.kmeans.Centroid2D;
import com.uwiseismic.kmeans.Clusterable2D;
import com.uwiseismic.kmeans.KMeansClustering;
import com.uwiseismic.kmeans.KMeansClusteringBounding;

//import com.
public class InSARVelFieldKMeans {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String binFile = "C:/Users/machel/Documents/vbox_share/fedora_22/coastal_sub/gulf_coast/p121_f85_stamps/stamps_workspace/filtering/hi_pass_intf_600_m_SBAS_4x_masked.bin";
//		String binFile = "C:/Users/machel/Documents/vbox_share/fedora_22/coastal_sub/gulf_coast/p121_f85_stamps/stamps_workspace/filtering/tmp.gmt";
//		String clusterFile = "C:/Users/machel/Documents/vbox_share/fedora_22/coastal_sub/gulf_coast/p121_f85_stamps/stamps_workspace/filtering/hi_pass_intf_600_m_SBAS_4x_masked_CLUSTERS_thresh-0.05_v3.gmt";
		String binFile = "C:/Users/machel/Documents/vbox_share/fedora_22/coastal_sub/gulf_coast/p121_f85_stamps/stamps_workspace/filtering/tmp2.gmt";
		String clusterFile = "C:/Users/machel/Documents/vbox_share/fedora_22/coastal_sub/gulf_coast/p121_f85_stamps/stamps_workspace/filtering/shiite.txt";
		int height = 6119;
	    int width = 3303;
	    int maxKMeans = 1000;
	    double thresh = 200;
	    int iters = 10000;
	    double max = Double.MIN_VALUE;
	    double min = Double.MAX_VALUE;
	    double latDBG;
	    ArrayList <Clusterable2D>pixels = new ArrayList<Clusterable2D>();
	    try{ 
	    	float val = 0;
	    	BufferedReader in = new BufferedReader(new FileReader(binFile));
	    	String buffer = in.readLine();;
	    	String cols[];
	    	while(buffer !=  null){	    		
    			cols = buffer.split("\\s+");   			
    			val = Float.parseFloat(cols[2]);
    			if(!cols[2].matches("NaN")){

    				latDBG = Double.parseDouble(cols[1]);
    				if(latDBG > max){ max = latDBG;}
    				if(latDBG < min){ min = latDBG;}
	    			pixels.add(new InSARClusterable2D(Double.parseDouble(cols[0]), 
	    					Double.parseDouble(cols[1]),
	    					(double)val));	
    			}	    			
  
    			buffer = in.readLine();
	    	}
	    	in.close();
	    	System.out.println("min lat = "+min+" max lat = "+max+" size = "+pixels.size());
	    	KMeansClustering kmeans = new KMeansClusteringBoundingMeanEuclidDist(pixels, maxKMeans, thresh, iters, null);

	    	kmeans.startAnalysis();
	    	
	    	BufferedWriter out = new BufferedWriter(new FileWriter(clusterFile));
	    	Centroid2D cents[] = kmeans.getMeansCentroids();
            for(int ii = 0;  ii < cents.length; ii++){
                System.out.println(cents[ii]);
                out.append(cents[ii].toString()+"\n");
            }
            out.close();

	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }
	}
}
