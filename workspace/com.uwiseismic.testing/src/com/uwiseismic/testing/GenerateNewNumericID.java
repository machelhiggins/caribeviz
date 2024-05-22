package com.uwiseismic.testing;

public class GenerateNewNumericID {

	public static int getNewID(int digitSize){
		return (int)Math.ceil(Math.random()*Math.pow(10, digitSize));
	}
}
