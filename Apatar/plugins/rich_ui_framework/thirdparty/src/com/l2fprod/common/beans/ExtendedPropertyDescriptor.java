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
package com.l2fprod.common.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * ExtendedPropertyDescriptor. <br>
 *  
 */
public class ExtendedPropertyDescriptor extends PropertyDescriptor {

  private Class tableCellRendererClass = null;
  private String category = "";

  public ExtendedPropertyDescriptor(String propertyName, Class beanClass)
    throws IntrospectionException {
    super(propertyName, beanClass);
  }

  public ExtendedPropertyDescriptor(
    String propertyName,
    Method getter,
    Method setter)
    throws IntrospectionException {
    super(propertyName, getter, setter);
  }

  public ExtendedPropertyDescriptor(
    String propertyName,
    Class beanClass,
    String getterName,
    String setterName)
    throws IntrospectionException {
    super(propertyName, beanClass, getterName, setterName);
  }

  /**
   * Sets this property category
   * 
   * @param category
   * @return this property for chaining calls.
   */
  public ExtendedPropertyDescriptor setCategory(String category) {
    this.category = category;
    return this;
  }

  /**
   * @return the category in which this property belongs
   */
  public String getCategory() {
    return category;
  }

  /**
   * Force this property to be readonly
   * 
   * @return this property for chaining calls.
   */
  public ExtendedPropertyDescriptor setReadOnly() {
    try {
      setWriteMethod(null);
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }
    return this;
  }


  /**
   * You can associate a special tablecellrenderer with a particular
   * Property. If set to null default renderer will be used.
   *
   * @param tableCellRendererClass
   */
  public void setPropertyTableRendererClass(Class tableCellRendererClass) {
    this.tableCellRendererClass = tableCellRendererClass;
  }

  /**
   * null or a custom TableCellRenderer-Class for this property
   * @return
   */
  public Class getPropertyTableRendererClass() {
    return (this.tableCellRendererClass);
  }

  public static ExtendedPropertyDescriptor newPropertyDescriptor(
    String propertyName,
    Class beanClass)
    throws IntrospectionException {
    // the same initialization phase as in the PropertyDescriptor
    Method readMethod = BeanUtils.getReadMethod(beanClass, propertyName);
    Method writeMethod = null;

    if (readMethod == null) {
      throw new IntrospectionException(
        "No getter for property "
          + propertyName
          + " in class "
          + beanClass.getName());
    }

    writeMethod =
      BeanUtils.getWriteMethod(
        beanClass,
        propertyName,
        readMethod.getReturnType());

    return new ExtendedPropertyDescriptor(
      propertyName,
      readMethod,
      writeMethod);
  }

  public static final Comparator BY_CATEGORY_COMPARATOR = new Comparator() {
    public int compare(Object o1, Object o2) {
      PropertyDescriptor desc1 = (PropertyDescriptor)o1;
      PropertyDescriptor desc2 = (PropertyDescriptor)o2;

      if (desc1 == null && desc2 == null) {
        return 0;
      } else if (desc1 != null && desc2 == null) {
        return 1;
      } else if (desc1 == null && desc2 != null) {
        return -1;
      } else {
        if (desc1 instanceof ExtendedPropertyDescriptor
          && !(desc2 instanceof ExtendedPropertyDescriptor)) {
          return -1;
        } else if (
          !(desc1 instanceof ExtendedPropertyDescriptor)
            && desc2 instanceof ExtendedPropertyDescriptor) {
          return 1;
        } else if (
          !(desc1 instanceof ExtendedPropertyDescriptor)
            && !(desc2 instanceof ExtendedPropertyDescriptor)) {
          return String.CASE_INSENSITIVE_ORDER.compare(
            desc1.getDisplayName(),
            desc2.getDisplayName());
        } else {
          int category =
            String.CASE_INSENSITIVE_ORDER.compare(
              ((ExtendedPropertyDescriptor)desc1).getCategory() == null
                ? ""
                : ((ExtendedPropertyDescriptor)desc1).getCategory(),
              ((ExtendedPropertyDescriptor)desc2).getCategory() == null
                ? ""
                : ((ExtendedPropertyDescriptor)desc2).getCategory());
          if (category == 0) {
            return String.CASE_INSENSITIVE_ORDER.compare(
              desc1.getDisplayName(),
              desc2.getDisplayName());
          } else {
            return category;
          }
        }
      }
    }
  };

}
