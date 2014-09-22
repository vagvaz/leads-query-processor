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
package com.l2fprod.common.swing.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * ColorCellRenderer. <br>
 *  
 */
@SuppressWarnings({"unchecked", "serial", "unused", "deprecation"})
public class ColorCellRenderer extends DefaultCellRenderer {

  public static String toHex(Color color) {
    String red = Integer.toHexString(color.getRed());
    String green = Integer.toHexString(color.getGreen());
    String blue = Integer.toHexString(color.getBlue());

    if (red.length() == 1) {
      red = "0" + red;
    }
    if (green.length() == 1) {
      green = "0" + green;
    }
    if (blue.length() == 1) {
      blue = "0" + blue;
    }
    return ("#" + red + green + blue).toUpperCase();
  }

  protected String convertToString(Object value) {
    if (value == null) {
      return null;
    }

    Color color = (Color)value;
    return "R:"
      + color.getRed()
      + " G:"
      + color.getGreen()
      + " B:"
      + color.getBlue()
      + " - "
      + toHex(color);
  }

  protected Icon convertToIcon(Object value) {
    if (value == null) {
      return null;
    }

    return new ColorIcon((Color)value);
  }

  private static class ColorIcon implements Icon {
    private Color color;
    public ColorIcon(Color color) {
      this.color = color;
    }
    public int getIconHeight() {
      return 10;
    }
    public int getIconWidth() {
      return 20;
    }
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Color oldColor = g.getColor();

      if (color != null) {
        g.setColor(color);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
      }

      g.setColor(UIManager.getColor("controlDkShadow"));
      g.drawRect(x, y, getIconWidth(), getIconHeight());

      g.setColor(oldColor);
    }
  }
}
