package com.uwiseismic.ergo.buildingclassifier;


public class StructureProbabilities {
	public double rm = 0;
	public double rm1 = 0;
	public double rm2 = 0;
	public double c1 = 0;
	public double c2 = 0;
	public double w1 = 0;
	public double s1 = 0;
	public double pc1 = 0;
	
	public void setStructureProbability(String name, double prob){
		if(name.matches("rm1"))
			rm1 = prob;
		else if(name.matches("rm2"))
			rm2 = prob;
		else if(name.matches("rm"))
			rm = prob;
		else if(name.matches("c1"))
			c1 = prob;
		else if(name.matches("c2"))
			c2 = prob;
		else if(name.matches("pc1"))
			pc1 = prob;
		else if(name.matches("w1"))
			w1 = prob;
		else if(name.matches("s1"))
			s1 = prob;				
	}
	
	public void setStructureProbability(int name, double prob){
		if(name == ProbConstants.RM)
			rm = prob;
		else if(name == ProbConstants.RM1)
			rm1 = prob;
		else if(name == ProbConstants.RM2)
			rm2 = prob;
		else if(name == ProbConstants.C1)
			c1 = prob;
		else if(name == ProbConstants.C2)
			c2 = prob;
		else if(name == ProbConstants.PC1)
			pc1 = prob;
		else if(name == ProbConstants.W1)
			w1 = prob;
		else if(name == ProbConstants.S1)
			s1 = prob;
	}
	
	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append("rm = ");
		s.append(rm);
		s.append(", rm1 = ");
		s.append(rm1);
		s.append(", rm2 = ");
		s.append(rm2);
		s.append(", c1 = ");
		s.append(c1);
		s.append(", c2 = ");
		s.append(c2);
		s.append(", w1 = ");
		s.append(w1);
		s.append(", s1 = ");
		s.append(s1);
		s.append(", pc1 ="); 
		s.append(pc1); 
		return s.toString();
	}
}
