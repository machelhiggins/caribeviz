package com.uwiseismic.ergo.buildingclassifier;

public class BuildingClassificationThread extends Thread {
	
	private BuildingClassification bc;
	private boolean isRunning = false;
	private StructureNotInEdException sntedExcept;
	private NoProbabilityFunctionException noProbExcept;
	
	public BuildingClassificationThread(BuildingClassification bc){
		this.bc = bc;
	}
	
	public void run() {
		try {

			isRunning = true;			
			bc.determineStructure(true, true);
			isRunning = false;

		} catch (StructureNotInEdException e){
			sntedExcept = e;
		}catch (NoProbabilityFunctionException e) {
			// TODO Auto-generated catch block
			noProbExcept = e;
		}
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public void wereThereExeptionsDuringRun() throws StructureNotInEdException, NoProbabilityFunctionException{
		// ** order is always NoProbabilityFunctionException first
		if(noProbExcept != null)
			throw noProbExcept;
		if(sntedExcept != null)
			throw sntedExcept;

	}
}
