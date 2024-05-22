package edu.psu.ems.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;

import edu.psu.ems.gis2defnode.geotools.EMSModifiedMapPane;

public class TestMapPane {

	public static void main(String[] args) {

		MapContent map = new MapContent();
	    EMSModifiedMapPane mapPane = new EMSModifiedMapPane(map);	 

	    GTRenderer renderer = new StreamingRenderer();

	    FeatureLayer layer =  null;
	    try{
			StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
			FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
		    
		    URL shapefileURL = new File("C:\\temp\\nkgn\\New_Kingston_WGS84\\New_Kingston.shp").toURI().toURL();
		    
		    DataStore store = FileDataStoreFinder.getDataStore(shapefileURL);
		    String names[] = store.getTypeNames();
		    FeatureSource featureSource = store.getFeatureSource(names[0]);           
		    FeatureCollection features = featureSource.getFeatures();
		//            CoordinateReferenceSystem csr = featureSource.getSchema().getCoordinateReferenceSystem();
		    
		    Stroke stroke = styleFactory.createStroke(
		            filterFactory.literal(Color.BLUE),
		            filterFactory.literal(1));
		
		    /*
		     * Setting the geometryPropertyName arg to null signals that we want to
		     * draw the default geomettry of features
		     */
		    LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);
		
		    Rule rule = styleFactory.createRule();
		    rule.symbolizers().add(sym);
		    FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
		    Style style = styleFactory.createStyle();
		    style.featureTypeStyles().add(fts);
		    
		    layer = new FeatureLayer(features, style);
//		    mapPane.addLayer(layer);

	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }
	    
	    map.addLayer(layer);
	    
	    mapPane = new EMSModifiedMapPane(map);
	    mapPane.setRenderer(renderer);
	    
	    JFrame frame = new JFrame("FrameDemo");
	    
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	  frame.getContentPane().add(mapPane, BorderLayout.CENTER);

	  

	  frame.pack();
	  frame.setVisible(true);
	  
	  frame.setSize(new Dimension(800, 800));

	}

}
