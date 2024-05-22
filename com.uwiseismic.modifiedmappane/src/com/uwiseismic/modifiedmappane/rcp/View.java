package com.uwiseismic.modifiedmappane.rcp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JToolBar;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.uwiseismic.modifiedmappane.rcp.map.JMapPaneRenderer;

/**
 * Initialize RCP components and displays view to user.
 * @author Sterling Ramroach (UWI)
 */
public class View extends ViewPart implements ISelectionProvider {
	public static final String ID = "com.uwiseismic.modifiedmappane.View";


	private JMapPaneRenderer mapPane;
	private Frame frame;

	SampleSelectionProviderAdapter selectionProviderAdapter = new SampleSelectionProviderAdapter();

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Composite swtAwtComponent = new Composite(parent, SWT.EMBEDDED|SWT.BACKGROUND);

		frame = SWT_AWT.new_Frame(swtAwtComponent);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(screenSize.width, screenSize.height);
		mapPane = new JMapPaneRenderer();

		frame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				mapPane.setSize(frame.getWidth(),frame.getHeight());
				try {
					//When the view or window is resized, reload all the components of the mapPane
					load();
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

		try {
			load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds layer to MapContent.
	 * Initializes mapPane and adds MapContent to mapPane.
	 * Adds the JToolbar and mapPane to frame.
	 * @throws Exception
	 */
	public void load() throws Exception {
		URL shape = new URL("file:///C:/Users/Sterls-PC/Desktop/WORK/Workspace2/org.caribbean.rcp/data/KGN/kgn_casualties_hospital_corrected1.shp");

		File file = new File(shape.toURI());
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


		mapC.addLayer(layer);      

		mapPane.setMapContent(mapC);
		mapPane.setName(fs.getName().toString());
		mapPane.init(mapC,frame.getWidth(),frame.getHeight());
		mapPane.setVisible(true);
		mapPane.setState(JMapPaneRenderer.Reset);

		JToolBar jtb = mapPane.createToolBar();
		jtb.setVisible(true);
		frame.add(jtb,BorderLayout.NORTH);
		frame.add(mapPane,BorderLayout.CENTER);

		frame.setBackground(Color.WHITE);
		frame.validate();
		frame.setVisible(true);

	}

	/**
	 * This class handles the selection events. 
	 */
	private class SampleSelectionProviderAdapter implements ISelectionProvider {

		List<ISelectionChangedListener> listeners = new ArrayList();

		ISelection theSelection = StructuredSelection.EMPTY;

		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		public ISelection getSelection() {
			return theSelection;
		}

		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			theSelection = selection;
			final SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
			Object[] listenersArray = listeners.toArray();

			for (int i = 0; i < listenersArray.length; i++) {
				final ISelectionChangedListener l = (ISelectionChangedListener) listenersArray[i];
				SafeRunner.run(new SafeRunnable() {
					public void run() {
						l.selectionChanged(e);
					}
				});
			}
		}

	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionProviderAdapter.addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection() {
		return selectionProviderAdapter.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionProviderAdapter.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		selectionProviderAdapter.setSelection(selection);
	}

	@Override
	public void setFocus() {

	}

}