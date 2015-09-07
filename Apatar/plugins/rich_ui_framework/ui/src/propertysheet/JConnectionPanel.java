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

package propertysheet;

import com.apatar.core.ApplicationData;
import com.l2fprod.common.model.DefaultBeanInfoResolver;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.LookAndFeelTweaks;

import javax.swing.*;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JConnectionPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    Object bean;
    PropertySheetPanel sheet;
    DefaultBeanInfoResolver resolver;

    public JConnectionPanel() {
        super();
        setLayout(LookAndFeelTweaks.createVerticalPercentLayout());

        resolver = new DefaultBeanInfoResolver();

        sheet = new PropertySheetPanel();
        sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
        sheet.setDescriptionVisible(true);
        sheet.setSortingCategories(true);
        sheet.setSortingProperties(false);
        add(sheet, "*");

        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Property prop = (Property) evt.getSource();
                prop.writeToObject(bean);
            }
        };
        sheet.addPropertySheetChangeListener(listener);
    }

    public void updateBean(Object bean) {
        this.bean = bean;
        BeanInfo beanInfo =
            (BeanInfo) ApplicationData.CreateBinInfoObject(bean.getClass().getName() + "BeanInfo", bean.getClass());
        sheet.setProperties(beanInfo.getPropertyDescriptors());
        sheet.readFromObject(bean);
    }

}
