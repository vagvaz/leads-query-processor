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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.apatar.ui.wizard.Wizard;

public abstract class AbstractDataBaseNode extends OperationalNode  /*
																	 * implements
																	 * DataBase
																	 */{

	public static final String OUT_CONN_POINT_NAME = "output";
	public static final String IN_CONN_POINT_NAME = "input";

	public static int INSERT_MODE = 0;
	public static int UPDATE_MODE = 1;
	public static int SYNCHRONIZE_MODE = 2;
	public static int DELETE_MODE = 3;

	protected int mode = INSERT_MODE;

	boolean deleteAllInRDB = false;

	public int counter = 0;

	protected RDBTable table = new RDBTable("", ETableMode.ReadWrite);

	// information how to connection to the data source
	protected long connectionDataId = -1;

	protected Element bakupProjectData;

	protected List<String> identificationFields = new ArrayList<String>();

	protected ArrayList<SynchronizationRecord> syncRecords = new ArrayList<SynchronizationRecord>();

	public AbstractDataBaseNode() {
		super();
		outputConnectionList.put(new ConnectionPoint(OUT_CONN_POINT_NAME,
				false, this, true), new TableInfo());
		inputConnectionList.add(new ConnectionPoint(IN_CONN_POINT_NAME, true,
				this, false));
	}

	public abstract DataBaseInfo getDataBaseInfo();

	public void setConnectionToNull() {
	};

	/**
	 * Returns only mapped fields if connected node is instance of DataTransNode
	 * (Transform etc.) Otherwise it returns all fields
	 * 
	 * @return
	 */
	public List<Record> getColumnsForUpdate() {

		List<Record> outputRecords = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable().getRecords();
		ConnectionPoint cp = getConnPoint(IN_CONN_POINT_NAME);
		if (cp != null) {
			List<Record> inputRecords = new ArrayList<Record>();
			List<Connector> connectors = cp.getConnectors();
			Connector leftConnector;
			try {
				leftConnector = (Connector) connectors.toArray()[0];
			} catch (Exception e) {
				return outputRecords;
			}
			if (leftConnector.getBegin().getNode() instanceof DataTransNode) {
				DataTransNode dataTransNode = (DataTransNode) leftConnector
						.getBegin().getNode();
				if (dataTransNode.prj == null) {
					return outputRecords;
				}

				for (Object node : dataTransNode.prj.getNodes().values()) {
					if (node instanceof ColumnNode) {
						ColumnNode colNode = (ColumnNode) node;
						if (colNode.isInbound()) {
							for (Record record : outputRecords) {
								if (record.getFieldName().equals(
										colNode.getRecord().getFieldName())) {
									inputRecords.add(colNode.getRecord());
									break;
								}
							}
						}
					}
				}
			} else {
				return outputRecords;
			}

			// AbstractNode prevNode = getBegin().getNode();

			return inputRecords;
		} else {
			return outputRecords;
		}
	}

	@Override
	public void beforeEdit() {
		if (connectionDataId < 0) {
			createProjectData();
		}
		IPersistent persistent = (ApplicationData.getProject()
				.getProjectData(connectionDataId));
		if (persistent != null) {
			bakupProjectData = persistent.saveToElement();
		} else {

		}
	}

	abstract public int executeUpdateQuery(String query);

	abstract public int getTotalRecodrsCount(TableInfo ti);

	abstract public int getTotalRecodrsCount(TableInfo ti, JdbcParams params);

	void createProjectData() {
		ProjectData pd = new ProjectData();
		pd.setName("default connection");
		connectionDataId = pd.getId();
		ApplicationData.getProject().addProjectData(pd);
	}

	@Override
	public void afterEdit(boolean resultEdit, AbstractApatarActions actions) {
		try {
			if (resultEdit) {
				createSchemaTable(actions);
			} else {
				IPersistent persistent = (ApplicationData.getProject()
						.getProjectData(connectionDataId));
				if (persistent != null) {
					(persistent).initFromElement(bakupProjectData);
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.updateRecords(ApplicationData.convertToTempDbType(st.getRecords()));

		// TODO - make an UI update from here
		if (table != null) {
			title = table.toString();
		}
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		return actions.customDatabaseNodeAction();
	}

	// save node
	@Override
	public Element saveToElement() {
		Element readNode = super.saveToElement();

		if (table != null) {
			readNode.addContent(table.saveToElement());
		}

		Element modeElement = new Element("mode");
		modeElement.setText("" + mode);
		readNode.addContent(modeElement);

		Element delElement = new Element("isDelete");
		delElement.setText("" + deleteAllInRDB);
		readNode.addContent(delElement);

		Element conIDElement = new Element("conID");
		conIDElement.setText("" + connectionDataId);
		readNode.addContent(conIDElement);

		if (identificationFields != null) {
			Element identFields = new Element("iFields");
			readNode.addContent(identFields);
			for (Object element : identificationFields) {
				Element field = new Element("field");
				field.setText(element.toString());
				identFields.addContent(field);
			}
		}

		return readNode;
	}

	// load node
	@Override
	public void initFromElement(Element node) {
		if (node == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
		super.initFromElement(node);

		PersistentUtils.InitObjectFromChild(table, node, false);

		String text = node.getChildText("mode");
		if (text != null) {
			mode = Integer.parseInt(text);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		text = node.getChildText("isDelete");
		if (text != null) {
			deleteAllInRDB = Boolean.parseBoolean(text);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		text = node.getChildText("conID");
		if (text != null) {
			connectionDataId = Long.parseLong(text);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}

		Element iField = node.getChild("iFields");
		if (iField != null) {
			List iFields = iField.getChildren();
			if (iFields != null || iFields.size() > 0) {
				identificationFields.clear();
				for (Iterator it = node.getChild("iFields").getChildren()
						.iterator(); it.hasNext();) {
					identificationFields.add(((Element) it.next()).getText());
				}
			} else {
				ApplicationData.COUNT_INIT_ERROR++;
			}
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
	}

	public abstract void createDatabaseParam(Wizard wizard);

	public abstract void createSchemaTable(AbstractApatarActions actions)
			throws Exception;

	public void setConnectionDataID(long id) {
		connectionDataId = id;
	}

	public long getConnectionDataID() {
		return connectionDataId;
	}

	public void setTable(RDBTable table) {
		// database node default name is the table name
		this.table = table;
	}

	public RDBTable getTable() {
		return table;
	}

	public String getTableName() {
		return table.getTableName();
	}

	@Override
	public TableInfo getDebugTableInfo() {
		// get this node output table
		return (TableInfo) outputConnectionList.values().toArray()[0];
	}

	public boolean isDeleteAllInRDB() {
		return deleteAllInRDB;
	}

	public void setDeleteAllInRDB(boolean deleteAllInRDB) {
		this.deleteAllInRDB = deleteAllInRDB;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public List<String> getIdentificationFields() {
		return identificationFields;
	}

	public void setIdentificationFields(List<String> identificationFields) {
		this.identificationFields.clear();
		this.identificationFields.addAll(identificationFields);
	}

	public void Transform(boolean readLastNodeData) {
		this.readLastNodeData = readLastNodeData;
		Transform();
	}

	// this method executes depending on
	// whether there is input connections or not
	// whether there is output connections or not
	@Override
	public void Transform() {
		if (getTiForConnection(IN_CONN_POINT_NAME) != null) {
			if (deleteAllInRDB) {
				ApplicationData.ProcessingProgress.Log("Erasing Data");
				try {
					deleteAllRecordsInRDB();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String messageMode = "Unknown mode";
			if (AbstractDataBaseNode.INSERT_MODE == mode) {
				messageMode = "Insert Mode";
			} else if (AbstractDataBaseNode.UPDATE_MODE == mode) {
				messageMode = "Update Mode";
			} else if (AbstractDataBaseNode.SYNCHRONIZE_MODE == mode) {
				messageMode = "Synchronize Mode";
			} else if (AbstractDataBaseNode.DELETE_MODE == mode) {
				messageMode = "Delete Mode";
			}
			ApplicationData.ProcessingProgress.Log("Start Writing to "
					+ getTableName());
			ApplicationData.ProcessingProgress.Log(messageMode);
			TransformTDBtoRDB(mode);
		}
		// read from real database into temporary in any case (even if there is
		// no output connectors);
		if (getTiForConnection(OUT_CONN_POINT_NAME) != null) {
			runReadRDBtoTDB();
		} else {
			if (readLastNodeData) {
				runReadRDBtoTDB();
			}
		}
	}

	private void runReadRDBtoTDB() {
		System.err.println("Start Reading from " + getTableName());
		ApplicationData.ProcessingProgress.Log("Start Reading from "
				+ getTableName());
		TransformRDBtoTDB();
	}

	/**
	 * method to use in Update (bidirectional update) operation
	 * 
	 * @param identificationFields
	 * @param inputTi
	 */
	public abstract void moveDataFromTempToReal(
			List<String> identificationFields, TableInfo inputTi);

	/*
	 * Transformation from Temp DB to Real DB
	 */
	protected abstract void TransformTDBtoRDB(int mode);

	/*
	 * Transformation from Real DB to Temp DB
	 */
	protected abstract void TransformRDBtoTDB();

	public abstract List<RDBTable> getTableList() throws Exception;

	public abstract List<Record> getFieldList(AbstractApatarActions action)
			throws Exception;

	public abstract void deleteAllRecordsInRDB() throws Exception;

	public void deleteRecordsInRDB(SynchronizationRecord rec) throws Exception {
	};

	@Override
	public SchemaTable getExpectedShemaTable() {
		return getTiForConnection(OUT_CONN_POINT_NAME).getSchemaTable();
	}

	public void removeInputConnectionPoints() {
		inputConnectionList.clear();
	}

	public void addSynchronizationRecord(SynchronizationRecord rec) {
		syncRecords.add(rec);
	}

	public void clearSynchronizationRecord() {
		syncRecords.clear();
	}

	public void removeField(SynchronizationRecord rec) {
		syncRecords.remove(rec);
	}

	protected boolean addSynchronizationRecord(ResultSet rs)
			throws SQLException {
		SynchronizationRecord rec = new SynchronizationRecord();
		for (String field : identificationFields) {
			Object obj = rs.getObject(field);
			if (obj == null) {
				return false;
			}
			rec.addField(new SynchronizationField(field, obj));
		}
		syncRecords.add(rec);
		return true;
	}

	protected void synchronization(List<SynchronizationRecord> srsReal,
			List<SynchronizationRecord> srs, List<String> identif)
			throws Exception {
		for (SynchronizationRecord srReal : srsReal) {
			if (!SynchronizationRecord.isPresent(srs, srReal, identif)) {
				deleteRecordsInRDB(srReal);
			}
		}
	}

	public boolean validateConnectionData() {
		return true;
	}

}
