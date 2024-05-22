package com.uwiseismic.testing;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.coords.UTMCoord;

public class ScratchDELETEME {

	public static void main(String args[]){
		try{
			CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
			crs = CRS.decode("EPSG:4326");
			CoordinateReferenceSystem crs2 = CRS.decode("EPSG:3857");
						
			MathTransform transform = CRS.findMathTransform(crs, crs2, true);
			
			CoordinateReferenceSystem utm19N = CRS.decode("EPSG:32618");
			
			MathTransform mercTo19N = CRS.findMathTransform(crs2, utm19N, true);
	
			Coordinate nll = new Coordinate();
			Coordinate nur = new Coordinate();
			
			Coordinate mercLL = JTS.transform(new Coordinate(-76.90697139694673,17.94678958687012,0),nll, transform);
			Coordinate mercUR = JTS.transform(new Coordinate(-76.65538977338475,18.07139741296038,0),nur, transform);
			
			Coordinate nll2 = new Coordinate();
			Coordinate nur2 = new Coordinate();
			Coordinate ll = JTS.transform(mercLL , nll2, mercTo19N);
			Coordinate ur = JTS.transform(mercUR, nur2, mercTo19N);
			
			System.out.println(mercLL.x+","+mercLL.y);
			System.out.println(mercUR.x+","+mercUR.y);
			
			LatLon ll_nll = UTMCoord.locationFromUTMCoord(18, AVKey.NORTH, ll.x, ll.y);
			LatLon ll_nur = UTMCoord.locationFromUTMCoord(18, AVKey.NORTH, ur.x, ur.y);
			System.out.println(ll_nll.getLatitude().degrees +","+ ll_nll.getLongitude().degrees);
			System.out.println(ll_nur.getLatitude().degrees +","+ ll_nur.getLongitude().degrees);

			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
