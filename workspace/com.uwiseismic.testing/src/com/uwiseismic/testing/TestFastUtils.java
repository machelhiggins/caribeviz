package com.uwiseismic.testing;

import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public class TestFastUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int capacity = (int) Math.pow(2,6);
		Int2ObjectOpenHashMap<SimplePoint> map = new Int2ObjectOpenHashMap<SimplePoint>();		
		int n = 0;
		for(; n < capacity; n++){
			int nextID = GenerateNewNumericID.getNewID(6);
			SimplePoint point = new SimplePoint(nextID, n+1, n+1);
			map.putIfAbsent(n, point);		
		}
		map.trim();
		System.out.println("size of map: "+map.size()+" after "+n+" iterations");
		int keys[] = new int[map.size()];
		map.keySet().toArray(keys);
		int collisions = 0;
		for(int z = 0; z < keys.length; z++){
			for(int k = 0; k < keys.length; k++){
				if(z != k && keys[z] == keys[k]){
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
