package com.uwiseismic.ergo.roadnetwork.collection;

import com.carrotsearch.hppc.HashOrderMixing;
import com.carrotsearch.hppc.HashOrderMixingStrategy;
import com.carrotsearch.hppc.IntObjectHashMap;

/**
 * @author machel
 *
 *stolen from GraphHopper. Did not use library because it simply doesn't work and my geotools is too old
 */
public class RNIntObjectHashMap<T> extends IntObjectHashMap<T> {
    
	public static final HashOrderMixingStrategy DETERMINISTIC = HashOrderMixing.constant(123321123321123312L);

    public RNIntObjectHashMap() {
        super(10, 0.75f, DETERMINISTIC);
    }

    public RNIntObjectHashMap(int capacity) {
        super(capacity, 0.75f, DETERMINISTIC);
    }

    public RNIntObjectHashMap(int capacity, double loadFactor) {
        super(capacity, loadFactor, DETERMINISTIC);
    }

    public RNIntObjectHashMap(int capacity, double loadFactor, HashOrderMixingStrategy hashOrderMixer) {
        super(capacity, loadFactor, hashOrderMixer);
    }
    
}

