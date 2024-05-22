package com.carrotsearch.hppc;

import java.util.RandomAccess;

/**
 * An indexed container provides random access to elements based on an
 * <code>index</code>. Indexes are zero-based.
 */
 @com.carrotsearch.hppc.Generated(
    date = "2018-05-21T12:24:05+0200",
    value = "KTypeIndexedContainer.java") 
public interface DoubleIndexedContainer extends DoubleCollection, RandomAccess {
  /**
   * Removes the first element that equals <code>e1</code>, returning its
   * deleted position or <code>-1</code> if the element was not found.
   */
  public int removeFirst(double e1);

  /**
   * Removes the last element that equals <code>e1</code>, returning its deleted
   * position or <code>-1</code> if the element was not found.
   */
  public int removeLast(double e1);

  /**
   * Returns the index of the first occurrence of the specified element in this
   * list, or -1 if this list does not contain the element.
   */
  public int indexOf(double e1);

  /**
   * Returns the index of the last occurrence of the specified element in this
   * list, or -1 if this list does not contain the element.
   */
  public int lastIndexOf(double e1);

  /**
   * Adds an element to the end of this container (the last index is incremented
   * by one).
   */
  public void add(double e1);

  /**
   * Inserts the specified element at the specified position in this list.
   * 
   * @param index
   *          The index at which the element should be inserted, shifting any
   *          existing and subsequent elements to the right.
   */
  public void insert(int index, double e1);

  /**
   * Replaces the element at the specified position in this list with the
   * specified element.
   * 
   * @return Returns the previous value in the list.
   */
  public double set(int index, double e1);

  /**
   * @return Returns the element at index <code>index</code> from the list.
   */
  public double get(int index);

  /**
   * Removes the element at the specified position in this container and returns
   * it.
   * 
   * @see #removeFirst
   * @see #removeLast
   * @see #removeAll
   */
  public double remove(int index);

  /**
   * Removes from this container all of the elements with indexes between
   * <code>fromIndex</code>, inclusive, and <code>toIndex</code>, exclusive.
   */
  public void removeRange(int fromIndex, int toIndex);
}
