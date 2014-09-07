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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

/**
 * BannerPanel. <br>
 *  
 */
@SuppressWarnings({"unchecked", "serial"})
public class BannerPanel extends JPanel {

  private JLabel titleLabel;

  private JTextComponent subtitleLabel;

  private JLabel iconLabel;

  public BannerPanel() {
    setBorder(
      new CompoundBorder(new EtchedBorder(), LookAndFeelTweaks.PANEL_BORDER));

    setOpaque(true);
    setBackground(UIManager.getColor("Table.background"));

    titleLabel = new JLabel();
    titleLabel.setOpaque(false);

    subtitleLabel = new JEditorPane("text/html", "<html>");
    subtitleLabel.setFont(titleLabel.getFont());

    LookAndFeelTweaks.makeBold(titleLabel);
    LookAndFeelTweaks.makeMultilineLabel(subtitleLabel);
    LookAndFeelTweaks.htmlize(subtitleLabel);

    iconLabel = new JLabel();
    iconLabel.setPreferredSize(new Dimension(50, 50));

    setLayout(new BorderLayout());

    JPanel nestedPane = new JPanel(new BorderLayout());
    nestedPane.setOpaque(false);
    nestedPane.add("North", titleLabel);
    nestedPane.add("Center", subtitleLabel);
    add("Center", nestedPane);
    add("East", iconLabel);
  }

  public void setTitleColor(Color color) {
    titleLabel.setForeground(color);
  }

  public Color getTitleColor() {
    return titleLabel.getForeground();
  }
  
  public void setSubtitleColor(Color color) {
    subtitleLabel.setForeground(color);
  }
  
  public Color getSubtitleColor() {
    return subtitleLabel.getForeground();
  }
  
  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  public String getTitle() {
    return titleLabel.getText();
  }
  
  public void setSubtitle(String subtitle) {
    subtitleLabel.setText(subtitle);
  }
  
  public String getSubtitle() {
    return subtitleLabel.getText();
  }
  
  public void setSubtitleVisible(boolean b) {
    subtitleLabel.setVisible(b);
  }
  
  public boolean isSubtitleVisible() {
    return subtitleLabel.isVisible();
  }
  
  public void setIcon(Icon icon) {
    iconLabel.setIcon(icon);
  }
  
  public Icon getIcon() {
    return iconLabel.getIcon();
  }
  
  public void setIconVisible(boolean b) {
    iconLabel.setVisible(b);
  }
  
  public boolean isIconVisible() {
    return iconLabel.isVisible();
  }
  
}
