package com.uwiseismic.test;

import com.uwiseismic.ergo.roadnetwork.collection.RNIntObjectHashMap;
import com.uwiseismic.gis.util.GenerateNewNumericID;
import com.uwiseismic.gis.util.SimplePoint;

/**
 * Just testing com.carrotsearch.hppc.IntObjectHashMap int to Obj map 
 * that changes in size by x^2, i.e. there will be empty (null) slots
 * and DO NOT trust the keys. Objects should be added with sequential 
 * int keys. Keep track of number of objects added et voila, super fast
 * int to object HashMap.
 * 
 * @author machel
 *
 */
public class TestIntObjHashMap {

	public static void main(String[] args) {
		int capacity = (int) Math.pow(2,6);
		RNIntObjectHashMap<SimplePoint> map = new RNIntObjectHashMap<SimplePoint>(128, 0.9f);		
		int n = 0;
		for(; n < capacity; n++){
			int nextID = GenerateNewNumericID.getNewID(6);
			SimplePoint point = new SimplePoint(nextID, n+1, n+1);
			map.putIfAbsent(n, point);		
			//map.put(n, new Double(n+1));
		}
		
		System.out.println("size of map: "+map.size()+" after "+n+" iterations");
		int keys[] = map.keys;
		for(int z = 0; z < map.keys.length; z++)
			System.out.println("Integer key"+keys[z]);
		int collisions = 0;
		for(int z = 0; z < map.keys.length; z++){
			for(int k = 0; k < map.keys.length; k++){
				if(z != k && map.keys[z] == map.keys[k]){
					//System.out.println("Got key collision keys(z) = keys(k) = "+keys[z]+"\t"+keys[k]);
					collisions++;
				}
			}			
		}
		System.out.println("Collisions: "+collisions);
		for(int z = 0; z < map.size(); z++){
				System.out.println("["+z+"]"+map.get(z));
		}
	}

}
