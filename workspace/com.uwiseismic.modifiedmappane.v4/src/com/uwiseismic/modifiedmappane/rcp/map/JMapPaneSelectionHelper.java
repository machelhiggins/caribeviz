/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Contributors:
 * Nathan Tolbert - initial API and implementation and/or initial documentation
 *******************************************************************************/

package com.uwiseismic.modifiedmappane.rcp.map;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.IllegalFilterException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.swing.event.MapMouseEvent;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.BoundingBox;

import com.uwiseismic.modifiedmappane.event.FeatureAttributeAdapter;
import com.uwiseismic.modifiedmappane.event.FeatureAttributeListener;

import edu.illinois.ncsa.ergo.gis.GISConstants;
import edu.illinois.ncsa.ergo.gis.datasets.RenderableDataset;
import edu.illinois.ncsa.ergo.gis.types.DatasetLayer;
import edu.illinois.ncsa.ergo.gis.types.SelectedDatasetItems;
import edu.illinois.ncsa.ergo.gis.types.SelectedFeatures;
import edu.illinois.ncsa.ergo.gis.util.SchemaUtils;

/**
 * Handles all the selection events from the JMapPaneRenderer.
 */
public class JMapPaneSelectionHelper implements FeatureAttributeAdapter
{
	private static final Color SELECTED_COLOUR = Color.CYAN;
	private static final Color SELECTED_OUTLINE_COLOUR = Color.RED;
	private static final float OPACITY = 1.0f;
	private static final float LINE_WIDTH = 2.0f;
	private static final float POINT_SIZE = 10.0f;

	FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
	StyleFactory sf = CommonFactoryFinder.getStyleFactory();

	private JMapPaneRenderer mapPane;

	@SuppressWarnings("rawtypes")
	private FeatureSource featureSource;

	private ISelectionProvider selectionProvider;

	//Track which features were selected (to highlight them).
	Set<FeatureId> IDs = new HashSet<FeatureId>();
	Set<Feature> feat = new HashSet<Feature>();

	DefaultFeatureCollection selectedCollection;

	public JMapPaneSelectionHelper(JMapPaneRenderer mapFrame)
	{
		this.mapPane = mapFrame;

	}

	public Layer getSelectedLayer()
	{
		return mapPane.getSelectedLayer();
	}

	public void clearSelectedItems(){
		//This shouldn't be necessary, but without it, the necessary repaint isn't done.
		reStyleChangedStructType();
		IDs.clear();
		feat.clear();
//		mapPane.forceRedraw();
//		mapPane.repaint();
		
	}

	/**
	 * Create a new thread to handle the pop up dialog which allows the user to 
	 * edit attributes of selected features.
	 */
	public void editFeature(){
		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		PopUpEditForm p = new PopUpEditForm(activeShell);

		if(this.getSelectedLayer() == null){
			return;
		}
		if(IDs.isEmpty()){
			p.NoSel();
			return;
		}

		ArrayList<SimpleFeature> toEdit = new ArrayList<SimpleFeature>();
		Iterator iterator = feat.iterator(); 

		// check values
		while (iterator.hasNext())
			toEdit.add((SimpleFeature)iterator.next());


		p.setFeatures(toEdit);
		p.create();
		p.open();
		if(p.didUserSubmit()){
			featuresToSave = new Vector(toEdit);
			this.doLayerEditted();
//			this.clearSelectedItems();
//			mapPane.forceRedraw();
//			mapPane.repaint();
			
		}
	}


	public void selectFeatures(MapMouseEvent ev, boolean propagateSelection)
	{

		/**
		 * Construct a 5x5 pixel rectangle centered on the mouse click position.
		 * This is done to account for the user not clicking directly on tiny features.
		 */
		Point screenPos = ev.getPoint();
		Rectangle screenRect = new Rectangle(screenPos.x - 5, screenPos.y - 5, 10, 10);

		/*
		 * Transform the screen rectangle into bounding box in the coordinate
		 * reference system of our map context. Note: we are using a naive
		 * method here but GeoTools also offers other, more accurate methods.
		 */
		AffineTransform screenToWorld = mapPane.getScreenToWorldTransform();
		Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
		ReferencedEnvelope bbox = new ReferencedEnvelope(worldRect, mapPane.getMapContent().getCoordinateReferenceSystem());

		selectFeatures(bbox, propagateSelection);

	}

	@SuppressWarnings("rawtypes")
	public void selectFeatures(ReferencedEnvelope bbox, boolean propagateSelection)
	{
		//** MH ** will not randomly select any layer. If null, give a nice message
		if(getSelectedLayer() == null){
			return;
		}
		featureSource = getSelectedLayer().getFeatureSource();

		/*
		 * Use the filter to identify the selected features
		 */
		try {
			FeatureCollection selectedFeatures = findFeature(bbox);
			if (selectedFeatures == null) {
				return;
			}
			FeatureIterator iter = selectedFeatures.features();
			selectFeatures(iter, propagateSelection);

		} catch (Exception ex) {
			//	logger.error("Error selecting features", ex); //$NON-NLS-1$
			return;
		}
	}

	public void selectFeatures(SelectedFeatures selectedFeatures, boolean propagateSelection)
	{
		Map<Integer, SimpleFeatureCollection> selectionSet = selectedFeatures.getSelectionSet();
		Layer selectedLayer = getSelectedLayer();

		for (Integer index : selectionSet.keySet()) {
			Layer layerByIndex = mapPane.getLayerByIndex(index);
			if (layerByIndex == selectedLayer) {
				featureSource = selectedLayer.getFeatureSource();
				SimpleFeatureIterator iter = selectionSet.get(index).features();
				selectFeatures(iter, propagateSelection);
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void selectFeatures(FeatureIterator iter, boolean propagateSelection)
	{

		// these are for the selection service, to pass around what got selected
		SelectedDatasetItems selected = new SelectedDatasetItems(this);
		selectedCollection = new DefaultFeatureCollection();


		try {
			while (iter.hasNext()) {
				Feature feature = iter.next();

				IDs.add(feature.getIdentifier());
				feat.add(feature);
				if (feature instanceof SimpleFeature) {
					selectedCollection.add((SimpleFeature) feature);
				}
			}

			// assume we're using a DatasetLayer, so we can wrap it in a SelectedDatasetItems
			if (propagateSelection && getSelectedLayer() instanceof DatasetLayer) {
				RenderableDataset dataset = ((DatasetLayer) getSelectedLayer()).getDataset();
				selected.setSelectionSet(dataset, selectedCollection);
				selectionProvider.setSelection(new StructuredSelection(selected));
			}

		} finally {
			iter.close();
		}

		if (IDs.isEmpty()) {
			return;
		}

		displaySelectedFeatures(IDs);

	}

	/**
	 * 
	 * @param env
	 * @param i
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	private SimpleFeatureCollection findFeature(ReferencedEnvelope env) throws IndexOutOfBoundsException
	{

		Layer layer = getSelectedLayer();
		if (layer == null) {
			return null;
		}

		try {
			String geomFieldname = layer.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
			if (geomFieldname == "") //$NON-NLS-1$
				geomFieldname = GISConstants.GEOM_FIELDNAME;
			BBOX f = ff.bbox(ff.property(geomFieldname), (BoundingBox) env);

			// need to create "and" filter with query in layer
			Filter allfilter = ff.and(f, layer.getQuery().getFilter());

			SimpleFeatureCollection fc = (SimpleFeatureCollection) layer.getFeatureSource().getFeatures(allfilter);
			return fc;
		} catch (IOException e) {
			//logger.error(e);
		} catch (IllegalFilterException e) {
			//logger.error(e);
		}

		return null;
	}

	/**
	 * Sets the display to paint selected features yellow and unselected
	 * features in the default style.
	 *
	 * @param IDs
	 *            identifiers of currently selected features
	 */
	private void displaySelectedFeatures(Set<FeatureId> IDs)
	{

		Layer layer = getSelectedLayer();
		Style style = layer.getStyle();
		List<FeatureTypeStyle> featureTypeStyles = style.featureTypeStyles();
		for (FeatureTypeStyle ftStyle : featureTypeStyles) {
			// remove the selected features rule
			for (Iterator<Rule> iterator = ftStyle.rules().iterator(); iterator.hasNext();) {
				Rule rule = iterator.next();
				if (GISConstants.SELECTED_FEATURES_STYLE.equals(rule.getName())) {
					iterator.remove();
				}
			}

			// and re-add it with the new selected IDs
			Rule selectedRule = createRule(SELECTED_OUTLINE_COLOUR, SELECTED_COLOUR);
			selectedRule.setFilter(ff.id(IDs));
			selectedRule.setName(GISConstants.SELECTED_FEATURES_STYLE);
			ftStyle.rules().add(0, selectedRule);
		}

		// this shouldn't be necessary, but without it, the necessary repaint isn't done.

		mapPane.forceRedraw();
		mapPane.repaint();
	}
	
	public void reStyleChangedStructType(){

	
//		if(IDs.isEmpty()) {				
			Layer layer = getSelectedLayer();
			if(layer != null){ 
				Style style = layer.getStyle();			
				List<FeatureTypeStyle> featureTypeStyles = style.featureTypeStyles();
				for (FeatureTypeStyle ftStyle : featureTypeStyles) {
					// remove the selected features rule
					for (Iterator<Rule> iterator = featureTypeStyles.get(0).rules().iterator(); iterator.hasNext();) {
						Rule rule = iterator.next();
						if (GISConstants.SELECTED_FEATURES_STYLE.equals(rule.getName())) {
							iterator.remove();
						}
					}						
				}
			}		
		

		// this shouldn't be necessary, but without it, the necessary repaint isn't done.
		mapPane.forceRedraw();
		mapPane.triggerForceRedraw();		
		mapPane.repaint();
		mapPane.pan(0);
		mapPane.pan(1);
	}

	/**
	 * Helper for createXXXStyle methods. Creates a new Rule containing a
	 * Symbolizer tailored to the geometry type of the features that we are
	 * displaying.
	 */
	private Rule createRule(Color outlineColor, Color fillColor)
	{

		Symbolizer symbolizer = null;
		Fill fill = null;
		Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

		String geometryType = SchemaUtils.getGeomForSchema(featureSource.getSchema());

		switch (geometryType) {
		case GISConstants.POLYGON:
			fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
			symbolizer = sf.createPolygonSymbolizer(stroke, fill, null);
			break;

		case GISConstants.LINE:
			symbolizer = sf.createLineSymbolizer(stroke, null);
			break;

		case GISConstants.POINT:
			fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));

			Mark mark = sf.getCircleMark();
			mark.setFill(fill);
			mark.setStroke(stroke);

			Graphic graphic = sf.createDefaultGraphic();
			graphic.graphicalSymbols().clear();
			graphic.graphicalSymbols().add(mark);
			graphic.setSize(ff.literal(POINT_SIZE));

			symbolizer = sf.createPointSymbolizer(graphic, null);

		}

		Rule rule = sf.createRule();

		rule.symbolizers().add(symbolizer);
		return rule;
	}

	public void clearSelection(FeatureLayer layer)
	{
		if (layer == null) {
			return;
		}
		Style style = layer.getStyle();
		if (style == null) {
			return;
		}
		boolean removed = false;
		List<FeatureTypeStyle> featureTypeStyles = style.featureTypeStyles();
		for (FeatureTypeStyle ftStyle : featureTypeStyles) {
			// remove the selected features rule
			for (Iterator<Rule> iterator = ftStyle.rules().iterator(); iterator.hasNext();) {
				Rule rule = iterator.next();
				if (GISConstants.SELECTED_FEATURES_STYLE.equals(rule.getName())) {
					removed = true;
					iterator.remove();
				}
			}
		}

		// don't refresh if the selection wasn't actually removed
		if (removed) {
			layer.setStyle(style);
			mapPane.forceRedraw();
			mapPane.repaint();
		}

	}

	public void setSelectionProvider(ISelectionProvider selectionProvider)
	{
		this.selectionProvider = selectionProvider;
	}
	
	/**** MH ***************************************************************/
	private Vector<SimpleFeature> featuresToSave;// = new ArrayList<SimpleFeature>();
	@Override
	public void doLayerEditted() {
		if(featuresToSave != null){
			for(FeatureAttributeListener listener: listeners){
				listener.featuresChange(featuresToSave);
			}
		}			
	}
	
	private Vector <FeatureAttributeListener> listeners  = new Vector<FeatureAttributeListener>();
	@Override
	public void addFeatuerAttributeListener(FeatureAttributeListener listener) {	
		listeners.add(listener);
	}

}
