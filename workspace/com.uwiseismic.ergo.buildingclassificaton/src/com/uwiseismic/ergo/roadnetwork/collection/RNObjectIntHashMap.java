package com.uwiseismic.ergo.roadnetwork.collection;

import com.carrotsearch.hppc.HashOrderMixing;
import com.carrotsearch.hppc.HashOrderMixingStrategy;
import com.carrotsearch.hppc.ObjectIntAssociativeContainer;
import com.carrotsearch.hppc.ObjectIntHashMap;



/**
 * stolen from GraphHopper. Did not use library because it simply doesn't work and my geotools is too old
 */
public class RNObjectIntHashMap<T> extends ObjectIntHashMap<T> {
	
    public RNObjectIntHashMap() {
        super(10, 0.75f, RNIntObjectHashMap.DETERMINISTIC);
    }

    public RNObjectIntHashMap(int capacity) {
        super(capacity, 0.75f, RNIntObjectHashMap.DETERMINISTIC);
    }

    public RNObjectIntHashMap(int capacity, double loadFactor) {
        super(capacity, loadFactor, RNIntObjectHashMap.DETERMINISTIC);
    }

    public RNObjectIntHashMap(int capacity, double loadFactor, HashOrderMixingStrategy hashOrderMixer) {
        super(capacity, loadFactor, hashOrderMixer);
    }

    public RNObjectIntHashMap(ObjectIntAssociativeContainer container) {
        this(container.size());
        putAll(container);
    }
}
