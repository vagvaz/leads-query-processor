/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013



    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.



    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.



    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

 */

package com.apatar.rss.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdom.Attribute;
import org.jdom.Element;

import com.apatar.rss.RssElement;
import com.apatar.ui.JDefaultContextMenu;

public class JRssFieldPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -341924927240920563L;
	JTree tree;
	JList list;
	JButton bAdd = new JButton("Add Child");
	JButton bDelete = new JButton("Delete");

	DefaultTreeModel treeModel = new DefaultTreeModel(null);
	DefaultListModel listModel = new DefaultListModel();

	private TreeRssElement currentElement;

	public JRssFieldPanel() {
		super(new BorderLayout());
		tree = new JTree(treeModel);
		list = new JList(listModel);
		createPanel();

		bAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				addElementToTreeItem();
			}
		});

		bDelete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				TreePath treepath = tree.getSelectionPath();

				if (null == treepath) {
					return;
				}

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treepath
						.getLastPathComponent();
				if (node.getParent() == null) {
					return;
				}
				TreeRssElement elem = (TreeRssElement) node.getUserObject();
				treeModel.removeNodeFromParent(node);
				if (elem.original != null) {
					elem.original.setLock(false);
				} else {
					elem.setLock(false);
				}
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
				if (tree.getSelectionPath() != null) {
					DefaultMutableTreeNode node = ((DefaultMutableTreeNode) tree
							.getSelectionPath().getLastPathComponent());
					TreeRssElement selElem = (TreeRssElement) node
							.getUserObject();

					currentElement = selElem;

					updateListChildrens(selElem);
				}
			}

		});
	}

	private void updateListChildrens(TreeRssElement selElem) {
		listModel.clear();
		List<TreeRssElement> elems = selElem.getTreeChildrens();
		for (TreeRssElement elem : elems) {
			if (!elem.isLock()) {
				listModel.addElement(elem);
			}
		}
	}

	private void createPanel() {
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		sp.setDividerLocation(350);
		sp.setDividerSize(3);
		sp.setOneTouchExpandable(true);
		sp.setResizeWeight(1.0);

		list.setComponentPopupMenu(new JDefaultContextMenu(list));

		JPanel fieldDelPanel = new JPanel(new BorderLayout(5, 5));
		fieldDelPanel.add(tree, BorderLayout.CENTER);

		JPanel buttonDelPanel = new JPanel();
		BoxLayout buttonDelLayout = new BoxLayout(buttonDelPanel,
				BoxLayout.X_AXIS);
		buttonDelPanel.setLayout(buttonDelLayout);
		buttonDelPanel.add(Box.createHorizontalGlue());
		buttonDelPanel.add(bDelete);

		fieldDelPanel.add(buttonDelPanel, BorderLayout.SOUTH);

		JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
		fieldPanel.add(new JScrollPane(list), BorderLayout.CENTER);

		JPanel buttonAddPanel = new JPanel();
		BoxLayout buttonAddLayout = new BoxLayout(buttonAddPanel,
				BoxLayout.X_AXIS);
		buttonAddPanel.setLayout(buttonAddLayout);
		buttonAddPanel.add(Box.createHorizontalGlue());
		buttonAddPanel.add(bAdd);

		fieldPanel.add(buttonAddPanel, BorderLayout.SOUTH);

		sp.setLeftComponent(new JScrollPane(fieldDelPanel));
		sp.setRightComponent(fieldPanel);

		add(sp);
	}

	public void setRootRssElement(RssElement root) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
				new TreeRssElement(root));
		treeModel.setRoot(rootNode);
		tree.setSelectionPath(new TreePath(rootNode.getPath()));
	}

	public DefaultMutableTreeNode addNodeToTree(TreeRssElement elem) {
		TreePath selPath = tree.getSelectionPath();
		return addNodeToTree(elem, (DefaultMutableTreeNode) selPath
				.getLastPathComponent());
	}

	public DefaultMutableTreeNode addNodeToTree(TreeRssElement elem,
			DefaultMutableTreeNode rootnode) {
		TreeRssElement addingNode = new TreeRssElement(elem);
		if (!elem.isUnbounded()) {
			elem.setLock(true);
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(addingNode);
		rootnode.add(node);
		tree.expandPath(new TreePath(node.getPath()));
		return node;
	}

	private class TreeRssElement extends RssElement {
		TreeRssElement original = null;
		boolean lock = false;
		List<TreeRssElement> treeChildrens = new ArrayList<TreeRssElement>();

		public TreeRssElement(RssElement element) {
			super(element, true);
			for (RssElement childElem : element.getChildrens()) {
				TreeRssElement newElem = new TreeRssElement(childElem);
				treeChildrens.add(new TreeRssElement(newElem));
				newElem.setLock(false);
			}
			if (element instanceof TreeRssElement) {
				original = (TreeRssElement) element;
			}
		}

		public void cloneChild() {
			List<TreeRssElement> replase = new ArrayList<TreeRssElement>();
			for (TreeRssElement childElem : treeChildrens) {
				TreeRssElement newElem = new TreeRssElement(childElem);
				replase.add(new TreeRssElement(newElem));
				newElem.setLock(false);
			}
			treeChildrens = replase;
		}

		public boolean isLock() {
			return lock;
		}

		public void setLock(boolean lock) {
			this.lock = lock;
		}

		public List<TreeRssElement> getTreeChildrens() {
			return treeChildrens;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private void addElementToTreeItem() {
		Object[] objs = list.getSelectedValues();
		for (Object obj : objs) {
			TreeRssElement elem = (TreeRssElement) obj;

			addNodeToTree(elem);
			updateListChildrens(currentElement);
		}
		tree.updateUI();
	}

	public void createTreeItem(RssElement elem, RssElement itemRssElement) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel
				.getRoot();
		createTreeItem(elem, itemRssElement, rootNode);
		tree.updateUI();
	}

	private void createTreeItem(RssElement elem, RssElement itemRssElement,
			DefaultMutableTreeNode node) {
		for (RssElement childElem : itemRssElement.getChildrens()) {
			RssElement requiredRssElement = elem.getChild(childElem.getName());
			DefaultMutableTreeNode addedNode;
			if (!(requiredRssElement instanceof TreeRssElement)) {
				TreeRssElement treeRssElement = new TreeRssElement(
						requiredRssElement);
				addedNode = addNodeToTree(treeRssElement, node);
			} else {
				addedNode = addNodeToTree((TreeRssElement) requiredRssElement,
						node);
			}
			createTreeItem(requiredRssElement, childElem, addedNode);
		}
	}

	public void createTreeItem(RssElement elem, Element item) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel
				.getRoot();
		createTreeItem(item, null, rootNode);
		tree.updateUI();
	}

	private void createTreeItem(Element itemElement, RssElement elemParent,
			DefaultMutableTreeNode node) {

		List<?> attrs = itemElement.getAttributes();
		for (Object obj : attrs) {
			Attribute attr = (Attribute) obj;
			TreeRssElement addElement = new TreeRssElement(new RssElement(attr
					.getName(), true, elemParent));
			addNodeToTree(addElement, node);
		}

		for (Object obj : itemElement.getChildren()) {
			Element elem = (Element) obj;
			TreeRssElement treeRssElement = new TreeRssElement(new RssElement(
					elem.getName(), false, elemParent));
			DefaultMutableTreeNode addedNode = addNodeToTree(treeRssElement,
					node);
			createTreeItem(elem, treeRssElement, addedNode);
		}
	}

	public RssElement generateItem() {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel
				.getRoot();
		RssElement item = new RssElement((RssElement) rootNode.getUserObject(),
				false);
		generateItem(item, rootNode);
		return item;
	}

	private void generateItem(RssElement elem, DefaultMutableTreeNode node) {
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node
					.getChildAt(i);
			RssElement childElement = (RssElement) childNode.getUserObject();
			RssElement newChildElem = new RssElement(childElement, false);
			elem.addChild(newChildElem);
			generateItem(newChildElem, childNode);
		}
	}

}
