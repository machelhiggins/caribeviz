package com.carrotsearch.hppc;

import java.util.Arrays;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.predicates.ObjectPredicate;

/**
 * Common superclass for collections. 
 */
  
@SuppressWarnings("unchecked")
  
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:04+0200",
    value = "AbstractKTypeCollection.java") 
abstract class AbstractObjectCollection<KType> 
  implements   
             ObjectCollection<KType>
{
  /**
   * Default implementation uses a predicate for removal.
   */
  @Override
  public int removeAll(final ObjectLookupContainer<? super KType> c) {
    // We know c holds sub-types of Object and we're not modifying c, so go unchecked.
    return this.removeAll(new ObjectPredicate<KType>() {
      public boolean apply(KType k) {
        return c.contains(k);
      }
    });
  }

  /**
   * Default implementation uses a predicate for retaining.
   */
  @Override
  public int retainAll(final ObjectLookupContainer<? super KType> c) {
    // We know c holds sub-types of Object and we're not modifying c, so go unchecked.
    return this.removeAll(new ObjectPredicate<KType>() {
      public boolean apply(KType k) {
        return !c.contains(k);
      }
    });
  }

  /**
   * Default implementation redirects to {@link #removeAll(ObjectPredicate)} and
   * negates the predicate.
   */
  @Override
  public int retainAll(final ObjectPredicate<? super KType> predicate) {
    return removeAll(new ObjectPredicate<KType>() {
      public boolean apply(KType value) {
        return !predicate.apply(value);
      };
    });
  }

  /**
   * Default implementation of copying to an array.
   */
  @Override
   
  public Object[] toArray()
    
  {
    KType[] array = ((KType[]) new Object [size()]);
    int i = 0;
    for (ObjectCursor<KType> c : this) {
      array[i++] = c.value;
    }
    return array;
  }

   
  public <T> T [] toArray(Class<T> componentClass) {
    final int size = size();
    final T[] array = (T[]) java.lang.reflect.Array.newInstance(componentClass, size);
    int i = 0;
    for (ObjectCursor<KType> c : this) {
      array[i++] = (T) c.value;
    }
    return array;
  }
    

  /**
   * Convert the contents of this container to a human-friendly string.
   */
  @Override
  public String toString() {
    return Arrays.toString(this.toArray());
  }

    
    protected   boolean equals(Object v1, Object v2) {
    return (v1 == v2) || (v1 != null && v1.equals(v2));
  }
        
}
