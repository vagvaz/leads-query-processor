/*TODO recorded refactoring
 * Метод CalculateResult перемещён из класса ValidatNode в класс DataTransNode
 * *********************
 *Добавлен метод CalculateResult(Project project)
 * *********************
 * Метод CalculateResult() вызывает метод CalculateResult(Project project),
 * передавая поле prj в параметре
 * *********************
 */
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

package com.apatar.core;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import com.apatar.functions.FunctionNode;

public abstract class DataTransNode extends OperationalNode {

	protected Project	prj	= new Project();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.OperationalNode#canSynchronyze()
	 */
	@Override
	public boolean canSynchronyze() {
		return false;
	}

	@Override
	public Element saveToElement() {

		// update column nodes references first
		// this ensures that column full name will be valid
		UpdateColumnNodes();

		Element el = super.saveToElement();

		StringWriter writer = new StringWriter();
		ReadWriteXMLData rwXMLdata = new ReadWriteXMLData();
		try {
			rwXMLdata.writeXMLData(prj, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		el.setAttribute("subProject", Entities.XML.escape(writer.toString()));

		return el;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		prj = new Project();
		String strPrj = Entities.XML
				.unescape(e.getAttributeValue("subProject"));

		try {
			ReadWriteXMLData.readXMLData(new java.io.StringReader(strPrj), prj);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}

	}

	public final void setSubProject(Project prj) {
		this.prj = prj;
	}

	public final Project getSubProject() {
		return prj;
	}

	// update categories for every column node
	// so it displays the valid category
	protected final void UpdateColumnNodes() {
		for (Object element : prj.getNodes().values()) {
			AbstractNode node = (AbstractNode) element;
			if (node instanceof ColumnNode) {
				ColumnNode cn = (ColumnNode) node;
				cn.updateCategory(this);
			}
		}
	}

	// fill input column nodes with the values from ResultSet
	public void FillInputColumnNodes(Project project, ResultSet rs) {
		for (Object node : project.getNodes().values()) {
			if (!(node instanceof ColumnNode)) {
				continue;
			}
			try {
				ColumnNode colNode = (ColumnNode) node;
				if (!colNode.isInbound()) {
					colNode.setResult(rs.getObject(((ColumnNode) node)
							.getColumnName()));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void FillInputColumnNodes(Project project, ResultSet rs,
			String connectionName) {
		for (Object node : project.getNodes().values()) {
			if (!(node instanceof ColumnNode)) {
				continue;
			}
			try {
				ColumnNode cnd = (ColumnNode) node;
				if (cnd.getConnectionName().equals(connectionName)) {
					Object obj = rs.getObject(cnd.getColumnName());
					cnd.setResult((obj));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void FillInputColumnNodes(ResultSet rs, String connectionName) {
		// fill all the column nodes
		FillInputColumnNodes(prj, rs, connectionName);
	}

	// calculate result of execution from all the output nodes
	// evaluate result as false or true
	// ie enlist all the nodes that has no connnected outputs get result from
	// them
	// and calculate the boolean result
	protected boolean calculateResult() {
		return calculateResult(prj);
	}

	protected boolean calculateResult(Project project) {
		for (Object node : project.getNodes().values()) {
			if (!(node instanceof FunctionNode)) {
				continue;
			}
			FunctionNode vnode = (FunctionNode) node;
			if (vnode.getInlinePosition() != AbstractNode.LAST_POSITION) {
				continue;
			}
			if (!((Boolean) vnode.getResult())) {
				return false;
			}
		}
		return true;
	}

}
