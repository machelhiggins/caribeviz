package com.uwiseismic.gis.location.grenada;


import java.io.IOException;
import java.util.StringTokenizer;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.uwiseismic.gis.location.datastorage.RuntimeStorageMethod;
import com.uwiseismic.gis.location.grenada.utils.GrenadaUtils;
import com.uwiseismic.gis.location.grenada.utils.LocationUtils2;

import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;
import edu.illinois.ncsa.ergo.gis.locations.AbstractLocation;
import edu.illinois.ncsa.ergo.gis.locations.Location;
import ncsa.tools.common.types.Property;

public class EnumerationDistrict extends AbstractLocation {
	public final static String TAG_SELF = "parish"; //$NON-NLS-1$
	public final static String TAG_DIST_CODE = "ed_code"; //$NON-NLS-1$

	private String cityCode;
	private String districtCode;
	
	
	public EnumerationDistrict(Property property) {
		fromProperty(property);
	}

	public EnumerationDistrict(String distCode, String cCode, String name,
			FeatureDataset featureDataset) {
		super(name, featureDataset);
		this.districtCode = distCode;
		this.cityCode = cCode;
	}
	
	public FeatureDataset getFeatureDataset(){		
		//*** DEBUG Lets just force retrieving dataset for now
		try {
			if(featureDataset == null || featureDataset.getFeatures().size() == 0){
				try {
					EnumerationDistrict t = null;
					if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
						t = LocationUtils2.getEnumerationDistrictFromShapefile(districtCode, cityCode);
					else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)					
//						t = LocationUtils2.getEnumerationDistrictFromPostGIS(districtCode, cityCode);
						t = null;
					if(t != null)
						featureDataset = t.getFeatureDataset();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return featureDataset;
	}

	public String getType() {
		return TAG_SELF;
	}

	public City getCity() {
		return GrenadaUtils.getCityFromCodeString(cityCode);
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public String getCityCode() {
		return cityCode;
	}
	
	public String getFullName() {
		String name = getName();
		String cityName = getCity().getShortName();

		String fullName = name + " " + GrenadaLocationFactory.ENUMERATION_DISTRICT + ", "
				+ cityName + " " + GrenadaLocationFactory.CITY;

		return fullName;
	}

	public String getShortName() {
		return getName();
	}
	
	public Location getContainingLocation() {
		return getCity();
	}

	public Property asProperty() {
		return new Property(TAG_SELF, districtCode+","+cityCode, Grenada.ID, "location"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void fromProperty(Property p) {
		StringTokenizer st = new StringTokenizer(p.getValue(),",");
		districtCode = st.nextToken();
		try{
			cityCode = st.nextToken();
		}catch(java.util.NoSuchElementException n){
			cityCode = LocationUtils2.getCityForEnumerationDistrictsFromCSV(districtCode).getCityCode();
		}
		City s = GrenadaUtils.getCityFromCodeString( cityCode );
        name = s.getName();
        featureDataset = s.getFeatureDataset();
		name = districtCode;		
		//getFeatureDataset();
	}

	public String getSubLocationType() {
		return TAG_SELF;
	}

	// USER FACING

	public Element asElement() {
		Element e = new DefaultElement(TAG_SELF);

		e.addAttribute(TAG_DIST_CODE, districtCode);

		return e;
	}

	public void initializeFromElement(Element element) {
		districtCode = element.attributeValue(TAG_DIST_CODE);
	}

	public String getNameWithType() {
		return getShortName() + " " + getType();
	}
}
