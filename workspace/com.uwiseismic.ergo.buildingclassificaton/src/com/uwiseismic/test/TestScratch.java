package com.uwiseismic.test;

public class TestScratch {

	public static void main(String[] args) {
		java.text.DecimalFormat formatter = new java.text.DecimalFormat("0.000#");
		double a = 0.1234456789;
		System.out.println(formatter.format(a));
		
		String occType = "RES1";
		if(occType.matches("RES1")){
			System.out.println("Guess what, it macthes");
		}

	}

}
