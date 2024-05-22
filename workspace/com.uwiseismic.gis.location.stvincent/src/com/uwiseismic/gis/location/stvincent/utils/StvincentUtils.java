package com.uwiseismic.gis.location.stvincent.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.uwiseismic.gis.location.stvincent.City;
import com.uwiseismic.gis.location.stvincent.ElectoralDistrict;
import com.uwiseismic.gis.location.stvincent.Region;

import ncsa.gis.datasets.properties.DatasetProperties;
import ncsa.gis.locations.Location;
import ncsa.tools.common.types.Property;

public class StvincentUtils {
	private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(StvincentUtils.class);

	private static List<City> cities = new LinkedList<City>();
	private static Region region;
	private static List<ElectoralDistrict> allElectoralDistricts;
	private static Map<City, List<ElectoralDistrict>> districtsInCity = new HashMap<City, List<ElectoralDistrict>>();
	private static Map<String, ElectoralDistrict> codeToDistrict = new HashMap<String, ElectoralDistrict>();
	private static Map<String, City> codeToCity = new HashMap<String, City>();

	public static ElectoralDistrict getElectoralDistrictFromName(String name,
			City city) {
		for (ElectoralDistrict c : getDistrictsInCity(city)) {
			if (c.getName().equals(name))
				return c;
		}
		return null;
	}

	public static ElectoralDistrict getElectoralDistrictForCode(String code) {
		logger.debug("Getting district for code: " + code); //$NON-NLS-1$

		ElectoralDistrict c = codeToDistrict.get(code);
		if (c != null)
			return c;

		try {
			ElectoralDistrict district = LocationUtils2
					.getElectoralDistrictFromPostGIS(code);
			codeToDistrict.put(code, district);
			return district;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}

	public static List<ElectoralDistrict> getAllElectoralDistricts(Region r){
		logger.debug("Getting all electoral districts for region: " + r.getName()); //$NON-NLS-1$
		
		if(allElectoralDistricts != null)
			return allElectoralDistricts;
		try{
			allElectoralDistricts = LocationUtils2
				.getAllElectoralDistrictsFromPostGIS();
			return allElectoralDistricts;
		}catch(Throwable t){
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}
	
	public static List<ElectoralDistrict> getDistrictsInCity(City s) {
		logger.debug("Getting district for city: " + s.getName()); //$NON-NLS-1$

		List<ElectoralDistrict> c = districtsInCity.get(s);
		if (c != null)
			return c;

		try {
			List<ElectoralDistrict> distrcit = LocationUtils2
					.getElectoralDistrictsFromPostGIS(s.getCityCode());
			districtsInCity.put(s, distrcit);
			return distrcit;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}

	public static ElectoralDistrict getDistrictFromCodeString(String code) {
		logger.debug("Getting district for code: " + code); //$NON-NLS-1$

		/*if (code.length() > 2) {
			code = code.substring(0, 6);
		}*/

		ElectoralDistrict dist = codeToDistrict.get(code);
		if (dist != null)
			return dist;

		try {
			ElectoralDistrict d = LocationUtils2
					.getElectoralDistrictFromPostGIS(code);
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
	
	public static Region getRegionFromCodeString(String code){
		logger.debug("Getting Region for code: " + code); //$NON-NLS-1$
		
		
		if (region != null)
			return region;

		try {					
			return region;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
		//********************************* IMPLEMENT

	}
	

	public static City getCityFromCodeString(String code) {
		logger.debug("Getting city for code: " + code); //$NON-NLS-1$

		if (code.length() > 2) {
			code = code.substring(0, 2);
		}

		City s = codeToCity.get(code);
		if (s != null)
			return s;

		try {
			// City city = LocationUtils2.getCityFromPostGIS( cityCode );
			City city = LocationUtils2.getCityFromPostGIS(code);
			codeToCity.put(code, city);
			return city;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}

	public static List<City> getCities() {
		logger.debug("Getting cities"); //$NON-NLS-1$

		if (cities.size() != 0)
			return cities;

		try {
			// states = LocationUtils2.getStates();
			cities = LocationUtils2.getCitiesFromPostGIS();
			return cities;
		} catch (Throwable t) {
			logger.error("Failed", t); //$NON-NLS-1$
			return null;
		}
	}
	
	public static Location createLocationFromProperty(Property p) {

		if (p.getName().equals(Region.TAG_SELF)) {
			return new Region(p);
		}
		if (p.getName().equals(City.TAG_SELF)) {
			return new City(p);
		}
		if (p.getName().equals(ElectoralDistrict.TAG_SELF)) {
			return new ElectoralDistrict(p);
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
			if (property.getName().equals(ElectoralDistrict.TAG_SELF)) {
				ElectoralDistrict c = new ElectoralDistrict(property);
				buffer.append(c.getName() + ", "); //$NON-NLS-1$
			}
		}

		return buffer.toString();
	}
}
