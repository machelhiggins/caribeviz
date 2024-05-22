package com.uwiseismic.gis.location.datastorage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class RuntimeStorageMethod {
	
	public final static int POSTGIS = 1;
	public final static int SHAPEFILE = 0;
	private static int instanceStor = -1;
	
	
	public static int STORAGE_TYPE(){
		if(instanceStor == -1){
			Bundle bundle = Platform.getBundle( Activator.PLUGIN_ID );
	        URL resourceURL = bundle.getResource( "props/storage_type" ); //$NON-NLS-1$
	        InputStream source;
	        try {
	            source = resourceURL.openStream();
	            BufferedReader bufRdr = new BufferedReader( new InputStreamReader( source ) );
	            String line = null;

	            line = bufRdr.readLine();
	            //          read each line of text file
	            while ( (line = bufRdr.readLine()) != null ) {
	            	if(line.trim().length() == 0)	            	
	            		continue;
	            	if(line.matches("^#.*"))
	            		continue;
	            	if(line.matches("postgis"))
	            		instanceStor = POSTGIS;
	            	if(line.matches("shapefile"))
	            		instanceStor = SHAPEFILE;
	            }
	        }catch(Exception io){
	        	io.printStackTrace();
	        }
		}
		
		return instanceStor;
	}

}
