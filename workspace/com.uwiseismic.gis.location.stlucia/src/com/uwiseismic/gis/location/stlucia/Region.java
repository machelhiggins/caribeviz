package com.uwiseismic.gis.location.stlucia;

import ncsa.gis.datasets.FeatureDataset;
import ncsa.gis.locations.AbstractLocation;
import ncsa.gis.locations.Location;
import ncsa.tools.common.types.Property;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.uwiseismic.gis.location.stlucia.utils.StluciaUtils;

/**
 * Represents super set of all Electoral district per country
 * 
 * @author Machel
 *
 */
public class Region extends AbstractLocation {
	public final static String TAG_SELF = "all_electoral_district"; //$NON-NLS-1$
	public final static String TAG_REGION_CODE = "region_code"; //$NON-NLS-1$
	public final static String REGION_ID = "1";

	private String districtCode = REGION_ID;

	public Region(Property property) {
		fromProperty(property);
	}

	public Region(String name,
			FeatureDataset featureDataset) {
		super(name, featureDataset);
	}

	public String getType() {
		return TAG_SELF;
	}

	public City getCountry() {
		return StluciaUtils.getCityFromCodeString(districtCode);
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public String getFullName() {
		String name = getName();
		String cityName = getCountry().getShortName();

		String fullName = name + " " + StluciaLocationFactory.DISTRICT + ", "
				+ cityName + " " + StluciaLocationFactory.CITY;

		return fullName;
	}

	public String getShortName() {
		return getName();
	}

    public Location getContainingLocation(){
        return getCountry();
    }
	public Property asProperty() {
		return new Property(TAG_SELF, districtCode, Stlucia.ID, "location"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void fromProperty(Property p) {
		//** Don't need property, this will have a unique id
        Region r = StluciaUtils.getRegionFromCodeString( REGION_ID );
        name = r.getName();
		
		featureDataset = r.getFeatureDataset();
	}

	public String getSubLocationType() {
		return TAG_SELF;
	}

	// USER FACING

	public Element asElement() {
		Element e = new DefaultElement(TAG_SELF);

		e.addAttribute(TAG_REGION_CODE, districtCode);

		return e;
	}

	public void initializeFromElement(Element element) {
		districtCode = element.attributeValue(TAG_REGION_CODE);
	}

	public String getNameWithType() {
		return getShortName() + " " + getType();
	}
}
