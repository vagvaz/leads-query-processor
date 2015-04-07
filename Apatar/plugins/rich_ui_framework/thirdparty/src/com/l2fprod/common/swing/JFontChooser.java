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
import com.l2fprod.common.swing.plaf.JFontChooserAddon;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * <code>JFontChooser</code> provides a pane of controls designed to allow a
 * user to manipulate and select a font.
 * 
 * @javabean.class
 *          name="JFontChooser"
 *          shortDescription="A component that supports selecting a Font."
 *          stopClass="javax.swing.JComponent"
 * 
 * @javabean.attribute
 *          name="isContainer"
 *          value="Boolean.FALSE"
 *          rtexpr="true"
 * 
 * @javabean.icons
 *          mono16="JFontChooser16-mono.gif"
 *          color16="JFontChooser16.gif"
 *          mono32="JFontChooser32-mono.gif"
 *          color32="JFontChooser32.gif"
 */
@SuppressWarnings({"unchecked", "serial", "unused"})
public class JFontChooser extends JComponent {

  // ensure at least the default ui is registered
  static {
    LookAndFeelAddons.contribute(new JFontChooserAddon());
  }

  public static final String SELECTED_FONT_CHANGED_KEY = "selectedFont";

  protected Font selectedFont;

  private FontChooserModel model;

  /**
   * Creates a font chooser with an initial default font and a default
   * model.
   */
  public JFontChooser() {
    this(new DefaultFontChooserModel());
  }

  /**
   * Creates a font chooser with an initial default font and a custom
   * model.
   * 
   * @param model
   */
  public JFontChooser(FontChooserModel model) {
    super();
    this.model = model;
    selectedFont = getFont();
    updateUI();
  }

  /**
   * Notification from the <code>UIManager</code> that the L&F has
   * changed. Replaces the current UI object with the latest version
   * from the <code>UIManager</code>.
   * 
   * @see javax.swing.JComponent#updateUI
   */
  public void updateUI() {
    setUI((FontChooserUI)LookAndFeelAddons.getUI(this, FontChooserUI.class));
  }

  /**
   * Sets the L&F object that renders this component.
   * 
   * @param ui the <code>FontChooserUI</code> L&F object
   * @see javax.swing.UIDefaults#getUI
   * 
   * @beaninfo
   *  bound: true
   *  hidden: true
   *  description: The UI object that implements the font chooser's LookAndFeel.
   */
  public void setUI(FontChooserUI ui) {
    super.setUI(ui);
  }

  /**
   * Returns the name of the L&F class that renders this component.
   * 
   * @return the string "FontChooserUI"
   * @see javax.swing.JComponent#getUIClassID
   * @see javax.swing.UIDefaults#getUI
   */
  public String getUIClassID() {
    return "FontChooserUI";
  }

  /**
   * Sets the selected font of this JFontChooser. This will fire a <code>PropertyChangeEvent</code>
   * for the property named {@link #SELECTED_FONT_CHANGED_KEY}.
   * 
   * @param f the font to select
   * @see javax.swing.JComponent#addPropertyChangeListener(java.beans.PropertyChangeListener)
   * 
   * @javabean.property
   *  bound="true"
   *  preferred="true"
   *  shortDescription="The current font the chooser is to display"
   */
  public void setSelectedFont(Font f) {
    Font oldFont = selectedFont;
    selectedFont = f;
    firePropertyChange(SELECTED_FONT_CHANGED_KEY, oldFont, selectedFont);
  }

  /**
   * Gets the current font value from the font chooser.
   * 
   * @return the current font value of the font chooser
   */
  public Font getSelectedFont() {
    return selectedFont;
  }

  /**
   * Gets the font chooser model of this font chooser.
   * 
   * @return the font chooser model of this font chooser.
   */
  public FontChooserModel getModel() {
    return model;
  }
  
  /**
   * Shows a modal font-chooser dialog and blocks until the dialog is
   * hidden. If the user presses the "OK" button, then this method
   * hides/disposes the dialog and returns the selected color. If the
   * user presses the "Cancel" button or closes the dialog without
   * pressing "OK", then this method hides/disposes the dialog and
   * returns <code>null</code>.
   * 
   * @param parent the parent <code>Component</code> for the
   *          dialog
   * @param title the String containing the dialog's title
   * @param initialFont the initial Font set when the font-chooser is
   *          shown
   * @return the selected font or <code>null</code> if the user
   *         opted out
   * @exception java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
   *              returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  public static Font showDialog(Component parent, String title, Font initialFont) {
    BaseDialog dialog;
    Window window = (parent == null?JOptionPane.getRootFrame():SwingUtilities
      .windowForComponent(parent));
    if (window instanceof Frame) {
      dialog = new BaseDialog((Frame)window, title, true);
    } else {
      dialog = new BaseDialog((Dialog)window, title, true);
    }
    dialog.setDialogMode(BaseDialog.OK_CANCEL_DIALOG);
    dialog.getBanner().setVisible(false);

    JFontChooser chooser = new JFontChooser();
    chooser.setSelectedFont(initialFont);

    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add("Center", chooser);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);

    if (dialog.ask()) {
      return chooser.getSelectedFont();
    } else {
      return null;
    }
  }

}