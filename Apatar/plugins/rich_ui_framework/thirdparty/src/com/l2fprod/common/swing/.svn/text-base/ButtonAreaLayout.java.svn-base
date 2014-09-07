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
package com.l2fprod.common.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * ButtonAreaLayout. <br>
 *  
 */
public final class ButtonAreaLayout implements LayoutManager {

  private int gap;

  public ButtonAreaLayout(int gap) {
    this.gap = gap;
  }

  public void addLayoutComponent(String string, Component comp) {
  }

  public void layoutContainer(Container container) {
    Insets insets = container.getInsets();
    Component[] children = container.getComponents();

    // calculate the max width
    int maxWidth = 0;
    int maxHeight = 0;
    int visibleCount = 0;
    Dimension componentPreferredSize;

    for (int i = 0, c = children.length; i < c; i++) {
      if (children[i].isVisible()) {
        componentPreferredSize = children[i].getPreferredSize();
        maxWidth = Math.max(maxWidth, componentPreferredSize.width);
        maxHeight = Math.max(maxHeight, componentPreferredSize.height);
        visibleCount++;
      }
    }

    int usedWidth = maxWidth * visibleCount + gap * (visibleCount - 1);

    for (int i = 0, c = children.length; i < c; i++) {
      if (children[i].isVisible()) {
        children[i].setBounds(
          container.getWidth()
            - insets.right
            - usedWidth
            + (maxWidth + gap) * i,
          insets.top,
          maxWidth,
          maxHeight);
      }
    }
  }

  public Dimension minimumLayoutSize(Container c) {
    return preferredLayoutSize(c);
  }

  public Dimension preferredLayoutSize(Container container) {
    Insets insets = container.getInsets();
    Component[] children = container.getComponents();

    // calculate the max width
    int maxWidth = 0;
    int maxHeight = 0;
    int visibleCount = 0;
    Dimension componentPreferredSize;

    for (int i = 0, c = children.length; i < c; i++) {
      if (children[i].isVisible()) {
        componentPreferredSize = children[i].getPreferredSize();
        maxWidth = Math.max(maxWidth, componentPreferredSize.width);
        maxHeight = Math.max(maxHeight, componentPreferredSize.height);
        visibleCount++;
      }
    }

    int usedWidth = maxWidth * visibleCount + gap * (visibleCount - 1);

    return new Dimension(
      insets.left + usedWidth + insets.right,
      insets.top + maxHeight + insets.bottom);
  }

  public void removeLayoutComponent(Component c) {
  }
}
