package com.uwiseismic.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.gis.util.ObjectToReal;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class DominicaExportCentroidsAttribsForReport {

	public static void main(String[] args) {
		String dataToAttrib = 
				"C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\Documents\\Final Report\\Figures\\datasets_to_attributes.txt";
		String shapefiles = 
				"C:\\Users\\machel\\OneDrive\\DRRC\\Dominica_Assessment\\Documents\\Final Report\\Figures\\data_to_convert.txt";
		HashMap <String,ArrayList<String>>datasetsToAttribs = new HashMap<String,ArrayList<String>>();
		HashMap <String,ArrayList<String>>dataAttribsToPull = new HashMap<String,ArrayList<String>>();
		String destDir = "C:\\Users\\machel\\Documents\\vbox_share\\fedora_22\\CARIBEVIZ\\Dominica_assessment\\plotting";
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(dataToAttrib));
			String l = reader.readLine();
			String c[];
			int i = 0;
			while(l != null){
				c = l.split("\\s+");
				ArrayList<String> attribsWeWant = new ArrayList<String>();
				for(i = 1; i < c.length; i++)
					attribsWeWant.add(c[i]);
				datasetsToAttribs.put(c[0], attribsWeWant);				
				l = reader.readLine();
			}
			reader.close();
			
			reader = new BufferedReader(new FileReader(shapefiles));
			l = reader.readLine();
			String c2[];
			while(l != null){;
				c2 = l.split("\"");
				c = c2[2].split("\\s+");
				//System.out.println(c2[1]);
				ArrayList<String> attribGroup = new ArrayList<String>();
				for(i = 1; i < c.length; i++){
					//System.out.println("\t\t["+c[i]+"]");
					attribGroup.add(c[i]);
				}
				dataAttribsToPull.put(c2[1], attribGroup);				
				l = reader.readLine();
			}
			reader.close();
			
			//** create directories
			for(String f: dataAttribsToPull.keySet()){
				File file = new File(f);
				File parentDir = file.getParentFile();
				File parentParentDir = parentDir.getParentFile();
				String dataDestDir = destDir+"/"+parentParentDir.getName()+"/"+parentDir.getName();
				new File(dataDestDir).mkdirs();
				ArrayList<String> attribGroup = dataAttribsToPull.get(f);
				BufferedWriter writers[] = new BufferedWriter[attribGroup.size()];
				String groupNames[] = new String[attribGroup.size()];
				//** open writers
				i = 0;
				for(String group : attribGroup){
					writers[i] = new BufferedWriter(new FileWriter(dataDestDir+"/"+group+".dat"));
					groupNames[i] = group;
					i++;
				}
				//** open shapefile and write out
				DataStore store = FileDataStoreFinder.getDataStore(new java.net.URL("file://"+
                        f));	             
				String []typeNames = store.getTypeNames();
				FeatureCollection fc = store.getFeatureSource(typeNames[0]).getFeatures();
				for(i = 0; i < writers.length; i++){
					FeatureIterator fIter = (FeatureIterator) fc.features();
					double centerCoords[] ;
					ArrayList<String> attribsWeWant = datasetsToAttribs.get(groupNames[i]);
			        while(fIter.hasNext()){
			        	 SimpleFeature feat = (SimpleFeature)fIter.next();
			        	 centerCoords = getGravityCenter(feat.getDefaultGeometry());
			        	 writers[i].write(centerCoords[0]+" "+centerCoords[1]+" ");
			        	 for(String attrib : attribsWeWant)
			        		 writers[i].write(ObjectToReal.getMeDouble(feat.getAttribute(attrib))+" ");
			        	 writers[i].write("\n");
					}				
				}
				for(i = 0; i < writers.length; i++)
					writers[i].close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
		
    public static double[] getGravityCenter(Object o) {
        double coords[] = null;
        if (o == null) {
            return coords;
        }

        if (o instanceof LineString) {
        	Coordinate[] points = ((LineString) o).getCoordinates();
        	coords = new double[2];
        	coords[0] = 0;
        	coords[1] = 0;
        	double count = 0;
        	for(int i = 0; i < points.length; i++){
        		coords[0] = coords[0] + points[i].x;
            	coords[1] = coords[1] + points[i].y;
            	count++;
        	}
        	coords[0] = coords[0]/count;
        	coords[1] = coords[1]/count;
        } else if (o instanceof MultiLineString) {
        	coords = new double[2];
        	coords[0] = 0;
        	coords[1] = 0;
        	double count = 0;
            MultiLineString mls = (MultiLineString) o;
            int n = mls.getNumGeometries();
            for (int i = 0; i < n; i++) {
            	Coordinate[] points = mls.getGeometryN(i).getCoordinates();
            	for(int j = 0; j < points.length; j++){
            		coords[0] = coords[0] + points[i].x;
                	coords[1] = coords[1] + points[i].y;
                	count++;
            	}
            }
            coords[0] = coords[0]/count;
        	coords[1] = coords[1]/count;
        }else if (o instanceof Polygon) {
        	Coordinate[] points = ((Polygon)o).getCoordinates();
        	coords = new double[2];
        	coords[0] = 0;
        	coords[1] = 0;
        	double count = 0;
        	for(int i = 0; i < points.length; i++){
        		coords[0] = coords[0] + points[i].x;
            	coords[1] = coords[1] + points[i].y;
            	count++;
        	}
        	coords[0] = coords[0]/count;
        	coords[1] = coords[1]/count;
        }else if (o instanceof MultiPolygon) {
        	coords = new double[2];
        	coords[0] = 0;
        	coords[1] = 0;
        	double count = 0;
        	MultiPolygon mls = (MultiPolygon) o;
            int n = mls.getNumGeometries();
            for (int i = 0; i < n; i++) {
            	Coordinate[] points = mls.getGeometryN(i).getCoordinates();
            	for(int j = 0; j < points.length; j++){
            		try{
            		coords[0] = coords[0] + points[i].x;
                	coords[1] = coords[1] + points[i].y;
                	count++;
            		}catch(Exception ex){
            			//ex.printStackTrace();
            		}
            	}
            }
            coords[0] = coords[0]/count;
        	coords[1] = coords[1]/count;
        }
        else if (o instanceof Point) {
        	Point p = (Point)o;
        	coords = new double[2];
        	coords[0] = p.getX();
        	coords[1] = p.getY();
        }
        return coords;
    }

}
