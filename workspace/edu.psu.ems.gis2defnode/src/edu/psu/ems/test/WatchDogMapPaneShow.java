package edu.psu.ems.test;

import edu.psu.ems.gis2defnode.geotools.EMSModifiedMapPane;

public class WatchDogMapPaneShow extends Thread{
	
	private EMSModifiedMapPane mapPane;
	public WatchDogMapPaneShow(EMSModifiedMapPane mapPane){
		this.mapPane = mapPane;
	}
	
	public void run(){
		try{
			wait(5000);
		}catch(Exception ex){}
		mapPane.setSize(mapPane.getParent().getSize());
		mapPane.setVisible(true);
	}

}
