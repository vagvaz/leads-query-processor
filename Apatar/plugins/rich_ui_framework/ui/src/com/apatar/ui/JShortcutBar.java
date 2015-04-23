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

package com.apatar.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.apatar.core.ColumnNodeFactory;
import com.apatar.core.Record;
import com.apatar.core.TableInfo;
import com.l2fprod.common.swing.JOutlookBar;

public class JShortcutBar extends JOutlookBar implements DropTargetListener,
		DragGestureListener {
	private static final long serialVersionUID = 1L;

	private static final String NODE_FACTORY_PROP = "NodeFactory";

	int horizontalAlignment;
	String connectionName;
	String category;

	Comparator comparator = Collator.getInstance();

	private class ShortcutBarIcon extends ImageIcon {
		private static final long serialVersionUID = 1L;

		private Color bkgrColor;

		public ShortcutBarIcon(Image img) {
			super(img);
		}

		public ShortcutBarIcon(URL src) {
			super(src);
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (null != bkgrColor) {
				g.drawImage(getImage(), x, y, bkgrColor, getImageObserver());
			} else {
				g.drawImage(getImage(), x, y, getImageObserver());
			}
		}

		public void setBkgrColor(Color bkgrColor) {
			this.bkgrColor = bkgrColor;
		}
	}

	private Map<String, JSortedPanel> catTabs = new HashMap<String, JSortedPanel>();

	// for table info ColumnNode factory is created for every field
	// and it is referenced to table information
	public JShortcutBar(TableInfo ti, String connectionName, String category,
			boolean inbound, int horizontalAlignment) {
		super();
		this.connectionName = connectionName;
		this.category = category;
		this.horizontalAlignment = horizontalAlignment;
		setTabPlacement(SwingConstants.LEFT);
		setAllTabsAlignment(SwingConstants.LEFT);

		update(ti, inbound);
	}

	public JShortcutBar(TableInfo ti, String connectionName, String category,
			boolean inbound, int horizontalAlignment, String[] categorys) {
		this(ti, connectionName, category, inbound, horizontalAlignment);
		createCategoryTab(categorys);
	}

	private void update(TableInfo ti, boolean inbound) {
		if (ti == null) {
			return;
		}
		catTabs.clear();
		removeAll();
		for (Record rec : ti.getSchemaTable().getRecords()) {
			ColumnNodeFactory cNf = rec.createColumnNodeFactory(connectionName,
					category, inbound);
			for (Object obj : cNf.getCategory()) {
				JSortedPanel pnl = getCategoryTab(obj.toString());
				pnl.addComponent(createPageButton(cNf), true);
			}
		}

		updateUI();
	}

	public JShortcutBar(Collection<NodeFactory> nodes, int horizontalAlignment) {
		super();

		this.horizontalAlignment = horizontalAlignment;
		setTabPlacement(SwingConstants.LEFT);
		setAllTabsAlignment(SwingConstants.LEFT);
		// this.setIconAt(JOutlookBar.)

		if (nodes == null) {
			return;
		}

		for (NodeFactory nf : nodes) {

			for (Object obj : nf.getCategory()) {
				JSortedPanel pnl = getCategoryTab(obj.toString());
				pnl.addComponent(createPageButton(nf), true);
			}
		}
	}

	public JShortcutBar(Collection<NodeFactory> nodes, int horizontalAlignment,
			String[] categorys) {
		this(nodes, horizontalAlignment);
		createCategoryTab(categorys);
	}

	private JSortedPanel getCategoryTab(String cat) {

		JSortedPanel pnl = catTabs.get(cat);
		if (null == pnl) {
			pnl = new JSortedPanel();
			pnl.setLayout(new GridLayout(0, 1));
			pnl.setOpaque(false);

			insertTab(cat, null, makeScrollPane(pnl), null,
					findInsertionPoint(cat));
			catTabs.put(cat, pnl);
		}

		return pnl;
	}

	private void createCategoryTab(String[] cat) {
		for (String element : cat) {
			getCategoryTab(element);
		}
	}

	private static MouseListener labelMouseListener = new MouseAdapter() {

		@Override
		public void mouseEntered(MouseEvent me) {
			JLabel label = (JLabel) me.getComponent();
			ShortcutBarIcon icon = (ShortcutBarIcon) label.getIcon();
			icon.setBkgrColor(UiUtils.yelowColor);
			label.repaint();
		}

		@Override
		public void mouseExited(MouseEvent me) {
			JLabel label = (JLabel) me.getComponent();
			ShortcutBarIcon icon = (ShortcutBarIcon) label.getIcon();
			icon.setBkgrColor(null);
			label.repaint();
		}
	};

	private JColumnLabel createPageButton(NodeFactory nf) {
		JColumnLabel label = new JColumnLabel(nf.getTitle());
		label.putClientProperty(NODE_FACTORY_PROP, nf);
		label.setIcon(new ShortcutBarIcon(nf.getIcon().getImage()));

		label.setHorizontalTextPosition(nf.getHorizontalTextPosition());
		label.setVerticalTextPosition(nf.getVerticalTextPosition());
		label.setHorizontalAlignment(horizontalAlignment);
		label.setBorder(new EmptyBorder(5, 5, 0, 5));

		label.addMouseListener(labelMouseListener);

		label.setForeground(nf.getTextColor());

		int fontStile = nf.getFontStile();
		if (fontStile >= 0) {
			label.setFont(label.getFont().deriveFont(fontStile));
		}

		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(label,
				DnDConstants.ACTION_MOVE, this);

		new DropTarget(label, DnDConstants.ACTION_NONE, this);

		return label;
	}

	public void dragGestureRecognized(DragGestureEvent dge) {
		JLabel label = (JLabel) dge.getComponent();
		NodeFactory nf = (NodeFactory) label
				.getClientProperty(NODE_FACTORY_PROP);
		dge.startDrag(DragSource.DefaultMoveDrop, DndTransferData
				.createNodeData(nf));
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
		JLabel label = (JLabel) dte.getDropTargetContext().getComponent();
		((ShortcutBarIcon) label.getIcon()).setBkgrColor(null);
		label.repaint();
	}

	public void drop(DropTargetDropEvent dtde) {
	}

	int findInsertionPoint(String element) {
		// Copy the model data references to a Vector.
		int size = getTabCount();
		Vector<String> list = new Vector<String>();
		for (int x = 0; x < size; ++x) {
			String o = getTitleAt(x).toString();
			list.addElement(o);
		}

		// Find the new element's insertion point.
		int insertionPoint = Collections
				.binarySearch(list, element, comparator);
		if (insertionPoint < 0) {
			insertionPoint = -(insertionPoint + 1);
		}
		return insertionPoint;
	}

}
