/*TODO recorded refactoring
 * исправлены синтаксические ошибки в названиях пакетов, методов и классов плагина MSEXCEL
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
 ### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
 ### GNU General Public License for more details.

 ### You should have received a copy of the GNU General Public License along
 ### with this program; if not, write to the Free Software Foundation, Inc.,
 ### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ________________________

 */

package com.apatar.msexcel;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.biff.EmptyCell;
import jxl.read.biff.BiffException;
import jxl.read.biff.BlankCell;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jdom.Element;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataDirection;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class MsExcelNode extends AbstractNonJdbcDataBaseNode {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("[", "]", "[",
			"]", true, true, false, true, false);

	// public ExcelParams excelParams = new ExcelParams();

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BYTE", 1, 1, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIT", 1, 1, false,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INTEGER", 2, 2, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SMALLINT", 2, 2,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "LONG", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "SINGLE", 4, 4, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "DOUBLE", 8, 8, true,
				true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "DECIMAL", 12, 12,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "CURRENCY", 8, 8,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "COUNTER", 11, 11,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "AUTONUMBER", 4, 4,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "REPLICATIONID", 2, 2,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "YESNO", 1, 1, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "TEXT", 0, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "CHAR", 0, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "LONGCHAR", 0,
				1073741823, false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "MEMO", 0, 65536,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "OLE", 0, 0x40000000,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "ATTACHMENT", 0,
				0x40000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "HYPERLINK", 0,
				0x40000000, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "DATETIME", 8, 8,
				false, false));

		rcList.add(new DBTypeRecord(ERecordType.Numeric, "NUMBER", 8, 8, false,
				false));
	}

	public MsExcelNode() {
		super();
		title = "MS Excel";
	}

	@Override
	public ImageIcon getIcon() {
		return MsExcelUtils.READ_MSEXCEL_NODE_ICON;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wizard.getDialog()),
					JdbcRecordSourceDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.msexcel.MsExcelConnection"),
					"db_connector", "msexcel");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new RecordSourceDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					TableModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(RecordSourceDescriptor.IDENTIFIER,
					descriptor2);

			/*
			 * WizardPanelDescriptor descriptor3 = new
			 * ExcelOptionDescriptor(this);
			 * wizard.registerWizardPanel(ExcelOptionDescriptor.IDENTIFIER,
			 * descriptor3);
			 */

			WizardPanelDescriptor descriptor3 = new TableModeDescriptor(this,
					RecordSourceDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,
					descriptor3);

			wizard.setKeyForReferringToDescription("help.connector.msexcel");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);
			wizard.showModalDialog();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Sheet getSheet() {
		MsExcelConnection conn = (MsExcelConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(Locale.US);
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(conn.getFile(), ws);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Sheet sheet = workbook.getSheet(getTableName());
		return sheet;
	}

	private WritableWorkbook getWritableWorkbook() {
		WritableWorkbook ww = null;
		MsExcelConnection conn = (MsExcelConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(Locale.US);
		try {
			Workbook workbook = Workbook.getWorkbook(conn.getFile(), ws);
			ww = Workbook.createWorkbook(conn.getFile(), workbook);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}

		return ww;
	}

	@Override
	public void TransformRDBtoTDB() {
		DataBaseTools.completeTransfer();
		MsExcelConnection conn = (MsExcelConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();
		Sheet sheet = getSheet();
		int start = conn.getOffset() - 1;
		int finish = (conn.getDataDirection() == DataDirection.HorisontalDirection ? sheet
				.getColumns()
				: sheet.getRows());
		if (conn.isFirstFieldName()) {
			start++;
		}
		// finish += start;
		Cell[] cells;
		boolean empty = true;
		TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);
		try {
			ti.getSchemaTable().updateRecords(getFieldList(null));
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		List<Record> recs = ti.getRecords();
		for (int i = start; i <= finish - 1; i++) {
			empty = true;
			cells = conn.getDataDirection() == DataDirection.HorisontalDirection ? sheet
					.getColumn(i)
					: sheet.getRow(i);
			KeyInsensitiveMap data = new KeyInsensitiveMap();
			if (cells.length < 1) {
				for (int j = conn.getFirstFieldPosition() - 1; j <= conn
						.getLastFieldPosition() - 1; j++) {
					MsExcelRecord rec = MsExcelRecord.getRecordByPosition(recs,
							j + 1);
					data.put(rec.getFieldName(), new JdbcObject(null, rec
							.getSqlType()));
				}
			}
			for (int j = conn.getFirstFieldPosition() - 1; j <= conn
					.getLastFieldPosition() - 1
					&& j < cells.length; j++) {
				MsExcelRecord rec = MsExcelRecord.getRecordByPosition(recs,
						j + 1);
				if (cells[j] instanceof EmptyCell
						|| cells[j] instanceof BlankCell) {
					data.put(rec.getFieldName(), new JdbcObject(null, rec
							.getSqlType()));
				} else {
					String contents = cells[j].getContents();
					if (conn.isSkipEmptyRecord()) {
						if (!contents.equals("")) {
							data.put(rec.getFieldName(), new JdbcObject(
									contents, rec.getSqlType()));
							empty = false;
						}
					} else {
						data.put(rec.getFieldName(), new JdbcObject(contents,
								rec.getSqlType()));
						empty = false;
					}
				}
			}
			if (empty) {
				if (conn.isSkipEmptyRecord()) {
					continue;
				} else {
					try {
						DataBaseTools.insertData(
								new DataProcessingInfo(ApplicationData
										.getTempDataBase().getDataBaseInfo(),
										ti.getTableName(), ti.getRecords(),
										ApplicationData.getTempJDBC()), data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(), ti
									.getTableName(), ti.getRecords(),
									ApplicationData.getTempJDBC()), data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		DataBaseTools.completeTransfer();
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
		if (mode == AbstractDataBaseNode.INSERT_MODE) {
			insertTDBtoRDB();
		} else {
			updateTDBtoRDB();
		}
		DataBaseTools.completeTransfer();
	}

	private void insertTDBtoRDB() {
		WritableWorkbook ww = getWritableWorkbook();
		MsExcelConnection conn = (MsExcelConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();
		WritableSheet sheet = ww.getSheet(getTableName());
		int insertIn = (conn.getDataDirection() == DataDirection.HorisontalDirection ? sheet
				.getColumns()
				: sheet.getRows());

		TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);

		List<Record> recs = ti.getRecords();

		// work the way if there is no field requried then skip it

		try {
			ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());

			while (rs.next()) {
				insertTDBtoRDB(rs, recs, conn, sheet, insertIn);
				insertIn++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} finally {
			try {
				ww.write();
				ww.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

	private void insertTDBtoRDB(ResultSet rs, List<Record> recs,
			MsExcelConnection conn, WritableSheet sheet, int insertIn)
	
			throws SQLException, RowsExceededException, WriteException {
		for (Record rec : recs) {
			MsExcelRecord erec = (MsExcelRecord) rec;
			Object obj = rs.getObject(erec.getFieldName());
			if (obj != null) {
				Label wc;
				if (conn.getDataDirection() == DataDirection.VerticalDirection) {
					wc = new Label(erec.getNumber() - 1, insertIn, obj
							.toString());
				} else {
					wc = new Label(insertIn, erec.getNumber() - 1, obj
							.toString());
				}
				sheet.addCell(wc);
			}
		}
	}

	private void updateTDBtoRDB() {
		WritableWorkbook ww = getWritableWorkbook();
		MsExcelConnection conn = (MsExcelConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();
		WritableSheet sheet = ww.getSheet(getTableName());

		TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);

		List<Record> recs = ti.getRecords();

		List<String> identif = getIdentificationFields();

		// work the way if there is no field requried then skip it

		try {
			ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());

			int start = conn.getOffset() - 1;
			if (conn.isFirstFieldName()) {
				start++;
			}
			int finish = (conn.getDataDirection() == DataDirection.VerticalDirection ? sheet
					.getRows()
					: sheet.getColumns()) - 1;

			Cell[] cells;

			while (rs.next()) {
				boolean coincidence = true;
				for (int i = start; i <= finish; i++) {
					cells = conn.getDataDirection() == DataDirection.VerticalDirection ? sheet
							.getRow(i)
							: sheet.getColumn(i);
					coincidence = true;
					for (String field : identif) {
						MsExcelRecord rec = (MsExcelRecord) Record
								.getRecordByFieldName(recs, field);
						int pos = rec.getNumber() - 1;
						Object obj = rs.getObject(field);
						if (obj != null) {
							try {
								if (!cells[pos].getContents().equals(obj)) {
									coincidence = false;
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
								coincidence = false;
								break;
							}
						}
					}
					if (coincidence) {
						insertTDBtoRDB(rs, recs, conn, sheet, i);
						break;
					}
				}
				if (!coincidence) {
					int insertIn = (conn.getDataDirection() == DataDirection.HorisontalDirection ? sheet
							.getColumns()
							: sheet.getRows());
					insertTDBtoRDB(rs, recs, conn, sheet, insertIn);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ww.write();
				ww.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws ClassNotFoundException, SQLException {
		List<Record> recs = new ArrayList<Record>();

		MsExcelConnection conn = (MsExcelConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();

		Sheet sheet = getSheet();
		DBTypeRecord dbtrec = DBTypeRecord.getRecordByOriginalType(
				getDataBaseInfo().getAvailableTypes(), "VARCHAR");

		if (conn.isFirstFieldName()) {
			Cell[] cells = conn.getDataDirection() == DataDirection.VerticalDirection ? sheet
					.getRow(conn.getOffset() - 1)
					: sheet.getColumn(conn.getOffset() - 1);
			if (cells.length < 1) {
				JOptionPane
						.showMessageDialog(
								ApatarUiMain.MAIN_FRAME,
								"Wrong dataset range is set. Please set correct offset or uncheck firstFieldName flag");
				return null;
			}
			for (int i = conn.getFirstFieldPosition() - 1; i <= conn
					.getLastFieldPosition() - 1; i++) {
				MsExcelRecord rec;
				if (i >= cells.length) {
					rec = new MsExcelRecord(dbtrec, "F" + (i + 1), 255, false);
				} else {
					String content = cells[i].getContents();
					rec = new MsExcelRecord(dbtrec, i >= cells.length
							|| content == null || content.equals("") ? "F"
							+ (i + 1) : content, 255, false);
				}
				rec.setNumber(i + 1);
				recs.add(rec);
			}
		} else {
			for (int i = conn.getFirstFieldPosition(); i <= conn
					.getLastFieldPosition(); i++) {
				MsExcelRecord rec = new MsExcelRecord(dbtrec, "F" + (i), 255,
						false);
				rec.setNumber(i);
				recs.add(rec);
			}
		}
		return recs;
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		MsExcelConnection connection = (MsExcelConnection) ApplicationData
				.getProject().getProjectData(getConnectionDataID()).getData();
		try {
			Workbook wb = Workbook.getWorkbook(connection.getFile());
			String[] sns = wb.getSheetNames();
			List<RDBTable> tables = new ArrayList<RDBTable>();
			for (String name : sns) {
				RDBTable table = new RDBTable(name, ETableMode.ReadWrite);
				tables.add(table);
			}
			return tables;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
					"Unfortunately file of this format is not supported.");
		}
		return null;
	}

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		if (connectionDataId == -1) {
			return;
		}

		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();

		st.updateRecords(getFieldList(null));
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {

	}

	@Override
	public boolean validateConnectionData() {
		MsExcelConnection c = (MsExcelConnection) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData();
		if (!c.getFile().isFile() || !c.getFile().exists()) {
			lastErrorMessage = "Please select proper Excel file";
			return false;
		}
		if (c.getOffset() < 1) {
			lastErrorMessage = "Please enter an integer value greater than 0 for Offset.";
			return false;
		}
		if (c.getFirstFieldPosition() < 1) {
			lastErrorMessage = "Please enter an integer value greater than 0 for First Field Position.";
			return false;
		}
		if (c.getLastFieldPosition() < 1) {
			lastErrorMessage = "Please enter an integer value greater than 0 for Last Field Position.";
			return false;
		}
		if (c.getFirstFieldPosition() > c.getLastFieldPosition()) {
			lastErrorMessage = "Please check your input. Last field position is less than first field position.";
			return false;
		}
		return true;
	}

	@Override
	public Element saveToElement() {
		return super.saveToElement();
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
	}

}
