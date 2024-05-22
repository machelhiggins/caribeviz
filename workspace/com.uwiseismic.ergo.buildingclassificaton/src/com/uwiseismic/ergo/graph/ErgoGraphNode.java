package com.uwiseismic.ergo.graph;

import java.util.ArrayList;



public interface ErgoGraphNode {

	/**
	 * Get edges associated with this node
	 * 
	 * @return
	 */
	public ArrayList<ErgoGraphEdge> getAssociatedEdges();

	/**
	 * Set edges associated with this edge
	 * 
	 * @param associatedEdges
	 */
	public void setAssociatedEdges(ArrayList<ErgoGraphEdge> associatedEdges);
	
	/**
	 * 
	 * Get the weight distance between this Node and p
	 * 
	 * @param p
	 * @return
	 */
	public double getDistanceTo(ErgoGraphNode p);
	
}
