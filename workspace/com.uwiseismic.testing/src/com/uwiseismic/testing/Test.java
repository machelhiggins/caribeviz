package com.uwiseismic.testing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.swt.awt.SWT_AWT;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

//import com.uwiseismic.modifiedmappane.rcp.PolygonStyleTwo;
//import com.uwiseismic.modifiedmappane.rcp.map.JMapPaneRenderer;

import edu.illinois.ncsa.ergo.gis.geotools.ui.internal.JMapPaneRenderer;

public class Test {

	public static void main(String[] args) {
		try{
			File file = new File("C:\\Users\\Machel\\Desktop\\1_aaa\\KMA BC Full w Fake Shelters.shp");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("url", file.toURI().toURL());
	
			DataStore dataStore = DataStoreFinder.getDataStore(map);
			String typeName = dataStore.getTypeNames()[0];
	
			FeatureSource<SimpleFeatureType, SimpleFeature> fs = dataStore
					.getFeatureSource(typeName);
	
			Filter filter = Filter.INCLUDE; 
			FeatureCollection<SimpleFeatureType, SimpleFeature> collection = fs.getFeatures(filter);
			CoordinateReferenceSystem crs = fs.getSchema().getCoordinateReferenceSystem();
	
			if (crs == null)crs = DefaultGeographicCRS.WGS84;
	
			MapContent mapC = new MapContent();		
			FeatureLayer layer = new FeatureLayer(collection,PolygonStyleTwo.getStructureTypeStyle());
	
	
			file = new File("C:\\Users\\Machel\\OneDrive\\DRRC\\CaribEViz\\dev_share\\jamaica_data_mobile\\KMA_ED_unimproved_land_per_sqft_values9.shp");
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("url", file.toURI().toURL());
	
			DataStore dataStore2 = DataStoreFinder.getDataStore(map2);
			String typeName2 = dataStore2.getTypeNames()[0];
	
			FeatureSource<SimpleFeatureType, SimpleFeature> fs2 = dataStore2
					.getFeatureSource(typeName2);
	
//			Filter filter = Filter.INCLUDE; 
			FeatureCollection<SimpleFeatureType, SimpleFeature> collection2 = fs2.getFeatures(filter);
			CoordinateReferenceSystem crs2 = fs2.getSchema().getCoordinateReferenceSystem();
			Style style = SLD.createSimpleStyle(fs2.getSchema());
			if (crs2 == null)crs2 = DefaultGeographicCRS.WGS84;
			FeatureLayer layer2 = new FeatureLayer(collection2,style);
			
			 
			
			mapC.addLayer(layer);      
			mapC.addLayer(layer2);    
			
			JMapPaneRenderer mapPane;
			JFrame frame;
			
			frame = new JFrame("JMapPaneRenderer Test");
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setSize(screenSize.width, screenSize.height);
			mapPane = new JMapPaneRenderer();

			frame.addComponentListener(new ComponentListener() {
				public void componentResized(ComponentEvent e) {
					mapPane.setSize(frame.getWidth(),frame.getHeight());
					try {
						//When the view or window is resized, reload all the components of the mapPane
//						load();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
				@Override
				public void componentMoved(ComponentEvent e) {}

				@Override
				public void componentShown(ComponentEvent e) {}

				@Override
				public void componentHidden(ComponentEvent e) {}
			});
			
	
//            mapPane.setRenderer(new StreamingRenderer());
            
//            JFrame mainFrame = new JFrame();
//            frame.setSize(new Dimension(1000, 1000));
            JPanel dummy = new JPanel();
            
            mapPane.setPreferredSize(dummy.getPreferredSize());
                       
            mapPane.setMapContent(mapC);
            dummy.setLayout(new FlowLayout());
            dummy.add(mapPane);
            dummy.validate();
            frame.getContentPane().add(dummy);
            frame.validate();
            frame.setVisible(true);
            
            try{Thread.sleep(1000*10);}catch(Exception ex){ex.printStackTrace();}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}

}
