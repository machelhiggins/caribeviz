package com.uwiseismic.gis.location.stlucia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import ncsa.gis.GISConstants;
import ncsa.gis.Messages;
import ncsa.gis.datasets.FeatureDataset;
import ncsa.gis.osgi.Activator;

import org.eclipse.core.runtime.Platform;
import org.geotools.data.FeatureSource;
import org.geotools.data.collection.CollectionDataStore;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.filter.AttributeExpression;
import org.geotools.filter.CompareFilter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.LiteralExpression;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.osgi.framework.Bundle;

import com.uwiseismic.db.JDBCConnectionPool;
import com.uwiseismic.db.pgsql.GeometryColumn;
import com.uwiseismic.db.sql.SearchConnector;
import com.uwiseismic.db.sql.SearchParameter;
import com.uwiseismic.db.sql.SelectStatement;
import com.uwiseismic.gis.location.stlucia.City;
import com.uwiseismic.gis.location.stlucia.ElectoralDistrict;
import com.uwiseismic.gis.location.stlucia.Region;
import com.uwiseismic.gis.location.stlucia.Stlucia;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

public class LocationUtils2
{
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( LocationUtils2.class );

    /**
     * Returns all Cities for this region
     * @return
     */
    public static List<City> getCitiesFromPostGIS(){

        List<City> cityList = new LinkedList<City>();

        try {

        	JDBCConnectionPool cPool = getJDBCConnectionPool();
        	Connection conn = (Connection)cPool.checkOut();
        	
        	SelectStatement sql = new SelectStatement("city");
        	sql.addSelectColumn("city_code");
        	sql.addSelectColumn("city_name");
        	sql.addSearchParameter(new SearchParameter("roi_id", Region.REGION_ID, false));      

        	
            PreparedStatement st = conn.prepareStatement(sql.toString());

            logger.debug( st.toString() );
            ResultSet rs = st.executeQuery();
            while ( rs.next() ) {
                String fips = rs.getString( 1 );
                String name = rs.getString( 2 );
                City s = new City( fips, name, null );
                cityList.add( s );
            }
            rs.close();
            cPool.checkIn(conn);

        } catch ( SQLException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch ( ClassNotFoundException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(Exception ex){
        	ex.printStackTrace();
        }

        return cityList;

    }

    /** 
     * Returns city from city string code
     * @param aCityCode
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static City getCityFromPostGIS( String aCityCode) throws Exception
    {
        WKTReader wktReader = new WKTReader();

        FeatureCollection fc = FeatureCollections.newCollection();

        FeatureTypeBuilder ftb = FeatureTypeBuilder.newInstance( "region" ); //$NON-NLS-1$
        ftb.addType( (GeometryAttributeType) AttributeTypeFactory.newAttributeType( "the_geom", Polygon.class ) ); //$NON-NLS-1$
             
        FeatureType ft = ftb.getFeatureType();

        
        String cityCode = null;
        String cityName = null;

        try {
        	JDBCConnectionPool cPool = getJDBCConnectionPool();
        	Connection conn = (Connection)cPool.checkOut();
        	
        	SelectStatement sql = new SelectStatement();
        	sql.addTableName("city");
        	sql.addTableName("city_boundries");
        	sql.addSelectColumn("ASTEXT(the_geom)", "the_geom");
        	sql.addSelectColumn("city.city_code");
        	sql.addSelectColumn("city_name");
        	sql.addSearchParameter(new SearchParameter("roi_id", Region.REGION_ID, false));
        	sql.addSearchElement(SearchConnector.AND_CONNECTOR);
        	sql.addSearchParameter(new SearchParameter("city.city_code", aCityCode, false));
            PreparedStatement st = conn.prepareStatement(sql.toString());
                       

            logger.debug( st.toString() );
            ResultSet rs = st.executeQuery();
            while ( rs.next() ) {
                Geometry tmpGeom = wktReader.read( rs.getString( 1 ) );
                Geometry geom = tmpGeom.getGeometryN( 0 );
                cityCode = rs.getString( 2 );
                cityName = rs.getString( 3 );
                //                Object[] att = { geom, cityCode, cityName };
                Object[] att = { geom };
                Feature f = ft.create( att );
                fc.add( f );
            }
            rs.close();
            cPool.checkIn(conn);

        } catch ( SQLException e ) {
            logger.error( "Failed", e ); //$NON-NLS-1$
        } catch ( ClassNotFoundException e ) {
            logger.error( "Failed", e ); //$NON-NLS-1$
        }

        //        return new City( stateFips, stateName, stateAbb, createFeatureDataset( fc ) );
        return new City( cityCode, cityName, createFeatureDataset( fc ) );
    }

    /**
     * Returns all ElectoralDistrict objects for city code string
     * @param cityCode
     * @return
     */
    public static List<ElectoralDistrict> getElectoralDistrictsFromPostGIS( String cityCode )
    {

        List<ElectoralDistrict> countyList = new LinkedList<ElectoralDistrict>();

        try {
        	JDBCConnectionPool cPool = getJDBCConnectionPool();
        	Connection conn = (Connection)cPool.checkOut();
        	
        	SelectStatement sql = new SelectStatement();
        	sql.addTableName("electoral_district");
        	sql.addTableName("electoral_district_boundaries");
        	sql.addSelectColumn("ASTEXT(the_geom)", "the_geom");
        	sql.addSelectColumn("electoral_district.ed_code");
        	sql.addSelectColumn("city_code");
        	sql.addSearchParameter(new SearchParameter("roi_id", Region.REGION_ID, false));
        	sql.addSearchElement(SearchConnector.AND_CONNECTOR);
        	sql.addSearchParameter(new SearchParameter("city_code", cityCode, false));
            PreparedStatement st = conn.prepareStatement(sql.toString());;
            
            logger.debug( st.toString() );
            ResultSet rs = st.executeQuery();
            while ( rs.next() ) {
                String distcode = rs.getString( 1 );
                String city_code = rs.getString( 2 );
                String name = rs.getString( 1 );
                ElectoralDistrict c = new ElectoralDistrict( distcode, city_code, name, null );
                countyList.add( c );
            }
            rs.close();
           cPool.checkIn(conn);

        } catch ( SQLException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch ( ClassNotFoundException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(Exception ex){
        	ex.printStackTrace();
        }

        return countyList;

    }

    /**
     * Returns ElectoralDistrict object for ED string code
     * @param distCode
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static ElectoralDistrict getElectoralDistrictFromPostGIS( String distCode ) throws Exception{
        WKTReader wktReader = new WKTReader();

        FeatureCollection fc = FeatureCollections.newCollection();

        FeatureTypeBuilder ftb = FeatureTypeBuilder.newInstance( "region" ); //$NON-NLS-1$
        ftb.addType( (GeometryAttributeType) AttributeTypeFactory.newAttributeType( "the_geom", Polygon.class ) ); //$NON-NLS-1$
        //        ftb.addType( AttributeTypeFactory.newAttributeType( "ctyfips", String.class, true, 15, null ) ); //$NON-NLS-1$
        //        ftb.addType( AttributeTypeFactory.newAttributeType( "state", String.class, true, 2, null ) ); //$NON-NLS-1$
        //        ftb.addType( AttributeTypeFactory.newAttributeType( "name", String.class, true, 15, null ) ); //$NON-NLS-1$

        FeatureType ft = ftb.getFeatureType();

        String distcode = null;
        String city_code = null;
        String name = null;

        try {
        	JDBCConnectionPool cPool = getJDBCConnectionPool();
        	Connection conn = (Connection)cPool.checkOut();
        	
        	SelectStatement sql = new SelectStatement();
        	sql.addTableName("electoral_district");
        	sql.addTableName("electoral_district_boundaries");
        	sql.addSelectColumn("ASTEXT(the_geom)", "the_geom");
        	sql.addSelectColumn("electoral_district.ed_code");
        	sql.addSelectColumn("city_code");
        	sql.addSearchParameter(new SearchParameter("roi_id", Region.REGION_ID, false));
        	sql.addSearchElement(SearchConnector.AND_CONNECTOR);
        	sql.addSearchParameter(new SearchParameter("electoral_district.ed_code", distCode, false));
            PreparedStatement st = conn.prepareStatement(sql.toString());;

           // String sql = "SELECT AsText(the_geom) as the_geom, distcode, city_code,  name FROM district WHERE distcode = '" + distCode + "'"; //$NON-NLS-1$ //$NON-NLS-2$

            logger.debug( st.toString() );
            ResultSet rs = st.executeQuery();
            while ( rs.next() ) {
                Geometry tmpGeom = wktReader.read( rs.getString( 1 ) );
                Geometry geom = tmpGeom.getGeometryN( 0 );
                distcode = rs.getString( 2 );
                city_code = rs.getString( 3 );
                name = rs.getString( 2 );
                //                Object[] att = { geom, distcode, city_code, name };
                Object[] att = { geom };
                Feature f = ft.create( att );
                fc.add( f );
            }
            rs.close();
           cPool.checkIn(conn);
        } catch ( SQLException e ) {
            logger.error( "Failed", e ); //$NON-NLS-1$
        } catch ( ClassNotFoundException e ) {
            logger.error( "Failed", e ); //$NON-NLS-1$
        }catch(Exception ex){
        	ex.printStackTrace();
        }

        return new ElectoralDistrict( distcode, city_code, name, createFeatureDataset( fc ) );
    }
  
    /**
     * Returns ALL ElectoralDistrict data objects for this region
     * @return
     * @throws Exception
     */
    public static List<ElectoralDistrict> getAllElectoralDistrictsFromPostGIS() throws Exception{
    	
    	List<ElectoralDistrict> edList = new LinkedList<ElectoralDistrict>();
        WKTReader wktReader = new WKTReader();

        String distcode = null;
        String city_code = null;
        String name = null;

        try {
        	JDBCConnectionPool cPool = getJDBCConnectionPool();
        	Connection conn = (Connection)cPool.checkOut();
        	
        	SelectStatement sql = new SelectStatement();
        	sql.addTableName("electoral_district");
        	sql.addTableName("electoral_district_boundaries");
        	sql.addSelectColumn("ASTEXT(the_geom)", "the_geom");
        	sql.addSelectColumn("electoral_district.ed_code");
        	sql.addSelectColumn("city_code");
        	sql.addSearchParameter(new SearchParameter("roi_id", Region.REGION_ID, false));
            PreparedStatement st = conn.prepareStatement(sql.toString());;

           // String sql = "SELECT AsText(the_geom) as the_geom, distcode, city_code,  name FROM district WHERE distcode = '" + distCode + "'"; //$NON-NLS-1$ //$NON-NLS-2$

            logger.debug( st.toString() );
            ResultSet rs = st.executeQuery();
            while ( rs.next() ) {
                Geometry tmpGeom = wktReader.read( rs.getString( 1 ) );
                Geometry geom = tmpGeom.getGeometryN( 0 );
                distcode = rs.getString( 2 );
                city_code = rs.getString( 3 );
                name = rs.getString( 2 );

                edList.add( new ElectoralDistrict( distcode, city_code, name, null ) );
            }
            rs.close();
           cPool.checkIn(conn);
        } catch ( SQLException e ) {
            logger.error( "Failed", e ); //$NON-NLS-1$
        } catch ( ClassNotFoundException e ) {
            logger.error( "Failed", e ); //$NON-NLS-1$
        }catch(Exception ex){
        	ex.printStackTrace();
        }
 	
    	return edList;
    }
    
    @SuppressWarnings("unchecked")
    private static FeatureDataset createFeatureDataset( Feature f )
    {
        try {
            FeatureCollection c = FeatureCollections.newCollection();
            c.add( f );

            CollectionDataStore ds = new CollectionDataStore( c );
            FeatureSource fs = ds.getFeatureSource( ds.getTypeNames()[0] );

            FeatureDataset dataset = new FeatureDataset();
            dataset.setDataId( null );
            dataset.setFeatureGeometryType( GISConstants.POLYGON );
            dataset.setFeatureSource( fs );
            dataset.setFriendlyName( Messages.getString( "LocationUtils2.0" ) ); //$NON-NLS-1$
            //dataset.setStyle( GeometryUtil.getDefaultStyle( GISConstants.POLYGON ) );
            Bundle bundle = Platform.getBundle( Activator.PLUGIN_ID );
            URL resourceURL = bundle.getResource( "gisStyles/gis-boundary_1.0.sld" ); //$NON-NLS-1$
            StyleFactory factory = StyleFactoryFinder.createStyleFactory();
            SLDParser parser = new SLDParser( factory, resourceURL );
            Style[] style = parser.readXML();
            dataset.setStyle( style[0] );
            dataset.setTypeId( GISConstants.REGION_OF_INTEREST );

            return dataset;
        } catch ( Throwable t ) {
            logger.error( "Failed to create dataset: " + t.getMessage() ); //$NON-NLS-1$
            return null;
        }
    }

    private static FeatureDataset createFeatureDataset( FeatureCollection fc )
    {
        try {
            CollectionDataStore ds = new CollectionDataStore( fc );
            FeatureSource fs = ds.getFeatureSource( ds.getTypeNames()[0] );
            logger.debug( fs.getSchema().getTypeName() );
            FeatureDataset dataset = new FeatureDataset();
            dataset.setDataId( null );
            dataset.setFeatureGeometryType( GISConstants.POLYGON );
            dataset.setFeatureSource( fs );
            dataset.setFriendlyName( Messages.getString( "LocationUtils2.1" ) ); //$NON-NLS-1$
            //dataset.setStyle( GeometryUtil.getDefaultStyle( GISConstants.POLYGON ) );

            Bundle bundle = Platform.getBundle( Activator.PLUGIN_ID );
            URL resourceURL = bundle.getResource( "gisStyles/gis-boundary_1.0.sld" ); //$NON-NLS-1$

            StyleFactory factory = StyleFactoryFinder.createStyleFactory();
            SLDParser parser = new SLDParser( factory, resourceURL );
            Style[] style = parser.readXML();
            dataset.setStyle( style[0] );
            dataset.setTypeId( GISConstants.REGION_OF_INTEREST );

            return dataset;
        } catch ( Throwable t ) {
            if ( t.getMessage() != null )
                logger.error( "Failed to create dataset: " + t.getMessage() ); //$NON-NLS-1$
            else
                logger.error( "Failed", t ); //$NON-NLS-1$

            return null;
        }
    }
    
    private static JDBCConnectionPool getJDBCConnectionPool() throws Exception{
		Bundle bundle = Platform.getBundle("com.uwiseismic.db");
		Class cPoolClass = bundle.loadClass("com.uwiseismic.db.JDBCConnectionPool");
		Method method = cPoolClass.getMethod("getInstance");
		return (JDBCConnectionPool) method.invoke(null);
    }
}
