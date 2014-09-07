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
package com.l2fprod.common.swing.plaf.windows;

import com.l2fprod.common.swing.border.ButtonBorder;
import com.l2fprod.common.swing.border.FourLineBorder;
import com.l2fprod.common.swing.plaf.basic.BasicOutlookBarUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Implements of OutlookBarUI with the Windows look and feel. <br>
 *  
 */
@SuppressWarnings({"unchecked", "serial", "unused", "deprecation"})
public class WindowsOutlookBarUI extends BasicOutlookBarUI {

  public static ComponentUI createUI(JComponent c) {
    return new WindowsOutlookBarUI();
  }
  
  private Border tabButtonBorder;

  public JScrollPane makeScrollPane(Component component) {
    JScrollPane scroll = super.makeScrollPane(component);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.getVerticalScrollBar().setUI(new ThinScrollBarUI());
    return scroll;
  }
  
  protected void installDefaults() {
    super.installDefaults();
    tabButtonBorder = UIManager.getBorder("OutlookBar.tabButtonBorder");
  }

  protected TabButton createTabButton() {
    TabButton button = new TabButton();
    button.setUI(new BasicButtonUI());
    button.setBorder(tabButtonBorder);
    return button;
  }
  
  public static class WindowsTabButtonBorder extends ButtonBorder {
    FourLineBorder normalBorder;
    FourLineBorder pressedBorder;
    public WindowsTabButtonBorder(Color color1, Color color2) {
      normalBorder = new FourLineBorder(color1, color1, color2, color2);
      pressedBorder = new FourLineBorder(color2, color2, color1, color1);      
    }
    protected void paintNormal(AbstractButton b, Graphics g, int x, int y,
      int width, int height) {
      normalBorder.paintBorder(b, g, x, y, width, height);
    }
    protected void paintDisabled(AbstractButton b, Graphics g, int x, int y,
      int width, int height) {
      normalBorder.paintBorder(b, g, x, y, width, height);
    }
    protected void paintRollover(AbstractButton b, Graphics g, int x, int y,
      int width, int height) {
      normalBorder.paintBorder(b, g, x, y, width, height);
    }
    protected void paintPressed(AbstractButton b, Graphics g, int x, int y,
      int width, int height) {
      pressedBorder.paintBorder(b, g, x, y, width, height);
    }
    public Insets getBorderInsets(Component c) {
      return normalBorder.getBorderInsets(c);
    }
  }
  
  public static class ThinScrollBarUI extends BasicScrollBarUI {
    public Dimension getPreferredSize(JComponent c) {
      return (scrollbar.getOrientation() == JScrollBar.VERTICAL)?new Dimension(8,
        48):new Dimension(48, 8);
    }
  }
  
}