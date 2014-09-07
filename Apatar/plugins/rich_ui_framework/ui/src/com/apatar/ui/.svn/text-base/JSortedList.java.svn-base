/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

package com.apatar.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class JSortedList<T> extends JList {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Comparator<T> comparator;
	/**
	 * Constructs a SortedList with the provided non-null
	 * DefaultListModel and Comparator
	 * The SortedList is sorted only if listData is sorted.
	 *
	 * @param listData a DefaultListModel to store in the list
	 * @param compare a Comparator to allow for sorting
	 */
	public JSortedList(Comparator<T> compare) {
	  super(new DefaultListModel());
	  this.comparator = compare;
	  setComponentPopupMenu( new JDefaultContextMenu(this) );
	}
	
	/**
	 * Sorts the elements of the SortedList using the collation
	 * rules of the default Locale.
	 *
	 * Although the List (Vector) creation and model update appear
	 * to be a lot of overhead, no model data is actually moving. Only
	 * model element references are being copied.
	 */
	@SuppressWarnings("unchecked")
	public void sort() {
	  DefaultListModel model = (DefaultListModel)this.getModel();
	  // We need a List (a Vector) for sorting
	  int size = model.getSize();
	  Vector<T> list = new Vector<T>();
	  for (int x = 0; x < size; ++x) {
	    T o = (T)model.get(x);
	    list.addElement(o);
	  }

	  // sort the List
	  Collections.sort(list, comparator);
	  // update the model with a sorted List
	  for (int x = 0; x < size; ++x) {
	    if (model.getElementAt(x) != list.elementAt(x)) {
	      model.set(x, list.elementAt(x));
	    }
	  }
	}

	/**
	 * Add a new element to the SortedList model.
	 *
	 * @param element the element to add to the model
	 * @param bSort <code>true</code> indicates that the newly added
	 * element should be inserted in its correctly sorted
	 * location, ie perform an insertion sort; <code>false</code> 
	 * indicates that the element should be appended to the model
	 */
	public void add(T element, boolean bSort) {
	  DefaultListModel model = (DefaultListModel)this.getModel();
	  if (!bSort) {
	    model.addElement(element);
	  }
	  else {
	    int insertionPoint = findInsertionPoint(element);
	    model.insertElementAt(element, insertionPoint);
	  }
	}

	/**
	 * Find the insertion point for the argument in a sorted list.
	 * 
	 * @param element find this object's insertion point in the sorted list
	 * @return the index of the insertion point
	 */
	@SuppressWarnings("unchecked")
	int findInsertionPoint(T element) {
	  DefaultListModel model = (DefaultListModel)this.getModel();
	  // Copy the model data references to a Vector.
	  int size = model.getSize();
	  Vector<T> list = new Vector<T>();
	  for (int x = 0; x < size; ++x) {
	    T o = (T)model.get(x);
	    list.addElement(o);
	  }

	  // Find the new element's insertion point.
	  int insertionPoint = Collections.binarySearch(list, element, comparator);
	  if (insertionPoint < 0 ) {
	    insertionPoint = -(insertionPoint + 1);
	  }
	  return insertionPoint;
	}
}
