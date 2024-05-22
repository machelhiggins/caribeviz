/*
 * Created on Jun 23, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.gis.util.geohash;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class FeatureHashable implements GeoHashable{

    private SimpleFeature feat;

    public FeatureHashable(SimpleFeature feat){
        this.feat = feat;
    }

    public Feature getFeature(){
    	return feat;
    }

    public Point getCentroid() {
        return ((Geometry)feat.getDefaultGeometry()).getCentroid();
    }

    public Geometry getEnvelope() {
        // TODO Auto-generated method stub
        return ((Geometry)feat.getDefaultGeometry()).getEnvelope();
    }

    public Geometry getGeometry() {
        // TODO Auto-generated method stub
        return (Geometry)feat.getDefaultGeometry();
    }


}
