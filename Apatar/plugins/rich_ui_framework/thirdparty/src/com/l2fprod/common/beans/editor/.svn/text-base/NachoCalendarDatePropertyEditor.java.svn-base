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

import java.util.Date;
import java.util.Locale;

import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DateField;

/**
 * Date Property Editor based on <a
 * href="http://nachocalendar.sf.net">NachoCalendar</a> component.
 * <br>
 */
public class NachoCalendarDatePropertyEditor extends AbstractPropertyEditor {

  private String dateFormatString;
  
  /**
   * Constructor for NachoCalendarDatePropertyEditor
   */
  public NachoCalendarDatePropertyEditor() {
    editor = CalendarFactory.createDateField();
    ((DateField)editor).setValue(new Date());
  }

  /**
   * Constructor for NachoCalendarDatePropertyEditor
   * 
   * @param dateFormatString string used to format the Date object,
   *          see: <b>java.text.SimpleDateFormat </b>
   * 
   * @param locale Locale used to display the Date object
   */
  public NachoCalendarDatePropertyEditor(String dateFormatString, Locale locale) {
    editor = CalendarFactory.createDateField();
    ((DateField)editor).setValue(new Date());
  }

  /**
   * Constructor for NachoCalendarDatePropertyEditor
   * 
   * @param locale Locale used to display the Date object
   */
  public NachoCalendarDatePropertyEditor(Locale locale) {
    editor = CalendarFactory.createDateField();
    ((DateField)editor).setValue(new Date());
    ((DateField)editor).setLocale(locale);
  }

  /**
   * Returns the Date of the Calendar
   * 
   * @return the date choosed as a <b>java.util.Date </b>b> object or
   *         null is the date is not set
   */
  public Object getValue() {
    return ((DateField)editor).getValue();
  }

  /**
   * Sets the Date of the Calendar
   * 
   * @param value the Date object
   */
  public void setValue(Object value) {
    if (value != null) {
      ((DateField)editor).setValue(value);
    }
  }

  /**
   * Returns the Date formated with the locale and formatString set.
   * 
   * @return the choosen Date as String
   */
  public String getAsText() {
    Date date = (Date)getValue();
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
      getDateFormatString());
    String s = formatter.format(date);
    return s;
  }

  /**
   * Sets the date format string. E.g "MMMMM d, yyyy" will result in
   * "July 21, 2004" if this is the selected date and locale is
   * English.
   * 
   * @param dateFormatString The dateFormatString to set.
   */
  public void setDateFormatString(String dateFormatString) {
    this.dateFormatString = dateFormatString;
  }

  /**
   * Gets the date format string.
   * 
   * @return Returns the dateFormatString.
   */
  public String getDateFormatString() {
    return dateFormatString;
  }

  /**
   * Sets the locale.
   * 
   * @param l The new locale value
   */
  public void setLocale(Locale l) {
    ((DateField)editor).setLocale(l);
  }

  /**
   * Returns the Locale used.
   * 
   * @return the Locale object
   */
  public Locale getLocale() {
    return ((DateField)editor).getLocale();
  }

}