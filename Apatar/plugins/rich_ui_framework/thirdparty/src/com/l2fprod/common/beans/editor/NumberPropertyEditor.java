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
package com.l2fprod.common.beans.editor;

import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.l2fprod.common.util.converter.ConverterRegistry;
import com.l2fprod.common.util.converter.NumberConverters;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Base editor for numbers. <br>
 */
public class NumberPropertyEditor extends AbstractPropertyEditor {

  private final Class type;
  private Object lastGoodValue;
  
  public NumberPropertyEditor(Class type) {
    if (!Number.class.isAssignableFrom(type)) {
      throw new IllegalArgumentException("type must be a subclass of Number");
    }

    editor = new JFormattedTextField();
    this.type = type;
    ((JFormattedTextField)editor).setValue(getDefaultValue());
    ((JFormattedTextField)editor).setBorder(LookAndFeelTweaks.EMPTY_BORDER);

    // use a custom formatter to have numbers with up to 64 decimals
    NumberFormat format = NumberConverters.getDefaultFormat();

    ((JFormattedTextField) editor).setFormatterFactory(
        new DefaultFormatterFactory(new NumberFormatter(format))
    );
  }

  public Object getValue() {
    String text = ((JTextField)editor).getText();
    if (text == null || text.trim().length() == 0) {
      return getDefaultValue();
    }
    
    // allow comma or colon
    text = text.replace(',', '.');
    
    // collect all numbers from this textfield
    StringBuffer number = new StringBuffer();
    number.ensureCapacity(text.length());
    for (int i = 0, c = text.length(); i < c; i++) {
      char character = text.charAt(i);
      if ('.' == character || '-' == character
        || (Double.class.equals(type) && 'E' == character)
        || (Float.class.equals(type) && 'E' == character)
        || Character.isDigit(character)) {
        number.append(character);
      } else if (' ' == character) {
        continue;
      } else {
        break;
      }
    }
  
    try {
      lastGoodValue = ConverterRegistry.instance().convert(type,
        number.toString());      
    } catch (Exception e) {
      UIManager.getLookAndFeel().provideErrorFeedback(editor);
    }
    
    return lastGoodValue;
  }

  public void setValue(Object value) {
    if (value instanceof Number) {
      ((JFormattedTextField)editor).setText(value.toString());
    } else {
      ((JFormattedTextField)editor).setValue(getDefaultValue());
    }
    lastGoodValue = value;
  }

  private Object getDefaultValue() {
    try {
      return type.getConstructor(new Class[] {String.class}).newInstance(
        new Object[] {"0"});
    } catch (Exception e) {
      // will not happen
      throw new RuntimeException(e);
    }
  }

}