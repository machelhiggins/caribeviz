package com.uwiseismic.ergo.analysis.efacility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jgrapht.GraphPath;

import com.uwiseismic.ergo.graph.ErgoGraph;
import com.uwiseismic.ergo.roadnetwork.DjisktraShortestPath;
import com.uwiseismic.ergo.roadnetwork.RoadGraph;
import com.uwiseismic.ergo.roadnetwork.RoadGraphNode;
import com.uwiseismic.ergo.roadnetwork.RoadWeightedDjisktraEdge;
import com.uwiseismic.gis.util.IllegalClassStateException;

public class HealthFacilityRanker implements FacilityRanker {

	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
	private ArrayList<Facility> facilities;
	private ArrayList<Facility> orderedFacilities = new ArrayList<Facility>();
	private double x = 0;
	private double y = 0;
	private double rankings[];
	private boolean endPointIsFacility = false;
	private RoadGraph graph;
	
	
    /* (non-Javadoc)
	 * @see com.uwiseismic.analyis.efacility.FacilityRanker#setFacilities(java.util.ArrayList)
	 */
	public void setFacilities(ArrayList<Facility> facilities) {	
		this.facilities = facilities;
	}
	
	/* (non-Javadoc)
	 * @see com.uwiseismic.analyis.efacility.FacilityRanker#getOrderFacilities()
	 */
	public ArrayList<Facility> getOrderFacilities() {
		return orderedFacilities;
	}


	/* (non-Javadoc)
	 * @see com.uwiseismic.analyis.efacility.FacilityRanker#getFacilitiesRanking()
	 */
	public double[] getFacilitiesRanking() {
		return rankings;
	}

	/**
	 * @param e
	 * @throws Exception
	 */
	public void setEndPoint(Object e)throws ClassCastException{
		if(e instanceof Mean){
			Mean mn = (Mean)e;
			x = mn.getX();
			y = mn.getY();
		}
		else if(e instanceof Facility){
		    endPointIsFacility = true;
			Facility fc = (Facility)e;
			x = fc.getX();
			y = fc.getY();
		}
		else
			throw new ClassCastException("Did not set a valid class for end point. "
			        +"Need com.uwiseismic.analyis.efacility.Facility or com.uwiseismic.analyis.efacility.Mean");
	}
	
	/* (non-Javadoc)
	 * @see com.uwiseismic.analyis.efacility.FacilityRanker#performRanking()
	 */
	public void performRanking() throws IllegalClassStateException {
		double dists[] = new double[facilities.size()];

		if(graph == null){
			logger.error("Missing RoadGraph object!!");
			throw new IllegalClassStateException("The RoadGraph object was not set!");
		}
		
		FacilityWrapperComparator wrappers[];		
		if(endPointIsFacility)
		    wrappers = new FacilityWrapperComparator[facilities.size() - 1];//** exclusive of this facility
		else
		    wrappers = new FacilityWrapperComparator[facilities.size()];
		
		int n = 0;
		Facility t;
		rankings = new double[facilities.size()];
		double maxDist = -1.0;
		for(Iterator <Facility>i = facilities.iterator(); i.hasNext();){			
			t = i.next();
			
			//** don't include health facility in endpoints if case is a health facility to many 
			if(t.getX() == x && t.getY() == y){
			    //System.err.println("DEBUG: Skipping myself");
				continue;
			}			
							
			GraphPath<RoadGraphNode, RoadWeightedDjisktraEdge> shortestPath = 
	        		 new DjisktraShortestPath(graph).getShortestPath(y,x,t.getY(),t.getX());
			if(shortestPath == null){//** This is at a road hanging out in nowhere and will ignore because that means NO ONE is there
				logger.error("Found an end point that is connected to a road but the road is not connected to network. Randomly assigning.");
				//** randomly send somewhere
				dists[n] = Math.random()*10000;
			}else
				dists[n] = shortestPath.getWeight();
			if(dists[n] > maxDist)
				maxDist = dists[n];
			wrappers[n] = new FacilityWrapperComparator(t,0);
			n++;	
		}
		//** get NORMALIZED inv function for distance to facility INSTEAD OF BETA INDEX
		for(int i = 0; i < dists.length; i++)
			rankings[i] = 1.0005 - (dists[i]/maxDist);		
		
		//*** apply the big\small hospital weighting factor plus update the FacilityWrapperComparators
		for(int i = 0; i < rankings.length; i++){
		    rankings[i] = rankings[i]*((HealthFacility)facilities.get(i)).getFacilitySizeFactor();
			wrappers[i].r = rankings[i];
		}
		
		Arrays.sort(wrappers);
		Arrays.sort(rankings);
		for(int i = 0; i < rankings.length; i++)
			orderedFacilities.add(wrappers[i].f);
	}
	
	
	/**
	 * Get road graph (RoadGraph) created from OSM shapefiles
	 * @return
	 */
	public RoadGraph getGraph() {
		return graph;
	}

	/**
	 * Set road graph (RoadGraph) created from OSM shapefiles
	 * This MUST BE BEFORE FOR ANY OPS
	 * 
	 * @param graph
	 */
	public void setGraph(ErgoGraph graph) {
		this.graph = (RoadGraph)graph;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public FacilityRanker clone(){
		HealthFacilityRanker clone = new HealthFacilityRanker();
		clone.setFacilities(facilities);
		clone.setGraph(graph);
		return clone;
	}

	class FacilityWrapperComparator implements Comparable{
		
		public Facility f;
		public double r;
		public  FacilityWrapperComparator(Facility f, double r){
			this.f = f;
			this.r = r;
		}
		
	    public int compareTo(Object a) {
	    	FacilityWrapperComparator in = (FacilityWrapperComparator)a;
	    	if(r == in.r)
	    		return 0;
	    	if(r < in.r)
	    		return -1;
	        return 1;
	    }

	}

}
