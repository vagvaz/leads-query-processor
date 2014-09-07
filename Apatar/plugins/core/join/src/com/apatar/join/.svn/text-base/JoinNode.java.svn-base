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

package com.apatar.join;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.ColumnNode;
import com.apatar.core.Condition;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.DataTransNode;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.Record;
import com.apatar.core.TableConditionWrapper;
import com.apatar.core.TableInfo;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.join.ui.JJoinDialog;
import com.apatar.ui.GetInputs;

public class JoinNode extends DataTransNode implements GetInputs {

	public static final String INPUT_CONN_POINT_1 = "input1";
	public static final String INPUT_CONN_POINT_2 = "input2";
	public static final String OUTPUT_CONN_POINT = "output";

	public static final String LEFT_JOIN = "left";
	public static final String RIGHT_JOIN = "right";
	public static final String INNER_JOIN = "inner";

	private String tableJoinType = LEFT_JOIN;

	List<Condition> conditions = new ArrayList<Condition>();

	public JoinNode() {
		super();
		title = "Join";
		inputConnectionList.add(new ConnectionPoint(INPUT_CONN_POINT_1, true,
				this, false, "Table 1", 1));
		inputConnectionList.add(new ConnectionPoint(INPUT_CONN_POINT_2, true,
				this, false, "Table 2", 2));

		outputConnectionList.put(new ConnectionPoint(OUTPUT_CONN_POINT, false,
				this, true), new TableInfo());
	}

	@Override
	public ImageIcon getIcon() {
		return JoinUtils.JOIN_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		// update column nodes references first
		UpdateColumnNodes();

		return JJoinDialog.showDialog(prj, this) == JJoinDialog.OK_OPTION;
	}

	@Override
	public Element saveToElement() {
		Element joinNode = super.saveToElement();
		joinNode.addContent(new Element("tableJoinType")
				.setText(getTableJoinType()));

		// save conditions
		for (Object element : conditions) {
			Condition condition = (Condition) element;
			joinNode.addContent(condition.saveToElement());
		}
		return joinNode;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);

		// read conditions
		conditions.clear();
		for (Iterator<Element> it = e.getChildren("Condition").iterator(); it
				.hasNext();) {
			Condition con = new Condition();
			con.initFromElement(it.next());
			conditions.add(con);
		}
		try {
			setTableJoinType(e.getChildText("tableJoinType"));
		} catch (RuntimeException e1) {
		}
	}

	public List getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public void clearConditions() {
		conditions.clear();
	}

	public void addCondition(Condition condition) {
		conditions.add(condition);
	}

	public void removeCondition(Condition condition) {
		conditions.remove(condition);
	}

	private String getColumnNodeFieldName(ColumnNode node) {
		return getTiForConnection(node.getConnectionName()).getTableName()
				+ "_" + node.getColumnName();
	}

	@Override
	public void Transform() {
		DataBaseTools.completeTransfer();

		List<String> ri1 = new ArrayList<String>();
		List<String> ri2 = new ArrayList<String>();
		// Map<String, String> nameChanges = new HashMap<String, String>();
		TableInfo outTI = getTiForConnection(OUTPUT_CONN_POINT);

		/*
		 * for (Iterator it = prj.getConnectors().iterator();it.hasNext();) {
		 * Connector connector = (Connector)it.next(); ColumnNode iNode =
		 * (ColumnNode)connector.getBegin().getNode(); ColumnNode oNode =
		 * (ColumnNode)connector.getEnd().getNode(); String connName =
		 * iNode.getConnectionName(); if (connName.equals(INPUT_CONN_POINT_1))
		 * ri1.add(iNode.getColumnName()); else ri2.add(iNode.getColumnName());
		 * //nameChanges.put(iNode.getColumnName(), oNode.getColumnName()); }
		 */

		for (Object node : prj.getNodes().values()) {
			if (!(node instanceof ColumnNode)) {
				continue;
			}
			ColumnNode colNode = (ColumnNode) node;
			String connName = colNode.getConnectionName();
			if (!colNode.isInbound()) {
				if (connName.equals(INPUT_CONN_POINT_1)) {
					ri1.add(colNode.getColumnName());
				} else {
					ri2.add(colNode.getColumnName());
				}
			}
		}

		com.apatar.core.TableInfo ti1 = getTiForConnection(INPUT_CONN_POINT_1);
		com.apatar.core.TableInfo ti2 = getTiForConnection(INPUT_CONN_POINT_2);

		TableConditionWrapper wrp = new TableConditionWrapper(ti1
				.getTableName(), ti2.getTableName(), conditions);

		// build records list from every table
		List<Record> rci1 = new ArrayList<Record>();
		List<Record> rci2 = new ArrayList<Record>();
		for (String str : ri1) {
			rci1.add(Record.getRecordByFieldName(ti1.getRecords(), str));
		}
		for (String str : ri2) {
			rci2.add(Record.getRecordByFieldName(ti2.getRecords(), str));
		}

		SQLCreationData arr[] = new SQLCreationData[] {
				new SQLCreationData(rci1, ti1.getTableName()),
				new SQLCreationData(rci2, ti2.getTableName(),
						getTableJoinType()) };

		SQLQueryString str = DataBaseTools.CreateSelectStringByJoin(
				ApplicationData.getTempDataBase().getDataBaseInfo(), arr, wrp,
				true);

		// SQLQueryString str = DataBaseTools.CreateSelectString(ApplicationData
		// .getTempDataBase().getDataBaseInfo(), arr, wrp);

		if (str == null) {
			return;
		}

		// str.query = str1.query;
		try {// selecting data from JOIN query
			ResultSet rs = DataBaseTools.executeSelect(str, ApplicationData
					.getTempJDBC());
			while (rs.next()) {
				// KeyInsensitiveMap data = DataBaseTools.GetDataFromRS(rs);

				// ----------------------------------------
				for (Object node : prj.getNodes().values()) {
					if (!(node instanceof ColumnNode)) {
						continue;
					}
					try {
						ColumnNode colNode = (ColumnNode) node;
						if (!colNode.isInbound()) {
							colNode
									.setResult(rs
											.getObject(getColumnNodeFieldName(colNode)));
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

				TableInfo ti = getTiForConnection(JoinNode.OUTPUT_CONN_POINT);
				DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
						.getTempDataBase().getDataBaseInfo(),
						ti.getTableName(), ti.getRecords(), ApplicationData
								.getTempJDBC()), datas);
				ApplicationData.ProcessingProgress.Step();
				// ----------------------------------------

				/*
				 * // replace one field name into another field name for (String
				 * oldName : nameChanges.keySet()) { Object dataobj =
				 * data.remove(oldName, true);
				 * data.put(nameChanges.get(oldName), dataobj); }
				 * DataBaseTools.insertData(new DataProcessingInfo(
				 * ApplicationData.getTempDataBase().getDataBaseInfo(),
				 * outTI.getTableName(), outTI.getRecords()), data);
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	@Override
	public TableInfo getDebugTableInfo() {
		// get this node output table
		return (TableInfo) outputConnectionList.values().toArray()[0];
	}

	public List<Record> getInputs() {
		HashSet<String> names = new HashSet<String>();
		TableInfo ti = AbstractNode
				.getOtherSideTableInfo(getConnPoint(JoinNode.INPUT_CONN_POINT_1));
		List<Record> recs1 = null;
		List<Record> recs2 = null;
		if (ti != null) {
			recs1 = ti.getRecords();
		}
		ti = AbstractNode
				.getOtherSideTableInfo(getConnPoint(JoinNode.INPUT_CONN_POINT_2));
		if (ti != null) {
			recs2 = AbstractNode.getOtherSideTableInfo(
					getConnPoint(JoinNode.INPUT_CONN_POINT_2)).getRecords();
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

	/**
	 * @return the tableJoinType
	 */
	public String getTableJoinType() {
		if ("".equals(tableJoinType)) {
			tableJoinType = "left";
		}
		return tableJoinType;
	}

	/**
	 * @param tableJoinType
	 *            the tableJoinType to set
	 */
	public void setTableJoinType(String joinType) {
		tableJoinType = joinType;
	}

}
