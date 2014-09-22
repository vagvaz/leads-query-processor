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

import com.l2fprod.common.swing.plaf.FontChooserUI;

import java.awt.GraphicsEnvironment;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.SortedMap;

/**
 * Default implementation of the FontChooserModel. It returns all
 * available fonts and commonly used font sizes.
 */
public class DefaultFontChooserModel implements FontChooserModel {

  private static final int[] DEFAULT_FONT_SIZES = {6, 8, 10, 11, 12, 14, 16,
      18, 20, 22, 24, 26, 28, 32, 40, 48, 56, 64, 72};

  private final String[] fontFamilies;
  private final String[] charSets;
  private final String previewMessage;
  
  public DefaultFontChooserModel() {
    fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getAvailableFontFamilyNames();
    Arrays.sort(fontFamilies);

    SortedMap map = Charset.availableCharsets();
    charSets = new String[map.size()];
    int i = 0;
    for (Iterator iter = map.keySet().iterator(); iter.hasNext(); i++) {
      charSets[i] = (String)iter.next();
    }
    
    ResourceBundle bundle =
      ResourceBundle.getBundle(FontChooserUI.class.getName() + "RB");
    previewMessage = bundle.getString("FontChooserUI.previewText");
  }

  public String[] getFontFamilies(String charSetName) {
    return fontFamilies;
  }

  public int[] getDefaultSizes() {
    return DEFAULT_FONT_SIZES;
  }

  public String[] getCharSets() {
    return charSets;
  }

  public String getPreviewMessage(String charSetName) {
    return previewMessage;
  }

}