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

import java.awt.Component;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JPanel;

public class JSortedPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	Comparator comparator;

	public JSortedPanel() {
		this(Collator.getInstance());
	}

	public JSortedPanel(Comparator comparator) {
		super();
		this.comparator = comparator;
	}
	
	@SuppressWarnings("unchecked")
	public void sort() {
		  // We need a List (a Vector) for sorting
		  int size = getComponentCount();
		  Vector<Object> list = new Vector<Object>();
		  for (int x = 0; x < size; ++x) {
		    Object o = getComponent(x);
		    list.addElement(o);
		  }

		  // sort the List
		  Collections.sort(list, comparator);
		  // update the model with a sorted List
		  for (int x = 0; x < size; ++x) {
		      add((Component)list.elementAt(x), x);
		  }
	}
	
	public void addComponent(Object element, boolean bSort) {
		  if (!bSort) {
		    add((Component)element);
		  }
		  else {
		    int insertionPoint = findInsertionPoint(element);
		    add((Component)element, insertionPoint);
		  }
		}

		/**
		 * Find the insertion point for the argument in a sorted list.
		 * 
		 * @param element find this object's insertion point in the sorted list
		 * @return the index of the insertion point
		 */
		@SuppressWarnings("unchecked")
		int findInsertionPoint(Object element) {
		  // Copy the model data references to a Vector.
		  int size = getComponentCount();
		  Vector<Object> list = new Vector<Object>();
		  for (int x = 0; x < size; ++x) {
		    String o = getComponent(x).toString();
		    list.addElement(o);
		  }

		  // Find the new element's insertion point.
		  int insertionPoint = Collections.binarySearch(list, element.toString(), comparator);
		  if (insertionPoint < 0 ) {
		    insertionPoint = -(insertionPoint + 1);
		  }
		  return insertionPoint;
		}
}
