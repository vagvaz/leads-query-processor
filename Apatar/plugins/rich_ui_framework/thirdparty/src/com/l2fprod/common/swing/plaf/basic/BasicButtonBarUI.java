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
package com.l2fprod.common.swing.plaf.basic;

import com.l2fprod.common.swing.JButtonBar;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.plaf.ButtonBarUI;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * BasicButtonBarUI. <br>
 *  
 */
public class BasicButtonBarUI extends ButtonBarUI {

  protected JButtonBar bar;
  protected PropertyChangeListener propertyListener;

  public static ComponentUI createUI(JComponent c) {
    return new BasicButtonBarUI();
  }
  
  public void installUI(JComponent c) {
    super.installUI(c);

    bar = (JButtonBar)c;

    installDefaults();
    installListeners();

    updateLayout();
  }

  public void uninstallUI(JComponent c) {
    uninstallDefaults();
    uninstallListeners();
    super.uninstallUI(c);
  }

  protected void installDefaults() {
  }

  protected void uninstallDefaults() {
  }

  protected void installListeners() {
    propertyListener = createPropertyChangeListener();
    bar.addPropertyChangeListener(propertyListener);
  }

  protected void uninstallListeners() {
    bar.removePropertyChangeListener(propertyListener);
  }

  protected PropertyChangeListener createPropertyChangeListener() {
    return new ChangeListener();
  }

  protected void updateLayout() {
    if (bar.getOrientation() == JButtonBar.HORIZONTAL) {
      bar.setLayout(new PercentLayout(PercentLayout.HORIZONTAL, 2));
    } else {
      bar.setLayout(new PercentLayout(PercentLayout.VERTICAL, 2));
    }
  }

  public Dimension getPreferredSize(JComponent c) {
    JButtonBar b = (JButtonBar)c;
    Dimension preferred = b.getLayout().preferredLayoutSize(c);
    if (b.getOrientation() == JButtonBar.HORIZONTAL) {
      return new Dimension(preferred.width, 53);
    } else {
      return new Dimension(74, preferred.height);
    }
  }

  
  private class ChangeListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals(JButtonBar.ORIENTATION_CHANGED_KEY)) {
        updateLayout();
        bar.revalidate();
        bar.repaint();
      }
    }
  }

}
