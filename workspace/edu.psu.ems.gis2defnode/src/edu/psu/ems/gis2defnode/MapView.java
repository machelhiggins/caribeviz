package edu.psu.ems.gis2defnode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
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
import edu.psu.ems.test.WatchDogMapPaneShow;

public class MapView extends ViewPart {


	public static final String ID = "edu.psu.ems.gis2defnode.MapView";
	private Composite mapComposite;
	private EMSModifiedMapPane mapPane;

	@Override
	public void createPartControl(Composite parent) {
		System.out.println("Starting create part control");

	 // Create a MapContent instance and add one or more layers to it
	    MapContent map = new MapContent();


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
	    System.out.println("Adding layer to mapcontent");
	    map.addLayer(layer);

	    mapPane = new EMSModifiedMapPane(map);
	    mapPane.setRenderer(renderer);
	    	
		
		mapComposite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		mapComposite.setLayout(new RowLayout());
		java.awt.Frame frame = SWT_AWT.new_Frame(mapComposite);

	    JFrame jframe = new JFrame();

//	    jframe.getContentPane().add(mapPane, BorderLayout.CENTER);
	    System.out.println("Adding jlabel to jframe");
	    jframe.getContentPane().add(new JLabel("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"), BorderLayout.CENTER);
	    jframe.pack();
	    jframe.setVisible(true);

	    System.out.println("Adding jframe to composite frame");
	    frame.add(jframe);
	    frame.setSize(new Dimension(800, 800));

	    new WatchDogMapPaneShow(mapPane).start();



	}

	public EMSModifiedMapPane getMapPane() {
		return mapPane;
	}



	public void setMapPane(EMSModifiedMapPane mapPane) {
		this.mapPane = mapPane;
	}



	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//viewer.getControl().setFocus();
		mapComposite.setFocus();
	}

}
