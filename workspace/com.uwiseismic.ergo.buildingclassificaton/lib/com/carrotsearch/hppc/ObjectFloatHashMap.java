  
package com.carrotsearch.hppc;

import java.util.*;

import com.carrotsearch.hppc.cursors.*;
import com.carrotsearch.hppc.predicates.*;
import com.carrotsearch.hppc.procedures.*;

import static com.carrotsearch.hppc.HashContainers.*;
import static com.carrotsearch.hppc.Containers.*;

/**
 * A hash map of <code>Object</code> to <code>float</code>, implemented using open
 * addressing with linear probing for collision resolution.
 * 
 * <p><strong>Note:</strong> read about <a href="{@docRoot}/overview-summary.html#scattervshash">important differences 
 * between hash and scatter sets</a>.</p>
 * 
 * @see ObjectFloatScatterMap
 * @see <a href="{@docRoot}/overview-summary.html#interfaces">HPPC interfaces diagram</a> 
 */
  @SuppressWarnings("unchecked")  
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeVTypeHashMap.java") 
public class ObjectFloatHashMap<KType>
  implements   
               
             ObjectFloatMap<KType>,
             Preallocable,
             Cloneable
{
  /** 
   * The array holding keys.
   */
  public    
         Object [] 
            
         keys;

  /**
   * The array holding values. 
   */
  public   float []   
         values;

  /**
   * We perturb hash values with a container-unique
   * seed to avoid problems with nearly-sorted-by-hash 
   * values on iterations.
   * 
   * @see #hashKey
   * @see "http://issues.carrot2.org/browse/HPPC-80"
   * @see "http://issues.carrot2.org/browse/HPPC-103"
   */
  protected int keyMixer;

  /**
   * The number of stored keys (assigned key slots), excluding the special 
   * "empty" key, if any (use {@link #size()} instead).
   * 
   * @see #size()
   */
  protected int assigned;

  /**
   * Mask for slot scans in {@link #keys}.
   */
  protected int mask;

  /**
   * Expand (rehash) {@link #keys} when {@link #assigned} hits this value. 
   */
  protected int resizeAt;

  /**
   * Special treatment for the "empty slot" key marker.
   */
  protected boolean hasEmptyKey;
  
  /**
   * The load factor for {@link #keys}.
   */
  protected double loadFactor;

  /**
   * Per-instance hash order mixing strategy.
   * @see #keyMixer
   */
  protected HashOrderMixingStrategy orderMixer;

  /**
   * New instance with sane defaults.
   */
  public ObjectFloatHashMap() {
    this(DEFAULT_EXPECTED_ELEMENTS);
  }

  /**
   * New instance with sane defaults.
   * 
   * @param expectedElements
   *          The expected number of elements guaranteed not to cause buffer
   *          expansion (inclusive).
   */
  public ObjectFloatHashMap(int expectedElements) {
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
  public ObjectFloatHashMap(int expectedElements, double loadFactor) {
    this(expectedElements, loadFactor, HashOrderMixing.defaultStrategy());
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
  public ObjectFloatHashMap(int expectedElements, double loadFactor, HashOrderMixingStrategy orderMixer) {
    this.orderMixer = orderMixer;
    this.loadFactor = verifyLoadFactor(loadFactor);
    ensureCapacity(expectedElements);
  }

  /**
   * Create a hash map from all key-value pairs of another container.
   */
  public ObjectFloatHashMap(ObjectFloatAssociativeContainer<? extends KType> container) {
    this(container.size());
    putAll(container);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float put(KType key, float value) {
    assert assigned < mask + 1;

    final int mask = this.mask;
    if (((key) == null)) {
      hasEmptyKey = true;
      float previousValue =  values[mask + 1];
      values[mask + 1] = value;
      return previousValue;
    } else {
      final KType[] keys = (KType[]) this.keys;
      int slot = hashKey(key) & mask;

      KType existing;
      while (!((existing = keys[slot]) == null)) {
        if (this.equals(existing,  key)) {
          final float previousValue =  values[slot];
          values[slot] = value;
          return previousValue;
        }
        slot = (slot + 1) & mask;
      }

      if (assigned == resizeAt) {
        allocateThenInsertThenRehash(slot, key, value);
      } else {
        keys[slot] = key;
        values[slot] = value;
      }

      assigned++;
      return 0f;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int putAll(ObjectFloatAssociativeContainer<? extends KType> container) {
    final int count = size();
    for (ObjectFloatCursor<? extends KType> c : container) {
      put(c.key, c.value);
    }
    return size() - count;
  }

  /**
   * Puts all key/value pairs from a given iterable into this map.
   */
  @Override
  public int putAll(Iterable<? extends ObjectFloatCursor<? extends KType>> iterable){
    final int count = size();
    for (ObjectFloatCursor<? extends KType> c : iterable) {
      put(c.key, c.value);
    }
    return size() - count;
  }

  /**
   * <a href="http://trove4j.sourceforge.net">Trove</a>-inspired API method. An equivalent
   * of the following code:
   * <pre>
   * if (!map.containsKey(key)) map.put(value);
   * </pre>
   * 
   * @param key The key of the value to check.
   * @param value The value to put if <code>key</code> does not exist.
   * @return <code>true</code> if <code>key</code> did not exist and <code>value</code>
   * was placed in the map.
   */
  public boolean putIfAbsent(KType key, float value) {
    int keyIndex = indexOf(key);
    if (!indexExists(keyIndex)) {
      indexInsert(keyIndex, key, value);
      return true;
    } else {
      return false;
    }
  }

    
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
  @Override
  public float putOrAdd(KType key, float putValue, float incrementValue) {
    assert assigned < mask + 1;

    int keyIndex = indexOf(key);
    if (indexExists(keyIndex)) {
      putValue = ((float) (( values[keyIndex]) + (incrementValue)));
      indexReplace(keyIndex, putValue);
    } else {
      indexInsert(keyIndex, key, putValue);
    }
    return putValue;
  }
    

     
  /**
   * Adds <code>incrementValue</code> to any existing value for the given <code>key</code>
   * or inserts <code>incrementValue</code> if <code>key</code> did not previously exist.
   * 
   * @param key The key of the value to adjust.
   * @param incrementValue The value to put or add to the existing value if <code>key</code> exists.
   * @return Returns the current value associated with <code>key</code> (after changes).
   */
  @Override
  public float addTo(KType key, float incrementValue)
  {
    return putOrAdd(key, incrementValue, incrementValue);
  }
    

  /**
   * {@inheritDoc}
   */
  @Override
  public float remove(KType key) {
    final int mask = this.mask;
    if (((key) == null)) {
      hasEmptyKey = false;
      float previousValue =  values[mask + 1];
      values[mask + 1] = 0f;
      return previousValue;
    } else {
      final KType[] keys = (KType[]) this.keys;
      int slot = hashKey(key) & mask;

      KType existing;
      while (!((existing = keys[slot]) == null)) {
        if (this.equals(existing,  key)) {
          final float previousValue =  values[slot];
          shiftConflictingKeys(slot);
          return previousValue;
        }
        slot = (slot + 1) & mask;
      }

      return 0f;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int removeAll(ObjectContainer<? super KType> other) {
    final int before = size();

    // Try to iterate over the smaller set of values or
    // over the container that isn't implementing 
    // efficient contains() lookup.

    if (other.size() >= size() &&
        other instanceof ObjectLookupContainer<?>) {
      if (hasEmptyKey) {
        if (other.contains(null)) {
          hasEmptyKey = false;
          values[mask + 1] = 0f;
        }
      }

      final KType[] keys = (KType[]) this.keys;
      for (int slot = 0, max = this.mask; slot <= max;) {
        KType existing;
        if (!((existing = keys[slot]) == null) && other.contains(existing)) {
          // Shift, do not increment slot.
          shiftConflictingKeys(slot);
        } else {
          slot++;
        }
      }
    } else {
      for (ObjectCursor<?> c : other) {
        this.remove((KType) c.value);
      }
    }

    return before - size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int removeAll(ObjectFloatPredicate<? super KType> predicate) {
    final int before = size();

    final int mask = this.mask;

    if (hasEmptyKey) {
      if (predicate.apply(null,  values[mask + 1])) {
        hasEmptyKey = false;
        values[mask + 1] = 0f;
      }
    }

    final KType[] keys = (KType[]) this.keys;
    final float[] values =  this.values;
    for (int slot = 0; slot <= mask;) {
      KType existing;
      if (!((existing = keys[slot]) == null) && 
          predicate.apply(existing, values[slot])) {
        // Shift, do not increment slot.
        shiftConflictingKeys(slot);
      } else {
        slot++;
      }
    }

    return before - size();    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int removeAll(ObjectPredicate<? super KType> predicate) {
    final int before = size();

    if (hasEmptyKey) {
      if (predicate.apply(null)) {
        hasEmptyKey = false;
        values[mask + 1] = 0f;
      }
    }

    final KType[] keys = (KType[]) this.keys;
    for (int slot = 0, max = this.mask; slot <= max;) {
      KType existing;
      if (!((existing = keys[slot]) == null) &&
          predicate.apply(existing)) {
        // Shift, do not increment slot.
        shiftConflictingKeys(slot);
      } else {
        slot++;
      }
    }

    return before - size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float get(KType key) {
    if (((key) == null)) {
      return hasEmptyKey ?  values[mask + 1] : 0f;
    } else {
      final KType[] keys = (KType[]) this.keys;
      final int mask = this.mask;
      int slot = hashKey(key) & mask;

      KType existing;
      while (!((existing = keys[slot]) == null)) {
        if (this.equals(existing,  key)) {
          return  values[slot];
        }
        slot = (slot + 1) & mask;
      }

      return 0f;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float getOrDefault(KType key, float defaultValue) {
    if (((key) == null)) {
      return hasEmptyKey ?  values[mask + 1] : defaultValue;
    } else {
      final KType[] keys = (KType[]) this.keys;
      final int mask = this.mask;
      int slot = hashKey(key) & mask;

      KType existing;
      while (!((existing = keys[slot]) == null)) {
        if (this.equals(existing,  key)) {
          return  values[slot];
        }
        slot = (slot + 1) & mask;
      }

      return defaultValue;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsKey(KType key) {
    if (((key) == null)) {
      return hasEmptyKey;
    } else {
      final KType[] keys = (KType[]) this.keys;
      final int mask = this.mask;
      int slot = hashKey(key) & mask;

      KType existing;
      while (!((existing = keys[slot]) == null)) {
        if (this.equals(existing,  key)) {
          return true;
        }
        slot = (slot + 1) & mask;
      }    

      return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int indexOf(KType key) {
    final int mask = this.mask;
    if (((key) == null)) {
      return hasEmptyKey ? mask + 1 : ~(mask + 1);
    } else {
      final KType[] keys = (KType[]) this.keys;
      int slot = hashKey(key) & mask;

      KType existing;
      while (!((existing = keys[slot]) == null)) {
        if (this.equals(existing,  key)) {
          return slot;
        }
        slot = (slot + 1) & mask;
      }

      return ~slot;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean indexExists(int index) {
    assert index < 0 || 
           (index >= 0 && index <= mask) ||
           (index == mask + 1 && hasEmptyKey);

    return index >= 0; 
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float indexGet(int index) {
    assert index >= 0 : "The index must point at an existing key.";
    assert index <= mask ||
           (index == mask + 1 && hasEmptyKey);

    return  values[index];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float indexReplace(int index, float newValue) {
    assert index >= 0 : "The index must point at an existing key.";
    assert index <= mask ||
           (index == mask + 1 && hasEmptyKey);

    float previousValue =  values[index];
    values[index] = newValue;
    return previousValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void indexInsert(int index, KType key, float value) {
    assert index < 0 : "The index must not point at an existing key.";

    index = ~index;
    if (((key) == null)) {
      assert index == mask + 1;
      values[index] = value;
      hasEmptyKey = true;
    } else {
      assert ((keys[index]) == null);

      if (assigned == resizeAt) {
        allocateThenInsertThenRehash(index, key, value);
      } else {
        keys[index] = key;
        values[index] = value;
      }

      assigned++;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    assigned = 0;
    hasEmptyKey = false;

    Arrays.fill(keys, null);

    /*  */
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void release() {
    assigned = 0;
    hasEmptyKey = false;

    keys = null;
    values = null;
    ensureCapacity(Containers.DEFAULT_EXPECTED_ELEMENTS);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return assigned + (hasEmptyKey ? 1 : 0);
  }

  /**
   * {@inheritDoc}
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int h = hasEmptyKey ? 0xDEADBEEF : 0;
    for (ObjectFloatCursor<KType> c : this) {
      h += BitMixer.mix(c.key) +
           BitMixer.mix(c.value);
    }
    return h;
  }

  /**
   * {@inheritDoc} 
   */
  @Override
  public boolean equals(Object obj) {
    return obj != null &&
           getClass() == obj.getClass() &&
           equalElements(getClass().cast(obj));
  }

  /**
   * Return true if all keys of some other container exist in this container.
   * Equality comparison is performed with this object's {@link #equals(Object, Object)} 
   * method.
   */
  protected boolean equalElements(ObjectFloatHashMap<?> other) {
    if (other.size() != size()) {
      return false;
    }

    for (ObjectFloatCursor<?> c : other) {
      KType key = (KType) c.key;
      if (!containsKey(key) ||
          !(Float.floatToIntBits(get(key)) == Float.floatToIntBits(c.value))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Ensure this container can hold at least the
   * given number of keys (entries) without resizing its buffers.
   * 
   * @param expectedElements The total number of keys, inclusive.
   */
  @Override
  public void ensureCapacity(int expectedElements) {
    if (expectedElements > resizeAt || keys == null) {
      final KType[] prevKeys = (KType[]) this.keys;
      final float[] prevValues =  this.values;
      allocateBuffers(minBufferSize(expectedElements, loadFactor));
      if (prevKeys != null && !isEmpty()) {
        rehash(prevKeys, prevValues);
      }
    }
  }

  /**
   * An iterator implementation for {@link #iterator}.
   */
  private final class EntryIterator extends AbstractIterator<ObjectFloatCursor<KType>> {
    private final ObjectFloatCursor<KType> cursor;
    private final int max = mask + 1;
    private int slot = -1;

    public EntryIterator() {
      cursor = new ObjectFloatCursor<KType>();
    }

    @Override
    protected ObjectFloatCursor<KType> fetch() {
      if (slot < max) {
        KType existing;
        for (slot++; slot < max; slot++) {
          if (!((existing = (KType) keys[slot]) == null)) {
            cursor.index = slot;
            cursor.key = existing;
            cursor.value =  values[slot];
            return cursor;
          }
        }
      }

      if (slot == max && hasEmptyKey) {
        cursor.index = slot;
        cursor.key = null;
        cursor.value =  values[max];
        slot++;
        return cursor;
      }

      return done();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<ObjectFloatCursor<KType>> iterator() {
      return new EntryIterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends ObjectFloatProcedure<? super KType>> T forEach(T procedure) {
    final KType[] keys = (KType[]) this.keys;
    final float[] values =  this.values;

    if (hasEmptyKey) {
      procedure.apply(null,  values[mask + 1]);
    }

    for (int slot = 0, max = this.mask; slot <= max; slot++) {
      if (!((keys[slot]) == null)) {
        procedure.apply(keys[slot], values[slot]);
      }
    }

    return procedure;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends ObjectFloatPredicate<? super KType>> T forEach(T predicate) {
    final KType[] keys = (KType[]) this.keys;
    final float[] values =  this.values;

    if (hasEmptyKey) {
      if (!predicate.apply(null,  values[mask + 1])) {
        return predicate;
      }
    }

    for (int slot = 0, max = this.mask; slot <= max; slot++) {
      if (!((keys[slot]) == null)) {
        if (!predicate.apply(keys[slot], values[slot])) {
          break;
        }
      }
    }

    return predicate;
  }

  /**
   * Returns a specialized view of the keys of this associated container. The
   * view additionally implements {@link ObjectLookupContainer}.
   */
  public KeysContainer keys() {
    return new KeysContainer();
  }

  /**
   * A view of the keys inside this hash map.
   */
  public final class KeysContainer extends AbstractObjectCollection<KType> 
                                   implements ObjectLookupContainer<KType> {
    private final ObjectFloatHashMap<KType> owner = ObjectFloatHashMap.this;

    @Override
    public boolean contains(KType e) {
      return owner.containsKey(e);
    }

    @Override
    public <T extends ObjectProcedure<? super KType>> T forEach(final T procedure) {
      owner.forEach(new ObjectFloatProcedure<KType>() {
        @Override
        public void apply(KType key, float value) {
          procedure.apply(key);
        }
      });

      return procedure;
    }

    @Override
    public <T extends ObjectPredicate<? super KType>> T forEach(final T predicate) {
      owner.forEach(new ObjectFloatPredicate<KType>() {
        @Override
        public boolean apply(KType key, float value) {
          return predicate.apply(key);
        }
      });

      return predicate;
    }

    @Override
    public boolean isEmpty() {
      return owner.isEmpty();
    }

    @Override
    public Iterator<ObjectCursor<KType>> iterator() {
      return new KeysIterator();
    }

    @Override
    public int size() {
      return owner.size();
    }

    @Override
    public void clear() {
      owner.clear();
    }
    
    @Override
    public void release() {
      owner.release();
    }

    @Override
    public int removeAll(ObjectPredicate<? super KType> predicate) {
      return owner.removeAll(predicate);
    }

    @Override
    public int removeAll(final KType e) {
      final boolean hasKey = owner.containsKey(e);
      if (hasKey) {
        owner.remove(e);
        return 1;
      } else {
        return 0;
      }
    }
  };

  /**
   * An iterator over the set of assigned keys.
   */
  private final class KeysIterator extends AbstractIterator<ObjectCursor<KType>> {
    private final ObjectCursor<KType> cursor;
    private final int max = mask + 1;
    private int slot = -1;

    public KeysIterator() {
      cursor = new ObjectCursor<KType>();
    }

    @Override
    protected ObjectCursor<KType> fetch() {
      if (slot < max) {
        KType existing;
        for (slot++; slot < max; slot++) {
          if (!((existing = (KType) keys[slot]) == null)) {
            cursor.index = slot;
            cursor.value = existing;
            return cursor;
          }
        }
      }

      if (slot == max && hasEmptyKey) {
        cursor.index = slot;
        cursor.value = null;
        slot++;
        return cursor;
      }

      return done();
    }
  }

  /**
   * @return Returns a container with all values stored in this map.
   */
  @Override
  public FloatCollection values() {
    return new ValuesContainer();
  }

  /**
   * A view over the set of values of this map.
   */
  private final class ValuesContainer extends AbstractFloatCollection {
    private final ObjectFloatHashMap<KType> owner = ObjectFloatHashMap.this;

    @Override
    public int size() {
      return owner.size();
    }

    @Override
    public boolean isEmpty() {
      return owner.isEmpty();
    }

    @Override
    public boolean contains(float value) {
      for (ObjectFloatCursor<KType> c : owner) {
        if ((Float.floatToIntBits(c.value) == Float.floatToIntBits(value))) {
          return true;
        }
      }
      return false;
    }

    @Override
    public <T extends FloatProcedure> T forEach(T procedure) {
      for (ObjectFloatCursor<KType> c : owner) {
        procedure.apply(c.value);
      }
      return procedure;
    }

    @Override
    public <T extends FloatPredicate> T forEach(T predicate) {
      for (ObjectFloatCursor<KType> c : owner) {
        if (!predicate.apply(c.value)) {
          break;
        }
      }
      return predicate;
    }

    @Override
    public Iterator<FloatCursor> iterator() {
      return new ValuesIterator();
    }

    @Override
    public int removeAll(final float e) {
      return owner.removeAll(new ObjectFloatPredicate<KType>() {
        @Override
        public boolean apply(KType key, float value) {
          return (Float.floatToIntBits(value) == Float.floatToIntBits(e));
        }
      });
    }

    @Override
    public int removeAll(final FloatPredicate predicate) {
      return owner.removeAll(new ObjectFloatPredicate<KType>() {
        @Override
        public boolean apply(KType key, float value) {
          return predicate.apply(value);
        }
      });
    }

    @Override
    public void clear() {
      owner.clear();
    }
    
    @Override
    public void release() {
      owner.release();
    }
  }
  
  /**
   * An iterator over the set of assigned values.
   */
  private final class ValuesIterator extends AbstractIterator<FloatCursor> {
    private final FloatCursor cursor;
    private final int max = mask + 1;
    private int slot = -1;

    public ValuesIterator() {
      cursor = new FloatCursor();
    }

    @Override
    protected FloatCursor fetch() {
      if (slot < max) {
        for (slot++; slot < max; slot++) {
          if (!(((KType) keys[slot]) == null)) {
            cursor.index = slot;
            cursor.value =  values[slot];
            return cursor;
          }
        }
      }

      if (slot == max && hasEmptyKey) {
        cursor.index = slot;
        cursor.value =  values[max];
        slot++;
        return cursor;
      }

      return done();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ObjectFloatHashMap<KType> clone() {
    try {
      /*  */
      ObjectFloatHashMap<KType> cloned = (ObjectFloatHashMap<KType>) super.clone();
      cloned.keys = keys.clone();
      cloned.values = values.clone();
      cloned.hasEmptyKey = cloned.hasEmptyKey;
      cloned.orderMixer = orderMixer.clone();
      return cloned;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Convert the contents of this map to a human-friendly string.
   */
  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("[");

    boolean first = true;
    for (ObjectFloatCursor<KType> cursor : this) {
      if (!first) {
        buffer.append(", ");
      }
      buffer.append(cursor.key);
      buffer.append("=>");
      buffer.append(cursor.value);
      first = false;
    }
    buffer.append("]");
    return buffer.toString();
  }

  @Override
  public String visualizeKeyDistribution(int characters) {
    return ObjectBufferVisualizer.visualizeKeyDistribution(keys, mask, characters);
  }

  /**
   * Creates a hash map from two index-aligned arrays of key-value pairs.
   */
  public static <KType> ObjectFloatHashMap<KType> from(KType[] keys, float[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }

    ObjectFloatHashMap<KType> map = new ObjectFloatHashMap<>(keys.length);
    for (int i = 0; i < keys.length; i++) {
      map.put(keys[i], values[i]);
    }

    return map;
  }
    
  /**
   * Returns a hash code for the given key.
   * 
   * <p>The default implementation mixes the hash of the key with {@link #keyMixer}
   * to differentiate hash order of keys between hash containers. Helps
   * alleviate problems resulting from linear conflict resolution in open
   * addressing.</p>
   * 
   * <p>The output from this function should evenly distribute keys across the
   * entire integer range.</p>
   */
    protected  
  int hashKey(KType key) {
    assert !((key) == null); // Handled as a special case (empty slot marker).
    return BitMixer.mix(key, this.keyMixer);
  }

  /**
   * Validate load factor range and return it. Override and suppress if you need
   * insane load factors.
   */
  protected double verifyLoadFactor(double loadFactor) {
    checkLoadFactor(loadFactor, MIN_LOAD_FACTOR, MAX_LOAD_FACTOR);
    return loadFactor;
  }

  /**
   * Rehash from old buffers to new buffers. 
   */
  protected void rehash(KType[] fromKeys, float[] fromValues) {
    assert fromKeys.length == fromValues.length &&
           HashContainers.checkPowerOfTwo(fromKeys.length - 1);
    
    // Rehash all stored key/value pairs into the new buffers.
    final KType[] keys = (KType[]) this.keys;
    final float[] values =  this.values;
    final int mask = this.mask;
    KType existing;

    // Copy the zero element's slot, then rehash everything else.
    int from = fromKeys.length - 1;
    keys[keys.length - 1] = fromKeys[from];
    values[values.length - 1] = fromValues[from];
    while (--from >= 0) {
      if (!((existing = fromKeys[from]) == null)) {
        int slot = hashKey(existing) & mask;
        while (!((keys[slot]) == null)) {
          slot = (slot + 1) & mask;
        }
        keys[slot] = existing;
        values[slot] = fromValues[from];
      }
    }
  }

  /**
   * Allocate new internal buffers. This method attempts to allocate
   * and assign internal buffers atomically (either allocations succeed or not).
   */
  protected void allocateBuffers(int arraySize) {
    assert Integer.bitCount(arraySize) == 1;

    // Compute new hash mixer candidate before expanding.
    final int newKeyMixer = this.orderMixer.newKeyMixer(arraySize);

    // Ensure no change is done if we hit an OOM.
    KType[] prevKeys = (KType[]) this.keys;
    float[] prevValues =  this.values;
    try {
      int emptyElementSlot = 1;
      this.keys = ((KType[]) new Object [arraySize + emptyElementSlot]);
      this.values = (new float [arraySize + emptyElementSlot]);
    } catch (OutOfMemoryError e) {
      this.keys = prevKeys;
      this.values = prevValues;
      throw new BufferAllocationException(
          "Not enough memory to allocate buffers for rehashing: %,d -> %,d", 
          e,
          this.mask + 1, 
          arraySize);
    }

    this.resizeAt = expandAtCount(arraySize, loadFactor);
    this.keyMixer = newKeyMixer;
    this.mask = arraySize - 1;
  }

  /**
   * This method is invoked when there is a new key/ value pair to be inserted into
   * the buffers but there is not enough empty slots to do so.
   * 
   * New buffers are allocated. If this succeeds, we know we can proceed
   * with rehashing so we assign the pending element to the previous buffer
   * (possibly violating the invariant of having at least one empty slot)
   * and rehash all keys, substituting new buffers at the end.  
   */
  protected void allocateThenInsertThenRehash(int slot, KType pendingKey, float pendingValue) {
    assert assigned == resizeAt
           && (((KType) keys[slot]) == null)
           && !((pendingKey) == null);

    // Try to allocate new buffers first. If we OOM, we leave in a consistent state.
    final KType[] prevKeys = (KType[]) this.keys;
    final float[] prevValues =  this.values;
    allocateBuffers(nextBufferSize(mask + 1, size(), loadFactor));
    assert this.keys.length > prevKeys.length;

    // We have succeeded at allocating new data so insert the pending key/value at
    // the free slot in the old arrays before rehashing.
    prevKeys[slot] = pendingKey;
    prevValues[slot] = pendingValue;

    // Rehash old keys, including the pending key.
    rehash(prevKeys, prevValues);
  }
  
  /**
   * Shift all the slot-conflicting keys and values allocated to 
   * (and including) <code>slot</code>.
   */
  protected void shiftConflictingKeys(int gapSlot) {
    final KType[] keys = (KType[]) this.keys;
    final float[] values =  this.values;
    final int mask = this.mask;

    // Perform shifts of conflicting keys to fill in the gap.
    int distance = 0;
    while (true) {
      final int slot = (gapSlot + (++distance)) & mask;
      final KType existing = keys[slot];
      if (((existing) == null)) {
        break;
      }

      final int idealSlot = hashKey(existing);
      final int shift = (slot - idealSlot) & mask;
      if (shift >= distance) {
        // Entry at this position was originally at or before the gap slot.
        // Move the conflict-shifted entry to the gap's position and repeat the procedure
        // for any entries to the right of the current position, treating it
        // as the new gap.
        keys[gapSlot] = existing;
        values[gapSlot] = values[slot]; 
        gapSlot = slot;
        distance = 0;
      }
    }

    // Mark the last found gap slot without a conflict as empty.
    keys[gapSlot] = null;
    values[gapSlot] = 0f;
    assigned--;
  }

    
    protected   boolean equals(Object v1, Object v2) {
    return (v1 == v2) || (v1 != null && v1.equals(v2));
  }
        
}
