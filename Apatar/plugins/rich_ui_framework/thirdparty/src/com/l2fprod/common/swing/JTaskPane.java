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

import com.l2fprod.common.swing.plaf.JTaskPaneAddon;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;
import com.l2fprod.common.swing.plaf.TaskPaneUI;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/**
 * <code>JTaskPane</code> provides an elegant view
 * to display a list of tasks ordered by groups ({@link JTaskPane}.
 * 
 * <p>
 * Although {@link JTaskPaneGroup} can be added to any other
 * container, the <code>JTaskPane</code> will provide better
 * fidelity when it comes to matching the look and feel of the host operating
 * system than any other panel. As example, when using on a Windows platform,
 * the <code>JTaskPane</code> will be painted with light gradient
 * background. Also <code>JTaskPane</code> takes care of using the
 * right {@link java.awt.LayoutManager} (as required by
 * {@link JCollapsiblePane}) so that
 * {@link JTaskPaneGroup} behaves correctly when collapsing and
 * expanding its content.
 *  
 * <p>
 * <code>JTaskPane<code> can be added to a JScrollPane.
 * 
 * <p>
 * Example:
 * <pre>
 * <code>
 * JFrame frame = new JFrame();
 * 
 * // a container to put all JTaskPaneGroup together
 * JTaskPane taskPaneContainer = new JTaskPane();
 * 
 * // add JTaskPaneGroups to the container
 * JTaskPaneGroup actionPane = createActionPane();
 * JTaskPaneGroup miscActionPane = createMiscActionPane();
 * JTaskPaneGroup detailsPane = createDetailsPane();
 * taskPaneContainer.add(actionPane);
 * taskPaneContainer.add(miscActionPane);
 * taskPaneContainer.add(detailsPane);
 *
 * // put the action list on the left in a JScrollPane
 * // as we have several taskPane and we want to make sure they
 * // all get visible.   
 * frame.add(new JScrollPane(taskPaneContainer), BorderLayout.EAST);
 * 
 * // and a file browser in the middle
 * frame.add(fileBrowser, BorderLayout.CENTER);
 * 
 * frame.pack().
 * frame.setVisible(true);
 * </code>
 * </pre>
 *
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 * 
 * @javabean.attribute
 *          name="isContainer"
 *          value="Boolean.TRUE"
 *          rtexpr="true"
 * 
 * @javabean.class
 *          name="JTaskPane"
 *          shortDescription="A component that contains JTaskPaneGroups."
 *          stopClass="java.awt.Component"
 * 
 * @javabean.icons
 *          mono16="JTaskPane16-mono.gif"
 *          color16="JTaskPane16.gif"
 *          mono32="JTaskPane32-mono.gif"
 *          color32="JTaskPane32.gif"
 */
@SuppressWarnings({"unchecked", "serial", "unused"})
public class JTaskPane extends JComponent implements Scrollable {

  public final static String UI_CLASS_ID = "TaskPaneUI";
  
  // ensure at least the default ui is registered
  static {
    LookAndFeelAddons.contribute(new JTaskPaneAddon());
  }

  /**
   * Creates a new empty taskpane.
   */
  public JTaskPane() {
    updateUI();
  }

  /**
   * Notification from the <code>UIManager</code> that the L&F has changed.
   * Replaces the current UI object with the latest version from the <code>UIManager</code>.
   * 
   * @see javax.swing.JComponent#updateUI
   */
  public void updateUI() {
    setUI((TaskPaneUI)LookAndFeelAddons.getUI(this, TaskPaneUI.class));
  }

  /**
   * Sets the L&F object that renders this component.
   * 
   * @param ui the <code>TaskPaneUI</code> L&F object
   * @see javax.swing.UIDefaults#getUI
   * 
   * @beaninfo bound: true hidden: true description: The UI object that
   * implements the taskpane's LookAndFeel.
   */
  public void setUI(TaskPaneUI ui) {
    super.setUI(ui);
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

  /**
   * Adds a new <code>JTaskPaneGroup</code> to this JTaskPane.
   * 
   * @param group
   */
  public void add(JTaskPaneGroup group) {
    super.add(group);
  }

  /**
   * Removes a new <code>JTaskPaneGroup</code> from this JTaskPane.
   * 
   * @param group
   */
  public void remove(JTaskPaneGroup group) {
    super.remove(group);
  }

  /**
   * @see Scrollable#getPreferredScrollableViewportSize()
   */
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  /**
   * @see Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
   */
  public int getScrollableBlockIncrement(
    Rectangle visibleRect,
    int orientation,
    int direction) {
    return 10;
  }
  
  /**
   * @see Scrollable#getScrollableTracksViewportHeight()
   */
  public boolean getScrollableTracksViewportHeight() {
    if (getParent() instanceof JViewport) {
      return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
    } else {
      return false;
    }
  }
  
  /**
   * @see Scrollable#getScrollableTracksViewportWidth()
   */
  public boolean getScrollableTracksViewportWidth() {
    return true;
  }
  
  /**
   * @see Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
   */
  public int getScrollableUnitIncrement(
    Rectangle visibleRect,
    int orientation,
    int direction) {
    return 10;
  }
  
}
