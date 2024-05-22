package com.uwiseismic.modifiedmappane.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.uwiseismic.modifiedmappane.mapawareDEFUNCT.DatasetSave;

public class Scratch {

	public static void main(String[] args) {
		String dbfPath = "C:\\temp\\DELETEME\\Dataset_saver_test\\Shelter_occ.dbf";
		String shapePath = "C:\\temp\\DELETEME\\Dataset_saver_test\\Shelter_occ.shp";
		Random rng = new Random(System.currentTimeMillis());
		File shapefn = new File(shapePath);
		DataStore storeRoads;
		DatasetSave ds = new DatasetSave();
		ds.setDBFPath(dbfPath);
		ds.start();
		ArrayList<SimpleFeature> toSave = new ArrayList<SimpleFeature>();
		try {
			DataStore filestore = FileDataStoreFinder.getDataStore(shapefn.toURI().toURL());
			String namesR[] = filestore.getTypeNames();
			FeatureSource fs = filestore.getFeatureSource(namesR[0]);           
			FeatureCollection fc = fs.getFeatures();
			
			//** randomly edit on of these
			FeatureIterator i = fc.features();
			while(i.hasNext()){
				SimpleFeature f = (SimpleFeature) i.next();
				if(rng.nextBoolean()){
					System.out.println("NANABANANA");
					f.setAttribute("efacil_nam", "NANABANANA");
					toSave.add(f);
				}
			}
			i.close();
			System.err.println("Disposing... ");
			filestore.dispose();
			ds.setFeaturesToSave(toSave);
			try{ Thread.sleep(5000);} catch(Exception e){e.printStackTrace();}
			ds.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


			
	}

}
