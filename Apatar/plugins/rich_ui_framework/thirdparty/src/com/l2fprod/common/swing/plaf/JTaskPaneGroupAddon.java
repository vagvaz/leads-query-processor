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

import com.l2fprod.common.swing.JTaskPaneGroup;
import com.l2fprod.common.swing.plaf.windows.WindowsClassicLookAndFeelAddons;
import com.l2fprod.common.swing.plaf.windows.WindowsLookAndFeelAddons;
import com.l2fprod.common.util.JVM;
import com.l2fprod.common.util.OS;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Addon for <code>JTaskPaneGroup</code>.<br>
 *
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
@SuppressWarnings({"unchecked", "serial", "unused"})
public class JTaskPaneGroupAddon extends AbstractComponentAddon {

  public JTaskPaneGroupAddon() {
    super("JTaskPaneGroup");
  }

  protected void addBasicDefaults(LookAndFeelAddons addon, List defaults) {
    Color menuBackground = new ColorUIResource(SystemColor.menu);
    defaults.addAll(Arrays.asList(new Object[]{
      JTaskPaneGroup.UI_CLASS_ID,
      "com.l2fprod.common.swing.plaf.basic.BasicTaskPaneGroupUI",
      "TaskPaneGroup.font",
      new FontUIResource(
        UIManager.getFont("Label.font").deriveFont(Font.BOLD)),        
      "TaskPaneGroup.background",
      UIManager.getColor("List.background"),
      "TaskPaneGroup.specialTitleBackground",
      new ColorUIResource(menuBackground.darker()),
      "TaskPaneGroup.titleBackgroundGradientStart",
      menuBackground,
      "TaskPaneGroup.titleBackgroundGradientEnd",
      menuBackground,
      "TaskPaneGroup.titleForeground",
      new ColorUIResource(SystemColor.menuText),
      "TaskPaneGroup.specialTitleForeground",
      new ColorUIResource(SystemColor.menuText).brighter(),
      "TaskPaneGroup.animate",
      Boolean.TRUE,
      "TaskPaneGroup.focusInputMap",
      new UIDefaults.LazyInputMap(
        new Object[] {
          "ENTER",
          "toggleExpanded",
          "SPACE",
          "toggleExpanded" }),
    }));
  }

  protected void addMetalDefaults(LookAndFeelAddons addon, List defaults) {
    super.addMetalDefaults(addon, defaults);
    // if using Ocean, use the Glossy l&f
    String taskPaneGroupUI = "com.l2fprod.common.swing.plaf.metal.MetalTaskPaneGroupUI";
    if (JVM.current().isOrLater(JVM.JDK1_5)) {
      try {
        Method method = MetalLookAndFeel.class.getMethod("getCurrentTheme", (Class[])null);
        Object currentTheme = method.invoke(null, (Object[])null);
        if (Class.forName("javax.swing.plaf.metal.OceanTheme").isInstance(
          currentTheme)) {
          taskPaneGroupUI = "com.l2fprod.common.swing.plaf.misc.GlossyTaskPaneGroupUI";
        }
      } catch (Exception e) {
      }
    }
    defaults.addAll(Arrays.asList(new Object[]{
      JTaskPaneGroup.UI_CLASS_ID,
      taskPaneGroupUI,
      "TaskPaneGroup.foreground",
      UIManager.getColor("activeCaptionText"),
      "TaskPaneGroup.background",
      MetalLookAndFeel.getControl(),
      "TaskPaneGroup.specialTitleBackground",
      MetalLookAndFeel.getPrimaryControl(),
      "TaskPaneGroup.titleBackgroundGradientStart",
      MetalLookAndFeel.getPrimaryControl(),
      "TaskPaneGroup.titleBackgroundGradientEnd",
      MetalLookAndFeel.getPrimaryControlHighlight(),
      "TaskPaneGroup.titleForeground",
      MetalLookAndFeel.getControlTextColor(),        
      "TaskPaneGroup.specialTitleForeground",
      MetalLookAndFeel.getControlTextColor(),     
      "TaskPaneGroup.borderColor",
      MetalLookAndFeel.getPrimaryControl(),
      "TaskPaneGroup.titleOver",
      MetalLookAndFeel.getControl().darker(),
      "TaskPaneGroup.specialTitleOver",
      MetalLookAndFeel.getPrimaryControlHighlight()        
    }));      
  }

  protected void addWindowsDefaults(LookAndFeelAddons addon,
    List defaults) {
    super.addWindowsDefaults(addon, defaults);
    
    if (addon instanceof WindowsLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        JTaskPaneGroup.UI_CLASS_ID,
        "com.l2fprod.common.swing.plaf.windows.WindowsTaskPaneGroupUI"}));

      String xpStyle = OS.getWindowsVisualStyle();
      if (WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {        
        defaults.addAll(Arrays.asList(new Object[]{
          "TaskPaneGroup.foreground",
          new ColorUIResource(86, 102, 45),
          "TaskPaneGroup.background",
          new ColorUIResource(246, 246, 236),
          "TaskPaneGroup.specialTitleBackground",
          new ColorUIResource(224, 231, 184),
          "TaskPaneGroup.titleBackgroundGradientStart",
          new ColorUIResource(255, 255, 255),
          "TaskPaneGroup.titleBackgroundGradientEnd",
          new ColorUIResource(224, 231, 184),
          "TaskPaneGroup.titleForeground",
          new ColorUIResource(86, 102, 45),
          "TaskPaneGroup.titleOver",
          new ColorUIResource(114, 146, 29),
          "TaskPaneGroup.specialTitleForeground",
          new ColorUIResource(86, 102, 45),
          "TaskPaneGroup.specialTitleOver",
          new ColorUIResource(114, 146, 29),
          "TaskPaneGroup.borderColor",
          new ColorUIResource(255, 255, 255),
        }));
      } else if (WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {
        defaults.addAll(Arrays.asList(new Object[]{
          "TaskPaneGroup.foreground",
          new ColorUIResource(Color.black),
          "TaskPaneGroup.background",
          new ColorUIResource(240, 241, 245),
          "TaskPaneGroup.specialTitleBackground",
          new ColorUIResource(222, 222, 222),
          "TaskPaneGroup.titleBackgroundGradientStart",
          new ColorUIResource(Color.white),
          "TaskPaneGroup.titleBackgroundGradientEnd",
          new ColorUIResource(214, 215, 224),
          "TaskPaneGroup.titleForeground",
          new ColorUIResource(Color.black),
          "TaskPaneGroup.titleOver",
          new ColorUIResource(126, 124, 124),
          "TaskPaneGroup.specialTitleForeground",
          new ColorUIResource(Color.black),
          "TaskPaneGroup.specialTitleOver",
          new ColorUIResource(126, 124, 124),
          "TaskPaneGroup.borderColor",
          new ColorUIResource(Color.white),
        }));
      } else {        
        defaults.addAll(Arrays.asList(new Object[]{
          "TaskPaneGroup.foreground",
          new ColorUIResource(Color.white),
          "TaskPaneGroup.background",
          new ColorUIResource(214, 223, 247),
          "TaskPaneGroup.specialTitleBackground",
          new ColorUIResource(33, 89, 201),
          "TaskPaneGroup.titleBackgroundGradientStart",
          new ColorUIResource(Color.white),
          "TaskPaneGroup.titleBackgroundGradientEnd",
          new ColorUIResource(199, 212, 247),
          "TaskPaneGroup.titleForeground",
          new ColorUIResource(33, 89, 201),
          "TaskPaneGroup.specialTitleForeground",
          new ColorUIResource(Color.white),
          "TaskPaneGroup.borderColor",
          new ColorUIResource(Color.white),
        }));
      }
    }
    
    if (addon instanceof WindowsClassicLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        JTaskPaneGroup.UI_CLASS_ID,
        "com.l2fprod.common.swing.plaf.windows.WindowsClassicTaskPaneGroupUI",
        "TaskPaneGroup.foreground",
        new ColorUIResource(Color.black),
        "TaskPaneGroup.background",
        new ColorUIResource(Color.white),
        "TaskPaneGroup.specialTitleBackground",
        new ColorUIResource(10, 36, 106),
        "TaskPaneGroup.titleBackgroundGradientStart",
        new ColorUIResource(212, 208, 200),
        "TaskPaneGroup.titleBackgroundGradientEnd",
        new ColorUIResource(212, 208, 200),
        "TaskPaneGroup.titleForeground",
        new ColorUIResource(Color.black),
        "TaskPaneGroup.specialTitleForeground",
        new ColorUIResource(Color.white),
        "TaskPaneGroup.borderColor",
        new ColorUIResource(212, 208, 200),
      }));
    }
  }
  
  protected void addMacDefaults(LookAndFeelAddons addon, List defaults) {
    super.addMacDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      JTaskPaneGroup.UI_CLASS_ID,
      "com.l2fprod.common.swing.plaf.misc.GlossyTaskPaneGroupUI",
      "TaskPaneGroup.background",
      new ColorUIResource(245, 245, 245),
      "TaskPaneGroup.titleForeground",
      new ColorUIResource(Color.black),
      "TaskPaneGroup.specialTitleBackground",
      new ColorUIResource(188,188,188),
      "TaskPaneGroup.specialTitleForeground",
      new ColorUIResource(Color.black),
      "TaskPaneGroup.titleBackgroundGradientStart",
      new ColorUIResource(250,250,250),
      "TaskPaneGroup.titleBackgroundGradientEnd",
      new ColorUIResource(188,188,188),
      "TaskPaneGroup.borderColor",
      new ColorUIResource(97, 97, 97),
      "TaskPaneGroup.titleOver",
      new ColorUIResource(125, 125, 97),
      "TaskPaneGroup.specialTitleOver",
      new ColorUIResource(125, 125, 97),
    }));
  }
  
}
