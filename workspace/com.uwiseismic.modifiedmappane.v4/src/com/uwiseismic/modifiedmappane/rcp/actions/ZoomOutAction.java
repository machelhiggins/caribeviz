package com.uwiseismic.modifiedmappane.rcp.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.uwiseismic.modifiedmappane.rcp.map.JMapPaneRenderer;

/**
 * This action is triggered by the Zoom Out button on the JToolBar.
 * The JMapPaneRenderer zooms out from the center of the current view.
 */
public class ZoomOutAction extends AbstractAction {

	private static final long serialVersionUID = 8669650422678543113L;
	private ImageIcon icon;
	JMapPaneRenderer map;

	public ZoomOutAction(JMapPaneRenderer map) {
		URL url = null;
		try {
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane.v4");
			URL fileURL = bundle.getEntry("resources/ZoomOut16.gif");
			url = FileLocator.toFileURL(fileURL);
		} catch (Exception e) {
			e.printStackTrace();
		}

		icon = new ImageIcon(url);
		this.putValue(Action.SMALL_ICON, icon);
		this.map = map;
	}

	public void actionPerformed(ActionEvent e) {
		map.enableSelecting(false);
		map.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		map.zoomOut();
	}
}
