/*TODO refactoring
 * сигнатура метода
 * private void setDataToPS(PreparedStatement ps, Object value, int columnType, int i) throws Exception
 * изменена на
 * public static void setDataToPS(PreparedStatement ps, Object value, int columnType, int i) throws Exception
 *
 * и метод перемещён в класс core.DatabaseTools
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

package com.apatar.update;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.ColumnNode;
import com.apatar.core.Condition;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.CoreUtils;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataTransNode;
import com.apatar.core.Entities;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.Project;
import com.apatar.core.ReadWriteXMLData;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.ui.GetInputs;
import com.apatar.update.ui.JUpdateDialog;

public class UpdateNode extends DataTransNode implements GetInputs {

	public static final String LEFT_TO_RIGHT_CONN_POINT = "left_to_right";
	public static final String RIGHT_TO_LEFT_CONN_POINT = "right_to_left";

	protected Project leftTorightTransformation = new Project();
	protected Project rightToLeftTransformation = new Project();
	protected Project updateConditions = new Project();

	List<Condition> matchByConditions = new ArrayList<Condition>();
	private String slaveIndexesTableName = "";

	public UpdateNode() {
		super();
		slaveIndexesTableName = "";
		title = "Update";
		inputConnectionList.add(new ConnectionPoint(LEFT_TO_RIGHT_CONN_POINT,
				true, this, false, "Left table", 1));
		inputConnectionList.add(new ConnectionPoint(RIGHT_TO_LEFT_CONN_POINT,
				true, this, false, "Right table", 2));
	}

	@Override
	public ImageIcon getIcon() {
		return UpdateUtils.UPDATE_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		// update column nodes references first
		UpdateColumnNodes();

		return JUpdateDialog.showDialog(updateConditions,
				rightToLeftTransformation, leftTorightTransformation, this) == JUpdateDialog.OK_OPTION;
	}

	@Override
	public Element saveToElement() {
		UpdateColumnNodes();

		Element joinNode = super.saveToElement();

		StringWriter writer = new StringWriter();
		ReadWriteXMLData rwXMLdata = new ReadWriteXMLData();
		try {
			rwXMLdata.writeXMLData(leftTorightTransformation, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element leftTorightTransformation = new Element(
				"leftTorightTransformation");
		leftTorightTransformation.addContent(Entities.XML.escape(writer
				.toString()));

		joinNode.addContent(leftTorightTransformation);

		writer = new StringWriter();
		// rwXMLdata = new ReadWriteXMLData();
		try {
			rwXMLdata.writeXMLData(rightToLeftTransformation, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element rightToLeftTransformation = new Element(
				"rightToLeftTransformation");
		rightToLeftTransformation.addContent(Entities.XML.escape(writer
				.toString()));

		joinNode.addContent(rightToLeftTransformation);

		writer = new StringWriter();
		// rwXMLdata = new ReadWriteXMLData();
		try {
			rwXMLdata.writeXMLData(updateConditions, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element updateConditions = new Element("updateConditions");
		updateConditions.addContent(Entities.XML.escape(writer.toString()));

		joinNode.addContent(updateConditions);
		for (Object element : matchByConditions) {
			Condition condition = (Condition) element;
			joinNode.addContent(condition.saveToElement());
		}

		return joinNode;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		leftTorightTransformation = new Project();
		rightToLeftTransformation = new Project();
		updateConditions = new Project();
		String strLR = Entities.XML.unescape(e
				.getChildText("leftTorightTransformation"));
		String strRL = Entities.XML.unescape(e
				.getChildText("rightToLeftTransformation"));
		String strUC = Entities.XML
				.unescape(e.getChildText("updateConditions"));

		try {
			ReadWriteXMLData.readXMLData(new java.io.StringReader(strLR),
					leftTorightTransformation);
			ReadWriteXMLData.readXMLData(new java.io.StringReader(strRL),
					rightToLeftTransformation);
			ReadWriteXMLData.readXMLData(new java.io.StringReader(strUC),
					updateConditions);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}
		matchByConditions.clear();
		for (Iterator<Element> it = e.getChildren("Condition").iterator(); it
				.hasNext();) {
			Condition con = new Condition();
			con.initFromElement(it.next());
			matchByConditions.add(con);
		}
	}

	@Override
	public void Transform() {
		DataBaseTools.completeTransfer();

		AbstractDataBaseNode inputNode1 = (AbstractDataBaseNode) getConnPoint(
				LEFT_TO_RIGHT_CONN_POINT).getConnectors().get(0).getBegin()
				.getNode();

		AbstractDataBaseNode inputNode2 = (AbstractDataBaseNode) getConnPoint(
				RIGHT_TO_LEFT_CONN_POINT).getConnectors().get(0).getBegin()
				.getNode();

		if (inputNode1.canSynchronyze() && inputNode2.canSynchronyze()) {
			System.out.println("Start synchronization.");
			if (leftTorightTransformation.getNodes().size() > 1
					&& rightToLeftTransformation.getNodes().size() > 1
					&& updateConditions.getNodes().size() > 2
					&& matchByConditions.size() > 0) {

				// generate SQL SELECT for LR (LR+conditions+matchBy);
				String selectLR;
				// generate SQL SELECT for RL (RL+conditions);
				String selectRL;
				try {
					TableInfo ti1 = inputNode1
							.getThisSideTableInfo((ConnectionPoint) inputNode1
									.getOutputConnPoints().toArray()[0]);
					TableInfo ti2 = inputNode2
							.getThisSideTableInfo((ConnectionPoint) inputNode2
									.getOutputConnPoints().toArray()[0]);

					AbstractDataBaseNode masterNode;
					AbstractDataBaseNode slaveNode;
					TableInfo masterTi;
					TableInfo slaveTi;
					Project LR;
					Project RL;
					List<Condition> matchBy;
					List<Record> recordsForSlaveKeys = new ArrayList<Record>();

					masterNode = inputNode1;
					masterTi = ti1;
					slaveNode = inputNode2;
					slaveTi = ti2;
					LR = leftTorightTransformation;
					RL = rightToLeftTransformation;
					matchBy = matchByConditions;

					HashMap<AbstractNode, ColumnNode> LRnodes = new HashMap<AbstractNode, ColumnNode>();

					HashMap<AbstractNode, ColumnNode> RLnodes = new HashMap<AbstractNode, ColumnNode>();

					for (AbstractNode node : LR.getNodes().values()) {
						if (node instanceof ColumnNode) {
							ColumnNode colNode = (ColumnNode) node;
							if (colNode.isInbound()) {
								LRnodes.put(CoreUtils
										.getFirstNodeInChain(colNode), colNode);
							}
						}
					}

					for (AbstractNode node : RL.getNodes().values()) {
						if (node instanceof ColumnNode) {
							ColumnNode colNode = (ColumnNode) node;
							if (colNode.isInbound()) {
								RLnodes.put(CoreUtils
										.getFirstNodeInChain(colNode), colNode);
							}
						}
					}

					List<Record> matchByMaster = new ArrayList<Record>();
					List<Record> matchBySlave = new ArrayList<Record>();

					for (Condition condition : matchBy) {
						for (Record record : slaveTi.getRecords()) {
							if (condition.getColumn2().equals(
									record.getFieldName())) {
								recordsForSlaveKeys.add(record);
								matchByMaster.add(record);
								for (Record record1 : masterTi.getRecords()) {
									if (condition.getColumn1().equals(
											record1.getFieldName())) {
										matchBySlave.add(record1);
									}
								}
								System.out.println(record.getFieldName());
							}
						}
					}
					slaveIndexesTableName = slaveTi.getTableName() + "_index";
					DataBaseTools.createTable(ApplicationData.getTempJDBC(),
							ApplicationData.getTempDataBaseInfo(),
							recordsForSlaveKeys, slaveIndexesTableName);
					selectLR = genrateSelectFromProject(LR, updateConditions,
							true, matchBy, masterTi);
					selectRL = genrateSelectFromProject(RL, updateConditions,
							false, matchBy, slaveTi);
					ResultSet masterRS = null;
					System.out.println("SelectLR = `" + selectLR + "`");
					Statement masterStmt = ApplicationData
							.getTempJDBCConnection().createStatement(
									ResultSet.TYPE_FORWARD_ONLY,
									ResultSet.CONCUR_UPDATABLE);
					masterRS = masterStmt.executeQuery(selectLR);
					ArrayList<Record> slaveFields = null;
					ArrayList<Record> masterFields = null;
					if (null != masterRS) {
						PreparedStatement selectFromSlave = ApplicationData
								.getTempJDBCConnection().prepareStatement(
										selectRL, ResultSet.TYPE_FORWARD_ONLY,
										ResultSet.CONCUR_UPDATABLE);
						masterFields = new ArrayList<Record>();
						for (Object node : LR.getNodes().values()) {
							if (!(node instanceof ColumnNode)) {
								continue;
							}
							ColumnNode colNode = (ColumnNode) node;
							if (!colNode.isInbound()) {
								masterFields.add(colNode.getRecord());
							}
						}
						slaveFields = new ArrayList<Record>();
						for (Object node : RL.getNodes().values()) {
							if (!(node instanceof ColumnNode)) {
								continue;
							}
							ColumnNode colNode = (ColumnNode) node;
							if (!colNode.isInbound()) {
								slaveFields.add(colNode.getRecord());
							}
						}

						String insertIntoSlaveQueryWithMatchBy = "insert into "
								+ slaveTi.getTableName() + " ( ";
						String insertIntoSlaveQueryWithMatchBy_values = " ) values (";
						String insertIntoSlaveQueryWithoutMatchBy = "insert into "
								+ slaveTi.getTableName() + " ( ";
						String insertIntoSlaveQueryWithoutMatchBy_values = " ) values (";
						List<String> columns = new ArrayList<String>();
						for (AbstractNode node : LRnodes.keySet()) {
							ColumnNode rightNode = LRnodes.get(node);
							if (!columns.contains(rightNode.getColumnName())) {
								insertIntoSlaveQueryWithMatchBy += "\""
										+ rightNode.getColumnName() + "\", ";
								insertIntoSlaveQueryWithMatchBy_values += "?, ";
								insertIntoSlaveQueryWithoutMatchBy += "\""
										+ rightNode.getColumnName() + "\", ";
								insertIntoSlaveQueryWithoutMatchBy_values += "?, ";
								columns.add(rightNode.getColumnName());
							}
						}

						for (Condition condition : matchBy) {
							if (!columns.contains(condition.getColumn2())) {
								insertIntoSlaveQueryWithMatchBy += "\""
										+ condition.getColumn2() + "\", ";
								insertIntoSlaveQueryWithMatchBy_values += "?, ";
								columns.add(condition.getColumn2());
							}
						}
						insertIntoSlaveQueryWithoutMatchBy = insertIntoSlaveQueryWithoutMatchBy
								.substring(0,
										insertIntoSlaveQueryWithoutMatchBy
												.length() - 2)
								+ insertIntoSlaveQueryWithoutMatchBy_values
										.substring(0,
												insertIntoSlaveQueryWithoutMatchBy_values
														.length() - 2) + ")";

						insertIntoSlaveQueryWithMatchBy = insertIntoSlaveQueryWithMatchBy
								.substring(0, insertIntoSlaveQueryWithMatchBy
										.length() - 2)
								+ insertIntoSlaveQueryWithMatchBy_values
										.substring(0,
												insertIntoSlaveQueryWithMatchBy_values
														.length() - 2) + ")";

						PreparedStatement insertIntoSlaveWithMatchBy = ApplicationData
								.getTempJDBCConnection().prepareStatement(
										insertIntoSlaveQueryWithMatchBy);
						PreparedStatement insertIntoSlaveWithoutMatchBy = ApplicationData
								.getTempJDBCConnection().prepareStatement(
										insertIntoSlaveQueryWithoutMatchBy,
										Statement.RETURN_GENERATED_KEYS);
						PreparedStatement insertIntoSlaveindexes = getPSForSlaveIndexes(
								recordsForSlaveKeys, slaveTi);
						System.out
								.println("insertIntoSlaveQueryWithMatchBy = `"
										+ insertIntoSlaveQueryWithMatchBy + "`");
						System.out
								.println("insertIntoSlaveQueryWithoutMatchBy = `"
										+ insertIntoSlaveQueryWithoutMatchBy
										+ "`");
						while (masterRS.next()) {
							for (int i = 0; i < matchBy.size(); i++) {
								int type = 0;
								for (Record record : masterTi.getRecords()) {
									if (record.getFieldName().equals(
											matchBy.get(i).getColumn1())) {
										type = record.getSqlType(true);
										break;
									}
								}
								DataBaseTools.setDataToPS(selectFromSlave,
										masterRS.getObject(matchBy.get(i)
												.getColumn1()), type, i + 1);
							}
							ResultSet slaveRS = null;
							slaveRS = selectFromSlave.executeQuery();
							if (slaveRS.next()) {
								// log record found to slaveIndexesTableName
								int i = 1;
								for (Record record2 : recordsForSlaveKeys) {
									String colName = null;
									for (Condition condition : matchBy) {
										if (condition.getColumn2().equals(
												record2.getFieldName())) {
											colName = condition.getColumn1();
											break;
										}
									}
									setDataToPS(insertIntoSlaveindexes,
											masterRS, record2, i, colName);
									i++;
								}
								insertIntoSlaveindexes.executeUpdate();
								// we should update record first decide which
								// side we should update: master or slave
								for (Object conditionsNode : updateConditions
										.getNodes().values()) {
									if (conditionsNode instanceof ColumnNode) {
										ColumnNode colNode = (ColumnNode) conditionsNode;
										if (!colNode.isInbound()) {
											Object obj = null;
											if (colNode
													.getConnectionName()
													.equals(
															LEFT_TO_RIGHT_CONN_POINT)) {
												obj = masterRS
														.getObject(colNode
																.getColumnName());
											} else {
												obj = slaveRS.getObject(colNode
														.getColumnName());
											}
											colNode.setResult(obj);
										}
									}
								}
								// execute the project
								com.apatar.core.Runnable rn = new com.apatar.core.Runnable();
								rn
										.execute(updateConditions.getNodes()
												.values());
								boolean isSlaveRecordShouldBeUpdated = calculateResult(updateConditions);
								if (isSlaveRecordShouldBeUpdated) {
									for (AbstractNode node : LRnodes.keySet()) {
										if (node instanceof ColumnNode) {
											ColumnNode colNode = (ColumnNode) node;
											Object obj = masterRS
													.getObject(colNode
															.getColumnName());
											colNode.setResult(obj);
										}
									}

									rn.execute(LR.getNodes().values());
									// here we have to update slaveRS
									for (AbstractNode node : LRnodes.keySet()) {
										ColumnNode rightNode = LRnodes
												.get(node);
										updateFieldInRS(slaveRS, rightNode
												.getColumnName(), rightNode
												.getResult(), rightNode
												.getRecord().getSqlType(true));
									}

									slaveRS.updateRow();
								} else {
									// master table must be updated.
									for (AbstractNode node : RLnodes.keySet()) {
										if (node instanceof ColumnNode) {
											ColumnNode colNode = (ColumnNode) node;
											colNode.setResult(slaveRS
													.getObject(colNode
															.getColumnName()));
										}
									}
									rn.execute(RL.getNodes().values());
									// here we have to update record in master
									// table
									for (AbstractNode node : RLnodes.keySet()) {
										ColumnNode rightNode = RLnodes
												.get(node);
										updateFieldInRS(masterRS, rightNode
												.getColumnName(), rightNode
												.getResult(), rightNode
												.getRecord().getSqlType(true));
									}

									masterRS.updateRow();
								}
							} else {
								// record not found, so it should be inserted to
								// slave
								for (AbstractNode node : LRnodes.keySet()) {
									if (node instanceof ColumnNode) {
										ColumnNode leftNode = (ColumnNode) node;
										leftNode.setResult(masterRS
												.getObject(leftNode
														.getColumnName()));
									}
								}
								com.apatar.core.Runnable rn = new com.apatar.core.Runnable();
								rn.execute(LR.getNodes().values());

								int i = 1;
								for (AbstractNode node : LRnodes.keySet()) {
									DataBaseTools.setDataToPS(
											insertIntoSlaveWithMatchBy, LRnodes
													.get(node).getResult(),
											LRnodes.get(node).getRecord()
													.getSqlType(true), i);
									DataBaseTools.setDataToPS(
											insertIntoSlaveWithoutMatchBy,
											LRnodes.get(node).getResult(),
											LRnodes.get(node).getRecord()
													.getSqlType(true), i);
									i++;
								}

								for (Condition condition : matchBy) {
									for (Record record2 : masterTi.getRecords()) {
										if (i > insertIntoSlaveWithMatchBy
												.getParameterMetaData()
												.getParameterCount()) {
											break;
										}
										if (record2.getFieldName().equals(
												condition.getColumn1())) {
											setDataToPS(
													insertIntoSlaveWithMatchBy,
													masterRS, record2, i,
													condition.getColumn1());
											break;
										}
									}
									i++;
								}

								try {
									insertIntoSlaveWithMatchBy.executeUpdate();
								} catch (Exception e) {
									System.err
											.println("Error writing to slave with mathBy fields. Error message: "
													+ e.getMessage());

								}
							}
						}
						// lets find records in slave which not present in
						// master
						String joinSql = "select s.* from "
								+ slaveTi.getTableName() + " s " + "left join "
								+ slaveIndexesTableName + " i on ";
						for (Condition condition : matchBy) {
							joinSql += "i.\"" + condition.getColumn2()
									+ "\"=s.\"" + condition.getColumn2()
									+ "\" and ";
						}
						joinSql = joinSql.substring(0, joinSql.length() - 4)
								+ " where i.\""
								+ ((Condition) matchBy.toArray()[0])
										.getColumn2() + "\" is null";
						System.out.println(joinSql);
						ResultSet forInsertIntoMaster = ApplicationData
								.getTempJDBCConnection().createStatement(
										ResultSet.TYPE_FORWARD_ONLY,
										ResultSet.CONCUR_UPDATABLE)
								.executeQuery(joinSql);
						if (forInsertIntoMaster != null) {
							PreparedStatement insertIntoMaster = ApplicationData
									.getTempJDBCConnection().prepareStatement(
											getInsertToMasterQuery(masterTi,
													RLnodes, matchBy),
											Statement.RETURN_GENERATED_KEYS);
							PreparedStatement insertIntoMasterWithoutMatchBy = ApplicationData
									.getTempJDBCConnection().prepareStatement(
											getInsertToMasterQuery(masterTi,
													RLnodes, null),
											Statement.RETURN_GENERATED_KEYS);
							while (forInsertIntoMaster.next()) {
								for (AbstractNode node : RLnodes.keySet()) {
									if (node instanceof ColumnNode) {
										ColumnNode leftNode = (ColumnNode) node;
										leftNode.setResult(forInsertIntoMaster
												.getObject(leftNode
														.getColumnName()));
									}
								}
								com.apatar.core.Runnable rn = new com.apatar.core.Runnable();
								rn.execute(RL.getNodes().values());
								int i = 1;
								for (AbstractNode node : RLnodes.keySet()) {
									ColumnNode rightNode = RLnodes.get(node);
									DataBaseTools.setDataToPS(insertIntoMaster,
											rightNode.getResult(), rightNode
													.getRecord().getSqlType(
															true), i);
									DataBaseTools.setDataToPS(
											insertIntoMasterWithoutMatchBy,
											rightNode.getResult(), rightNode
													.getRecord().getSqlType(
															true), i);
									i++;
								}

								for (Condition condition : matchBy) {
									for (Record record2 : masterTi.getRecords()) {
										if (record2.getFieldName().equals(
												condition.getColumn1())) {
											try {
												setDataToPS(insertIntoMaster,
														forInsertIntoMaster,
														record2, i, condition
																.getColumn2());
											} catch (Exception e) {
												// e.printStackTrace();
											}
											break;
										}
									}
									i++;
								}
								try {
									insertIntoMaster.execute();
								} catch (RuntimeException e) {

								}
							}
						}
					}
					List<String> identificationFields = new ArrayList<String>();
					for (Condition cnd : matchBy) {
						identificationFields.add(cnd.getColumn1());
					}
					TableInfo mTi = new TableInfo();
					mTi.setTableName(masterTi.getTableName());
					mTi.setSchemaTable(new SchemaTable(masterFields));
					DataBaseTools.completeTransfer();
					masterNode
							.moveDataFromTempToReal(identificationFields, mTi);
					identificationFields.clear();
					for (Condition cnd : matchBy) {
						identificationFields.add(cnd.getColumn2());
					}
					TableInfo sTi = new TableInfo();
					sTi.setTableName(slaveTi.getTableName());
					for (AbstractNode node : LR.getNodes().values()) {
						if (node instanceof ColumnNode) {
							ColumnNode colNode = (ColumnNode) node;
							if (colNode.isInbound()) {
								sTi.getSchemaTable().addRecord(
										colNode.getRecord());
							}
						}
					}
					DataBaseTools.completeTransfer();
					slaveNode.moveDataFromTempToReal(identificationFields, sTi);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.err
						.println("Update node should be properly configured.");
			}

		} else {
			System.err.println("Both input nodes have to be synchronyzable");
		}
		DataBaseTools.completeTransfer();
	}

	private String getInsertToMasterQuery(TableInfo masterTi,
			HashMap<AbstractNode, ColumnNode> nodes, List<Condition> matchBy) {
		String res = "insert into " + masterTi.getTableName() + " (";
		String values = " ) values ( ";

		List<String> columns = new ArrayList<String>();
		for (AbstractNode node : nodes.keySet()) {
			if (!columns.contains(nodes.get(node).getColumnName())) {
				res += "\"" + nodes.get(node).getColumnName() + "\", ";
				values += "?, ";
				columns.add(nodes.get(node).getColumnName());
			}
		}
		if (null != matchBy) {
			for (Condition condition : matchBy) {
				if (!columns.contains(condition.getColumn1())) {
					res += "\"" + condition.getColumn1() + "\", ";
					values += "?, ";
					columns.add(condition.getColumn1());
				}
			}
		}
		res = res.substring(0, res.length() - 2)
				+ values.substring(0, values.length() - 2) + ")";
		System.out.println(res);
		return res;
	}

	private PreparedStatement getPSForSlaveIndexes(List<Record> records,
			TableInfo slaveTi) {
		try {
			String sql = "insert into " + slaveIndexesTableName + " (";
			String valuesString = " values (";
			for (Record record : records) {
				sql += "\"" + record.getFieldName() + "\", ";
				valuesString += "?, ";
			}
			sql = sql.substring(0, sql.length() - 2);
			valuesString = valuesString.substring(0, valuesString.length() - 2);
			sql += ") " + valuesString + ")";
			return ApplicationData.getTempJDBCConnection()
					.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void updateFieldInRS(ResultSet destRs, ResultSet srcRs,
			String destColumnName, String srcColumnName, int columnType)
			throws Exception {
		switch (columnType) {
		case Types.BIGINT:
		case Types.NUMERIC:
			destRs.updateLong(destColumnName, srcRs.getLong(srcColumnName));
			break;

		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
			destRs.updateString(destColumnName, srcRs.getString(srcColumnName));

			break;

		case Types.DATE:
			destRs.updateDate(destColumnName, srcRs.getDate(srcColumnName));
			break;

		case Types.TIME:
			destRs.updateTime(destColumnName, srcRs.getTime(srcColumnName));

			break;

		case Types.TIMESTAMP:
			destRs.updateTimestamp(destColumnName, srcRs
					.getTimestamp(srcColumnName));

			break;

		case Types.BOOLEAN:
			destRs.updateBoolean(destColumnName, srcRs
					.getBoolean(srcColumnName));

			break;

		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.REAL:
			destRs.updateDouble(destColumnName, srcRs.getDouble(srcColumnName));

			break;

		case Types.FLOAT:
			destRs.updateFloat(destColumnName, srcRs.getFloat(srcColumnName));

			break;

		case Types.INTEGER:
			destRs.updateInt(destColumnName, srcRs.getInt(srcColumnName));

			break;

		case Types.NULL:
			destRs.updateNull(destColumnName);

			break;

		case Types.REF:
			destRs.updateRef(destColumnName, srcRs.getRef(srcColumnName));

			break;

		case Types.SMALLINT:
		case Types.TINYINT:
			destRs.updateShort(destColumnName, srcRs.getShort(srcColumnName));

			break;

		}
	}

	private void updateFieldInRS(ResultSet destRs, String destColumnName,
			Object value, int columnType) throws Exception {
		if (value == null) {
			System.out.println("value is null");
			destRs.updateNull(destColumnName);
			return;
		}
		System.out.println("value class is `" + value.getClass().toString()
				+ "`");
		switch (columnType) {
		case Types.BIGINT:
		case Types.NUMERIC:
			try {
				destRs.updateLong(destColumnName, (Long) value);
			} catch (ClassCastException e) {
				if (value instanceof Number) {
					if (value instanceof Short) {
						destRs.updateLong(destColumnName, ((Short) value)
								.longValue());
					}
					if (value instanceof Integer) {
						destRs.updateLong(destColumnName, ((Integer) value)
								.longValue());
					}
					if (value instanceof BigInteger) {
						destRs.updateLong(destColumnName, ((BigInteger) value)
								.longValue());
					}
					if (value instanceof BigDecimal) {
						destRs.updateLong(destColumnName, ((BigDecimal) value)
								.longValue());
					}
					if (value instanceof Byte) {
						destRs.updateLong(destColumnName, ((Byte) value)
								.longValue());
					}
				} else {
					throw e;
				}
			}
			break;

		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
			destRs.updateString(destColumnName, (String) value);

			break;

		case Types.DATE:
			if (value instanceof Timestamp) {
				destRs.updateTimestamp(destColumnName, (Timestamp) value);
			} else {
				destRs.updateDate(destColumnName, (Date) value);
			}
			break;

		case Types.TIME:
			destRs.updateTime(destColumnName, (Time) value);

			break;

		case Types.TIMESTAMP:
			destRs.updateTimestamp(destColumnName, (Timestamp) value);

			break;

		case Types.BOOLEAN:
			if (value instanceof Number) {
				if (((Number) value).intValue() != 0) {
					destRs.updateBoolean(destColumnName, true);
				} else {
					destRs.updateBoolean(destColumnName, false);
				}
			} else {
				destRs.updateBoolean(destColumnName, (Boolean) value);
			}

			break;

		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.REAL:
			destRs.updateDouble(destColumnName, (Double) value);

			break;

		case Types.FLOAT:
			destRs.updateFloat(destColumnName, (Float) value);

			break;

		case Types.INTEGER:
			destRs.updateInt(destColumnName, (Integer) value);

			break;

		case Types.NULL:
			destRs.updateNull(destColumnName);

			break;

		case Types.REF:
			destRs.updateNull(destColumnName);

			break;

		case Types.SMALLINT:
		case Types.TINYINT:
			destRs.updateShort(destColumnName, (Short) value);

			break;

		}
	}

	private void setDataToPS(PreparedStatement ps, ResultSet rs, Record record,
			int i, String colName) throws Exception {
		if (null == colName) {
			colName = record.getFieldName();
		}
		switch (record.getSqlType(true)) {
		case Types.BIGINT:
		case Types.NUMERIC:
			ps.setLong(i, rs.getLong(colName));
			break;

		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
			ParameterMetaData pms = ps.getParameterMetaData();
			pms.getParameterCount();
			ps.setString(i, rs.getString(colName));

			break;

		case Types.DATE:
			ps.setDate(i, rs.getDate(colName));
			break;

		case Types.TIME:
			ps.setTime(i, rs.getTime(colName));

			break;

		case Types.TIMESTAMP:
			ps.setTimestamp(i, rs.getTimestamp(colName));

			break;

		case Types.BOOLEAN:
			ps.setBoolean(i, rs.getBoolean(colName));

			break;

		case Types.DECIMAL:
			ps.setBigDecimal(i, rs.getBigDecimal(colName));

			break;

		case Types.DOUBLE:
		case Types.REAL:
			ps.setDouble(i, rs.getDouble(colName));

			break;

		case Types.FLOAT:
			ps.setFloat(i, rs.getFloat(colName));

			break;

		case Types.INTEGER:
			ps.setInt(i, rs.getInt(colName));

			break;

		case Types.NULL:
			ps.setNull(i, Types.NULL);

			break;

		case Types.REF:
			ps.setRef(i, rs.getRef(colName));

			break;

		case Types.SMALLINT:
		case Types.TINYINT:
			ps.setShort(i, rs.getShort(colName));

			break;

		}
	}

	private KeyInsensitiveMap getFieldsFromProject(Project master,
			Project conditions, boolean getLeftSideConditions,
			List<Condition> matchBy, TableInfo ti) {
		KeyInsensitiveMap map = new KeyInsensitiveMap();
		for (Object node : master.getNodes().values()) {
			if (!(node instanceof ColumnNode)) {
				continue;
			}
			ColumnNode colNode = (ColumnNode) node;
			if (!colNode.isInbound()) {
				map.put(colNode.getColumnName(), colNode.getColumnName());
			}
		}
		for (Object node : conditions.getNodes().values()) {
			if (!(node instanceof ColumnNode)) {
				continue;
			}
			ColumnNode colNode = (ColumnNode) node;
			if (getLeftSideConditions) {
				if (!colNode.isInbound()) {
					map.put(colNode.getColumnName(), colNode.getColumnName());
				}
			} else {
				if (colNode.isInbound()) {
					map.put(colNode.getColumnName(), colNode.getColumnName());
				}
			}
		}
		if (null != matchBy) {
			if (getLeftSideConditions) {
				for (Condition condition : matchBy) {
					map.put(condition.getColumn1(), condition.getColumn1());
				}
			}
			if (!getLeftSideConditions) {
				for (Condition condition : matchBy) {
					map.put(condition.getColumn1(), condition.getColumn1());
				}
			}
		}
		return map;
	}

	private String genrateSelectFromProject(Project master, Project conditions,
			boolean getLeftSideConditions, List<Condition> matchBy, TableInfo ti) {
		String select = "select ";
		for (Object node : master.getNodes().values()) {
			if (!(node instanceof ColumnNode)) {
				continue;
			}
			ColumnNode colNode = (ColumnNode) node;
			if (!colNode.isInbound()) {
				select += "\"" + colNode.getRecord().getFieldName() + "\", ";
			}
		}
		for (Object node : conditions.getNodes().values()) {
			if (!(node instanceof ColumnNode)) {
				continue;
			}
			ColumnNode colNode = (ColumnNode) node;
			if (getLeftSideConditions) {
				if (!colNode.isInbound()
						&& (colNode.getConnectionName()
								.equals(LEFT_TO_RIGHT_CONN_POINT))) {
					select += "\"" + colNode.getColumnName() + "\", ";
				}
			} else {
				if (!colNode.isInbound()
						&& (colNode.getConnectionName()
								.equals(RIGHT_TO_LEFT_CONN_POINT))) {
					select += "\"" + colNode.getColumnName() + "\", ";
				}
			}
		}
		if (getLeftSideConditions) {
			int i = 0;
			for (Condition condition : matchBy) {
				i++;
				select += "\"" + condition.getColumn1() + "\"";
				if (i < matchBy.size()) {
					select += ", ";
				}
			}
		} else {
			select = select.substring(0, select.length() - 2);
		}
		// select += " from " + ti.getTableName() + " ";
		select = "select * from " + ti.getTableName() + " ";
		// select = "select * from " + ti.getTableName();
		if (!getLeftSideConditions) {
			select += " where ";
			for (Condition condition : matchBy) {
				select += "\"" + condition.getColumn2() + "\" = ? and ";
			}
			select = select.substring(0, select.length() - 4);
		}

		return select;
	}

	@Override
	public TableInfo getDebugTableInfo() {
		// get this node output table
		return null;
	}

	public List<Record> getInputs() {
		HashSet<String> names = new HashSet<String>();
		TableInfo ti = AbstractNode
				.getOtherSideTableInfo(getConnPoint(UpdateNode.LEFT_TO_RIGHT_CONN_POINT));
		List<Record> recs1 = null;
		List<Record> recs2 = null;
		if (ti != null) {
			recs1 = ti.getRecords();
		}
		ti = AbstractNode
				.getOtherSideTableInfo(getConnPoint(UpdateNode.RIGHT_TO_LEFT_CONN_POINT));
		if (ti != null) {
			recs2 = AbstractNode.getOtherSideTableInfo(
					getConnPoint(UpdateNode.RIGHT_TO_LEFT_CONN_POINT))
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

	public void clearMatchByConditions() {
		matchByConditions.clear();
	}

	public void addMatchBy(Condition matchByCondition) {
		matchByConditions.add(matchByCondition);
	}

	public void removeCondition(Condition matchByCondition) {
		matchByConditions.remove(matchByCondition);
	}

	/**
	 * @return the matchByConditions
	 */
	public List<Condition> getMatchByConditions() {
		return matchByConditions;
	}

	/**
	 * @param matchByConditions
	 *            the matchByConditions to set
	 */
	public void setMatchByConditions(List<Condition> matchByConditions) {
		this.matchByConditions = matchByConditions;
	}

}
