/*
 * Created on Jan 20, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.kmeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.ObjectToReal;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class FeatureClusterable implements Clusterable2D{

    private CentroidGroup2D centroidGroup;
    private double latitude = Double.MAX_VALUE;
    private double longitude = Double.MAX_VALUE;
    private double value = 0;
    private boolean logValue = false;
    /**
     *
     * The attributes listed in <code>featureKeysToWeight</code> will be summed to a singular value.
     *
     * Note that the value held by this class is natural-logged since orders of magnitude variations is very large
     * @param feature
     * @param featuresKeyToWeigh
     * @throws ClusterableException
     */
    public FeatureClusterable(SimpleFeature feature, ArrayList<String> featureKeysToWeigh) throws ClusterableException{
        //** get lat and long       
        latitude = ObjectToReal.getMeDouble(feature.getAttribute("lat"));
        longitude = ObjectToReal.getMeDouble(feature.getAttribute("lon"));
        
        
        //** test if polyon (a la building footprints) and pull out centroid for lat\long
        if(latitude == 0 || longitude == 0){
            List attList = feature.getAttributes();
            Object attribs[] = new Object[attList.size()];
            attList.toArray(attribs);
            Point centre = null;
            for(int i = 0; i < attribs.length; i++){
                Object at = attribs[i];
                if(at instanceof MultiPolygon){
                    com.vividsolutions.jts.geom.MultiPolygon mp =
                        (MultiPolygon)at;
                    centre = mp.convexHull().getCentroid();
                    latitude = centre.getY();
                    longitude = centre.getX();
                    break;
                }
                if(at instanceof Polygon){
                    com.vividsolutions.jts.geom.Polygon mp =
                        (Polygon)at;
                    centre = mp.convexHull().getCentroid();
                    latitude = centre.getY();
                    longitude = centre.getX();
                    break;
                }
            }
        }
        if(latitude == 0|| longitude == 0){
            throw new ClusterableException("Could not determing the longitude and latitude of the feature.");
        }

        //** get the attributes that holds the weight values
        try{
      
        	double newVal = 0;
            for(Iterator <String>i = featureKeysToWeigh.iterator(); i.hasNext();){
                newVal = ObjectToReal.getMeDouble(feature.getAttribute(i.next()));
                value += newVal;
            }
        }catch(NullPointerException npe){throw new ClusterableException("Key must be a parseable double value.", npe);}
        catch(NumberFormatException nfe){throw new ClusterableException("Could not key's parse double value", nfe);}
    }

    public CentroidGroup2D getCentroidGroup() {
        return centroidGroup;
    }

    public double getValue() {
        if(logValue){
            if(value == 0)
                return 0;
            return Math.log(value);
        }else
            return value;
    }
    
    public void setValue(double value){
    	this.value = value;
    }

    public double getX() {
        return longitude;
    }

    public double getY() {
        return latitude;
    }

    public void setCentroidGroup(CentroidGroup2D centroidGroup) {
        this.centroidGroup = centroidGroup;
    }

    /**
     * @return the logValue
     */
    public boolean isLogValue() {
        return logValue;
    }

    /**
     * @param logValue the logValue to set
     */
    public void setLogValue(boolean logValue) {
        this.logValue = logValue;
    }

    public int compareTo(Object arg0) {
        Clusterable2D a = (Clusterable2D)arg0;
        if(a.getX() == getX() && a.getY() == getY() && a.getValue() == getValue())
            return 0;
        else if (a.getValue() > getValue())
            return -1;
        else
            return 1;
    }




}
