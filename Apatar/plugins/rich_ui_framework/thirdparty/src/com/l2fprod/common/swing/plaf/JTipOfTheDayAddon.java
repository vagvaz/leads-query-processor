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

import com.l2fprod.common.swing.JTipOfTheDay;
import com.l2fprod.common.swing.plaf.basic.BasicTipOfTheDayUI;
import com.l2fprod.common.swing.plaf.windows.WindowsTipOfTheDayUI;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;

/**
 * Addon for <code>JTipOfTheDay</code>.<br>
 */
@SuppressWarnings({"unchecked", "serial", "unused"})
public class JTipOfTheDayAddon extends AbstractComponentAddon {

  public JTipOfTheDayAddon() {
    super("JTipOfTheDay");
  }

  protected void addBasicDefaults(LookAndFeelAddons addon, List defaults) {
    defaults.add(JTipOfTheDay.uiClassID);
    defaults.add(BasicTipOfTheDayUI.class.getName());

    defaults.add("TipOfTheDay.font");
    defaults.add(UIManager.getFont("TextPane.font"));

    defaults.add("TipOfTheDay.tipFont");
    defaults.add(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 13f));

    defaults.add("TipOfTheDay.background");
    defaults.add(new ColorUIResource(Color.white));

    defaults.add("TipOfTheDay.icon");
    defaults.add(LookAndFeel.makeIcon(BasicTipOfTheDayUI.class,
      "TipOfTheDay24.gif"));

    defaults.add("TipOfTheDay.border");
    defaults.add(new BorderUIResource(BorderFactory.createLineBorder(new Color(
      117, 117, 117))));

    addResource(defaults,
      "com.l2fprod.common.swing.plaf.basic.resources.TipOfTheDay");
  }

  protected void addWindowsDefaults(LookAndFeelAddons addon, List defaults) {
    super.addWindowsDefaults(addon, defaults);

    defaults.add(JTipOfTheDay.uiClassID);
    defaults.add(WindowsTipOfTheDayUI.class.getName());

    defaults.add("TipOfTheDay.background");
    defaults.add(new ColorUIResource(128, 128, 128));

    defaults.add("TipOfTheDay.font");
    defaults.add(UIManager.getFont("Label.font").deriveFont(13f));

    defaults.add("TipOfTheDay.icon");
    defaults.add(LookAndFeel.makeIcon(WindowsTipOfTheDayUI.class,
      "tipoftheday.png"));

    defaults.add("TipOfTheDay.border");
    defaults
      .add(new BorderUIResource(new WindowsTipOfTheDayUI.TipAreaBorder()));

    addResource(defaults,
      "com.l2fprod.common.swing.plaf.windows.resources.TipOfTheDay");
  }

}
