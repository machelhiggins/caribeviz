package com.uwiseismic.gis.location.barbuda;

import java.util.LinkedList;
import java.util.List;

import com.uwiseismic.gis.location.barbuda.utils.BarbudaUtils;
import com.uwiseismic.gis.location.barbuda.utils.LocationUtils2;

import ncsa.gis.locations.Country;
import ncsa.gis.locations.Location;
import ncsa.gis.locations.LocationFactory;
import ncsa.tools.common.types.Property;

public class BarbudaLocationFactory implements LocationFactory
{
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( BarbudaLocationFactory.class );
    public final static String CITY = "City";
    public final static String DISTRICT = "District";
    public final static String MUNICIPALITY = "Electorial District";
    public final static String SUBDISTRICT = "Subdistrict/Village";

    public final static Barbuda country = new Barbuda();

    public List<String> getAllSubRegionTypes()
    {
        List<String> l = new LinkedList<String>();
        l.add( CITY );
        l.add( DISTRICT );
        l.add( MUNICIPALITY );
        l.add( SUBDISTRICT );

        return l;
    }

    public Country getCountry( String id )
    {
        return country;
    }

    public List<? extends Location> getSubRegions( Location l )
    {
        if ( l.getType().equals( Barbuda.TAG_SELF ) )
            return BarbudaUtils.getCities();
        if ( l.getType().equals( City.TAG_SELF ) )
            return BarbudaUtils.getDistrictsInCity( (City) l );
        if ( l.getType().equals( Region.TAG_SELF ) )
            return BarbudaUtils.getAllElectoralDistricts( (Region) l );

        return null;
    }

    public Location getParentRegion( Location l )
    {
        if ( l.getType().equals( Region.TAG_SELF ) )
            return ((Region) l).getCountry();
        if ( l.getType().equals( City.TAG_SELF ) )
            return ((City) l).getCountry();
        if ( l.getType().equals( ElectoralDistrict.TAG_SELF ) )
        	return ((ElectoralDistrict) l).getCity();
        return null;

    }

    public Location createLocationFromProperty( Property p )
    {
        return BarbudaUtils.createLocationFromProperty( p );
    }

    public Location realizeLocation( Location l )
    {
        try {
            Location r = null;
            if ( l instanceof City ) {
                City s = (City) l;
                r = LocationUtils2.getCityFromPostGIS( s.getCityCode() );
            } else if ( l instanceof ElectoralDistrict ) {
                ElectoralDistrict c = (ElectoralDistrict) l;
                r = LocationUtils2.getElectoralDistrictFromPostGIS( c.getDistrictCode() );
            } 
            return r;
        } catch ( Throwable t ) {
            logger.error( "Failed", t ); //$NON-NLS-1$
            return null;
        }
    }
}
