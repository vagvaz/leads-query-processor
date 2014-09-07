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

package com.apatar.sugarcrm;

import java.net.URL;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.jdom.Element;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.ETableMode;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.LogUtils;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.sugarcrm.ui.SugarcrmRecordSourceDescriptor;
import com.apatar.sugarcrm.ws51.Entry_value;
import com.apatar.sugarcrm.ws51.Field;
import com.apatar.sugarcrm.ws51.Get_entry_list_result;
import com.apatar.sugarcrm.ws51.Module_fields;
import com.apatar.sugarcrm.ws51.Module_list;
import com.apatar.sugarcrm.ws51.Name_value;
import com.apatar.sugarcrm.ws51.Set_entry_result;
import com.apatar.sugarcrm.ws51.SugarsoapLocator;
import com.apatar.sugarcrm.ws51.SugarsoapPortType;
import com.apatar.sugarcrm.ws51.User_auth;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class SugarcrmNode extends AbstractNonJdbcDataBaseNode {

	private SugarsoapPortType binding = null;
	private User_auth USER_SOAP = null;
	private Set_entry_result AUTH_RESULT = null;

	private JDialog wizardDialog = null;

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			true, true, false, true, false);

	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		rcList
				.add(new DBTypeRecord(ERecordType.Text, "ID", 1, 255, true,
						true));
		rcList.add(new DBTypeRecord(ERecordType.Text, "USER_NAME", 1, 255,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "MODIFIED_USER_NAME", 1,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "NAME", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "ASSIGNED_USER_NAME", 1,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "BOOL", 3, 3, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "RELATE", 1, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "MULTIENUM", 1, 255,
				false, false));
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
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "CURRENCY", 4, 4,
				true, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "TEAM_LIST", 1, 255,
				true, true));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DECIMAL", 8, 8, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB", 8, 8, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "URL", 1, 255, false,
				false));
	}

	public SugarcrmNode() {
		super();
		title = "SugarCRM";
	}

	public SugarsoapPortType getBinding() {

		if (binding == null) {
			try {
				SugarcrmConnection conn = (SugarcrmConnection) ApplicationData
						.getProject().getProjectData(connectionDataId)
						.getData();

				URL url = new URL(conn.getUrl());

				SugarsoapLocator sugarsoap = new SugarsoapLocator();
				binding = sugarsoap.getsugarsoapPort(url);
				login();

			} catch (Exception e) {
				lastErrorMessage = LogUtils.GetExceptionMessage(e);
				if (!ApplicationData.ProcessingProgress.Log(e)) {
					binding = null;
					return null;
				}
			}
		}

		if (AUTH_RESULT.getId().equals("-1")) {
			binding = null;
			lastErrorMessage = "Error: You must specify a valid username and password";
			System.err
					.println("Error: You must specify a valid username and password");
		}
		return binding;
	}

	@Override
	public ImageIcon getIcon() {
		return SugarcrmUtils.READ_SUGARCRM_NODE_ICON;
	}

	public String getWebServiceName() {
		return "sugarcrm";
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		wizardDialog = wizard.getDialog();
		wizardDialog.setTitle(title + " Properties");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wizardDialog),
					RecordSourceDescriptor.IDENTIFIER, Class
							.forName("com.apatar.sugarcrm.SugarcrmConnection"),
					"db_connector", "sugarcrm");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new SugarcrmRecordSourceDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					TableModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(RecordSourceDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new TableModeDescriptor(this,
					RecordSourceDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,
					descriptor3);

			wizard.setKeyForReferringToDescription("help.connector.sugarcrm");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			binding = null;
			wizard.showModalDialog();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login() {
		SugarcrmConnection conn = (SugarcrmConnection) ApplicationData
				.getProject().getProjectData(connectionDataId).getData();

		if (ApplicationData.SugarcrmAppExchange) {
			if (conn.getUserName().length() == 0
					|| conn.getPassword().getValue().length() == 0) {

				JOptionPane.showMessageDialog(wizardDialog,
						"Please input SugarCRM credentials.");
				binding = null;
				return;
			}
		}

		// generate hash password
		String md5password = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			StringBuffer sb = new StringBuffer();
			byte buf[] = conn.getPassword().getValue().getBytes();
			byte[] md5 = md.digest(buf);

			for (byte element : md5) {
				String tmpStr = "0" + Integer.toHexString((0xff & element));
				sb.append(tmpStr.substring(tmpStr.length() - 2));
			}

			md5password = sb.toString();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		USER_SOAP = new User_auth(conn.getUserName(), md5password, "");
		try {
			AUTH_RESULT = binding.login(USER_SOAP, "sugarcrm");
		} catch (Exception e) {
			binding = null;
			if (!ApplicationData.ProcessingProgress.Log(e)) {
				return;
			}
		}
	}

	@Override
	public Element saveToElement() {
		return super.saveToElement();
	}

	@Override
	public void initFromElement(Element node) {
		super.initFromElement(node);
	}

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		if (connectionDataId == -1) {
			return;
		}

		// binding can be empty
		if (null == getBinding()) {
			return;
		}

		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.updateRecords(getFieldList(null));
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {

		if (AbstractDataBaseNode.INSERT_MODE == mode) {
			insertTDBtoRDB();
		} else {
			try {
				updateTDBtoRDB();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void insertTDBtoRDB() {
		// binding can be empty
		if (getBinding() == null) {
			return;
		}

		try {
			TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
			ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());
			ResultSetMetaData resuleMetaData = rs.getMetaData();

			// ------------------------------
			Name_value arrNameValuesToEnter[][];
			List<List> arrayLists = new ArrayList<List>();
			int countRowInArr = 0;
			int countColumns = resuleMetaData.getColumnCount();

			String columnsName[] = new String[countColumns];
			for (int i = 1; i <= countColumns; i++) {
				columnsName[i - 1] = resuleMetaData.getColumnName(i);
			}

			String value = "";
			while (rs.next()) {
				List<Name_value> listNameValuesToEnter = new ArrayList<Name_value>();

				for (int i = 1; i <= countColumns; i++) {
					value = rs.getString(i);
					if (null != value) {
						listNameValuesToEnter.add(new Name_value(
								columnsName[i - 1].toLowerCase(), value));
					}

				}

				arrayLists.add(listNameValuesToEnter);
			}

			arrNameValuesToEnter = new Name_value[2][];
			List tmpList = null;
			int countColumnInList = 0;

			Iterator it = arrayLists.iterator();
			while (it.hasNext()) {
				countRowInArr = 0;
				for (Iterator<List> i = it; i.hasNext(); countRowInArr++) {
					if (countRowInArr == 2) {
						break;
					}
					tmpList = i.next();
					countColumnInList = 0;
					arrNameValuesToEnter[countRowInArr] = new Name_value[tmpList
							.size()];

					for (Iterator<Name_value> j = tmpList.iterator(); j
							.hasNext(); countColumnInList++) {
						arrNameValuesToEnter[countRowInArr][countColumnInList] = j
								.next();
					}

					if (!ApplicationData.ProcessingProgress.Step()) {
						return;
					}
				}

				// send data to web-service SugarCRM
				storeDataToSugar(AUTH_RESULT.getId(), getTableName(),
						arrNameValuesToEnter);
			}
		} catch (RemoteException e) {
			if (!ApplicationData.ProcessingProgress.Log(e)) {
				return;
			}
		} catch (SQLException e) {
			if (!ApplicationData.ProcessingProgress.Log(e)) {
				return;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void storeDataToSugar(String session, String module_name,
			Name_value[][] name_value_lists) throws RemoteException {
		for (Name_value[] name_values : name_value_lists) {
			if (null == name_values) {
				continue;
			}
			for (Name_value name_value : name_values) {
				name_value.setValue(name_value.getValue().replaceAll("<(.*?)>",
						"$1"));
				// name_value.setValue(name_value.getValue().replaceAll(">",
				// ""));
			}
		}
		binding.set_entries(session, module_name, name_value_lists);
	}

	protected void updateTDBtoRDB() throws Exception {
		// binding can be empty
		if (getBinding() == null) {
			return;
		}

		try {
			TableInfo iTI = getTiForConnection(IN_CONN_POINT_NAME);
			TableInfo oTI = getTiForConnection(OUT_CONN_POINT_NAME);

			// List<Object> values = new ArrayList<Object>();
			// final String TEMP_NAME = "sugarcrm";

			// DataBaseTools.createTable(ApplicationData.getTempJDBC(),
			// ApplicationData.getTempDataBaseInfo(), oTI.getRecords(),
			// TEMP_NAME);
			// insertRDBtoTDB(TEMP_NAME);

			// KeyInsensitiveMap selectDatas = new KeyInsensitiveMap();
			// String selectFields[] = new String[identificationFields.size() +
			// 1];
			// selectFields[0] = "id";

			// ApplicationData.getTempJDBCConnection().setAutoCommit(false);
			ResultSet rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());
			// Statement st =
			// ApplicationData.getTempJDBC().getConnection().createStatement();
			while (rs.next()) {

				KeyInsensitiveMap selectDatas = new KeyInsensitiveMap();
				KeyInsensitiveMap values = DataBaseTools.GetDataFromRS(rs);
				for (String substr : values.keySet()) {
					if (identificationFields.contains(substr)) {
						selectDatas.put(substr, values.get(substr, false));
					}
				}

				// SQLCreationData sqd = new
				// SQLCreationData(oTI.getSchemaTable()
				// .getRecords(), "");
				// SQLQueryString sqs = DataBaseTools
				// .CreateSelectString(ApplicationData
				// .getTempDataBaseInfo(), sqd, selectDatas);
				//
				// if (sqs == null) {
				// return;
				// }

				// sqs = DataBaseTools.getPreparedSQL(sqs, values);

				// System.out.println(sqs.query);
				String where = "";
				for (String field : selectDatas.keySet()) {
					where += getTableName().toLowerCase()
							+ "."
							+ field
							+ " = '"
							+ (String) ((JdbcObject) selectDatas.get(field,
									false)).getValue() + "' and ";
				}

				String field_id[] = new String[1];
				field_id[0] = "id";

				Get_entry_list_result getEntryListResult = getBinding()
						.get_entry_list(AUTH_RESULT.getId(), getTableName(),
								where.substring(0, where.length() - 5), "id",
								0, field_id, 1, 0);

				// PreparedStatement ps = DataBaseTools
				// .getPreparedStatementWithStringData(ApplicationData
				// .getTempJDBCConnection(), sqs, values);
				//
				// ResultSet selectRs = ps.executeQuery();
				//
				// values = DataBaseTools.GetDataFromRS(rs);
				Entry_value entryValue[] = getEntryListResult.getEntry_list();
				Name_value nameValues[];

				if (getEntryListResult.getResult_count() == 0) {
					// we have to insert new row
					List<Name_value> columnValues = getValuesToUpdate(rs);

					Name_value arrNameValuesToEnter[][] = new Name_value[1][columnValues
							.size()];
					for (int i = 0; i < columnValues.size(); i++) {
						arrNameValuesToEnter[0][i] = columnValues.get(i);
					}

					storeDataToSugar(AUTH_RESULT.getId(), getTableName(),
							arrNameValuesToEnter);

				} else {
					// we have to update existing record
					String id = "";

					List<Name_value> columnValues = getValuesToUpdate(rs);

					Name_value arrNameValuesToEnter[][] = new Name_value[1][columnValues
							.size() + 1];
					for (Entry_value element : entryValue) {
						nameValues = element.getName_value_list();
						for (Name_value element2 : nameValues) {
							if (element2.getName().equalsIgnoreCase("id")) {
								id = element2.getValue();
								break;
							}
						}

						break;
					}

					arrNameValuesToEnter[0][0] = new Name_value("id", id);

					for (int i = 0; i < columnValues.size(); i++) {
						arrNameValuesToEnter[0][i + 1] = columnValues.get(i);
					}

					storeDataToSugar(AUTH_RESULT.getId(), getTableName(),
							arrNameValuesToEnter);

				}
				// ps.close();
				if (!ApplicationData.ProcessingProgress.Step()) {
					return;
					// } else
					// continue;
				}
			}
			// st.close();

			/*
			 * KeyInsensitiveMap selectDatas = new KeyInsensitiveMap(); //
			 * restrict full set of values to identification fields int j = 1;
			 * for (String substr : values.keySet()) { if
			 * (identificationFields.contains(substr)) { selectDatas.put(substr,
			 * values.get(substr, false)); selectFields[j++] = substr; } }
			 * SQLCreationData sqd = new
			 * SQLCreationData(oTI.getSchemaTable().getRecords(),
			 * getTableName()); SQLQueryString sqs =
			 * DataBaseTools.CreateSelectString(getDataBaseInfo(), sqd,
			 * selectDatas); if (sqs == null) return; sqs =
			 * DataBaseTools.getPreparedSQL(sqs, values); boolean error = false;
			 * boolean presents = false; Entry_value entry_value[] = null;
			 * Name_value name_value[] = null; int count = 100; int index = 0;
			 * while (count == 100) { Get_entry_list_result getEntryListResult =
			 * getBinding().get_entry_list(AUTH_RESULT.getId(), getTableName(),
			 * "", "id", 100 * index++, selectFields, 100, 0); count =
			 * getEntryListResult.getResult_count(); if(0 == count) break; if
			 * (getEntryListResult.getResult_count() > 1) { error = true; break;
			 * } if (1 == getEntryListResult.getResult_count()) { // update, if
			 * a records is found in a table if (presents) { error = true;
			 * break; } presents = true; entry_value =
			 * getEntryListResult.getEntry_list(); name_value =
			 * entry_value[0].getName_value_list(); } } if (error) { //Message
			 * error to log continue; } if (presents) { String id = "0"; for(int
			 * i=0; i<name_value.length; i++){ if( "id".equals(
			 * name_value[i].getName().toLowerCase()) ){ id =
			 * name_value[i].getValue(); break; } } List<Name_value>
			 * columnValues = getValuesToUpdate(rs); Name_value
			 * arrNameValuesToEnter[][] = new Name_value[ 1
			 * ][columnValues.size() + 1]; arrNameValuesToEnter[0][0] = new
			 * Name_value("id", id); for (int i=0; i<columnValues.size(); i++)
			 * arrNameValuesToEnter[0][i+1] = columnValues.get( i );
			 * binding.set_entries(AUTH_RESULT.getId(), getTableName(),
			 * arrNameValuesToEnter); if
			 * (!ApplicationData.ProcessingProgress.Step()) return; } else { //
			 * insert, if a records is not found in a table List<Name_value>
			 * columnValues = getValuesToUpdate(rs); Name_value
			 * arrNameValuesToEnter[][] = new Name_value[ 1
			 * ][columnValues.size()]; for (int i=0; i<columnValues.size(); i++)
			 * arrNameValuesToEnter[0][i] = columnValues.get( i );
			 * binding.set_entries(AUTH_RESULT.getId(), getTableName(),
			 * arrNameValuesToEnter); if
			 * (!ApplicationData.ProcessingProgress.Step()) return; } }
			 */

		} catch (SQLException e) {
			if (!ApplicationData.ProcessingProgress.Log(e)) {
				return;
			}
		} catch (RemoteException e) {
			if (!ApplicationData.ProcessingProgress.Log(e)) {
				return;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private List<Name_value> getValuesToUpdate(ResultSet rs) {

		List<Name_value> columnValues = new ArrayList<Name_value>();

		try {
			String columnsName[] = new String[rs.getMetaData().getColumnCount()];
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				columnsName[i - 1] = rs.getMetaData().getColumnName(i);
			}

			String value = "";

			for (String element : columnsName) {
				value = rs.getString(element);
				if (null != value) {
					columnValues.add(new Name_value(element.toLowerCase(),
							value));
				}
			}
		} catch (SQLException e) {
			if (!ApplicationData.ProcessingProgress.Log(e)) {
				return null;
			}
		}

		return columnValues;
	}

	private void insertRDBtoTDB(String table) {
		DataBaseTools.completeTransfer();
		if (getBinding() == null) {
			return;
		}

		try {
			Module_fields mf = binding.get_module_fields(AUTH_RESULT.getId(),
					getTableName());
			Field fields[] = mf.getModule_fields();

			String selectFields[] = new String[fields.length];

			for (int i = 0; i < fields.length; i++) {
				selectFields[i] = fields[i].getName();
			}

			int count = 100;
			int index = 0;
			while (count == 100) {

				Get_entry_list_result getEntryListResult = getBinding()
						.get_entry_list(AUTH_RESULT.getId(), getTableName(),
								"", "id", 100 * index++, selectFields, 100, 0);
				count = getEntryListResult.getResult_count();
				if (0 == count) {
					return;
				}

				Entry_value entryValue[] = getEntryListResult.getEntry_list();
				Name_value nameValues[];

				List<Object> values = new ArrayList<Object>();

				TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);
				List<Record> records = ti.getSchemaTable().getRecords();

				KeyInsensitiveMap datas = new KeyInsensitiveMap();

				for (Entry_value element : entryValue) {
					nameValues = element.getName_value_list();

					values.clear();

					for (Name_value element2 : nameValues) {

						String value = element2.getValue();
						if (value != null && !value.equals("")) {
							String unesc = htmlDecode(element2.getValue());
							datas.put(element2.getName(), unesc);
						}

					}

					DataProcessingInfo dpi = new DataProcessingInfo(
							ApplicationData.getTempDataBase().getDataBaseInfo(),
							table, records);
					DataBaseTools.insertData(dpi, datas, true);
					if (!ApplicationData.ProcessingProgress.Step()) {
						return;
					}
				}
			}

		} catch (Exception e) {
			if (!ApplicationData.ProcessingProgress.Log(e)) {
				return;
			}
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	@Override
	protected void TransformRDBtoTDB() {
		DataBaseTools.completeTransfer();
		TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);
		insertRDBtoTDB(ti.getTableName());
		DataBaseTools.completeTransfer();
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {

		List<RDBTable> tableList = new ArrayList<RDBTable>();

		if (getBinding() == null) {
			return tableList;
		}

		Module_list modules_list = getBinding().get_available_modules(
				AUTH_RESULT.getId());

		if (!"0".equals(modules_list.getError().getNumber())) {
			System.err.println("getTableList. Error: "
					+ modules_list.getError().getDescription());
		}
		String modules[] = modules_list.getModules();

		for (String element : modules) {
			tableList
					.add(new RDBTable(element.toString(), ETableMode.ReadWrite));
		}

		return tableList;
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {

		List<Record> res = new ArrayList<Record>();

		Field fields[] = getFields(getTableName());

		List<DBTypeRecord> availableTypes = dataBaseInfo.getAvailableTypes();

		/*
		 * Here are the names of types stored, that are available from this
		 * SugarCRM Node
		 */
		List<String> availableTypeNames = new ArrayList<String>();
		List<String> ignoredTypeNames = new ArrayList<String>();

		for (DBTypeRecord dbType : availableTypes) {
			availableTypeNames.add(dbType.getOriginalType());
		}
		for (Field field : fields) {
			if (availableTypeNames.contains(field.getType().toUpperCase())) {
				DBTypeRecord dbirec = DBTypeRecord.getRecordByOriginalType(
						getDataBaseInfo().getAvailableTypes(), field.getType()
								.toUpperCase());
				res.add(new Record(dbirec, field.getName(), dbirec.getLength(),
						!field.getType().equalsIgnoreCase("id"), dbirec
								.isSupportSign(), dbirec.isSupportPK()));
			} else {
				ignoredTypeNames.add(field.getName() + " [" + field.getType()
						+ "]");
			}
		}

		if (actions != null && !ignoredTypeNames.isEmpty()) {
			String ignoreMessage = "Couldn't import the following fields: \n";
			for (String ignoredType : ignoredTypeNames) {
				ignoreMessage += "- " + ignoredType + "\n";
			}
			actions.dialogAction(ignoreMessage);
		}

		return res;
	}

	public Field[] getFields(String tableName) throws Exception {
		Module_fields mf = null;
		try {
			mf = getBinding().get_module_fields(AUTH_RESULT.getId(), tableName);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		if (mf.getModule_fields().length < 1) {
			System.err.println("getField - tableName = `" + tableName
					+ "`. Error:" + mf.getError().getDescription());
		}
		return mf.getModule_fields();
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		// getBinding().
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	public static String htmlDecode(String str) {
		String[][] code = { { "&lt;", "<" }, { "&gt;", ">" },
				{ "&quot;", "\"" }, { "&#039;", "'" }, { "&amp;", "&" },
				{ "&#096;", "`" } };
		for (int i = 0; i < 5; i++) {
			str = str.replaceAll(code[i][0], code[i][1]);
		}
		return str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		binding = null;
		boolean res = false;
		try {
			if (getBinding() != null) {
				res = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!res) {
			if (isLastErrorMessageEmpty()) {
				lastErrorMessage = "Error: You must specify a valid username and password";
			}
		}

		return res;
	}

}