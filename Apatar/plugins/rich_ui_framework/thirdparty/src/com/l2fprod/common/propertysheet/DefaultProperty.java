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

/**
 * $ $ License.
 *
 * Copyright $ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.l2fprod.common.propertysheet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.l2fprod.common.beans.BeanUtils;

/**
 * DefaultProperty. <br>
 *  
 */
@SuppressWarnings({"unchecked", "serial"})
public class DefaultProperty extends AbstractProperty implements Cloneable {

  private String name;
  private String displayName;
  private String shortDescription;
  private Class type;
  private boolean editable = true;
  private String category;
  private Property parent;
  private List subProperties = new ArrayList();

  public Object clone() {
    DefaultProperty other = null;
    try {
    	other = (DefaultProperty) super.clone();
    } catch (CloneNotSupportedException e) {
    	e.printStackTrace();
    }
    other.name = name;
    other.displayName = displayName;
    other.shortDescription = shortDescription;
    other.type = type;
    other.editable = editable;
    other.category = category;
    other.setValue(getValue());
    return other;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public Class getType() {
    return type;
  }

  public void setType(Class type) {
    this.type = type;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Reads the value of this Property from the given object. It uses reflection
   * and looks for a method starting with "is" or "get" followed by the
   * capitalized Property name.
   */
  public void readFromObject(Object object) {
    try {
      Method method = BeanUtils.getReadMethod(object.getClass(), getName());
      if (method != null) {
        setValue(method.invoke(object, (Object[])null));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
    for (Iterator iter = subProperties.iterator(); iter.hasNext();) {
      Property subProperty = (Property)iter.next();
      subProperty.readFromObject(object);
    }
  }

  /**
   * Writes the value of the Property to the given object. It uses reflection
   * and looks for a method starting with "set" followed by the capitalized
   * Property name and with one parameter with the same type as the Property.
   */
  public void writeToObject(Object object) {
    try {
      Method method =
        BeanUtils.getWriteMethod(object.getClass(), getName(), getType());
      if (method != null) {
        method.invoke(object, new Object[] { getValue()});
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (parent != null)
    	parent.readFromObject(object);
    for (Iterator iter = subProperties.iterator(); iter.hasNext();) {
        Property subProperty = (Property)iter.next();
        subProperty.readFromObject(object);
      }
  }

  public int hashCode() {
    return 28 + ((name != null)?name.hashCode():3)
      + ((displayName != null)?displayName.hashCode():94)
      + ((shortDescription != null)?shortDescription.hashCode():394)
      + ((category != null)?category.hashCode():34)
      + ((type != null)?type.hashCode():39)
      + Boolean.valueOf(editable).hashCode();
  }
      
  public boolean equals(Object other) {
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
          
    if (other == this) {
      return true;
    }
          
    DefaultProperty dp = (DefaultProperty) other;
    
    return compare(name, dp.name) &&
      compare(displayName, dp.displayName) &&
      compare(shortDescription, dp.shortDescription) &&
      compare(category, dp.category) &&
      compare(type, dp.type) &&
      editable == dp.editable;
  }
      
  private boolean compare(Object o1, Object o2) {
    return (o1 != null)? o1.equals(o2) : o2 == null;
  }
      
  public String toString() {
    return "name=" + getName() + ", displayName=" + getDisplayName()
      + ", type=" + getType() + ", category=" + getCategory() + ", editable="
      + isEditable() + ", value=" + getValue();
  }

  public Property getParentProperty() {
  	return null;
  }
  
  public void setParentProperty( Property parent ) {
    this.parent = parent;
  }
  
  public Property[] getSubProperties() {
  	return (Property[]) subProperties.toArray( new Property[subProperties.size()] );
  }
  
  public void clearSubProperties() {
	for (Iterator iter = this.subProperties.iterator(); iter.hasNext();) {
		Property subProp = (Property) iter.next();
		if (subProp instanceof DefaultProperty)
			((DefaultProperty)subProp).setParentProperty(null);
	}
	this.subProperties.clear();
  }
  
  public void addSubProperties( Collection subProperties ) {
	this.subProperties.addAll( subProperties );
	for (Iterator iter = this.subProperties.iterator(); iter.hasNext();) {
		Property subProp = (Property) iter.next();
		if (subProp instanceof DefaultProperty)
			((DefaultProperty)subProp).setParentProperty(this);
	}
  }
  
  public void addSubProperties( Property[] subProperties ) {
	this.addSubProperties( Arrays.asList( subProperties ) );
  }
  
  public void addSubProperty( Property subProperty ) {
	this.subProperties.add( subProperty );
	if (subProperty instanceof DefaultProperty)
		((DefaultProperty)subProperty).setParentProperty(this);
  }
}