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
package com.l2fprod.common.swing.plaf.blue;

import com.l2fprod.common.swing.plaf.ButtonBarButtonUI;
import com.l2fprod.common.swing.plaf.basic.BasicButtonBarUI;

import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

/**
 * BlueishButtonBarUI. <br>
 *  
 */
public class BlueishButtonBarUI extends BasicButtonBarUI {

  public static ComponentUI createUI(JComponent c) {
    return new BlueishButtonBarUI();
  }

  protected void installDefaults() {
    Border b = bar.getBorder();
    if (b == null || b instanceof UIResource) {
      bar.setBorder(
        new BorderUIResource(
          new CompoundBorder(
            BorderFactory.createLineBorder(
              UIManager.getColor("controlDkShadow")),
            BorderFactory.createEmptyBorder(1, 1, 1, 1))));
    }
    
    Color color = bar.getBackground();
    if (color == null || color instanceof ColorUIResource) {
      bar.setOpaque(true);
      bar.setBackground(new ColorUIResource(Color.white));
    }
  }

  public void installButtonBarUI(AbstractButton button) {
    button.setUI(new BlueishButtonBarButtonUI());
    button.setHorizontalTextPosition(JButton.CENTER);
    button.setVerticalTextPosition(JButton.BOTTOM);
    button.setOpaque(false);
  }

  static class BlueishButtonBarButtonUI
    extends BlueishButtonUI implements ButtonBarButtonUI {
  }

}
