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
import com.l2fprod.common.beans.editor.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping between Properties, Property Types and Property Editors.
 */
@SuppressWarnings({"unchecked", "serial"})
public class PropertyEditorRegistry implements PropertyEditorFactory {

  private Map typeToEditor;
  private Map propertyToEditor;

  public PropertyEditorRegistry() {
    typeToEditor = new HashMap();
    propertyToEditor = new HashMap();
    registerDefaults();
  }

  public PropertyEditor createPropertyEditor(Property property) {
    return getEditor(property);
  }

  /**
   * Gets an editor for the given property. The lookup is as follow:
   * <ul>
   * <li>if propertyDescriptor.getPropertyEditorClass() returns a valid value,
   * it is returned, else,
   * <li>if an editor was registered with
   * {@link #registerEditor(Property, PropertyEditor)}, it is
   * returned, else</li>
   * <li>if an editor class was registered with
   * {@link #registerEditor(Property, Class)}, it is returned, else
   * <li>
   * <li>look for editor for the property type using
   * {@link #getEditor(Class)}.it is returned, else
   * </li>
   * <li>look for editor using PropertyEditorManager.findEditor(Class);
   * </li>
   * </ul>
   *
   * @param property
   * @return an editor suitable for the Property.
   */
  public synchronized PropertyEditor getEditor(Property property) {
    PropertyEditor editor = null;
    if (property instanceof PropertyDescriptorAdapter) {
      PropertyDescriptor descriptor = ((PropertyDescriptorAdapter) property).getDescriptor();
      if (descriptor != null) {
        Class clz = descriptor.getPropertyEditorClass();
        if (clz != null) {
          editor = loadPropertyEditor(clz);
        }
      }
    }
    if (editor == null) {
      Object value = propertyToEditor.get(property);
      if (value instanceof PropertyEditor) {
        editor = (PropertyEditor) value;
      } else if (value instanceof Class) {
        editor = loadPropertyEditor((Class) value);
      } else {
        editor = getEditor(property.getType());
      }
    }
    if ((editor == null) && (property instanceof PropertyDescriptorAdapter)) {
      PropertyDescriptor descriptor = ((PropertyDescriptorAdapter) property).getDescriptor();
      Class clz = descriptor.getPropertyType();
      editor = PropertyEditorManager.findEditor(clz);
    }
    return editor;
  }

  /**
   * Load PropertyEditor from clz through reflection.
   * @param clz Class to load from.
   * @return Loaded propertyEditor
   */
  private PropertyEditor loadPropertyEditor(Class clz) {
    PropertyEditor editor = null;
    try {
      editor = (PropertyEditor) clz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return editor;
  }

  /**
   * Gets an editor for the given property type. The lookup is as
   * follow:
   * <ul>
   * <li>if an editor was registered with
   * {@link #registerEditor(Class, PropertyEditor)}, it is returned,
   * else</li>
   * <li>if an editor class was registered with
   * {@link #registerEditor(Class, Class)}, it is returned, else
   * <li>
   * <li>it returns null.</li>
   * </ul>
   *
   * @param type
   * @return an editor suitable for the Property type or null if none
   *         found
   */
  public synchronized PropertyEditor getEditor(Class type) {
    PropertyEditor editor = null;
    Object value = typeToEditor.get(type);
    if (value instanceof PropertyEditor) {
      editor = (PropertyEditor)value;
    } else if (value instanceof Class) {
      try {
        editor = (PropertyEditor)((Class)value).newInstance();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return editor;
  }

  public synchronized void registerEditor(Class type, Class editorClass) {
    typeToEditor.put(type, editorClass);
  }

  public synchronized void registerEditor(Class type, PropertyEditor editor) {
    typeToEditor.put(type, editor);
  }

  public synchronized void unregisterEditor(Class type) {
    typeToEditor.remove(type);
  }

  public synchronized void registerEditor(Property property, Class editorClass) {
    propertyToEditor.put(property, editorClass);
  }

  public synchronized void registerEditor(Property property,
      PropertyEditor editor) {
    propertyToEditor.put(property, editor);
  }

  public synchronized void unregisterEditor(Property property) {
    propertyToEditor.remove(property);
  }

  /**
   * Adds default editors. This method is called by the constructor
   * but may be called later to reset any customizations made through
   * the <code>registerEditor</code> methods. <b>Note: if overriden,
   * <code>super.registerDefaults()</code> must be called before
   * plugging custom defaults. </b>
   */
  public void registerDefaults() {
    typeToEditor.clear();
    propertyToEditor.clear();

    // our editors
    registerEditor(String.class, StringPropertyEditor.class);

    registerEditor(PasswordString.class, PasswordStringPropertyEditor.class);

    registerEditor(double.class, DoublePropertyEditor.class);
    registerEditor(Double.class, DoublePropertyEditor.class);

    registerEditor(float.class, FloatPropertyEditor.class);
    registerEditor(Float.class, FloatPropertyEditor.class);

    registerEditor(int.class, IntegerPropertyEditor.class);
    registerEditor(Integer.class, IntegerPropertyEditor.class);

    registerEditor(long.class, LongPropertyEditor.class);
    registerEditor(Long.class, LongPropertyEditor.class);

    registerEditor(short.class, ShortPropertyEditor.class);
    registerEditor(Short.class, ShortPropertyEditor.class);

    registerEditor(boolean.class, BooleanAsCheckBoxPropertyEditor.class);
    registerEditor(Boolean.class, BooleanAsCheckBoxPropertyEditor.class);

    registerEditor(File.class, FilePropertyEditor.class);

    registerEditor(FolderPath.class, FolderPathPropertyEditor.class);

    registerEditor(Enum.class, ComboBoxFromEnumPropertyEditor.class);

    // awt object editors
    registerEditor(Color.class, ColorPropertyEditor.class);
    registerEditor(Dimension.class, DimensionPropertyEditor.class);
    registerEditor(Insets.class, InsetsPropertyEditor.class);
    try {
      Class fontEditor =
        Class.forName("com.l2fprod.common.beans.editor.FontPropertyEditor");
      registerEditor(Font.class, fontEditor);
    } catch (Exception e) {
      // FontPropertyEditor might not be there when using the split jars
    }
    registerEditor(Rectangle.class, RectanglePropertyEditor.class);


    //
    // Date Editors based on what we have in the classpath
    //

    boolean foundDateEditor = false;

    // if JCalendar jar is available, use it as the default date
    // editor
    try {
      Class.forName("com.toedter.calendar.JDateChooser");
      registerEditor(Date.class, JCalendarDatePropertyEditor.class);
      foundDateEditor = true;
    } catch (ClassNotFoundException e) {
      // No JCalendar found
    }

    registerEditor(Time.class, JCalendarTimePropertyEditor.class);

    if (!foundDateEditor) {
      // try NachoCalendar
      try {
        Class.forName("net.sf.nachocalendar.components.DateField");
        registerEditor(
          Date.class,
          Class
            .forName("com.l2fprod.common.beans.editor.NachoCalendarDatePropertyEditor"));
        foundDateEditor = true;
      } catch (ClassNotFoundException e) {
        // No NachoCalendar found
      }
    }
  }

}