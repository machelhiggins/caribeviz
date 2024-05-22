package com.uwiseismic.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.ergo.analysis.efacility.Facility;
import com.uwiseismic.ergo.analysis.efacility.Mean;
import com.uwiseismic.ergo.analysis.efacility.ShelterFacilityRanker;
import com.uwiseismic.ergo.analysis.efacility.gis.ShelterFacilityAnalysis;
import com.uwiseismic.ergo.roadnetwork.RoadGraph;
import com.uwiseismic.ergo.roadnetwork.RoadNetworkToGraph;
import com.uwiseismic.kmeans.Centroid2D;
import com.uwiseismic.kmeans.Clusterable2D;
import com.uwiseismic.kmeans.FeatureClusterable;
import com.uwiseismic.kmeans.KMeansClustering;
import com.uwiseismic.kmeans.KMeansClusteringBounding;

public class KMeanPopDislocationTest extends ShelterFacilityAnalysis{

	public static void main(String[] args) {
		KMeanPopDislocationTest testing = new KMeanPopDislocationTest();
		testing.testPopDislocationMyKMeans();

	}

	public void testPopDislocationMyKMeans() {
		File shapefileFilename = new File("C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\Documents\\Final Report\\Figures\\Intraslab\\data\\Dominica Intraslab UWI Pop Dislocation.shp");
		if (shapefileFilename == null) {
			System.out.println("Wrong file");
			return;
		}
		try {
			DataStore store = FileDataStoreFinder.getDataStore(new java.net.URL("file://" + shapefileFilename));

			String[] typeNames = store.getTypeNames();
			FeatureCollection popCasualtiesFC = store.getFeatureSource(typeNames[0]).getFeatures();// FeatureCollections.newCollection();

			// ******** FOLLOWING IS FROMT
			// ShelterFacilityAnalysis.shelterOccupancyAnalysis method
			// ** Number of Kmeans has been hardcoded to be no of
			// popDislocated/100 if no of popDislocated is greater than 10000.
			// ** If less then number of kmeans is no of popDislocated/10.
			int numMeans = 0;
			if (popCasualtiesFC.getBounds().getArea() <= 0.03)// degrees squared,
															// 0.03 works for
															// cities like
															// kingston and
															// portmore
				numMeans = popCasualtiesFC.size() > 10000 ? popCasualtiesFC.size() / 100 : popCasualtiesFC.size() / 10;
			else {
				numMeans = 400 * (int) Math.ceil(popCasualtiesFC.getBounds().getArea() / 0.03);
			}
			System.err.println("DEBUG" + this.getClass().getName() + " k = " + numMeans + " area = "
					+ popCasualtiesFC.getBounds().getArea());
			try {
				BufferedWriter writer = new BufferedWriter(
						new FileWriter("C:\\temp\\osm_edge_node_creator_scratch\\pop_casualties.txt"));

				ArrayList <String> keys = new ArrayList<String>();
		        keys.add("popdisloc");
				FeatureIterator i = (FeatureIterator) popCasualtiesFC.features();
				while (i.hasNext()) {
					SimpleFeature t = (SimpleFeature) i.next();
					FeatureClusterable featCl  = new FeatureClusterable(t, keys);
					writer.write(featCl.getX()+","+featCl.getY()+","+featCl.getValue()+"\n");
				}
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			store.dispose();

			shapefileFilename =
	        		new File("C:\\temp\\osm_edge_node_creator_scratch\\DUPLICATE_OSM_Dominica_Data_Set_planet_osm_line_lines.shp");

	         if (shapefileFilename == null || !shapefileFilename.exists()) {
	             System.out.println("Wrong file");
	            return;
	        }
            DataStore roadStore = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
                           shapefileFilename));	             
            String []typeNamesRoads = roadStore.getTypeNames();
			RoadNetworkToGraph procRoad = 
					new RoadNetworkToGraph(roadStore.getFeatureSource(typeNamesRoads[0]).getFeatures());
			RoadGraph graph = procRoad.processRoadNetwork();
	        this.ranker.setGraph(graph);        
			
			this.findFacilitiesAndPopCasualtyKMeans(null, 0, (int) Math.ceil(numMeans), popCasualtiesFC);

			BufferedWriter writer = new BufferedWriter(
					new FileWriter("C:\\temp\\osm_edge_node_creator_scratch\\kmeans.txt"));
			for (Mean tm : this.popDislocationMeans) {
				writer.write(tm.getX() + "," + tm.getY() + "," + tm.getMeanValue()+"\n");
				System.err.println(tm.getX() + "," + tm.getY() + "," + tm.getMeanValue());				
			}
			writer.close();
				        
			Mean chosen = null;
			double maxValue = Double.MIN_VALUE;
	        for(Mean tm : this.popDislocationMeans){
	        	tm.getPopulationDistribution();
	        }
	        
	        
	        for(Facility t : facilities){
	        	System.out.println(t.getID()+","+t.getX()+","+t.getY()+"\t\t\t\t\t"+t.getOverCapacityAmount()+"\t"+t.getOccupancy());
	        }
	        
	        roadStore.dispose();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
