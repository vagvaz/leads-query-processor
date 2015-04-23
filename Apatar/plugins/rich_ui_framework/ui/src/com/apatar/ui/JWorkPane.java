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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import com.apatar.core.AbstractNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.ColumnNode;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;
import com.apatar.core.OperationalNode;
import com.apatar.core.Project;
import com.apatar.core.Runnable;
import com.apatar.ui.JNodePanel.JConnectionPointLabel;

public class JWorkPane extends JPanel implements MouseListener,
		MouseMotionListener, FocusListener, DropTargetListener,
		DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 1L;

	protected Point mousePressStart = new Point(0, 0);
	protected List<Component> selectedObjArr;

	private Rectangle mouseSelect;
	private final JPanel clip;

	private final Project project;

	int maxWidth = 0;
	int maxHeight = 0;

	ContextMenu contextMenu = new ContextMenu();
	NodeContextMenu nodeContextMenu = new NodeContextMenu();

	public JWorkPane(Project project) {
		super(null);

		this.project = project;

		setLayout(null);
		setAutoscrolls(true);

		setFocusable(true);
		addMouseListener(this);
		addFocusListener(this);

		// process delete components functionality
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
				"delete");
		getActionMap().put("delete", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				deleteSelectableComponent();
			}
		});

		addMouseMotionListener(this);
		new DropTarget(this, DnDConstants.ACTION_MOVE, this);

		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
				this, DnDConstants.ACTION_MOVE, this);

		selectedObjArr = new ArrayList<Component>();

		// this panel for select elements
		clip = new JPanel();
		clip.setOpaque(false);
		clip.setBorder(new LineBorder(Color.BLACK));

		resume();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Rectangle clipRect = g.getClipBounds();

		// g.setColor(UiUtils.blackColor);
		int left = (clipRect.x / 8) * 8;
		int top = (clipRect.y / 8) * 8;

		for (int x = left; x <= clipRect.x + clipRect.width; x += 8) {
			for (int y = top; y <= clipRect.y + clipRect.height; y += 8) {
				g.drawLine(x, y, x, y);
			}
		}
	}

	private void unSelectAllItems() {

		for (int i = 0; i < getComponentCount(); i++) {
			Component comp = getComponent(i);

			if (comp instanceof Selectable) {
				((Selectable) comp).setSelected(false);
			}
		}
	}

	private Component getClickableElement(Point workPaneMousePoint) {
		for (int i = 0; i < getComponentCount(); i++) {
			Component comp = getComponent(i);

			// if pressed on component and component implements Selectable
			// interface
			Rectangle compRec = new Rectangle(comp.getBounds().x, comp
					.getBounds().y, comp.getBounds().width,
					comp.getBounds().height);

			if (compRec.contains(workPaneMousePoint)
					&& comp instanceof Selectable) {

				if (((Selectable) comp).isMouseClickedOnComponent(
						(workPaneMousePoint.x - compRec.x),
						(workPaneMousePoint.y - compRec.y))) {
					return comp;
				}
			}
		} // for

		return null;
	}

	protected JNodePanel getNodePanel(AbstractNode node) {

		for (int i = 0; i < getComponentCount(); i++) {
			Component comp = getComponent(i);
			if ((comp instanceof JNodePanel)
					&& (((JNodePanel) comp).getNode() == node)) {

				return (JNodePanel) comp;
			}
		}

		return null;
	}

	protected JConnectorArrow getConnectorArrow(Connector conn) {

		for (int i = 0; i < getComponentCount(); i++) {
			Component comp = getComponent(i);
			if ((comp instanceof JConnectorArrow)
					&& (((JConnectorArrow) comp).getConnector() == conn)) {

				return (JConnectorArrow) comp;
			}
		}

		return null;
	}

	protected void resume() {
		Collection<AbstractNode> nodes = project.getNodes().values();

		for (AbstractNode node : nodes) {
			new JNodePanel(this, node);
		}

		for (Connector con : project.getConnectors()) {
			new JConnectorArrow(this, con);
		}
	}

	public void addNode(JNodePanel nodePanel) {
		int width = nodePanel.getX() + nodePanel.getWidth();
		int height = nodePanel.getY() + nodePanel.getHeight();
		boolean change = false;

		if (width > maxWidth) {
			maxWidth = width;
			change = true;
		}
		if (height > maxHeight) {
			maxHeight = height;
			change = true;
		}

		if (change) {
			setPreferredSize(new Dimension(maxWidth, maxHeight));
			updateUI();
		}
	}

	class ContextMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		ContextMenu() {
			super();
			add(new AbstractAction("Delete") {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent arg0) {
					deleteSelectableComponent();
				}
			});
		}
	}

	class NodeContextMenu extends JPopupMenu {

		JMenu spetialItem = new JMenu("Special");

		private static final long serialVersionUID = 1L;

		JNodePanel nodePanel;

		@SuppressWarnings("serial")
		NodeContextMenu() {
			super();

			add(new AbstractAction("Configure") {
				public void actionPerformed(ActionEvent arg0) {
					configureNode(nodePanel);
				}
			});

			addSeparator();

			add(spetialItem);

			add(new AbstractAction("Delete") {
				public void actionPerformed(ActionEvent arg0) {
					deleteSelectableComponent();
				}
			});
			add(new AbstractAction("Change Name") {
				public void actionPerformed(ActionEvent arg0) {
					setComponentProperties(nodePanel.getNode());
				}
			});
			/*
			add(new AbstractAction("Run and Preview Results") {
				public void actionPerformed(ActionEvent arg0) {
					debugNode(nodePanel.getNode());
				}
			});*/
		}

		void setComponentProperties(AbstractNode node) {
			JPropertiesDialog dlg = new JPropertiesDialog(
					ApatarUiMain.MAIN_FRAME, "Change Name", true);
			dlg.setLocationRelativeTo(nodePanel);
			dlg.setVisible(true);
			if (dlg.isOk()) {
				ApplicationData.STATUS_APPLICATION = ApplicationData.EDITED_STATUS;
				nodePanel.changeTitle(dlg.getNodeName());
			}
		}

		void debugNode(AbstractNode node) {
			if (node instanceof OperationalNode) {
				OperationalNode opNode = (OperationalNode) node;
				// execute to the operational node X
				ApplicationData.clearLogsBeforeRun();

				Runnable rn = new Runnable(true);
				rn.Run(ApplicationData.getProject().getNodes().values(),
						opNode, new ProcessingProgressActions());
			}
		}

		public JMenu getSpetialItem() {
			return spetialItem;
		}
	}

	public void deleteSelectableComponent() {

		if (0 == selectedObjArr.size()) {
			return;
		}

		int returnFlag = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete the Component(s)", "Message",
				JOptionPane.YES_NO_OPTION);

		if (JOptionPane.YES_OPTION == returnFlag) {

			for (Component comp : selectedObjArr) {
				if (comp instanceof Selectable) {
					if (((Selectable) comp).isSelected()) {
						((Selectable) comp).beforeDeleteComponent();
						removeComponent(comp);
					}
				}
			}

			selectedObjArr.removeAll(selectedObjArr);
		}

		updateUI();
	}

	public void deleteAllComponent() {
		Component[] components = getComponents();
		for (Component element : components) {
			removeComponent(element);
		}
	}

	public void addNodeToProject(AbstractNode node) {
		project.addNode(node);
	}

	void removeComponent(Component comp) {
		ApplicationData.STATUS_APPLICATION = ApplicationData.EDITED_STATUS;
		if (comp instanceof JNodePanel) {
			AbstractNode node = ((JNodePanel) comp).getNode();
			Collection<ConnectionPoint> cps = node.getConnPoints();
			for (ConnectionPoint cp : cps) {
				List<Connector> connectors = cp.getConnectors();
				while (connectors.size() > 0) {
					Connector connector = connectors.get(0);
					project.getConnectors().remove(connector);
					connector.getBegin().decrementCountConnection();
					connector.getBegin().getConnectors().remove(connector);
					connector.getEnd().decrementCountConnection();
					connector.getEnd().getConnectors().remove(connector);
					Component connArrow = getConnectorArrow(connector);
					if (connArrow != null) {
						remove(connArrow);
					}
				}
				project.getNodes().remove(node.getId());
			}
		}
		if (comp instanceof JConnectorArrow) {
			Connector connector = ((JConnectorArrow) comp).getConnector();
			connector.getBegin().decrementCountConnection();
			connector.getBegin().getConnectors().remove(connector);
			connector.getEnd().decrementCountConnection();
			connector.getEnd().getConnectors().remove(connector);
			project.getConnectors().remove(connector);
		}
		remove(comp);
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// Mouse event processing & focus
	// ///////////////////////////////////////////////////////////////////////////////////
	public void mouseClicked(MouseEvent e) {
		// request focus to process messages
		requestFocusInWindow();

		// source of the event is alwasy of work pane
		Component activeComp = getClickableElement(e.getPoint());

		if (activeComp != null && activeComp instanceof JNodePanel
				&& e.getClickCount() == 2) {
			configureNode(activeComp);
		}
	}

	public void mousePressed(MouseEvent e) {
		// request focus to process messages
		requestFocusInWindow();
		if (!(e.getSource() instanceof Component)) {
			return;
		}

		// source of the event is always of work pane
		Component activeComp = getClickableElement(e.getPoint());

		if (null == activeComp) {
			// click outside of selectable element
			unSelectAllItems();
			mousePressStart.setLocation(e.getX(), e.getY());
			mouseSelect = new Rectangle(mousePressStart);
			selectedObjArr.removeAll(selectedObjArr);
			this.add(clip);

			return;
		}

		Selectable selectObj = (Selectable) activeComp;
		// Ctrl pressed
		if (e.isControlDown()) {
			selectObj.setSelected(!selectObj.isSelected());
			if (selectObj.isSelected()) {
				selectedObjArr.add(activeComp);
			} else {
				selectedObjArr.remove(activeComp);
				// Shift pressed
			}
		} else if (e.isShiftDown()) {
			if (!selectObj.isSelected()) {
				selectObj.setSelected(true);
				selectedObjArr.add(activeComp);
			}
			// only mouse clicked
		} else {

			if (!selectObj.isSelected()) {
				unSelectAllItems();
				selectObj.setSelected(true);

				selectedObjArr.removeAll(selectedObjArr);
				selectedObjArr.add(activeComp);
			}
		}

		if (e.getButton() != MouseEvent.BUTTON1) {

			activeComp = getClickableElement(e.getPoint());

			if (null != activeComp && activeComp instanceof JNodePanel) {
				nodeContextMenu.nodePanel = (JNodePanel) activeComp;
				addSpetionalAction(activeComp, nodeContextMenu.getSpetialItem());
				nodeContextMenu.show(this, e.getX(), e.getY());
			} else {
				contextMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			this.remove(clip);

			if (mouseSelect != null) {
				selectedObjArr.removeAll(selectedObjArr);

				for (int i = 0; i < getComponentCount(); i++) {
					Component comp = getComponent(i);

					if (mouseSelect.intersects(comp.getBounds())
							&& (comp instanceof Selectable)) {
						selectedObjArr.add(comp);
						((Selectable) comp).setSelected(true);
					}
				}
			}
			mouseSelect = null;
		}
		updateUI();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		// this function works only in select set of components mode
		// when clipping rectangle is already added to the panel

		if (clip.getParent() == null) {
			return;
		}
		if (e.getX() > mousePressStart.x) {
			mouseSelect.width = e.getX() - mousePressStart.x;
		} else {
			mouseSelect.width = mousePressStart.x - e.getX();
			mouseSelect.x = e.getX();
		}

		if (e.getY() > mousePressStart.y) {
			mouseSelect.height = e.getY() - mousePressStart.y;
		} else {
			mouseSelect.height = mousePressStart.y - e.getY();
			mouseSelect.y = e.getY();
		}

		if (mouseSelect != null) {
			clip.setBounds(mouseSelect.x, mouseSelect.y, mouseSelect.width,
					mouseSelect.height);
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent arg0) {
	}

	// ///////////////////////////////////////////////////////////////////////////////
	// Drag functionality
	// //////////////////////////////////////////////////////////////////////////////
	public void drop(DropTargetDropEvent dtde) {

		Transferable data = dtde.getTransferable();

		try {
			if (data.isDataFlavorSupported(DndTransferData.createNodeData)) {

				NodeFactory nf = (NodeFactory) data
						.getTransferData(DndTransferData.createNodeData);
				AbstractNode newNode = nf.createNode();
				if (newNode instanceof ColumnNode) {
					ColumnNode colNode = (ColumnNode) newNode;
					if (colNode.isInbound()) {
						if (project.getNodes().values().contains(newNode)) {
							return;
						}
					}
				}

				project.addNode(newNode);
				newNode.setPosition(dtde.getLocation());

				addNode(new JNodePanel(this, newNode));
			} else if (data.isDataFlavorSupported(DndTransferData.moveNodeData)) {
				/*
				 * 
				 * DndTransferData.MoveNodeData moveData =
				 * (DndTransferData.MoveNodeData) data
				 * .getTransferData(DndTransferData.moveNodeData);
				 * 
				 * Point loc = dtde.getLocation();
				 * SwingUtilities.convertPointFromScreen(loc, this);
				 * 
				 * int newPosX = loc.x - moveData.origin.x; int newPosY = loc.y
				 * - moveData.origin.y; // drag all components at time for
				 * (Iterator it = moveData.nodePanels.iterator(); it.hasNext();)
				 * { MoveRecord mr = (MoveRecord)it.next();
				 * mr.nodePanel.setLocation((int)(loc.x - mr.origin.getX()),
				 * (int)(loc.y - mr.origin.getY())); }
				 * 
				 * moveData.nodePanels.get(0).nodePanel.setLocation(loc.x -
				 * moveData.origin.x, loc.y - moveData.origin.y);
				 */

			} else if (data.isDataFlavorSupported(DndTransferData.linkNodeData)) {

				ConnectionPoint begin = (ConnectionPoint) data
						.getTransferData(DndTransferData.linkNodeData);

				if (!(dtde.getDropTargetContext().getComponent() instanceof JConnectionPointLabel)) {
					return;
				}

				JLabel destLabel = (JLabel) dtde.getDropTargetContext()
						.getComponent();
				JNodePanel nodePanel = (JNodePanel) destLabel.getParent();

				ConnectionPoint end = nodePanel.getConnPoint(destLabel);

				// Can't input/output connection from 1 connPoint
				if (begin.getIsMultipleConnection() == true) {
					if (end.getCountConnection() < 1) {
						Connector conn = project.connect(begin, end);
						new JConnectorArrow(this, conn);

						begin.incrementCountConnection();
						end.incrementCountConnection();
					}
				} else if ((begin.getCountConnection() < 1)
						&& (end.getCountConnection() < 1)) {
					Connector conn = project.connect(begin, end);
					new JConnectorArrow(this, conn);

					begin.incrementCountConnection();
					end.incrementCountConnection();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	public void dragEnter(DragSourceDragEvent dsde) {
	}

	public void dragGestureRecognized(DragGestureEvent dge) {

		// source of the event is alwasy of work pane
		Component activeComp = getClickableElement(dge.getDragOrigin());

		if (activeComp != null && activeComp instanceof JNodePanel
				&& ((JNodePanel) activeComp).isSelected()) {
			List<JNodePanel> selected = new ArrayList<JNodePanel>();
			for (Component cp : selectedObjArr) {
				if (cp instanceof JNodePanel) {
					selected.add((JNodePanel) cp);
				}
			}
			dge.startDrag(DragSource.DefaultMoveDrop, DndTransferData
					.createMoveData(selected, dge.getDragOrigin()), this);
		} else if (dge.getComponent() instanceof JConnectionPointLabel) {
			ConnectionPoint connPoint = ((JNodePanel) dge.getComponent()
					.getParent()).getConnPoint((JLabel) dge.getComponent());
			dge.startDrag(DragSource.DefaultLinkDrop, DndTransferData
					.createLinkData(connPoint), this);
		}
	}

	public void dragOver(DragSourceDragEvent dsde) {

		Transferable data = dsde.getDragSourceContext().getTransferable();
		try {
			DndTransferData.MoveNodeData moveData = (DndTransferData.MoveNodeData) data
					.getTransferData(DndTransferData.moveNodeData);

			Point loc = dsde.getLocation();
			SwingUtilities.convertPointFromScreen(loc, this);

			int newPosX = loc.x - moveData.origin.x;
			int newPosY = loc.y - moveData.origin.y;

			// drag all components at time
			ApplicationData.STATUS_APPLICATION = ApplicationData.EDITED_STATUS;
			for (MoveRecord mr : moveData.nodePanels) {
				mr.nodePanel.setLocation((int) (mr.origin.getX() + newPosX),
						(int) (mr.origin.getY() + newPosY));
			}

			updateUI();
		} catch (Exception e) {
		}
	}

	private void configureNode(Component activeComp) {
		Container parent = getParent();
		while (parent != null && !(parent instanceof Window)) {
			parent = parent.getParent();
		}
		AbstractNode node = ((JNodePanel) activeComp).getNode();
		ApatarActions actions = new ApatarActions(node);
		actions.setWin((Window) parent);
		((JNodePanel) activeComp).getNode().edit(actions);

		// TODO - take a look into edit function of abstract node

		JNodePanel np = (JNodePanel) activeComp;
		np.changeTitle(node.getTitle());
		np.updateConnectionPointLabel(node);
	}

	private void addSpetionalAction(Component activeComp, JMenu item) {
		List<Action> actions = ((JNodePanel) activeComp).getNode()
				.getSpecialAction();
		item.removeAll();

		if (actions == null || actions.size() < 1) {
			item.setVisible(false);
			return;
		}
		for (Action action : actions) {
			item.add(new JMenuItem(action));
		}
		item.setVisible(true);
	}

}