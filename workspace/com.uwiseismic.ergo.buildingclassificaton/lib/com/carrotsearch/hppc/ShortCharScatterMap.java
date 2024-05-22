  
package com.carrotsearch.hppc;

import static com.carrotsearch.hppc.Containers.*;
import static com.carrotsearch.hppc.HashContainers.*;

/**
 * Same as {@link ShortCharHashMap} but does not implement per-instance
 * key mixing strategy and uses a simpler (faster) bit distribution function.
 * 
 * <p><strong>Note:</strong> read about 
 * <a href="{@docRoot}/overview-summary.html#scattervshash">important differences 
 * between hash and scatter sets</a>.</p>
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:06+0200",
    value = "KTypeVTypeScatterMap.java") 
public class ShortCharScatterMap extends ShortCharHashMap
{
  /**
   * New instance with sane defaults.
   */
  public ShortCharScatterMap() {
    this(DEFAULT_EXPECTED_ELEMENTS);
  }

  /**
   * New instance with sane defaults.
   */
  public ShortCharScatterMap(int expectedElements) {
    this(expectedElements, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  @SuppressWarnings("deprecation")
  public ShortCharScatterMap(int expectedElements, double loadFactor) {
    super(expectedElements, loadFactor, HashOrderMixing.none());
  }

    protected  
  int hashKey(short key) {
    return BitMixer.mixPhi(key);
  }

  /**
   * Creates a hash map from two index-aligned arrays of key-value pairs.
   */
  public static  ShortCharScatterMap from(short[] keys, char[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }

    ShortCharScatterMap map = new ShortCharScatterMap(keys.length);
    for (int i = 0; i < keys.length; i++) {
      map.put(keys[i], values[i]);
    }

    return map;
  }
}
