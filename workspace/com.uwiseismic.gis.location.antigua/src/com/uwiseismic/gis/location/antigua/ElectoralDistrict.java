package com.uwiseismic.gis.location.antigua;


import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.uwiseismic.gis.location.antigua.utils.AntiguaUtils;

import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;
import edu.illinois.ncsa.ergo.gis.locations.AbstractLocation;
import edu.illinois.ncsa.ergo.gis.locations.Location;
import ncsa.tools.common.types.Property;

public class ElectoralDistrict extends AbstractLocation {
	public final static String TAG_SELF = "electoral_district"; //$NON-NLS-1$
	public final static String TAG_DIST_CODE = "ed_code"; //$NON-NLS-1$

	private String cityCode;
	private String districtCode;

	public ElectoralDistrict(Property property) {
		fromProperty(property);
	}

	public ElectoralDistrict(String distCode, String cCode, String name,
			FeatureDataset featureDataset) {
		super(name, featureDataset);
		this.districtCode = distCode;
		this.cityCode = cCode;
	}

	public String getType() {
		return TAG_SELF;
	}

	public City getCity() {
		return AntiguaUtils.getCityFromCodeString(districtCode);
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public String getFullName() {
		String name = getName();
		String cityName = getCity().getShortName();

		String fullName = name + " " + AntiguaLocationFactory.DISTRICT + ", "
				+ cityName + " " + AntiguaLocationFactory.CITY;

		return fullName;
	}

	public String getShortName() {
		return getName();
	}

	public Location getContainingLocation() {
		return getCity();
	}

	public Property asProperty() {
		return new Property(TAG_SELF, districtCode, Antigua.ID, "location"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void fromProperty(Property p) {
		districtCode = p.getValue();
		ElectoralDistrict c = AntiguaUtils
				.getElectoralDistrictForCode(districtCode);
		name = c.getName();
		featureDataset = c.getFeatureDataset();
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