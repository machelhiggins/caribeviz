package com.uwiseismic.gis.location.dominica;

import java.util.LinkedList;
import java.util.List;

import com.uwiseismic.gis.location.datastorage.RuntimeStorageMethod;
import com.uwiseismic.gis.location.dominica.utils.DominicaUtils;
import com.uwiseismic.gis.location.dominica.utils.LocationUtils2;
import com.uwiseismic.gis.location.dominica.City;
import com.uwiseismic.gis.location.dominica.EnumerationDistrict;
import com.uwiseismic.gis.location.dominica.Dominica;
import com.uwiseismic.gis.location.dominica.utils.DominicaUtils;

import edu.illinois.ncsa.ergo.gis.locations.Country;
import edu.illinois.ncsa.ergo.gis.locations.Location;
import edu.illinois.ncsa.ergo.gis.locations.LocationFactory;
import ncsa.tools.common.types.Property;

public class DominicaLocationFactory implements LocationFactory
{
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( DominicaLocationFactory.class );
    public final static String CITY = "City";
    public final static String DISTRICT = "Parish";

    public final static Dominica country = new Dominica();

    public List<String> getAllSubRegionTypes()
    {
        List<String> l = new LinkedList<String>();
        l.add( CITY );
        l.add( DISTRICT );

        return l;
    }

    public Country getCountry( String id ){
        return country;
    }

    public List<? extends Location> getSubRegions( Location l )
    {

        if ( l.getType().equals( Dominica.TAG_SELF ) )
            return DominicaUtils.getCities();
        if ( l.getType().equals( City.TAG_SELF ) )
            return DominicaUtils.getDistrictsInCity( (City) l );

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
        return DominicaUtils.createLocationFromProperty( p );
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
