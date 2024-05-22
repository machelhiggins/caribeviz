package com.uwiseismic.ergo.graph;

import java.util.ArrayList;

import com.uwiseismic.ergo.roadnetwork.RoadGraphEdge;
import com.uwiseismic.ergo.roadnetwork.RoadGraphNode;
import com.uwiseismic.ergo.roadnetwork.collection.RNIntObjectHashMap;

/**
 * Interface for a Graph, i.e., non-directional network with nodes and edges 
 * 
 * @author machel
 *
 */
public interface ErgoGraph {
	

	/**
	 * Get vertices (nodes) in this graph
	 * @return
	 */
	public ArrayList<ErgoGraphNode> getNodes();

	/**
	 * Get edges in this graph
	 * @return
	 */
	public ArrayList<ErgoGraphEdge> getEdges();
	
	/**
	 * Add edge. Implementing classes must add nodes with edges
	 * @param edge
	 */
	public void addEdge(ErgoGraphEdge edge);

	/**
	 * Add node.
	 * @param node
	 */
	public void addNode(ErgoGraphNode node);
	

}
