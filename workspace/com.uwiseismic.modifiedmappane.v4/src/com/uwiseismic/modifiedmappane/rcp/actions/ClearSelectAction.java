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
 * This action is triggered by the Clear Selected Items button on the JToolBar.
 * The list of selected features is cleared and the previously selected features which were
 * highlighted, are no longer highlighted.
 */
public class ClearSelectAction extends AbstractAction {

	private static final long serialVersionUID = -2268590691864666234L;
	private ImageIcon icon;
	JMapPaneRenderer map;

	public ClearSelectAction(JMapPaneRenderer map) {
		URL url = null;
		try {
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane.v4");
			URL fileURL = bundle.getEntry("resources/clear.png");
			url = FileLocator.toFileURL(fileURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		icon = new ImageIcon(url);
		this.putValue(Action.SMALL_ICON, new ImageIcon(icon.getImage().getScaledInstance(16, 16, 0)));
		this.map = map;
	}

	public void actionPerformed(ActionEvent e) {
		map.enableSelecting(false);
		map.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		map.getSelectionHelper().clearSelectedItems();
		map.repaint();
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
}
