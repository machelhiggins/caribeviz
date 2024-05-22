package com.uwiseismic.ergo.buildingclassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class HistogramNormalizeEDStruct {

	public static int RM1 = 0;
	public static int RM2 = 1;
	public static int W1 = 2;
	public static int PC2 = 3;
	public static int C1 = 4;
	public static int C2 = 5;
	public static int C3 = 6;
	public static int URM = 7;
	public static int S = 8;
			
	/**
	 *Perform histogram equalization per structure type.
	 *TODO: Explain further
	 * 
	 * @param edsFuncs
	 */
	public void performHistogramEqualization(ArrayList <EDStructTypeRankingFunction>edsFuncs){

		//** create values arrays for our EDStructTypeRankingFunction
		ScoreWrapper rmTwo[] = new ScoreWrapper[edsFuncs.size()];
		ScoreWrapper w1[] = new ScoreWrapper[edsFuncs.size()];
		ScoreWrapper pc[] = new ScoreWrapper[edsFuncs.size()];
		ScoreWrapper cOne[] = new ScoreWrapper[edsFuncs.size()];
		ScoreWrapper cTwo[] = new ScoreWrapper[edsFuncs.size()];
		ScoreWrapper cThree[] = new ScoreWrapper[edsFuncs.size()];
		ScoreWrapper urm[] = new ScoreWrapper[edsFuncs.size()];
		ScoreWrapper s[] = new ScoreWrapper[edsFuncs.size()];
		int n =0;
		for(Iterator<EDStructTypeRankingFunction>i = edsFuncs.iterator();i.hasNext();){
			EDStructTypeRankingFunction ed = i.next();	
			ed.setRecalculateScores(false);
			rmTwo[n] = new ScoreWrapper(ed,RM2, ed.getRmTwoScore());
			w1[n] = new ScoreWrapper(ed,W1, ed.getW1Score());
			pc[n] = new ScoreWrapper(ed,PC2, ed.getPCScore());
			//System.out.println(ed.getPCScore());
			cOne[n] = new ScoreWrapper(ed, C1, ed.getCOneScore());
			cTwo[n] = new ScoreWrapper(ed, C2, ed.getcTwoScore());
			cThree[n] = new ScoreWrapper(ed,C3, ed.getcThreeScore());
			urm[n] = new ScoreWrapper(ed,URM, ed.getURMScore());
			s[n] = new ScoreWrapper(ed,S, ed.getSScore());
			n++;
		}	
		try{
			histogramEqualizePerStruct(rmTwo);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			histogramEqualizePerStruct(w1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			histogramEqualizePerStruct(pc);
		}catch(Exception ex){
			ex.printStackTrace();
			for(int z =0; z <pc.length; z++)
				System.err.println(pc[z].getScore());
			System.exit(1);
		}
		try{
			histogramEqualizePerStruct(cOne);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			histogramEqualizePerStruct(cTwo);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			histogramEqualizePerStruct(cThree);
		}catch(Exception ex){
				ex.printStackTrace();
		}
		try{
			histogramEqualizePerStruct(urm);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			histogramEqualizePerStruct(s);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}
	
	private void histogramEqualizePerStruct(ScoreWrapper vals[]){
		Arrays.sort(vals);
				
		double bins[] = new double[vals.length];
		double freq[] = new double[vals.length];		
		int freqIndex = 0;
		double cdfIndices[] = new double[vals.length];
		
		int i;
		for(i =0; i<bins.length;i++){
			freq[i] = 0;
			bins[i] = 0;
		}		
		
		double val;
		double oldVal = -Double.MIN_VALUE;
		for(int m =0; m < vals.length; m++){			
			val = vals[m].getScore();
			for(i = 0; i < bins.length; i++){
				if(bins[i] == val){
					freq[i]++;					
					break;
				}
			}
			cdfIndices[m] = 0;
			if(i == bins.length){				
				bins[freqIndex] = val;
				freq[freqIndex]++;								
				if(m == 1 && oldVal != val)
					cdfIndices[m-1] = freq[freqIndex-1];
				else if(m == 0)
					cdfIndices[m] = 0;
				else if(m+1 == vals.length )
					cdfIndices[m] = freq[freqIndex];
				else{
					//System.out.println(m+"\t"+freqIndex);
					if(freqIndex == 0)
						cdfIndices[m-1] = freq[freqIndex];
					else
						cdfIndices[m-1] = freq[freqIndex-1];
				}
					
				freqIndex++;				
			}			
			else if(m+1 == vals.length ){
				cdfIndices[m] = freq[freqIndex-1];
			}
			
			oldVal = val;
		}
		double sum = 0;
		for(int m = 0; m < cdfIndices.length; m++){
			sum += cdfIndices[m];			
			vals[m].setScore((float)sum/(float)vals.length);			
		}		
	}
	
	class ScoreWrapper implements Comparable{
		
		private int structType;
		private double score = 0;
		private EDStructTypeRankingFunction myEdStructType;
		
		public ScoreWrapper(EDStructTypeRankingFunction myEdStructType, int structType, double score){
			this.myEdStructType = myEdStructType;
			this.score = score;
			this.structType = structType;
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {

			if(structType == RM1){
				myEdStructType.setRmOneScore(score);
			}if(structType == RM2){
				myEdStructType.setRmTwoScore(score);
			}if(structType == W1){
				myEdStructType.setW1Score(score);
			}if(structType == PC2){
				myEdStructType.setPCScore(score);				
			}if(structType == C1){
				myEdStructType.setcOneScore(score);				
			}if(structType == C2){
				myEdStructType.setcTwoScore(score);
			}if(structType == C3){
				myEdStructType.setcThreeScore(score);
			}if(structType == URM){
				myEdStructType.setURMScore(score);
			}if(structType == S){
				myEdStructType.setSScore(score);
			}
	
			this.score = score;
		}

		public int compareTo(Object o) {
			ScoreWrapper obj = (ScoreWrapper)o;
			if(obj.getScore() > score)
				return -1;
			else if(obj.getScore() < score)
				return 1;
			return 0;
		}
		
		

		
	}
}
