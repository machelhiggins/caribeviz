package com.uwiseismic.ergo.analysis.efacility;

import java.awt.geom.Point2D;
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


/**
 * THIS IS EXACTLY LIKE <code>HealthcareFacilityRanker</code>. This has been separated to accommodate possilbe future changes to
 * <code>HealthcareFacilityRanker</code>
 * 
 * 
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public class ShelterFacilityRanker implements FacilityRanker {

	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
	private ArrayList<Facility> facilities;
	private ArrayList<Facility> orderedFacilities = new ArrayList<Facility>();;
	private double x = 0;
	private double y = 0;
	private double rankings[];
	private RoadGraph graph;
	
		
	
	public void setFacilities(ArrayList<Facility> facilities) {	
		this.facilities = facilities;
	}

	
	public ArrayList<Facility> getOrderFacilities() {
		return orderedFacilities;
	}

	
	public double[] getFacilitiesRanking() {
		return rankings;
	}

	public void setEndPoint(Object e)throws ClassCastException{
		if(e instanceof Mean){
			Mean mn = (Mean)e;
			x = mn.getX();
			y = mn.getY();
		}
		else if(e instanceof Facility){
			Facility fc = (Facility)e;
			x = fc.getX();
			y = fc.getY();
		}
		else
		    throw new ClassCastException("Did not set a valid class for end point. "
                    +"Need com.uwiseismic.analyis.efacility.Facility or com.uwiseismic.analyis.efacility.Mean");
	}
	
	
	public void performRanking() throws IllegalClassStateException {
		
		
		if(graph == null){
			logger.error("Missing RoadGraph object!!");
			throw new IllegalClassStateException("The RoadGraph object was not set!");
		}
		int n = 0;		
		int activeFacilities = 0;
		for(Facility t: facilities){
			if(!t.isDecommissioned())
				activeFacilities++;
		}
		double dists[] = new double[activeFacilities];
		rankings = new double[activeFacilities];
		double maxDist = Double.MIN_VALUE;
		FacilityWrapperComparator wrappers[] = new FacilityWrapperComparator[activeFacilities];
		n = 0;
		for(Facility t: facilities){			
			//** ignore if facility decommissioned
			if(t.isDecommissioned())
				continue;
			//** don't include health facility in endpoints if case is a health facility to many 
			if(t.getX() == x && t.getY() == y){
				dists[n] = Math.random()*1e6;
			}else{
				GraphPath<RoadGraphNode, RoadWeightedDjisktraEdge> shortestPath = 
		        		 new DjisktraShortestPath(graph).getShortestPath(y,x,t.getY(),t.getX());
				
				if(shortestPath == null){//** This is at a road hanging out in nowhere and will ignore because that means NO ONE is there
					logger.error("Found an end point that is connected to a road but the road is not connected to network. Randomly assigning.");
					//** randomly send somewhere
					dists[n] = Math.random()*1e6;
				}else{
					dists[n] = shortestPath.getWeight();
					if(dists[n] > maxDist)
						maxDist = dists[n];
				}
			}			
			wrappers[n] = new FacilityWrapperComparator(t,0);
			n++;	
		}
		
		//** get NORMALIZED inv function for distance to facility INSTEAD OF BETA INDEX
		for(int i = 0; i < dists.length; i++){
			rankings[i] = 1.0005 - (dists[i]/maxDist);
			wrappers[i].r = rankings[i];
		}
			
		Arrays.sort(wrappers);
		Arrays.sort(rankings);
		for(int i = 0; i < rankings.length; i++)
			orderedFacilities.add(wrappers[i].f);
	}

	
	/**
	 * Set road graph (RoadGraph) created from OSM shapefiles
	 * This MUST BE DONE BEFORE ANY OPS
	 * @return
	 */
	public ErgoGraph getGraph() {
		return graph;
	}
	

	@Override
	public void setGraph(ErgoGraph graph) {
		this.graph = (RoadGraph)graph;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public FacilityRanker clone(){
		ShelterFacilityRanker clone = new ShelterFacilityRanker();
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