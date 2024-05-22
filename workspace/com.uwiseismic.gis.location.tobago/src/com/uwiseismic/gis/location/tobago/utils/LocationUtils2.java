package com.uwiseismic.gis.location.tobago.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Platform;
import org.geotools.data.collection.CollectionDataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.osgi.framework.Bundle;

import com.uwiseismic.gis.location.tobago.City;
import com.uwiseismic.gis.location.tobago.EnumerationDistrict;
import com.uwiseismic.gis.location.tobago.osgi.Activator;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import edu.illinois.ncsa.ergo.gis.GISConstants;
import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;
import edu.illinois.ncsa.ergo.gis.datasets.Messages;
import edu.illinois.ncsa.ergo.gis.util.DatasetUtils;
import ncsa.tools.common.types.Property;

public class LocationUtils2
{
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( LocationUtils2.class );

    private static FeatureCollection districtsFC;

//    /**
//     * Returns all Cities for this region
//     * @return
//     */
//    public static List<City> getCitiesFromPostGIS(){
//
//        List<City> cityList = new LinkedList<City>();
//
//        try {
//
//        	JDBCConnectionPool cPool = getJDBCConnectionPool();
//        	Connection conn = (Connection)cPool.checkOut();
//
//        	SelectStatement selectQuery = new SelectStatement("city");
//        	selectQuery.addSelectColumn("city_code");
//        	selectQuery.addSelectColumn("city_name");
//        	selectQuery.addSearchParameter(new SearchParameter("roi_id", Barbados.REGION_ID, false));
//
//            PreparedStatement st = conn.prepareStatement(selectQuery.toString());
//
//            logger.debug( st.toString() );
//            ResultSet rs = st.executeQuery();
//            while ( rs.next() ) {
//                String fips = rs.getString( 1 );
//                String name = rs.getString( 2 );
//                City s = new City( fips, name, null );
//                cityList.add( s );
//            }
//            rs.close();
//            cPool.checkIn(conn);
//
//        } catch ( SQLException e1 ) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch ( ClassNotFoundException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch(Exception ex){
//        	ex.printStackTrace();
//        }
//
//        return cityList;
//    }

    /**
     * @return
     */
    public static List<City> getCitiesFromCSV()
    {
        Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.tobago.osgi.Activator.PLUGIN_ID );
        URL resourceURL = bundle.getResource( "regionCode/listCity.csv" ); //$NON-NLS-1$

        List<City> cityList = new LinkedList<City>();

        InputStream source;
        try {
            source = resourceURL.openStream();
            BufferedReader bufRdr = new BufferedReader( new InputStreamReader( source ) );
            String line = null;

            line = bufRdr.readLine();
            //          read each line of text file
            while ( (line = bufRdr.readLine()) != null ) {
                StringTokenizer st = new StringTokenizer( line, "," ); //$NON-NLS-1$

                String cityCode = st.nextToken(); //statefips
                String cityName = st.nextToken(); //statename
                //                String abb =  st.nextToken();  //state abb

                City s = new City( cityCode, cityName, null );
                cityList.add( s );

            }
            return cityList;
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


//    /**
//     * Returns city from city string code
//     * @param aCityCode
//     * @return
//     * @throws Exception
//     */
//    @SuppressWarnings("unchecked")
//    public static City getCityFromPostGIS( String aCityCode) throws Exception
//    {
//    	System.out.println("getCityFromPostGIS()");//****************** DEBUG ******************
//    	WKTReader wktReader = new WKTReader();
//
//        FeatureCollection fc = FeatureCollections.newCollection();
//
//        FeatureTypeBuilder ftb = FeatureTypeBuilder.newInstance( "region" ); //$NON-NLS-1$
//        ftb.addType( (GeometryAttributeType) AttributeTypeFactory.newAttributeType( "the_geom", Polygon.class ) ); //$NON-NLS-1$
//
//        FeatureType ft = ftb.getFeatureType();
//
//
//        String cityCode = null;
//        String cityName = null;
//
//        try {
//        	JDBCConnectionPool cPool = getJDBCConnectionPool();
//        	Connection conn = (Connection)cPool.checkOut();
//
//        	SelectStatement selectQuery = new SelectStatement();
//        	selectQuery.addTableName("city");
//        	selectQuery.addTableName("city_boundaries");
//        	selectQuery.addSelectColumn("ASTEXT(city_boundaries.the_geom)", "the_geom");
//        	//selectQuery.addSelectColumn("the_geom");
//        	selectQuery.addSelectColumn("city.city_code");
//        	selectQuery.addSelectColumn("city_name");
//        	selectQuery.addSearchParameter(new SearchParameter("city.city_code", "city_boundaries.city_code", false));
//        	selectQuery.addSearchElement(SearchConnector.AND_CONNECTOR);
//        	selectQuery.addSearchParameter(new SearchParameter("roi_id", Barbados.REGION_ID, false));
//        	selectQuery.addSearchElement(SearchConnector.AND_CONNECTOR);
//        	selectQuery.addSearchParameter(new SearchParameter("city.city_code", aCityCode, false));
//            PreparedStatement st = conn.prepareStatement(selectQuery.toString());
//
//            logger.debug( st.toString() );
//            ResultSet rs = st.executeQuery();
//            while ( rs.next() ) {
//            //rs.next();
//                Geometry tmpGeom = wktReader.read( rs.getString( 1 ) );
//                Geometry geom = tmpGeom.getGeometryN( 0 );
//                cityCode = rs.getString( 2 );
//                cityName = rs.getString( 3 );
//                //                Object[] att = { geom, cityCode, cityName };
//                Object[] att = { geom };
//                Feature f = ft.create( att );
//                fc.add( f );
//
//               // double utmZoneCenterLongitude = ...  // Center lon of zone, example: zone 10 = -123
//               // int zoneNumber = ...                 // zone number, example: 10
//               // double latitude, longitude = ...     // lat, lon in degrees
//
//                MathTransformFactory mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
//                ReferencingFactoryContainer factories = new ReferencingFactoryContainer(null);
//
//                GeographicCRS geoCRS = org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;
//                CartesianCS cartCS = org.geotools.referencing.cs.DefaultCartesianCS.GENERIC_2D;
//
//                ParameterValueGroup parameters = mtFactory.getDefaultParameters("Transverse_Mercator");//****************** DEBUG ******************
//                parameters.parameter("central_meridian").setValue(-75.000000);//****************** DEBUG ******************
//                parameters.parameter("latitude_of_origin").setValue(0.0);//****************** DEBUG ******************
//                parameters.parameter("scale_factor").setValue(0.9996);//****************** DEBUG ******************
//                parameters.parameter("false_easting").setValue(500000.0);//****************** DEBUG ******************
//                parameters.parameter("false_northing").setValue(0.0);//****************** DEBUG ******************
//
//                Map properties = Collections.singletonMap("name", "WGS 84 / UTM Zone " + 18);//****************** DEBUG ******************
//                ProjectedCRS projCRS = factories.createProjectedCRS(properties, geoCRS, null, parameters, cartCS);//****************** DEBUG ******************
//
//                MathTransform transform = CRS.findMathTransform(projCRS,DefaultGeographicCRS.WGS84);//****************** DEBUG ******************
//
//                Geometry transformedGeometry = JTS.transform(geom, transform);//****************** DEBUG ******************
//                att[0] = transformedGeometry;//****************** DEBUG ******************
//                f = ft.create( att );                               //****************** DEBUG ******************
//                fc.clear();//****************** DEBUG ******************
//                fc.add( f );//****************** DEBUG ******************
//
//
//                break;
//            }
//            rs.close();
//            cPool.checkIn(conn);
//
//        }
//        catch ( SQLException e ) {
//        	e.printStackTrace();//****************** DEBUG ******************
//            logger.error( "Failed", e ); //$NON-NLS-1$
//        } catch ( ClassNotFoundException e ) {
//            logger.error( "Failed", e ); //$NON-NLS-1$
//        }
//        catch(Exception xe){xe.printStackTrace();}//****************** DEBUG ******************
//
//        return new City( cityCode, cityName, createFeatureDataset( fc ) );
//    }

    public static City getCityFromShapefile( String cityCode ) throws Exception
    {
//        Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.barbados.osgi.Activator.PLUGIN_ID );
//        URL resourceURL = bundle.getResource( "regionShapefile/city.shp" ); //$NON-NLS-1$
//
//        FeatureCollection newfc = FeatureCollections.newCollection();
//
//        FeatureTypeBuilder ftb = FeatureTypeBuilder.newInstance( "region" ); //$NON-NLS-1$
//        ftb.addType( AttributeTypeFactory.newAttributeType( "the_geom", Polygon.class ) ); //$NON-NLS-1$
//        FeatureType ft = ftb.getFeatureType();
//
//        IndexedShapefileDataStore shapefileDS = new IndexedShapefileDataStore( resourceURL );
//
//        FilterFactory ff = FilterFactoryFinder.createFilterFactory();
//
//        AttributeExpression codeField = ff.createAttributeExpression( "CITY_CODE" ); //$NON-NLS-1$
//        LiteralExpression codeExp = ff.createLiteralExpression( Integer.parseInt( cityCode ) );
//        CompareFilter filter = ff.createCompareFilter( CompareFilter.COMPARE_EQUALS );
//        filter.addLeftValue( codeField );
//        filter.addRightValue( codeExp );
//
//        FeatureCollection fc = shapefileDS.getFeatureSource().getFeatures( filter );
//        FeatureIterator iter = fc.features();
//
//        String cityName = ""; //$NON-NLS-1$
//        try {
//            while ( iter.hasNext() ) {
//                Feature feature = iter.next();
//                Geometry geom = (Geometry) feature.getAttribute( "the_geom" ); //$NON-NLS-1$
//                if ( geom instanceof MultiPolygon ) {
//                    MultiPolygon mp = (MultiPolygon) geom;
//                    for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
//                        Object[] att = { mp.getGeometryN( i ) };
//                        Feature f = ft.create( att );
//                        newfc.add( f );
//                    }
//                } else {
//                    Object[] att = { geom };
//                    Feature f = ft.create( att );
//                    newfc.add( f );
//                }
//
//                cityName = (String) feature.getAttribute( "CITY_NAME" ); //$NON-NLS-1$
//                break;
//            }
//        } finally {
//            fc.close( iter );
//        }
//
//        return new City( cityCode, cityName, createFeatureDataset( fc ) );
        Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.tobago.osgi.Activator.PLUGIN_ID );
        URL resourceURL = bundle.getResource( "regionShapefile/city.shp" ); //$NON-NLS-1$

        DefaultFeatureCollection newfc = new DefaultFeatureCollection();

		AttributeTypeBuilder attBuilder = new AttributeTypeBuilder();
		attBuilder.setName("the_geom"); //$NON-NLS-1$
		attBuilder.setBinding(Polygon.class);
		attBuilder.crs(DefaultGeographicCRS.WGS84);
		GeometryType geomType = attBuilder.buildGeometryType();
		GeometryDescriptor geomDesc = attBuilder.buildDescriptor("the_geom", geomType); //$NON-NLS-1$

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("region"); //$NON-NLS-1$
		builder.add(geomDesc);
		//		builder.add("the_geom", Polygon.class); //$NON-NLS-1$

		SimpleFeatureType ft = builder.buildFeatureType();

		ShapefileDataStore shapefileDS = (ShapefileDataStore) DatasetUtils.getShapefileDataStore(resourceURL, false);
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
//		Filter filter = ff.equal(ff.property("CITY_CODE"), ff.literal(cityCode));//$NON-NLS-1$ 
		Filter filter = CQL.toFilter("CITY_CODE = '"+cityCode+"'");
		
		SimpleFeatureCollection fc = shapefileDS.getFeatureSource().getFeatures(filter);
		SimpleFeatureIterator iterator = fc.features();

		String cityName = ""; //$NON-NLS-1$
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geom = (Geometry) feature.getAttribute("the_geom"); //$NON-NLS-1$
				if (geom instanceof MultiPolygon) {
					MultiPolygon mp = (MultiPolygon) geom;
					for (int i = 0; i < mp.getNumGeometries(); i++) {
						Object[] att = { mp.getGeometryN(i) };
						SimpleFeature f = SimpleFeatureBuilder.build(ft, att, null);
						newfc.add(f);
					}
				} else {
					Object[] att = { geom };
					SimpleFeature f = SimpleFeatureBuilder.build(ft, att, null);
					newfc.add(f);
				}

				cityName = (String) feature.getAttribute("CITY_NAME"); //$NON-NLS-1$
				break;
			}
		} finally {
			iterator.close();
		}

		return new City(cityCode, cityName, createFeatureDataset(fc));
    }


//    /**
//     * Returns all EnumerationDistrict objects for city code string
//     * @param cityCode
//     * @return
//     */
//    public static List<EnumerationDistrict> getEnumerationDistrictsFromPostGIS( String cityCode ) throws Exception{
//
//        List<EnumerationDistrict> edList = new LinkedList<EnumerationDistrict>();
//        WKTReader wktReader = new WKTReader();
//
//        FeatureCollection fc = FeatureCollections.newCollection();
//
//        FeatureTypeBuilder ftb = FeatureTypeBuilder.newInstance( "region" ); //$NON-NLS-1$
//        ftb.addType( (GeometryAttributeType) AttributeTypeFactory.newAttributeType( "the_geom", Polygon.class ) ); //$NON-NLS-1$
//        //        ftb.addType( AttributeTypeFactory.newAttributeType( "ctyfips", String.class, true, 15, null ) ); //$NON-NLS-1$
//        //        ftb.addType( AttributeTypeFactory.newAttributeType( "state", String.class, true, 2, null ) ); //$NON-NLS-1$
//        //        ftb.addType( AttributeTypeFactory.newAttributeType( "name", String.class, true, 15, null ) ); //$NON-NLS-1$
//
//        FeatureType ft = ftb.getFeatureType();
//        try {
//        	JDBCConnectionPool cPool = getJDBCConnectionPool();
//        	Connection conn = (Connection)cPool.checkOut();
//
//        	SelectStatement selectQuery = new SelectStatement();
//        	selectQuery.addTableName("electoral_district");
//        	selectQuery.addSelectColumn("ASTEXT(electoral_district.the_geom)", "the_geom");
//        	selectQuery.addSelectColumn("electoral_district.ed_code");
//        	selectQuery.addSelectColumn("city_code");
//        	selectQuery.addSelectColumn("district_name");
//        	selectQuery.addSearchParameter(new SearchParameter("roi_id", Barbados.REGION_ID, false));
//        	selectQuery.addSearchElement(SearchConnector.AND_CONNECTOR);
//        	selectQuery.addSearchParameter(new SearchParameter("city_code", cityCode, false));
//        	selectQuery.setOrderBy(new OrderByClause("district_name"));
//            PreparedStatement st = conn.prepareStatement(selectQuery.toString());;
//            logger.debug( st.toString() );
//            ResultSet rs = st.executeQuery();
//            while ( rs.next() ) {
//                String distcode = rs.getString( 2 );
//                String city_code = rs.getString( 3 );
//                String name = rs.getString( 4 );
//
//                Geometry tmpGeom = wktReader.read( rs.getString( 1 ) );
//                Geometry geom = tmpGeom.getGeometryN( 0 );
//
//                Object[] att = { geom };
//                Feature f = ft.create( att );
//                fc.add( f );
//
//                FeatureCollection fc2 = FeatureCollections.newCollection();
//                fc2.add(fc2);
//                EnumerationDistrict c = new EnumerationDistrict( distcode, city_code, name, createFeatureDataset(fc2));
//                edList.add( c );
//            }
//            rs.close();
//           cPool.checkIn(conn);
//
//        } catch ( SQLException e1 ) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch ( ClassNotFoundException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }catch(Exception ex){
//        	ex.printStackTrace();
//        }
//
//        return edList;
//
//    }

    public static List<EnumerationDistrict> getEnumerationDistrictsFromCSV( String aCityCode )
    {
        Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.tobago.osgi.Activator.PLUGIN_ID );
        URL resourceURL = bundle.getResource( "regionCode/listDistrict.csv" ); //$NON-NLS-1$

        List<EnumerationDistrict> districtList = new LinkedList<EnumerationDistrict>();

        InputStream source;
        try {
            source = resourceURL.openStream();
            BufferedReader bufRdr = new BufferedReader( new InputStreamReader( source ) );
            String line = null;

            line = bufRdr.readLine();
            //          read each line of text file
            while ( (line = bufRdr.readLine()) != null ) {
            	if(line.trim().length() == 0)
            		continue;
                StringTokenizer st = new StringTokenizer( line, "," ); //$NON-NLS-1$

                String districtCode = st.nextToken(); // District code
                String cityCode = st.nextToken();

                if ( cityCode.matches( aCityCode ) ) {
                	EnumerationDistrict c = new EnumerationDistrict( districtCode, cityCode, districtCode, null );
                	//EnumerationDistrict c = getEnumerationDistrictFromShapefile(districtCode, cityCode);
                    districtList.add( c );
                }

            }
            return districtList;
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;

    }


//    /**
//     * Returns EnumerationDistrict object for ED string code
//     * @param distCode
//     * @return
//     * @throws Exception
//     */
//    @SuppressWarnings("unchecked")
//    public static EnumerationDistrict getEnumerationDistrictFromPostGIS( String distCode, String cityCode ) throws Exception{
//        WKTReader wktReader = new WKTReader();
//
//        FeatureCollection fc = FeatureCollections.newCollection();
//
//        FeatureTypeBuilder ftb = FeatureTypeBuilder.newInstance( "region" ); //$NON-NLS-1$
//        ftb.addType( (GeometryAttributeType) AttributeTypeFactory.newAttributeType( "the_geom", Polygon.class ) ); //$NON-NLS-1$
//        //        ftb.addType( AttributeTypeFactory.newAttributeType( "ctyfips", String.class, true, 15, null ) ); //$NON-NLS-1$
//        //        ftb.addType( AttributeTypeFactory.newAttributeType( "state", String.class, true, 2, null ) ); //$NON-NLS-1$
//        //        ftb.addType( AttributeTypeFactory.newAttributeType( "name", String.class, true, 15, null ) ); //$NON-NLS-1$
//
//        FeatureType ft = ftb.getFeatureType();
//
//        String distcode = null;
//        String city_code = null;
//        String name = null;
//
//        try {
//        	JDBCConnectionPool cPool = getJDBCConnectionPool();
//        	Connection conn = (Connection)cPool.checkOut();
//
//        	SelectStatement selectQuery = new SelectStatement();
//        	selectQuery.addTableName("electoral_district");
//        	selectQuery.addSelectColumn("ASTEXT(electoral_district.the_geom)", "the_geom");
//        	selectQuery.addSelectColumn("electoral_district.ed_code");
//        	selectQuery.addSelectColumn("city_code");
//        	selectQuery.addSearchParameter(new SearchParameter("roi_id", Barbados.REGION_ID, false));
//        	selectQuery.addSearchElement(SearchConnector.AND_CONNECTOR);
//        	selectQuery.addSearchParameter(new SearchParameter("electoral_district.ed_code", distCode, false));
//        	selectQuery.addSearchElement(SearchConnector.AND_CONNECTOR);
//        	selectQuery.addSearchParameter(new SearchParameter("electoral_district.city_code", cityCode, false));
//            PreparedStatement st = conn.prepareStatement(selectQuery.toString());;
//
//            logger.debug( st.toString() );
//            ResultSet rs = st.executeQuery();
//            while ( rs.next() ) {
//                Geometry tmpGeom = wktReader.read( rs.getString( 1 ) );
//                Geometry geom = tmpGeom.getGeometryN( 0 );
//                distcode = rs.getString( 2 );
//                city_code = rs.getString( 3 );
//                name = rs.getString( 2 );
//                //  Object[] att = { geom, distcode, city_code, name };
//                Object[] att = { geom };
//                Feature f = ft.create( att );
//                fc.add( f );
//            }
//            rs.close();
//           cPool.checkIn(conn);
//        } catch ( SQLException e ) {
//            logger.error( "Failed", e ); //$NON-NLS-1$
//        } catch ( ClassNotFoundException e ) {
//            logger.error( "Failed", e ); //$NON-NLS-1$
//        }catch(Exception ex){
//        	ex.printStackTrace();
//        }
//
//        return new EnumerationDistrict( distcode, city_code, name, createFeatureDataset( fc ) );
//    }


    /**
     *
     * Returning Parishes for this region, ED's too small
     *
     * @param distCode
     * @param cityCode
     * @return
     * @throws Exception
     */
    public static EnumerationDistrict getEnumerationDistrictFromShapefile( String distCode, String cityCode ) throws Exception
    {

//        if(districtsFC == null){
//            Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.barbados.osgi.Activator.PLUGIN_ID );
//            URL resourceURL = bundle.getResource( "regionShapefile/parishes.shp" ); //$NON-NLS-1$
//            IndexedShapefileDataStore shapefileDS = new IndexedShapefileDataStore( resourceURL );
//            districtsFC = shapefileDS.getFeatureSource().getFeatures();
//        }
//
//        FeatureCollection newfc = FeatureCollections.newCollection();
//
//        FeatureTypeBuilder ftb = FeatureTypeBuilder.newInstance( "region" ); //$NON-NLS-1$
//        ftb.addType( AttributeTypeFactory.newAttributeType( "the_geom", Polygon.class ) ); //$NON-NLS-1$
//        FeatureType ft = ftb.getFeatureType();
//
//
//        FilterFactory ff = FilterFactoryFinder.createFilterFactory();
//
//        AttributeExpression codeField = ff.createAttributeExpression( "ED_CODE" ); //$NON-NLS-1$
//        LiteralExpression codeExp = ff.createLiteralExpression( distCode );
//        CompareFilter filter = ff.createCompareFilter( CompareFilter.COMPARE_EQUALS );
//        filter.addLeftValue( codeField );
//        filter.addRightValue( codeExp );
//
//        AttributeExpression codeField_c = ff.createAttributeExpression( "CITY_CODE" ); //$NON-NLS-1$
//        LiteralExpression codeExp_c = ff.createLiteralExpression( cityCode );
//        CompareFilter filter_c = ff.createCompareFilter( CompareFilter.COMPARE_EQUALS );
//        filter_c.addLeftValue( codeField_c );
//        filter_c.addRightValue( codeExp_c );
//
//
//        //System.out.println("FILTER IS: "+filter.and(filter_c));
//
//
//        FeatureCollection fc = districtsFC.subCollection(filter.and(filter_c) );
//        FeatureIterator iter = fc.features();
//
//        String distName = ""; //$NON-NLS-1$
//        try {
//            while ( iter.hasNext() ) {
//                Feature feature = iter.next();
//                Geometry geom = (Geometry) feature.getAttribute( "the_geom" ); //$NON-NLS-1$
//
//                if ( geom instanceof MultiPolygon ) {
//                    MultiPolygon mp = (MultiPolygon) geom;
//                    for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
//                        Object[] att = { mp.getGeometryN( i ) };
//                        Feature f = ft.create( att );
//                        newfc.add( f );
//                    }
//                } else {
//                    Object[] att = { geom };
//                    Feature f = ft.create( att );
//                    newfc.add( f );
//                }
//
//                distName = (String) feature.getAttribute( "ED_CODE" ); //$NON-NLS-1$
//                break;
//            }
//        } finally {
//            fc.close( iter );
//        }
    	
	    Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.tobago.osgi.Activator.PLUGIN_ID );
	    URL resourceURL = bundle.getResource( "regionShapefile/enumeration_districts.shp" ); //$NON-NLS-1$
		
        DefaultFeatureCollection newfc = new DefaultFeatureCollection();

		AttributeTypeBuilder attBuilder = new AttributeTypeBuilder();
		attBuilder.setName("the_geom"); //$NON-NLS-1$
		attBuilder.setBinding(Polygon.class);
		attBuilder.crs(DefaultGeographicCRS.WGS84);
		GeometryType geomType = attBuilder.buildGeometryType();
		GeometryDescriptor geomDesc = attBuilder.buildDescriptor("the_geom", geomType); //$NON-NLS-1$

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("region"); //$NON-NLS-1$
		builder.add(geomDesc);
		//		builder.add("the_geom", Polygon.class); //$NON-NLS-1$
		SimpleFeatureType ft = builder.buildFeatureType();

		ShapefileDataStore shapefileDS = (ShapefileDataStore) DatasetUtils.getShapefileDataStore(resourceURL, false);
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
//		Filter filter = ff.equal(ff.property("ED_CODE"), ff.literal(distCode));//$NON-NLS-1$ 
		Filter filter = CQL.toFilter("ED_CODE = '"+distCode+"'");
		
		SimpleFeatureCollection fc = shapefileDS.getFeatureSource().getFeatures(filter);
		SimpleFeatureIterator iterator = fc.features();

		String distName = ""; //$NON-NLS-1$
//		String cityCode = distCode.substring(0, 2);
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geom = (Geometry) feature.getAttribute("the_geom"); //$NON-NLS-1$

				if (geom instanceof MultiPolygon) {
					MultiPolygon mp = (MultiPolygon) geom;
					for (int i = 0; i < mp.getNumGeometries(); i++) {
						Object[] att = { mp.getGeometryN(i) };
						SimpleFeature f = SimpleFeatureBuilder.build(ft, att, null);
						newfc.add(f);
					}
				} else {
					Object[] att = { geom };
					SimpleFeature f = SimpleFeatureBuilder.build(ft, att, null);
					newfc.add(f);
				}

				distName = (String) feature.getAttribute("ED_CODE"); //$NON-NLS-1$
				break;
			}
		} finally {
			iterator.close();
		}


        //System.out.println("ED size: "+fc.size());
        return new EnumerationDistrict( distCode, cityCode, distName, createFeatureDataset( fc ) );
    }


//    /**
//     * Returns ALL EnumerationDistrict data objects for this region WHICH ARE PARISHES. EDs too small
//     * @return
//     * @throws Exception
//     */
//    public static List<EnumerationDistrict> getAllEnumerationDistrictsFromPostGIS() throws Exception{
//
//    	List<EnumerationDistrict> edList = new LinkedList<EnumerationDistrict>();
//        WKTReader wktReader = new WKTReader();
//
//        String distcode = null;
//        String city_code = null;
//        String name = null;
//
//        try {
//        	JDBCConnectionPool cPool = getJDBCConnectionPool();
//        	Connection conn = (Connection)cPool.checkOut();
//
//        	SelectStatement selectQuery = new SelectStatement();
//        	selectQuery.addTableName("electoral_district");
//        	selectQuery.addSelectColumn("ASTEXT(electoral_district.the_geom)", "the_geom");
//        	selectQuery.addSelectColumn("electoral_district.ed_code");
//        	selectQuery.addSelectColumn("city_code");
//        	selectQuery.addSearchParameter(new SearchParameter("roi_id", Barbados.REGION_ID, false));
//            PreparedStatement st = conn.prepareStatement(selectQuery.toString());;
//
//            logger.debug( st.toString() );
//            ResultSet rs = st.executeQuery();
//            while ( rs.next() ) {
//                Geometry tmpGeom = wktReader.read( rs.getString( 1 ) );
//                Geometry geom = tmpGeom.getGeometryN( 0 );
//                distcode = rs.getString( 2 );
//                city_code = rs.getString( 3 );
//                name = rs.getString( 2 );
//
//                edList.add( new EnumerationDistrict( distcode, city_code, name, null ) );
//            }
//            rs.close();
//           cPool.checkIn(conn);
//        } catch ( SQLException e ) {
//            logger.error( "Failed", e ); //$NON-NLS-1$
//        } catch ( ClassNotFoundException e ) {
//            logger.error( "Failed", e ); //$NON-NLS-1$
//        }catch(Exception ex){
//        	ex.printStackTrace();
//        }
//
//    	return edList;
//    }

    @SuppressWarnings("unchecked")
    private static FeatureDataset createFeatureDataset( Feature f )
    {
        try {
			DefaultFeatureCollection c = new DefaultFeatureCollection();
			c.add((SimpleFeature) f);

			CollectionDataStore ds = new CollectionDataStore(c);
			SimpleFeatureSource fs = ds.getFeatureSource(ds.getTypeNames()[0]);

            FeatureDataset dataset = new FeatureDataset();
            dataset.setDataId( null );
            dataset.setFeatureGeometryType( GISConstants.POLYGON );
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
        	CollectionDataStore ds = new CollectionDataStore(fc);
			SimpleFeatureSource fs = ds.getFeatureSource(ds.getTypeNames()[0]);
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
    public static City getCityForEnumerationDistrictsFromCSV( String targetDistrictCode ){
    	Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.tobago.osgi.Activator.PLUGIN_ID );
        URL resourceURL = bundle.getResource( "regionCode/listDistrict.csv" ); //$NON-NLS-1$

        InputStream source;
        try {
            source = resourceURL.openStream();
            BufferedReader bufRdr = new BufferedReader( new InputStreamReader( source ) );
            String line = null;

            line = bufRdr.readLine();
            //          read each line of text file
            while ( (line = bufRdr.readLine()) != null ) {
            	if(line.trim().length() == 0)
            		continue;
                StringTokenizer st = new StringTokenizer( line, "," ); //$NON-NLS-1$

                String districtCode = st.nextToken(); // District code
                String cityCode = st.nextToken();

                if ( targetDistrictCode.matches( districtCode ) ) {
                	Property p = new Property();
                	p.setValue(cityCode);
                	City targetCity = new City(p);
                	bufRdr.close();
                	return targetCity;
                }
                
            }
            bufRdr.close();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;

    }
//    private static JDBCConnectionPool getJDBCConnectionPool() throws Exception{
//		Bundle bundle = Platform.getBundle("com.uwiseismic.db");
//		Class cPoolClass = bundle.loadClass("com.uwiseismic.db.JDBCConnectionPool");
//		Method method = cPoolClass.getMethod("getInstance");
//		return (JDBCConnectionPool) method.invoke(null);
//    } 
}
