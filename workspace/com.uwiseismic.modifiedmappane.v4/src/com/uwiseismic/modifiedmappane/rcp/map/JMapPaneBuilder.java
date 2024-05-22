/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Contributors:
 * Jong Lee, Nathan Tolbert - initial API and implementation and/or initial documentation
 * Modified by Yong Wook Kim (NCSA)
 *******************************************************************************/
package com.uwiseismic.modifiedmappane.rcp.map;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.logging.Logging;

import edu.illinois.ncsa.ergo.gis.exceptions.RenderingException;
//import edu.illinois.ncsa.ergo.gis.geotools.ui.internal.JMapPaneRenderer;

import edu.illinois.ncsa.ergo.gis.types.RendererState;
import edu.illinois.ncsa.ergo.gis.types.Scenario;
import edu.illinois.ncsa.ergo.gis.ui.builders.RendererBuilder;
import edu.illinois.ncsa.ergo.gis.util.DatasetUtils;

/**
 * JMapPane builder class
 */
public class JMapPaneBuilder{
	public static final String RENDERER_NAME = "2D-Swing"; //$NON-NLS-1$
	private static final String EXT_ID = "edu.illinois.ncsa.ergo.gis.geotools.ui.builders.JMapPaneBuilder"; //$NON-NLS-1$
	private static final String MAX_Y = "maxY"; //$NON-NLS-1$
	private static final String MAX_X = "maxX"; //$NON-NLS-1$
	private static final String MIN_Y = "minY"; //$NON-NLS-1$
	private static final String MIN_X = "minX"; //$NON-NLS-1$

	private static String[] SUPPORTED_IMAGE_FORMAT = new String[] { "bmp", "jpg", "png", "gif" }; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$

	public static final int CLICK_MODE_STANDARD = 0;
	public static final int CLICK_MODE_SELECT_LOCATION = 1;

	

}
