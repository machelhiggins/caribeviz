/*
 * Created on Jun 16, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.analysis.efacility.gis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.uwiseismic.ergo.analysis.efacility.CasualtiesMean;
import com.uwiseismic.ergo.analysis.efacility.Facility;
import com.uwiseismic.ergo.analysis.efacility.HealthFacility;
import com.uwiseismic.ergo.analysis.efacility.HealthFacilityRanker;
import com.uwiseismic.ergo.analysis.efacility.Mean;
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

/**
 *
 * It should be noted that this class INCLUDES severity 4 (Instantaneously killed or mortally injured, HASUZ MR4 13-3)<br>
 *
 * Number of Kmeans has been hardcoded to be no of casualties/100 if no of casualties is greater than 10000. If less then number
 * of kmeans is no of casualties/10.
 *
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public class HealthFacilityAnalysis {

	protected ArrayList <Facility> facilities = new ArrayList<Facility>();
	protected ArrayList <Mean> casualtiesMeanList = new ArrayList<Mean>();
	protected FeatureCollection roads;
	protected HealthFacilityRanker ranker = new HealthFacilityRanker();
	private double smallFacilityWeight = -1;
	private  CoordinateReferenceSystem crs;


	public void healthcareCasualitiesAnalysis(IProgressMonitor monitor, int totalWorkUnits, FeatureCollection roads,
			FeatureCollection casualties){
		this.roads = roads;

        //** preprocess roads by creating graph
		RoadNetworkToGraph procRoad = new RoadNetworkToGraph(roads);
        RoadGraph graph = procRoad.processRoadNetwork();
        ranker.setGraph(graph);
		
		try {
	        if(monitor != null)
	        	monitor.worked((int)Math.floor((double)totalWorkUnits/4.0));
	        
	        //**  Number of Kmeans has been hardcoded to be no of casualties/100 if no of casualties is greater than 10000.
            //** If less then number of kmeans is no of casualties/10.
		    int numMeans = casualties.size() > 10000 ? casualties.size()/100 : casualties.size() /10;
		    //numMeans = 1;
            findFacilitiesAndCasualtyKMeans(monitor, (int)Math.floor((double)totalWorkUnits/4.0), (int)Math.ceil(numMeans), casualties);

            if(monitor != null)
	        	monitor.worked((int)Math.floor((double)totalWorkUnits/4.0));
            
            for(Facility t : facilities)          
                if(smallFacilityWeight != -1)
                    ((HealthFacility)t).setSmallFacilityFactor(smallFacilityWeight);
            
            
            if(monitor != null)
	        	monitor.worked((int)Math.floor((double)totalWorkUnits/4.0));
           
            //** perform kmean pop distribution based on ranking and whatever logic
            for(Mean tm :casualtiesMeanList){              
                System.err.println("DEBUG] "+this.getClass().getName()
                		+" Distributing pop for Mean "+tm.getX()+", "+tm.getY()+" = "+tm.getMeanValue());   
                tm.getPopulationDistribution();
//                while(!checkInPopDistRunner(tm))
//                    try{Thread.sleep(500);}catch(Exception ex){ex.printStackTrace();}
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

	public DefaultFeatureCollection getHealthcareFacilties(){

		//** Build Feature collection
		//SimpleFeatureCollection fc =  FeatureCollections.newCollection();
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

	protected void findFacilitiesAndCasualtyKMeans(IProgressMonitor monitor, int towork,
    		int maxKMeans, FeatureCollection casualtiesFC)throws NoSuchElementException, ClusterableException{

    	//TODO IMPLEMENT  IProgressMonitor

		/* Attributes to watch out for in casualties
		 * ------
		 * efacilty (boolean) - FOR EMERGENCY FACILITIES AND SUCH, NA HERE !!!!
		 * occ_type COM6 - hospital and the sort
		 * severity1
		 * severity2
		 * severity3
		 * severity4
		 */
    	try {

	         ArrayList <String> keys = new ArrayList<String>();
	         keys.add("severity1");
	         keys.add("severity2");
	         keys.add("severity3");
	         keys.add("severity4");

	         //** create featureclusterable objects and get stdev to determine kmeans threashold
	         ArrayList <Clusterable2D>featureWeights = new ArrayList<Clusterable2D>();
	         double mean = 0;
             double lat = 0;
             double longi = 0;
             double occRate = 0;
             int maxCap = 0;
             FeatureIterator i = (FeatureIterator) casualtiesFC.features();
	         while(i.hasNext()){
	        	 SimpleFeature t = (SimpleFeature)i.next();

	        	 //** lets copy feature coordinate system
	        	 if(crs == null){
	        		 crs = t.getDefaultGeometryProperty().getType().getCoordinateReferenceSystem();
	        	 }

	             FeatureClusterable featCl;
	             featCl = new FeatureClusterable(t, keys);
	             if(featCl.getValue()<0.5)
	            	 featCl.setValue(0);
	             else if(featCl.getValue() < 1.0)
	            	 featCl.setValue(1);
	             else
	            	 featCl.setValue(Math.ceil(featCl.getValue()));
	             if(featCl.getValue() > 0){ 
	            	 mean += featCl.getValue();
		             featureWeights.add(featCl);
	             }

	             // ** find healthcare facilities
	             Object obj = t.getAttribute("occ_type");
	             if(obj instanceof String
	            		 && ((String)obj).matches("COM6")){
	            	 obj = t.getAttribute("lat");
	            	 if(obj == null){
	            		 Geometry geom = (Geometry)t.getAttribute(0);
	            		 Point c = geom.getCentroid();
	            		 lat = c.getY();	            			            	 	            	
		                 longi = c.getX();
	            	 }
	            	 else{
	            		 lat = ObjectToReal.getMeDouble(obj).doubleValue();	            	
		            	 obj = t.getAttribute("lon");	            	 
		                 longi = ObjectToReal.getMeDouble(obj).doubleValue();	 
	            	 }	            	 
	            	 obj = t.getAttribute("ef_occ_rat");	            	 
	                 occRate = ObjectToReal.getMeDouble(obj).doubleValue();	            	 
	            	 obj = t.getAttribute("max_occ");	            	 
	            	 maxCap = ObjectToReal.getMeDouble(obj).intValue();
	            	 
	            	 HealthFacility hf = new HealthFacility(lat, longi, maxCap, (int)occRate);
	            	 obj = t.getAttribute("efacil_nam");
                     if(obj instanceof String)
                         hf.setID((String)obj);
	            	 facilities.add(hf);

	             }
	         }

	         System.out.println("DEBUG: Number of facilities "+facilities.size());
	         
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

	         //** hardcoding iterations to  casualtiesFC.size()/100 because it was more than enough for the largest
	         //** building stock and no such thing as too much and kmeans always converge much faster than this limit
	         //KMeansClustering kmeans = new KMeansClusteringBounding(featureWeights, maxKMeans,
	         //		 stdev*m, casualtiesFC.size()/100, new Random(System.currentTimeMillis()));

	         /********************* DEBUG ************ REMOVE BELOW AND UNCOMMENT ABOVE *****************
	          * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	          */
	         //maxKMeans = 500;
	         System.out.println("[DEBUG] "+this.getClass().getName()
                		+" Mean = "+mean+" stdev = "+stdev+" using log vals = "+
	                     usingLogValue +"\t threshold = "+(2*stdev/maxKMeans));

	         KMeansClustering kmeans = new KMeansClusteringBounding(featureWeights, maxKMeans,
                     stdev/maxKMeans, 1000, new Random(System.currentTimeMillis()));
             /********************* DEBUG ************ REMOVE ABOVE AND UNCOMMENT PREVIOUS *****************
              * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
              */

	         long start = System.currentTimeMillis();
	         System.out.println("[DEBUG] "+this.getClass().getName()
                		+" Starting kmeans for "+featureWeights.size()+" buildings casualties");
	         kmeans.startAnalysis();
	         System.out.println("[DEBUG] "+this.getClass().getName()
                		+" For the ROI that took "+(System.currentTimeMillis() - start)/1000+" seconds");
	         Centroid2D cents[] = kmeans.getMeansCentroids();

	         for(int ii = 0;  ii < cents.length; ii++){
	             CasualtiesMean cm = new CasualtiesMean();
                 cm.setMeanValue(cents[ii].getTotalValue());
                 //System.out.println("[DEBUG] "+cents[ii]+"\t Total val of cluster: "+cents[ii].getTotalValue());
	             cm.setID(""+ii);
	             cm.setX(cents[ii].getX());
	             cm.setY(cents[ii].getY());
	             //** ALWAYS CLONE ranker
	        	 cm.setFacilityRanker(ranker.clone());
	        	 cm.setGraph(ranker.getGraph());
	        	 cm.setFacilities(facilities);
	             casualtiesMeanList.add(cm);
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



	private int numOfCPUs = -1;
	private PopDistRunner threads[];

	private boolean checkInPopDistRunner(Mean m){
	    if(numOfCPUs == -1){
	        numOfCPUs = Runtime.getRuntime().availableProcessors() - 1; //** leave one available
	        threads = new PopDistRunner[numOfCPUs];
	    }
	    for(int i = 0; i < numOfCPUs; i++){
	        if(threads[i] == null){
	            threads[i] = new PopDistRunner();
	            threads[i].mean = m;
	            threads[i].start();
	            return true;
	        }
	        else if(threads[i].jobDone){
	            threads[i] = new PopDistRunner();
	            threads[i].mean = m;
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
