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

import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.plaf.TaskPaneUI;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

/**
 * Base implementation of the <code>JTaskPane</code> UI.
 */
public class BasicTaskPaneUI extends TaskPaneUI {

  public static ComponentUI createUI(JComponent c) {
    return new BasicTaskPaneUI();
  }
  
  protected JTaskPane taskPane;
  protected boolean useGradient;
  protected Color gradientStart;
  protected Color gradientEnd;

  public void installUI(JComponent c) {
    super.installUI(c);
    taskPane = (JTaskPane)c;
    taskPane.setLayout(new PercentLayout(PercentLayout.VERTICAL, 14));
    taskPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
    taskPane.setOpaque(true);

    if (taskPane.getBackground() == null
      || taskPane.getBackground() instanceof ColorUIResource) {
      taskPane
        .setBackground(UIManager.getColor("TaskPane.background"));
    }
    
    useGradient = UIManager.getBoolean("TaskPane.useGradient");
    if (useGradient) {
      gradientStart = UIManager
      .getColor("TaskPane.backgroundGradientStart");
      gradientEnd = UIManager
      .getColor("TaskPane.backgroundGradientEnd");
    }
  }

  public void paint(Graphics g, JComponent c) {
    Graphics2D g2d = (Graphics2D)g;
    if (useGradient) {
      Paint old = g2d.getPaint();
      GradientPaint gradient = new GradientPaint(0, 0, gradientStart, 0, c
        .getHeight(), gradientEnd);
      g2d.setPaint(gradient);
      g.fillRect(0, 0, c.getWidth(), c.getHeight());      
      g2d.setPaint(old);
    }
  }

}
