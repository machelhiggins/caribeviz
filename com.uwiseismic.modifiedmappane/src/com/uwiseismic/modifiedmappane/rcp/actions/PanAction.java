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
 * This action is triggered by the following Pan buttons on the JToolBar; Left, Right, Up, Down, Free.
 * The Pan buttons; L, R, U, and D, all shift the entire map in their respective directions.
 * Free pan allows the user to pan the map via a mouse click and drag.
 */
public class PanAction extends AbstractAction {

	private static final long serialVersionUID = 2718536128821468386L;
	private ImageIcon icon;
	JMapPaneRenderer map;

	/**
	 * Integer representation of pan direction:
	 * 0 = left
	 * 1 = right
	 * 2 = up
	 * 3 = down
	 * 4 = free = pan according to how much the mouse drags the map
	 */
	int direction;

	public PanAction(JMapPaneRenderer map, int dir) {
		URL url = null;
		try {
			Bundle bundle = Platform.getBundle("com.uwiseismic.modifiedmappane");
			direction = dir;
			URL fileURL = null;
			//Left
			if(dir == 0)fileURL = bundle.getEntry("resources/panL.png"); 
			//Right
			if(dir == 1)fileURL = bundle.getEntry("resources/panR.png"); 
			//Up
			if(dir == 2)fileURL = bundle.getEntry("resources/panU.png");  
			//Down
			if(dir == 3)fileURL = bundle.getEntry("resources/panD.png");  

			//Free
			if(dir == 4)fileURL = bundle.getEntry("resources/Pan16.gif"); 

			url = FileLocator.toFileURL(fileURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		icon = new ImageIcon(url);
		this.putValue(Action.SMALL_ICON, new ImageIcon(icon.getImage().getScaledInstance(16, 16, 0)));
		this.map = map;
	}

	/**
	 * the action occurred - set the state of the map pane to pan
	 */
	public void actionPerformed(ActionEvent e) {
		map.enableSelecting(false);
		if(direction == 4){
			map.setState(JMapPaneRenderer.PanFree);

			map.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			map.enablePanning(true);
		}

		if(direction == 0){
			map.setState(JMapPaneRenderer.PanLeft);
			map.panLeft();
		}
		if(direction == 1){
			map.setState(JMapPaneRenderer.PanRight);
			map.panRight();
		}
		if(direction == 2){
			map.setState(JMapPaneRenderer.PanUp);
			map.panUp();
		}
		if(direction == 3){
			map.setState(JMapPaneRenderer.PanDown);
			map.panDown();
		}

	}
}
