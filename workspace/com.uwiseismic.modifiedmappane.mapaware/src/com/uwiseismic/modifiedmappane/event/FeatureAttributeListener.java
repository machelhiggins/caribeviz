package com.uwiseismic.modifiedmappane.event;

import java.util.Vector;

import org.opengis.feature.simple.SimpleFeature;

public interface FeatureAttributeListener {
	
	public void featuresChange(Vector<SimpleFeature> featuresChanged);
			
	public void dispose();
	
}
