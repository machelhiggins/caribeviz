  
package com.carrotsearch.hppc;

import com.carrotsearch.hppc.cursors.LongFloatCursor;

/**
 * An associative container with unique binding from keys to a single value.
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:06+0200",
    value = "KTypeVTypeMap.java") 
public interface LongFloatMap extends LongFloatAssociativeContainer {
  /**
   * @return Returns the value associated with the given key or the default
   *         value for the value type, if the key is not associated with any
   *         value.  For numeric value types, this default value is 0, for
   *         object types it is {@code null}.
   */
  public float get(long key);

  /**
   * @return Returns the value associated with the given key or the provided
   *         default value if the key is not associated with any value.
   */
  public float getOrDefault(long key, float defaultValue);

  /**
   * Place a given key and value in the container.
   * 
   * @return The value previously stored under the given key in the map is
   *         returned.
   */
  public float put(long key, float value);

  /**
   * Puts all keys from another container to this map, replacing the values of
   * existing keys, if such keys are present.
   * 
   * @return Returns the number of keys added to the map as a result of this
   *         call (not previously present in the map). Values of existing keys
   *         are overwritten.
   */
  public int putAll(LongFloatAssociativeContainer container);

  /**
   * Puts all keys from an iterable cursor to this map, replacing the values of
   * existing keys, if such keys are present.
   * 
   * @return Returns the number of keys added to the map as a result of this
   *         call (not previously present in the map). Values of existing keys
   *         are overwritten.
   */
  public int putAll(Iterable<? extends LongFloatCursor> iterable);

    
  /**
   * If <code>key</code> exists, <code>putValue</code> is inserted into the map,
   * otherwise any existing value is incremented by <code>additionValue</code>.
   * 
   * @param key
   *          The key of the value to adjust.
   * @param putValue
   *          The value to put if <code>key</code> does not exist.
   * @param incrementValue
   *          The value to add to the existing value if <code>key</code> exists.
   * @return Returns the current value associated with <code>key</code> (after
   *         changes).
   */
  public float putOrAdd(long key, float putValue, float incrementValue);
    

    
  /**
   * An equivalent of calling
   * 
   * <pre>
   * putOrAdd(key, additionValue, additionValue);
   * </pre>
   * 
   * @param key
   *          The key of the value to adjust.
   * @param additionValue
   *          The value to put or add to the existing value if <code>key</code>
   *          exists.
   * @return Returns the current value associated with <code>key</code> (after
   *         changes).
   */
  public float addTo(long key, float additionValue);

    

  /**
   * Remove all values at the given key. The default value for the key type is
   * returned if the value does not exist in the map.
   */
  public float remove(long key);

  /**
   * Compares the specified object with this set for equality. Returns
   * <tt>true</tt> if and only if the specified object is also a
   * {@link LongFloatMap} and both objects contains exactly the same key-value
   * pairs.
   */
  public boolean equals(Object obj);

  /**
   * @return A hash code of elements stored in the map. The hash code is defined
   *         as a sum of hash codes of keys and values stored within the set).
   *         Because sum is commutative, this ensures that different order of
   *         elements in a set does not affect the hash code.
   */
  public int hashCode();

  /**
   * Returns a logical "index" of a given key that can be used to speed up
   * follow-up value setters or getters in certain scenarios (conditional
   * logic).
   * 
   * The semantics of "indexes" are not strictly defined. Indexes may (and
   * typically won't be) contiguous.
   * 
   * The index is valid only between map modifications (it will not be affected
   * by read-only operations like iteration or value retrievals).
   * 
   * @see #indexExists
   * @see #indexGet
   * @see #indexInsert
   * @see #indexReplace
   * 
   * @param key
   *          The key to locate in the map.
   * @return A non-negative value of the logical "index" of the key in the map
   *         or a negative value if the key did not exist.
   */
  public int indexOf(long key);

  /**
   * @see #indexOf
   * 
   * @param index
   *          The index of a given key, as returned from {@link #indexOf}.
   * @return Returns <code>true</code> if the index corresponds to an existing
   *         key or false otherwise. This is equivalent to checking whether the
   *         index is a positive value (existing keys) or a negative value
   *         (non-existing keys).
   */
  public boolean indexExists(int index);

  /**
   * Returns the value associated with an existing key.
   * 
   * @see #indexOf
   * 
   * @param index
   *          The index of an existing key.
   * @return Returns the value currently associated with the key.
   * @throws AssertionError
   *           If assertions are enabled and the index does not correspond to an
   *           existing key.
   */
  public float indexGet(int index);

  /**
   * Replaces the value associated with an existing key and returns any previous
   * value stored for that key.
   * 
   * @see #indexOf
   * 
   * @param index
   *          The index of an existing key.
   * @return Returns the previous value associated with the key.
   * @throws AssertionError
   *           If assertions are enabled and the index does not correspond to an
   *           existing key.
   */
  public float indexReplace(int index, float newValue);

  /**
   * Inserts a key-value pair for a key that is not present in the map. This
   * method may help in avoiding double recalculation of the key's hash.
   * 
   * @see #indexOf
   * 
   * @param index
   *          The index of a previously non-existing key, as returned from
   *          {@link #indexOf}.
   * @throws AssertionError
   *           If assertions are enabled and the index corresponds to an
   *           existing key.
   */
  public void indexInsert(int index, long key, float value);

  /**
   * Clear all keys and values in the container.
   * 
   * @see #release()
   */
  public void clear();

  /**
   * Removes all elements from the collection and additionally releases any
   * internal buffers. Typically, if the object is to be reused, a simple
   * {@link #clear()} should be a better alternative since it'll avoid
   * reallocation.
   * 
   * @see #clear()
   */
  public void release();
  
  /**
   * Visually depict the distribution of keys.
   * 
   * @param characters
   *          The number of characters to "squeeze" the entire buffer into.
   * @return Returns a sequence of characters where '.' depicts an empty
   *         fragment of the internal buffer and 'X' depicts full or nearly full
   *         capacity within the buffer's range and anything between 1 and 9 is between.
   */
  public String visualizeKeyDistribution(int characters);  
}
