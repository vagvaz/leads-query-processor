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

package com.apatar.editgrid;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.jdom.Element;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApatarException;
import com.apatar.core.ApplicationData;
import com.apatar.core.Base64;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.Connector;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataDirection;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.JdbcParams;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.editgrid.ui.WorkbookListDescriptor;
import com.apatar.editgrid.ui.WorksheetDescriptor;
import com.apatar.editgrid.ui.WorksheetListDescriptor;
import com.apatar.editgrid.ws.AuthCreateSessionKeyRequest;
import com.apatar.editgrid.ws.Binary;
import com.apatar.editgrid.ws.Cell;
import com.apatar.editgrid.ws.CellListRequest;
import com.apatar.editgrid.ws.CellSetRequest;
import com.apatar.editgrid.ws.EditGridService;
import com.apatar.editgrid.ws.EditGridServiceLocator;
import com.apatar.editgrid.ws.EditGridServicePort_PortType;
import com.apatar.editgrid.ws.Workbook;
import com.apatar.editgrid.ws.WorkbookCreateRequest;
import com.apatar.editgrid.ws.WorkbookGetRequest;
import com.apatar.editgrid.ws.WorkbookImportRequest;
import com.apatar.editgrid.ws.WorkbookListRequest;
import com.apatar.editgrid.ws.Worksheet;
import com.apatar.editgrid.ws.WorksheetListRequest;
import com.apatar.editgrid.ws.Workspace;
import com.apatar.editgrid.ws.WorkspaceGetRequest;
import com.apatar.filesystem.FileParams;
import com.apatar.filesystem.FileSystemNode;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class EditgridNode extends AbstractNonJdbcDataBaseNode {

	private JDialog wizardDialog = null;
	private String sessionKey = null;
	private EditGridServicePort_PortType editGridPort = null;
	private Workspace egWorkspace = null;
	private Workbook egWorkbook = null;
	private Worksheet egWorksheet = null;
	private RDBTable workbookTable = null;
	private RDBTable worksheetTable = null;
	private boolean skipReadOnInsert = false;

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			true, true, true, true, false);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList
				.add(new DBTypeRecord(ERecordType.Text, "ID", 1, 255, true,
						true));
		rcList.add(new DBTypeRecord(ERecordType.Text, "USER_NAME", 1, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "NAME", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "ASSIGNED_USER_NAME", 1,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "BOOL", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "RELATE", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "ASSIGNED_USER_NAME", 1,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "ENUM", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 1, 32672,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "TEXT", 1, 65000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "LONGTEXT", 1, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Time, "TIME", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "NUM", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "PHONE", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "EMAIL", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "DATETIME", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "TINYINT", 2, 2, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 4, 4, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SHORT", 4, 4, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "FLOAT", 4, 4, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "TEAM_LIST", 1, 255,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 8, 8, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB", 8, 8, true,
				false));
	}

	public EditgridNode() {
		super();
		title = "EditGrid";
	}

	private EditGridServicePort_PortType getEditGridPort() throws Exception {
		if (null == editGridPort) {

			EditGridService service = new EditGridServiceLocator();
			editGridPort = service.getEditGridServicePort();
		}
		return editGridPort;
	}

	private void createSessionKey() throws Exception {

		sessionKey = null;
		EditgridConnection conn = (EditgridConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();

		String organization = (conn.getOrganization().equals("") ? "user"
				: conn.getOrganization());
		AuthCreateSessionKeyRequest sessionKey = new AuthCreateSessionKeyRequest(
				conn.getAppKey(), organization + "/" + conn.getUserName(), conn
						.getPassword().getValue(), 0, 0, 0, 0);
		this.sessionKey = getEditGridPort().doAuthCreateSessionKey(sessionKey);
	}

	private Object getBinding(boolean forceGetNewSessionKey) {

		try {
			if (forceGetNewSessionKey || sessionKey == null) {
				createSessionKey();
				System.out.println("Login successfull: " + sessionKey);
			} else {
				System.out.println("Already logged in.");
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(),
					"Error logging in", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		return sessionKey;
	}

	@Override
	public ImageIcon getIcon() {
		return EditgridUtils.READ_EDITGRID_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		wizardDialog = wizard.getDialog();
		wizardDialog.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptorCredentials = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wizardDialog),
					WorkbookListDescriptor.IDENTIFIER, Class
							.forName("com.apatar.editgrid.EditgridConnection"),
					"db_connector", "editgrid");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptorCredentials);

			WorkbookListDescriptor workbookListPanel = new WorkbookListDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					WorksheetListDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(WorkbookListDescriptor.IDENTIFIER,
					workbookListPanel);

			WizardPanelDescriptor descriptorSheets = new WorksheetListDescriptor(
					this, WorkbookListDescriptor.IDENTIFIER,
					WorksheetDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(WorksheetListDescriptor.IDENTIFIER,
					descriptorSheets);

			WizardPanelDescriptor descriptorFields = new WorksheetDescriptor(
					this, WorksheetListDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(WorksheetDescriptor.IDENTIFIER,
					descriptorFields);

			wizard.setKeyForReferringToDescription("help.connector.editgrid");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			wizard.showModalDialog();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Element saveToElement() {
		Element el = super.saveToElement();
		try {
			el.addContent(workbookTable.saveToElement());
			el.addContent(worksheetTable.saveToElement());
		} catch (RuntimeException e) {
		}
		el.addContent(new Element("isSkipReadOnInsert").addContent(String
				.valueOf(isSkipReadOnInsert())));
		return el;
	}

	@Override
	public void initFromElement(Element node) {
		super.initFromElement(node);
		try {
			setSkipReadOnInsert(Boolean.valueOf(node
					.getChildText("isSkipReadOnInsert")));
		} catch (Exception e) {
			setSkipReadOnInsert(false);
		}
		Element wb = node.getChild("com.apatar.editgrid.EgWorkbook");
		workbookTable = new EgWorkbook("", ETableMode.ReadWrite);
		workbookTable.setTableName(wb.getAttributeValue("tableName"));
		String mode = wb.getAttributeValue("mode");
		if ("ReadWrite".equals(mode)) {
			workbookTable.setMode(ETableMode.ReadWrite);
		} else if ("ReadOnly".equals(mode)) {
			workbookTable.setMode(ETableMode.ReadOnly);
		} else {
			workbookTable.setMode(ETableMode.WriteOnly);
		}
		Element ws = node.getChild("com.apatar.editgrid.EgWorksheet");
		worksheetTable = new EgWorksheet("", ETableMode.ReadWrite);
		worksheetTable.setTableName((null == ws ? "" : ws
				.getAttributeValue("tableName")));
		if (table == null) {
			table = new RDBTable(worksheetTable.getTableName(), worksheetTable
					.getMode());
		} else {
			if (!"".equals(worksheetTable.getTableName())) {
				table.setMode(worksheetTable.getMode());
				table.setTableName(worksheetTable.getTableName());
			} else {
				if ("".equals(table.getTableName())) {
					throw new RuntimeException(
							"Cannot read sheet name from datamap. Configure EditGrid connector manually.");
				} else {
					worksheetTable.setMode(table.getMode());
					worksheetTable.setTableName(table.getTableName());
				}
			}
		}
		mode = ws.getAttributeValue("mode");
		if ("ReadWrite".equals(mode)) {
			worksheetTable.setMode(ETableMode.ReadWrite);
		} else if ("ReadOnly".equals(mode)) {
			worksheetTable.setMode(ETableMode.ReadOnly);
		} else {
			worksheetTable.setMode(ETableMode.WriteOnly);
		}
	}

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		if (connectionDataId == -1) {
			return;
		}

		// binding can be empty
		if (null == getBinding(false)) {
			return;
		}

		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.updateRecords(getFieldList(null));
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
		ConnectionPoint cp = getConnPoint(IN_CONN_POINT_NAME);
		if (cp != null) {
			List<Connector> connectors = cp.getConnectors();
			Connector leftConnector = (Connector) connectors.toArray()[0];
			EditgridConnection connection = (EditgridConnection) ApplicationData
					.getProject().getProjectData(connectionDataId).getData();
			if (leftConnector.getBegin().getNode() instanceof FileSystemNode) {
				// here we import files from FileSystemNode

				TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
				ResultSet rs;
				try {
					rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
							ApplicationData.getTempJDBC(), ApplicationData
									.getTempDataBaseInfo());
					String pathToDir = ((FileParams) ApplicationData
							.getProject().getProjectData(
									((FileSystemNode) leftConnector.getBegin()
											.getNode()).getConnectionDataID())
							.getData()).getDirectory().getPath();
					while (rs.next()) {
						try {
							doImportWorkbook(pathToDir + rs.getString("Path"),
									rs.getString("Name"));
						} catch (Exception e) {
							e.printStackTrace();
						}
						ApplicationData.ProcessingProgress.Step();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (!"".equals(connection.getDocToImport().getPath())) {
				// here we upload one file to EditGrid
				try {
					doImportWorkbook(connection.getDocToImport().getPath(),
							connection.getDocToImport().getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// here we upload only data on the sheet
				TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
				int tempCount = 0;
				try {
					tempCount = DataBaseTools.getRScount(ti.getTableName(),
							ApplicationData.getTempJDBC(), ApplicationData
									.getTempDataBaseInfo());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (tempCount == 0) {
					return;
				}
				if (AbstractDataBaseNode.INSERT_MODE == mode) {
					insertTDBtoRDB(ti, tempCount);
				} else {
					updateTDBtoRDB(ti, tempCount);
				}
			}
		}
		DataBaseTools.completeTransfer();
	}

	private void doImportWorkbook(String pathToFile, String docName)
			throws Exception {
		EditgridConnection connection = (EditgridConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();

		if (!pathToFile.endsWith(".xls")) {
			throw new ApatarException(
					"EditGrid connector says: this file is not Excel "
							+ "document. File path is: `" + pathToFile + "`");
		}
		String pathToDoc = "/"
				+ (connection.getOrganization().equals("") ? "user"
						: connection.getOrganization()) + "/"
				+ connection.getUserName() + "/" + docName;
		if (!connection.getOverwriteDocOnImport()) {
			// we have to check if document already exists
			// if yes - we must skip importing it
			WorkbookGetRequest wbGet = new WorkbookGetRequest(getSessionKey(),
					pathToDoc);
			try {
				System.out.println("Checking if document already exists. "
						+ "If yes - we must skip importing it");
				getEditGridPort().doWorkbookGet(wbGet);
				System.err.println("Document `" + pathToDoc
						+ "` already exist.");
				return;
			} catch (Exception e) {
				if (!e.getMessage().startsWith("Resource not found")) {
					e.printStackTrace();
				}
			}
		}
		System.out
				.println("Start encoding file to base64. Original file size = "
						+ String.valueOf(new File(pathToFile).length()));
		Binary base64data = new Binary(Base64.encodeFromFile(pathToFile));
		WorkbookImportRequest wbir = new WorkbookImportRequest(getSessionKey(),
				pathToDoc, "xls", base64data);
		System.out.println("Data encoded to base64. Data length = "
				+ String.valueOf(base64data.getBase64Bin().length()));

		try {
			getEditGridPort().doWorkbookImport(wbir);
		} catch (Exception e) {
			if (e.getMessage().startsWith("Resource not found")) {
				System.out.println("Document does not exist at EditGrid. "
						+ "We have to create an empty one first.");
				Workbook newWb = new Workbook();
				newWb.setName(docName);
				newWb.setPublicAccess("Read-only");
				newWb.setTimeZone("GMT +0000 -- Europe/London");
				newWb.setNote("");
				WorkbookCreateRequest wbCreate = new WorkbookCreateRequest(
						getSessionKey(), "//mine", newWb);
				try {
					getEditGridPort().doWorkbookCreate(wbCreate);
					System.out.println("An empty spreadsheet created.");
					getEditGridPort().doWorkbookImport(wbir);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				e.printStackTrace();
			}
		}
	}

	private Cell[] generateCellsArray(TableInfo ti, int tempCount,
			int currentCellsRowCount, EditgridConnection conn) throws Exception {
		Cell[] cells = new Cell[tempCount * ti.getRecords().toArray().length];
		int columnCount = ti.getSchemaTable().getRecords().toArray().length;
		int currentRow = currentCellsRowCount + 1;
		int currentArrayElement = 0;
		ResultSet tempData = DataBaseTools.getScrollableRSWithAllFields(ti
				.getTableName(), ApplicationData.getTempJDBC(), ApplicationData
				.getTempDataBaseInfo());
		if (conn.isVerticalDirection()) {
			while (tempData.next()) {
				for (int i = 0; i < columnCount; i++) {
					String value = null;
					try {
						value = tempData.getString(ti.getRecords().get(i)
								.getFieldName());
						if (cells[currentArrayElement] == null) {
							if (null != value && !"".equals(value)) {
								cells[currentArrayElement] = new Cell();
								cells[currentArrayElement].setInput(value);
							}
							currentArrayElement++;
						}
						System.out.println(value);
					} catch (RuntimeException e) {
					}
				}
				currentRow++;
			}
		} else {
			for (int i = 0; i < columnCount; i++) {
				tempData.first();
				currentRow = currentCellsRowCount + 1;
				do {
					String value = null;
					try {
						value = tempData.getString(ti.getRecords().get(i)
								.getFieldName());
						if (cells[currentArrayElement] == null) {
							if (null != value && !"".equals(value)) {
								cells[currentArrayElement] = new Cell();
								cells[currentArrayElement].setInput(value);
							}
							currentArrayElement++;
						}
						System.out.println(value);
					} catch (RuntimeException e) {
					}
					currentRow++;
				} while (tempData.next());
			}
		}
		return cells;
	}

	private void insertTDBtoRDB(TableInfo ti, int tempCount) {
		// binding can be empty
		if (getBinding(false) == null) {
			return;
		}
		try {
			Cell[] currentCells = null;
			int currentCellsRowCount = 0;
			EditgridConnection conn = (EditgridConnection) ApplicationData
					.getProject().getProjectData(connectionDataId).getData();

			if (!isDeleteAllInRDB() && !isSkipReadOnInsert()) {
				currentCells = getRow(conn.getFirstDataRow(), conn
						.isVerticalDirection(), conn.getMaxRows());
				currentCellsRowCount = getRowCount(currentCells, conn);
			}
			Cell[] cells = generateCellsArray(ti, tempCount,
					currentCellsRowCount, conn);
			String range = "";
			if (conn.isVerticalDirection()) {
				range = getEgWorksheet().getName()
						+ "!R"
						+ String.valueOf(currentCellsRowCount
								+ conn.getFirstDataRow())
						+ "C"
						+ conn.getFirstFieldPosition()
						+ ":R"
						+ String.valueOf(currentCellsRowCount
								+ conn.getFirstDataRow() + tempCount - 1) + "C"
						+ String.valueOf(conn.getLastFieldPosition());
			} else {
				range = getEgWorksheet().getName()
						+ "!R"
						+ conn.getFirstFieldPosition()
						+ "C"
						+ String.valueOf(currentCellsRowCount
								+ conn.getFirstDataRow())
						+ ":R"
						+ String.valueOf(conn.getLastFieldPosition())
						+ "C"
						+ String.valueOf(currentCellsRowCount
								+ conn.getFirstDataRow() + tempCount - 1);
			}

			updateCellsInEditGrid(range, cells);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateCellsInEditGrid(String range, Cell[] cells)
			throws Exception {
		System.out.println("Preparing request to EditGrid");
		System.out.println("Updating range: `" + range + "`");
		CellSetRequest ciR = new CellSetRequest(getSessionKey(),
				getEgWorkbook().getId(), range, getEgWorksheet().getName()
						+ "!A1:B1", cells);
		System.out.println("Sending request to EditGrid");
		getEditGridPort().doCellSet(ciR);
		System.out.println("Request sent");
	}

	private void updateTDBtoRDB(TableInfo ti, int tempCount) {
		List<String> identFields = getIdentificationFields();
		if (identFields.toArray().length == 0) {
			System.err.println("No identification fields are selected.");
			return;
		}
		// binding can be empty
		if (getBinding(false) == null) {
			return;
		}
		try {
			Cell[] currentCells = null;
			EditgridConnection conn = (EditgridConnection) ApplicationData
					.getProject().getProjectData(connectionDataId).getData();

			currentCells = getRow(conn.getFirstDataRow(), conn
					.isVerticalDirection(), conn.getMaxRows());

			TableInfo tempTi = getTiForConnection(OUT_CONN_POINT_NAME)
					.getClonedTi();
			tempTi.setTableName(tempTi.getTableName() + "_forupdate");
			DataBaseTools.createTable(ApplicationData.getTempJDBC(),
					ApplicationData.getTempDataBaseInfo(), tempTi.getRecords(),
					tempTi.getTableName());
			insertDataToTempTable(conn, currentCells, tempTi);

			// update records
			// TODO this code should be implemented into DataBaseTools
			DataBaseInfo tempDbi = ApplicationData.getTempDataBaseInfo();
			JdbcParams tempParams = ApplicationData.getTempJDBC();

			ResultSet tempData = DataBaseTools.getScrollableRSWithAllFields(ti
					.getTableName(), ApplicationData.getTempJDBC(), tempDbi);
			ResultSetMetaData rsmd = tempData.getMetaData();

			Statement selectSt = tempParams.getConnection().createStatement(
					ResultSet.FETCH_FORWARD, ResultSet.CONCUR_UPDATABLE);
			while (tempData.next()) {
				String where = "";
				for (String field : identFields) {

					where += "\"" + field + "\" = '"
							+ tempData.getString(field).replaceAll("'", "\\'")
							+ "' and ";
				}
				String select = "select * from " + tempTi.getTableName()
						+ " where " + where.substring(0, where.length() - 4);

				ResultSet selectRs = selectSt.executeQuery(select);
				if (selectRs.next()) {
					// we have to update this record;
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						try {
							if (null != tempData.getString(i)) {
								selectRs.updateString(rsmd.getColumnName(i),
										tempData.getString(i));
							}
						} catch (RuntimeException e) {
							System.err.println("Data for column `"
									+ rsmd.getColumnName(i) + "` not found.");
						}
					}
					selectRs.updateRow();
				} else {
					// we have to insert new record;
					String insert = "insert into " + tempTi.getTableName()
							+ " (";
					String values = ") values (";
					for (Record record : tempTi.getRecords()) {
						insert += "\"" + record.getFieldName() + "\", ";
						try {
							values += "'"
									+ tempData.getString(record.getFieldName())
											.replaceAll("'", "\\'") + "', ";
						} catch (RuntimeException e) {
							values += "null, ";
						}
					}
					insert = insert.substring(0, insert.length() - 2)
							+ values.substring(0, values.length() - 2) + ")";
					selectSt.execute(insert);
				}
			}

			int updatedDataCount = 0;
			updatedDataCount = DataBaseTools.getRScount(tempTi.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());
			Cell[] cells = generateCellsArray(tempTi, updatedDataCount, conn
					.getOffset(), conn);
			String range = "";
			if (conn.isVerticalDirection()) {
				range = getEgWorksheet().getName()
						+ "!R"
						+ String.valueOf(conn.getFirstDataRow())
						+ "C"
						+ conn.getFirstFieldPosition()
						+ ":R"
						+ String.valueOf(conn.getFirstDataRow()
								+ updatedDataCount - 1) + "C"
						+ String.valueOf(conn.getLastFieldPosition());
			} else {
				range = getEgWorksheet().getName()
						+ "!R"
						+ conn.getFirstFieldPosition()
						+ "C"
						+ String.valueOf(conn.getFirstDataRow())
						+ ":R"
						+ String.valueOf(conn.getLastFieldPosition())
						+ "C"
						+ String.valueOf(conn.getFirstDataRow()
								+ updatedDataCount - 1);
			}
			updateCellsInEditGrid(range, cells);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int getRowCount(Cell[] cells, EditgridConnection conn) {
		if (null == cells || cells.length == 0) {
			return 0;
		}
		if (conn.isVerticalDirection()) {
			return cells[cells.length - 1].getRow() + 1 - conn.getOffset()
					+ (!conn.isFirstFieldName() ? 1 : 0);
		} else {
			return cells[cells.length - 1].getCol() + 1 - conn.getOffset()
					+ (!conn.isFirstFieldName() ? 1 : 0);
		}
	}

	private Map<Integer, Cell[]> getMapOfCells(Cell[] cells,
			EditgridConnection conn, int columnsCount) {
		Map<Integer, Cell[]> rows = new HashMap<Integer, Cell[]>();
		int rowsCount = getRowCount(cells, conn);
		for (int i = 1; i <= rowsCount; i++) {
			rows.put(i, new Cell[columnsCount]);
		}

		for (Cell cell : cells) {

			int colNum = 0;
			int rowNum = 0;
			if (conn.isVerticalDirection()) {
				colNum = cell.getCol();
				rowNum = cell.getRow();
			} else {
				colNum = cell.getRow();
				rowNum = cell.getCol();
			}
			for (int i = 0; i < columnsCount; i++) {
				if (i == (colNum - (conn.getFirstFieldPosition() - 1))) {
					// rows.get((rowNum == 0) ? 1 : rowNum)[i] = cell;
					rows.get(rowNum + 1 - conn.getOffset()
							+ (!conn.isFirstFieldName() ? 1 : 0))[i] = cell;
				}
			}
		}
		return rows;
	}

	private void insertDataToTempTable(EditgridConnection conn, Cell[] cells,
			TableInfo ti) throws Exception {
		int columnsCount = ti.getSchemaTable().getRecords().toArray().length;
		String insertSQLheader = "insert into " + ti.getTableName() + " (";
		String valuesStr = "";
		for (Record rec : ti.getSchemaTable().getRecords()) {
			insertSQLheader += "\"" + rec.getFieldName() + "\", ";
			valuesStr += "?, ";
		}

		insertSQLheader = insertSQLheader.substring(0,
				insertSQLheader.length() - 2)
				+ ") values ("
				+ valuesStr.substring(0, valuesStr.length() - 2)
				+ ")";

		PreparedStatement insertPs = ApplicationData.getTempJDBC()
				.getConnection().prepareStatement(insertSQLheader);
		Map<Integer, Cell[]> rows = getMapOfCells(cells, conn, columnsCount);
		int rowsCount = getRowCount(cells, conn);

		for (int row = 1; row <= rowsCount; row++) {
			// checking for all nulls in row
			boolean doAddCurrentInsertSql = true;
			if (conn.isSkipEmptyRecord()) {
				doAddCurrentInsertSql = false;
				for (int column = 0; column < columnsCount; column++) {
					if (rows.get(row)[column] != null) {
						doAddCurrentInsertSql = true;
						break;
					}
				}
			}
			if (doAddCurrentInsertSql) {
				for (int column = 0; column < columnsCount; column++) {
					if (rows.get(row)[column] != null) {
						insertPs.setString(column + 1, rows.get(row)[column]
								.getValue());
					} else {
						insertPs.setNull(column + 1, java.sql.Types.VARCHAR);
					}
				}
			}

			insertPs.execute();
		}
	}

	@Override
	protected void TransformRDBtoTDB() {
		try {
			DataBaseTools.completeTransfer();
			EditgridConnection conn = (EditgridConnection) ApplicationData
					.getProject().getProjectData(connectionDataId).getData();

			Cell[] cells = getRow(conn.getFirstDataRow(), conn
					.isVerticalDirection(), conn.getMaxRows());
			TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

			insertDataToTempTable(conn, cells, ti);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {

		List<RDBTable> tableList = new ArrayList<RDBTable>();

		if (getBinding(false) == null) {
			if (getBinding(true) == null) {
				throw new RuntimeException("Unable to login to EditGrid");
			}
		}
		WorksheetListRequest wsListRequest = new WorksheetListRequest(
				getSessionKey(), getEgWorkbook().getId());

		Worksheet[] sheetList = getEditGridPort()
				.doWorksheetList(wsListRequest);
		for (Worksheet worksheet : sheetList) {
			tableList.add(new EgWorksheet(worksheet.getName(),
					ETableMode.ReadWrite, worksheet));
		}

		return tableList;
	}

	public List<RDBTable> getWorkbookList() throws Exception {

		List<RDBTable> workbookList = new ArrayList<RDBTable>();

		if (getBinding(false) == null) {
			if (getBinding(true) == null) {
				throw new RuntimeException("Unable to login to EditGrid");
			}
		}
		getWorkSpace();
		WorkbookListRequest wbList = new WorkbookListRequest(getSessionKey(),
				egWorkspace.getId(), 1, 0, Integer.MAX_VALUE, 0);
		Workbook[] booksList = getEditGridPort().doWorkbookList(wbList);
		for (Workbook workbook : booksList) {
			workbookList.add(new EgWorkbook(workbook.getName(),
					ETableMode.ReadWrite, workbook));
		}

		return workbookList;
	}

	private void getWorkSpace() throws Exception {
		if (egWorkspace == null) {
			WorkspaceGetRequest workspaceGetRequest = new WorkspaceGetRequest(
					getSessionKey(), "//mine");
			egWorkspace = getEditGridPort().doWorkspaceGet(workspaceGetRequest);
		}
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws ClassNotFoundException, SQLException {
		List<Record> recs = new ArrayList<Record>();

		EditgridConnection conn = (EditgridConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();

		DBTypeRecord dbtrec = DBTypeRecord.getRecordByOriginalType(
				getDataBaseInfo().getAvailableTypes(), "VARCHAR");

		try {
			if (conn.isFirstFieldName()) {
				com.apatar.editgrid.ws.Cell[] cells = getRow(
						conn.getOffset(),
						(conn.getDataDirection() == DataDirection.VerticalDirection ? true
								: false), 1);
				if ((null == cells) || (cells.length < 1)) {
					JOptionPane
							.showMessageDialog(
									ApatarUiMain.MAIN_FRAME,
									"Wrong dataset range is set. Please set correct offset or uncheck firstFieldName flag");
					return null;
				}
				for (com.apatar.editgrid.ws.Cell cell : cells) {
					recs.add(new Record(dbtrec, cell.getValue(), 32000, false));
				}
			} else {
				for (int i = conn.getFirstFieldPosition(); i <= conn
						.getLastFieldPosition(); i++) {
					recs.add(new Record(dbtrec, "F" + String.valueOf(i), 32000,
							false));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return recs;
	}

	private com.apatar.editgrid.ws.Cell[] getRow(int rowIndex,
			boolean isVertical, int numberRowsToReturn) throws Exception {
		EditgridConnection conn = (EditgridConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();
		String range = "";
		if (isVertical) {
			range = "R"
					+ String.valueOf(rowIndex)
					+ "C"
					+ String.valueOf(conn.getFirstFieldPosition())
					+ ":R"
					+ String.valueOf(numberRowsToReturn < 2 ? 1
							: numberRowsToReturn) + "C"
					+ String.valueOf(conn.getLastFieldPosition());
		} else {
			range = "R"
					+ String.valueOf(conn.getFirstFieldPosition())
					+ "C"
					+ String.valueOf(rowIndex)
					+ ":R"
					+ String.valueOf(conn.getLastFieldPosition())
					+ "C"
					+ String.valueOf(numberRowsToReturn < 2 ? 1
							: numberRowsToReturn);
		}
		range = getEgWorksheet().getName() + "!" + range;
		System.out.println("Cells range to select data is: `" + range + "`");
		CellListRequest clRequest = new CellListRequest(getSessionKey(),
				getEgWorkbook().getId(), range, 0);
		return getEditGridPort().doCellList(clRequest);
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		System.err.println("Clearing all data feature is not implemented.");
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	/**
	 * @return the egWorkbook
	 * @throws Exception
	 */
	public Workbook getEgWorkbook() throws Exception {
		if (egWorkbook == null && workbookTable != null) {
			setEgWorkbook(workbookTable.getTableName());
		}
		return egWorkbook;
	}

	/**
	 * @param egWorkbook
	 *            the egWorkbook to set
	 */
	public void setEgWorkbook(Workbook egWorkbook) {
		this.egWorkbook = egWorkbook;
	}

	public void setEgWorkbook(String workbookName) throws Exception {
		WorkbookListRequest wbGet = new WorkbookListRequest(getSessionKey(),
				getEgWorkspace().getId(), 1, 0, Integer.MAX_VALUE, 0);
		Workbook[] list = getEditGridPort().doWorkbookList(wbGet);
		for (Workbook workbook : list) {
			if (workbook.getName().equals(workbookName)) {
				egWorkbook = workbook;
				return;
			}
		}
	}

	public void setEgWorkbook(EgWorkbook table) throws Exception {
		setWorkbookTable(table);
		setEgWorkbook(table.getEgWorkbook());
	}

	/**
	 * @return the workbookTable
	 */
	public RDBTable getWorkbookTable() {
		if (null == workbookTable) {
			workbookTable = new RDBTable("", ETableMode.ReadWrite);
		}
		return workbookTable;
	}

	/**
	 * @param workbookTable
	 *            the workbookTable to set
	 */
	public void setWorkbookTable(RDBTable workbookTable) {
		this.workbookTable = workbookTable;
	}

	/**
	 * @return the egWorksheet
	 * @throws Exception
	 */
	public Worksheet getEgWorksheet() throws Exception {
		if (egWorksheet == null && worksheetTable != null) {
			setEgWorksheet(worksheetTable.getTableName());
		}
		return egWorksheet;
	}

	/**
	 * @param egWorksheet
	 *            the egWorksheet to set
	 */
	public void setEgWorksheet(Worksheet egWorksheet) {
		this.egWorksheet = egWorksheet;
	}

	public void setEgWorksheet(String workSheetName) throws Exception {
		WorksheetListRequest wsGet = new WorksheetListRequest(getSessionKey(),
				getEgWorkbook().getId());
		Worksheet[] list = getEditGridPort().doWorksheetList(wsGet);
		for (Worksheet worksheet : list) {
			if (workSheetName.equals(worksheet.getName())) {
				egWorksheet = worksheet;
				return;
			}
		}
	}

	public void setEgWorksheet(EgWorksheet egWorksheetTable) {
		worksheetTable = egWorksheetTable;
		setTable(egWorksheetTable);
		setEgWorksheet(egWorksheetTable.getEgWorksheet());
	}

	/**
	 * @return the worksheetTable
	 */
	public RDBTable getWorksheetTable() {
		if (worksheetTable == null) {
			worksheetTable = new RDBTable("", ETableMode.ReadWrite);
		}
		return worksheetTable;
	}

	/**
	 * @param worksheetTable
	 *            the worksheetTable to set
	 */
	public void setWorksheetTable(RDBTable worksheetTable) {
		this.worksheetTable = worksheetTable;
	}

	/**
	 * @return the egWorkspace
	 * @throws Exception
	 */
	public Workspace getEgWorkspace() throws Exception {
		if (egWorkspace == null) {
			getWorkSpace();
		}
		return egWorkspace;
	}

	/**
	 * @param egWorkspace
	 *            the egWorkspace to set
	 */
	public void setEgWorkspace(Workspace egWorkspace) {
		this.egWorkspace = egWorkspace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		sessionKey = null;
		egWorkbook = null;
		egWorksheet = null;
		egWorkspace = null;
		return null != getBinding(true);
	}

	/**
	 * @return the sessionKey
	 */
	public String getSessionKey() {
		if ((null == sessionKey) || ("".equals(sessionKey))) {
			sessionKey = (String) getBinding(true);
		}
		return sessionKey;
	}

	/**
	 * @return the skipReadOnInsert
	 */
	public boolean isSkipReadOnInsert() {
		return skipReadOnInsert;
	}

	/**
	 * @param skipReadOnInsert
	 *            the skipReadOnInsert to set
	 */
	public void setSkipReadOnInsert(boolean skipReadOnInsert) {
		this.skipReadOnInsert = skipReadOnInsert;
	}

}