package com.uwiseismic.modifiedmappane.mapawareDEFUNCT;

import java.util.HashMap;

import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;


//import edu.illinois.ncsa.ergo.gis.Dataset;

public class DatasetAware implements MapLayerListListener {
	
//	private HashMap<String, Dataset> datasets = new HashMap<String,Dataset>();
//	private Dataset edittingDS;
//	private int edittingLayer;

	private DatasetSave dsSaver = new DatasetSave();
	
	
	@Override
	public void layerAdded(MapLayerListEvent event) {
		// TODO Not doing anything here. 
		//** layers should be added from a MapContext only
		
	}

	@Override
	public void layerRemoved(MapLayerListEvent event) {
		// TODO Auto-generated method stub
//		int removed = event.getFromIndex();
//		datasets.remove(""+removed);
//		if(edittingLayer == removed)
			 
	}

	@Override
	public void layerChanged(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layerMoved(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layerPreDispose(MapLayerListEvent event) {
		// TODO Auto-generated method stub
		
	}

//	public void addedDataset(Dataset newDataset, int layerIndex){
//		datasets.put(""+layerIndex, newDataset);
//	}
//	
	public void setEditting(int index){
		
	}
	
	public void disableEditting(int index){
		
	}
	
//	public void disableEditting(Dataset ds){
//		
//	}
//	
//	private void setEditting(Dataset ds){
//		
//	}
	
	public void finalize(){
		dsSaver.close();
	}
	
}
