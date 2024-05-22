  
package com.carrotsearch.hppc;

/*  */

import static com.carrotsearch.hppc.Containers.*;
import static com.carrotsearch.hppc.HashContainers.*;

/**
 * An identity hash map of <code>Object</code> to <code>char</code>.
 */
  @SuppressWarnings("all")  
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:06+0200",
    value = "KTypeVTypeIdentityHashMap.java") 
public class ObjectCharIdentityHashMap<KType> 
  extends ObjectCharHashMap<KType>
{
  /**
   * New instance with sane defaults.
   */
  public ObjectCharIdentityHashMap() {
    this(DEFAULT_EXPECTED_ELEMENTS);
  }

  /**
   * New instance with sane defaults.
   * @param expectedElements
   *          The expected number of elements guaranteed not to cause buffer
   *          expansion (inclusive).
   */
  public ObjectCharIdentityHashMap(int expectedElements) {
    this(expectedElements, DEFAULT_LOAD_FACTOR);
  }

  /**
   * New instance with sane defaults.
   * 
   * @param expectedElements
   *          The expected number of elements guaranteed not to cause buffer
   *          expansion (inclusive).
   * @param loadFactor
   *          The load factor for internal buffers. Insane load factors (zero, full capacity)
   *          are rejected by {@link #verifyLoadFactor(double)}.
   */
  public ObjectCharIdentityHashMap(int expectedElements, double loadFactor) {
    this(expectedElements, loadFactor, HashOrderMixing.randomized());
  }

  /**
   * New instance with the provided defaults.
   * 
   * @param expectedElements
   *          The expected number of elements guaranteed not to cause a rehash (inclusive).
   * @param loadFactor
   *          The load factor for internal buffers. Insane load factors (zero, full capacity)
   *          are rejected by {@link #verifyLoadFactor(double)}.
   * @param orderMixer
   *          Hash key order mixing strategy. See {@link HashOrderMixing} for predefined
   *          implementations. Use constant mixers only if you understand the potential
   *          consequences.
   */
  public ObjectCharIdentityHashMap(int expectedElements, double loadFactor, HashOrderMixingStrategy orderMixer) {
    this.orderMixer = orderMixer;
    this.loadFactor = verifyLoadFactor(loadFactor);
    ensureCapacity(expectedElements);
  }

  /**
   * Create a hash map from all key-value pairs of another container.
   */
  public ObjectCharIdentityHashMap(ObjectCharAssociativeContainer<? extends KType> container) {
    this(container.size());
    putAll(container);
  }

  @Override
  public int hashKey(KType key) {
    assert !((key) == null); // Handled as a special case (empty slot marker).
    return BitMixer.mix(System.identityHashCode(key), this.keyMixer);
  }

  @Override
  public boolean equals(Object v1, Object v2) {
    return v1 == v2;
  }

  /*  */

  /**
   * Creates a hash map from two index-aligned arrays of key-value pairs.
   */
  public static <KType> ObjectCharIdentityHashMap<KType> from(KType[] keys, char[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }

    ObjectCharIdentityHashMap<KType> map = new ObjectCharIdentityHashMap<>(keys.length);
    for (int i = 0; i < keys.length; i++) {
      map.put(keys[i], values[i]);
    }

    return map;
  }  
}
