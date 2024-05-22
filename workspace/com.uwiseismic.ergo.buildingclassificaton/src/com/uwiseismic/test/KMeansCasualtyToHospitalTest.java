/*
 * Created on Jan 20, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.uwiseismic.ergo.analysis.efacility.CasualtiesMean;
import com.uwiseismic.ergo.analysis.efacility.Facility;
import com.uwiseismic.ergo.analysis.efacility.HealthFacility;
import com.uwiseismic.ergo.analysis.efacility.Mean;
import com.uwiseismic.ergo.analysis.efacility.gis.HealthFacilityAnalysis;
import com.uwiseismic.ergo.roadnetwork.RoadGraph;
import com.uwiseismic.ergo.roadnetwork.RoadNetworkToGraph;
import com.uwiseismic.gis.util.ObjectToReal;
import com.uwiseismic.kmeans.Centroid2D;
import com.uwiseismic.kmeans.Clusterable2D;
import com.uwiseismic.kmeans.FeatureClusterable;
import com.uwiseismic.kmeans.KMeansClustering;
import com.uwiseismic.kmeans.KMeansClusteringBounding;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class KMeansCasualtyToHospitalTest extends HealthFacilityAnalysis{

    public static void main(String args[]){
        KMeansCasualtyToHospitalTest test = new KMeansCasualtyToHospitalTest();
        test.testCasualtyMyKMeans(100);
        //test.testCasualtyACMKMeans(30);
    }

    public void testCasualtyMyKMeans(int maxKMeans){
    	File shapefileFilename = new File("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\Documents\\Final Report\\Figures\\95\\data\\Dominica 95 Casualties.shp");
		if (shapefileFilename == null) {
			System.out.println("Wrong file");
			return;
		}
		try {
			DataStore store = FileDataStoreFinder.getDataStore(new java.net.URL("file://" + shapefileFilename));

			String[] typeNames = store.getTypeNames();
			FeatureCollection casualties = store.getFeatureSource(typeNames[0]).getFeatures();// FeatureCollections.newCollection();
	        
	        shapefileFilename =
	        		new File("C:\\temp\\osm_edge_node_creator_scratch\\DUPLICATE_OSM_Dominica_Data_Set_planet_osm_line_lines.shp");
	
	         if (shapefileFilename == null || !shapefileFilename.exists()) {
	             System.out.println("Wrong file");
	            return;
	        }
	        DataStore roadStore = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
	                       shapefileFilename));	             
	        String []typeNamesRoads = roadStore.getTypeNames();

	        this.roads = roadStore.getFeatureSource(typeNamesRoads[0]).getFeatures();
		       //** preprocess roads by creating graph
	        System.out.println("Processing roads");
			RoadNetworkToGraph procRoad = new RoadNetworkToGraph(roads);
	        RoadGraph graph = procRoad.processRoadNetwork();
	        System.out.println("Done processing roads");
	        ranker.setGraph(graph);
	        
	        store.dispose();
	        roadStore.dispose();

		        
	        //**  Number of Kmeans has been hardcoded to be no of casualties/100 if no of casualties is greater than 10000.
            //** If less then number of kmeans is no of casualties/10.
		    int numMeans = casualties.size() > 10000 ? casualties.size()/100 : casualties.size() /10;
		    System.out.println("Number of casaulity means:" + numMeans);
		    
            findFacilitiesAndCasualtyKMeans(null, 1, (int)Math.ceil(numMeans), casualties);

            
            //??????????????????????????? WHAT THE FUCK IS HAPPENING BELOW?????????????
            for(Facility t : facilities)          
                if(this.getSmallFacilityWeight() != -1)
                    ((HealthFacility)t).setSmallFacilityFactor(getSmallFacilityWeight());

           
            System.out.println("Number of means "+casualtiesMeanList.size());
            //** perform kmean pop distribution based on ranking and whatever logic
            double totalCasualties = 0;
            for(Mean mean :casualtiesMeanList){             
            	mean.getPopulationDistribution();
            	totalCasualties += mean.getMeanValue();
            }
            
            for(Facility t : facilities)
            	System.out.println(t.getID()+"\tcap="+t.getOccupancy()+" overcap="+t.getOverCapacityAmount()+" with total casualties "+totalCasualties);
            		
            
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }

}
