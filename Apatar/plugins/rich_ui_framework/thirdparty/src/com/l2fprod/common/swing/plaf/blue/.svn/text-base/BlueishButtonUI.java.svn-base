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

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * BlueishButtonUI. <br>
 *  
 */
public class BlueishButtonUI
  extends BasicButtonUI {

  private static Color blueishBackgroundOver = new Color(224, 232, 246);
  private static Color blueishBorderOver = new Color(152, 180, 226);

  private static Color blueishBackgroundSelected = new Color(193, 210, 238);
  private static Color blueishBorderSelected = new Color(49, 106, 197);

  public BlueishButtonUI() {
    super();
  }

  public void installUI(JComponent c) {
    super.installUI(c);

    AbstractButton button = (AbstractButton)c;
    button.setRolloverEnabled(true);
    button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
  }

  public void paint(Graphics g, JComponent c) {
    AbstractButton button = (AbstractButton)c;
    if (button.getModel().isRollover()
      || button.getModel().isArmed()
      || button.getModel().isSelected()) {
      Color oldColor = g.getColor();
      if (button.getModel().isSelected()) {
        g.setColor(blueishBackgroundSelected);
      } else {
        g.setColor(blueishBackgroundOver);
      }
      g.fillRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);

      if (button.getModel().isSelected()) {
        g.setColor(blueishBorderSelected);
      } else {
        g.setColor(blueishBorderOver);
      }
      g.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);

      g.setColor(oldColor);
    }

    super.paint(g, c);
  }

}
