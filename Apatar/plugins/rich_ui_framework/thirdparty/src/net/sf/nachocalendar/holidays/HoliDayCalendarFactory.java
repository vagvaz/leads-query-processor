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
 * HoliDayCalendarFactory.java
 *
 * Created on August 17, 2004, 10:45 PM
 */

package net.sf.nachocalendar.holidays;

import net.sf.nachocalendar.components.CalendarPanel;
import net.sf.nachocalendar.components.DateField;
import net.sf.nachocalendar.components.DatePanel;
import net.sf.nachocalendar.components.DefaultDayRenderer;
import net.sf.nachocalendar.components.DefaultHeaderRenderer;

/**
 * Factory for holiday showing components.
 * @author Ignacio Merani
 */
public final class HoliDayCalendarFactory {
    /** Default Constructor. */
    private HoliDayCalendarFactory() {
        
    }
    /**
     * Creates a DateField with a parent Dialog.
     * @return a new DateField
     */    
    public static DateField createDateField() {
        DateField datefield = new DateField();
        datefield.setRenderer(new HolidayDecorator(new DefaultDayRenderer()));
        datefield.setHeaderRenderer(new DefaultHeaderRenderer());
        datefield.setModel(new HoliDayModel());
        return datefield;
    }
    
    /**
     * Creates a new CalendarPanel.
     * @param quantity quantity of months to show
     * @param orientation the orientation
     * @return a new CalendarPanel
     */    
    public static CalendarPanel createCalendarPanel(int quantity, int orientation) {
        CalendarPanel cp = new CalendarPanel(quantity, orientation);
        cp.setRenderer(new HolidayDecorator(new DefaultDayRenderer()));
        cp.setHeaderRenderer(new DefaultHeaderRenderer());
        cp.setModel(new HoliDayModel());
        return cp;
    }
    
    /**
     * Creates a new CalendarPanel with default values.
     * @return a new CalendarPanel
     */    
    public static CalendarPanel createCalendarPanel() {
        CalendarPanel cp = new CalendarPanel();
        cp.setRenderer(new HolidayDecorator(new DefaultDayRenderer()));
        cp.setHeaderRenderer(new DefaultHeaderRenderer());
        cp.setModel(new HoliDayModel());
        return cp;
    }
    
    /**
     * Factory Method to create a DatePanel customized to
     * show holidays. It takes care of all the initial setup.
     * @return a new DatePanel
     */    
    public static DatePanel createDatePanel() {
        DatePanel retorno = new DatePanel();
        configureDatePanel(retorno);
        return retorno;
    }
    
    /**
     * Factory Method to create a DatePanel customized to
     * show holidays. It takes care of all the initial setup.
     * @param showWeekNumbers true to show the week numbers
     * @return a new DatePanel
     */    
    public static DatePanel createDatePanel(boolean showWeekNumbers) {
        DatePanel retorno = new DatePanel(showWeekNumbers);
        configureDatePanel(retorno);
        return retorno;
    }
    
    private static void configureDatePanel(DatePanel dp) {
        dp.setRenderer(new DefaultDayRenderer());
        dp.setHeaderRenderer(new DefaultHeaderRenderer());
        dp.setModel(new HoliDayModel());
    }
}
