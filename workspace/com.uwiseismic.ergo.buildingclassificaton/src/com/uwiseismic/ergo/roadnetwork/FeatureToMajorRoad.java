/*
 * Created on Oct 12, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.roadnetwork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.DegreeToMeter;
import com.uwiseismic.gis.util.geohash.FeatureHashable;
import com.uwiseismic.gis.util.geohash.GeoHash;
import com.uwiseismic.gis.util.geohash.GeoHashable;
import com.vividsolutions.jts.geom.Geometry;

public class FeatureToMajorRoad {

    private GeoHash majorRoads;
    private FeatureCollection roads;
    ArrayList<GeoHashable> roadsWeWant = new ArrayList<GeoHashable>();
    private double currentResolution = 0.02; // smallest grid size for GeoHash
    private double MULTIPLE_INCR_CURRENT_RESOLUTION = 1.4; // amount to multiply by to increase currentResolution
    private int MAX_CHANGES_TO_CURR_RESOLUTION = 3; //** amount of times we can change GeoHash currentResolution
    

    private GeoHash createGeoHash(){
    	ReferencedEnvelope roadEnv = roads.getBounds();
    	double aRez = GeoHash.getASensibleSmallestCellSize(roads.getBounds());
    	System.out.println("[DEBUG] THE SMALLEST ROADNERWORK GEOHASH CELL SIZE (meters) = "+DegreeToMeter.degreeToMeter(roadEnv.getMaxY(), aRez)+" -- Current smallest is "+DegreeToMeter.degreeToMeter(roadEnv.getMaxY(), currentResolution));
    	if(currentResolution > aRez)
    		currentResolution = aRez;
        GeoHash majorRoads = new GeoHash(currentResolution, roads.getBounds());
        //** create geohashables.
        String roadType = null;

        for(FeatureIterator i = roads.features();i.hasNext();){
            SimpleFeature feature = (SimpleFeature)i.next();
             roadType = (String) (feature.getAttribute("highway"));
            if(roadType != null && (roadType.matches("highway")
                    ||  roadType.matches("primary")
                    ||  roadType.matches("secondary")
                    )){
                roadsWeWant.add(new FeatureHashable(feature));
            }
        }
        GeoHashable rwwArr[] = new GeoHashable[roadsWeWant.size()];
        roadsWeWant.toArray(rwwArr);
        majorRoads.setHashables(Arrays.asList(rwwArr));
        return majorRoads;
    }




    public FeatureToMajorRoad(FeatureCollection roads){
        this.roads = roads;
        majorRoads = createGeoHash();
    }

    /**
     * Will return distance between feature and closet road with OpenStreetMap attribute "highway" = <highway/primary/secondary>. 
     * This method is ignorant of CRS's. Up to user to ensure that <code> roads</code> supplied to the constructor of this class 
     * has the same CRS as <code>feat</code>
     * 
     * @param feat
     * @return
     */
    public double measureFeatureToRoad(Feature feat){
        double minDist = Double.MAX_VALUE;
        double d = Double.MAX_VALUE;
        //** find road in enclosing bounding box using GeoHash
        Geometry geom = (Geometry)feat.getDefaultGeometryProperty().getValue();
        ArrayList <GeoHashable> contents =  majorRoads.getContentsOfBottomCell(geom);
        int changingCurrentResolution = 0;
        if(contents != null){
        	
        	//** check that geohash isn't empty due to cell sizes being too small
        	while(contents.size() == 0){
        		currentResolution = currentResolution*MULTIPLE_INCR_CURRENT_RESOLUTION;
        		GeoHash majorRoads = new GeoHash(currentResolution, roads.getBounds());
        		contents =  majorRoads.getContentsOfBottomCell(geom);
        		GeoHashable rwwArr[] = new GeoHashable[roadsWeWant.size()];
        		roadsWeWant.toArray(rwwArr);
                majorRoads.setHashables(Arrays.asList(rwwArr));
                
        		changingCurrentResolution++;
        		//** If we keep expanding the smallest cell size of the GeoHash more than MAX_CHANGES_TO_CURR_RESOLUTION
        		//** then building is too far away from real primary\highway so assign it 50km, which is arbitrary.
        		if( changingCurrentResolution == MAX_CHANGES_TO_CURR_RESOLUTION){
        			minDist = 0.5;
        			break;
        		}
        	}        
	        for(Iterator <GeoHashable>i = contents.iterator(); i.hasNext();){
	            d = i.next().getGeometry().distance(geom);
	            if(d< minDist)
	                minDist = d;
	        }
        }
        else{
        	System.err.println("DEBUG "+this.getClass().getName()+": Could not get road features near bldg_id: "+
        		((SimpleFeature)feat).getAttribute("bldg_id"));
        }

    
        return minDist;
    }

    /**
     * @param args
     */
    public static void main(String args[]){
    	File roadShape = new File("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\data\\OSM\\highway\\OSM_Dominica_Data_Set_planet_osm_line_lines.shp");
    	
/*      File bldgs = new File("C:\\temp\\nkgn\\maeved-New Kingston.shp");  
        
        try{
            long debugTime = System.currentTimeMillis();
            System.err.println("Loading and preprocessing: ");
            DataStore store = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
                    roadShape));
            String []typeNames = store.getTypeNames();
            FeatureCollection roadFC  =  store.getFeatureSource(typeNames[0]).getFeatures();//FeatureCollections.newCollection();

            store = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
                    bldgs));
            typeNames = store.getTypeNames();
            FeatureCollection bldgFC  =  store.getFeatureSource(typeNames[0]).getFeatures();//FeatureCollections.newCollection();

            FeatureToMajorRaoad roadMeasure = new FeatureToMajorRaoad(roadFC);

            for(FeatureIterator i = bldgFC.features(); i.hasNext();){
                Feature feat = i.next();
                System.out.println(((Integer)feat.getAttribute("RECNO"))+
                        "\t"+roadMeasure.measureFeatureToRoad((feat)));
            }


            long old = debugTime;
            debugTime = System.currentTimeMillis();
            System.err.println("It took "+((debugTime-old)/1000)+" to load and preprocess");
        }catch(Exception e){
            e.printStackTrace();
        }*/
    	File bldgs = new File("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\survey_analysis\\large_buildings_FROM_INTERMEDIATE_BUILDING_STOCK.shp");
    	//File bldgs = new File("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\survey_analysis\\small_buildings_FROM_INTERMEDIATE_BUILDING_STOCK.shp");    
    	//File bldgs = new File("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\survey_analysis\\scratch\\one_small_bldg_in_roseau2.shp");

        try{
            long debugTime = System.currentTimeMillis();
            System.err.println("Loading and preprocessing: ");
            DataStore store = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
                    roadShape));
            String []typeNames = store.getTypeNames();
            FeatureCollection roadFC  =  store.getFeatureSource(typeNames[0]).getFeatures();//FeatureCollections.newCollection();

            DataStore storeBldgs = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
                    bldgs));
            typeNames = storeBldgs.getTypeNames();
            FeatureCollection bldgFC  =  storeBldgs.getFeatureSource(typeNames[0]).getFeatures();//FeatureCollections.newCollection();

            FeatureToMajorRoad roadMeasure = new FeatureToMajorRoad(roadFC);
            
            BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\survey_analysis\\intermediate_buidling_stock_RP_VALUES_LARGE_BUILDINGS.txt"));
            //BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\survey_analysis\\intermediate_buidling_stock_RP_VALUES_SMALL_BUILDINGS.txt"));
            //BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\survey_analysis\\scratch\\wtf_meng.txt"));
            
            double minDist;
            double majorRoadIndexOrig;
            for(FeatureIterator i = bldgFC.features(); i.hasNext();){
            	Feature feat = i.next();            	
            	minDist = roadMeasure.measureFeatureToRoad((feat));
                double majorRoadIndex =  DegreeToMeter.degreeToMeter(feat.getBounds().getMaxY(), minDist);
                majorRoadIndexOrig = majorRoadIndex;
                //** normalized to 500 m
                if(majorRoadIndex > 5000)
            		majorRoadIndex = 1;
            	else
            		majorRoadIndex = (majorRoadIndex/5000);
                //** prints recno (should be buldg_id in the future), min distance to road , major road index thats been bastarderized w the above if statement
            	out.write(((String)feat.getProperty("bldg_id").getValue())+
                        "\t"+majorRoadIndexOrig+"\t"+majorRoadIndex+"\n");
                System.out.println(((String)feat.getProperty("bldg_id").getValue())+
                		"\t"+majorRoadIndexOrig+"\t"+majorRoadIndex);
            }
            out.flush();
            out.close();
            store.dispose();
            storeBldgs.dispose();
            long old = debugTime;      
            debugTime = System.currentTimeMillis();
            System.err.println("It took "+((debugTime-old)/1000)+" seconds to load and preprocess");
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
