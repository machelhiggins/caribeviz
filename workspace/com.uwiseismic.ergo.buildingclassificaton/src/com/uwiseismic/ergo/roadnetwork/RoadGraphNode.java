package com.uwiseismic.ergo.roadnetwork;

import java.util.ArrayList;

import com.uwiseismic.ergo.graph.ErgoGraphEdge;
import com.uwiseismic.ergo.graph.ErgoGraphNode;
import com.uwiseismic.gis.util.SimplePoint;

public class RoadGraphNode implements ErgoGraphNode, Comparable<RoadGraphNode>{
	
	private SimplePoint node;
	private ArrayList <ErgoGraphEdge> associatedEdges;
	
	public RoadGraphNode(SimplePoint node, ArrayList <ErgoGraphEdge> associatedEdges){
		this.node = node;
		if(associatedEdges != null)
			this.associatedEdges = associatedEdges;
	}

	public ArrayList<ErgoGraphEdge> getAssociatedEdges() {
		return associatedEdges;
	}

	public void setAssociatedEdges(ArrayList<ErgoGraphEdge> associatedEdges) {
		this.associatedEdges = associatedEdges;
	}

	public SimplePoint getSimpleNode() {
		return node;
	}
	
	public double getDistanceTo(ErgoGraphNode p){
		return node.getDistanceTo(((RoadGraphNode)p).getSimpleNode());
	}
	
	public double getDistanceTo(SimplePoint p){
		return node.getDistanceTo(p);
	}
 
	public String toString(){
		return node.toString();
	}

	@Override
	public int compareTo(RoadGraphNode o) {
		// TODO Auto-generated method stub
		return node.compareTo(o.getSimpleNode());
	}
	
}
