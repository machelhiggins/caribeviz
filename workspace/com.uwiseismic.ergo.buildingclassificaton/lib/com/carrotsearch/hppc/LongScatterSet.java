  
package com.carrotsearch.hppc;

import static com.carrotsearch.hppc.Containers.*;
import static com.carrotsearch.hppc.HashContainers.*;

/**
 * Same as {@link LongHashSet} but does not implement per-instance key mixing
 * strategy and uses a simpler (faster) bit distribution function.
 * 
 * <p>
 * <strong>Note:</strong> read about <a href="{@docRoot}
 * /overview-summary.html#scattervshash">important differences between hash and
 * scatter sets</a>.
 * </p>
 * 
 * @see LongHashSet
 * @see <a href="{@docRoot}/overview-summary.html#interfaces">HPPC interfaces diagram</a> 
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeScatterSet.java") 
public class LongScatterSet extends LongHashSet {
  /**
   * New instance with sane defaults.
   */
  public LongScatterSet() {
    this(DEFAULT_EXPECTED_ELEMENTS, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  public LongScatterSet(int expectedElements) {
    this(expectedElements, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  @SuppressWarnings("deprecation")
  public LongScatterSet(int expectedElements, double loadFactor) {
    super(expectedElements, loadFactor, HashOrderMixing.none());
  }

    protected  
  int hashKey(long key) {
    return BitMixer.mixPhi(key);
  }
  
  /**
   * Create a set from a variable number of arguments or an array of
   * <code>long</code>. The elements are copied from the argument to the
   * internal buffer.
   */
  /*  */
  public static  LongScatterSet from(long... elements) {
    final LongScatterSet set = new LongScatterSet(elements.length);
    set.addAll(elements);
    return set;
  }
}