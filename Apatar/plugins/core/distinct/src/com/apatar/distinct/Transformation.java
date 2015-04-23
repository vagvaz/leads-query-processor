/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.distinct;

import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdom.Element;

import com.apatar.core.AbstractNode;
import com.apatar.core.ColumnNode;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;

public class Transformation {

	int currentId = 0;
	int currentIndexColumn = 0;
	int currenIndexTransform = 0;
	boolean error = false;

	public Transformation() {
		super();
	}

	/**
	 * createSequenceTransformXml
	 * 
	 * @param nodes
	 * @return Element Transform
	 */
	public Element createSequenceTransformXml(Collection<AbstractNode> nodes) {

		currenIndexTransform = 0;
		error = false;
		Element root = new Element("Transforms");
		Element targetColumn = null;
		for (Object element : nodes) {
			currentId = 0;
			currentIndexColumn = 0;
			ColumnNode colNode = (ColumnNode) element;

			Element transformElement = new Element("Transform");
			transformElement.setAttribute("id", String
					.valueOf(currenIndexTransform++));
			root.addContent(transformElement);
			targetColumn = new Element("TargetColumn");
			targetColumn.setAttribute("name", colNode.getColumnName());
			transformElement.addContent(targetColumn);

			xmlTransform(transformElement, targetColumn, colNode);
		}

		return error ? null : root;
	}

	private void xmlTransform(Element generalElement, Element element,
			AbstractNode node) {
		for (Object element2 : node.getConnPoints()) {
			ConnectionPoint cp = (ConnectionPoint) element2;
			if (cp.isInbound()) {
				Object value = cp.getVariableValue();

				if (null == value) {
					List connectors = cp.getConnectors();
					if (connectors == null || connectors.size() == 0) {
						JOptionPane.showMessageDialog(null, "Error");
						error = true;
						return;
					}
					Connector connector = (Connector) connectors.get(0);
					if (connector == null) {
						JOptionPane.showMessageDialog(null, "Error");
						error = true;
						return;
					}

					Object inputNode = connector.getBegin().getNode();

					if (inputNode instanceof ColumnNode) {
						addSourceColumn(generalElement, element,
								(AbstractNode) inputNode);
					}
				} else {
					Element inputElement = new Element("input");
					inputElement.setAttribute("type", "const");
					inputElement.setAttribute("value", value.toString());
					element.addContent(inputElement);
				}
			}
		}
	}

	private void addSourceColumn(Element generalElement, Element element,
			AbstractNode node) {
		ColumnNode colNode = (ColumnNode) node;
		Element columnElement = new Element("SourceColumn");
		columnElement.setAttribute("name", colNode.getColumnName());
		columnElement.setAttribute("id", String.valueOf(currentId));
		generalElement.addContent(columnElement);
		Element inputElement = new Element("input");
		inputElement.setAttribute("type", "column");
		inputElement.setAttribute("id", String.valueOf(currentId++));
		inputElement
				.setAttribute("index", String.valueOf(currentIndexColumn++));
		element.addContent(inputElement);
	}
}
