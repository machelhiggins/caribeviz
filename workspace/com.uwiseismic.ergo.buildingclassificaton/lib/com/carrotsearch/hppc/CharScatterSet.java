  
package com.carrotsearch.hppc;

import static com.carrotsearch.hppc.Containers.*;
import static com.carrotsearch.hppc.HashContainers.*;

/**
 * Same as {@link CharHashSet} but does not implement per-instance key mixing
 * strategy and uses a simpler (faster) bit distribution function.
 * 
 * <p>
 * <strong>Note:</strong> read about <a href="{@docRoot}
 * /overview-summary.html#scattervshash">important differences between hash and
 * scatter sets</a>.
 * </p>
 * 
 * @see CharHashSet
 * @see <a href="{@docRoot}/overview-summary.html#interfaces">HPPC interfaces diagram</a> 
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeScatterSet.java") 
public class CharScatterSet extends CharHashSet {
  /**
   * New instance with sane defaults.
   */
  public CharScatterSet() {
    this(DEFAULT_EXPECTED_ELEMENTS, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  public CharScatterSet(int expectedElements) {
    this(expectedElements, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  @SuppressWarnings("deprecation")
  public CharScatterSet(int expectedElements, double loadFactor) {
    super(expectedElements, loadFactor, HashOrderMixing.none());
  }

    protected  
  int hashKey(char key) {
    return BitMixer.mixPhi(key);
  }
  
  /**
   * Create a set from a variable number of arguments or an array of
   * <code>char</code>. The elements are copied from the argument to the
   * internal buffer.
   */
  /*  */
  public static  CharScatterSet from(char... elements) {
    final CharScatterSet set = new CharScatterSet(elements.length);
    set.addAll(elements);
    return set;
  }
}