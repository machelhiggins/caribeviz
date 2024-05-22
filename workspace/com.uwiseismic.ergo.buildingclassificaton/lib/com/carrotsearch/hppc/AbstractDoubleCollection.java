package com.carrotsearch.hppc;

import java.util.Arrays;

import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.predicates.DoublePredicate;

/**
 * Common superclass for collections. 
 */
  
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:04+0200",
    value = "AbstractKTypeCollection.java") 
abstract class AbstractDoubleCollection 
  implements   
             DoubleCollection
{
  /**
   * Default implementation uses a predicate for removal.
   */
  @Override
  public int removeAll(final DoubleLookupContainer c) {
    // We know c holds sub-types of double and we're not modifying c, so go unchecked.
    return this.removeAll(new DoublePredicate() {
      public boolean apply(double k) {
        return c.contains(k);
      }
    });
  }

  /**
   * Default implementation uses a predicate for retaining.
   */
  @Override
  public int retainAll(final DoubleLookupContainer c) {
    // We know c holds sub-types of double and we're not modifying c, so go unchecked.
    return this.removeAll(new DoublePredicate() {
      public boolean apply(double k) {
        return !c.contains(k);
      }
    });
  }

  /**
   * Default implementation redirects to {@link #removeAll(DoublePredicate)} and
   * negates the predicate.
   */
  @Override
  public int retainAll(final DoublePredicate predicate) {
    return removeAll(new DoublePredicate() {
      public boolean apply(double value) {
        return !predicate.apply(value);
      };
    });
  }

  /**
   * Default implementation of copying to an array.
   */
  @Override
    public double [] toArray()
 
  {
    double[] array = (new double [size()]);
    int i = 0;
    for (DoubleCursor c : this) {
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
