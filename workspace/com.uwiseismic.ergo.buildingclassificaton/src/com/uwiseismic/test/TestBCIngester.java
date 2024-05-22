package com.uwiseismic.test;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapPane;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.uwiseismic.ergo.buildingclassifier.BCIngester;
import com.uwiseismic.ergo.buildingclassifier.BCMC;
import com.uwiseismic.ergo.buildingclassifier.BuildingClassification;
import com.uwiseismic.ergo.buildingclassifier.BuildingProbParameterBuilder;
import com.uwiseismic.ergo.buildingclassifier.BuildingProbParameters;
import com.uwiseismic.ergo.buildingclassifier.EDStructTypeRankingFunction;
import com.uwiseismic.ergo.gis.PolygonStyleTwo;
import com.uwiseismic.ergo.occupancy.Occupancy;
import com.uwiseismic.gis.util.geohash.GeoHash;

public class TestBCIngester {

	public static void main(String[] args) {
	
        try{
            //File fn = new File("C:/temp/nkgn/New_Kingston_WGS84/New_Kingston.shp");
        	//File fn = new File("C:\\temp\\BC-MCMC-temp-data\\Kingston Metropolitan Area Building Inventory Footprints UWI version v4.1.shp");
//        	File fn = new File("C:\\temp\\BC-MCMC-temp-data\\Test for shelters v2.shp");
//        	File fn = new File("C:\\Users\\Machel\\OneDrive\\DRRC\\CaribEViz\\dev_share\\jamaica_data_mobile\\Portmore_Building_Footprint_Ergo_attribs.shp");
//        	File fn = new File("C:\\Users\\Machel\\Desktop\\1_aaa\\KMA BC Test w Fake Shelters v2.2.shp");
//        	File fn = new File("C:\\temp\\portmore\\Portmore Building Classification 5.2.shp");
        	File fn = new File("C:\\Projects\\JAVA\\DRRCDataWarehouse\\WebContent\\ergo-repo\\datasets\\com.uwiseismic.ergo.buildingclassificaton.uwiBuildingClassification.v1.0\\Portmore_Building_Classifiation\\converted\\Portmore_Building_Classifiation.shp");
        	
        	
        	
        	//File fn = new File("C:\\temp\\nkgn\\one_small_building\\small.shp");
        	//File fn = new File("C:\\temp\\nkgn\\one_small_building\\big.shp");
        	
            DataStore store = FileDataStoreFinder.getDataStore(fn.toURI().toURL());
            String names[] = store.getTypeNames();
            FeatureSource featureSource = store.getFeatureSource(names[0]);           
            FeatureCollection buildings = featureSource.getFeatures();
            CoordinateReferenceSystem csr = featureSource.getSchema().getCoordinateReferenceSystem();
            
            
            fn = new File("C:\\Projects\\JAVA\\DRRCDataWarehouse\\WebContent\\ergo-repo\\datasets\\com.uwiseismic.ergo.buildingclassificaton.osmRoadNetwork.v1.0\\KMA_and_Portmore_OSM_Road_Network1459213853755\\converted\\KMA_Portmore_road_network.shp");
            DataStore storeRoads = FileDataStoreFinder.getDataStore(fn.toURI().toURL());
            String namesR[] = storeRoads.getTypeNames();
            FeatureSource fsRoads = storeRoads.getFeatureSource(namesR[0]);           
            FeatureCollection roads = fsRoads.getFeatures();  

//            fn = new File("C:\\Users\\Machel\\OneDrive\\DRRC\\CaribEViz\\dev_share\\jamaica_data_mobile\\KMA_ED_unimproved_land_per_sqft_values9.shp");
            fn = new File("C:\\Projects\\JAVA\\DRRCDataWarehouse\\WebContent\\ergo-repo\\datasets\\com.uwiseismic.ergo.buildingclassificaton.uwiEnumerationDistricts.v1.0\\Portmore_Enumeration_District1463084989154\\converted\\Portmore_EDs_WGS84_w_Pop_UIL.shp");
            DataStore storeEDs = FileDataStoreFinder.getDataStore(fn.toURI().toURL());
            String namesED[] = storeEDs.getTypeNames();
            FeatureSource fsEDs = storeEDs.getFeatureSource(namesED[0]);           
            FeatureCollection eds = fsEDs.getFeatures(); 
            BCIngester bci = new BCIngester();
            
//            File xmlFile = new File("probabilities/probability_thresholds.xml");
//            File xmlFile = new File("C:\\temp\\portmore\\Portmore Building Classification Parameters no wood.xml");
//            File xmlFile = new File("C:\\temp\\portmore\\portmore up in here.xml");            
            File xmlFile = new File("C:\\Users\\Machel\\Desktop\\1_aaa\\Portmore Buildiing Classification 20160707.xml");
            BuildingProbParameters probParams = BuildingProbParameterBuilder.create(xmlFile);
            bci.process(eds, buildings, roads, probParams, null,0);
            
            GeoHash buildingsGuess = bci.getBuildings();
            
            
            //BCMC mc = new BCMC(buildingsGuess, 500);
            BCMC mc = new BCMC(buildingsGuess, 250);
            mc.performMC(null,0);
                             
            //** get occupancy and occupany rates;
            Occupancy occ = new Occupancy();
            BuildingClassification bcs[] = mc.getAllBuildings();
            for(int i = 0; i < bcs.length; i++){
            	occ.determineOccupancyType(bcs[i]);
            	SimpleFeature feat = bcs[i].getFeature();//** DEBUG			
            }
            
            for(Iterator i = bci.getEdRankings().getHashables().iterator(); i.hasNext();){
            	EDStructTypeRankingFunction ed = (EDStructTypeRankingFunction)i.next();
            	occ.determineOccupancyRates(ed);
            }
            
            
    		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
    		SimpleFeature f;
    		SimpleFeatureType featureType = null;
            for(Iterator <BuildingClassification> i = buildingsGuess.getHashables().iterator(); i.hasNext();){
            	f = i.next().getFeature();
            	if(featureType == null)
            		featureType = f.getFeatureType();
            	features.add(f);            	
            }
            SimpleFeatureCollection bldgsClassified = new ListFeatureCollection(featureType, features);;
            bldgsClassified.getBounds();                                    
            
            readAndDisplay(roads, eds, bldgsClassified,  buildings.getBounds());
            System.out.println("RM1: BLUE\t"
				+"RM2: RED\t"
				+"C1: YELLOW\t"
				+"C2: CYAN\t"
				+"PC1: WHITE\t"
				+"W1: PINK\t"
				+"S1: BLACK\t"
				+"UNKOWN: GREY");
            saveDebugOutput(bcs);
            saveclassification(bldgsClassified);
	    }catch(Exception ex){
	        ex.printStackTrace();
	    }    
	}
	
	public static void saveDebugOutput(BuildingClassification bcs[]) throws IOException{
		
		//** save classifications to text file
		BufferedWriter writer = new BufferedWriter(new FileWriter("debug_bmcmc_output/last_bcmcmc_output.txt"));
		
		java.text.DecimalFormat formatter = new java.text.DecimalFormat("0.000#");
		double structProbs[];
		int structFreq[];
		
		for(int i = 0; i < bcs.length; i++){
			structFreq = bcs[i].getLikelyStructureMemory();
			structProbs = bcs[i].getAllStructuresProbabilities();
			writer.write(bcs[i].getBldgID()+"\t"+bcs[i].getMostLikelyStructureString());

			try{
					writer.write("("+formatter.format(+bcs[i].getStructuresProbability(
							bcs[i].getMostLikelyStructure()))+")  >>\t");
				}catch(java.lang.IllegalArgumentException iax){
					iax.printStackTrace();
				}
				
			for(int n = 0; n < structFreq.length; n++)
				writer.write(structFreq[n]+"\t");
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}
	
	public static void saveclassification(FeatureCollection bldgs){		
		
		int n = 0;		
		String listing[] = new File("debug_bmcmc_output/").list();		
		if(listing.length > 0 ){
			Arrays.sort(listing);
//			for(int z = 0; z < listing.length; z++)
//				System.out.println(listing[z]);
			int length = listing.length;
			if(listing[length -1].matches("last_bcmcmc_output.txt")){
				length--;				
			}
			if(length > 0){
				System.err.println("NEW n: "+listing[length -1].
						substring(listing[length -1].lastIndexOf('_')+1, listing[length -1].length()-4));
				n = Integer.parseInt(listing[length -1].
						substring(listing[length -1].lastIndexOf('_')+1, listing[length -1].length()-4));
				n++;
			}
		}		
		String fn = "debug_bmcmc_output/bmcmc_output_"+n+".shp";
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
	
	public static JPanel readAndDisplay(FeatureCollection roads, FeatureCollection eds, 
			FeatureCollection bldgs, ReferencedEnvelope env)throws Exception{
        try{
        	       
        	
            //** get envelope bounds
            //Envelope bounds = featureSource.getBounds();            
  
            //** lets just display frame for now with code straight from geotools.org tutorial
            
            //MapContext map = new DefaultMapContext(eds.getSchema().getCoordinateReferenceSystem());
            //map.setTitle("New Kingston \\ Kingston Classification");        	
        	MapContent map = new MapContent();
  
            //map.addLayer(eds, PolygonStyle.getStyle(Color.GRAY, Color.WHITE, 1, 0.0));
           // map.addLayer(eds, LineStyle.getStyle(Color.GRAY, 1, 0.0));
            
            //map.addLayer(roads, LineStyle.getStyle(Color.GRAY, 1, 0.0));
                    
        	//FeatureLayer layer = new FeatureLayer(bldgs,PolygonStyle.getStyle(Color.BLACK, Color.WHITE, 1.5, 1.0));
        	FeatureLayer layer = new FeatureLayer(bldgs,PolygonStyleTwo.getStructureTypeStyle());
            map.addLayer(layer);                                                
            //map.setCoordinateReferenceSystem(csr);          
            //map.setAreaOfInterest(env);
            //map.getLayerBounds();                               
            
            JMapPane mapPane = new JMapPane(map);  
            mapPane.setRenderer(new StreamingRenderer());
            
            JFrame mainFrame = new JFrame("Classification Test");
            mainFrame.setSize(new Dimension(3036, 2055));
            JPanel dummy = new JPanel(){
                public Dimension getPreferredSize(){
                    return new Dimension(3000, 2000);
                }
            };
            
            mapPane.setPreferredSize(dummy.getPreferredSize());
            
            dummy.setLayout(new FlowLayout());
            dummy.add(mapPane);
            dummy.validate();
            mainFrame.getContentPane().add(dummy);
            mainFrame.validate();
            mainFrame.setVisible(true);
            
            return mapPane;
            
        }catch(Exception e){
            throw e;
        }
    }

}
