package com.uwiseismic.ergo.buildingclassifier;

public class ProbConstants {
	
	//** type of probnability function
	public static final String LOGISTIC_FUNCTION = "logistic";
	public static final String LOGIC_TREE = "logic_tree";
	public static final String RATIO_FUNCTION = "ratio";
	public static final int LOGISTIC_FUNCTION_TYPE = 0;
	public static final int LOGIC_TREE_FUNCTION_TYPE = 1;
	public static final int RATIO_FUNCTION_TYPE = 2;
	
	//** logic operators in XML file
	public static final String EQ = "EQ";
	public static final String GT = "GT";
	public static final String LT = "LT";
	public static final String GTEQ = "GTEQ";
	public static final String LTEQ = "LTEQ";
	public static final String AND = "AND";
	public static final String OR = "OR";	
	public static final String OPERATORS[] = {EQ, GT, LT, GTEQ, LTEQ, AND, OR}; 
	
	//** statistical values used in logic tree
	public static final String MEAN = "mean";
	public static final String MODE = "mode";
	public static final String SIGMA = "sigma";
	public static final String MEAN_MINUS_SIGMA = "mean-sigma";
	public static final String UPPER_NINETY_PERCENTILE = "upper90";
	public static final String UPPER_EIGHTY_PERCENTILE = "upper80";
	public static final String UPPER_SEVENTY_PERCENTILE = "upper70";
	public static final String MEAN_PLUS_SIGMA = "mean+sigma";
	
	//** the metrics that contribute probability
	//** NB: THE following integers must me sequential, this is 
	//** being used as an array index elsewhere!!!!!!!!!!
	public static final  int RP = 0;
	public static final  int BA = 1;
	public static final  int BAN = 2;
	public static final  int BSR = 3;
	public static final  int BSRN = 4;
	public static final  int INZ = 5;
	public static final  int WR = 6;
	public static final String RP_STR = "rp";
	public static final String BA_STR = "ba";
	public static final String BAN_STR = "ban";
	public static final String BSR_STR = "bsr";
	public static final String BSRN_STR = "bsrn";
	public static final String INZ_STR = "inz";	
	public static final String WR_STR = "wr";
	public static final String METRICS_STRINGS[]  = { RP_STR, BA_STR, BAN_STR, BSR_STR, BSRN_STR, INZ_STR, WR_STR };
	public static final String METRICS_DESCRIPTIONS[] = { //****** TODO: REMOVE AND PULL  THIS FROM XML FILE
			"Major Road Proxmity Index",
			"Building Footprint Area Metric",
			"Building Footprint Area Similarity to Neighbors Metric",
			"Building Footprint Shape Regularity Metric",
			"Building Footprint Shape Regulartiy Similarity to Neighbors Metric",
			"Income Zone Metric",
			"Ratio of Wood (W1) Structures"
	};
	
	//** NB: THE following integers must me sequential, this is 
	//** being used as an array index somewhere else!!!!!!!!!!
	public static final  int UNKNOWN = -1;
	public static final  int RM = 0;
	public static final  int RM1 = 1;
	public static final  int RM2 = 2;
	public static final  int C1 = 3;
	public static final  int C2 = 4;
	public static final  int W1 = 5;
	public static final  int S1 = 6;
	public static final  int PC1 = 7;
	public static final  int URM = 8;
	public static final  int PC2 = 9;
	public static final String STRUCT_TYPES[]  = {"RM", "RM1", "RM2", "C1", "C2", "W1", "S1", "PC1", "URM", "PC2"};
	
	public static String getMetricName(int metric){
		return METRICS_STRINGS[metric];
	}
	
	public static int getMetricConstant(String name){
		name = name.toLowerCase();
		for(int i = 0; i < METRICS_STRINGS.length; i++){
			if(METRICS_STRINGS[i].equals(name))
				return i;
		}
		return -1;
	}
	
	public static String getStructureType(int structInt){
		return STRUCT_TYPES[structInt];
	}
	
	public static int getStructureType(String structStr){
		structStr = structStr.toUpperCase();
		for(int i = 0; i < STRUCT_TYPES.length; i++){
			if(STRUCT_TYPES[i].equals(structStr))
				return i;
		}
		System.out.println("Structure type string "+structStr);
		return UNKNOWN;
	}
	
	public static String verifyMetricType(String m){
		m = m.toLowerCase();
		for(int i = 0; i < METRICS_STRINGS.length; i++){
			if(METRICS_STRINGS[i].equals(m))
				return METRICS_STRINGS[i];
		}
		return null;
	}
	
	public static String verifyFunctionType(String m){
		m = m.toLowerCase();
		if(LOGISTIC_FUNCTION.equals(m))
			return LOGISTIC_FUNCTION;
		else if(LOGIC_TREE.equals(m))
			return LOGIC_TREE;
		else if(RATIO_FUNCTION.equals(m))
			return RATIO_FUNCTION;
		return null;
	}
	
	public static String verifyLogicOperators(String m){
		m = m.toUpperCase();
		for(int i = 0; i < OPERATORS.length; i++){
			if(OPERATORS[i].equals(m))
				return OPERATORS[i];
		}
		return null;
	}
	
	
}
