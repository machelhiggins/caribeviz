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
 * This action is triggered by the Reset button on the JToolBar.
 * The full extent of the shapefile is displayed.
 */
public class ResetAction extends AbstractAction {

	private static final long serialVersionUID = 4199311387893380494L;
	private ImageIcon icon;
	JMapPaneRenderer map;

	public ResetAction(JMapPaneRenderer map) {
		URL url = null;
		try {
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane");
			URL fileURL = bundle.getEntry("resources/Reset16.gif");
			url = FileLocator.toFileURL(fileURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		icon = new ImageIcon(url);
		this.putValue(Action.SMALL_ICON, icon);
		this.map = map;
	}

	public void actionPerformed(ActionEvent e) {
		if (map == null) {
			return;
		}
		if (map.getMapContent() == null) {
			return;
		}

		try {
			map.getDisplayArea().equals(map.getMapContent().getMaxBounds());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		map.setState(JMapPaneRenderer.Reset);

		map.enableSelecting(false);
		map.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		map.showDrawingWait();
		map.repaint();
	}

}
