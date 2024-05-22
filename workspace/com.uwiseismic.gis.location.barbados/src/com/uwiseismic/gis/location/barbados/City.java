package com.uwiseismic.gis.location.barbados;


import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.uwiseismic.location.barbados.utils.BarbadosUtils;

import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;
import edu.illinois.ncsa.ergo.gis.locations.AbstractLocation;
import edu.illinois.ncsa.ergo.gis.locations.Country;
import edu.illinois.ncsa.ergo.gis.locations.Location;
import ncsa.tools.common.types.Property;

public class City extends AbstractLocation
{
    public final static String TAG_SELF = "island"; //$NON-NLS-1$
    public final static String TAG_CITY_CODE = "city_code"; //$NON-NLS-1$

    private String cityCode;

    //    public City( String fipsCode, String name, String abb, FeatureDataset dataset )
    public City( String fipsCode, String name, FeatureDataset dataset ){
        super( name, dataset );
        this.cityCode = fipsCode;
    }

    public City( Property p ){
        fromProperty( p );
    }

    public String getType()
    {
        return TAG_SELF;
    }

    public Country getCountry()
    {
        return Barbados.INSTANCE;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public Property asProperty()
    {
        return new Property( TAG_SELF, cityCode, Barbados.ID, "location" ); //$NON-NLS-2$ //$NON-NLS-3$
    }

    private void fromProperty( Property p )
    {
        cityCode = p.getValue();
        City s = BarbadosUtils.getCityFromCodeString( cityCode );
        name = s.getName();
        featureDataset = s.getFeatureDataset();
    }

    public String getFullName()
    {
        return name;
    }

    public String getShortName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Location getContainingLocation()
    {
        return getCountry();
    }

    public String getSubLocationType(){
        return EnumerationDistrict.TAG_SELF;
    }

    // USER FACING

    public Element asElement()
    {
        Element e = new DefaultElement( TAG_SELF );

        e.addAttribute( TAG_CITY_CODE, cityCode );

        return e;
    }

    public void initializeFromElement( Element element )
    {
        cityCode = element.attributeValue( TAG_CITY_CODE );
    }

    public String getNameWithType()
    {
        return getShortName() + " " + getType();
    }
}
