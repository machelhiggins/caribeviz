package com.uwiseismic.testing;

public class SimplePoint implements Comparable<SimplePoint>{
    public double lat = Double.NaN;
    public double lon = Double.NaN;
    public int id = -1;
    private final static double PRECISION = 1e-6;
    
    public SimplePoint(double lat, double lon){
    	this.lat = lat;
    	this.lon = lon;
    	id = GenerateNewNumericID.getNewID(8);
    }
    
    public SimplePoint(int id, double lat, double lon){
    	this.id = id;
    	this.lat = lat;
    	this.lon = lon;
    }

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
    
    public boolean isValid() {
        return !Double.isNaN(lat) && !Double.isNaN(lon);
    }

   
    public int getID() {
		return id;
	}

	public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        return hash;
    }

   
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        SimplePoint other = (SimplePoint)obj;
        return Math.abs(lat - other.lat) < PRECISION && Math.abs(lon - other.lon) < PRECISION;
    }
    
    public String toString() {
        return lat + "," + lon;
    }

	@Override
	public int compareTo(SimplePoint other) {
		if(equals(other))
			return 0;
		return (int)((lat - other.lat) + (lon - other.lon));
	}
}
