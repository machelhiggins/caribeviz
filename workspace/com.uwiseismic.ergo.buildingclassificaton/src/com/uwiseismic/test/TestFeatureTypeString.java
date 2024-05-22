package com.uwiseismic.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class TestFeatureTypeString {

	public static void main(String[] args) {
		File fn = new File("C:\\temp\\nkgn\\New_Kingston_WGS84\\New_Kingston.shp");
        DataStore store;
		try {
			store = FileDataStoreFinder.getDataStore(fn.toURI().toURL());
			String names[] = store.getTypeNames();
	        FeatureSource featureSource = store.getFeatureSource(names[0]);           
	        FeatureCollection c = featureSource.getFeatures();
	        for(FeatureIterator i = c.features();i.hasNext();){
	        	/*AttributeType att[] = i.next().getFeatureType().getAttributeTypes();
	        	for(int z = 0;  z < att.length; z++){
	        		System.out.print(att[z].getLocalName()+":"+att[z].getBinding().getName());
	        		System.out.print(", ");
	        	}
	        	System.out.println();*/
	        	;
	        	Coordinate coords[] = ((Geometry)i.next().getDefaultGeometryProperty().getValue()).getCoordinates();
	        	for(int z = 0;  z < coords.length; z++){
	        		System.out.print(coords[z].x+":"+coords[z].y);
	        		System.out.print(", ");
	        	}
	        	System.out.println();
	        }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

	}

}
