package com.uwiseismic.ergo.roadnetwork;


import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.geohash.GeoHashable;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * 
 * Lite class to do searches on for roads. Simplified geometries for faster calculations.
 * Also stores road type
 * 
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public class RoadEdgeEvelope implements Comparable, GeoHashable{

	private Geometry envelope;
	private SimpleFeature feature;
	private Coordinate centre;
	private Point centreP;
	private int type;
	
	/**
	 * @param type constants from <code>RoadNetworkConstants</code>
	 * @param envelope The envelope of the geometry that is the road
	 */
	public RoadEdgeEvelope(int type, Geometry envelope){
		this.type = type;
		this.envelope = envelope;
		centreP = envelope.getCentroid();
		centre = centreP.getCoordinate();
	}

	/**
	 * Returns envelope of this road
	 * 
	 * @return
	 */
	public Geometry getEnvelope() {
		return envelope;
	}
	
	
	/**
	 * Get centre of this geometry envelope of this road
	 * 
	 * @return
	 */
	public Coordinate getCentre(){
		return centre;
	}

	/**
	 * 
	 * Retursn road type using <code>RoadNetworkConstants</code>
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set road type using constants using <code>RoadNetworkConstants</code>
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		RoadEdgeEvelope b = (RoadEdgeEvelope)arg0;		
		return centre.compareTo(b.getCentre());
	}

    /* (non-Javadoc)
     * @see com.uwiseismic.util.geohash.GeoHashable#getCentroid()
     */
    public Point getCentroid() {
        return centreP;
    }

    /* (non-Javadoc)
     * @see com.uwiseismic.util.geohash.GeoHashable#getGeometry()
     */
    public Geometry getGeometry() {       
        return ((Geometry)feature.getDefaultGeometry());
    }

    /**
     * @return the feature
     */
    public Feature getFeature() {
        return feature;
    }

    /**
     * @param feature the feature to set
     */
    public void setFeature(SimpleFeature feature) {
        this.feature = feature;
    }
	
	
}
