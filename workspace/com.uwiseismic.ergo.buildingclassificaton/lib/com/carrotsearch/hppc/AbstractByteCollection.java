package com.carrotsearch.hppc;

import java.util.Arrays;

import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.predicates.BytePredicate;

/**
 * Common superclass for collections. 
 */
  
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:04+0200",
    value = "AbstractKTypeCollection.java") 
abstract class AbstractByteCollection 
  implements   
             ByteCollection
{
  /**
   * Default implementation uses a predicate for removal.
   */
  @Override
  public int removeAll(final ByteLookupContainer c) {
    // We know c holds sub-types of byte and we're not modifying c, so go unchecked.
    return this.removeAll(new BytePredicate() {
      public boolean apply(byte k) {
        return c.contains(k);
      }
    });
  }

  /**
   * Default implementation uses a predicate for retaining.
   */
  @Override
  public int retainAll(final ByteLookupContainer c) {
    // We know c holds sub-types of byte and we're not modifying c, so go unchecked.
    return this.removeAll(new BytePredicate() {
      public boolean apply(byte k) {
        return !c.contains(k);
      }
    });
  }

  /**
   * Default implementation redirects to {@link #removeAll(BytePredicate)} and
   * negates the predicate.
   */
  @Override
  public int retainAll(final BytePredicate predicate) {
    return removeAll(new BytePredicate() {
      public boolean apply(byte value) {
        return !predicate.apply(value);
      };
    });
  }

  /**
   * Default implementation of copying to an array.
   */
  @Override
    public byte [] toArray()
 
  {
    byte[] array = (new byte [size()]);
    int i = 0;
    for (ByteCursor c : this) {
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
