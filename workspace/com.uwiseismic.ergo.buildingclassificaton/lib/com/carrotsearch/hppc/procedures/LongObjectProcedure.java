package com.carrotsearch.hppc.procedures;

/**
 * A procedure that applies to <code>long</code>, <code>Object</code> pairs.
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:07+0200",
    value = "KTypeVTypeProcedure.java") 
public interface LongObjectProcedure<VType> {
  public void apply(long key, VType value);
}