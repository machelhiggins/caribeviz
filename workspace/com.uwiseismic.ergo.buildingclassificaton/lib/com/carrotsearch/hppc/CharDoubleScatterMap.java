  
package com.carrotsearch.hppc;

import static com.carrotsearch.hppc.Containers.*;
import static com.carrotsearch.hppc.HashContainers.*;

/**
 * Same as {@link CharDoubleHashMap} but does not implement per-instance
 * key mixing strategy and uses a simpler (faster) bit distribution function.
 * 
 * <p><strong>Note:</strong> read about 
 * <a href="{@docRoot}/overview-summary.html#scattervshash">important differences 
 * between hash and scatter sets</a>.</p>
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:06+0200",
    value = "KTypeVTypeScatterMap.java") 
public class CharDoubleScatterMap extends CharDoubleHashMap
{
  /**
   * New instance with sane defaults.
   */
  public CharDoubleScatterMap() {
    this(DEFAULT_EXPECTED_ELEMENTS);
  }

  /**
   * New instance with sane defaults.
   */
  public CharDoubleScatterMap(int expectedElements) {
    this(expectedElements, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  @SuppressWarnings("deprecation")
  public CharDoubleScatterMap(int expectedElements, double loadFactor) {
    super(expectedElements, loadFactor, HashOrderMixing.none());
  }

    protected  
  int hashKey(char key) {
    return BitMixer.mixPhi(key);
  }

  /**
   * Creates a hash map from two index-aligned arrays of key-value pairs.
   */
  public static  CharDoubleScatterMap from(char[] keys, double[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }

    CharDoubleScatterMap map = new CharDoubleScatterMap(keys.length);
    for (int i = 0; i < keys.length; i++) {
      map.put(keys[i], values[i]);
    }

    return map;
  }
}
