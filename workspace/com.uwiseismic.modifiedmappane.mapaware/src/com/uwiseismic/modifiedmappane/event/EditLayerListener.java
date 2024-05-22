package com.uwiseismic.modifiedmappane.event;

import org.geotools.map.Layer;

public interface EditLayerListener {
	
	 public void editLayer(Layer layer);
	 
	 public void disableEdit(Layer layer);

}
