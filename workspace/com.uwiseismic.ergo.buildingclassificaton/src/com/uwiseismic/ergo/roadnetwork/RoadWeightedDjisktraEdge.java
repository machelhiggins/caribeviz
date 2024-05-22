package com.uwiseismic.ergo.roadnetwork;

import org.jgrapht.graph.DefaultWeightedEdge;

public class RoadWeightedDjisktraEdge extends DefaultWeightedEdge implements Comparable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1608154072181974743L;
	
	private RoadGraphEdge edge;
	public RoadWeightedDjisktraEdge(RoadGraphEdge edge){
		this.edge = edge;
	}

	@Override
	public int compareTo(Object o) {
		return edge.compareTo(((RoadGraphEdge)o));
	}
}
