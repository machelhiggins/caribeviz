package com.uwiseismic.ergo.analysis.efacility.gis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.uwiseismic.ergo.analysis.efacility.Facility;
import com.uwiseismic.ergo.analysis.efacility.ShelterFacilityRanker;
import com.uwiseismic.ergo.analysis.efacility.Mean;
import com.uwiseismic.ergo.analysis.efacility.PopDislocationMean;
import com.uwiseismic.ergo.analysis.efacility.ShelterFacility;
import com.uwiseismic.ergo.roadnetwork.RoadGraph;
import com.uwiseismic.ergo.roadnetwork.RoadNetworkToGraph;
import com.uwiseismic.gis.util.ObjectToReal;
import com.uwiseismic.kmeans.Centroid2D;
import com.uwiseismic.kmeans.Clusterable2D;
import com.uwiseismic.kmeans.ClusterableException;
import com.uwiseismic.kmeans.FeatureClusterable;
import com.uwiseismic.kmeans.KMeansClustering;
import com.uwiseismic.kmeans.KMeansClusteringBounding;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class ShelterFacilityAnalysis {

	protected ArrayList <Facility> facilities = new ArrayList<Facility>();
	protected ArrayList <Mean> popDislocationMeans = new ArrayList<Mean>();
	private FeatureCollection roads;
	protected ShelterFacilityRanker ranker = new ShelterFacilityRanker();
	private double smallFacilityWeight = -1;
	private  CoordinateReferenceSystem crs;
	private int numOfCPUs = -1;
	private PopDistRunner threads[];

	public void shelterOccupancyAnalysis(IProgressMonitor monitor, int totalWorkUnits, SimpleFeatureCollection roads,
			SimpleFeatureCollection popDislocated){
		this.roads = roads;

		//** preprocess roads to creating graph
		RoadNetworkToGraph procRoad = new RoadNetworkToGraph(roads);
		RoadGraph graph = procRoad.processRoadNetwork();
        ranker.setGraph(graph);        

		try {
			if(monitor != null)
	        	monitor.worked((int)Math.floor((double)totalWorkUnits/4.0));
			
	        //**  Number of Kmeans has been hardcoded to be no of popDislocated/100 if no of popDislocated is greater than 10000.
            //** If less then number of kmeans is no of popDislocated/10.
		    int numMeans = 0;
		    if(popDislocated.getBounds().getArea() <= 0.03)// degrees squared, 0.03 works for cities like kingston and portmore
		    	numMeans = popDislocated.size() > 10000 ? popDislocated.size()/100 : popDislocated.size() /10;
		    else{
		    	numMeans = 400*(int)Math.ceil(popDislocated.getBounds().getArea()/0.03);			    
		    }		    
            this.findFacilitiesAndPopCasualtyKMeans(monitor, (int)Math.floor((double)totalWorkUnits/4.0), 
            		(int)Math.ceil(numMeans), popDislocated);
            
            if(monitor != null)
	        	monitor.worked((int)Math.floor((double)totalWorkUnits/4.0));         

            //** distribute popKmeans to facilities
            //** for debug purposs not using threads
            for(Mean tm : popDislocationMeans){
//            	if(popDislocationMeans.size() < 100) //** for some reason if the mean number is low this method returns while thread running ...
            		tm.getPopulationDistribution();            		
//            	else{	
//	                //** perform pop distribution based on ranking and whatever logic
//	                while(!checkInPopDistRunner(tm))
//	                    try{Thread.sleep(200);}catch(Exception ex){ex.printStackTrace();}
//            	}
            }

            //** wait on threads to finish
            while(!donePopDistRunners())
                try{Thread.sleep(100);}catch(Exception ex){ex.printStackTrace();}
            
            if(monitor != null)
	        	monitor.worked((int)Math.floor((double)totalWorkUnits/4.0));

        } catch (NoSuchElementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClusterableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	public DefaultFeatureCollection getEmergencyShelterFacilties(){
		//** wait on threads to finish
        while(!donePopDistRunners())
            try{Thread.sleep(100);}catch(Exception ex){ex.printStackTrace();}
        
		//** Build Feature collection
		DefaultFeatureCollection fc;
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		
        //** feature type (feature structure)
        StringBuffer featureTypeString = new StringBuffer();
        featureTypeString.append("the_geom:Point");
        featureTypeString.append(",");
        featureTypeString.append("lat:Double");
        featureTypeString.append(",");
        featureTypeString.append("lon:Double");
        featureTypeString.append(",");
        featureTypeString.append("efacil_nam:String");
        featureTypeString.append(",");
        featureTypeString.append("max_occ:Double");
        featureTypeString.append(",");
        featureTypeString.append("occ:Double");
        featureTypeString.append(",");
        featureTypeString.append("over_cap:Double");

        SimpleFeatureType featureType = null;

        try {
            featureType = DataUtilities.createType("facilities_utilization_WGS84",
                    featureTypeString.toString());
        } catch (SchemaException e1) {
            e1.printStackTrace();
            return null;
        }

		;
			
		for(Facility t : facilities){

			Point geomType = new GeometryFactory().createPoint(new Coordinate(t.getX(),t.getY()));

	        //** Set attribute type information

			//** attribute values
			ArrayList values = new ArrayList();
            values.add(geomType);
            values.add(new Double(t.getY()));
            values.add(new Double(t.getX()));
            values.add(t.getID());
            values.add(new Double(t.getMaxCapacity()));
            values.add(new Double(t.getOccupancy()));
            values.add(new Double(t.getOverCapacityAmount()));
System.out.println("FROM getEmergencyShelterFacilties METHOD "+t.getOccupancy());

			SimpleFeature feature;
            //feature = featureType.create(value.toArray());            	
        	feature = SimpleFeatureBuilder.build( featureType, values, "fid" );
            feature.setDefaultGeometry(geomType);
            features.add(feature);


		}
		//** create feature collection
		fc = new DefaultFeatureCollection();
		
		for(Iterator <SimpleFeature> iter = features.iterator();iter.hasNext();){
			List<Object> data = new LinkedList<Object>();
			SimpleFeature f = iter.next();
			List<Object> values = f.getAttributes();
			for (Object obj: values) {
				data.add(obj);
			}
			SimpleFeature newf = SimpleFeatureBuilder.build(featureType, data, null);
			fc.add(newf);

		}	
		return fc;
	}


    public FeatureCollection getRoads() {
		return roads;
	}

	public void setRoads(FeatureCollection roads) {
		this.roads = roads;
	}

	/**
	 * Reweigh smaller facilities (less that 50 beds) when considering ranking and pop distribution
	 *
	 * @return
	 */
	public double getSmallFacilityWeight() {
		return smallFacilityWeight;
	}

	/**
	 * Reweigh smaller facilities (less that 50 beds) when considering ranking and pop distribution
	 * @param smallFacilityWeight
	 */
	public void setSmallFacilityWeight(double smallFacilityWeight) {
		this.smallFacilityWeight = smallFacilityWeight;
	}

	/**
	 * 
	 * FYI this method is what decommissions shelter facilities with > 0.2 mean damage
	 * 
	 * @param monitor
	 * @param towork
	 * @param maxKMeans
	 * @param popDislocatedFC
	 * @throws NoSuchElementException
	 * @throws ClusterableException
	 */
	protected void findFacilitiesAndPopCasualtyKMeans(IProgressMonitor monitor, int towork,
    		int maxKMeans, FeatureCollection popDislocatedFC)throws NoSuchElementException, ClusterableException{
		
		/* Attributes to watch out for in popDislocated
		 * ------
		 * efacilty (boolean) - FOR EMERGENCY FACILITIES AND SUCH, NA HERE !!!!
		 * occ_type GOV6 - shelter
		 * popdisloc
	
		 */
    	try {

	         ArrayList <String> keys = new ArrayList<String>();
	         keys.add("popdisloc");


	         //** create featureclusterable objects and get stdev to determine kmeans threashold
	         ArrayList <Clusterable2D>featureWeights = new ArrayList<Clusterable2D>();
	         double mean = 0;
             double lat = 0;
             double longi = 0;
             int occRate = 0;
             int maxCap = 0;
             FeatureIterator i = (FeatureIterator) popDislocatedFC.features();
	         while(i.hasNext()){
	        	 SimpleFeature t = (SimpleFeature)i.next();
	        	 	        
	             FeatureClusterable featCl;
	             featCl = new FeatureClusterable(t, keys);
	             if(featCl.getValue() > 0.9){ //** arbritrarily cut off at 0.9 for fractional person we dislocated
	            	 featCl.setValue(Math.ceil(featCl.getValue()));	      	            	 
	            	 mean += featCl.getValue();
		             featureWeights.add(featCl);
	             }
	            	 

	             // ** find healthcare facilities
	             Object obj = t.getAttribute("occ_type");
	             if(obj instanceof String
	            		 && ((String)obj).matches("GOV6")){ //TODO: Change this!!!!!
	            	 obj = t.getAttribute("lat");	            
	            	 if(obj == null){
	            		 Geometry geom = (Geometry)t.getAttribute(0);
	            		 Point c = geom.getCentroid();
	            		 lat = c.getY();	            			            	 	            	
		                 longi = c.getX();
	            	 }
	            	 else{
	            		 lat = ObjectToReal.getMeDouble(obj);	            	         	 
		                 longi = ObjectToReal.getMeDouble(t.getAttribute("lon"));	 
	            	 }	            	 
	                 occRate = ObjectToReal.getMeInteger(t.getAttribute("ef_occ_rat"));	                 
	            	 maxCap = ObjectToReal.getMeInteger(t.getAttribute("max_occ"));
	            	 ShelterFacility sf  = new ShelterFacility(lat, longi, maxCap, occRate);
	            	 
	            	 /******************************************************************************************************/
	            	 /******************************************************************************************************/
	            	 /** Decommission facilities with 0.2 or mean damage
	            	  * 
	            	  * 
	            	  */
	            	 /******************************************************************************************************/
	            	 /******************************************************************************************************/
	            	 if(ObjectToReal.getMeDouble((t.getAttribute("meandamage"))) >= 0.6)
	            		 sf.setDecommissioned(true);	            	 	            			
	            	 /******************************************************************************************************/
	            	 /******************************************************************************************************/
	            	 
	            	 
	            	 obj = t.getAttribute("efacil_nam");
                     if(obj instanceof String)
                         sf.setID((String)obj);                     
	            	 facilities.add(sf);	        	
	             }
	         }	     
	        
	         System.out.println("[DEBUG] "+this.getClass().getName()+" Number of features with casualties = "+ featureWeights.size());
	         mean /= featureWeights.size();	         
	         double stdev = 0;
	         for(Clusterable2D it : featureWeights)
	             stdev += Math.pow(it.getValue() - mean, 2);
	         stdev = Math.sqrt(stdev/featureWeights.size());
	         //** if stdev is of two orders larger...use log values for FeatureClusterable ***
	         boolean usingLogValue = false;
	         if(stdev/mean > 10){        	 
	             usingLogValue = true;
	             for(Clusterable2D it : featureWeights)
	                 ((FeatureClusterable)it).setLogValue(true);
	             mean = Math.log(mean);
	             stdev = Math.log(stdev);
	         }
	         double m = 1.1;
	         //double m = 1.5;
	         double t = stdev/(m*2);
	         //double t = (2*stdev/maxKMeans);
	         
	         //** hardcoding iterations to  casualtiesFC.size()/100 because it was more than enough for the largest
	         //** building stock and no such thing as too much and kmeans always converge much faster than this limit
	         //KMeansClustering kmeans = new KMeansClusteringBounding(featureWeights, maxKMeans,
	         //		 stdev*m, casualtiesFC.size()/100, new Random(System.currentTimeMillis()));

	         /********************* DEBUG ************ REMOVE BELOW AND UNCOMMENT ABOVE *****************
	          * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	          */
	         //maxKMeans = 500;
	         
	         System.out.println("[DEBUG] "+this.getClass().getName() +" Mean = "+mean+" stdev = "+stdev+" using log vals = "+
	                     usingLogValue +"\t threshold = "+t);

	         KMeansClustering kmeans = new KMeansClusteringBounding(featureWeights, maxKMeans,
                     t, 1000, new Random(System.currentTimeMillis()));
             /********************* DEBUG ************ REMOVE ABOVE AND UNCOMMENT PREVIOUS *****************
              * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
              */

	         //long start = System.currentTimeMillis();
	         //System.err.println("[DEBUG] Starting kmeans for "+featureWeights.size()+" buildings popDislocated");
	         kmeans.startAnalysis();
	         //System.err.println("[DEBUG] For the entire region that took "+(System.currentTimeMillis() - start)/1000+" seconds");
	         Centroid2D cents[] = kmeans.getMeansCentroids();

	         for(int ii = 0;  ii < cents.length; ii++){
	        	 cents[ii].setTotalValue(Math.floor(cents[ii].getTotalValue()));
	        	 // ** if not a mean with center of gravity being zero
	        	 if(cents[ii].getTotalValue() > 0){
	        		 
	        		 System.out.println("[DEBUG] "+this.getClass().getName() 
	        				 +" "+cents[ii]+"\t Total val of cluster: "+cents[ii].getTotalValue());
	        		 
	        		 PopDislocationMean pm = new PopDislocationMean();
		        	 pm.setMeanValue(cents[ii].getTotalValue());
		        	 pm.setID(""+ii);
		        	 pm.setX(cents[ii].getX());
		        	 pm.setY(cents[ii].getY());	
		        	 //** ALWAYS CLONE ranker
		        	 pm.setFacilityRanker(ranker.clone());
		        	 pm.setFacilities(facilities);
		        	 pm.setGraph(ranker.getGraph());
		        	 popDislocationMeans.add(pm);
	        	 }
	         }
	         if(monitor != null)
	         	monitor.worked(towork);
	         
    	} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw e;
		} catch (ClusterableException e) {
			e.printStackTrace();
			throw e;
		}
    }



	private boolean checkInPopDistRunner(Mean m){
	    if(numOfCPUs == -1){
	        numOfCPUs = Runtime.getRuntime().availableProcessors() - 2; //** leave one available
	        threads = new PopDistRunner[numOfCPUs];
	    }
	    for(int i = 0; i < numOfCPUs; i++){
	        if(threads[i] == null){
	            threads[i] = new PopDistRunner();
	            threads[i].mean = m;
	            threads[i].start();
	            threads[i].setPriority(Thread.MAX_PRIORITY);
	            return true;
	        }
	        else if(threads[i].jobDone){
	            threads[i] = new PopDistRunner();
	            threads[i].mean = m;
	            threads[i].setPriority(Thread.MAX_PRIORITY);
                threads[i].start();
                return true;
	        }
	    }
	    return false;
	}

	private boolean donePopDistRunners(){
	    int n = 1;
	    for(int i = 0; i < numOfCPUs; i++){
            if(threads[i] == null)
                n++;
            else if(threads[i].jobDone)
                n++;
        }
	    return n >= numOfCPUs;
	}

    private class PopDistRunner extends Thread{
	    public Mean mean;
	    public boolean jobDone = false;

        public void run(){
	        mean.getPopulationDistribution();
	        jobDone = true;
        }
    }
}

