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

import com.l2fprod.common.swing.plaf.JLinkButtonAddon;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A button targeted to be used as an hyperlink. Most UI will make it
 * transparent and it will react on mouse over by changing the cursor to the
 * hand cursor.
 * 
 * @javabean.class
 *          name="JLinkButton"
 *          shortDescription="A button looking as an hyperlink."
 *          stopClass="java.awt.Component"
 * 
 * @javabean.attribute
 *          name="isContainer"
 *          value="Boolean.FALSE"
 *          rtexpr="true"
 * 
 * @javabean.icons
 *          mono16="JLinkButton16-mono.gif"
 *          color16="JLinkButton16.gif"
 *          mono32="JLinkButton32-mono.gif"
 *          color32="JLinkButton32.gif"
 */
@SuppressWarnings({"unchecked", "serial", "unused"})
public class JLinkButton extends JButton {

  public static final String UI_CLASS_ID = "LinkButtonUI";
  
  // ensure at least the default ui is registered
  static {
    LookAndFeelAddons.contribute(new JLinkButtonAddon());
  }

  public JLinkButton() {
    super();
  }

  public JLinkButton(String text) {
    super(text);
  }

  public JLinkButton(String text, Icon icon) {
    super(text, icon);
  }

  public JLinkButton(Action a) {
    super(a);
  }

  public JLinkButton(Icon icon) {
    super(icon);
  }

  /**
   * Returns the name of the L&F class that renders this component.
   * 
   * @return the string {@link #UI_CLASS_ID}
   * @see javax.swing.JComponent#getUIClassID
   * @see javax.swing.UIDefaults#getUI
   */
  public String getUIClassID() {
    return UI_CLASS_ID;
  }
  
}
