package com.carrotsearch.hppc;

/**
 * Marker interface for containers that can check if they contain a given object
 * in at least time <code>O(log n)</code> and ideally in amortized constant time
 * <code>O(1)</code>.
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeLookupContainer.java") 
public interface DoubleLookupContainer extends DoubleContainer {
  public boolean contains(double e);
}