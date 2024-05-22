package com.uwiseismic.gis.util;

import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class WGS84BoundingBox extends Envelope2D{
	
	private final static CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
	
	public WGS84BoundingBox(){
		super(crs);
	}

	public WGS84BoundingBox(double x, double y, double width, double height){
		super(crs,x,y,width,height);
	}
}
