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

import com.l2fprod.common.swing.plaf.DirectoryChooserUI;
import com.l2fprod.common.swing.plaf.windows.WindowsDirectoryChooserUI;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;

/**
 * An extension of the JFileChooser but dedicated to directory selection. <br>
 *  
 * @javabean.class
 *          name="JDirectoryChooser"
 *          shortDescription="JDirectoryChooser allows to select one or more directories."
 *          stopClass="javax.swing.JFileChooser"
 * 
 * @javabean.icons
 *          mono16="JDirectoryChooser16-mono.gif"
 *          color16="JDirectoryChooser16.gif"
 *          mono32="JDirectoryChooser32-mono.gif"
 *          color32="JDirectoryChooser32.gif"
 */
@SuppressWarnings({"unchecked", "serial", "unused"})
public class JDirectoryChooser extends JFileChooser {

  /**
   * Creates a JDirectoryChooser pointing to the user's home directory.
   */
  public JDirectoryChooser() {
    super();
  }

  /**
   * Creates a JDirectoryChooser using the given File as the path.
   * 
   * @param currentDirectory
   */
  public JDirectoryChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Creates a JDirectoryChooser using the given current directory and
   * FileSystemView
   * 
   * @param currentDirectory
   * @param fsv
   */
  public JDirectoryChooser(File currentDirectory, FileSystemView fsv) {
    super(currentDirectory, fsv);
  }

  /**
   * Creates a JDirectoryChooser using the given FileSystemView
   * 
   * @param fsv
   */
  public JDirectoryChooser(FileSystemView fsv) {
    super(fsv);
  }

  /**
   * Creates a JDirectoryChooser using the given path.
   * 
   * @param currentDirectoryPath
   */
  public JDirectoryChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
  }

  public JDirectoryChooser(String currentDirectoryPath, FileSystemView fsv) {
    super(currentDirectoryPath, fsv);
  }

  /**
   * Sets the L&F object that renders this component.
   * 
   * @param ui the <code>ButtonBarUI</code> L&F object
   * @see javax.swing.UIDefaults#getUI
   * 
   * @beaninfo bound: true hidden: true description: The UI object that
   * implements the buttonbar's LookAndFeel.
   */
  public void setUI(ComponentUI ui) {
    if (!(ui instanceof DirectoryChooserUI && ui instanceof FileChooserUI)) {
      setUI(new WindowsDirectoryChooserUI(this));
    } else {
      super.setUI(ui);
    }
  }

  protected JDialog createDialog(Component parent) throws HeadlessException {
    JDialog dialog = super.createDialog(parent);
    ((JComponent)dialog.getContentPane()).setBorder(
      LookAndFeelTweaks.WINDOW_BORDER);
    return dialog;
  }
  
}
