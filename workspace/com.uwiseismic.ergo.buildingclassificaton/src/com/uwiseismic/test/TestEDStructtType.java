package com.uwiseismic.test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.uwiseismic.ergo.buildingclassifier.EDStructTypeRankingFunction;
import com.uwiseismic.ergo.buildingclassifier.HistogramNormalizeEDStruct;

public class TestEDStructtType {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap <String, EDStructTypeRankingFunction>eds = new HashMap<String,EDStructTypeRankingFunction>();
		//ED	BLDG_CNT	POP	AREA	BLDG_CNT\AREA	AREA/BLDG_CNT	POP/AREA	AREA/POP	POP/BLDG_CNT
		//0		1			2			3				4				5			6			7
		try{
			BufferedReader reader = new BufferedReader(new FileReader("C:\\Projects\\Scratch\\survey_building_fprint_analysis\\building_cnt_pop_area_ratios.txt"));
			String line;
			String c[];
			double areaBldgCount = 0;
			double maxAreaBldgCount = Double.MIN_VALUE;
			double bldgCount = 0;
			double maxBldgCount = Double.MIN_VALUE;
			double meanGSQFT = 0;
			double maxMeanGSQFT = Double.MIN_VALUE;
			double meanUnimpLandVal = 0;
			double maxMeanUnimpLandVal = Double.MIN_VALUE;
			double pop =0;
			double maxPop = Double.MIN_VALUE;
			double popArea = 0;	
			double maxPopArea = Double.MIN_VALUE;
			double popBldgCount = 0;	
			double maxPopBldgCount = Double.MIN_VALUE;
			double stdGSQFT = 0;
			double maxstdGSQFT = Double.MIN_VALUE;
			double stdUnimpLandVal = 0;
			double maxSTDUnimpLandVal = Double.MIN_VALUE;
			while((line = reader.readLine()) != null){
				c = line.split("\\t");
				EDStructTypeRankingFunction ed = eds.get(c[0]);
				if(ed == null){
					ed = new EDStructTypeRankingFunction();
					ed.setID(c[0]);
					eds.put(ed.getID(), ed);
				}
				bldgCount = Double.parseDouble(c[1]);
				pop = Double.parseDouble(c[2]);
				ed.setPop(pop);
				areaBldgCount = Double.parseDouble(c[4]);
				ed.setAreaBldgCount(areaBldgCount);
				popArea = Double.parseDouble(c[5]);
				ed.setPopArea(popArea);
				popBldgCount = Double.parseDouble(c[6]);
				ed.setPopBldgCount(popBldgCount);
				meanGSQFT = Double.parseDouble(c[7]);	
				ed.setMeanGSQFT(meanGSQFT);
				stdGSQFT = Double.parseDouble(c[8]);
				ed.setStdGSQFT(stdGSQFT);
				
				if(bldgCount > maxBldgCount)
					maxBldgCount = bldgCount;
				if(pop > maxPop)
					maxPop = pop;
				if(areaBldgCount > maxAreaBldgCount)
					maxAreaBldgCount = areaBldgCount;
				if(popArea > maxPopArea)
					maxPopArea = popArea;
				if(popBldgCount > maxPopBldgCount)
					maxPopBldgCount = popBldgCount;
				if(meanGSQFT > maxMeanGSQFT)
					maxMeanGSQFT = meanGSQFT;
				if(stdGSQFT > maxstdGSQFT)
					maxstdGSQFT = stdGSQFT;							
			}
			reader.close();
			
			reader = new BufferedReader(new FileReader("C:\\Projects\\Scratch\\survey_building_fprint_analysis\\unimproved_land_value_ed.txt"));
			reader.readLine();//** skip header
			while((line = reader.readLine()) != null){
				c = line.split("\\t");
				EDStructTypeRankingFunction ed = eds.get(c[0]);
				if(ed == null){
					ed = new EDStructTypeRankingFunction();
					ed.setID(c[0]);
					eds.put(ed.getID(), ed);
				}
				meanUnimpLandVal = Double.parseDouble(c[1]);
				ed.setMeanUnimpLandVal(meanUnimpLandVal);
				stdUnimpLandVal = Double.parseDouble(c[2]);
				ed.setStdUnimpLandVal(stdUnimpLandVal);
				
				if(meanUnimpLandVal > maxstdGSQFT)
					maxMeanUnimpLandVal = meanUnimpLandVal;
				if(stdUnimpLandVal > maxSTDUnimpLandVal)
					maxSTDUnimpLandVal = stdUnimpLandVal;								
			}
			reader.close();
			
			System.out.println("ED	RM1	RM2	C1	C2	C3	W1	S3	URML");
			for(Iterator<EDStructTypeRankingFunction>i = eds.values().iterator();i.hasNext();){
				EDStructTypeRankingFunction ed = i.next();
				ed.setAreaBldgCount(ed.getAreaBldgCount()/maxBldgCount);
				ed.setMeanGSQFT(ed.getMeanGSQFT()/maxMeanGSQFT);
				ed.setMeanUnimpLandVal(ed.getMeanUnimpLandVal()/maxMeanUnimpLandVal);
				ed.setPop(ed.getPop()/maxPop);
				ed.setPopArea(ed.getPopArea()/maxPopArea);
				ed.setPopBldgCount(ed.getPopBldgCount()/maxPopBldgCount);
				ed.setStdGSQFT(ed.getStdGSQFT()/maxstdGSQFT);
				ed.setStdUnimpLandVal(ed.getStdUnimpLandVal()/maxSTDUnimpLandVal);
//				System.out.println(ed.getED()+"\t"+ed.getRmOneScore()+"\t"
//						+ed.getRmTwoScore()+"\t"
//						+ed.getCOneScore()+"\t"
//						+ed.getcTwoScore()+"\t"
//						+ed.getcThreeScore()+"\t"
//						+ed.getW1Score()+"\t"
//						+ed.getS3Score()+"\t"
//						+ed.getUrmLScore());
			}
			System.out.println("****************HE SCORES*****************");
			HistogramNormalizeEDStruct he = new HistogramNormalizeEDStruct();
			he.performHistogramEqualization(new ArrayList<EDStructTypeRankingFunction>(eds.values()));			
			for(Iterator<EDStructTypeRankingFunction>i = eds.values().iterator();i.hasNext();){
				EDStructTypeRankingFunction ed = i.next();
				System.out.println(ed.getID()+"\t"+ed.getRmOneScore()+"\t"
						+ed.getRmTwoScore()+"\t"
						+ed.getCOneScore()+"\t"
						+ed.getcTwoScore()+"\t"
						+ed.getcThreeScore()+"\t"
						+ed.getW1Score()+"\t"
						+ed.getSScore()+"\t"
						+ed.getURMScore());
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}

}
