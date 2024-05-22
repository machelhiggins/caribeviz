  
package com.carrotsearch.hppc;

import static com.carrotsearch.hppc.Containers.*;
import static com.carrotsearch.hppc.HashContainers.*;

/**
 * Same as {@link ShortHashSet} but does not implement per-instance key mixing
 * strategy and uses a simpler (faster) bit distribution function.
 * 
 * <p>
 * <strong>Note:</strong> read about <a href="{@docRoot}
 * /overview-summary.html#scattervshash">important differences between hash and
 * scatter sets</a>.
 * </p>
 * 
 * @see ShortHashSet
 * @see <a href="{@docRoot}/overview-summary.html#interfaces">HPPC interfaces diagram</a> 
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeScatterSet.java") 
public class ShortScatterSet extends ShortHashSet {
  /**
   * New instance with sane defaults.
   */
  public ShortScatterSet() {
    this(DEFAULT_EXPECTED_ELEMENTS, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  public ShortScatterSet(int expectedElements) {
    this(expectedElements, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  @SuppressWarnings("deprecation")
  public ShortScatterSet(int expectedElements, double loadFactor) {
    super(expectedElements, loadFactor, HashOrderMixing.none());
  }

    protected  
  int hashKey(short key) {
    return BitMixer.mixPhi(key);
  }
  
  /**
   * Create a set from a variable number of arguments or an array of
   * <code>short</code>. The elements are copied from the argument to the
   * internal buffer.
   */
  /*  */
  public static  ShortScatterSet from(short... elements) {
    final ShortScatterSet set = new ShortScatterSet(elements.length);
    set.addAll(elements);
    return set;
  }
}