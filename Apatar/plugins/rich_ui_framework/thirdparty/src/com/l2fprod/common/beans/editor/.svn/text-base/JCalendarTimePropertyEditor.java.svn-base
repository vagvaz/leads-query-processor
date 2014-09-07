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

package com.l2fprod.common.beans.editor;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class JCalendarTimePropertyEditor extends JCalendarDatePropertyEditor {
	
	public JCalendarTimePropertyEditor() {
		JSpinner spinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
		JSpinner.DateEditor de = new JSpinner.DateEditor(spinner, "HH:mm:ss");
		spinner.setEditor(de);
	    editor = spinner;
	}
	
	  /**
	   * Returns the Date of the Calendar
	   * 
	   * @return the date choosed as a <b>java.util.Date </b>b> object or
	   *         null is the date is not set
	   */
	  public Object getValue() {
		  SpinnerDateModel model = (SpinnerDateModel)((JSpinner)editor).getModel();
		  Date date = model.getDate();
		  java.sql.Time time = new java.sql.Time(date.getTime());
	    return time;
	  }

	  /**
	   * Sets the Date of the Calendar
	   * 
	   * @param value the Date object
	   */
	  public void setValue(Object value) {
	    if (value != null) {
	    	java.sql.Time time = (java.sql.Time)value;
	    	((JSpinner)editor).setValue(new Date(time.getTime()));
	    }
	  }
	  
}
