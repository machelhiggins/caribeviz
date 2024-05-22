package com.uwiseismic.gis.location.nevis;

import java.util.LinkedList;
import java.util.List;

import com.uwiseismic.gis.location.nevis.utils.LocationUtils2;
import com.uwiseismic.gis.location.nevis.utils.NevisUtils;

import ncsa.gis.locations.Country;
import ncsa.gis.locations.Location;
import ncsa.gis.locations.LocationFactory;
import ncsa.tools.common.types.Property;

public class NevisLocationFactory implements LocationFactory
{
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( NevisLocationFactory.class );
    public final static String CITY = "City";
    public final static String DISTRICT = "District";
    public final static String MUNICIPALITY = "Electorial District";
    public final static String SUBDISTRICT = "Subdistrict/Village";

    public final static Nevis country = new Nevis();

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
        if ( l.getType().equals( Nevis.TAG_SELF ) )
            return NevisUtils.getCities();
        if ( l.getType().equals( City.TAG_SELF ) )
            return NevisUtils.getDistrictsInCity( (City) l );
        if ( l.getType().equals( Region.TAG_SELF ) )
            return NevisUtils.getAllElectoralDistricts( (Region) l );

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
        return NevisUtils.createLocationFromProperty( p );
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
