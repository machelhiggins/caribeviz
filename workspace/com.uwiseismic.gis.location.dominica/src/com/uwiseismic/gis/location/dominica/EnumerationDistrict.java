package com.uwiseismic.gis.location.dominica;

import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;
import edu.illinois.ncsa.ergo.gis.locations.AbstractLocation;
import edu.illinois.ncsa.ergo.gis.locations.Location;
import ncsa.tools.common.types.Property;

import java.util.StringTokenizer;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.uwiseismic.gis.location.dominica.utils.DominicaUtils;
import com.uwiseismic.gis.location.dominica.utils.LocationUtils2;

public class EnumerationDistrict extends AbstractLocation {
	public final static String TAG_SELF = "enumeration_district"; //$NON-NLS-1$
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

	public String getType() {
		return TAG_SELF;
	}

	public City getCity() {
		return DominicaUtils.getCityFromCodeString(districtCode);
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public String getFullName() {
		String name = getName();
		String cityName = getCity().getShortName();

		String fullName = name + " " + DominicaLocationFactory.DISTRICT + ", "
				+ cityName + " " + DominicaLocationFactory.CITY;

		return fullName;
	}

	public String getShortName() {
		return getName();
	}

	public Location getContainingLocation() {
		return getCity();
	}

	public Property asProperty() {
		return new Property(TAG_SELF, districtCode, Dominica.ID, "location"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void fromProperty(Property p) {
		StringTokenizer st = new StringTokenizer(p.getValue(),",");
		districtCode = st.nextToken();
		try{
			cityCode = st.nextToken();
		}catch(java.util.NoSuchElementException n){
			cityCode = LocationUtils2.getCityForEnumerationDistrictsFromCSV(districtCode).getCityCode();
		}
		City s = DominicaUtils.getCityFromCodeString( cityCode );
        name = s.getName();
        featureDataset = s.getFeatureDataset();
		name = districtCode;		
		//getFeatureDataset();
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
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
