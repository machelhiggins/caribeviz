package com.uwiseismic.modifiedmappane.rcp.bingmaps;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

/**
 * This class uses the longtitude/latitude bounds of the mapArea to calculate the 
 * center point and relevant zoom level with which to query the Bing Maps API.
 */
public class ImageCoordinateParser {


	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private double maxImageWidth = screenSize.width;
	private double maxImageHeight = screenSize.height;

	//Taken from Bing Maps API documentation
	private double MinLatitude = -85.05112878;
	private double MaxLatitude = 85.05112878;
	private double MinLongitude = -180;
	private double MaxLongitude = 180;

	private double north, south, east, west;
	private double mapWidth, mapHeight, latitude, longtitude;
	private int zoom;
	
	public ImageCoordinateParser(){}
	
	
	//This is where the calculation for center point and zoom level is done.
	public void processCoordinates(){

		latitude = (north + south) / 2;
		longtitude = (east + west) / 2;

		double zoom1 = 0, zoom2 = 0;

		//Determine the best zoom level based on the map scale and bounding coordinate information
		if (north != south && east != west)
		{
			//best zoom level based on map width
			zoom1 = Math.log(360.0 / 256.0 * (maxImageWidth - 2) / (east - west)) / Math.log(2);
			//best zoom level based on map height
			zoom2 = Math.log(180.0 / 256.0 * (maxImageHeight - 2) / (north - south)) / Math.log(2);
		}

		//use the most zoomed out of the two zoom levels
		zoom = (int)Math.floor((zoom1 < zoom2) ? zoom1 : zoom2);

		//Calculate pixel coordinates of the corners of the bounding box.
		Point topLeftCorner = LatLongToPixelXY(north, west, zoom);
		Point bottomRightCorner = LatLongToPixelXY(south, east, zoom);

		//Using the pixel coordinates, calculate the map width and height that fits tight around the bounding box.
		mapWidth = Math.abs(bottomRightCorner.getX() - topLeftCorner.getX());
		mapHeight = Math.abs(bottomRightCorner.getY() - topLeftCorner.getY());
	}



	public double getMapWidth() {
		return mapWidth;
	}

	public double getMapHeight() {
		return mapHeight;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public int getZoom() {
		return zoom;
	}

	public void setNorth(double north) {
		this.north = north;
	}

	public void setSouth(double south) {
		this.south = south;
	}

	public void setEast(double east) {
		this.east = east;
	}

	public void setWest(double west) {
		this.west = west;
	}

	/// <summary>
	/// Converts a point from latitude/longitude WGS-84 coordinates (in degrees)
	/// into pixel XY coordinates at a specified level of detail.
	/// </summary>
	/// <param name="latitude">Latitude of the point, in degrees.</param>
	/// <param name="longitude">Longitude of the point, in degrees.</param>
	/// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
	/// to 23 (highest detail).</param>
	/// <param name="pixelX">Output parameter receiving the X coordinate in pixels.</param>
	/// <param name="pixelY">Output parameter receiving the Y coordinate in pixels.</param>
	private Point LatLongToPixelXY(double latitude, double longitude, int levelOfDetail)
	{
		latitude = Clip(latitude, MinLatitude, MaxLatitude);
		longitude = Clip(longitude, MinLongitude, MaxLongitude);

		double x = (longitude + 180) / 360;
		double sinLatitude = Math.sin(latitude * Math.PI / 180);
		double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

		int mapSize = MapSize(levelOfDetail);
		int pixelX = (int)Clip(x * mapSize + 0.5, 0, mapSize - 1);
		int pixelY = (int)Clip(y * mapSize + 0.5, 0, mapSize - 1);
		//Point p = new Point();

		return new Point(pixelX, pixelY);
	}

	/// <summary>
	/// Clips a number to the specified minimum and maximum values.
	/// </summary>
	/// <param name="n">The number to clip.</param>
	/// <param name="minValue">Minimum allowable value.</param>
	/// <param name="maxValue">Maximum allowable value.</param>
	/// <returns>The clipped value.</returns>
	private double Clip(double n, double minValue, double maxValue)
	{
		return Math.min(Math.max(n, minValue), maxValue);
	}

	/// <summary>
	/// Determines the map width and height (in pixels) at a specified level
	/// of detail.
	/// </summary>
	/// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
	/// to 23 (highest detail).</param>
	/// <returns>The map width and height in pixels.</returns>
	private int MapSize(int levelOfDetail)
	{
		return (int)256 << levelOfDetail;
	}
}
