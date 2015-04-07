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

import com.l2fprod.common.swing.plaf.JOutlookBarAddon;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;
import com.l2fprod.common.swing.plaf.OutlookBarUI;

import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.TabbedPaneUI;

/**
 * <code>JOutlookBar</code> brings the famous Outlook component to
 * Swing. The component shows stacks of components where only one
 * stack is visible at a time. <br>
 * 
 * The tab orientation of the {@link javax.swing.JTabbedPane}is
 * mapped to the JOutlookBar orientation as follow:
 * <ul>
 * <li>with JTabbedPane.TOP or JTabbedPane.BOTTOM, JOutlookBar will
 * layout the components horizontally
 * <li>with JTabbedPane.LEFT or JTabbedPane.RIGHT, JOutlookBar will
 * layout the components vertically (default)
 * </ul>
 * 
 * @javabean.class
 *          name="JOutlookBar"
 *          shortDescription="JOutlookBar brings the famous Outlook component to Swing"
 *          stopClass="javax.swing.JTabbedPane"
 * 
 * @javabean.icons
 *          mono16="JOutlookBar16-mono.gif"
 *          color16="JOutlookBar16.gif"
 *          mono32="JOutlookBar32-mono.gif"
 *          color32="JOutlookBar32.gif"
 */
@SuppressWarnings({"unchecked", "serial", "unused"})
public class JOutlookBar extends JTabbedPane {

  public static final String UI_CLASS_ID = "OutlookBarUI";
  
  static {
    LookAndFeelAddons.contribute(new JOutlookBarAddon());
  }

  /**
   * Used when generating PropertyChangeEvents for the "animated" property
   */
  public static final String ANIMATED_CHANGED_KEY = "animated";

  protected Map extendedPages;
  private boolean animated = true;
  
  /**
   *  
   */
  public JOutlookBar() {
    this(LEFT);
  }

  /**
   * @param tabPlacement
   */
  public JOutlookBar(int tabPlacement) {
    super(tabPlacement, WRAP_TAB_LAYOUT);
    extendedPages = new WeakHashMap();
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
    setUI((OutlookBarUI)LookAndFeelAddons.getUI(this, OutlookBarUI.class));
  }

  /**
   * Sets the L&F object that renders this component.
   * 
   * @param ui the <code>OutlookBarUI</code> L&F object
   * @see javax.swing.UIDefaults#getUI
   * 
   * @beaninfo bound: true hidden: true description: The UI object
   *           that implements the buttonbar's LookAndFeel.
   */
  public void setUI(OutlookBarUI ui) {
    super.setUI((TabbedPaneUI)ui);
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
   * Enables or disables animation during tab transition.
   * 
   * @param animated
   * @javabean.property
   *          bound="true"
   *          preferred="true"
   */
  public void setAnimated(boolean animated) {
    if (this.animated != animated) {
      this.animated = animated;
      firePropertyChange(ANIMATED_CHANGED_KEY, !animated, animated);
    }
  }
  
  /**
   * @return true if this taskpane is animated during expand/collapse
   *         transition.
   */
  public boolean isAnimated() {
    return animated;
  }

  /**
   * Builds a JScrollPane to hold the component. By default tabs are
   * not scrollable. They can be made scrollable by putting them in a
   * JScrollPane and adding the JScrollPane instead of the tab to the
   * JOutlookBar. It is recommended to use this method to create the
   * scrollbar as the UI may choose to return a JScrollPane specially
   * configured for the JOutlookBar component (ex. with different
   * scrollbars)
   * 
   * @param component
   * @return a JScrollPane with <code>component</code> as view
   */
  public JScrollPane makeScrollPane(Component component) {
    return ((OutlookBarUI)getUI()).makeScrollPane(component);
  }

  public void removeTabAt(int index) {
    checkIndex(index);
    removeExtendedPage(index);
    super.removeTabAt(index);
  }
  
  /**
   * Sets the title alignment for all tabs
   * 
   * @param alignment
   *          one of {@link javax.swing.SwingConstants#LEFT},
   *          {@link javax.swing.SwingConstants#CENTER},
   *          {@link javax.swing.SwingConstants#RIGHT}.
   */
  public void setAllTabsAlignment(int alignment) {
    for (Iterator iter = extendedPages.values().iterator(); iter.hasNext();) {
      ExtendedPage page = (ExtendedPage)iter.next();
      page.setTabAlignment(alignment);
    }
  }
  
  /**
   * Sets the title alignment of the tab at <code>index</code>
   * 
   * @param index
   * @param alignment
   *          one of {@link javax.swing.SwingConstants#LEFT},
   *          {@link javax.swing.SwingConstants#CENTER},
   *          {@link javax.swing.SwingConstants#RIGHT}.
   */
  public void setAlignmentAt(int index, int alignment) {
    getExtendedPage(index).setTabAlignment(alignment);
  }
  
  /**
   * @param index
   * @return the title alignment of the tab at <code>index</code>
   */
  public int getAlignmentAt(int index) {
    return getExtendedPage(index).getTabAlignment();   
  }

  /**
   * Overriden to notify the UI about the change
   */
  public void setTitleAt(int index, String title) {
    super.setTitleAt(index, title);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(index));
  }

  /**
   * Overriden to notify the UI about the change
   */
  public void setIconAt(int index, Icon icon) {
    super.setIconAt(index, icon);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(index));
  }

  /**
   * Overriden to notify the UI about the change
   */
  public void setBackgroundAt(int index, Color background) {
    super.setBackgroundAt(index, background);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(index));
  }

  /**
   * Overriden to notify the UI about the change
   */
  public void setForegroundAt(int index, Color foreground) {
    super.setForegroundAt(index, foreground);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(index));
  }

  /**
   * Overriden to notify the UI about the change
   */
  public void setToolTipTextAt(int index, String toolTipText) {
    super.setToolTipTextAt(index, toolTipText);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(index));
  }

  /**
   * Overriden to notify the UI about the change
   */
  public void setDisplayedMnemonicIndexAt(int tabIndex, int mnemonicIndex) {
    super.setDisplayedMnemonicIndexAt(tabIndex, mnemonicIndex);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(tabIndex));
  }

  /**
   * Overriden to notify the UI about the change
   */
  public void setMnemonicAt(int index, int mnemonic) {
    super.setMnemonicAt(index, mnemonic);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(index));
  }

  /**
   * Overriden to notify the UI about the change
   */
  public void setDisabledIconAt(int index, Icon disabledIcon) {
    super.setDisabledIconAt(index, disabledIcon);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(index));
  }
  
  /**
   * Overriden to notify the UI about the change
   */
  public void setEnabledAt(int index, boolean enabled) {
    super.setEnabledAt(index, enabled);
    firePropertyChange("tabPropertyChangedAtIndex", null, new Integer(index));
  }
    
  protected void addImpl(Component comp, Object constraints, int index) {
    if (index != -1) {
      super.addImpl(comp, constraints, index);
    } else {
      // insertTab always add component at the end of the components array
      // however the look and feel classes of JOutlookBar expect the component
      // to be in the right order when it calls getComponents().
      // We must make sure the component gets inserted in the right place
      int pageIndex = indexOfComponent(comp);
      if (pageIndex == -1) {
        super.addImpl(comp, constraints, index);
      } else {
        // this is one of our component, attempt to insert it in the right
        // position
        super.addImpl(comp, constraints, pageIndex * 2);
      }
    }
  }
  
  protected void removeExtendedPage(int index) {
    Component component = getComponentAt(index);
    extendedPages.remove(component);
  }
  
  protected ExtendedPage getExtendedPage(int index) {
    checkIndex(index);

    Component component = getComponentAt(index);
    ExtendedPage page = (ExtendedPage)extendedPages.get(component);
    if (page == null) {
      page = new ExtendedPage();
      page.component = component;
      extendedPages.put(component, page);
    }
    return page;
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= getTabCount()) {
      throw new IndexOutOfBoundsException(
      "Index: " + index + ", Tab count: " + getTabCount());
    }
  }

  private class ExtendedPage {
    Component component;
    int alignment = UIManager.getInt("OutlookBar.tabAlignment");

    public void setTabAlignment(int alignment) {
      if (this.alignment != alignment) {
        this.alignment = alignment;
        JOutlookBar.this.firePropertyChange("tabPropertyChangedAtIndex", null,
          new Integer(getIndex()));
      }
    }
    
    public int getIndex() {
      return indexOfComponent(component);
    }
    
    public int getTabAlignment() {
      return alignment;
    }
  }
  
}