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

/*
 *  NachoCalendar
 *
 * Project Info:  http://nachocalendar.sf.net
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * Changes
 * -------
 *
 * 2004-10-01    Class is now final and default constructor is private
 *
 * -------
 *
 * TaskCalendarFactory.java
 *
 * Created on August 18, 2004, 10:49 PM
 */

package net.sf.nachocalendar.tasks;

import net.sf.nachocalendar.components.CalendarPanel;
import net.sf.nachocalendar.components.DateField;
import net.sf.nachocalendar.components.DatePanel;
import net.sf.nachocalendar.components.DefaultDayRenderer;
import net.sf.nachocalendar.components.DefaultHeaderRenderer;

/**
 * Factory class used to obtain objects customized to show Tasks.
 * @author Ignacio Merani
 */
public final class TaskCalendarFactory {
    
    /** Default constructor. */
    private TaskCalendarFactory() {
        
    }
    
    /**
     * Returns a new DateField customized to show Tasks.
     * @return a new DateField
     */    
    public static DateField createDateField() {
        DateField retorno = new DateField();
        configureDateField(retorno);
        return retorno;
    }
    
    private static void configureDateField(DateField df) {
        df.setModel(new TaskDataModel());
        df.setRenderer(new TaskDecorator(new DefaultDayRenderer()));
        df.setHeaderRenderer(new DefaultHeaderRenderer());
    }
    
    /**
     * Returns a new CalendarPanel customized to show Tasks.
     * @return a new CalendarPanel
     */    
    public static CalendarPanel createCalendarPanel() {
        CalendarPanel retorno = new CalendarPanel();
        configureCalendarPanel(retorno);
        return retorno;
    }
    
    /**
     * Returns a new CalendarPanel customized to show Tasks.
     * @param quantity quantity of months to show
     * @param orientation the orientation
     * @return a new CalendarPanel
     */    
    public static CalendarPanel createCalendarPanel(int quantity, int orientation) {
        CalendarPanel retorno = new CalendarPanel(quantity, orientation);
        configureCalendarPanel(retorno);
        return retorno;
    }
    
    /**
     * Returns a new CalendarPanel customized to show Tasks.
     * @param quantity quantity of months to show
     * @return a new CalendarPanel
     */    
    public static CalendarPanel createCalendarPanel(int quantity) {
        CalendarPanel retorno = new CalendarPanel(quantity, CalendarPanel.VERTICAL);
        configureCalendarPanel(retorno);
        return retorno;
    }
    
    private static void configureCalendarPanel(CalendarPanel cp) {
        cp.setModel(new TaskDataModel());
        cp.setRenderer(new TaskDecorator(new DefaultDayRenderer()));
        cp.setHeaderRenderer(new DefaultHeaderRenderer());
    }
    
    /**
     * Returns a new DatePanel customized to show Tasks.
     * @return a new DatePanel
     */    
    public static DatePanel createDatePanel() {
        DatePanel retorno = new DatePanel();
        configureDatePanel(retorno);
        return retorno;
    }
    
    /**
     * Returns a new DatePanel customized to show Tasks.
     * @param showWeekNumbers true to show week numbers
     * @return a new DatePanel
     */    
    public static DatePanel createDatePanel(boolean showWeekNumbers) {
        DatePanel retorno = new DatePanel(showWeekNumbers);
        configureDatePanel(retorno);
        return retorno;
    }
    
    private static void configureDatePanel(DatePanel dp) {
        dp.setModel(new TaskDataModel());
        dp.setRenderer(new TaskDecorator(new DefaultDayRenderer()));
        dp.setHeaderRenderer(new DefaultHeaderRenderer());
    }
}
