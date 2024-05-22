/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Contributors:
 *    Nathan Tolbert - initial API and implementation and/or initial documentation
 *    Modified by Yong Wook Kim (NCSA)
 *******************************************************************************/
package com.uwiseismic.modifiedmappane.rcp.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.RenderingExecutor;
import org.geotools.swing.RenderingExecutorEvent;

/**
 * JMapPane for double layer for showing map selection and others
 */
@SuppressWarnings("serial")
public class DoubleBufferedJMapPane extends AbstractMapPane
{

	private GTRenderer renderer;
	private BufferedImage baseImage;
	private BufferedImage altBaseImage;
	private Graphics2D baseImageGraphics;
	private Graphics2D altBaseImageGraphics;

	/**
	 * Creates a new map pane.
	 */
	public DoubleBufferedJMapPane()
	{
		this(null);
	}

	/**
	 * Creates a new map pane.
	 *
	 * @param content
	 *            the map content containing the layers to display
	 *            (may be {@code null})
	 */
	public DoubleBufferedJMapPane(MapContent content)
	{
		this(content, null, null);
	}

	/**
	 * Creates a new map pane. Any or all arguments may be {@code null}
	 *
	 * @param content
	 *            the map content containing the layers to display
	 * @param executor
	 *            the rendering executor to manage drawing
	 * @param renderer
	 *            the renderer to use for drawing layers
	 */
	public DoubleBufferedJMapPane(MapContent content, RenderingExecutor executor, GTRenderer renderer)
	{
		super(content, executor);
		doSetRenderer(renderer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapContent(MapContent content)
	{
		super.setMapContent(content);
		if (content != null && renderer != null) {
			// If the new map content had layers to draw, and this pane is visible,
			// then the map content will already have been set with the renderer
			//
			if (renderer.getMapContent() != content) { // just check reference equality
				renderer.setMapContent(mapContent);
			}
		}
	}

	/**
	 * Gets the renderer, creating a default one if required.
	 *
	 * @return the renderer
	 */
	public GTRenderer getRenderer()
	{
		if (renderer == null) {
			doSetRenderer(new StreamingRenderer());
		}
		return renderer;
	}

	/**
	 * Sets the renderer to be used by this map pane.
	 *
	 * @param renderer
	 *            the renderer to use
	 */
	public void setRenderer(GTRenderer renderer)
	{
		doSetRenderer(renderer);
	}

	private void doSetRenderer(GTRenderer newRenderer)
	{
		if (newRenderer != null) {
			Map<Object, Object> hints = newRenderer.getRendererHints();
			if (hints == null) {
				hints = new HashMap<Object, Object>();
			}

			if (newRenderer instanceof StreamingRenderer) {
				if (hints.containsKey(StreamingRenderer.LABEL_CACHE_KEY)) {
					labelCache = (LabelCache) hints.get(StreamingRenderer.LABEL_CACHE_KEY);
				} else {
					labelCache = new LabelCacheImpl();
					hints.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
				}
			}

			newRenderer.setRendererHints(hints);

			if (mapContent != null) {
				newRenderer.setMapContent(mapContent);
			}
		}

		renderer = newRenderer;
	}

	/**
	 * Retrieve the map pane's current base image.
	 * <p>
	 * The map pane caches the most recent rendering of map layers as an image to avoid time-consuming rendering requests whenever
	 * possible. The base image will be re-drawn whenever there is a change to map layer data, style or visibility; and it will be
	 * replaced by a new image when the pane is resized.
	 * <p>
	 * This method returns a <b>live</b> reference to the current base image. Use with caution.
	 *
	 * @return a live reference to the current base image
	 */
	public RenderedImage getBaseImage()
	{
		return this.baseImage;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (baseImage != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(baseImage, imageOrigin.x, imageOrigin.y, null);			
		}
	}
	

	@Override
	protected void drawLayers(boolean createNewImage)
	{
		drawingLock.lock();
		try {
			if (mapContent != null && !mapContent.getViewport().isEmpty() && acceptRepaintRequests.get()) {

				Rectangle r = getVisibleRect();

				// if we have an empty rectangle, don't do anything.
				// (this can happen if the view was closed and re-opened)
				// in this case, it will request another draw once the view
				// is re-established with the correct size, so we can just do
				// nothing here instead.
				if (r.width == 0 || r.height == 0) {
					return;
				}

				// if the back-buffer image doesn't exist, or is the wrong size, or needs to be
				// created for some other reason, make a new one
				if (altBaseImage == null || createNewImage || r.width != altBaseImage.getWidth() || r.height != altBaseImage.getHeight()) {
					altBaseImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
							.createCompatibleImage(r.width, r.height, Transparency.TRANSLUCENT);

					if (altBaseImageGraphics != null) {
						altBaseImageGraphics.dispose();
					}

					altBaseImageGraphics = altBaseImage.createGraphics();
					clearLabelCache.set(true);

				} else {
					// otherwise keep the old image, and just clear it, so we're
					// not allocating a jillion graphics objects
					altBaseImageGraphics.setBackground(getBackground());
					altBaseImageGraphics.clearRect(0, 0, r.width, r.height);
				}

				if (mapContent != null && !mapContent.layers().isEmpty()) {
					getRenderingExecutor().submit(mapContent, getRenderer(), altBaseImageGraphics, this);
				}
			}
		}
		catch(Exception ee){}
		finally {
			drawingLock.unlock();
		}
	}

	@Override
	public void onRenderingCompleted(RenderingExecutorEvent event)
	{
		BufferedImage temp = baseImage;
		baseImage = altBaseImage;
		altBaseImage = temp;
		

		Runnable rT = new Runnable() {
			public void run() {
				Graphics2D temp2 = baseImageGraphics;
				baseImageGraphics = altBaseImageGraphics;
				altBaseImageGraphics = temp2;
			}
		};
		new Thread(rT).start();				
		

		super.onRenderingCompleted(event);

	}

	@Override
	public void reset() {}

}
