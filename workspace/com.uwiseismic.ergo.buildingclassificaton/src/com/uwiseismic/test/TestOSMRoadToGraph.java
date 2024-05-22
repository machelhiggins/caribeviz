package com.uwiseismic.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;

import com.uwiseismic.ergo.roadnetwork.RoadGraph;
import com.uwiseismic.ergo.roadnetwork.RoadGraphNode;
import com.uwiseismic.ergo.roadnetwork.RoadNetworkToGraph;
import com.uwiseismic.ergo.roadnetwork.collection.RNIntObjectHashMap;



public class TestOSMRoadToGraph {
	
//    private static final int COORD_STATE_UNKNOWN = 0;
//    private static final int COORD_STATE_PILLAR = -2;
//    private static final int FIRST_NODE_ID = 1;
//    private static final String[] DIRECT_COPY_TAGS = new String[]{"name"};
//    private File roadsFile;
//	
	public static void main(String[] args) {
		 File shapefileFilename =
	        		new File("C:\\temp\\osm_edge_node_creator_scratch\\SUBSET_OSM_Dominica_Data_Set_planet_osm_line_lines.shp");

	         if (shapefileFilename == null || !shapefileFilename.exists()) {
	             System.out.println("Wrong file");
	            return;
	        }
	         try{
	        	 System.out.println("Loading and preprocessing: ");
	             DataStore store = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
	                           shapefileFilename));	             
	             String []typeNames = store.getTypeNames();
	             FeatureCollection roads = store.getFeatureSource(typeNames[0]).getFeatures();
	             
	             RoadNetworkToGraph procRoad = new RoadNetworkToGraph(roads);
	             
	             RoadGraph graph = procRoad.processRoadNetwork();
	             System.out.println("Created graph: "+graph);
	             RNIntObjectHashMap<RoadGraphNode> nodeIDMap = graph.getNodeIDMap();
	             
	             int keys[] = nodeIDMap.keys;
	             System.out.println(keys.length);
	             int numOfNull = 0;
	             BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\temp\\osm_edge_node_creator_scratch\\temp.txt"));
	             
	             for(int i =0; i < keys.length; i++){
	            	 if(nodeIDMap.get(i) == null)
	            		 numOfNull++;
	            	 else
	            		 writer.write(nodeIDMap.get(i).getSimpleNode().toString()+","+i+"\n");
	             }
	             writer.close();
	             System.out.println("Number of nulls "+numOfNull+"/"+keys.length);
	             
	         }catch(Exception ex){
	        	 ex.printStackTrace();
	         }
		
		
	}
}
