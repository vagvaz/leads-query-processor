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
package com.l2fprod.common.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

/**
 * FourLineBorder. <br>
 * 
 */
public class FourLineBorder implements Border {

  private Color top;
  private Color left;
  private Color bottom;
  private Color right;

  public FourLineBorder(Color top, Color left, Color bottom, Color right) {
    this.top = top;
    this.left = left;
    this.bottom = bottom;
    this.right = right;
  }

  public Insets getBorderInsets(Component c) {
    return new Insets(top == null?0:1, left == null?0:1, bottom == null?0:1,
      right == null?0:1);
  }

  public boolean isBorderOpaque() {
    return true;
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int width,
    int height) {
    if (bottom != null) {
      g.setColor(bottom);
      g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
    }

    if (right != null) {
      g.setColor(right);
      g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
    }

    if (top != null) {
      g.setColor(top);
      g.drawLine(x, y, x + width - 1, y);
    }

    if (left != null) {
      g.setColor(left);
      g.drawLine(x, y, x, y + height - 1);
    }
  }

}