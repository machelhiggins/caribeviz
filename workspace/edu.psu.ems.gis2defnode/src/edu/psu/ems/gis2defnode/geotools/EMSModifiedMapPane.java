package edu.psu.ems.gis2defnode.geotools;

import java.awt.Graphics;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;

import com.uwiseismic.modifiedmappane.rcp.map.JMapPaneRenderer;

public class EMSModifiedMapPane {
	
	
	private Layer orginalLayer;
	private Layer connectionLayer;

	public void onMouseClicked(MapMouseEvent ev) {
		//print the screen and world position of the mouse
		System.out.println("mouse click at");
		System.out.printf("  screen: x=%d y=%d \n", ev.getX(), ev.getY());
	
		DirectPosition2D pos = ev.getWorldPos();
		System.out.printf("  world: x=%.2f y=%.2f \n", pos.x, pos.y);
	}
	
	public void onMouseEntered(MapMouseEvent ev) {
		System.out.println("mouse entered map pane");
	}
	
	public void onMouseExited(MapMouseEvent ev) {
		System.out.println("mouse left map pane");
	}

	public Layer getOrginalLayer() {
		return orginalLayer;
	}

	public void setOrginalLayer(Layer orginalLayer) {		
		this.getMapContent().removeLayer(this.orginalLayer);
		this.getMapContent().addLayer(orginalLayer);
		this.orginalLayer = orginalLayer;
		this.getMapContent().removeLayer(this.connectionLayer);
		this.show();
	}

	public Layer getConnectionLayer() {
		return connectionLayer;
	}

	public void setConnectionLayer(Layer connectionLayer) {
		this.connectionLayer = connectionLayer;
	}
	
	public void paint(Graphics g2){
		System.out.println("We're painting shit but...");
		
		super.paint(g2);
		
	}
	
	
	
}
