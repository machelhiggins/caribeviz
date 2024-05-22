/*
 * Created on Jun 23, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.gis.util.geohash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Create tessellation of bounding box of GeoHashable collection for faster searching
 *
 * @author <a href="mailto:machelhiggins.hotmail.com">Machel Higgins </a>
 *
 * TODO
 */
public class GeoHash {

    private Collection <GeoHashable> collection;
    private Envelope envelope;
    private Geometry envGeom;
    private Cell topCell;
    private double smallestCellSize;
    private int levels;
    private double EXTEND_X = 0.25; // amount (degrees) to extend x so for better searching
    private double EXTEND_Y = 0.25; // amount (degrees) to extend y so for better searching


    /**
     *
     *
     * @param smallestCellSize DECIMAL DEGREES; > 100 m
     * @param envelope Envelope to tessellate
     */
    public GeoHash(double smallestCellSize, ReferencedEnvelope envelope){
        this.smallestCellSize = smallestCellSize;        
        //
        // ** bounding envelope too small when searching for things way outside Geohash
        envelope = new ReferencedEnvelope(envelope.getMinX()-EXTEND_X,envelope.getMaxX()+EXTEND_X,
        		envelope.getMinY()-EXTEND_Y, envelope.getMaxY()+EXTEND_Y, 
        		envelope.getCoordinateReferenceSystem());
        this.envelope = envelope;
        
        //** determine how many levels to subdivide down to
        double xMinLevel = Math.floor(Math.log(envelope.getWidth()/smallestCellSize)/Math.log(2));
        double yMinLevel = Math.floor(Math.log(envelope.getHeight()/smallestCellSize)/Math.log(2));
        
        //** if minLevel less than 2 because smallest cell size bigger than envelop
        //** we hardcode to two levels. These means someone (ME) messed up somewhere
        //** or issue with shapefile
        if(xMinLevel < 0) xMinLevel = 2;
        if(yMinLevel < 0) yMinLevel = 2;
               
        if(xMinLevel > yMinLevel)
            levels = (int)xMinLevel;
        else
            levels = (int)yMinLevel;
//        System.out.println("xLevel = " + xMinLevel + "\nEnv W = " + envelope.getWidth() + " Env H = " + envelope.getHeight()
//        		+ "\nYMin = " + yMinLevel);
        if(levels > 5)
        	levels = 5;
        //** create a Geometry for envelope for testing etc...
        GeometryFactory fact = new GeometryFactory();
        envGeom = new Polygon(fact.createLinearRing(
                new Coordinate[]{
                        new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                        new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                        new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                        new Coordinate(envelope.getMinX(), envelope.getMinY()),
                        new Coordinate(envelope.getMinX(), envelope.getMaxY())}),
                        null, fact);
        createQuadTree();
    }


    public void setHashables(Collection <GeoHashable>collection){
        this.collection = collection;
        GeoHashable t;
        for(Iterator <GeoHashable>i = collection.iterator(); i.hasNext();){
            t = i.next();
            if(!topCell.addConstituent(t)){
            	System.err.println("Found member that was not added to GeoHash object");
            	//System.out.println(topCell.addConstituentDEBUG(t));
            }
        }
    }

    public Collection getHashables(){
    	return collection;
    }

    private void createQuadTree(){
        topCell = new Cell(envGeom, levels);
    }

    public Envelope getEnvelope(){
        return envelope;
    }
    
    /**
     * Return GeoHashables that contains <code> geom</code>
     * @param geom
     * @return
     */
    public ArrayList <GeoHashable> getContainedIn(Geometry geom){
    	return topCell.getAnythingContainedIn(geom);
    }
    
    
    /**
     * 
     * Returns GeoHashables contained in <code>geom</code>
     * @param geom
     * @return
     */
    public ArrayList <GeoHashable> getContaining(Geometry geom){
    	return topCell.getAnythingContaining(geom);
    }

    public ArrayList <GeoHashable> getIntersectedBy(Geometry geom){
    	return topCell.getAnythingContainedIn(geom);
    }


    /**
     * Returns the contents of the smallest cell that the geom is contained in
     * @param geom
     * @return
     */
    public ArrayList <GeoHashable> getContentsOfBottomCell(Geometry geom){
        return topCell.getContentsOfBottomCell(geom);
    }
    
    public static double getASensibleSmallestCellSize(ReferencedEnvelope env){
        double w = env.getWidth();
        double h = env.getHeight();
        if(w > h)
        	return h/Math.pow(2, 5);
        else
        	return w/Math.pow(2, 5);
    }
}
