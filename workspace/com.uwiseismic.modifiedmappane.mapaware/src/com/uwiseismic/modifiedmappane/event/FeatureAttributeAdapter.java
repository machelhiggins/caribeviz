package com.uwiseismic.modifiedmappane.event;

public interface FeatureAttributeAdapter {
	
	/**
	 * Fire event for listeners
	 */
	public void doLayerEditted();
	
	public void addFeatuerAttributeListener(FeatureAttributeListener listener);	

}
