package com.uwiseismic.ergo.roadnetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;


import com.uwiseismic.ergo.roadnetwork.collection.RNObjectIntHashMap;
import com.uwiseismic.gis.util.DistanceCalcEarth;
import com.uwiseismic.gis.util.SimplePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public class RoadNetworkToGraph {
    private static final int COORD_STATE_UNKNOWN = 0;
    private static final int COORD_STATE_PILLAR = -2;
    private static final int FIRST_NODE_ID = 1;
    private int nextNodeId = FIRST_NODE_ID;
    private final RNObjectIntHashMap<Coordinate> coordState = new RNObjectIntHashMap<>(1000, 0.7f);
    private FeatureCollection roads;
    private RoadGraph graph = new RoadGraph();

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    
    private int debugNumberSavedNodesFromJunc = 0;
    
    public RoadNetworkToGraph(FeatureCollection roads){
    	this.roads = roads;
    }
    
    public RoadGraph processRoadNetwork(){
    	logger.info("Converting OSM Shapefile to graph");
    	long start = System.currentTimeMillis();
    	processJunctions();
    	processRoads();
    	logger.info("It took "+((System.currentTimeMillis() - start))+" milliseconds to convert OSM Shapefile to graph");
    	return graph;
    }
    
    private List<Coordinate[]> getCoords(Object o) {
        ArrayList<Coordinate[]> ret = new ArrayList<>();
        if (o == null) {
            return ret;
        }

        if (o instanceof LineString) {
            ret.add(((LineString) o).getCoordinates());
        } else if (o instanceof MultiLineString) {
            MultiLineString mls = (MultiLineString) o;
            int n = mls.getNumGeometries();
            for (int i = 0; i < n; i++) {
                ret.add(mls.getGeometryN(i).getCoordinates());
            }
        }

        return ret;
    }
    
    private void processJunctions() {
        FeatureIterator<SimpleFeature> roadsIterator = roads.features();
        try {
            HashSet<Coordinate> tmpSet = new HashSet<>();
            while (roadsIterator.hasNext()) {
                SimpleFeature road = roadsIterator.next();

                for (Coordinate[] points : getCoords(road.getDefaultGeometry())) {
                    tmpSet.clear();
                    for (int i = 0; i < points.length; i++) {
                        Coordinate c = points[i];

                        // don't add the same coord twice for the same edge - happens with bad geometry, i.e.
                        // duplicate coords or a road which forms a circle (e.g. roundabout)
                        if (tmpSet.contains(c))
                            continue;

                        tmpSet.add(c);

                        // skip if its already a node
                        int state = coordState.get(c);
                        if (state >= FIRST_NODE_ID) {
                            continue;
                        }

                        if (i == 0 || i == points.length - 1 || state == COORD_STATE_PILLAR) {
                            // turn into a node if its the first or last
                            // point, or already appeared in another edge
                            int nodeId = nextNodeId++;
                            coordState.put(c, nodeId);
                            saveStartEndNodePosition(nodeId, c);
                        } else if (state == COORD_STATE_UNKNOWN) {
                            // mark it as a pillar (which may get upgraded
                            // to an edge later)
                            coordState.put(c, COORD_STATE_PILLAR);
                        }
                    }
                }

            }
        }catch(Exception e){e.printStackTrace();} 
       
        if (nextNodeId == FIRST_NODE_ID)
            throw new IllegalArgumentException("No data found for roads file ");
        logger.info("Number of junction points : " + (nextNodeId - FIRST_NODE_ID));
    }

    private void processRoads() {
        FeatureIterator<SimpleFeature> roadIterator = roads.features();;

        try {           
            while (roadIterator.hasNext()) {
                SimpleFeature road = roadIterator.next();

                for (Coordinate[] points : getCoords(road.getDefaultGeometry())) {

                    // Parse all points in the geometry, splitting into
                    // individual graphhopper edges
                    // whenever we find a node in the list of points
                    Coordinate startTowerPnt = null;
                    List<Coordinate> pillars = new ArrayList<Coordinate>();
                    for (Coordinate point : points) {
                        if (startTowerPnt == null) {
                            startTowerPnt = point;
                        } else {
                            int state = coordState.get(point);
                            if (state >= FIRST_NODE_ID) {
                                int fromTowerNodeId = coordState.get(startTowerPnt);
                                
                                int toTowerNodeId = state;
                                // get distance and estimated centres
                                double distance = getWayLength(startTowerPnt, pillars, point);
                                SimplePoint estmCentre = new SimplePoint(
                                        0.5 * (lat(startTowerPnt) + lat(point)),
                                        0.5 * (lng(startTowerPnt) + lng(point)));
                               
                                addEdge(fromTowerNodeId, toTowerNodeId, road, distance, estmCentre);
                                startTowerPnt = point;
                                pillars.clear();
                            } else {
                                pillars.add(point);
                            }
                        }
                    }
                }

            }
        } catch(Exception ex){ex.printStackTrace();}        
    }
        
    /*
     * Get longitude using the current long-lat order convention
     */
    private double lng(Coordinate coordinate) {
        return coordinate.getOrdinate(0);
    }

    /*
	 * Get latitude using the current long-lat order convention
     */
    private double lat(Coordinate coordinate) {
        return coordinate.getOrdinate(1);
    }

    private void saveStartEndNodePosition(int nodeId, Coordinate point) {
    	//System.out.println("Adding node from junction search which may be a waste of time");
    	debugNumberSavedNodesFromJunc++;
        graph.addNode(new RoadGraphNode(new SimplePoint(nodeId, lat(point), lng(point)), null));
    }
    	
    private double getWayLength(Coordinate start, List<Coordinate> pillars, Coordinate end) {
        double distance = 0;

        Coordinate previous = start;
        for (Coordinate point : pillars) {
            distance += DistanceCalcEarth.calcDist(lat(previous), lng(previous), lat(point), lng(point));
            previous = point;
        }
        distance += DistanceCalcEarth.calcDist(lat(previous), lng(previous), lat(end), lng(end));

        return distance;
    }

	private void addEdge(int fromTowerNodeID, int toTowerNodeID, SimpleFeature road, double distance, SimplePoint estimatedCenter) {
				
		RoadGraphEdge edge = new RoadGraphEdge(road, fromTowerNodeID, toTowerNodeID);

		edge.setWayDistance(distance);
		edge.setEstimatedCenter(estimatedCenter);		
		graph.addEdge(edge);
	}
    
//    private long getOSMId(SimpleFeature road) {
//        long id = Long.parseLong(road.getAttribute("osm_id").toString());
//        return id;
//    }

}
