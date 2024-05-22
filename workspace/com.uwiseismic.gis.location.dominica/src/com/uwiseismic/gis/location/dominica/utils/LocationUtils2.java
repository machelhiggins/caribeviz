package com.uwiseismic.gis.location.dominica.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Platform;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFinder;
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

import edu.illinois.ncsa.ergo.gis.GISConstants;
import edu.illinois.ncsa.ergo.gis.datasets.FeatureDataset;
import edu.illinois.ncsa.ergo.gis.datasets.Messages;
import edu.illinois.ncsa.ergo.gis.osgi.Activator;
import edu.illinois.ncsa.ergo.gis.util.DatasetUtils;
import ncsa.tools.common.types.Property;

//import com.uwiseismic.db.JDBCConnectionPool;
//import com.uwiseismic.db.pgsql.GeometryColumn;
//import com.uwiseismic.db.sql.SearchConnector;
//import com.uwiseismic.db.sql.SearchParameter;
//import com.uwiseismic.db.sql.SelectStatement;
import com.uwiseismic.gis.location.dominica.City;
import com.uwiseismic.gis.location.dominica.Dominica;
import com.uwiseismic.gis.location.dominica.EnumerationDistrict;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

public class LocationUtils2
{
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( LocationUtils2.class );

    
    @SuppressWarnings("unchecked")
    private static FeatureDataset createFeatureDataset( Feature f )
    {
    	try {
			DefaultFeatureCollection c = new DefaultFeatureCollection();
			c.add((SimpleFeature) f);

			CollectionDataStore ds = new CollectionDataStore(c);
			SimpleFeatureSource fs = (SimpleFeatureSource) ds.getFeatureSource(ds.getTypeNames()[0]);

			FeatureDataset dataset = new FeatureDataset();
			dataset.setDataId(null);
			dataset.setFeatureGeometryType(GISConstants.POLYGON);
			dataset.setFeatureSource(fs);
			dataset.setFriendlyName(Messages.getString("LocationUtils2.0")); //$NON-NLS-1$
			// dataset.setStyle( GeometryUtil.getDefaultStyle( GISConstants.POLYGON ) );
			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			URL resourceURL = bundle.getResource("gisStyles/gis-boundary_1.0.sld"); //$NON-NLS-1$
			StyleFactory factory = StyleFactoryFinder.createStyleFactory();
			SLDParser parser = new SLDParser(factory, resourceURL);
			Style[] style = parser.readXML();
			dataset.setStyle(style[0]);
			dataset.setTypeId(GISConstants.REGION_OF_INTEREST);

			return dataset;
		} catch (Throwable t) {
			logger.error("Failed to create dataset: " + t.getMessage()); //$NON-NLS-1$
			return null;
		}
    }

    private static FeatureDataset createFeatureDataset( FeatureCollection fc )
    {
    	try {
			CollectionDataStore ds = new CollectionDataStore(fc);
			SimpleFeatureSource fs = (SimpleFeatureSource) ds.getFeatureSource(ds.getTypeNames()[0]);
			logger.debug(fs.getSchema().getTypeName());
			FeatureDataset dataset = new FeatureDataset();
			dataset.setDataId(null);
			dataset.setFeatureGeometryType(GISConstants.POLYGON);
			dataset.setFeatureSource(fs);
			dataset.setFriendlyName(Messages.getString("LocationUtils2.1")); //$NON-NLS-1$
			// dataset.setStyle( GeometryUtil.getDefaultStyle( GISConstants.POLYGON ) );

			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			URL resourceURL = bundle.getResource("gisStyles/gis-boundary_1.0.sld"); //$NON-NLS-1$

			StyleFactory factory = StyleFactoryFinder.createStyleFactory();
			SLDParser parser = new SLDParser(factory, resourceURL);
			Style[] style = parser.readXML();
			dataset.setStyle(style[0]);
			dataset.setTypeId(GISConstants.REGION_OF_INTEREST);

			return dataset;
		} catch (Throwable t) {
			if (t.getMessage() != null)
				logger.error("Failed to create dataset: " + t.getMessage()); //$NON-NLS-1$
			else
				logger.error("Failed", t); //$NON-NLS-1$

			return null;
		}
    }
    
    /**
     * @return
     */
    public static List<City> getCitiesFromCSV()
    {
        Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.dominica.osgi.Activator.PLUGIN_ID );
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
    
    public static City getCityFromShapefile( String cityCode ) throws Exception
    {
        Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.dominica.osgi.Activator.PLUGIN_ID );
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
    
    
    public static EnumerationDistrict getEnumerationDistrictFromShapefile( String distCode, String cityCode ) throws Exception
    {

		Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.dominica.osgi.Activator.PLUGIN_ID );
		URL resourceURL = bundle.getResource("regionShapefile/electoral_district.shp"); //$NON-NLS-1$
		
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

		return  new EnumerationDistrict( distCode, cityCode, distName, createFeatureDataset( fc ) );
    }
    
    public static List<EnumerationDistrict> getEnumerationDistrictsFromCSV( String aCityCode )
    {
        Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.dominica.osgi.Activator.PLUGIN_ID );
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
    public static City getCityForEnumerationDistrictsFromCSV( String targetDistrictCode ){
    	Bundle bundle = Platform.getBundle( com.uwiseismic.gis.location.dominica.osgi.Activator.PLUGIN_ID );
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
}
