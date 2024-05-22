package com.uwiseismic.ergo.roadnetwork;

import java.util.ArrayList;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedPseudograph;

import com.uwiseismic.ergo.graph.ErgoGraphEdge;
import com.uwiseismic.ergo.graph.ErgoGraphNode;
import com.uwiseismic.gis.util.SimplePoint;

public class DjisktraShortestPath {
	
	private RoadGraph graph;
	public DjisktraShortestPath(RoadGraph graph){
		this.graph = graph;
	}
	
	
	
	public GraphPath<RoadGraphNode, RoadWeightedDjisktraEdge> getShortestPath(double startLat, double startLon,
			double endLat, double endLon){
		
		int nodes[] = getStartEndNodes(startLat, startLon, endLat, endLon);
		RoadGraphNode source = graph.getNodeIDMap().get(nodes[0]);
	    RoadGraphNode destination = graph.getNodeIDMap().get(nodes[1]);

	    WeightedPseudograph <RoadGraphNode, RoadWeightedDjisktraEdge> weightedGraph = 
        		new <RoadGraphNode, RoadWeightedDjisktraEdge>WeightedPseudograph(RoadWeightedDjisktraEdge.class);
	    
	    //** add all nodes
	    for(int i = 1; i <= graph.getNodes().size();i++)
	    	weightedGraph.addVertex(graph.getNodeIDMap().get(i));
	    
	    for(ErgoGraphEdge t : graph.getEdges()){
	    	RoadGraphEdge edge = (RoadGraphEdge)t;
	    	RoadWeightedDjisktraEdge dEdge = new RoadWeightedDjisktraEdge(edge);
	    	weightedGraph.addEdge(graph.getNodeIDMap().get(edge.getStartNodeID()),
	    			graph.getNodeIDMap().get(edge.getEndNodeID()), 
	    					dEdge);
	    	weightedGraph.setEdgeWeight(dEdge, 
	    			RoadPathWeigher.weighDistance(edge.getWayDistance(), edge.getWeight(), RoadGraphEdge.MAX_WEIGHT));
		}
	    
	    GraphPath<RoadGraphNode, RoadWeightedDjisktraEdge> pathway = DijkstraShortestPath.findPathBetween(weightedGraph,
	    		source, destination);
	    return pathway;
	  
	}
	
	private int[] getStartEndNodes(double startLat,double startLon, 
			double endLat, double endLon){
		int startEnd[] = new int[] {getNodeFromGeo(startLon, startLat), getNodeFromGeo(endLon, endLat)};
		return startEnd;
	}
	
	private int getNodeFromGeo(double lon, double lat){
		ArrayList<ErgoGraphNode> nodes = graph.getNodes();
		SimplePoint geoPoint = new SimplePoint(0,lat,lon);
		SimplePoint closest = null;
		double closestDist = Double.MAX_VALUE;
		double dist;
		for(ErgoGraphNode t : nodes){
			RoadGraphNode node = (RoadGraphNode)t;
			dist = node.getDistanceTo(geoPoint);
			if(dist  < closestDist){
				closest = node.getSimpleNode();
				closestDist = dist;
			}
		}
			
		if(closest != null)
			return closest.getID();
		return -1;
	}
	

}
