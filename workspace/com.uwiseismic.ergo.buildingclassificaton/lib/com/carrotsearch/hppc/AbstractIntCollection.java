package com.carrotsearch.hppc;

import java.util.Arrays;

import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.predicates.IntPredicate;

/**
 * Common superclass for collections. 
 */
  
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:04+0200",
    value = "AbstractKTypeCollection.java") 
abstract class AbstractIntCollection 
  implements   
             IntCollection
{
  /**
   * Default implementation uses a predicate for removal.
   */
  @Override
  public int removeAll(final IntLookupContainer c) {
    // We know c holds sub-types of int and we're not modifying c, so go unchecked.
    return this.removeAll(new IntPredicate() {
      public boolean apply(int k) {
        return c.contains(k);
      }
    });
  }

  /**
   * Default implementation uses a predicate for retaining.
   */
  @Override
  public int retainAll(final IntLookupContainer c) {
    // We know c holds sub-types of int and we're not modifying c, so go unchecked.
    return this.removeAll(new IntPredicate() {
      public boolean apply(int k) {
        return !c.contains(k);
      }
    });
  }

  /**
   * Default implementation redirects to {@link #removeAll(IntPredicate)} and
   * negates the predicate.
   */
  @Override
  public int retainAll(final IntPredicate predicate) {
    return removeAll(new IntPredicate() {
      public boolean apply(int value) {
        return !predicate.apply(value);
      };
    });
  }

  /**
   * Default implementation of copying to an array.
   */
  @Override
    public int [] toArray()
 
  {
    int[] array = (new int [size()]);
    int i = 0;
    for (IntCursor c : this) {
      array[i++] = c.value;
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

        
}
