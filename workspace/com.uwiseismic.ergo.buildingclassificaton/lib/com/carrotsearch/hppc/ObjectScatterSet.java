  
package com.carrotsearch.hppc;

import static com.carrotsearch.hppc.Containers.*;
import static com.carrotsearch.hppc.HashContainers.*;

/**
 * Same as {@link ObjectHashSet} but does not implement per-instance key mixing
 * strategy and uses a simpler (faster) bit distribution function.
 * 
 * <p>
 * <strong>Note:</strong> read about <a href="{@docRoot}
 * /overview-summary.html#scattervshash">important differences between hash and
 * scatter sets</a>.
 * </p>
 * 
 * @see ObjectHashSet
 * @see <a href="{@docRoot}/overview-summary.html#interfaces">HPPC interfaces diagram</a> 
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeScatterSet.java") 
public class ObjectScatterSet<KType> extends ObjectHashSet<KType> {
  /**
   * New instance with sane defaults.
   */
  public ObjectScatterSet() {
    this(DEFAULT_EXPECTED_ELEMENTS, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  public ObjectScatterSet(int expectedElements) {
    this(expectedElements, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   */
  @SuppressWarnings("deprecation")
  public ObjectScatterSet(int expectedElements, double loadFactor) {
    super(expectedElements, loadFactor, HashOrderMixing.none());
  }

    protected  
  int hashKey(KType key) {
    return BitMixer.mixPhi(key);
  }
  
  /**
   * Create a set from a variable number of arguments or an array of
   * <code>Object</code>. The elements are copied from the argument to the
   * internal buffer.
   */
  /*  */
  @SafeVarargs
  /*  */
  public static <KType> ObjectScatterSet<KType> from(KType... elements) {
    final ObjectScatterSet<KType> set = new ObjectScatterSet<KType>(elements.length);
    set.addAll(elements);
    return set;
  }
}