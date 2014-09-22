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

package com.apatar.groupByNew;

import java.io.IOException;
import java.io.StringWriter;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.ColumnNode;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.DataTransNode;
import com.apatar.core.Entities;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.Project;
import com.apatar.core.ReadWriteXMLData;
import com.apatar.core.Record;
import com.apatar.core.TableInfo;
import com.apatar.groupByNew.AggregateNode;
import com.apatar.groupByNew.GroupByNewNodeUtils;
import com.apatar.groupByNew.ui.JAggregateDialog;
import com.apatar.ui.GetInputs;

/*
 *
 */
public class AggregateNode extends DataTransNode implements GetInputs {

	public static final String INPUT_CONN_POINT_1 = "input1";
	public static final String INPUT_CONN_POINT_2 = "input2";
	public static final String OUTPUT_CONN_POINT = "output";
	Project project_1 = new Project();
	Project project_2 = new Project();

	public AggregateNode() {
		super();

		outputConnectionList.put(new ConnectionPoint(OUTPUT_CONN_POINT, false,
				this, true), new TableInfo());
		inputConnectionList.add(new ConnectionPoint(INPUT_CONN_POINT_1, true,
				this, false, 1));
		inputConnectionList.add(new ConnectionPoint(INPUT_CONN_POINT_2, true,
				this, false, 2));

		title = "Aggregate";
	}

	@Override
	public ImageIcon getIcon() {
		return GroupByNewNodeUtils.TRANSFORM_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		return JAggregateDialog.showDialog(project_1, project_2, this) == JAggregateDialog.OK_OPTION;
	}

	@Override
	public Element saveToElement() {
		Element el = super.saveToElement();
		writeProjectData(el, "subProject_1", project_1);

		return writeProjectData(el, "subProject_2", project_2);
	}

	private Element writeProjectData(Element parent, String attributeName,
			Project project) {
		StringWriter writer = new StringWriter();
		ReadWriteXMLData rwXMLdata = new ReadWriteXMLData();
		try {
			rwXMLdata.writeXMLData(project, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		parent.setAttribute(attributeName, Entities.XML.escape(writer
				.toString()));
		return parent;
	}

	@Override
	public void initFromElement(Element e) {
		System.out.println("Node id = `" + e.getAttributeValue("id")
				+ "`.Errors before reading = `"
				+ String.valueOf(ApplicationData.COUNT_INIT_ERROR) + "`");
		super.initFromElement(e);

		project_1 = new Project();
		String strPrj = "";
		try {
			strPrj = Entities.XML.unescape(e.getAttributeValue("subProject_1"));

			ReadWriteXMLData.readXMLData(new java.io.StringReader(strPrj),
					project_1);
		} catch (Exception e1) {
			project_1 = prj;
			e1.printStackTrace();
		}
		project_2 = new Project();
		try {
			strPrj = Entities.XML.unescape(e.getAttributeValue("subProject_2"));

			ReadWriteXMLData.readXMLData(new java.io.StringReader(strPrj),
					project_2);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println("Errors after reading = `"
				+ String.valueOf(ApplicationData.COUNT_INIT_ERROR) + "`");
	}

	@Override
	public TableInfo getDebugTableInfo() {
		return getTiForConnection(OUTPUT_CONN_POINT);
	}

	@Override
	public void Transform() {
		DataBaseTools.completeTransfer();
		try {
			transformForInputConnectionPoint(
					getTiForConnection(INPUT_CONN_POINT_1), project_1);
			DataBaseTools.completeTransfer();
			transformForInputConnectionPoint(
					getTiForConnection(INPUT_CONN_POINT_2), project_2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	private void transformForInputConnectionPoint(TableInfo iTI, Project project)
			throws Exception {
		ResultSet rs = null;
		rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
				ApplicationData.getTempJDBC(), ApplicationData
						.getTempDataBaseInfo());
		while (rs.next()) {
			// fill all the column nodes
			for (Object node : project.getNodes().values()) {
				if (node instanceof ColumnNode) {
					ColumnNode colNode = (ColumnNode) node;
					try {
						if (!colNode.isInbound()) {
							Object obj = rs.getObject(((ColumnNode) node)
									.getColumnName());
							colNode.setResult(obj);
						}
					} catch (SQLException e) {
						colNode.setResult(null);
						// e.printStackTrace();
					}
				}
			}

			// execute the project
			com.apatar.core.Runnable rn = new com.apatar.core.Runnable();

			try {
				rn.execute(project.getNodes().values());
			} catch (SocketException e) {
				System.err.println("Error calculating result row. Message: `"
						+ e.getMessage() + "`");
			}

			KeyInsensitiveMap datas = new KeyInsensitiveMap();
			for (Object node : project.getNodes().values()) {
				if (!(node instanceof ColumnNode)) {
					continue;
				}
				ColumnNode cnode = (ColumnNode) node;
				if (!cnode.isInbound()) {
					continue;
				}
				List<Connector> connectors = cnode.getConnPoint(
						ColumnNode.CONN_POINT).getConnectors();
				if (connectors == null || connectors.size() < 1) {
					continue;
				}

				datas.put(cnode.getColumnName(), new JdbcObject(cnode
						.getResult(), cnode.getSqlType()));
			}

			TableInfo ti = getTiForConnection(AggregateNode.OUTPUT_CONN_POINT);

			DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), ti.getTableName(), ti
					.getRecords(), ApplicationData.getTempJDBC()), datas);
			ApplicationData.ProcessingProgress.Step();
		}
	}

	public List<Record> getInputs() {
		HashSet<String> names = new HashSet<String>();
		TableInfo ti = AbstractNode
				.getOtherSideTableInfo(getConnPoint(AggregateNode.INPUT_CONN_POINT_1));
		List<Record> recs1 = null;
		List<Record> recs2 = null;
		if (ti != null) {
			recs1 = ti.getRecords();
		}
		ti = AbstractNode
				.getOtherSideTableInfo(getConnPoint(AggregateNode.INPUT_CONN_POINT_2));
		if (ti != null) {
			recs2 = AbstractNode.getOtherSideTableInfo(
					getConnPoint(AggregateNode.INPUT_CONN_POINT_2))
					.getRecords();
		}

		List<Record> result = new ArrayList<Record>();

		if (recs1 != null) {
			for (Record rec : recs1) {
				String fn = rec.getFieldName();
				names.add(fn);
				result.add(rec.clone());
				names.add(fn);
			}
		}
		if (recs2 != null) {
			for (Record rec : recs2) {
				String fn = rec.getFieldName();
				Record clone = rec.clone();
				if (names.contains(fn)) {
					clone.setFieldName(fn + "_2");
				}
				names.add(fn);
				result.add(clone);
			}
		}

		return result;
	}
}
