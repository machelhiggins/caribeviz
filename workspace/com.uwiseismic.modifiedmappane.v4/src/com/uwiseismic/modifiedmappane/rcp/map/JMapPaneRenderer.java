/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Contributors:
 *    Jong Lee, Nathan Tolbert - initial implementation 
 *    Modified by Yong Wook Kim (NCSA)
 *    Modified by Sterling Ramroach (UWI)
 *******************************************************************************/
package com.uwiseismic.modifiedmappane.rcp.map;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.Timer;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.swing.RenderingExecutorEvent;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.osgi.framework.Bundle;

import com.uwiseismic.modifiedmappane.event.EditLayerListener;
import com.uwiseismic.modifiedmappane.event.FeatureAttributeAdapter;
import com.uwiseismic.modifiedmappane.event.FeatureAttributeListener;
import com.uwiseismic.modifiedmappane.rcp.actions.ClearSelectAction;
import com.uwiseismic.modifiedmappane.rcp.actions.EditFeatureAction;
import com.uwiseismic.modifiedmappane.rcp.actions.PanAction;
import com.uwiseismic.modifiedmappane.rcp.actions.QuerySatAction;
import com.uwiseismic.modifiedmappane.rcp.actions.ResetAction;
import com.uwiseismic.modifiedmappane.rcp.actions.SelectAction;
import com.uwiseismic.modifiedmappane.rcp.actions.ZoomInAction;
import com.uwiseismic.modifiedmappane.rcp.actions.ZoomOutAction;
import com.uwiseismic.modifiedmappane.rcp.bingmaps.GISVirtualEarthAPISatImageProvider;
import com.uwiseismic.modifiedmappane.rcp.blur.GaussianFilter;

import edu.illinois.ncsa.ergo.gis.GISConstants;
import edu.illinois.ncsa.ergo.gis.types.DatasetLayer;
import edu.illinois.ncsa.ergo.gis.types.SelectedFeatures;
import edu.illinois.ncsa.ergo.gis.ui.IClickLocationCallback;
import edu.illinois.ncsa.ergo.gis.ui.builders.RendererBuilder;

/**
 * This map pane is used to display all layers.
 * It handles and paints all changes made to the extent of the shapefile and associated satellite images.
 */
@SuppressWarnings("serial")
public class JMapPaneRenderer extends DoubleBufferedJMapPane implements MapMouseListener, FeatureAttributeAdapter, EditLayerListener
{
	private double zoomFactor =  RendererBuilder.DEFAULT_ZOOM_FACTOR;
	private double panFactor = RendererBuilder.DEFAULT_PAN_FACTOR;
	private BufferedImage waitImg = null;
	private BufferedImage baseImg = null;
	private BufferedImage zoomImg = null;

	private JMapPaneSelectionHelper selectionStyleHelper = new JMapPaneSelectionHelper(this);

	private boolean isDragging;

	private DirectPosition2D startClick;
	private DirectPosition2D endClick;

	private FeatureLayer selectedLayer = null;
	private JPopupMenu popupMenu = new JPopupMenu();

	private double clickX;
	private double clickY;
	private double releaseX;
	private double releaseY;


	private int clickMode = JMapPaneBuilder.CLICK_MODE_STANDARD;


	private Graphics graphics;
	private boolean enableZoom = false;
	private boolean enablePan = false;
	private boolean enableSelect = false;
	int lastX;
	int lastY;

	//The main ReferencedEnvelope used.
	private ReferencedEnvelope mapArea = null;


	//This is the object that is used to connect to the Bing API to fetch the desired satellite image.
	private GISVirtualEarthAPISatImageProvider bingMap = new GISVirtualEarthAPISatImageProvider();

	private BufferedImage mapB = null;

	private boolean scroll    = false;
	private boolean QuerySatelliteImagery = false;

	private Rectangle zRect;

	public void init(MapContent mapContent, int width, int height)
	{
		setMapContent(mapContent);
		//Sometimes the frame that initializes this mapPane may not have a set 
		//width or height, which causes a compilation error. However, this is fixed 
		//at runtime by the parent component which manipulates the mapPane.
		if(width <= 0 || height <= 0)setSize(width, height);
		else setSize(2,2);

		if(getWidth() == 0 || getHeight() == 0)
			baseImg = new BufferedImage(2, 2,BufferedImage.TYPE_INT_ARGB);
		else
			baseImg = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_ARGB);
		
		baseImg.getGraphics().drawImage((BufferedImage)this.getBaseImage(), 0, 0, null);

		setZoomFactor(RendererBuilder.DEFAULT_ZOOM_FACTOR);
		setPanFactor(RendererBuilder.DEFAULT_PAN_FACTOR);
		this.addMouseListener(this);

		addMouseListener(dragBox);
		addMouseMotionListener(dragBox);

		mapArea = this.getDisplayArea();
		showDrawingWait();
//		System.out.println(getWidth() + " " + getHeight());
//		selectionStyleHelper.clearSelection(selectedLayer);
		new StartupWatchDog(this.getDisplayArea()).start();
	}


	/**
	 * Reset the map area to include the full extent of all
	 * layers and redraw the display
	 */
	public void reset() {
		if (fullExtent != null) {
			setDisplayArea(fullExtent);
		}
	}
	
	public void resize(){
		this.onShownOrResized();
	}

	
	public boolean mapChange = true;
	private ReferencedEnvelope oldMap = null;
	
	
	@Override
	public void paint(Graphics g){
		//To fix the issue with ERGO saving styles: When features are selected, and the view is closed, 
		//upon re-opening of the view, those selected features would still be highlighted. However, this is not 
		//desirable so the style is cleared if there are no selected features.
		clearAnySelectedStyle();
		super.paint(g);
		try{		
			
			//Retrieve the rendered image.
			baseImg = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_ARGB);
			baseImg.getGraphics().drawImage((BufferedImage)this.getBaseImage(), 0, 0, null);

			//if(temp != baseImg) mapChange = true;
			if(oldMap == null)mapChange = true;
			else if(!oldMap.equals(this.getDisplayArea()))mapChange = true;
			else mapChange = false;
			
			//Request satellite image from Bing Maps
			if(QuerySatelliteImagery && mapChange){
				//Use the coordinates of the top-left, and bottom-right corner of the mapArea in the request to Bing Maps.
				if(getWidth()<=2 || getHeight() <=2) mapB = null;
				else mapB = bingMap.getExactImage(getMapXY(0,0)[0],getMapXY(getWidth(),getHeight())[1],getMapXY(getWidth(),getHeight())[0],getMapXY(0,0)[1]);
				//mapB = bingMap.getExactImage(mapArea.getMinX(),mapArea.getMinY(), mapArea.getMaxX(), mapArea.getMaxY());
			}
			else if (!QuerySatelliteImagery){
				//Paint a white background
				mapB = new BufferedImage(getWidth(), getHeight() , BufferedImage.TYPE_INT_RGB);
				Graphics2D gg = mapB.createGraphics();
				gg.setColor(Color.WHITE);
				gg.fillRect(0, 0, mapB.getWidth(), mapB.getHeight());
				gg.dispose();
			}
			//If query to Bing Maps was successful, paint satellite image.
			if(mapB != null){
				Graphics2D g2 = (Graphics2D) g;
				g2.drawImage(mapB, 0, 0, getWidth(), getHeight(), null);
			}
			else if(QuerySatelliteImagery && mapB == null){
				//If the query to Bing Maps failed, display a "No Connection" image from the bundle.
				URL url = null;
				try {
					Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane.v4");
					URL fileURL = bundle.getEntry("resources/noNet.png");
					url = FileLocator.toFileURL(fileURL);
					mapB = ImageIO.read(url);
					Graphics2D g2 = (Graphics2D) g;
					Image i = mapB.getScaledInstance(baseImg.getWidth(), baseImg.getHeight(), 0);
					ImageIcon imc1 = new ImageIcon(i);
					imc1.paintIcon(this, g2, 0, 0);
				} catch (Exception e) {
					e.printStackTrace();}
			}

			//Display newly rendered image.
			if(baseImg != null){
				Graphics2D gg = (Graphics2D) g;
				gg.setColor(new Color(255,255,255));
				ImageIcon imc2 = new ImageIcon(baseImg);
				imc2.paintIcon(this, gg, 0, 0);

			}	
			oldMap = this.getDisplayArea();	

		}
		catch(Exception e){	
			e.printStackTrace();
		}
	}


	@Override
	public void onRenderingStarted(RenderingExecutorEvent ev)
	{
		showDrawingWait();
	};

	/**
	 * Displays the "Drawing Image" indicator.
	 */
	public void showDrawingWait()
	{

		if (getGraphics() != null) {
			getGraphics().drawImage(getDrawingWaitImg(), 0, 0, null);
		}
	}

	public JMapPaneSelectionHelper getSelectionHelper(){
		return selectionStyleHelper;
	}


	private BufferedImage getDrawingWaitImg()
	{
		if (waitImg == null) {
			Bundle bundle = Platform.getBundle("edu.illinois.ncsa.ergo.gis.geotools.ui"); //$NON-NLS-1$
			URL entry = bundle.getEntry("icons/drawingImg.png"); //$NON-NLS-1$
			try {
				waitImg = ImageIO.read(entry);
			} catch (IOException e) {
				e.printStackTrace();
				//logger.error(e);
				waitImg = new BufferedImage(0, 0, BufferedImage.TYPE_BYTE_BINARY);
			}
		}
		return waitImg;
	}


	/**
	 * Initialize all the actions/buttons on the toolbar.
	 * @return
	 */
	public JToolBar createToolBar(){
		JToolBar jtb = new JToolBar();
		jtb.setFloatable(false);


		Action querySat = new QuerySatAction(this);
		querySat.putValue(Action.SHORT_DESCRIPTION, "Query Satellite Image");
		Action reset = new ResetAction(this);
		reset.putValue(Action.SHORT_DESCRIPTION, "Reset");
		Action zoomIn = new ZoomInAction(this);
		zoomIn.putValue(Action.SHORT_DESCRIPTION, "Zoom in");
		Action zoomOut = new ZoomOutAction(this);
		zoomOut.putValue(Action.SHORT_DESCRIPTION, "Zoom out");

		Action panL = new PanAction(this,0);
		panL.putValue(Action.SHORT_DESCRIPTION, "Pan Left");
		Action panR = new PanAction(this,1);
		panR.putValue(Action.SHORT_DESCRIPTION, "Pan Right");		
		Action panU = new PanAction(this,2);
		panU.putValue(Action.SHORT_DESCRIPTION, "Pan Up");		
		Action panD = new PanAction(this,3);
		panD.putValue(Action.SHORT_DESCRIPTION, "Pan Down");	
		Action panFree = new PanAction(this,4);
		panFree.putValue(Action.SHORT_DESCRIPTION, "Free Pan");

		Action select = new SelectAction(this);
		select.putValue(Action.SHORT_DESCRIPTION, "Select");
		Action editSelection = new EditFeatureAction(this);
		editSelection.putValue(Action.SHORT_DESCRIPTION, "Edit Selected Features");
		Action clearSelection = new ClearSelectAction(this);
		clearSelection.putValue(Action.SHORT_DESCRIPTION, "Clear Selected Items");

		jtb.add(zoomIn);
		jtb.add(zoomOut);
		jtb.add(panL);
		jtb.add(panR);
		jtb.add(panU);
		jtb.add(panD);
		jtb.add(panFree);

		jtb.addSeparator();
		jtb.add(reset);
		jtb.add(querySat);

		jtb.addSeparator();
		jtb.add(select);
		jtb.add(editSelection);
		jtb.add(clearSelection);

		return jtb;
	}

	/**
	 * Let the renderer know the user is zooming.
	 * @param isZooming
	 */
	public void enableZooming(boolean isZooming){
		enableZoom = isZooming;
	}

	/**
	 * Let the renderer know if the user is panning
	 * @param isPanning
	 */
	public void enablePanning(boolean isPanning){
		enablePan = isPanning;

		//Change curse if isPanning == true.
		if(!isPanning)this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		else this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
	}

	/**
	 * Let the renderer know if the user is selecting features to be edited.
	 * @param isSelecting
	 */
	public void enableSelecting(boolean isSelecting){
		enableSelect = isSelecting;

		if(enableSelect)setCursor(new Cursor(Cursor.HAND_CURSOR));
		else this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}




	/**
	 * calculate the map coordinate from screen coordinate
	 * 
	 * @param screenX
	 * @param screenY
	 * @return
	 */
	public double[] getMapXY(double screenX, double screenY)
	{
		// simply use screen2world in geotools instead of this method.
		java.awt.Rectangle bounds = this.getBounds();
		ReferencedEnvelope mapArea = this.getDisplayArea();

		double width = mapArea.getWidth();
		double height = mapArea.getHeight();

		double mapX = (screenX * width / (double) bounds.width) + mapArea.getMinX();
		double mapY = ((bounds.height - screenY) * height / (double) bounds.height) + mapArea.getMinY();

		double[] mapXY = { mapX, mapY };

		return mapXY;
	}

	/**
	 * calculate the screen coordinate from map coordinate
	 * 
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public double[] getScreenXY(double mapX, double mapY)
	{
		// simply use world2screen in geotools instead of this method.
		java.awt.Rectangle bounds = this.getBounds();
		ReferencedEnvelope mapArea = this.getDisplayArea();

		// calculate the zoom in level
		double width = mapArea.getWidth();
		double height = mapArea.getHeight();

		double screenX = ((mapX - mapArea.getMinX()) * (double) bounds.width / width);
		double screenY = (double) bounds.height - ((mapY - mapArea.getMinY()) * (double) bounds.height / height);
		double[] screenXY = { screenX, screenY };

		return screenXY;
	}

	/**
	 * Zoom to full extent
	 */
	public void resetDefaultView()
	{
		this.reset();
	}

	/**
	 * Zoom in at the center of the map
	 */
	public void zoomIn()
	{
		if (getMapContent().layers().isEmpty())
			return;
		double centerX = (this.getDisplayArea().getMinX() + this.getDisplayArea().getMaxX()) / 2.0;
		double centerY = (this.getDisplayArea().getMinY() + this.getDisplayArea().getMaxY()) / 2.0;

		zoomIn(centerX, centerY);
	}

	/**
	 * Zoom in at the given map point (world coordinates)
	 */
	public void zoomIn(double mapX, double mapY)
	{
		if (getMapContent().layers().isEmpty())
			return;

		changeViewPort(mapX, mapY, getZoomFactor());
	}

	/**
	 * Zoom out at the center of the map
	 */
	public void zoomOut()
	{
		if (getMapContent().layers().isEmpty())
			return;
		double centerX = (this.getDisplayArea().getMinX() + this.getDisplayArea().getMaxX()) / 2.0;
		double centerY = (this.getDisplayArea().getMinY() + this.getDisplayArea().getMaxY()) / 2.0;
		zoomOut(centerX, centerY);
	}

	/**
	 * Zoom out at the given map point (world coordinates)
	 */
	public void zoomOut(double mapX, double mapY)
	{
		if (getMapContent().layers().isEmpty())
			return;

		changeViewPort(mapX, mapY, 1.0 / getZoomFactor());
	}

	/**
	 * Change the center of the map with the given map point (world cooridnates)
	 */
	public void reCenter(double mapX, double mapY)
	{
		if (getMapContent().layers().isEmpty())
			return;

		changeViewPort(mapX, mapY, 1.0);
	}

	/**
	 * Pan left
	 */
	public void panLeft()
	{
		pan(0);
	}

	/**
	 * Pan right
	 */
	public void panRight()
	{
		pan(1);
	}

	/**
	 * Pan up
	 */
	public void panUp()
	{
		pan(2);
	}

	/**
	 * Pan down
	 */
	public void panDown()
	{
		pan(3);
	}

	/**
	 * @param pan_type:
	 * 		0: pan left 
	 * 		1: pan right 
	 * 		2: pan up 
	 * 		3: pan down 
	 */
	public void pan(int pan_type)
	{
		if (getMapContent().layers().isEmpty()) // when there are no layers in the mapcontext.
			return;

		double x = (double) getSize().getWidth() / 2.0;
		double y = (double) getSize().getHeight() / 2.0;

		switch (pan_type) {
		case 0:
			x += getPanFactor();
			break;
		case 1:
			x -= getPanFactor();
			break;
		case 2:
			y += getPanFactor();
			break;
		case 3:
			y -= getPanFactor();

		}
		DirectPosition2D screenPt = new DirectPosition2D(x, y);
		DirectPosition2D mapXY = new DirectPosition2D();
		getScreenToWorldTransform().transform(screenPt, mapXY);

		changeViewPort(mapXY.x, mapXY.y, 1.0);
	}

	/**
	 * Change view port with given map point with zoom level it means that zoom
	 * in/out, recenter.
	 */
	private void changeViewPort(double mapX, double mapY, double zlevel)
	{
		double width = this.getDisplayArea().getWidth() / 2.0;
		double height = this.getDisplayArea().getHeight() / 2.0;

		double x1 = mapX - (width / zlevel);
		double y1 = mapY - (height / zlevel);
		double x2 = mapX + (width / zlevel);
		double y2 = mapY + (height / zlevel);
		ReferencedEnvelope re = new ReferencedEnvelope(x1, x2, y1, y2, getMapContent().getCoordinateReferenceSystem());
		mapArea = re;

		this.setDisplayArea(re);
	}

	/**
	 * Change view port with given map point with box created by mouse dragging
	 * in/out, recenter.
	 */
	private void changeViewPortMouseBox(double mapX, double mapY, double zlevel)
	{
		double x1 = 0.0;
		double y1 = 0.0;
		double x2 = 0.0;
		double y2 = 0.0;

		clickX = mapX;
		clickY = mapY;

		//Calculate envelope bounds
		if (clickX > releaseX) {
			x1 = releaseX;
			x2 = clickX;
		} else {
			x1 = clickX;
			x2 = releaseX;
		}

		if (clickY > releaseY) {
			y1 = releaseY;
			y2 = clickY;
		} else {
			y1 = clickY;
			y2 = releaseY;
		}

		ReferencedEnvelope re = new ReferencedEnvelope(x1, x2, y1, y2, getMapContent().getCoordinateReferenceSystem());
		this.setDisplayArea(re);
		mapArea = re;
	}


	public void forceRedraw()
	{
		// this is a fairly nasty hack, but, it forces the pane to re-render itself.
		// (if there's a better way, other than repaint which doesn't, I'd love to know it.)
		if (getMapContent().layers().isEmpty())
			return;
		double centerX = (this.getDisplayArea().getMinX() + this.getDisplayArea().getMaxX()) / 2.0;
		double centerY = (this.getDisplayArea().getMinY() + this.getDisplayArea().getMaxY()) / 2.0;
		changeViewPort(centerX, centerY, 1.0);

	}

	public double getZoomFactor()
	{
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor)
	{
		this.zoomFactor = zoomFactor;
	}

	public double getPanFactor()
	{
		return panFactor;
	}

	public void setPanFactor(double panFactor)
	{
		this.panFactor = panFactor;
	}

	public void processMouseEvent(MouseEvent event)
	{
		if (event.isPopupTrigger()) {
			double[] mapXY = getMapXY((double) event.getX(), (double) event.getY());
			clickX = mapXY[0];
			clickY = mapXY[1];
			if (clickMode != JMapPaneBuilder.CLICK_MODE_SELECT_LOCATION) {
				popupMenu.show(event.getComponent(), event.getX(), event.getY());
			}
		}
		super.processMouseEvent(event);
	}

	@Override
	public void onMouseClicked(final MapMouseEvent event){
		if (clickMode == JMapPaneBuilder.CLICK_MODE_SELECT_LOCATION && event.getButton() == 1) {

			if (callback != null) {
				callback.clicked(event.getWorldPos().x, event.getWorldPos().y);
			}

//			// set point position
//			pointMark = event.getPoint();
//
//			// set start and end position of the line path
//			if (isLineStart) {
//				isLineStart = !isLineStart;
//				lineStart = event.getWorldPos();
//			} else {
//				isLineStart = !isLineStart;
//				lineEnd = event.getWorldPos();
//			}
//
//			if (isLine) {
//				createLinePath();
//			}
//
//			if (isPoint) {
//				createPointMark();
//			}
//
//			return;
		}
//
//		if (event.isControlDown()) {
//			selectionStyleHelper.selectFeatures(event, true);
//			return;
//		}
//		if (event.isShiftDown()) {
//			selectionStyleHelper.selectFeatures(event, true);
//			return;
//		}
//
//		if (event.getClickCount() > 1) {
//			// it work as if single click
//			if (event.getButton() == 1) {
//				if (clickMode != JMapPaneBuilder.CLICK_MODE_SELECT_LOCATION) {
//					reCenter(event.getWorldPos().x, event.getWorldPos().y);
//				}
//			} else if (event.getButton() == 2) {
//				if (clickMode != JMapPaneBuilder.CLICK_MODE_SELECT_LOCATION) {
//					resetDefaultView();
//				}
//			}
//			wasDoubleClick = true;
//		} else {
//
//			// the way this is handled is stupid -- we get handed a single click first even if it's a
//			// double-click, so we have to check and see if another click comes through before
//			// we process the single click.
//			Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval"); //$NON-NLS-1$
//			timer = new Timer(timerinterval.intValue(), new ActionListener() {
//				public void actionPerformed(ActionEvent evt)
//				{
//					if (wasDoubleClick) {
//						wasDoubleClick = false; // reset flag
//					} else {
//						if (event.getButton() == 1) {
//							reCenter(event.getWorldPos().x, event.getWorldPos().y);
//						} else if (event.getButton() == 2) {
//							if (clickMode != JMapPaneBuilder.CLICK_MODE_SELECT_LOCATION) {
//								resetDefaultView();
//							}
//						}
//					}
//				}
//			});
//			timer.setRepeats(false);
//			timer.start();
//
//		}

	}

	@Override
	public void onMouseDragged(MapMouseEvent ev)
	{
		if(enableZoom){
			dragBox.setEnabled(true);
			isDragging = true;
		}
		if(enablePan){

			//Display blurred image while panning////////////////////////////
			if ((lastX > 0) && (lastY > 0)) {
				int dx = lastX - startX;
				int dy = lastY - startY;

				graphics = this.getGraphics();
				graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
				BufferedImage destImage = getBlurredImage();

				Image i = destImage.getScaledInstance(getWidth(), getHeight(), 0);
				ImageIcon ic = new ImageIcon(i);

				ic.paintIcon(this, graphics, dx, dy);				
			}

			lastX = ev.getX();
			lastY = ev.getY();
			///////////////////////////////////////////////////////////////////
		}
	}

	/**
	 * Apply a Gaussian Filter to the map to increase usability while waiting on renderer to draw new image.
	 * @return
	 */
	public BufferedImage getBlurredImage() {
		Rectangle dr = getBounds();
		BufferedImage destImage = new BufferedImage(dr.width, dr.height,BufferedImage.TYPE_INT_ARGB);
		destImage = new GaussianFilter(3).filter(baseImg, destImage);
		return destImage;
	}

	@Override
	public void onMouseEntered(MapMouseEvent ev){}

	@Override
	public void onMouseExited(MapMouseEvent ev){}

	@Override
	public void onMouseMoved(MapMouseEvent ev){}

	int startX;
	int startY;
	int endX;
	int endY;
	@Override
	public void onMousePressed(MapMouseEvent ev)
	{
		startClick = ev.getWorldPos();
		startX = ev.getX();
		startY = ev.getY();

		lastX = 0;
		lastY = 0;	

		//Tells the user to wait for the map to highlight the selected feature.
		if(enableSelect){
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			selectionStyleHelper.selectFeatures(ev, true);
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void onMouseReleased(MapMouseEvent ev)
	{
		endClick = ev.getWorldPos();
		releaseX = endClick.x;
		releaseY = endClick.y;

		if (isDragging && enableZoom) {
			endX = ev.getX();
			endY = ev.getY();

			//Display blurred zoomed image//////////////////////////////////////
			graphics = this.getGraphics();
			graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
			//zoomImg = getBlurredImage().getSubimage(Math.min(startX, ev.getX()), Math.min(startY,ev.getY()),Math.abs(startX - ev.getX()), Math.abs(startY - ev.getY()));

			baseImg = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_ARGB);
			baseImg.getGraphics().drawImage((BufferedImage)this.getBaseImage(), 0, 0, null);
			try{
				zoomImg = getBlurredImage().getSubimage(Math.min(startX, ev.getX()), Math.min(startY,ev.getY()),Math.abs(startX - ev.getX()), Math.abs(startY - ev.getY()));
			}catch(Exception ex){ex.printStackTrace();}

			try{
				Image dimg = zoomImg.getScaledInstance(getWidth(), getHeight(),Image.SCALE_SMOOTH);			
				ImageIcon zoomIcon = new ImageIcon(dimg);
				zoomIcon.paintIcon(this, graphics, 0, 0);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			////////////////////////////////////////////////////////////////////

			isDragging = false;
			enableZooming(false);
			dragBox.setEnabled(false);

			ReferencedEnvelope env = new ReferencedEnvelope(new Envelope2D(startClick, endClick));
System.out.println("zooming");
			//Zoom in with box
			changeViewPortMouseBox(startClick.x, startClick.y, getZoomFactor());
		}

		if(enablePan){
			enablePanning(false);

			endX = ev.getX();
			endY = ev.getY();

			double x = (double) getSize().getWidth() / 2.0;
			double y = (double) getSize().getHeight() / 2.0;

			int deltaY = (int) Math.abs(endY - startY);
			int deltaX = (int) Math.abs(endX - startX);
			if(endY < startY){
				y += deltaY;
				startY += deltaY;
				//endY += deltaY;
			}
			else {
				y -= deltaY;
				startY -= deltaY;
				//endY -= deltaY;
			}

			if(endX < startX){
				x += deltaX;
				startX += deltaX;
				//endX += deltaX;
			}
			else{
				x -= deltaX;
				startX -= deltaX;
				//endX -= deltaX;
			}

			DirectPosition2D screenPt = new DirectPosition2D(x, y);
			DirectPosition2D mapXY = new DirectPosition2D();
			getScreenToWorldTransform().transform(screenPt, mapXY);
			changeViewPort(mapXY.x, mapXY.y, 1.0);
		}

		lastX = 0;
		lastY = 0;

	}

	//We throttle scrolling, because the map pane can't keep up,
	//and can freak out if we throw too many scroll commands at it too fast.
	long lastScroll = 0;

	@Override
	public void onMouseWheelMoved(MapMouseEvent event)
	{
		long now = System.currentTimeMillis();
		if (now - lastScroll < 500) {
			return;
		}
		if (event.getWheelAmount() > 0) {
			zoomOut();
		} else {
			zoomIn();
		}
		lastScroll = now;

	}

	public Layer getLayerByIndex(int index)
	{
		return getMapContent().layers().get(index);
	}

//	public Layer getSelectedLayer()
//	{
//		if (selectedLayer != null) {
//			return selectedLayer;
//		}
//		try {
//			// if nothing selected, find the first layer that isn't the ROI
//			for (Layer layer : getMapContent().layers()) {
//				if (layer instanceof DatasetLayer) {
//					try {
//						String typeId = ((DatasetLayer) layer).getDataset().getTypeId();
//						if (GISConstants.REGION_OF_INTEREST.equals(typeId)) {
//							continue;
//						}
//					} catch (NullPointerException e) {
//						//logger.error("layer " + layer.getTitle() + "does not have an associated dataset"); //$NON-NLS-1$//$NON-NLS-2$
//					}
//				}
//
//				return layer;
//
//			}
//
//		} catch (NullPointerException e) {
//			//logger.error("No Map layers for map"); //$NON-NLS-1$
//		}
//		// otherwise, if we hit a null, or have no layers, return null
//		return null;
//	}

	/**
	 * Return the highlighted selected features to normal.
	 */
	public void clearSelection()
	{	
		selectionStyleHelper.clearSelection(selectedLayer);
	}
	
	
	

	public void setSelectionLayer(DatasetLayer layer)
	{
		if (layer instanceof FeatureLayer) {

			// before we do anything, make sure the selected layer is
			// actually for this map view/scenario/mapContent
			if (!getMapContent().layers().contains(layer)) {
				return;
			}

			// if we select a different layer, clear the feature selection
			if (selectedLayer != layer) {
				//clearSelection();
			}

			selectedLayer = (FeatureLayer) layer;
		} else {
			//logger.debug("JMapPane doesn't do anything with selected non-feature layers, ignoring"); //$NON-NLS-1$
		}

	}

	public void setSelectedFeatures(SelectedFeatures selectedFeatures)
	{
		selectionStyleHelper.selectFeatures(selectedFeatures, false);
	}

	public void setSelectionProvider(ISelectionProvider selectionProvider)
	{
		selectionStyleHelper.setSelectionProvider(selectionProvider);

	}


	/**
	 * Ask Bing Maps for satellite image or not.
	 */
	public void toggleQuerySatImages() {
		// TODO Auto-generated method stub
		QuerySatelliteImagery = !QuerySatelliteImagery;
		oldMap = null;
		showDrawingWait();
		repaint();

	}
	
	private boolean isPoint = false;
	private boolean isLine = false;
	private boolean isLineStart = true;
	private Point pointMark;
	private IClickLocationCallback callback = null;
	
	/**
	 * 
	 * @param clickMode
	 * @param clickCallback
	 */
	public void setClickMode(int clickMode, IClickLocationCallback clickCallback)
	{
		this.clickMode = clickMode;
		this.callback = clickCallback;
	}

	/**
	 * 
	 * @param isPoint
	 */
	public void setIsPoint(boolean isPoint)
	{
		this.isPoint = isPoint;
	}

	/**
	 * 
	 * @param isLine
	 */
	public void setIsLine(boolean isLine)
	{
		this.isLine = isLine;
	}

	/**
	 * 
	 * @param lineStart
	 */
	public void switchLinePoint(boolean lineStart)
	{
		if (lineStart) {
			isLineStart = true;
		} else {
			isLineStart = false;
		}
	}

	/*********************************** MH EDITS BELOW *************************************************/
	/** Added interfaces : FeatureAttributeAdapter, EditLayerListener */
	/*
	 * MH
	 *  (non-Javadoc)
	 * @see com.uwiseismic.modifiedmappane.event.FeatureAttributeAdapter#doLayerEditted()
	 */
	@Override
	public void doLayerEditted() {
		//no op 
	}

	private Vector <FeatureAttributeListener> listeners  = new Vector<FeatureAttributeListener>();
	/* 
	 * MH
	 * (non-Javadoc)
	 * @see com.uwiseismic.modifiedmappane.event.FeatureAttributeAdapter#addFeatuerAttributeListener(com.uwiseismic.modifiedmappane.event.FeatuerAttributeListener)
	 */
	@Override
	public void addFeatuerAttributeListener(FeatureAttributeListener listener) {
		//ADD any variable that this listener needs: 
		listeners.add(listener);
		selectionStyleHelper.addFeatuerAttributeListener(listener);
	}

	/* 
	 * MH
	 * (non-Javadoc)
	 * @see com.uwiseismic.modifiedmappane.event.EditLayerListener#editLayer(int)
	 */
	@Override
	public void editLayer(Layer layer) {
		
		System.out.println("Setting layer to EDIT !!!!!!!!!!!!!");
		this.setSelectionLayer(layer);
	}

	/* 
	 * MH
	 * (non-Javadoc)
	 * @see com.uwiseismic.modifiedmappane.event.EditLayerListener#disableEdit(int)
	 */
	@Override
	public void disableEdit(Layer layer) {
//		selectedLayer = null;	
		for(FeatureAttributeListener listener: listeners){
			listener.dispose();
		}		
	}
	
	public void setSelectionLayer(Layer layer){
		selectedLayer = (FeatureLayer) layer;		
	}
	
	public Layer getSelectedLayer()
	{
		if (selectedLayer != null) {
			return selectedLayer;
		}
//		try {
//			// if nothing selected, find the first layer that isn't the ROI
//			for (Layer layer : getMapContent().layers()) {
//				if (layer instanceof DatasetLayer) {
//					try {
//						String typeId = ((DatasetLayer) layer).getDataset().getTypeId();
//						if (GISConstants.REGION_OF_INTEREST.equals(typeId)) {
//							continue;
//						}
//					} catch (NullPointerException e) {
//						//logger.error("layer " + layer.getTitle() + "does not have an associated dataset"); //$NON-NLS-1$//$NON-NLS-2$
//					}
//				}
//
//				return layer;
//
//			}
//
//		} catch (NullPointerException e) {
//			//logger.error("No Map layers for map"); //$NON-NLS-1$
//		}
		// otherwise, if we hit a null, or have no layers, return null
		return null;
	}
	
	public void triggerForceRedraw(){
		doSetDisplayArea(getDisplayArea());
	}

	private void clearAnySelectedStyle(){
		//To fix the issue with ERGO saving styles: When features are selected, and the view is closed, 
		//upon re-opening of the view, those selected features would still be highlighted. However, this is not 
		//desirable so the style is cleared if there are no selected features.		
		if(selectionStyleHelper.IDs.isEmpty()) {				
			Layer layer = selectionStyleHelper.getSelectedLayer();
			if(layer != null){ //** MH...added because of null pointer exception being thrown on first load
				Style style = layer.getStyle();
				if(style != null){//** MH...added because of null pointer exception being thrown on first load
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
			}
		}
	}
	
//	private class ShutdownHook extends Thread {
//		@Override
//		public void run() {
//			try {
//				final IWorkbench workbench = PlatformUI.getWorkbench();
//				final Display display = PlatformUI.getWorkbench().getDisplay();
//				if (workbench != null && !workbench.isClosing()) {
//					display.syncExec(new Runnable() {
//						public void run() {
//							IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
//							for (int i = 0; i < workbenchWindows.length; i++) {
//								IWorkbenchWindow workbenchWindow = workbenchWindows[i];
//								if (workbenchWindow == null) {
//									// SIGTERM shutdown code must access
//									// workbench using UI thread!!
//								} else {
//
//								}
//							}
//						}
//					});
//				}
//			} catch (IllegalStateException e) {
//				// ignore
//			}
//		}
//	}

	/**
	 * 
	 * Horrible hack to make sure that extent is painted when it changes after init() is called
	 *  
	 * 
	 * @author Machel
	 *
	 */
	private class StartupWatchDog extends Thread {
		private ReferencedEnvelope re;
		public StartupWatchDog(ReferencedEnvelope re){
			this.re = re;
		}
		public void run(){
			try{sleep(3000);}catch (Exception ex){}
			if(!re.equals(getDisplayArea())){
				reset();
				forceRedraw();
				repaint();
			}
			
		}
	}	
}
