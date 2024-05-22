/*
 * Created on Jun 18, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.test;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.uwiseismic.ergo.analysis.efacility.gis.HealthFacilityAnalysis;

import edu.illinois.ncsa.ergo.gis.exceptions.TaskException;
import edu.illinois.ncsa.ergo.gis.util.FeatureUtils;
import ncsa.tools.ogrescript.exceptions.EnvironmentAccessException;

public class HealthFacilityAnalysisTest {

    public static void main(String args[]){
        try{


            //for(int i =0 ; i < 100; i++){
                HealthFacilityAnalysis ha = new HealthFacilityAnalysis();
                //File fn = new File("C:\\temp\\BC-MCMC-temp-data\\KMA Casualties w hospitals.shp");
                File fn = new File("C:\\Users\\Machel\\Desktop\\1_aaa\\Indoor Casualties.shp");
                DataStore store = FileDataStoreFinder.getDataStore(fn.toURI().toURL());
                String names[] = store.getTypeNames();
                FeatureSource featureSource = store.getFeatureSource(names[0]);
                FeatureCollection casualties = featureSource.getFeatures();


//                fn = new File("C:\\Users\\Machel\\OneDrive\\DRRC\\CaribEViz\\dev_share\\jamaica_data_mobile\\KMA_Portmore_road_network.shp");\
                fn = new File("C:\\temp\\BC-MCMC-temp-data\\KMA_Portmore_road_network.shp");
                DataStore storeRoads = FileDataStoreFinder.getDataStore(fn.toURI().toURL());
                String namesR[] = storeRoads.getTypeNames();
                FeatureSource fsRoads = storeRoads.getFeatureSource(namesR[0]);
                FeatureCollection roads = fsRoads.getFeatures();

                ha.setSmallFacilityWeight(0.7);
                ha.healthcareCasualitiesAnalysis(null , 0,  roads, casualties);
                
                FeatureCollection facils = ha.getHealthcareFacilties();
                System.out.println("Size of our facilities is "+facils.size());
                saveFacilities(facils);
                
                
            //}

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static void saveFacilities(FeatureCollection bldgs){
				
		String fn = "med_facil_occ_2.shp";
		System.out.println("Creating shapefile: "+fn);
		ShapefileDataStore newDataStore =  null;;
		
		// write features to file
		try {
			ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
			    
		    File file = new File(fn);
	
		    Map<String, Serializable> params = new HashMap<String, Serializable>();
	        params.put("url", file.toURI().toURL());
	        params.put("create spatial index", Boolean.TRUE);
			
			newDataStore = (ShapefileDataStore) factory.createNewDataStore(params);
			newDataStore.createSchema((SimpleFeatureType) bldgs.getSchema());
			//newDataStore.createSchema(TYPE);
			newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
		
			String typeName = newDataStore.getTypeNames()[0];
			SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
			if(!SimpleFeatureStore.class.isInstance(featureSource)) {
				throw new Exception(typeName + " does not support read/write access");
			} else {
		        SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
		        Transaction transaction = new DefaultTransaction("create");
		        featureStore.setTransaction(transaction);
		        try {
		            featureStore.addFeatures(bldgs);
		            transaction.commit();
//		            transaction.close();
		        } catch(Exception e) {
		            transaction.rollback();
		            //transaction.close();
		            e.printStackTrace();
		        } finally{
		        	transaction.close();
		        }
		    }
		} catch(Exception e) {
		    e.printStackTrace();;
		} finally {
			if(newDataStore != null)
				newDataStore.dispose();
		}
	}
    
    private static void storeClassificationResult(SimpleFeatureCollection bldgsClassified, 	String typeName) throws TaskException{
		try{			
			SimpleFeatureType originalSchema= bldgsClassified.getSchema();
			SimpleFeatureTypeBuilder builder = FeatureUtils.prepareSimpleFeatureTypeBuilder(originalSchema, typeName);
			SimpleFeatureType newSchema= builder.buildFeatureType();
			SimpleFeatureIterator iter= bldgsClassified.features();
			DefaultFeatureCollection resultBuildingClassification = new DefaultFeatureCollection();
			try {
				while (iter.hasNext()) {
					List<Object> data = new LinkedList<Object>();
					SimpleFeature f = iter.next();
					List<Object> values = f.getAttributes();
					for (Object obj: values) {
						data.add(obj);
					}
					SimpleFeature newf = SimpleFeatureBuilder.build(newSchema, data, null);
					resultBuildingClassification.add(newf);
   
				}
			} finally {
				iter.close();
			}
			
			
			//** feature.store is needed for OGRe..
			commitFeatures(resultBuildingClassification, createFeatureStore(newSchema));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static SimpleFeatureStore createFeatureStore(SimpleFeatureType featureType)
			throws IOException, MalformedURLException{		
		
		String tempFileName= "med_facil_occ.shp";
		URL shapeURL= new File(tempFileName).toURI().toURL();
		DataStore shapefileDatastore= new ShapefileDataStore(shapeURL);
		shapefileDatastore.createSchema(featureType);
		SimpleFeatureStore featureStore= (SimpleFeatureStore) shapefileDatastore.getFeatureSource(featureType.getTypeName());	
		return featureStore;
	}
	
	private static void commitFeatures(SimpleFeatureCollection featureCollection, 
			SimpleFeatureStore featureStore) throws IOException{
		DefaultTransaction transaction = new DefaultTransaction();
		try {
			featureStore.setTransaction(transaction);
			featureStore.addFeatures(featureCollection);
			transaction.commit();
			featureStore.setTransaction(Transaction.AUTO_COMMIT);
		} finally {
			//featureCollection.clear();
			transaction.close();
		}
	}

}
