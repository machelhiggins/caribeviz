package com.uwiseismic.ergo.buildingclassifier;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.DocumentException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.uwiseismic.ergo.buildingclassifier.logictree.IllegalLogicTreeOperationException;
import com.uwiseismic.ergo.buildingclassifier.logistic.IllegalLogisticFuncParameterException;
import com.uwiseismic.ergo.buildingclassifier.ratiofunc.IllegalRatioFuncParameterException;
import com.uwiseismic.ergo.datasets.XMLDataset;
import com.uwiseismic.ergo.occupancy.Occupancy;
import com.uwiseismic.gis.util.geohash.GeoHash;

import edu.illinois.ncsa.ergo.core.analysis.elf.tasks.AnalysisBaseTask;
import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;
import edu.illinois.ncsa.ergo.gis.exceptions.TaskException;
import edu.illinois.ncsa.ergo.gis.util.FeatureUtils;
import ncsa.tools.elf.core.exceptions.ScriptExecutionException;
import ncsa.tools.ogrescript.exceptions.EnvironmentAccessException;

/**
 * @author Machel
 *
 */
public class BuildingClassificationTask extends AnalysisBaseTask {
	
	//**output
	private SimpleFeatureType resultType;
//	private DefaultFeatureCollection bldgClassificationResult;
	private DefaultFeatureCollection resultBuildingClassification;
	//**input
	//private FeatureDataset uwiBuildingInventory;
	private FeatureDataset uwiBuildingClassification;
	private FeatureDataset osmRoadNetwork;
	private FeatureDataset uwiEnumerationDistrict;
	private XMLDataset uwiBuildingClassificationParams;
	private String resultName;	
	 ;


	protected void wrappedExecute(IProgressMonitor monitor) throws ScriptExecutionException {
		
		BuildingProbParameters probParams = null;
		try {
			probParams = BuildingProbParameterBuilder.create(uwiBuildingClassificationParams.getXMLDocument());
		} catch (NullPointerException e1) {
			e1.printStackTrace();//TODO proper error logging
		} catch (IOException e1) {
			e1.printStackTrace();//TODO proper error logging
		} catch (IllegalLogicTreeOperationException e1) {
			e1.printStackTrace();//TODO proper error logging
		} catch (IllegalLogisticFuncParameterException e1) {
			e1.printStackTrace();//TODO proper error logging
		} catch (DocumentException e1) {
			e1.printStackTrace();//TODO proper error logging
		} catch (IllegalRatioFuncParameterException e1) {
			e1.printStackTrace();//TODO proper error logging
		}
		BCIngester bci = new BCIngester();			
		try {
			bci.process(uwiEnumerationDistrict.getAllFeatures(), uwiBuildingClassification.getAllFeatures(), 
					osmRoadNetwork.getAllFeatures(), probParams, monitor, 40);
			if(monitor != null)
				monitor.worked(20);
		} catch (IOException e1) {
			e1.printStackTrace();//TODO proper error logging
		} catch (Exception e1) {
			e1.printStackTrace();//TODO proper error logging
		}
		
		GeoHash buildingsGuess = bci.getBuildings();
		//** RANDOM amount of iterations for BCMC but usually don't 
		//** need more than this to converge. Will consider MCMC in future.
		//** The hardcoded structure classification HAS to be reworked to accommodate
		//** LARGE and SMALL island states. The prevalence and distribtion of wood 
		//** structures are different for a planned city of kingston than that of BDOS
		//** or Dominica.
        BCMC mc = new BCMC(buildingsGuess, 300); 				
		if(monitor != null)
			monitor.worked(30);
        
        try {
			mc.performMC(monitor, 40);
		} catch (StructureNotInEdException e1) {
			e1.printStackTrace();//TODO proper error logging
		} catch (NoProbabilityFunctionException e1) {
			e1.printStackTrace();//TODO proper error logging
		}
 
      //** get occupancy and occupany rates;
        Occupancy occ = new Occupancy();
        BuildingClassification bcs[] = mc.getAllBuildings();
        for(int i = 0; i < bcs.length; i++){
        	occ.determineOccupancyType(bcs[i]);
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

        
		try {
			storeClassificationResult(monitor, bldgsClassified);
		} catch (TaskException e) {
			e.printStackTrace();//TODO proper error logging
		}
	}


	private void storeClassificationResult(IProgressMonitor monitor, SimpleFeatureCollection bldgsClassified) throws TaskException{
		try{
			
			SimpleFeatureType originalSchema= bldgsClassified.getSchema();
			String typeName= getResultType().getTypeName();
			SimpleFeatureTypeBuilder builder = FeatureUtils.prepareSimpleFeatureTypeBuilder(originalSchema, typeName);
			SimpleFeatureType newSchema= builder.buildFeatureType();
			SimpleFeatureIterator iter= bldgsClassified.features();
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
			commitFeatures(resultBuildingClassification, createFeatureStore(newSchema, "feature.store.buildingClassification"));
			
		} catch (IOException e) {
			error("IO problem", e);
		} catch (EnvironmentAccessException e) {
			error("Ogre Script Problem", e);
		}
	}
	
	
	private SimpleFeatureStore createFeatureStore(SimpleFeatureType featureType, String featureStoreKey)
			throws IOException, MalformedURLException,EnvironmentAccessException{
		
		Path tempDir= Files.createTempDirectory("temp");
		String tempFileName= tempDir.toString() + "/" + featureType.getTypeName() + ".shp";
		URL shapeURL= new File(tempFileName).toURI().toURL();
		DataStore shapefileDatastore= new ShapefileDataStore(shapeURL);
		shapefileDatastore.createSchema(featureType);
		SimpleFeatureStore featureStore= (SimpleFeatureStore) shapefileDatastore.getFeatureSource(featureType.getTypeName());
		environment.addOrSetEntry(featureStoreKey, featureStore, false);
		return featureStore;
	}
	
	private void commitFeatures(SimpleFeatureCollection featureCollection, 
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



	public SimpleFeatureType getResultType() {		
		return resultType;
	}


	public void setResultType(SimpleFeatureType resultType) {
		this.resultType = resultType;
	}


	public DefaultFeatureCollection getResultBuildingClassification() {
		return resultBuildingClassification;
	}



	public void setResultBuildingClassification(DefaultFeatureCollection resultBuildingClassification) {
		this.resultBuildingClassification = resultBuildingClassification;
	}




	public FeatureDataset getOsmRoadNetwork() {
		return osmRoadNetwork;
	}



	public FeatureDataset getUwiBuildingClassification() {
		return uwiBuildingClassification;
	}



	public void setUwiBuildingClassification(FeatureDataset uwiBuildingClassification) {
		this.uwiBuildingClassification = uwiBuildingClassification;
	}



	public void setOsmRoadNetwork(FeatureDataset osmRoadNetwork) {
		this.osmRoadNetwork = osmRoadNetwork;
	}



	public FeatureDataset getUwiEnumerationDistrict() {
		return uwiEnumerationDistrict;
	}



	public void setUwiEnumerationDistrict(FeatureDataset uwiEnumerationDistrict) {
		this.uwiEnumerationDistrict = uwiEnumerationDistrict;
	}



	public XMLDataset getUwiBuildingClassificationParams() {
		return uwiBuildingClassificationParams;
	}



	public void setUwiBuildingClassificationParams(XMLDataset uwiBuildingClassificationParams) {
		this.uwiBuildingClassificationParams = uwiBuildingClassificationParams;
	}



	public String getResultName() {
		return resultName;
	}



	public void setResultName(String resultName) {
		System.err.println("Setting result name "+resultName);
		this.resultName = resultName;
	}
	
//	private void generateBldgResult() throws TaskException{
//	try {
//
//	SimpleFeatureType originalSchema= bldgsClassified.getSchema();
//	String typeName= getResultType().getTypeName();
//	SimpleFeatureTypeBuilder builder = FeatureUtils.prepareSimpleFeatureTypeBuilder(originalSchema, typeName);
//	SimpleFeatureType newSchema= builder.buildFeatureType();
//	SimpleFeatureIterator iter= bldgsClassified.features();
//	try {
//		while (iter.hasNext()) {
//			List<Object> data = new LinkedList<Object>();
//			SimpleFeature f = iter.next();
//			List<Object> values = f.getAttributes();
//			for (Object obj: values) {
//				data.add(obj);
//			}
//			SimpleFeature newf = SimpleFeatureBuilder.build(newSchema, data, null);
//			//resultBuildingClassification.add(newf);
//	        if(!resultBuildingClassification.add(newf))
//	        	System.err.println("It didn't add feature :(");
//	        		        
//		}
//	} finally {
//		iter.close();
//	}
//	
//	try {//** DEBUG
//		System.out.println("Size of resultBuildingClassification : "+resultBuildingClassification.getCount());//** DEBUG
//	} catch (IOException e1) {//** DEBUG
//		e1.printStackTrace();//** DEBUG
//	}//** DEBUG
//	
//	commitFeatures(resultBuildingClassification, createFeatureStore(newSchema, "feature.store.bldgresult"));
//	} catch (IOException e) {
//		error("IO problem", e);
//	} catch (EnvironmentAccessException e) {
//		error("Ogre Script Problem", e);
//	}
//}

}
