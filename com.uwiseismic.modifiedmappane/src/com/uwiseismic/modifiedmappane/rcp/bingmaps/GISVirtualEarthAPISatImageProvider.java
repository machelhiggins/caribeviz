package com.uwiseismic.modifiedmappane.rcp.bingmaps;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.uwiseismic.modifiedmappane.rcp.map.JMapPaneRenderer;


/**
 * This class is responsible for connecting to Bing Maps, querying Bing Maps, and retrieving the 
 * relevant response (satellite image) in the form of a BufferedImage.
 * 
 * @author Sterling Ramroach (UWI)
 */
public class GISVirtualEarthAPISatImageProvider {

	//This is the key provided by Bing to use their Bing Maps api.
	private static String BING_MAPS_KEY = "ArMIgGVxrAc4dAaUZaNIonoErDHjoq2PqYlhsDgHFaeT9-kcBeh7bsSjYyam8vfF";

	//Strings used to build the query.
	private static String AMP    = "&";
	private static String EQUALS = "=";
	private static String COMMA  = ",";
	private final String VE_API_URL_HOST = "dev.virtualearth.net";
	private final String VE_API_URL_PATH = "/REST/v1/Imagery/Map/";

	//This variable is used to calculate the center and zoom level of the required satellite image.
	private ImageCoordinateParser imParser;



	public GISVirtualEarthAPISatImageProvider(){
		imParser = new ImageCoordinateParser();
	}


	/**
	 * This function uses the bounds of the current mapArea in the view to query Bing Maps
	 * and returns the retrieved satellite image.
	 * 
	 * @params {minX, minY, maxX, maxY} = the bounds of the mapArea displayed in the view.
	 * @return Satellite image in the form of a BufferedImage.
	 */
	public BufferedImage getExactImage(double minX, double minY, double maxX, double maxY){
		imParser.setNorth(maxY);
		imParser.setEast(minX);
		imParser.setSouth(minY);
		imParser.setWest(maxX);
		imParser.processCoordinates();

		int mapWidth  = (int)imParser.getMapWidth();
		int mapHeight = (int)imParser.getMapHeight();	
		double Lat       = imParser.getLatitude();
		double Lon       = imParser.getLongtitude();
		int zoomFactor   = imParser.getZoom();

		String requestURLFile = VE_API_URL_PATH+"Aerial/"+ Lat + COMMA + Lon +"/" + zoomFactor +"?mapSize"+EQUALS+mapWidth+COMMA+mapHeight+AMP
				+"key"+EQUALS+BING_MAPS_KEY;
		try{

			HttpURLConnection httpConn = (HttpURLConnection) new URL("http",VE_API_URL_HOST,80,requestURLFile).openConnection();
			httpConn.connect();

			InputStream in = httpConn.getInputStream();

			BufferedImage bufferedImage = ImageIO.read(in);
			httpConn.disconnect();
			return bufferedImage;
		}catch(Exception ex){
			//An exception will be thrown if the attempt to connect to Bing Maps fails.
			//This usually happens when there is no connection to the internet.
			return null;
		}	

	}

}