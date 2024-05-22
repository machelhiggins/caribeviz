package com.carrotsearch.hppc;

import java.util.Arrays;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;

/**
 * Common superclass for collections. 
 */
  
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:04+0200",
    value = "AbstractKTypeCollection.java") 
abstract class AbstractLongCollection 
  implements   
             LongCollection
{
  /**
   * Default implementation uses a predicate for removal.
   */
  @Override
  public int removeAll(final LongLookupContainer c) {
    // We know c holds sub-types of long and we're not modifying c, so go unchecked.
    return this.removeAll(new LongPredicate() {
      public boolean apply(long k) {
        return c.contains(k);
      }
    });
  }

  /**
   * Default implementation uses a predicate for retaining.
   */
  @Override
  public int retainAll(final LongLookupContainer c) {
    // We know c holds sub-types of long and we're not modifying c, so go unchecked.
    return this.removeAll(new LongPredicate() {
      public boolean apply(long k) {
        return !c.contains(k);
      }
    });
  }

  /**
   * Default implementation redirects to {@link #removeAll(LongPredicate)} and
   * negates the predicate.
   */
  @Override
  public int retainAll(final LongPredicate predicate) {
    return removeAll(new LongPredicate() {
      public boolean apply(long value) {
        return !predicate.apply(value);
      };
    });
  }

  /**
   * Default implementation of copying to an array.
   */
  @Override
    public long [] toArray()
 
  {
    long[] array = (new long [size()]);
    int i = 0;
    for (LongCursor c : this) {
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
