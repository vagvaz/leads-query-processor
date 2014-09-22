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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.apatar.core.AbstractNode;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;

public class JConnectorArrow extends JPanel implements Selectable {
	private static final long serialVersionUID = 1L;

	private static final int GAP_ARROW_X = 20;
	private static final int GAP_ARROW_Y = 9;

	private static final int EMPTY_BORDER_Y_FOR_1_POS = 4;

	protected boolean selected = false;

	private final Color color;

	private JWorkPane workPane = null;

	private int stateConnector = 0;
	private int widthPen = 1;
	protected Connector connector;

	private final List<Point> arrowPath = new ArrayList<Point>();
	private final int lengthForSetSelectArrow = 5;

	public Connector getConnector() {
		return connector;
	}

	public JConnectorArrow(JPanel parent, Connector connector) {
		this.connector = connector;
		color = UiUtils.blackColor;
		selected = false;
		workPane = (JWorkPane) parent;

		setOpaque(false);
		// setBorder( BorderFactory.createLineBorder(Color.BLACK, 1) );
		parent.add(this);
		updateBounds();
		repaint();
	}

	public void updateBounds() {

		workPane = (JWorkPane) getParent();

		JNodePanel begin = workPane.getNodePanel(((AbstractNode) connector
				.getBegin().getNode()));
		JNodePanel end = workPane.getNodePanel(((AbstractNode) connector
				.getEnd().getNode()));

		ConnectionPoint beginPoint = connector.getBegin();
		ConnectionPoint endPoint = connector.getEnd();

		int emptyBorder = 1;

		int connPointRadius = beginPoint.getConnLabel().getIcon()
				.getIconWidth() / 2;
		int beginConnPonitPosY = beginPoint.getConnLabel().getLocation().y;
		int endConnPonitPosY = endPoint.getConnLabel().getLocation().y;

		int startX = begin.getX() + begin.getWidth();
		int endX = end.getX();
		int startY = begin.getY();
		int endY = end.getY();

		int x = begin.getX() + begin.getWidth();
		int y = begin.getY() + beginConnPonitPosY + connPointRadius
				- emptyBorder;

		int w = end.getX() - x;
		int h = end.getY() - y + endConnPonitPosY + connPointRadius * 2
				+ emptyBorder;

		stateConnector = 0;

		if (endX - startX <= GAP_ARROW_X) {
			stateConnector = 3;

			x = endX - GAP_ARROW_X;
			w = startX - x + GAP_ARROW_X;
		}

		if (h <= GAP_ARROW_Y - EMPTY_BORDER_Y_FOR_1_POS) {
			stateConnector = 1;

			y = endY + endConnPonitPosY - emptyBorder
					- EMPTY_BORDER_Y_FOR_1_POS;
			h = startY - y + beginConnPonitPosY + connPointRadius + emptyBorder
					* 2 + EMPTY_BORDER_Y_FOR_1_POS;
		}

		if ((endX - startX <= GAP_ARROW_X) && (stateConnector == 1)) {
			stateConnector = 2;

			y = endY + endConnPonitPosY - emptyBorder;
			h = startY - y + beginConnPonitPosY + connPointRadius + emptyBorder
					* 2;
		}

		setBounds(x, y, w, h);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);

		BasicStroke stroke = new BasicStroke(widthPen);

		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(stroke);
		g2.setColor(color);

		eraseArrowPath();

		if (stateConnector == 3) {

			int topEmptyBorder = 4;

			g2.drawLine(getWidth() - GAP_ARROW_X, 1, getWidth() - widthPen, 1);

			addPointToArrowPath(getWidth() - GAP_ARROW_X, 1);
			addPointToArrowPath(getWidth() - widthPen, 1);

			g2.drawLine(getWidth() - widthPen, 1, getWidth() - widthPen,
					getHeight() / 2);

			addPointToArrowPath(getWidth() - widthPen, getHeight() / 2);

			g2.drawLine(getWidth() - widthPen, getHeight() / 2, 0,
					getHeight() / 2);

			addPointToArrowPath(0, getHeight() / 2);

			g2.drawLine(0, getHeight() / 2, 0, getHeight() - topEmptyBorder);

			addPointToArrowPath(0, getHeight() - topEmptyBorder);

			g2.drawLine(0, getHeight() - topEmptyBorder, GAP_ARROW_X - 7,
					getHeight() - topEmptyBorder);

			addPointToArrowPath(GAP_ARROW_X - 7, getHeight() - topEmptyBorder);

			g2.setStroke(new BasicStroke(2));

			polygon.moveTo(GAP_ARROW_X - 7, getHeight() - 4);
			polygon.lineTo(GAP_ARROW_X - 7, getHeight() - 6);
			polygon.lineTo(GAP_ARROW_X - 3, getHeight() - 4);
			polygon.lineTo(GAP_ARROW_X - 7, getHeight() - 2);
			polygon.closePath();
			g2.fill(polygon);
			g2.draw(polygon);

		} else if (stateConnector == 2) {
			int topEmptyBorder = 4;

			g2.drawLine(getWidth() - GAP_ARROW_X, getHeight() - topEmptyBorder
					/ 2, getWidth() - widthPen, getHeight() - topEmptyBorder
					/ 2);

			addPointToArrowPath(getWidth() - GAP_ARROW_X, getHeight()
					- topEmptyBorder / 2);
			addPointToArrowPath(getWidth() - widthPen, getHeight()
					- topEmptyBorder / 2);

			g2.drawLine(getWidth() - widthPen,
					getHeight() - topEmptyBorder / 2, getWidth() - widthPen,
					getHeight() / 2);

			addPointToArrowPath(getWidth() - widthPen, getHeight() / 2);

			g2.drawLine(getWidth() - widthPen, getHeight() / 2, 0,
					getHeight() / 2);

			addPointToArrowPath(0, getHeight() / 2);

			g2.drawLine(0, getHeight() / 2, 0, topEmptyBorder);

			addPointToArrowPath(0, topEmptyBorder);

			g2.drawLine(0, topEmptyBorder, GAP_ARROW_X - 6, topEmptyBorder);

			addPointToArrowPath(GAP_ARROW_X - 6, topEmptyBorder);

			g2.setStroke(new BasicStroke(2));

			polygon.moveTo(13, 4);
			polygon.lineTo(13, 2);
			polygon.lineTo(17, 4);
			polygon.lineTo(13, 6);
			polygon.closePath();
			g2.fill(polygon);
			g2.draw(polygon);

		} else if (stateConnector == 1) {
			int topEmptyBorder = 4;

			g2.drawLine(0, getHeight() - topEmptyBorder / 2
					- EMPTY_BORDER_Y_FOR_1_POS, getWidth() / 2, getHeight()
					- topEmptyBorder / 2 - EMPTY_BORDER_Y_FOR_1_POS);

			addPointToArrowPath(0, getHeight() - topEmptyBorder / 2
					- EMPTY_BORDER_Y_FOR_1_POS);
			addPointToArrowPath(getWidth() / 2, getHeight() - topEmptyBorder
					/ 2 - EMPTY_BORDER_Y_FOR_1_POS);

			g2.drawLine(getWidth() / 2, getHeight() - topEmptyBorder / 2
					- EMPTY_BORDER_Y_FOR_1_POS, getWidth() / 2, topEmptyBorder
					+ EMPTY_BORDER_Y_FOR_1_POS);

			addPointToArrowPath(getWidth() / 2, topEmptyBorder
					+ EMPTY_BORDER_Y_FOR_1_POS);

			g2.drawLine(getWidth() / 2, topEmptyBorder
					+ EMPTY_BORDER_Y_FOR_1_POS, getWidth() - 7, topEmptyBorder
					+ EMPTY_BORDER_Y_FOR_1_POS);

			addPointToArrowPath(getWidth() - 7, topEmptyBorder
					+ EMPTY_BORDER_Y_FOR_1_POS);

			g2.setStroke(new BasicStroke(2));

			polygon.moveTo(getWidth() - 7, 4 + EMPTY_BORDER_Y_FOR_1_POS);
			polygon.lineTo(getWidth() - 7, 2 + EMPTY_BORDER_Y_FOR_1_POS);
			polygon.lineTo(getWidth() - 3, 4 + EMPTY_BORDER_Y_FOR_1_POS);
			polygon.lineTo(getWidth() - 7, 6 + EMPTY_BORDER_Y_FOR_1_POS);
			polygon.closePath();
			g2.fill(polygon);
			g2.draw(polygon);

		} else {
			g2.drawLine(0, 1, getWidth() / 2, 1);

			addPointToArrowPath(0, 1);
			addPointToArrowPath(getWidth() / 2, 1);

			g2.drawLine(getWidth() / 2, 1, getWidth() / 2, getHeight() - 4);

			addPointToArrowPath(getWidth() / 2, getHeight() - 4);

			g2.drawLine(getWidth() / 2, getHeight() - 4, getWidth() - 7,
					getHeight() - 4);

			addPointToArrowPath(getWidth() - 7, getHeight() - 4);

			g2.setStroke(new BasicStroke(2));

			polygon.moveTo(getWidth() - 7, getHeight() - 4);
			polygon.lineTo(getWidth() - 7, getHeight() - 6);
			polygon.lineTo(getWidth() - 3, getHeight() - 4);
			polygon.lineTo(getWidth() - 7, getHeight() - 2);
			polygon.closePath();
			g2.fill(polygon);
			g2.draw(polygon);
		}
	}

	public boolean isMouseClickedOnComponent(int X, int Y) {

		Point p0 = null;
		Point p1 = null;
		int length = 0;
		boolean isApply = false;

		for (int i = 0; i < arrowPath.size() - 1; i++) {
			p0 = arrowPath.get(i);
			p1 = arrowPath.get(i + 1);

			length = ((p0.y - p1.y) * X + (p1.x - p0.x) * Y + (p0.x * p1.y - p1.x
					* p0.y))
					/ (int) Math.sqrt(Math.pow((p1.x - p0.x), 2)
							+ Math.pow((p1.y - p0.y), 2));

			length = Math.abs(length);

			double length_p0p = Math.sqrt(Math.pow((X - p0.x), 2)
					+ Math.pow((Y - p0.y), 2));
			double length_p1p = Math.sqrt(Math.pow((X - p1.x), 2)
					+ Math.pow((Y - p1.y), 2));
			double length_p0p1 = Math.sqrt(Math.pow((p1.x - p0.x), 2)
					+ Math.pow((p1.y - p0.y), 2));

			double delta = Math.sqrt(Math.pow(length_p0p, 2)
					+ Math.pow(length, 2))
					+ Math.sqrt(Math.pow(length_p1p, 2) + Math.pow(length, 2));

			if (lengthForSetSelectArrow > (delta - length_p0p1)) {
				isApply = true;
				break;
			}
		}

		if (!isApply) {
			return false;
		} else {
			return true;
		}
	}

	public void setSelected(boolean value) {

		if (value) {
			widthPen = 3;
		} else {
			widthPen = 1;
		}

		selected = value;
	}

	private void eraseArrowPath() {
		arrowPath.removeAll(arrowPath);
	}

	private void addPointToArrowPath(int x, int y) {
		arrowPath.add(new Point(x, y));
	}

	public boolean isSelected() {
		return selected;
	}

	public void beforeDeleteComponent() {

	}

	// map events to parent
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID() != MouseEvent.MOUSE_EXITED
				&& e.getID() != MouseEvent.MOUSE_ENTERED) {
			getParent().dispatchEvent(e);
		}
	}
}
