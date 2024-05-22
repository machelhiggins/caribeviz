/*
 * Created on Jun 23, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.gis.util.geohash;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public interface GeoHashable {

    /**
     * Get centroid of the <code>Geometry</code> this object represents
     *
     * @return
     */
    public Point getCentroid();

    /**
     * Return envelope of the <code>Geometry</code> this object represents
     *
     * @return
     */
    public Geometry getEnvelope();

    /**
     * Return the <code>Geometry</code> this object represents
     *
     * @return
     */
    public Geometry getGeometry();


}
