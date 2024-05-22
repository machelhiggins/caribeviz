package com.uwiseismic.ergo.roadnetwork;

import org.opengis.feature.simple.SimpleFeature;

public class RoadNetworkConstants {
    public static final int HIGHWAY = 1;
    public static final int PRIMARY = 2;
    public static final int SECONDARY = 3;
    public static final int RESIDENTIAL = 4;
    
    public static int getRoadType(SimpleFeature feature){
    	String roadType = (String) (feature.getAttribute("highway"));
    	if(roadType.matches("highway.*"))
            return RoadNetworkConstants.HIGHWAY;
        else if(roadType.matches("primary.*"))
            return RoadNetworkConstants.PRIMARY;
        else if(roadType.matches("secondary.*"))
            return RoadNetworkConstants.SECONDARY;
        else
            return RoadNetworkConstants.RESIDENTIAL;
    }
}
