package com.uwiseismic.gis.location.barbuda;

import ncsa.gis.datasets.FeatureDataset;
import ncsa.gis.locations.AbstractLocation;
import ncsa.gis.locations.Location;
import ncsa.tools.common.types.Property;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.uwiseismic.gis.location.barbuda.utils.BarbudaUtils;

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
		return BarbudaUtils.getCityFromCodeString(districtCode);
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public String getFullName() {
		String name = getName();
		String cityName = getCity().getShortName();

		String fullName = name + " " + BarbudaLocationFactory.DISTRICT + ", "
				+ cityName + " " + BarbudaLocationFactory.CITY;

		return fullName;
	}

	public String getShortName() {
		return getName();
	}

	public Location getContainingLocation() {
		return getCity();
	}

	public Property asProperty() {
		return new Property(TAG_SELF, districtCode, Barbuda.ID, "location"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void fromProperty(Property p) {
		districtCode = p.getValue();
		ElectoralDistrict c = BarbudaUtils
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
