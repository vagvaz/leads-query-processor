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

package com.apatar.mapReduce;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.ApplicationData;
import com.apatar.core.ColumnNode;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.DataTransNode;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.Record;
import com.apatar.core.TableInfo;
import com.apatar.mapReduce.MapReduceNode;
import com.apatar.mapReduce.MapReduceNodeUtils;
import com.apatar.mapReduce.ui.JTransformDialog;
import com.apatar.ui.GetInputs;

public class MapReduceNode extends DataTransNode implements GetInputs {

	public static final String INPUT_CONN_POINT = "input";
	public static final String OUTPUT_CONN_POINT = "output";

	public MapReduceNode() {
		super();

		outputConnectionList.put(new ConnectionPoint(OUTPUT_CONN_POINT, false,
				this, true), new TableInfo());
		inputConnectionList.add(new ConnectionPoint(INPUT_CONN_POINT, true,
				this, false));

		title = "MapReduce";
	}

	@Override
	public ImageIcon getIcon() {
		return MapReduceNodeUtils.TRANSFORM_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {

		// JTransformDialog transformDialog = new JTransformDialog(prj, this);
		return JTransformDialog.showDialog(prj, this) == JTransformDialog.OK_OPTION;
		// transformDialog.setKeyForReferringToDescription("help.operation.transform");
		// transformDialog.setVisible(true);
		// return true;
	}

	@Override
	public Element saveToElement() {
		Element transformNode = super.saveToElement();

		return transformNode;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
	}

	@Override
	public TableInfo getDebugTableInfo() {
		return getTiForConnection(OUTPUT_CONN_POINT);
	}

	@Override
	public void Transform() {

		TableInfo iTI = getTiForConnection(INPUT_CONN_POINT);
		ResultSet rs = null;
		DataBaseTools.completeTransfer();
		try {
			rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());
			while (rs.next()) {
				try {
					// fill all the column nodes
					for (Object node : prj.getNodes().values()) {
						if (!(node instanceof ColumnNode)) {
							continue;
						}
						try {
							ColumnNode colNode = (ColumnNode) node;
							if (!colNode.isInbound()) {
								colNode.setResult(rs
										.getObject(((ColumnNode) node)
												.getColumnName()));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}

					// execute the project
					com.apatar.core.Runnable rn = new com.apatar.core.Runnable();
					rn.execute(prj.getNodes().values());
					// TODO BUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

					KeyInsensitiveMap datas = new KeyInsensitiveMap();
					for (Object node : prj.getNodes().values()) {
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

					TableInfo ti = getTiForConnection(MapReduceNode.OUTPUT_CONN_POINT);
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(), ti
									.getTableName(), ti.getRecords(),
									ApplicationData.getTempJDBC()), datas);
				} catch (Exception e) {
					e.printStackTrace();
				}

				ApplicationData.ProcessingProgress.Step();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	public List<Record> getInputs() {
		TableInfo ti = getTiForConnection(MapReduceNode.INPUT_CONN_POINT);
		if (ti == null) {
			return null;
		}
		return ti.getRecords();
	}
}
