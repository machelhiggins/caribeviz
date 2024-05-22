  
package com.carrotsearch.hppc;

/**
 * A set of <code>Object</code>s.
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeSet.java") 
public interface ObjectSet<KType> extends ObjectCollection<KType> {
  /**
   * Adds <code>k</code> to the set.
   * 
   * @return Returns <code>true</code> if this element was not part of the set
   *         before. Returns <code>false</code> if an equal element is already part of
   *         the set, <b>does not replace the existing element</b> with the argument.
   */
  public boolean add(KType k);

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
