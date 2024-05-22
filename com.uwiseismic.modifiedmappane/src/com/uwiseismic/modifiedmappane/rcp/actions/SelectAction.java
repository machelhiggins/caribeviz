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
 * This action is triggered by the Select button on the JToolBar.
 * The user is able to select multiple features to be edited. 
 * Each selected feature is highlighted.
 */
public class SelectAction extends AbstractAction {

	private static final long serialVersionUID = 5845095410512178784L;
	private ImageIcon icon;
	JMapPaneRenderer map;

	public SelectAction(JMapPaneRenderer map) {
		URL url = null;
		try {
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane");
			URL fileURL = bundle.getEntry("resources/Add16.gif");
			url = FileLocator.toFileURL(fileURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		icon = new ImageIcon(url);
		this.putValue(Action.SMALL_ICON, icon);
		this.map = map;
	}

	public void actionPerformed(ActionEvent e) {
		map.enableSelecting(true);
		map.setState(JMapPaneRenderer.Select);
	}
}
