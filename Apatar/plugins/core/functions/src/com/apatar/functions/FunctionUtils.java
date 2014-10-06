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

package com.apatar.functions;

import java.awt.Point;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;

import com.apatar.core.AbstractNode;
import com.apatar.core.ColumnNode;
import com.apatar.core.ColumnNodeFactory;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;
import com.apatar.core.CoreUtils;
import com.apatar.core.DataConversionAlgorithm;
import com.apatar.core.Project;
import com.apatar.core.Record;
import com.apatar.core.TableInfo;
import com.apatar.ui.JConnectorArrow;
import com.apatar.ui.JNodePanel;
import com.apatar.ui.JWorkPane;

public class FunctionUtils {
	public static final ImageIcon FUNCTION_ICON = new ImageIcon(
			FunctionNodeFactory.class.getResource("validate-32.png"));
	public static final ImageIcon SMALL_FUNCTION_ICON = new ImageIcon(
			FunctionNodeFactory.class.getResource("validate-16.png"));

	public static String getAntipodeForFunction(String base, int destType) {

		if (base.startsWith("com.apatar.functions.String.data.")) {
			if ("com.apatar.functions.String.data.ToInt16TransformFunction"
					.equals(base)
					|| "com.apatar.functions.String.data.ToInt32TransformFunction"
							.equals(base)
					|| "com.apatar.functions.String.data.ToInt64TransformFunction"
							.equals(base)
					|| "com.apatar.functions.String.data.ToIntSingleTransformFunction"
							.equals(base)
					|| "com.apatar.functions.String.data.ToBooleanTransformFunction"
							.equals(base)) {
				switch (destType) {
				case Types.BIGINT: // ToInt64
					return "com.apatar.functions.String.data.ToInt64TransformFunction";

				case Types.INTEGER: // ToInt32
				case Types.SMALLINT: // ToInt32
					return "com.apatar.functions.String.data.ToInt32TransformFunction";

				case Types.TINYINT: // ToInt16
					return "com.apatar.functions.String.data.ToInt16TransformFunction";

				case Types.BIT: // ToSingle
					return "com.apatar.functions.String.data.ToSingleTransformFunction";

				case Types.DECIMAL: // ToDecimal
					return "com.apatar.functions.String.data.ToDecimalTransformFunction";

				case Types.DOUBLE: // ToDouble
				case Types.FLOAT:
				case Types.NUMERIC:
				case Types.REAL:
					return "com.apatar.functions.String.data.ToDoubleTransformFunction";

				case Types.CHAR: // ToString
				case Types.CLOB:
				case Types.LONGVARCHAR:
				case Types.VARCHAR:
					return "com.apatar.functions.String.data.ToStringTransformFunction";

				}

			} else if ("com.apatar.functions.String.data.ToDateTransformFunction"
					.equals(base)
					|| "com.apatar.functions.String.data.ToTimeTransformFunction"
							.equals(base)) {
				switch (destType) {
				case Types.CLOB: // ToString
				case Types.LONGVARCHAR:
				case Types.VARCHAR:
					return "com.apatar.functions.String.data.ToStringTransformFunction";
				case Types.DATE:
					return "com.apatar.functions.String.data.ToDateTransformFunction";
				case Types.TIME:
					return "com.apatar.functions.String.data.ToTimeTransformFunction";

				}
			}

			return null;
		} else {
			return null;
		}
	}

	public static int createNodeColumns(JWorkPane workPane, Project project,
			TableInfo output, TableInfo input, int xOut, int startYOut, int xIn) {
		for (Record rot : output.getSchemaTable().getRecords()) {
			Record rit = Record.getRecordByFieldName(input.getSchemaTable()
					.getRecords(), rot.getFieldName());
			// if types don't fit then reject this record
			if (rit != null && !DataConversionAlgorithm.recordTypeFit(rit, rot)) {
				rit = null;
			}

			// main functionality

			AbstractNode node = FunctionUtils.addColumnNodeToPane(output, rit,
					workPane, xOut, startYOut, true);

			if (rit != null) {

				AbstractNode inputNode = FunctionUtils.addColumnNodeToPane(
						input, rit, workPane, xIn, startYOut, false);

				FunctionUtils.linkTwoNodes(inputNode, node, workPane, project);
			}

			startYOut += node.getHeigth() + 10;
		}
		return startYOut;
	}

	public static void addNodeToPane(AbstractNode node, JWorkPane workPane,
			int x, int y) {
		node.setPosition(new Point(x, y));
		JNodePanel np = new JNodePanel(workPane, node);
		workPane.addNode(np);
		workPane.addNodeToProject(node);
	}

	public static boolean linkTwoNodes(AbstractNode left, AbstractNode right,
			JWorkPane workPane, Project project) {
		String leftConnectionPoint = null;
		String rightConnectionPoint = null;
		if (left instanceof ColumnNode) {
			leftConnectionPoint = ColumnNode.CONN_POINT;
		} else if (left instanceof FunctionNode) {
			ConnectionPoint lcp = (ConnectionPoint) left.getOutputConnPoints()
					.toArray()[0];
			leftConnectionPoint = lcp.getName();
		}
		if (right instanceof ColumnNode) {
			rightConnectionPoint = ColumnNode.CONN_POINT;
		} else if (right instanceof FunctionNode) {
			ConnectionPoint rcp = (ConnectionPoint) right.getInputConnPoints()
					.toArray()[0];
			rightConnectionPoint = rcp.getName();
		}
		if (leftConnectionPoint != null && rightConnectionPoint != null) {
			Connector connector = project.connect(left
					.getConnPoint(leftConnectionPoint), right
					.getConnPoint(rightConnectionPoint));

			new JConnectorArrow(workPane, connector);
			left.getConnPoint(leftConnectionPoint).incrementCountConnection();
			right.getConnPoint(rightConnectionPoint).incrementCountConnection();
			return true;
		} else {
			return false;
		}
	}

	public static AbstractNode addColumnNodeToPane(TableInfo table,
			Record record, JWorkPane workPane, int x, int y, boolean inbound) {
		ColumnNodeFactory oNf = record.createColumnNodeFactory(table
				.getTableName(), null, inbound);
		AbstractNode node = oNf.createNode();
		addNodeToPane(node, workPane, x, y);

		return node;
	}

	public static void reverseNodeColumns(JWorkPane workPane,
			Project projectSrc, Project projectDest, TableInfo output,
			TableInfo input, int xOut, int startYOut, int xIn) {
		AbstractNode node = null;
		ArrayList<Integer> processedKeys = new ArrayList<Integer>();
		AbstractNode leftNode = null;
		AbstractNode rightNode = null;
		for (int key : projectSrc.getNodes().keySet()) {
			if (processedKeys.contains(key)) {
				continue;
			}
			processedKeys.add(key);
			node = projectSrc.getNode(key);
			if (node instanceof FunctionNode) {
				continue;
			}
			Collection<AbstractNode> nodes = CoreUtils.getChainNodes(node);
			if (nodes.size() == 1) {
				continue;
			}
			if (nodes.toArray()[0] instanceof ColumnNode) {
				ColumnNode cn = (ColumnNode) nodes.toArray()[0];
				leftNode = addColumnNodeToPane(output, cn.getRecord(),
						workPane, xIn, startYOut, !cn.isInbound());
			}
			if (nodes.toArray()[nodes.size() - 1] instanceof ColumnNode) {
				ColumnNode cn = (ColumnNode) nodes.toArray()[nodes.size() - 1];
				rightNode = addColumnNodeToPane(input, cn.getRecord(),
						workPane, xOut, startYOut, !cn.isInbound());
				processedKeys.add(cn.getId());
			}

			if (nodes.size() > 3) {
				// simply add first and last node and not link them
			} else if (nodes.size() == 2) {
				// add both nodes and link them
				linkTwoNodes(leftNode, rightNode, workPane, projectDest);
			} else if (nodes.size() == 3) {
				// add one function (if possible) and link (if possible)
				if (nodes.toArray()[1] instanceof FunctionNode) {
					FunctionNode fn = (FunctionNode) nodes.toArray()[1];

					ColumnNode cn = (ColumnNode) rightNode;
					String newNodeClassName = getAntipodeForFunction(fn
							.getNameClassTransformation(), cn.getRecord()
							.getSqlType());
					if (null != newNodeClassName) {

						FunctionNodeFactory fnc = new FunctionNodeFactory(fn
								.getClass().getClassLoader(), newNodeClassName);
						FunctionNode fnode = (FunctionNode) fnc.createNode();
						addNodeToPane(node, workPane, xIn + ((xOut - xIn) / 2),
								startYOut);
						linkTwoNodes(leftNode, fnode, workPane, projectDest);
						linkTwoNodes(fnode, rightNode, workPane, projectDest);
					}
				}
			}
			startYOut += leftNode.getHeigth() + 10;
		}
	}
}
