  
package com.carrotsearch.hppc.cursors;

/**
 * A cursor over entries of an associative container (Object keys and double
 * values).
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:04+0200",
    value = "KTypeVTypeCursor.java") 
public final class ObjectDoubleCursor<KType> {
  /**
   * The current key and value's index in the container this cursor belongs to.
   * The meaning of this index is defined by the container (usually it will be
   * an index in the underlying storage buffer).
   */
  public int index;

  /**
   * The current key.
   */
  public KType key;

  /**
   * The current value.
   */
  public double value;

  @Override
  public String toString() {
    return "[cursor, index: " + index + ", key: " + key + ", value: " + value + "]";
  }
}
