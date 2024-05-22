  
package com.carrotsearch.hppc;

import java.util.*;

import com.carrotsearch.hppc.cursors.*;
import com.carrotsearch.hppc.predicates.*;
import com.carrotsearch.hppc.procedures.*;

import static com.carrotsearch.hppc.HashContainers.*;
import static com.carrotsearch.hppc.Containers.*;

/**
 * A hash map of <code>char</code> to <code>Object</code>, implemented using open
 * addressing with linear probing for collision resolution.
 * 
 * <p><strong>Note:</strong> read about <a href="{@docRoot}/overview-summary.html#scattervshash">important differences 
 * between hash and scatter sets</a>.</p>
 * 
 * @see CharObjectScatterMap
 * @see <a href="{@docRoot}/overview-summary.html#interfaces">HPPC interfaces diagram</a> 
 */
  @SuppressWarnings("unchecked")  
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeVTypeHashMap.java") 
public class CharObjectHashMap<VType>
  implements   
               
             CharObjectMap<VType>,
             Preallocable,
             Cloneable
{
  /** 
   * The array holding keys.
   */
  public   char []   
         keys;

  /**
   * The array holding values. 
   */
  public    
         Object [] 
            
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
  public CharObjectHashMap() {
    this(DEFAULT_EXPECTED_ELEMENTS);
  }

  /**
   * New instance with sane defaults.
   * 
   * @param expectedElements
   *          The expected number of elements guaranteed not to cause buffer
   *          expansion (inclusive).
   */
  public CharObjectHashMap(int expectedElements) {
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
  public CharObjectHashMap(int expectedElements, double loadFactor) {
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
  public CharObjectHashMap(int expectedElements, double loadFactor, HashOrderMixingStrategy orderMixer) {
    this.orderMixer = orderMixer;
    this.loadFactor = verifyLoadFactor(loadFactor);
    ensureCapacity(expectedElements);
  }

  /**
   * Create a hash map from all key-value pairs of another container.
   */
  public CharObjectHashMap(CharObjectAssociativeContainer<? extends VType> container) {
    this(container.size());
    putAll(container);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VType put(char key, VType value) {
    assert assigned < mask + 1;

    final int mask = this.mask;
    if (((key) == 0)) {
      hasEmptyKey = true;
      VType previousValue = (VType) values[mask + 1];
      values[mask + 1] = value;
      return previousValue;
    } else {
      final char[] keys =  this.keys;
      int slot = hashKey(key) & mask;

      char existing;
      while (!((existing = keys[slot]) == 0)) {
        if (((existing) == ( key))) {
          final VType previousValue = (VType) values[slot];
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
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int putAll(CharObjectAssociativeContainer<? extends VType> container) {
    final int count = size();
    for (CharObjectCursor<? extends VType> c : container) {
      put(c.key, c.value);
    }
    return size() - count;
  }

  /**
   * Puts all key/value pairs from a given iterable into this map.
   */
  @Override
  public int putAll(Iterable<? extends CharObjectCursor<? extends VType>> iterable){
    final int count = size();
    for (CharObjectCursor<? extends VType> c : iterable) {
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
  public boolean putIfAbsent(char key, VType value) {
    int keyIndex = indexOf(key);
    if (!indexExists(keyIndex)) {
      indexInsert(keyIndex, key, value);
      return true;
    } else {
      return false;
    }
  }

    

    

  /**
   * {@inheritDoc}
   */
  @Override
  public VType remove(char key) {
    final int mask = this.mask;
    if (((key) == 0)) {
      hasEmptyKey = false;
      VType previousValue = (VType) values[mask + 1];
      values[mask + 1] = null;
      return previousValue;
    } else {
      final char[] keys =  this.keys;
      int slot = hashKey(key) & mask;

      char existing;
      while (!((existing = keys[slot]) == 0)) {
        if (((existing) == ( key))) {
          final VType previousValue = (VType) values[slot];
          shiftConflictingKeys(slot);
          return previousValue;
        }
        slot = (slot + 1) & mask;
      }

      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int removeAll(CharContainer other) {
    final int before = size();

    // Try to iterate over the smaller set of values or
    // over the container that isn't implementing 
    // efficient contains() lookup.

    if (other.size() >= size() &&
        other instanceof CharLookupContainer) {
      if (hasEmptyKey) {
        if (other.contains(((char) 0))) {
          hasEmptyKey = false;
          values[mask + 1] = null;
        }
      }

      final char[] keys =  this.keys;
      for (int slot = 0, max = this.mask; slot <= max;) {
        char existing;
        if (!((existing = keys[slot]) == 0) && other.contains(existing)) {
          // Shift, do not increment slot.
          shiftConflictingKeys(slot);
        } else {
          slot++;
        }
      }
    } else {
      for (CharCursor c : other) {
        this.remove( c.value);
      }
    }

    return before - size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int removeAll(CharObjectPredicate<? super VType> predicate) {
    final int before = size();

    final int mask = this.mask;

    if (hasEmptyKey) {
      if (predicate.apply(((char) 0), (VType) values[mask + 1])) {
        hasEmptyKey = false;
        values[mask + 1] = null;
      }
    }

    final char[] keys =  this.keys;
    final VType[] values = (VType[]) this.values;
    for (int slot = 0; slot <= mask;) {
      char existing;
      if (!((existing = keys[slot]) == 0) && 
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
  public int removeAll(CharPredicate predicate) {
    final int before = size();

    if (hasEmptyKey) {
      if (predicate.apply(((char) 0))) {
        hasEmptyKey = false;
        values[mask + 1] = null;
      }
    }

    final char[] keys =  this.keys;
    for (int slot = 0, max = this.mask; slot <= max;) {
      char existing;
      if (!((existing = keys[slot]) == 0) &&
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
  public VType get(char key) {
    if (((key) == 0)) {
      return hasEmptyKey ? (VType) values[mask + 1] : null;
    } else {
      final char[] keys =  this.keys;
      final int mask = this.mask;
      int slot = hashKey(key) & mask;

      char existing;
      while (!((existing = keys[slot]) == 0)) {
        if (((existing) == ( key))) {
          return (VType) values[slot];
        }
        slot = (slot + 1) & mask;
      }

      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VType getOrDefault(char key, VType defaultValue) {
    if (((key) == 0)) {
      return hasEmptyKey ? (VType) values[mask + 1] : defaultValue;
    } else {
      final char[] keys =  this.keys;
      final int mask = this.mask;
      int slot = hashKey(key) & mask;

      char existing;
      while (!((existing = keys[slot]) == 0)) {
        if (((existing) == ( key))) {
          return (VType) values[slot];
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
  public boolean containsKey(char key) {
    if (((key) == 0)) {
      return hasEmptyKey;
    } else {
      final char[] keys =  this.keys;
      final int mask = this.mask;
      int slot = hashKey(key) & mask;

      char existing;
      while (!((existing = keys[slot]) == 0)) {
        if (((existing) == ( key))) {
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
  public int indexOf(char key) {
    final int mask = this.mask;
    if (((key) == 0)) {
      return hasEmptyKey ? mask + 1 : ~(mask + 1);
    } else {
      final char[] keys =  this.keys;
      int slot = hashKey(key) & mask;

      char existing;
      while (!((existing = keys[slot]) == 0)) {
        if (((existing) == ( key))) {
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
  public VType indexGet(int index) {
    assert index >= 0 : "The index must point at an existing key.";
    assert index <= mask ||
           (index == mask + 1 && hasEmptyKey);

    return (VType) values[index];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VType indexReplace(int index, VType newValue) {
    assert index >= 0 : "The index must point at an existing key.";
    assert index <= mask ||
           (index == mask + 1 && hasEmptyKey);

    VType previousValue = (VType) values[index];
    values[index] = newValue;
    return previousValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void indexInsert(int index, char key, VType value) {
    assert index < 0 : "The index must not point at an existing key.";

    index = ~index;
    if (((key) == 0)) {
      assert index == mask + 1;
      values[index] = value;
      hasEmptyKey = true;
    } else {
      assert ((keys[index]) == 0);

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

    Arrays.fill(keys, ((char) 0));

    /*  */ 
    Arrays.fill(values, null);
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
    for (CharObjectCursor<VType> c : this) {
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
   * Values are compared using {@link Objects#equals(Object)} method.
   */
  protected boolean equalElements(CharObjectHashMap<?> other) {
    if (other.size() != size()) {
      return false;
    }

    for (CharObjectCursor<?> c : other) {
      char key =  c.key;
      if (!containsKey(key) ||
          !java.util.Objects.equals(get(key), c.value)) {
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
      final char[] prevKeys =  this.keys;
      final VType[] prevValues = (VType[]) this.values;
      allocateBuffers(minBufferSize(expectedElements, loadFactor));
      if (prevKeys != null && !isEmpty()) {
        rehash(prevKeys, prevValues);
      }
    }
  }

  /**
   * An iterator implementation for {@link #iterator}.
   */
  private final class EntryIterator extends AbstractIterator<CharObjectCursor<VType>> {
    private final CharObjectCursor<VType> cursor;
    private final int max = mask + 1;
    private int slot = -1;

    public EntryIterator() {
      cursor = new CharObjectCursor<VType>();
    }

    @Override
    protected CharObjectCursor<VType> fetch() {
      if (slot < max) {
        char existing;
        for (slot++; slot < max; slot++) {
          if (!((existing =  keys[slot]) == 0)) {
            cursor.index = slot;
            cursor.key = existing;
            cursor.value = (VType) values[slot];
            return cursor;
          }
        }
      }

      if (slot == max && hasEmptyKey) {
        cursor.index = slot;
        cursor.key = ((char) 0);
        cursor.value = (VType) values[max];
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
  public Iterator<CharObjectCursor<VType>> iterator() {
      return new EntryIterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends CharObjectProcedure<? super VType>> T forEach(T procedure) {
    final char[] keys =  this.keys;
    final VType[] values = (VType[]) this.values;

    if (hasEmptyKey) {
      procedure.apply(((char) 0), (VType) values[mask + 1]);
    }

    for (int slot = 0, max = this.mask; slot <= max; slot++) {
      if (!((keys[slot]) == 0)) {
        procedure.apply(keys[slot], values[slot]);
      }
    }

    return procedure;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends CharObjectPredicate<? super VType>> T forEach(T predicate) {
    final char[] keys =  this.keys;
    final VType[] values = (VType[]) this.values;

    if (hasEmptyKey) {
      if (!predicate.apply(((char) 0), (VType) values[mask + 1])) {
        return predicate;
      }
    }

    for (int slot = 0, max = this.mask; slot <= max; slot++) {
      if (!((keys[slot]) == 0)) {
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
  public final class KeysContainer extends AbstractCharCollection 
                                   implements CharLookupContainer {
    private final CharObjectHashMap<VType> owner = CharObjectHashMap.this;

    @Override
    public boolean contains(char e) {
      return owner.containsKey(e);
    }

    @Override
    public <T extends CharProcedure> T forEach(final T procedure) {
      owner.forEach(new CharObjectProcedure<VType>() {
        @Override
        public void apply(char key, VType value) {
          procedure.apply(key);
        }
      });

      return procedure;
    }

    @Override
    public <T extends CharPredicate> T forEach(final T predicate) {
      owner.forEach(new CharObjectPredicate<VType>() {
        @Override
        public boolean apply(char key, VType value) {
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
    public Iterator<CharCursor> iterator() {
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
    public int removeAll(CharPredicate predicate) {
      return owner.removeAll(predicate);
    }

    @Override
    public int removeAll(final char e) {
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
  private final class KeysIterator extends AbstractIterator<CharCursor> {
    private final CharCursor cursor;
    private final int max = mask + 1;
    private int slot = -1;

    public KeysIterator() {
      cursor = new CharCursor();
    }

    @Override
    protected CharCursor fetch() {
      if (slot < max) {
        char existing;
        for (slot++; slot < max; slot++) {
          if (!((existing =  keys[slot]) == 0)) {
            cursor.index = slot;
            cursor.value = existing;
            return cursor;
          }
        }
      }

      if (slot == max && hasEmptyKey) {
        cursor.index = slot;
        cursor.value = ((char) 0);
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
  public ObjectCollection<VType> values() {
    return new ValuesContainer();
  }

  /**
   * A view over the set of values of this map.
   */
  private final class ValuesContainer extends AbstractObjectCollection<VType> {
    private final CharObjectHashMap<VType> owner = CharObjectHashMap.this;

    @Override
    public int size() {
      return owner.size();
    }

    @Override
    public boolean isEmpty() {
      return owner.isEmpty();
    }

    @Override
    public boolean contains(VType value) {
      for (CharObjectCursor<VType> c : owner) {
        if (java.util.Objects.equals(c.value, value)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public <T extends ObjectProcedure<? super VType>> T forEach(T procedure) {
      for (CharObjectCursor<VType> c : owner) {
        procedure.apply(c.value);
      }
      return procedure;
    }

    @Override
    public <T extends ObjectPredicate<? super VType>> T forEach(T predicate) {
      for (CharObjectCursor<VType> c : owner) {
        if (!predicate.apply(c.value)) {
          break;
        }
      }
      return predicate;
    }

    @Override
    public Iterator<ObjectCursor<VType>> iterator() {
      return new ValuesIterator();
    }

    @Override
    public int removeAll(final VType e) {
      return owner.removeAll(new CharObjectPredicate<VType>() {
        @Override
        public boolean apply(char key, VType value) {
          return java.util.Objects.equals(value, e);
        }
      });
    }

    @Override
    public int removeAll(final ObjectPredicate<? super VType> predicate) {
      return owner.removeAll(new CharObjectPredicate<VType>() {
        @Override
        public boolean apply(char key, VType value) {
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
  private final class ValuesIterator extends AbstractIterator<ObjectCursor<VType>> {
    private final ObjectCursor<VType> cursor;
    private final int max = mask + 1;
    private int slot = -1;

    public ValuesIterator() {
      cursor = new ObjectCursor<VType>();
    }

    @Override
    protected ObjectCursor<VType> fetch() {
      if (slot < max) {
        for (slot++; slot < max; slot++) {
          if (!(( keys[slot]) == 0)) {
            cursor.index = slot;
            cursor.value = (VType) values[slot];
            return cursor;
          }
        }
      }

      if (slot == max && hasEmptyKey) {
        cursor.index = slot;
        cursor.value = (VType) values[max];
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
  public CharObjectHashMap<VType> clone() {
    try {
      /*  */
      CharObjectHashMap<VType> cloned = (CharObjectHashMap<VType>) super.clone();
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
    for (CharObjectCursor<VType> cursor : this) {
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
    return CharBufferVisualizer.visualizeKeyDistribution(keys, mask, characters);
  }

  /**
   * Creates a hash map from two index-aligned arrays of key-value pairs.
   */
  public static <VType> CharObjectHashMap<VType> from(char[] keys, VType[] values) {
    if (keys.length != values.length) {
      throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
    }

    CharObjectHashMap<VType> map = new CharObjectHashMap<>(keys.length);
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
  int hashKey(char key) {
    assert !((key) == 0); // Handled as a special case (empty slot marker).
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
  protected void rehash(char[] fromKeys, VType[] fromValues) {
    assert fromKeys.length == fromValues.length &&
           HashContainers.checkPowerOfTwo(fromKeys.length - 1);
    
    // Rehash all stored key/value pairs into the new buffers.
    final char[] keys =  this.keys;
    final VType[] values = (VType[]) this.values;
    final int mask = this.mask;
    char existing;

    // Copy the zero element's slot, then rehash everything else.
    int from = fromKeys.length - 1;
    keys[keys.length - 1] = fromKeys[from];
    values[values.length - 1] = fromValues[from];
    while (--from >= 0) {
      if (!((existing = fromKeys[from]) == 0)) {
        int slot = hashKey(existing) & mask;
        while (!((keys[slot]) == 0)) {
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
    char[] prevKeys =  this.keys;
    VType[] prevValues = (VType[]) this.values;
    try {
      int emptyElementSlot = 1;
      this.keys = (new char [arraySize + emptyElementSlot]);
      this.values = ((VType[]) new Object [arraySize + emptyElementSlot]);
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
  protected void allocateThenInsertThenRehash(int slot, char pendingKey, VType pendingValue) {
    assert assigned == resizeAt
           && (( keys[slot]) == 0)
           && !((pendingKey) == 0);

    // Try to allocate new buffers first. If we OOM, we leave in a consistent state.
    final char[] prevKeys =  this.keys;
    final VType[] prevValues = (VType[]) this.values;
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
    final char[] keys =  this.keys;
    final VType[] values = (VType[]) this.values;
    final int mask = this.mask;

    // Perform shifts of conflicting keys to fill in the gap.
    int distance = 0;
    while (true) {
      final int slot = (gapSlot + (++distance)) & mask;
      final char existing = keys[slot];
      if (((existing) == 0)) {
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
    keys[gapSlot] = ((char) 0);
    values[gapSlot] = null;
    assigned--;
  }

        
}
