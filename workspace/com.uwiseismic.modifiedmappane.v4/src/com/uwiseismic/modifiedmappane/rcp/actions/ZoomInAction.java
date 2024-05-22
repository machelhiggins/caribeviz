package com.uwiseismic.modifiedmappane.rcp.actions;

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
 * This action is triggered by the Zoom In button on the JToolBar.
 * The user is able to draw a rectangle via mouse click and drag.
 * The JMapPaneRenderer then zooms into that rectangle.
 */
public class ZoomInAction extends AbstractAction {

	private static final long serialVersionUID = 5757407203303739037L;
	JMapPaneRenderer map;
	ImageIcon icon;

	public ZoomInAction(JMapPaneRenderer map) {
		URL url = null;
		try {
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane.v4");
			URL fileURL = bundle.getEntry("resources/ZoomIn16.gif");
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
		map.enableZooming(true);
	}

}
