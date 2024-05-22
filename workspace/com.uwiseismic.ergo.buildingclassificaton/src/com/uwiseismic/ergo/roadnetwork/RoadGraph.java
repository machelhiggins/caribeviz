package com.uwiseismic.ergo.roadnetwork;

import java.util.ArrayList;
import java.util.HashMap;

import com.uwiseismic.ergo.graph.ErgoGraph;
import com.uwiseismic.ergo.graph.ErgoGraphEdge;
import com.uwiseismic.ergo.graph.ErgoGraphNode;
import com.uwiseismic.ergo.roadnetwork.collection.RNIntObjectHashMap;

public class RoadGraph implements ErgoGraph{
	private ArrayList<ErgoGraphNode> nodes = new ArrayList<ErgoGraphNode>();
	private ArrayList<ErgoGraphEdge> edges = new ArrayList<ErgoGraphEdge>();
	private RNIntObjectHashMap<RoadGraphNode> nodeIDMap = new RNIntObjectHashMap<RoadGraphNode>();
	private RNIntObjectHashMap<ArrayList <RoadGraphEdge>> nodeIDEdgesMap = 
			new RNIntObjectHashMap<ArrayList <RoadGraphEdge>>();
	private HashMap<Integer, ArrayList <RoadGraphEdge>> slowNodeIDtoEdgeMap = 
			new HashMap<Integer,ArrayList <RoadGraphEdge>>(); //** temporary hashmap for storage until rebuilding faster RNIntObjectHashMap
	
	private boolean rebuildingNodesIDEdgesMap = false;
	private int dbg = 0;
	
	public RoadGraph(){
	}
	
	public RoadGraph(ArrayList<RoadGraphNode> nodes, ArrayList<RoadGraphEdge> edges){
		//this.nodes = nodes;
		//this.edges = edges;
		/** Always add nodes first **/
		for(RoadGraphNode newNode: nodes)
			addNode(newNode);
		for(RoadGraphEdge newEdge: edges)
			addEdge(newEdge);
		
		rebuildingNodesIDEdgesMap = true;
	}
	
	public String toString(){
		return "DEBUG: "+nodes.size()+" nodes and "+edges.size()+" edges with keys node hashmap being "+nodeIDMap.keys.length+" long";
	}

	public ArrayList<ErgoGraphNode> getNodes() {
		return nodes;
	}

	public ArrayList<ErgoGraphEdge> getEdges() {
		return edges;
	}
	
	public void addEdge(RoadGraphEdge edge){
		edges.add(edge);
		int nodeID = edge.getStartNodeID();
		ArrayList <RoadGraphEdge> edgeList = slowNodeIDtoEdgeMap.get(new Integer(nodeID));
		if(edgeList == null){
			edgeList = new ArrayList<RoadGraphEdge>();
			slowNodeIDtoEdgeMap.put(new Integer(nodeID), edgeList);
		}
		edgeList.add(edge);
		
		nodeID = edge.getEndNodeID();
		ArrayList <RoadGraphEdge> endEdgeList = slowNodeIDtoEdgeMap.get(new Integer(nodeID));
		if(endEdgeList == null){
			endEdgeList = new ArrayList<RoadGraphEdge>();
			slowNodeIDtoEdgeMap.put(new Integer(nodeID), endEdgeList);
		}
		endEdgeList.add(edge);
		
		rebuildingNodesIDEdgesMap = true;
	}
	
	private void rebuildEdgeIntObjMap(){
		nodeIDEdgesMap.clear();
		for(int i = 1; i <= nodes.size(); i++){
			ArrayList <RoadGraphEdge> slowMap = slowNodeIDtoEdgeMap.get(new Integer(i));
			if(slowMap != null){
				nodeIDEdgesMap.put(i,slowMap);
			}
		}
		rebuildingNodesIDEdgesMap = false;
	}
	
	public void addNode(ErgoGraphNode node){
		RoadGraphNode nodeRG = (RoadGraphNode) node;
		nodes.add(nodeRG);
		nodeIDMap.putIfAbsent(nodeRG.getSimpleNode().getID(), nodeRG);
		dbg++;
	}	
	
	public RNIntObjectHashMap<RoadGraphNode> getNodeIDMap(){	
		return nodeIDMap;
	}
	
	public RoadGraphEdge[] getNodePaths(int node){
		if(rebuildingNodesIDEdgesMap)
			rebuildEdgeIntObjMap();
		ArrayList <RoadGraphEdge> edgeList = nodeIDEdgesMap.get(node);
		if(edgeList == null)
			return null; 
		RoadGraphEdge sharedEdges[] = new RoadGraphEdge[edgeList.size()];
		return edgeList.toArray(sharedEdges);		
	}

	@Override
	public void addEdge(ErgoGraphEdge edge) {
		// TODO Auto-generated method stub
		
	}
	
}
