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
package com.l2fprod.common.swing.plaf;

import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.plaf.windows.WindowsClassicLookAndFeelAddons;
import com.l2fprod.common.swing.plaf.windows.WindowsLookAndFeelAddons;
import com.l2fprod.common.util.OS;

import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Addon for <code>JTaskPane</code>. <br>
 *  
 */
@SuppressWarnings({"unchecked", "serial", "unused"})
public class JTaskPaneAddon extends AbstractComponentAddon {

  public JTaskPaneAddon() {
    super("JTaskPane");
  }
  
  protected void addBasicDefaults(LookAndFeelAddons addon, List defaults) {
    super.addBasicDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      JTaskPane.UI_CLASS_ID,
      "com.l2fprod.common.swing.plaf.basic.BasicTaskPaneUI",
      "TaskPane.useGradient",
      Boolean.FALSE,
      "TaskPane.background",
      UIManager.getColor("Desktop.background")
    }));
  }

  protected void addMetalDefaults(LookAndFeelAddons addon, List defaults) {
    super.addMetalDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      "TaskPane.background",
      MetalLookAndFeel.getDesktopColor()
    }));
  }
  
  protected void addWindowsDefaults(LookAndFeelAddons addon, List defaults) {
    super.addWindowsDefaults(addon, defaults);
    if (addon instanceof WindowsClassicLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        "TaskPane.background",
        UIManager.getColor("List.background")
      }));      
    } else if (addon instanceof WindowsLookAndFeelAddons) {     
      String xpStyle = OS.getWindowsVisualStyle();
      ColorUIResource background;
      ColorUIResource backgroundGradientStart;
      ColorUIResource backgroundGradientEnd;
      
      if (WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {        
        background = new ColorUIResource(201, 215, 170);
        backgroundGradientStart = new ColorUIResource(204, 217, 173);
        backgroundGradientEnd = new ColorUIResource(165, 189, 132);
      } else if (WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {
        background = new ColorUIResource(192, 195, 209);
        backgroundGradientStart = new ColorUIResource(196, 200, 212);
        backgroundGradientEnd = new ColorUIResource(177, 179, 200);
      } else {        
        background = new ColorUIResource(117, 150, 227);
        backgroundGradientStart = new ColorUIResource(123, 162, 231);
        backgroundGradientEnd = new ColorUIResource(99, 117, 214);
      }      
      defaults.addAll(Arrays.asList(new Object[]{
        "TaskPane.useGradient",
        Boolean.TRUE,
        "TaskPane.background",
        background,
        "TaskPane.backgroundGradientStart",
        backgroundGradientStart,
        "TaskPane.backgroundGradientEnd",
        backgroundGradientEnd,
      }));
    }
  }

  protected void addMacDefaults(LookAndFeelAddons addon, List defaults) {
    super.addMacDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      "TaskPane.background",
      new ColorUIResource(238, 238, 238),
    }));            
  }

}
