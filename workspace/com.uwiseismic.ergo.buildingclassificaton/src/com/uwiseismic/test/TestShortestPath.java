package com.uwiseismic.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.jgrapht.GraphPath;

import com.uwiseismic.ergo.roadnetwork.DjisktraShortestPath;
import com.uwiseismic.ergo.roadnetwork.RoadGraph;
import com.uwiseismic.ergo.roadnetwork.RoadGraphNode;
import com.uwiseismic.ergo.roadnetwork.RoadNetworkToGraph;
import com.uwiseismic.ergo.roadnetwork.RoadWeightedDjisktraEdge;

public class TestShortestPath {

	public static void main(String[] args) {
		 File shapefileFilename =
	        		new File("C:\\temp\\osm_edge_node_creator_scratch\\SUBSET_OSM_Dominica_Data_Set_planet_osm_line_lines.shp");

	         if (shapefileFilename == null || !shapefileFilename.exists()) {
	             System.out.println("Wrong file");
	            return;
	        }
	         try{
	        	 
/*	        	 SimplePoint p1 = new SimplePoint(1,15.2980774,-61.3753208); 
	        	 SimplePoint p2 = new SimplePoint(1,15.5695952,-61.3106509);
	        	 System.out.println("COMPARISON: "+ p1.compareTo(p2));
	        	 System.out.println("EQUALS: "+ p1.equals(p2));		
	        	 if(!p1.equals(p2)) System.exit(1);*/
	        	 
	        	 System.out.println("Loading and preprocessing: ");
	             DataStore store = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
	                           shapefileFilename));
	             String []typeNames = store.getTypeNames();
	             FeatureCollection roads = store.getFeatureSource(typeNames[0]).getFeatures();
	             
	             RoadNetworkToGraph procRoad = new RoadNetworkToGraph(roads);
	             RoadGraph graph = procRoad.processRoadNetwork();
	             System.out.println("Created graph: "+graph);
	             
	             
	             DjisktraShortestPath dj = new DjisktraShortestPath(graph);
	             GraphPath<RoadGraphNode, RoadWeightedDjisktraEdge> getShortestPath = dj.getShortestPath(15.3265253,-61.3874764,15.2846026,-61.3741622);
	   
	             BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\temp\\osm_edge_node_creator_scratch\\path_nodes.txt"));
	             //System.out.println(getShortestPath.getLength());
	             //System.out.println(getShortestPath.getWeight());
	             
	             for(RoadGraphNode vertex: getShortestPath.getVertexList()){
	            	 writer.write(vertex+"\n");
	             	System.out.println(vertex);
	             }
	             writer.close();
	         }catch(Exception ex){
	        	 ex.printStackTrace();
	         }
		

	}

}
