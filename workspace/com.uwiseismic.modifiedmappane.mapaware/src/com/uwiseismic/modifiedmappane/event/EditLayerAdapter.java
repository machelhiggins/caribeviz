package com.uwiseismic.modifiedmappane.event;

import java.util.Vector;

import org.geotools.map.Layer;


public class EditLayerAdapter {
	
	private Vector<EditLayerListener> listeners = new Vector<EditLayerListener>();
	
	
	public void setLayerToEdited(Layer layer){
		for(EditLayerListener listener : listeners)
			listener.editLayer(layer);
	}
	
	public void disableLayerToEdit(Layer layer){
		for(EditLayerListener listener : listeners)
			listener.disableEdit(layer);
	}
	
	public void addListener(EditLayerListener listener){
		listeners.add(listener);
	}
	

}
