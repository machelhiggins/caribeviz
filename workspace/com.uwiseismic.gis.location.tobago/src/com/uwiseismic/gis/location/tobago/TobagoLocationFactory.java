package com.uwiseismic.gis.location.tobago;

import java.util.LinkedList;
import java.util.List;


import com.uwiseismic.gis.location.datastorage.RuntimeStorageMethod;
import com.uwiseismic.gis.location.tobago.utils.LocationUtils2;
import com.uwiseismic.gis.location.tobago.utils.TobagoUtils;


import edu.illinois.ncsa.ergo.gis.locations.Country;
import edu.illinois.ncsa.ergo.gis.locations.Location;
import edu.illinois.ncsa.ergo.gis.locations.LocationFactory;
import ncsa.tools.common.types.Property;

public class TobagoLocationFactory implements LocationFactory
{
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( TobagoLocationFactory.class );
    public final static String CITY = "Country";
    public final static String ENUMERATION_DISTRICT = "Parish";

    public final static Tobago country = new Tobago();

    public List<String> getAllSubRegionTypes()
    {
        List<String> l = new LinkedList<String>();
        l.add( CITY );
        l.add( ENUMERATION_DISTRICT );

        return l;
    }

    public Country getCountry( String id ){
        return country;
    }

    public List<? extends Location> getSubRegions( Location l )
    {

        if ( l.getType().equals( Tobago.TAG_SELF ) )
            return TobagoUtils.getCities();
        if ( l.getType().equals( City.TAG_SELF ) )
            return TobagoUtils.getDistrictsInCity( (City) l );

        return null;
    }

    public Location getParentRegion( Location l ){
        if ( l.getType().equals( City.TAG_SELF ) )
            return ((City) l).getCountry();
        if ( l.getType().equals( EnumerationDistrict.TAG_SELF ) )
        	return ((EnumerationDistrict) l).getCity();
        return null;

    }

    public Location createLocationFromProperty( Property p ){    	
        return TobagoUtils.createLocationFromProperty( p );
    }

    public Location realizeLocation( Location l ){    	
        try {
            Location r = null;
            if ( l instanceof City ) {
                City s = (City) l;
                if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
                	r = LocationUtils2.getCityFromShapefile( s.getCityCode() );
                else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)
//                	r = LocationUtils2.getCityFromPostGIS( s.getCityCode() );
                	r = null;
            } 
            else if ( l instanceof EnumerationDistrict ) {
                EnumerationDistrict c = (EnumerationDistrict) l;
                if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.SHAPEFILE)
                	r = LocationUtils2.getEnumerationDistrictFromShapefile( c.getDistrictCode() , c.getCityCode());
                else if(RuntimeStorageMethod.STORAGE_TYPE() == RuntimeStorageMethod.POSTGIS)
//                	r = LocationUtils2.getEnumerationDistrictFromPostGIS( c.getDistrictCode() , c.getCityCode());
                	r = null;
            } 
            return r;
        } catch ( Throwable t ) {
            logger.error( "Failed", t ); //$NON-NLS-1$
            return null;
        }
    }
}