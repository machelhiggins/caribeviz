package com.uwiseismic.test;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.DefaultFeatureCollections;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.feature.Feature;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

//import com.uwiseismic.geotools.shapefile.ShapefileFileResourceInfo;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

public class GeotoolsBSConverter {
    
    
    public GeotoolsBSConverter(String shapefileFilename)throws Exception {
        try{
            
          //** open shape file and get coordinate system
        	File temp = new File(shapefileFilename);
        	DataStore store = FileDataStoreFinder.getDataStore(temp.toURI().toURL());
                            	
        	if(store == null)
        		System.err.println("Shapefiledatastore  is null");
        	
            String typeName = store.getTypeNames()[0];
            
        	if(store.getTypeNames() == null)
        		System.err.println("DID NOT GET ANY NAMES");
        	else{
        		String names[]  = store.getTypeNames();
        		for(int x =0; x < names.length; x++)
        			System.out.println(names[x]);
        	}
        	
        	if(store.getFeatureSource(typeName)== null){
        		System.err.println("FEATURE SOURCE FEATURES IS NULL");
        	}
        	
        	
        	
        	String []typeNames = store.getTypeNames();
        	FeatureCollection fsCollection =  store.getFeatureSource(typeNames[0]).getFeatures();
        	FeatureIterator i = (FeatureIterator) fsCollection.features();        	
        	SimpleFeature feature = null;             
            
            
            //** get shape file coordinate system
            CoordinateReferenceSystem crs =  store.getFeatureSource(typeNames[0]).getSchema().getCoordinateReferenceSystem();
            MathTransform transform = CRS.findMathTransform(crs,DefaultGeographicCRS.WGS84, false);             

            //** Iterate over all features in shapefile and add attributes
            com.vividsolutions.jts.geom.Point centre = null;    
            int buildingID = 0;            
            @SuppressWarnings("deprecation")
            ListFeatureCollection fc = new ListFeatureCollection(DefaultFeatureCollections.newCollection("fake"));      
            while(i.hasNext()){
                feature = (SimpleFeature)i.next();
                Object bldgCapObj;
                bldgCapObj = feature.getAttribute("BLDG_POP");
                if(bldgCapObj != null){
                	System.out.println(bldgCapObj.getClass().getName());                	
                }
                else{
                	continue;
                }
                Object attribs[] = new Object[feature.getFeatureType().getAttributeCount()];
                attribs = feature.getAttributes().toArray(attribs);
                
                Object fatArray[] =  new Object[attribs.length+26];
                FeatureType featureType = (FeatureType) feature.getFeatureType();
                String storeys = (String)feature.getAttribute("STOREYS");
                double area = 0.0;
                int index = 0;
                StringBuffer featureString = new StringBuffer();
                for(int it = 0; it < attribs.length; it++){
                	
                    Object at = attribs[it];      
                    //** build feature string
                    featureString.append(featureType.getDescription().toString());
                    featureString.append(":");                                      
                    
                    //** get centroid ***
                    if(at instanceof MultiPolygon){
                        area = ((MultiPolygon)at).getArea();
                        //** Transform this geometry ********** technically DEBUG ******************
                        Geometry targetGeometry = JTS.transform( (com.vividsolutions.jts.geom.MultiPolygon)at, transform);
                        at = targetGeometry; 
                        com.vividsolutions.jts.geom.MultiPolygon mp =
                            (MultiPolygon)at;                        
                        centre = mp.convexHull().getCentroid();          
                        //at = centre;//***************** DEBUG : Converting to point *****************
                    }
                    
                    String type = featureType.getBinding().toString();
                    type = type.substring(type.lastIndexOf('.'+1), type.length());
                    
                    if(type.matches("Long"))                        
                        featureString.append("Integer");
                    else
                        featureString.append(type);
                                                                
                    if(it + 1 < attribs.length)
                        featureString.append(",");                                       
                    
                    fatArray[index] = at;
                    index++;
                }
                attribs = null;                                             
                                                                                                                
                //** Set attribute type information
                
                //** BLDG_ID
                fatArray[index] = new Integer(buildingID);
                featureString.append(",BLDG_ID:Integer");                                                                                       
                
                //** PAR_ID
                index++;
                fatArray[index] = new Integer(0);
                featureString.append(",PAR_ID:Integer");                                                  
                                
                //** PARID_CARD
                index++;
                fatArray[index] = "017056 00010_1";
                featureString.append(",PARID_CARD:String");                                  
   
                //** LAT
                index++;    
                fatArray[index] = new Double(centre.getCoordinate().y);
                featureString.append(",LAT:Double");                                  
                 
                //** LON
                index++;    
                fatArray[index] = new Double(centre.getCoordinate().x);
                featureString.append(",LON:Double");                                   

                
                //** YEAR_BUILT
                index++;     
                fatArray[index] = new Integer(1995);
                featureString.append(",YEAR_BUILT:Integer");                                  

                                                
                //** NO_STORIES
                index++;
                fatArray[index] = new Integer(storeys);
                featureString.append(",NO_STORIES:Integer");                                  

                
                //** A_STORIES
                index++;
                fatArray[index] = new Integer(storeys);
                featureString.append(",A_STORIES:Integer");                                  
                
                //** SQ_FEET
                index++;  
                fatArray[index] = new Double(Double.parseDouble(storeys)*area);
                featureString.append(",SQ_FOOT:Integer");                                  

                //** GSQ_FEET
                index++;  
                fatArray[index] = new Double(area);
                featureString.append(",GSQ_FEET:Integer");  
                
              //**APPR_VAL
                index++;  
                fatArray[index] = new Integer(1000000);
                featureString.append(",APPR_BLDG");
                featureString.append(":");
                featureString.append("Integer");  
                
              //**REPL_CST            
                index++;  
                fatArray[index] = new Integer(1000000);
                featureString.append(",REPL_CST");
                featureString.append(":");
                featureString.append("Integer");
                
              //**STR_CST       
                index++;  
                fatArray[index] = new Integer(1000000);
                featureString.append(",STR_CST");
                featureString.append(":");
                featureString.append("Integer");
                
              //**NSTRA_CST   
                index++;  
                fatArray[index] = new Integer(1000000);
                featureString.append(",NSTRA_CST");
                featureString.append(":");
                featureString.append("Integer");
                               
              //**NSTRD_CST
                index++;
                fatArray[index] = new Integer(1000000);
                featureString.append(",NSTRD_CST");
                featureString.append(":");
                featureString.append("Integer");
                
              //**CONT_VAL                    
                index++;  
                fatArray[index] = new Integer(10000);
                featureString.append(",CONT_VAL");
                featureString.append(":");
                featureString.append("Integer");
                
                if(area > 250){                
                 
                    //** B_STORIES
                    index++;  
                    fatArray[index] = new Integer(2);
                    featureString.append(",B_STORIES:Integer"); 
                    
                  //** STRUCT_TYPE                 
                    index++;  
                    fatArray[index] = "RM1M";
                    featureString.append(",STRUCT_TYPE");
                    featureString.append(":");
                    featureString.append("String");
                    
                    //** STRUCT_TYPE                 
                    index++;  
                    fatArray[index] = "RM1M";
                    featureString.append(",STR_TYP2");
                    featureString.append(":");
                    featureString.append("String");
                    
                    
                  //**OCC_TYPE
                    index++;  
                    fatArray[index] = "COM5";
                    featureString.append(",OCC_TYPE");
                    featureString.append(":");
                    featureString.append("String");
                    
                  //** DGN_LVL
                    index++;  
                    fatArray[index] = "RM1M";
                    featureString.append(",DGN_LVL");
                    featureString.append(":");
                    featureString.append("String");
                    
                  //**STRUCT_TYPE
                    index++;  
                    fatArray[index] = new Integer(0);
                    //featureString.append(",NO_DU");
                    featureString.append(",DWELL_UNIT");
                    featureString.append(":");
                    featureString.append("Integer");
                    
                }else{
                    //** B_STORIES
                    index++;  
                    fatArray[index] = new Integer(0);
                    featureString.append(",B_STORIES:Integer"); 
                    
                    //** STRUCT_TYPE                 
                    index++;  
                    //fatArray[index] = "RM2L";
                    fatArray[index] = "RM1L";
                    featureString.append(",STRUCT_TYPE");
                    featureString.append(":");
                    featureString.append("String");                
                    
                    index++;  
                    //fatArray[index] = "RM2L";
                    fatArray[index] = "RM1L";
                    featureString.append(",STR_TYP2");
                    featureString.append(":");
                    featureString.append("String");

                    
                    //**OCC_TYPE
                    index++;  
                    fatArray[index] = "RES1";
                    featureString.append(",OCC_TYPE");
                    featureString.append(":");
                    featureString.append("String");                   
                    
                    index++;  
                    fatArray[index] = "RM1L";
                    featureString.append(",DGN_LVL");
                    featureString.append(":");
                    featureString.append("String");
                    
                    //**NO_DU
                    index++;  
                    fatArray[index] = new Integer(2);
                    //featureString.append(",NO_DU");
                    featureString.append(",DWELL_UNIT");
                    featureString.append(":");
                    featureString.append("Integer");
                }
              //**STR_PROB
                index++;  
                fatArray[index] = new Double(75.0);
                featureString.append(",STR_PROB");
                featureString.append(":");
                featureString.append("Double");
                
              //**MAJOR_OCC
                index++;  
                fatArray[index] = "COM4";
                featureString.append(",MAJOR_OCC");
                featureString.append(":");
                featureString.append("String");
                
              //**BROAD_OCC
                index++;  
                fatArray[index] = "COM4";
                featureString.append(",BROAD_OCC");
                featureString.append(":");
                featureString.append("String");  
                
                //**BROAD_OCC
                index++;  
                fatArray[index] = "FALSE";
                featureString.append(",EFACILITY");
                featureString.append(":");
                featureString.append("String");
        
 
                //**build the feature                
                //** use org.geotools.feature.simple.SimpleFeatureTypeBuilder in the future
                featureType = DataUtilities.createType(feature.getFeatureType().getTypeName(),
                        featureString.toString());
                //** Transform - entire shape file should be transformed into regular WGS84
                //featureType = DefaultFeatureTypeFactory.transform(featureType, DefaultGeographicCRS.WGS84);//.retype(featureType, DefaultGeographicCRS.WGS84);                
                
                feature.setAttributes(fatArray);        
                
                fc.add(feature);
            }
            
            System.err.println(fc.size());
            
            
            //**** WRITE NEW SHAPE FILE************************************
            String newFN = shapefileFilename.substring(0,shapefileFilename.lastIndexOf("\\"))
                    +"\\b-maeved-"
                    +shapefileFilename.substring(shapefileFilename.lastIndexOf("\\")+1,shapefileFilename.length()) ;           
            
            System.out.println("CREATING NEW FILE > "+newFN);
            
            //** METHOD 1*********************************************
            //**                                                
            File newFile = new File(newFN);           

            FeatureType mainFeatureType = feature.getFeatureType();  
            DataStore newDataStore = new ShapefileDataStore( newFile.toURI().toURL() );
            newDataStore.createSchema( feature.getFeatureType() );
            FeatureStore featureStore = (FeatureStore) newDataStore.getFeatureSource( mainFeatureType.getName() );                            

            newDataStore.createSchema(feature.getFeatureType());                            
            
             //** Write the features to the shapefile
            Transaction transaction = new DefaultTransaction("create");            
            typeName = newDataStore.getTypeNames()[0];
            FeatureStore featureSource = (FeatureStore) featureStore.getDataStore();
            if (featureSource instanceof FeatureStore) {
                FeatureStore featureStoreT = (FeatureStore) featureSource;
                featureStoreT.setTransaction(transaction);
                try {
                	featureStoreT.addFeatures(fc);
                    
                    transaction.commit();

                    transaction.close();
                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();
                    throw problem;

                }// finally {
//                    transaction.close();
//                }
            } else {
                throw new Exception(typeName + " does not support read/write access");                
            }
            
            
        }catch(Exception e){
            throw new Exception("An error occurred while reading "+shapefileFilename,e);
        }
    }
    
    public void testMeMeng(){
    	try{
            //new GeotoolsBSConverter("C:\\Documents and Settings\\All Users\\Documents\\nkgn-scenario\\nkgn\\New Kingston.shp");
            	new GeotoolsBSConverter("J:\\HAZARD_PROJECT\\DRRC_RISK_ATLAS\\data\\Jamaica\\Caribeviz Building Inventory\\Kingston_StAnd_test_guess_EDPOP.shp");
                //new GeotoolsBSConverter("F:\\Hazard Mapping\\DRRC_RISK_ATLAS\\data\\Jamaica\\UWITT_seismic study\\kgn_bldgfootprints.shp");
                    
            }catch(Exception e){
                e.printStackTrace();
            }
    }
   
    public static void main(String args[]){
        try{
        //new GeotoolsBSConverter("C:\\Documents and Settings\\All Users\\Documents\\nkgn-scenario\\nkgn\\New Kingston.shp");
        	new GeotoolsBSConverter("J:\\HAZARD_PROJECT\\DRRC_RISK_ATLAS\\data\\Jamaica\\Caribeviz Building Inventory\\Kingston_StAnd_test_guess_EDPOP_TESTING.shp");
            //new GeotoolsBSConverter("F:\\Hazard Mapping\\DRRC_RISK_ATLAS\\data\\Jamaica\\UWITT_seismic study\\kgn_bldgfootprints.shp");
                
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
