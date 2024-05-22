package com.uwiseismic.modifiedmappane.mapawareDEFUNCT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileWriter;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.map.Layer;
import org.geotools.map.StyleLayer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;


public class DatasetSave extends Thread {

	private boolean close = false;
	private String dbfPath;
	private SimpleFeatureType featureType;
	private Vector<String> guidsToSave = new Vector<String>();
	private HashMap<String, SimpleFeature> featsToSave = new HashMap<String, SimpleFeature>();
	
	
	private final int SAVE_INTERVAL = 20*1000;//** save every 20 seconds
	private final String GUID = "guid";
	
	public void run(){
		while(!close){
			if(guidsToSave.size() > 0){				
				Job j = new Job("Saving edits") { //$NON-NLS-1$
					protected IStatus run(IProgressMonitor monitor){
						try {
							saveDBF();
						} catch (IOException e) {
							e.printStackTrace();
						}	
						return Status.OK_STATUS;
					}
				};
				j.setUser(true);
				j.schedule();		
				try{ Thread.sleep(SAVE_INTERVAL);} catch(Exception e){e.printStackTrace();}											
			}
			else
				try{ Thread.sleep(1000);} catch(Exception e){e.printStackTrace();}
		}
	}
	
	public void saveDBF() throws IOException{
		
		File dbfFile = new File(dbfPath);
		File newDbf = new File(dbfFile.getParentFile().getAbsolutePath()+"/new_"+dbfFile.getName());
		File backupDbf = new File(dbfFile.getParentFile().getAbsolutePath()+"/backup_"+dbfFile.getName());
		
		FileInputStream fis = new FileInputStream(dbfFile);
		DbaseFileReader dbfReader =  new DbaseFileReader(fis.getChannel(),
				false,  Charset.forName("ISO-8859-1"));
		DbaseFileHeader header = dbfReader.getHeader();
		
		
		WritableByteChannel out = new FileOutputStream(newDbf).getChannel();
		DbaseFileWriter writer = new DbaseFileWriter(header,out);
		
		//** find guid column
		int guidCol = -1;
		for(int i = 0; i < header.getNumFields();i++){
			if(header.getFieldName(i).matches(GUID)){
				guidCol = i;
				break;
			}
		}
		int removeToSave;
		int n;
		while ( dbfReader.hasNext() ){
		   Object[] fields = dbfReader.readEntry();
		   removeToSave = -1;
		   n = 0;
		   String guidField = (String) fields[guidCol];		   		   
		   //** search through guids for a match		   
		   for(String comp : guidsToSave){
			   if(guidField.equals(comp)){
				   List attribs = featsToSave.get(comp).getAttributes();
				   Object newAttrVals[] = new Object[fields.length];
				   for(int z = 1; z < attribs.size();z++) //** copy fields except at index 0, thats the geometry
					   newAttrVals[z-1] = attribs.get(z);				   
				   System.out.println("Saving ");
				   removeToSave = n;
				   n++;
				   writer.write(newAttrVals);
				   break;
			   }
		   }
		   if(removeToSave >= 0)
			   featsToSave.remove(guidsToSave.remove(removeToSave));
		   else
			   writer.write(fields);		   
		}
		
		dbfReader.close();
		fis.close();
		writer.close();
		
		//** back up old dbf file and copy over new one
		if(backupDbf.exists()) {
			System.err.println("Back up exists");
			if(!backupDbf.delete());
				System.err.println("Could not delete the backup");
		}
		if(!dbfFile.renameTo(backupDbf)){			
			System.err.println("Could not back up dbf file");			
		}else if(!newDbf.renameTo( new File(dbfPath))){
			System.err.println("Could not copy over new dbf file");
		}
	}
	
	public void setDBFPath(String dbfPath){
		this.dbfPath = dbfPath;
		featureType = null;
	}
	
	public void setFeaturesToSave(ArrayList <SimpleFeature> feats){
		String guid;
		for(SimpleFeature f : feats){
			if(featureType == null){
				featureType = f.getFeatureType();
			}
			guid = (String) f.getAttribute(GUID);
			if(guid != null){
				guidsToSave.add(guid);
				featsToSave.put(guid, f);
			}
			
		}
	}
	
	public void close(){
		close = true;
	}
	
}
