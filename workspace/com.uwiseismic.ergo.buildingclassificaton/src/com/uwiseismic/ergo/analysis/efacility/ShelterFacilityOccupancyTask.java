package com.uwiseismic.ergo.analysis.efacility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;

import com.uwiseismic.ergo.analysis.efacility.gis.ShelterFacilityAnalysis;

import edu.illinois.ncsa.ergo.core.analysis.elf.tasks.AnalysisBaseTask;
import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;
import edu.illinois.ncsa.ergo.gis.exceptions.TaskException;
import edu.illinois.ncsa.ergo.gis.util.FeatureUtils;
import ncsa.tools.elf.core.exceptions.ScriptExecutionException;
import ncsa.tools.ogrescript.exceptions.EnvironmentAccessException;

public class ShelterFacilityOccupancyTask extends AnalysisBaseTask {
	
	//**output
	private SimpleFeatureType resultType;
	private DefaultFeatureCollection shelterFacilitiesOccupancy;
	//**input
	private FeatureDataset popDislocated;
	private FeatureDataset osmRoadNetwork;
	//private double smallFacilityWeight;

	protected void wrappedExecute(IProgressMonitor monitor) throws ScriptExecutionException {
        ShelterFacilityAnalysis sa = new ShelterFacilityAnalysis();        
        try {
        	sa.shelterOccupancyAnalysis(monitor , 80, osmRoadNetwork.getAllFeatures(), popDislocated.getAllFeatures());
		} catch (IOException e) {
			// TODO proper error logging
			e.printStackTrace();
		}
        shelterFacilitiesOccupancy = (DefaultFeatureCollection)sa.getEmergencyShelterFacilties();
        try {
			storeResults(monitor);
		} catch (TaskException e) {			
			e.printStackTrace();//// TODO proper error logging
		}
	}

	private void storeResults(IProgressMonitor monitor) throws TaskException{
		try {
			
			SimpleFeatureCollection fc = shelterFacilitiesOccupancy.collection();
			SimpleFeatureType originalSchema= fc.getSchema();
			
			String typeName= getResultType().getTypeName();
			SimpleFeatureTypeBuilder builder = FeatureUtils.prepareSimpleFeatureTypeBuilder(originalSchema, typeName);
			SimpleFeatureType newSchema = builder.buildFeatureType();
			
			//** feature.store is needed for OGRe..
			commitFeatures(shelterFacilitiesOccupancy, createFeatureStore(newSchema, "feature.store.shelterFacilitiesOccupancy"));
			
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
	
	private void commitFeatures(DefaultFeatureCollection featureCollection, 
			SimpleFeatureStore featureStore) throws IOException{
		DefaultTransaction transaction = new DefaultTransaction();
		try {
			featureStore.setTransaction(transaction);
			featureStore.addFeatures(featureCollection);
			transaction.commit();
			featureStore.setTransaction(Transaction.AUTO_COMMIT);
		} finally {
			featureCollection.clear();
			transaction.close();
		}
	}

	public SimpleFeatureType getResultType() {
		return resultType;
	}



	public void setResultType(SimpleFeatureType resultType) {
		this.resultType = resultType;
	}

	public FeatureDataset getPopDislocated() {
		return popDislocated;
	}

	public void setPopDislocated(FeatureDataset popDislocated) {
		this.popDislocated = popDislocated;
	}

	public FeatureDataset getOsmRoadNetwork() {
		return osmRoadNetwork;
	}

	public void setOsmRoadNetwork(FeatureDataset osmRoadNetwork) {
		this.osmRoadNetwork = osmRoadNetwork;
	}

	public DefaultFeatureCollection getShelterFacilitiesOccupancy() {
		return shelterFacilitiesOccupancy;
	}

	public void setShelterFacilitiesOccupancy(DefaultFeatureCollection shelterFacilitiesOccupancy) {
		this.shelterFacilitiesOccupancy = shelterFacilitiesOccupancy;
	}

	

}
