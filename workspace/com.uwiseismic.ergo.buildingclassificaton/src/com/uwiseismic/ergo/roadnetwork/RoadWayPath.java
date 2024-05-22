package com.uwiseismic.ergo.roadnetwork;

import java.util.ArrayList;

public class RoadWayPath {
	
	private ArrayList <RoadGraphNode> nodes = new ArrayList<RoadGraphNode>();
	private ArrayList <RoadGraphEdge> edges = new ArrayList<RoadGraphEdge>();
	private int indexOfLastCalculatedEdges = 0;
	private RoadGraph graph = new RoadGraph();
	private boolean findEdges = true;
	
	public RoadWayPath(RoadGraph graph){
		this.graph = graph;
	}
	
	/**
	 * Add nodes in sequence of path!
	 * @param edge
	 */
	public void addNode(RoadGraphNode node){
		if(nodes.size() > 0){
			//if(!nodes.get(nodes.size()-1).equals(node)){
			if(!nodes.contains(node)){
				nodes.add(node);
				findEdges = true;
			}
		}else{
			nodes.add(node);
			findEdges = true;
		}
	}

	/**
	 * Get nodes in sequence of path
	 * @return
	 */
	public ArrayList<RoadGraphNode> getNodes(){
		return nodes;
	}		
	
	public void getEdges(){
		int nodeID = -1;
		int nextNodeID = -1;
		RoadGraphNode nodesArr [] = new RoadGraphNode[nodes.size()];
		nodesArr = nodes.toArray(nodesArr);
		for(int n = indexOfLastCalculatedEdges; n< nodesArr.length+1; n++){
			nodeID = nodesArr[n].getSimpleNode().getID();
			nextNodeID = nodesArr[n+1].getSimpleNode().getID();
			 RoadGraphEdge[] edgesArr = graph.getNodePaths(nodeID);
			 for(int i = 0; i < edgesArr.length; i++){
				 int potentialNode = edgesArr[i].getOtherNode(nodeID);
				 if(potentialNode >= 0 && potentialNode == nextNodeID ){
					 edges.add(edgesArr[i]);
					 break;
				 }
			 }
		}
		indexOfLastCalculatedEdges = nodesArr.length-1;
	}
	
	public double getWayDistance(){
		if(findEdges){
			getEdges();
		}
		double dist = 0;
		for(RoadGraphEdge edge : edges)
			dist = dist + edge.getWayDistance();
		return dist;
	}
	 

}
