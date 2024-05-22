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
 * This action is triggered by the Query Satellite Image button on the JToolBar.
 * A query is sent to bing maps to retrieve the relevant satellite image to match the 
 * current footprints. This can be toggled on and off. 
 */
public class QuerySatAction extends AbstractAction {

	private static final long serialVersionUID = 6047513626563033938L;
	private ImageIcon icon;
	JMapPaneRenderer map;

	public QuerySatAction(JMapPaneRenderer map) {
		URL url = null;
		try {
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane");
			URL fileURL = bundle.getEntry("resources/sat.png");
			url = FileLocator.toFileURL(fileURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		icon = new ImageIcon(url);
		this.putValue(Action.SMALL_ICON, new ImageIcon(icon.getImage().getScaledInstance(16, 16, 0)));
		this.map = map;
	}

	public void actionPerformed(ActionEvent e) {      
		map.toggleQuerySatImages();
		map.repaint();
	}
}
