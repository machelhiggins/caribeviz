package com.uwiseismic.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.geohash.FeatureHashable;
import com.uwiseismic.gis.util.geohash.GeoHash;
import com.uwiseismic.gis.util.geohash.GeoHashable;

public class TestGeoHash {

	public static void main(String args[]){
		File shapefileFilename =
        		new File("C:\\Users\\Machel\\OneDrive\\DRRC\\CaribEViz\\dev_share\\jamaica_data_mobile\\KMA_Portmore_road_network.shp");
		DataStore store;
		try {
			store = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
			        shapefileFilename));
			  String []typeNames = store.getTypeNames();
			  FeatureCollection fc  =  store.getFeatureSource(typeNames[0]).getFeatures();//FeatureCollections.newCollection();

	            ArrayList <GeoHashable> roads = new ArrayList <GeoHashable>();
	            GeoHash ghRoads = new GeoHash(GeoHash.getASensibleSmallestCellSize(fc.getBounds()), fc.getBounds());
	            for(FeatureIterator feat = fc.features(); feat.hasNext();){
	            	GeoHashable h = new FeatureHashable((SimpleFeature) feat.next());
	            	roads.add(h);
	            }
	            ghRoads.setHashables(roads);
			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}
	
	
}

