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
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

/*
 * Created on 13.12.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.apatar.ui;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNode;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;
import com.apatar.core.ETableMode;
import com.apatar.core.RDBTable;

public class JNodePanel extends JPanel implements Selectable {
	private static final long serialVersionUID = 1L;

	public class JConnectionPointLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		public JConnectionPointLabel(Icon icon) {
			super(icon);
		}
	}

	List<JConnectionPointLabel> connectionPointLabels = new ArrayList<JConnectionPointLabel>();

	private JTextPanel textPanel = null;

	protected boolean selected = false;

	protected boolean focused = false;

	protected AbstractNode node;

	protected Map<JLabel, ConnectionPoint> connPoints;

	static private int connPointRadius = 0;

	protected JWorkPane workPane;

	int height;

	public JNodePanel(JPanel parent, AbstractNode node) {
		super(null);
		setFocusable(false);

		height = calculateHeight(node.getHeigth(), node.getInputConnPoints()
				.size()
				+ node.getExtConnPoints().size(), node.getOutputConnPoints()
				.size());
		connPointRadius = node.getInputConnPointIcon().getIconHeight() / 2;
		init(parent, node, node.getPosition().x, node.getPosition().y);
	}

	protected void createConnPoint(ConnectionPoint connPoint, int listSize,
			String connType) {

		Icon icon = null;
		int x = 0;
		int y = 0;

		int number = connPoint.getPositionNumber();

		if ("input".equals(connType)) {
			x = 0;
			y = ((height / listSize) * number) - connPointRadius;
			icon = node.getInputConnPointIcon();
		} else if ("output".equals(connType)) {
			x = node.getWidth() - connPointRadius * 2 - 1;
			y = ((height / listSize) * number) - connPointRadius;
			icon = node.getOutputConnPointIcon();
		} else if ("ext".equals(connType)) {
			/*
			 * x = ((this.node.getWidth() / listSize ) * number) -
			 * connPointRadius; y = 0;
			 */
			x = 0;
			y = ((height / listSize) * number) - connPointRadius;

			icon = node.getExtConnPointIcon();
		}

		int size = icon.getIconHeight();

		JConnectionPointLabel cpLabel = new JConnectionPointLabel(icon);
		String cpComment = connPoint.getComment();
		if (cpComment != null) {
			cpLabel.setToolTipText(cpComment);
		}
		connectionPointLabels.add(cpLabel);
		add(cpLabel);

		cpLabel.setBounds(x, y, size, size);
		cpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		connPoint.setConnLabel(cpLabel);
		connPoints.put(cpLabel, connPoint);

		if (connPoint.isInbound()) {
			new DropTarget(cpLabel, DnDConstants.ACTION_LINK,
					(DropTargetListener) getParent());

		} else {
			DragSource.getDefaultDragSource()
					.createDefaultDragGestureRecognizer(cpLabel,
							DnDConstants.ACTION_LINK,
							(DragGestureListener) getParent());
		}
	}

	public ConnectionPoint getConnPoint(JLabel label) {
		return connPoints.get(label);
	}

	protected void updateBorder() {
		updateUI();
	}

	@Override
	public void setLocation(int x, int y) {

		super.setLocation(x, y);

		node.setPosition(new Point(x, y));
		textPanel.updateComponent();

		JWorkPane workPane = (JWorkPane) getParent();

		workPane.addNode(this);

		Iterator<ConnectionPoint> connPointIter = connPoints.values()
				.iterator();
		while (connPointIter.hasNext()) {
			Iterator<Connector> connIter = connPointIter.next().getConnectors()
					.iterator();
			while (connIter.hasNext()) {
				JConnectorArrow connArrow = workPane.getConnectorArrow(connIter
						.next());
				connArrow.updateBounds();
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		int circleRadius = 6;

		Graphics2D g2 = (Graphics2D) g;

		GradientPaint grad = new GradientPaint(0, 0, UiUtils.whiteColor, 0,
				height, getBackground());
		g2.setPaint(grad);

		g2.fillRoundRect(connPointRadius, connPointRadius, node.getWidth()
				- connPointRadius * 2 - 1, height - connPointRadius * 2,
				circleRadius, circleRadius);

		g2.setColor(UiUtils.blackColor);

		if (selected || focused) {
			g2.setStroke(new BasicStroke(3.0F));
		} else {
			g2.setStroke(new BasicStroke(1.0F));
		}

		g2.drawRoundRect(connPointRadius, connPointRadius, node.getWidth()
				- connPointRadius * 2 - 1, height - connPointRadius * 2,
				circleRadius, circleRadius);
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		updateBorder();
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
		updateBorder();
	}

	public AbstractNode getNode() {
		return node;
	}

	public boolean isMouseClickedOnComponent(int x, int y) {
		Rectangle rec = new Rectangle(connPointRadius, connPointRadius,
				getWidth() - connPointRadius, getHeight() - connPointRadius);

		return rec.contains(x, y);
	}

	private void init(JPanel parent, AbstractNode node, int x, int y) {
		this.node = node;

		updateBorder();
		setOpaque(false);
		setBounds(x, y, this.node.getWidth(), height);
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

		workPane = (JWorkPane) parent;
		parent.add(this, 0);
		workPane.addNode(this);

		setLayout(null);

		ImageIcon nodeIcon = null;

		connPoints = new HashMap<JLabel, ConnectionPoint>();

		nodeIcon = node.getIcon();

		insertConnectionPoints(getTableMode(node));

		JLabel icon = new JLabel(nodeIcon);
		add(icon);
		int iconWidth = nodeIcon.getIconWidth();
		int iconHeight = nodeIcon.getIconHeight();
		icon.setBounds(this.node.getWidth() / 2 - iconWidth / 2, height / 2
				- iconHeight / 2, iconWidth, iconHeight);

		textPanel = new JTextPanel(this);

		parent.add(textPanel, workPane.getComponentCount());

		repaint();
		parent.updateUI();
	}

	private ETableMode getTableMode(AbstractNode node) {
		ETableMode tm = ETableMode.ReadWrite;
		if (node instanceof AbstractDataBaseNode) {
			AbstractDataBaseNode adbNode = (AbstractDataBaseNode) node;
			RDBTable table = adbNode.getTable();
			if (table != null) {
				tm = table.getMode();
			}
		}
		return tm;
	}

	public void beforeDeleteComponent() {
		workPane.removeComponent(textPanel);
	}

	public void changeTitle(String title) {
		node.setTitle(title);
		textPanel.updateComponent();
	}

	public String getNodeTitle() {
		return node.getTitle();
	}

	// map events to parent
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID() != MouseEvent.MOUSE_EXITED
				&& e.getID() != MouseEvent.MOUSE_ENTERED) {
			getParent().dispatchEvent(e);
		}
	}

	public void insertConnectionPoints(ETableMode tm) {

		if (tm == ETableMode.ReadOnly || tm == ETableMode.ReadWrite) {
			int sizeConnPointList = node.getOutputConnPoints().size() + 1;
			for (ConnectionPoint cp : node.getOutputConnPoints()) {
				createConnPoint(cp, sizeConnPointList, "output");
			}
		}

		// insert input and ext connection points
		if (tm == ETableMode.WriteOnly || tm == ETableMode.ReadWrite) {
			int sizeConnPointList = node.getInputConnPoints().size() + 1
					+ node.getExtConnPoints().size();
			for (ConnectionPoint cp : node.getExtConnPoints()) {
				createConnPoint(cp, sizeConnPointList, "ext");
			}
			for (ConnectionPoint cp : node.getInputConnPoints()) {
				createConnPoint(cp, sizeConnPointList, "input");
			}
		}
	}

	public void removeConnectionPointLabel() {
		for (JConnectionPointLabel label : connectionPointLabels) {
			remove(label);
		}
	}

	public void updateConnectionPointLabel(AbstractNode node) {
		removeConnectionPointLabel();
		insertConnectionPoints(getTableMode(node));
	}

	protected static int calculateHeight(int minHeight, int countInput,
			int countOutput) {
		int areaPoint = connPointRadius * 2;
		int standoff = 10;
		int maxcountPoint = Math.max(countInput, countOutput);

		int requiredHeight = (standoff * 2)
				+ ((areaPoint + standoff) * (maxcountPoint - 1));

		return Math.max(minHeight, requiredHeight);
	}

	public int getFontStile() {
		return node.getFontStyle();
	}
}

class JTextPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JNodePanel nodePanel = null;

	private JTextArea titleLabel = null;

	private final int PANEL_HEIGHT = 18;

	public JTextPanel(JNodePanel nodePanel) {
		setLayout(null);
		setOpaque(false);

		this.nodePanel = nodePanel;
		titleLabel = new JTextArea();
		titleLabel.setOpaque(false);
		titleLabel.setEditable(false);
		titleLabel.setFont(getFont());
		add(titleLabel);
		updateComponent();
	}

	public void updateComponent() {
		titleLabel.setText(nodePanel.getNode().getTitle());

		int fontStile = nodePanel.getFontStile();
		if (fontStile >= 0) {
			titleLabel.setFont(titleLabel.getFont().deriveFont(fontStile));
		}

		int panelWidth = titleLabel.getPreferredSize().width;
		int posX = ((nodePanel.getBounds().width - panelWidth) / 2);

		setBounds(nodePanel.getNode().getPosition().x + posX, nodePanel
				.getNode().getPosition().y
				+ nodePanel.getHeight(), panelWidth, PANEL_HEIGHT
				+ titleLabel.getPreferredSize().height);
		titleLabel.setBounds(0, 0, getWidth(), getHeight());
		super.updateUI();
	}

}
