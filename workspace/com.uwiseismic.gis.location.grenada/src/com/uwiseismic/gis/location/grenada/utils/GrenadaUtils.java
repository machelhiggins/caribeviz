package com.uwiseismic.gis.location.grenada.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import com.uwiseismic.gis.location.datastorage.RuntimeStorageMethod;
import com.uwiseismic.gis.location.grenada.City;
import com.uwiseismic.gis.location.grenada.EnumerationDistrict;



import edu.illinois.ncsa.ergo.gis.datasets.properties.DatasetProperties;
import edu.illinois.ncsa.ergo.gis.locations.Location;
import ncsa.tools.common.types.Property;

public class GrenadaUtils {
	private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(GrenadaUtils.class);

	private static List<City> cities = new LinkedList<City>();
	private static List<EnumerationDistrict> allEnumerationDistricts;
	private static Map<City, List<EnumerationDistrict>> districtsInCity = new HashMap<City, List<EnumerationDistrict>>();
	private static Map<String, EnumerationDistrict> codeToDistrict = new HashMap<String, EnumerationDistrict>();
	private static Map<String, City> codeToCity = new HashMap<String, City>();

	public static EnumerationDistrict getEnumerationDistrictFromName(String name,
			City city) {
		for (EnumerationDistrict c : getDistrictsInCity(city)) {
			if (c.getName().equals(name))
				return c;
		}
		return null;
	}

	public static EnumerationDistrict getEnumerationDistrictForCode(String code, String cityCode) {
		logger.debug("Getting district for code: " + code); //$NON-NLS-1$
		//System.err.println("Getting district for code: " + code+", "+cityCode); //$NON-NLS-1$

		EnumerationDistrict c = codeToDistrict.get(code);
		if (c != null){
			if(c.getFeatureDataset() == null){
				try {
					if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
						c = LocationUtils2
							.getEnumerationDistrictFromShapefile(code, cityCode);
					else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)
//						c = LocationUtils2
//							.getEnumerationDistrictFromPostGIS(code, cityCode);
						c = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			codeToDistrict.put(code, c);
				
			return c;
		}
		try {
			EnumerationDistrict district = null;
			if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
				district = LocationUtils2
					//.getEnumerationDistrictFromPostGIS(code, cityCode);
					.getEnumerationDistrictFromShapefile(code, cityCode);
			else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)
//				district = LocationUtils2
//				.getEnumerationDistrictFromPostGIS(code, cityCode);
				district = null;
			codeToDistrict.put(code, district);
			return district;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}


	public static List<EnumerationDistrict> getDistrictsInCity(City s) {
		logger.debug("Getting district for city: " + s.getName()); //$NON-NLS-1$
		
		List<EnumerationDistrict> c = districtsInCity.get(s);
		
		if (c != null){			
			return c;
		}
		
		try {
			List<EnumerationDistrict> districts = null;
			if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
				districts = LocationUtils2
					//.getEnumerationDistrictsFromPostGIS(s.getCityCode());
					.getEnumerationDistrictsFromCSV(s.getCityCode());
			else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)
//				districts = LocationUtils2.getEnumerationDistrictsFromPostGIS(s.getCityCode());
				districts = null;

			districtsInCity.put(s, districts);
			return districts;
		} catch (Throwable t) {
			t.printStackTrace();
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}

	public static EnumerationDistrict getDistrictFromCodeString(String code, String city) {
		logger.debug("Getting district for code: " + code); //$NON-NLS-1$

		/*if (code.length() > 2) {
			code = code.substring(0, 6);
		}*/

		EnumerationDistrict dist = codeToDistrict.get(code);
		if (dist != null)
			return dist;

		try {
			EnumerationDistrict d = null;
			if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
				d = LocationUtils2
					//.getEnumerationDistrictFromPostGIS(code, city);
					.getEnumerationDistrictFromShapefile(code, city);
			else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)
//				d = LocationUtils2.getEnumerationDistrictFromPostGIS(code, city);
				d = null;
			codeToDistrict.put(code, d);
			return d;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}

	public static City getCityFromName(String name) {
		for (City s : getCities()) {
			if (s.getName().equals(name))
				return s;
		}
		return null;
	}
	
	public static City getCityFromCodeString(String code) {

		City s = codeToCity.get(code);
		if (s != null)
			return s;

		try {
			City city = null;
			if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
				city = LocationUtils2
					//.getCityFromPostGIS(code);
					.getCityFromShapefile(code);
			else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)
//				city = LocationUtils2.getCityFromPostGIS(code);
				city = null;
			codeToCity.put(code, city);
			return city;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			t.printStackTrace();//******************* DEBUG **********************
			return null;
		}
	}

	public static List<City> getCities() {
		logger.debug("Getting cities"); //$NON-NLS-1$

		if (cities.size() != 0)
			return cities;

		try {
			if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
			cities = LocationUtils2//.getCitiesFromPostGIS();
					.getCitiesFromCSV();
			else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)
//				cities = LocationUtils2.getCitiesFromPostGIS();
				cities = null;
			return cities;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}
	
	public static Location createLocationFromProperty(Property p) {

		if (p.getName().equals(City.TAG_SELF)) {
			return new City(p);
		}
		if (p.getName().equals(EnumerationDistrict.TAG_SELF)) {
			return new EnumerationDistrict(p);
		}

		return null;
	}

	public static String getLocationStringFromDataset(DatasetProperties p) {
		List<Property> properties = p.getProperties();
		List<Property> locProperties = new LinkedList<Property>();
		for (Property property : properties) {
			if (property.getCategory() == null)
				continue;

			if (property.getCategory().equals("location")) //$NON-NLS-1$
				locProperties.add(property);
		}

		if (locProperties.size() == 0) {
			return "none"; //$NON-NLS-1$
		}

		StringBuffer buffer = new StringBuffer();

		for (Property property : locProperties) {
			if (property.getName().equals(EnumerationDistrict.TAG_SELF)) {
				EnumerationDistrict c = new EnumerationDistrict(property);
				buffer.append(c.getName() + ", "); //$NON-NLS-1$
			}
		}

		return buffer.toString();
	}
}
