package edu.psu.ems.gis2defnode.ui.handlers;

import java.awt.Color;
import java.io.File;
import java.net.URL;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;

import edu.psu.ems.gis2defnode.MapView;
import edu.psu.ems.gis2defnode.geotools.EMSModifiedMapPane;
import edu.psu.ems.test.WatchDogMapPaneShow;

public class OpenShapefile extends org.eclipse.core.commands.AbstractHandler {
	
	private StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
	private FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
	
	public void execute(){
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get view 
			MapView ourView = (MapView)PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage().findView(MapView.ID);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			
			EMSModifiedMapPane mapPane = ourView.getMapPane();
			if(mapPane.getConnectionLayer() != null){				
				 MessageDialog dialog = new MessageDialog(
					      null, "A node connections layer exists. Continue?", null, "Question",
					      MessageDialog.QUESTION,
					      new String[] {"Yes", "No"},0);
				 
				 if(dialog.getReturnCode() == 1)
					 return null;
			}
			
			//Create a file chooser
//			final JFileChooser fc = new JFileChooser();
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			dialog.setFilterExtensions(new String [] {"*.shp"});
//			dialog.setFilterPath(".");
			String shapefilename = dialog.open();
			if( shapefilename != null){
				System.out.println("Using file "+shapefilename);
				try{
					URL shapefileURL = new File(shapefilename).toURI().toURL();
					if(shapefileURL == null) {
						MessageBox errMsg = new MessageBox(shell,
								IMessageProvider.ERROR);
						errMsg.setText("Shapefile error");
						errMsg.setMessage("Selected shapefile is not valid.");
						errMsg.open();
						return null;
					}
		
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
		            
		            FeatureLayer layer = new FeatureLayer(features, style);
		
			        mapPane.setOrginalLayer(layer);
//			        mapPane.show();
			        mapPane.setVisible(true);
			        
				}catch(Exception ex){
					MessageBox errMsg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							IMessageProvider.ERROR);
					errMsg.setText("Shapefile error");
					errMsg.setMessage("Encountered an error opening shapefile.");
					errMsg.open();
					ex.printStackTrace();
				}
			        		      				
			}
		return null;
	}
	
}
