package com.uwiseismic.ergo.roadnetwork;

public class RoadPathWeigher {

	public static double weighDistance(double distance, int weight, int maxWeight){
		return distance*1/Math.exp((double)weight/(5*maxWeight));
	}
	
	/**
	 * Testing 1,2,3
	 * @param args
	 */
	public static void main(String[] args) {
		int l = 20;
		double x[] = new double[l];
		for(int i =0; i <l; i++)
			x[i] = i*5;
		double weights[] = new double[]{3,2,1};
		for(int w = 0; w < weights.length; w++){
			for(int i = 0; i < x.length; i++){
				System.out.println("w["+weights[w]+"] x: "+x[i]+" y: "
						+weighDistance(x[i],(int)weights[w],3));
			}
		}
	}
}
