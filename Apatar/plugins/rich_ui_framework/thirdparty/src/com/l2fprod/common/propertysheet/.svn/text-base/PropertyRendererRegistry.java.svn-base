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

import com.apatar.core.FolderPath;
import com.apatar.core.PasswordString;
import com.l2fprod.common.swing.renderer.BooleanCellRenderer;
import com.l2fprod.common.swing.renderer.ColorCellRenderer;
import com.l2fprod.common.swing.renderer.DateRenderer;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import com.l2fprod.common.swing.renderer.FolderPathCellRenderer;
import com.l2fprod.common.swing.renderer.PasswordStringCellRenderer;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;

import java.awt.Color;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.beans.PropertyDescriptor;

import javax.swing.table.TableCellRenderer;

/**
 * Mapping between Properties, Property Types and Renderers.
 */
@SuppressWarnings({"unchecked", "serial"})
public class PropertyRendererRegistry implements PropertyRendererFactory {

  private Map typeToRenderer;
  private Map propertyToRenderer;

  public PropertyRendererRegistry() {
    typeToRenderer = new HashMap();
    propertyToRenderer = new HashMap();
    registerDefaults();
  }
  
  public TableCellRenderer createTableCellRenderer(Property property) {
    return getRenderer(property);
  }

  public TableCellRenderer createTableCellRenderer(Class type) {
    return getRenderer(type);
  }

  /**
   * Gets a renderer for the given property. The lookup is as follow:
   * <ul>
   * <li>if a renderer was registered with
   * {@link ExtendedPropertyDescriptor#setPropertyTableRendererClass(Class)} - BeanInfo, it is
   * returned, else</li>
   * <li>if a renderer was registered with
   * {@link #registerRenderer(Property, TableCellRenderer)}, it is
   * returned, else</li>
   * <li>if a renderer class was registered with
   * {@link #registerRenderer(Property, Class)}, it is returned, else
   * <li>
   * <li>look for renderer for the property type using
   * {@link #getRenderer(Class)}.</li>
   * </ul>
   * 
   * @param property
   * @return a renderer suitable for the Property.
   */
  public synchronized TableCellRenderer getRenderer(Property property)
 {

   // editors bound to the property descriptor have the highest priority
   TableCellRenderer renderer = null;
   if (property instanceof PropertyDescriptorAdapter) {
     PropertyDescriptor descriptor = ((PropertyDescriptorAdapter) property).getDescriptor();
     if (descriptor instanceof ExtendedPropertyDescriptor) {
       if (((ExtendedPropertyDescriptor) descriptor).getPropertyTableRendererClass() != null) {
         try {
           return (TableCellRenderer) (((ExtendedPropertyDescriptor) descriptor).getPropertyTableRendererClass()).newInstance();
         }
         catch (Exception ex){
           ex.printStackTrace();
         }
       }
     }
   }
    Object value = propertyToRenderer.get(property);
    if (value instanceof TableCellRenderer) {
      renderer = (TableCellRenderer)value;
    } else if (value instanceof Class) {
      try {
        renderer = (TableCellRenderer)((Class)value).newInstance();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      renderer = getRenderer(property.getType());
    }
    return renderer;
  }

  /**
   * Gets a renderer for the given property type. The lookup is as
   * follow:
   * <ul>
   * <li>if a renderer was registered with
   * {@link #registerRenderer(Class, TableCellRenderer)}, it is returned,
   * else</li>
   * <li>if a renderer class was registered with
   * {@link #registerRenderer(Class, Class)}, it is returned, else
   * <li>
   * <li>it returns null.</li>
   * </ul>
   * 
   * @param type
   * @return a renderer editor suitable for the Property type or null if none
   *         found
   */
  public synchronized TableCellRenderer getRenderer(Class type) {
    TableCellRenderer renderer = null;
    Object value = typeToRenderer.get(type);
    if (value instanceof TableCellRenderer) {
      renderer = (TableCellRenderer)value;
    } else if (value instanceof Class) {
      try {
        renderer = (TableCellRenderer)((Class)value).newInstance();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return renderer;
  }

  public synchronized void registerRenderer(Class type, Class rendererClass) {
    typeToRenderer.put(type, rendererClass);
  }

  public synchronized void registerRenderer(Class type, TableCellRenderer renderer) {
    typeToRenderer.put(type, renderer);
  }

  public synchronized void unregisterRenderer(Class type) {
    typeToRenderer.remove(type);
  }

  public synchronized void registerRenderer(Property property, Class rendererClass) {
    propertyToRenderer.put(property, rendererClass);
  }

  public synchronized void registerRenderer(Property property,
      TableCellRenderer renderer) {
    propertyToRenderer.put(property, renderer);
  }

  public synchronized void unregisterRenderer(Property property) {
    propertyToRenderer.remove(property);
  }

  /**
   * Adds default renderers. This method is called by the constructor
   * but may be called later to reset any customizations made through
   * the <code>registerRenderer</code> methods. <b>Note: if overriden,
   * <code>super.registerDefaults()</code> must be called before
   * plugging custom defaults. </b>
   */
  public void registerDefaults() {
    typeToRenderer.clear();
    propertyToRenderer.clear();

    // use the default renderer for Object and all primitives
    DefaultCellRenderer renderer = new DefaultCellRenderer();
    renderer.setShowOddAndEvenRows(false);

    PasswordStringCellRenderer passwordRenderer =
    	new PasswordStringCellRenderer();
    passwordRenderer.setShowOddAndEvenRows(false);
    
    FolderPathCellRenderer folderPathRenderer =
    	new FolderPathCellRenderer();
    folderPathRenderer.setShowOddAndEvenRows(false);
    
    ColorCellRenderer colorRenderer = new ColorCellRenderer();
    colorRenderer.setShowOddAndEvenRows(false);

    BooleanCellRenderer booleanRenderer = new BooleanCellRenderer();

    DateRenderer dateRenderer = new DateRenderer();
    dateRenderer.setShowOddAndEvenRows(false);
    
    registerRenderer(Object.class, renderer);
    registerRenderer(Color.class, colorRenderer);
    registerRenderer(boolean.class, booleanRenderer);
    registerRenderer(Boolean.class, booleanRenderer);
    registerRenderer(byte.class, renderer);
    registerRenderer(Byte.class, renderer);
    registerRenderer(char.class, renderer);
    registerRenderer(Character.class, renderer);
    registerRenderer(double.class, renderer);
    registerRenderer(Double.class, renderer);
    registerRenderer(float.class, renderer);
    registerRenderer(Float.class, renderer);
    registerRenderer(int.class, renderer);
    registerRenderer(Integer.class, renderer);
    registerRenderer(long.class, renderer);
    registerRenderer(Long.class, renderer);
    registerRenderer(short.class, renderer);
    registerRenderer(Short.class, renderer);
    registerRenderer(Date.class, dateRenderer);
    registerRenderer(Time.class, renderer);
    
    registerRenderer(PasswordString.class, passwordRenderer);
    registerRenderer(FolderPath.class, folderPathRenderer);
  }

}
