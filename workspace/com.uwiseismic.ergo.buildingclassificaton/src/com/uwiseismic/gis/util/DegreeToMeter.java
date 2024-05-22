package com.uwiseismic.gis.util;

public class DegreeToMeter {

    private static double m1 = 111132.92;     // latitude calculation term 1
    private static double m2 = -559.82;       // latitude calculation term 2
    private static double m3 = 1.175;         // latitude calculation term 3
    private static double m4 = -0.0023;       // latitude calculation term 4
    //private static double p1 = 111412.84;     // longitude calculation term 1
    //private static double p2 = -93.5;         // longitude calculation term 2
    //private static double p3 = 0.118;         // longitude calculation term 3

	/**
	 * Will return the length in meters (along the latitude) of the degrees specified
	 * 
	 * @param latitude
	 * @param degrees
	 * @return
	 */
	public static double degreeToMeter(double latitude, double degrees){
		//** stole this from http://gis.stackexchange.com/questions/75528/length-of-a-degree-where-do-the-terms-in-this-formula-come-from
	    // Calculate the length of a degree of latitude and longitude in meters
	    double latlen = m1 + (m2 * Math.cos(2 * latitude)) + (m3 * Math.cos(4 * latitude)) +
	            (m4 * Math.cos(6 * latitude));
	    //** will only use latitude
	    /*longlen = (p1 * Math.cos(lat)) + (p2 * Math.cos(3 * lat)) +
	                (p3 * Math.cos(5 * lat));
	                */
	    return latlen * degrees;
	}
	
	public static void main(String args[]){
		System.out.println(DegreeToMeter.degreeToMeter(15.3, 1.7976931348623157E308));
	}
}
