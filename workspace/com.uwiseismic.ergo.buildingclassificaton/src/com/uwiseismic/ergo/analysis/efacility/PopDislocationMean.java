/*
 * Created on Jan 22, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.ergo.analysis.efacility;

import java.util.ArrayList;
import java.util.HashMap;

import com.uwiseismic.ergo.graph.ErgoGraph;
import com.uwiseismic.gis.util.IllegalClassStateException;

public class PopDislocationMean extends DefaultMean{


    private double rankings[];
    private ArrayList <Facility> orderedFacils;
    private boolean alreadyRanked = false;
    private double uncateredPop = 0;
    private ErgoGraph graph;
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    /* (non-Javadoc)
     * @see com.uwiseismic.analyis.efacility.Mean#getPopulationDistribution()
     */
    public HashMap<Facility, Integer> getPopulationDistribution() {
        try{
         // TODO : check if this test should be done.
            //** Ranking may not be one-off but is EXTREMELY expensive.
            if(!alreadyRanked)
                performRankingAndPopDist();
            alreadyRanked = true;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        HashMap<Facility, Integer> popDist = new HashMap<Facility, Integer>();
        
        if(rankings.length == 0)
	    	return popDist;
       
        //** Will  dump population in top 10. If they're full, then save overflow        
        uncateredPop = Math.floor(getMeanValue());
        for(int i = 1; i <= 10 && i < rankings.length; i++){    
        	uncateredPop = orderedFacils.get(rankings.length-i).addOccupancy(uncateredPop, false);
        	if(uncateredPop == 0)
        		break;
        }
        
        //** and then add spill over to the rest based on ranking, regardless of capacity
        if(uncateredPop > 0){
	        double totalRank = 0;
	        for(int i = 1; i <= 10 && i < rankings.length; i++)
	        	totalRank += rankings[rankings.length -i];
	        for(int i = 1; i <= 10 && i < rankings.length; i++){
	        	orderedFacils.get(rankings.length-i).
	        		addOccupancy(Math.floor(uncateredPop*rankings[rankings.length-i]/totalRank), true);
	        }
        }
        for(int i = 1; i < 10 && i < rankings.length; i++){
        	popDist.put(orderedFacils.get(rankings.length-i), 
        		new Integer((int)Math.ceil(orderedFacils.get(rankings.length-i).getOccupancy())));      
        }
        
        return popDist;
    }


    /**
     * ADD IProgressMonitor TO THIS !!!!!!!!!!!
     *
     * @throws Exception
     */
    private void performRankingAndPopDist()throws Exception{       
        if(getFacilityRanker() == null){
        	this.setFacilityRanker(new ShelterFacilityRanker());        	
        }
        if(graph == null){
    		logger.error("Missing RoadGraph object!!");
    		throw new IllegalClassStateException("The RoadGraph object was not set!");
    	}
        getFacilityRanker().setGraph(graph);
        getFacilityRanker().setEndPoint(this);
        getFacilityRanker().setFacilities(facilities);
        getFacilityRanker().performRanking();
        rankings = getFacilityRanker().getFacilitiesRanking();
        orderedFacils = getFacilityRanker().getOrderFacilities();
        alreadyRanked = true;
    }

	/* (non-Javadoc)
	 * @see com.uwiseismic.analyis.efacility.Mean#getUncateredForPop()
	 */
	public double getUncateredForPop() {
		return uncateredPop;
	}
	
	@Override
	public void setGraph(ErgoGraph graph) {
		this.graph = graph;
		
	}

	@Override
	public ErgoGraph getGraph() {
		return graph;
	}

}
