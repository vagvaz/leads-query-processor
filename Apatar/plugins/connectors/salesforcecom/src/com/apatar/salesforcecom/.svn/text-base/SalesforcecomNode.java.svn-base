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
��� You should have received a copy of the GNU General Public License ar
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.salesforcecom;

// http://www.salesforce.com/us/developer/docs/sforce70/wwhelp/wwhimpl/js/html/wwhelp.htm
import java.io.File;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import org.apache.axis.message.MessageElement;
import org.jdom.Element;

import propertysheet.JPropertySheetPage;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApatarException;
import com.apatar.core.ApatarRegExp;
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
import com.apatar.core.SynchronizationField;
import com.apatar.core.SynchronizationRecord;
import com.apatar.core.TableInfo;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.salesforcecom.ui.SFDCRecordSourceDescriptor;
import com.apatar.salesforcecom.ws.ApiFault;
import com.apatar.salesforcecom.ws.DeleteResult;
import com.apatar.salesforcecom.ws.DescribeGlobalResult;
import com.apatar.salesforcecom.ws.DescribeGlobalSObjectResult;
import com.apatar.salesforcecom.ws.DescribeSObjectResult;
import com.apatar.salesforcecom.ws.Field;
import com.apatar.salesforcecom.ws.InvalidIdFault;
import com.apatar.salesforcecom.ws.InvalidSObjectFault;
import com.apatar.salesforcecom.ws.LoginResult;
import com.apatar.salesforcecom.ws.QueryResult;
import com.apatar.salesforcecom.ws.SObject;
import com.apatar.salesforcecom.ws.SaveResult;
import com.apatar.salesforcecom.ws.SessionHeader;
import com.apatar.salesforcecom.ws.SforceServiceLocator;
import com.apatar.salesforcecom.ws.SoapBindingStub;
import com.apatar.salesforcecom.ws.UnexpectedErrorFault;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;
import com.sforce.async.BatchInfo;
import com.sforce.async.BatchRequest;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.async.OperationEnum;
import com.sforce.async.RestConnection;
import com.sforce.ws.ConnectorConfig;

public class SalesforcecomNode extends AbstractNonJdbcDataBaseNode {

	static final DataBaseInfo dataBaseInfo = new DataBaseInfo("", "", "", "",
			true, true, true, true, true, false);
	static {
		List<DBTypeRecord> rcList = dataBaseInfo.getAvailableTypes();
		// currently salesforce supports only string type
		rcList.add(new DBTypeRecord(ERecordType.Text, "STRING", 0, 16864,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "PICKLIST", 0, 16864,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "MULTIPICKLIST", 0,
				16864, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "COMBOBOX", 0, 2264192,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "REFERENCE", 0, 2264192,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "BASE64", 32000, 2264192,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BOOLEAN", 0, 1,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "CURRENCY", 0, 16864,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "TEXTAREA", 0, 16864,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "INT", 8, 8, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "DOUBLE", 8, 8, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal, "PERCENT", 8, 8, true,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "PHONE", 0, 16864, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "ID", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Timestamp, "DATETIME", 8, 8,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "URL", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "EMAIL", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Clob, "ANYTYPE", 0, 2264192,
				false, false));
	}

	private final HashMap<String, Integer> sqlType = new HashMap<String, Integer>();
	private String soqlQuery = "";

	SoapBindingStub binding = null;
	private String sessionId = "";
	private String serviceURL = "";
	private RestConnection SFDCrestConnection = null;
	private static final long RESTBATCHMAXSIZE = 8589934592L; // 8 Mb
	private String errorCSVfile = "";

	// when binding is returned it is defintely logged in.
	protected SoapBindingStub getBinding(boolean b, boolean createSession,
			boolean doSilentLogin) throws Exception {
		SforceServiceLocator service = new SforceServiceLocator();
		// if (binding == null)
		// {
		try {

			try {

				if (getConnection().isSandbox()) {
					setServiceURL(getConnection().getSandboxURL());
				} else {
					setServiceURL(getConnection().getSalesforceURL());
				}
				service.setSoapEndpointAddress(getServiceURL());
				binding = (SoapBindingStub) service.getSoap();
			} catch (ServiceException ex1) {
				System.out.println("Service Exception logging on \n"
						+ ex1.getMessage());
				lastErrorMessage = LogUtils.GetExceptionMessage(ex1);
				throw ex1;
			}

			if (doSilentLogin) {
				silentLogin();
			} else {
				login(b);
			}
		} catch (Exception e) {
			binding = null;
			e.printStackTrace();
			throw e;
		}
		// }
		return binding;
	}

	public SalesforcecomNode() {
		super();
		title = "Salesforce.com";
	}

	@Override
	public ImageIcon getIcon() {
		return SalesforcecomUtils.READ_SALESFORCECOM_NODE_ICON;
	}

	@Override
	public Element saveToElement() {
		Element readNode = super.saveToElement();

		Element soqlElement = new Element("soql");
		soqlElement.setText(getSoqlQuery());
		readNode.addContent(soqlElement);

		return readNode;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);

		String text = e.getChildText("soql");
		if (text != null) {
			setSoqlQuery(text);
		} else {
			setSoqlQuery("");
		}
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		JDialog wd = wizard.getDialog();

		wd.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wd),
					RecordSourceDescriptor.IDENTIFIER,
					ApplicationData
							.classForName("com.apatar.salesforcecom.SalesforceConnection"),
					"db_connector", "salesforce");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new SFDCRecordSourceDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					TableModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(RecordSourceDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new TableModeDescriptor(this,
					RecordSourceDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,
					descriptor3);

			wizard
					.setKeyForReferringToDescription("help.connector.salesforcecom");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			wizard.showModalDialog();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ShowDataConnectionCredentialsOnly() {
		Wizard wizard = new Wizard(ApatarUiMain.MAIN_FRAME);
		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this,
					new JPropertySheetPage(wizard.getDialog()),
					WizardPanelDescriptor.FINISH,
					ApplicationData
							.classForName("com.apatar.salesforcecom.SalesforceConnection"),
					"db_connector", "salesforce");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);
			wizard.showModalDialog();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// stub the better way is not found yet
	private MessageElement newMessageElement(Field field, Object value)
			throws Exception {
		MessageElement me = new MessageElement("", field.getName());

		org.w3c.dom.Element e = me.getAsDOM();
		e.removeAttribute("xsi:type");
		e.removeAttribute("xmlns:ns1");
		e.removeAttribute("xmlns:xsd");
		e.removeAttribute("xmlns:xsi");

		me = new MessageElement(e);

		if (value != null) {
			if (field.getSoapType().getValue().equalsIgnoreCase("xsd:dateTime")) {
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				me.setValue(df.format((Date) value));
			} else if (field.getSoapType().getValue().equalsIgnoreCase(
					"xsd:date")) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				me.setValue(df.format((Date) value));
			} else {
				if (value instanceof java.sql.Clob) {
					Clob clValue = (Clob) value;
					me.setObjectValue(clValue.getSubString(1, (int) clValue
							.length()));
				} else {
					me.setObjectValue(value);
				}
			}
		} else {
			me.setObjectValue(null);
		}

		return me;
	}

	// stub the better way is not found yet
	private Object GetValueObject(MessageElement me, Field fld) {
		if (me.getValue() == null) {
			return null;
		}

		String str = fld.getSoapType().getValue();
		if (str.equalsIgnoreCase("tns:ID")
				|| str.equalsIgnoreCase("xsd:string")
				|| str.equalsIgnoreCase("xsd:anyType")) {
			return me.getValue();
		}

		if (str.equalsIgnoreCase("xsd:dateTime")
				|| str.equalsIgnoreCase("xsd:date")) {
			try {
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Date res = df.parse(me.getValue());
				if (str.equalsIgnoreCase("xsd:dateTime")) {
					return new java.sql.Timestamp(res.getTime());
				} else {
					return res;
				}
			} catch (ParseException pe) {
				try {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					return df.parse(me.getValue());
				} catch (ParseException pe1) {
					System.out.println(pe1.getMessage());
					// return default
					return new Date(0);
				}
			}
		}
		if (str.equalsIgnoreCase("xsd:boolean")) {
			return Boolean.parseBoolean(me.getValue());
		}
		if (str.equalsIgnoreCase("xsd:double")) {
			return Double.parseDouble(me.getValue());
		}
		if (str.equalsIgnoreCase("xsd:int")) {
			return Integer.parseInt(me.getValue());
		}
		if (str.equalsIgnoreCase("xsd:anyType")) {
			return Integer.parseInt(me.getValue());
		}

		return me.getValue();
	}

	private boolean silentLogin() throws Exception {
		LoginResult loginResult;
		SalesforceConnection conn = getConnection();

		if (conn.IsEmpty()) {
			lastErrorMessage = "Please input salesforce.com credentials.";
			binding = null;
			return false;
		}

		binding.setTimeout(60000000);
		loginResult = binding.login(conn.userName, conn.password.getValue());

		System.out.println("The session id is: " + loginResult.getSessionId());
		System.out.println("The server url is: " + loginResult.getServerUrl());
		setServiceURL(loginResult.getServerUrl());
		setSessionId(loginResult.getSessionId());

		binding._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, loginResult
				.getServerUrl());

		binding.setMaintainSession(true);

		// Create a new session header object and set the session id to that
		// returned by the login
		SessionHeader sh = new SessionHeader();
		sh.setSessionId(loginResult.getSessionId());
		binding.setHeader(new SforceServiceLocator().getServiceName()
				.getNamespaceURI(), "SessionHeader", sh);
		return true;
	}

	private void login(boolean b) throws ApiFault, RemoteException {
		LoginResult loginResult;
		SalesforceConnection conn = getConnection();

		if (ApplicationData.SalesForceAppExchange) {
			if (conn.IsEmpty()) {
				JOptionPane.showMessageDialog(null,
						"Please input salesforce.com credentials.");
				ShowDataConnectionCredentialsOnly();
				// reget connection data
				conn = (SalesforceConnection) ApplicationData.getProject()
						.getProjectData(connectionDataId).getData();
				if (conn.IsEmpty()) {
					binding = null;
					return;
				}
			}

			// assume that credentials ok
			if (b) {
				if (JOptionPane
						.showConfirmDialog(
								null,
								"Are you sure you want to connect to SalesForce.com?",
								"Establishing Salesforce.com connection!",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
					binding = null;
					return;
				}
			}
		}
		binding.setTimeout(60000000);
		loginResult = binding.login(conn.userName, conn.password.getValue());

		if (ApplicationData.ProcessingProgress != null) {
			ApplicationData.ProcessingProgress.Log("The session id is: "
					+ loginResult.getSessionId());
			ApplicationData.ProcessingProgress.Log("The server url is: "
					+ loginResult.getServerUrl());
		}
		setServiceURL(loginResult.getServerUrl());
		setSessionId(loginResult.getSessionId());

		binding._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, loginResult
				.getServerUrl());

		binding.setMaintainSession(true);

		// Create a new session header object and set the session id to that
		// returned by the login
		SessionHeader sh = new SessionHeader();
		sh.setSessionId(loginResult.getSessionId());
		binding.setHeader(new SforceServiceLocator().getServiceName()
				.getNamespaceURI(), "SessionHeader", sh);
		return;
	}

	// get list of tables
	@Override
	public List<RDBTable> getTableList() throws Exception {
		List<RDBTable> rv = new ArrayList<RDBTable>();
		DescribeGlobalResult describeGlobalResult = null;
		// binding can be empty
		if (getBinding(true, true, false) == null) {
			return rv;
		}

		describeGlobalResult = getBinding(false, false, false).describeGlobal();
		DescribeGlobalSObjectResult[] types = describeGlobalResult
				.getSobjects();

		for (DescribeGlobalSObjectResult element : types) {
			rv.add(new RDBTable(element.getName(), ETableMode.ReadWrite));
		}
		return rv;
	}

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		if (connectionDataId == -1) {
			return;
		}

		// binding can be empty
		if (getBinding(false, true, false) == null) {
			return;
		}

		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();

		st.updateRecords(getFieldList(null));
	}

	private Field getFieldByName(String fName, Field[] fields) {
		if (fName == null || "".equals(fName)) {
			return null;
		}
		for (Field field : fields) {
			if (fName.equalsIgnoreCase(field.getName())) {
				return field;
			}
		}
		return null;
	}

	@Override
	protected void TransformRDBtoTDB() {
		// binding can be empty
		try {
			if (getBinding(false, true, false) == null) {
				return;
			}

			DescribeSObjectResult[] describeSObjectResults;

			describeSObjectResults = getBinding(false, false, false)
					.describeSObjects(new String[] { getTableName() });
			Field[] fields = describeSObjectResults[0].getFields();

			String selectSQL = getSoqlQuery();

			if (("select * from " + getTableName())
					.equalsIgnoreCase(getSoqlQuery())
					|| "".equals(getSoqlQuery())) {
				selectSQL = new String("select ");
				for (int i = 0; i < fields.length; i++) {
					if (i > 0) {
						selectSQL += ", ";
					}
					// String field =
					// dataBaseInfo.getStartSymbolEdgingFieldName() +
					// fields[i].getName() +
					// dataBaseInfo.getFinishSymbolEdgingFieldName();
					selectSQL += fields[i].getName();
				}
				selectSQL += " from " + getTableName();
			}

			TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);
			SoapBindingStub binding = getBinding(false, false, false);
			try {
				QueryResult qr;
				if (getConnection().isReturnDeletedRecords()) {
					qr = binding.queryAll(selectSQL);
				} else {
					qr = binding.query(selectSQL);
				}

				// getRecords returns null if there is no objects
				if (qr.getRecords() == null) {
					return;
				}

				boolean continueLoop = true;
				while (continueLoop) {
					// process the query results
					for (int i = 0; i < qr.getRecords().length; i++) {
						SObject con = qr.getRecords()[i];
						MessageElement[] objFields = con.get_any();

						KeyInsensitiveMap datas = new KeyInsensitiveMap();

						boolean skipId = (con.getId() != null);
						if (skipId) {
							datas.put("Id", new String(con.getId()));
						}
						for (MessageElement objField : objFields) {
							String name = objField.getName();
							if ("id".equalsIgnoreCase(name)) {
								continue;
							}
							Object obj = GetValueObject(objField,
									getFieldByName(name, fields));
							if (obj == null) {
								List<Record> recs = ti.getRecords();
								int fieldType = Record.getRecordByFieldName(
										recs, name).getSqlType();
								obj = new JdbcObject(null, fieldType);
							}
							datas.put(name, obj);
						}

						DataBaseTools.insertData(
								new DataProcessingInfo(ApplicationData
										.getTempDataBase().getDataBaseInfo(),
										ti.getTableName(), ti.getRecords(),
										ApplicationData.getTempJDBC()), datas);

						if (!ApplicationData.ProcessingProgress.Step()) {
							return;
						}
					}
					if (qr.isDone()) {
						continueLoop = false;
					} else {
						qr = binding.queryMore(qr.getQueryLocator());
					}
				}
			} catch (ApiFault af) {
				if (!ApplicationData.ProcessingProgress
						.Log("Failed to execute query succesfully, error message was:"
								+ af.getExceptionMessage())) {
					return;
				}
			} catch (Exception ex) {
				if (!ApplicationData.ProcessingProgress
						.Log("Failed to execute query succesfully, error message was:"
								+ ex.getMessage())) {
					return;
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

	private RestConnection getSFDCrestConnection() {
		if (SFDCrestConnection != null) {
			return SFDCrestConnection;
		}
		ConnectorConfig config = new ConnectorConfig();
		config.setSessionId(getSessionId());
		String restEndpoint = getServiceURL().substring(0,
				getServiceURL().indexOf("Soap/"))
				+ "async/17.0/";
		config.setRestEndpoint(restEndpoint);
		// This should only be false when doing debugging.
		config.setCompression(true);
		// Set this to true to see HTTP requests and responses on stdout
		config.setTraceMessage(false);
		try {
			setSFDCrestConnection(new RestConnection(config));
		} catch (Exception e) {
			e.printStackTrace();
			setSFDCrestConnection(null);
		}
		return SFDCrestConnection;
	}

	private com.sforce.async.SObject getRESTSObjectWithData(Field[] fields,
			ResultSet rs) throws Exception {
		com.sforce.async.SObject sob = new com.sforce.async.SObject();
		for (Field field : fields) {
			Object value = rs.getObject(field.getName());
			if (value != null && !value.equals("")) {
				if (field.getSoapType().getValue().equalsIgnoreCase(
						"xsd:dateTime")) {
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					sob.setField(field.getName(), df.format(rs
							.getTimestamp(field.getName())));
				} else if (field.getSoapType().getValue().equalsIgnoreCase(
						"xsd:date")) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					sob.setField(field.getName(), df.format(rs.getDate(field
							.getName())));
				} else {
					sob
							.setField(field.getName(), rs.getString(field
									.getName()));
				}
			}
		}
		return sob;
	}

	private int getRESTSObjectWithData(Field[] fields, ResultSet rs,
			com.sforce.async.SObject sob) throws Exception {
		int res = 0;
		String val = "";
		for (Field field : fields) {
			Object value = rs.getObject(field.getName());
			val = "";
			if (value != null && !value.equals("")) {
				if (field.getSoapType().getValue().equalsIgnoreCase(
						"xsd:dateTime")) {
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					val = df.format(rs.getTimestamp(field.getName()));
				} else if (field.getSoapType().getValue().equalsIgnoreCase(
						"xsd:date")) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					val = df.format(rs.getDate(field.getName()));
				} else {
					val = rs.getString(field.getName());
				}
				sob.setField(field.getName(), val);
				res += val.length();
			}
		}
		return res;
	}

	private SalesforceConnection getConnection() {
		return (SalesforceConnection) ApplicationData.getProject()
				.getProjectData(connectionDataId).getData();

	}

	protected void insertTDBtoRDB() {
		try {
			TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
			// binding can be empty
			if (getBinding(false, true, false) == null) {
				return;
			}

			DescribeSObjectResult[] describeSObjectResults;
			describeSObjectResults = getBinding(false, false, false)
					.describeSObjects(new String[] { getTableName() });
			Field[] fields = describeSObjectResults[0].getFields();

			// work the way if there is no field requried then skip it

			// int recordsCount =
			// DataBaseTools.getRecordsCount(ti.getTableName(),
			// ApplicationData.getTempJDBC(), ApplicationData
			// .getTempDataBaseInfo());
			ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());

			ArrayList<SObject> sObjs = new ArrayList<SObject>();
			int count = 0;
			long restBatchSize = 0L;
			int currentBatch = 0;
			long currentBatchRecordSize = 0L;

			List<BatchRequest> batchRequestsList = new ArrayList<BatchRequest>();
			List<BatchInfo> batchInfoList = new ArrayList<BatchInfo>();
			String jobId = "";
			JobInfo jobResult = null;
			if (getConnection().isUseBulkApi()) {
				JobInfo job = new JobInfo();
				job.setObject(getTableName());
				job.setOperation(OperationEnum.insert);
				jobResult = getSFDCrestConnection().createJob(job);
				jobId = jobResult.getId();
				System.out.println("New job ID=" + jobId + " in "
						+ jobResult.getState() + " state.");
				batchRequestsList.add(getSFDCrestConnection().createBatch(
						jobResult));
			}

			while (rs.next()) {
				if (getConnection().isUseBulkApi()) {
					addDataToBatch(currentBatchRecordSize, fields, rs,
							restBatchSize, currentBatch, batchRequestsList,
							batchInfoList, jobResult);
				} else {
					SObject sObj = new SObject();
					sObj.setType(getTableName());

					MessageElement[] me2 = createMessageElements(fields, rs,
							null, null);
					// return array of element to pass to salesforce.

					sObj.set_any(me2);

					sObjs.add(sObj);

					if (++count > 199) {
						if (!createObjects(sObjs, true)) {
							return;
						}
						sObjs.clear();
						count = 0;
					}
				}
			}
			if (getConnection().isUseBulkApi()) {
				bulkDataSend(jobId, batchInfoList, batchRequestsList);
			} else {
				createObjects(sObjs, true);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (SQLException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
	}

	private void addDataToBatch(long currentBatchRecordSize, Field[] fields,
			ResultSet rs, long restBatchSize, int currentBatch,
			List<BatchRequest> batchRequestsList,
			List<BatchInfo> batchInfoList, JobInfo jobResult) throws Exception {
		com.sforce.async.SObject restSob = new com.sforce.async.SObject();
		currentBatchRecordSize = getRESTSObjectWithData(fields, rs, restSob);
		if ((currentBatchRecordSize + restBatchSize) >= RESTBATCHMAXSIZE) {
			// we have to send current data to SF and create new
			// batch
			batchInfoList.add(batchRequestsList.get(currentBatch)
					.completeRequest());
			batchRequestsList.add(getSFDCrestConnection()
					.createBatch(jobResult));
			currentBatch++;
			restBatchSize = 0;
		}
		restBatchSize += currentBatchRecordSize;
		batchRequestsList.get(currentBatch).addSObject(restSob);
	}

	private void bulkDataSend(String jobId, List<BatchInfo> batchInfoList,
			List<BatchRequest> batchRequestsList) throws Exception {
		batchInfoList.add(batchRequestsList.get(batchRequestsList.size() - 1)
				.completeRequest());
		JobInfo cls = new JobInfo();
		cls.setState(JobStateEnum.Closed);
		cls.setId(jobId);
		JobInfo closedJob = getSFDCrestConnection().updateJob(cls);
		System.out.println("Job `" + closedJob.getId()
				+ "` closes. Waiting for data loading finish.");
		ApplicationData.ProcessingProgress.Log("Job `" + closedJob.getId()
				+ "` closes. Waiting for data loading finish.");
		int numberDone;
		List<sfRestErrorRecord> errorsList = new ArrayList<sfRestErrorRecord>();
		setErrorCSVfile("");
		do {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}

			BatchInfo[] statusList = getSFDCrestConnection().getBatchInfoList(
					jobId).getBatchInfo();
			numberDone = 0;
			for (BatchInfo b : statusList) {
				if (b.getState() == BatchStateEnum.Completed
						|| b.getState() == BatchStateEnum.Failed) {
					numberDone++;
					com.sforce.async.SaveResult[] batchResults = getSFDCrestConnection()
							.getBatchResult(jobId, b.getId()).getResult();
					for (com.sforce.async.SaveResult saveResult : batchResults) {
						if (!saveResult.isSuccess()) {
							errorsList.add(new sfRestErrorRecord(saveResult));
						}
					}
				}
				System.out.println("Status of batch " + b.getId() + ": "
						+ b.getState());
			}
		} while (numberDone < batchInfoList.size());
		ApplicationData.ProcessingProgress.Log("Batches processed: "
				+ String.valueOf(numberDone));
		ApplicationData.ProcessingProgress.Log("Rows with errors: "
				+ String.valueOf(errorsList.size()));
		if (errorsList.size() > 0) {
			System.err.println("creating errors dump file");
			if (dumpErrorsToCSV(errorsList)) {
				System.err.println("dump file `" + getErrorCSVfile()
						+ "` created");
			} else {
				System.err.println("Error creating errors dump");
			}
		}
	}

	private boolean dumpErrorsToCSV(List<sfRestErrorRecord> errList) {
		if (!createErrorCSV()) {
			return false;
		}
		try {
			TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
			String selectSQL = "select * from " + ti.getTableName()
					+ " where \"Id\" = ?";
			System.out.println(selectSQL);
			PreparedStatement selectPs = ApplicationData
					.getTempJDBCConnection().prepareStatement(selectSQL);
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(errorCSVfile);
				// creating CSV header
				String header = "";
				for (Record rec : ti.getRecords()) {
					header += rec.getFieldName() + ",";
				}
				header += "ErrorMessage";
				pw.println(header);

				for (sfRestErrorRecord sfRestErrorRecord : errList) {
					DataBaseTools.setDataToPS(selectPs, sfRestErrorRecord
							.getId(), Types.VARCHAR, 1);
					ResultSet rs = selectPs.executeQuery();
					String row = "";
					while (rs.next()) {
						for (Record field : ti.getRecords()) {
							String value = rs.getString(field.getFieldName());
							if (value == null) {
								value = "";
							}
							row += value + ",";
						}
						row += sfRestErrorRecord.getErrorMessage();
					}
					pw.println(row);
				}
			} finally {
				pw.close();
				selectPs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean createErrorCSV() {
		setErrorCSVfile(getConnection().getErrorCSVpath().getPath()
				+ File.separator
				+ "errors"
				+ (new SimpleDateFormat("yyyyMMddHHmmss"))
						.format(new GregorianCalendar().getTime()) + ".csv");
		File err = new File(errorCSVfile);
		System.err.println(errorCSVfile);
		try {
			return err.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean createObjects(ArrayList<SObject> sObjs, boolean isCreate)
			throws InvalidSObjectFault, UnexpectedErrorFault, InvalidIdFault,
			RemoteException, Exception {
		if (sObjs.size() < 1) {
			return true;
		}
		SObject[] stSObj = new SObject[sObjs.size()];
		SaveResult[] results;
		if (isCreate) {
			results = getBinding(false, false, false).create(
					sObjs.toArray(stSObj));
		} else {
			results = getBinding(false, false, false).update(
					sObjs.toArray(stSObj));
		}

		if (!writeToLog(results)) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.apatar.core.AbstractNonJdbcDataBaseNode#moveDataFromTempToReal(java
	 * .util.List)
	 */
	@Override
	public void moveDataFromTempToReal(List<String> identificationFields,
			TableInfo inputTi) {
		List<String> ids = this.identificationFields;
		this.identificationFields = identificationFields;
		try {
			updateTDBtoRDB(inputTi);
		} finally {
			this.identificationFields = ids;
		}
	}

	private void updateTDBtoRDB(TableInfo inpuTI) {
		DataBaseTools.completeTransfer();

		DescribeSObjectResult[] describeSObjectResults;
		Field[] fields = null;
		try {
			int batchMaxCountCreate = 200;
			int batchMaxCountUpdate = 200;
			int recordsUpdated = 0;
			int recordsInserted = 0;
			describeSObjectResults = getBinding(false, true, false)
					.describeSObjects(new String[] { getTableName() });
			fields = describeSObjectResults[0].getFields();

			TableInfo iTI = null;
			TableInfo oti = null;

			if (inpuTI == null) {
				oti = getTiForConnection(OUT_CONN_POINT_NAME);
				iTI = getTiForConnection(IN_CONN_POINT_NAME);
			} else {
				iTI = inpuTI;
				oti = getTiForConnection(OUT_CONN_POINT_NAME);
			}
			boolean canUseUpsert = false;
			if (identificationFields.size() == 1) {
				if (!(canUseUpsert = "Id".equals(identificationFields.get(0)))) {
					for (Field field : fields) {
						if (identificationFields.get(0).equals(field.getName())) {
							try {
								canUseUpsert = field.getExternalId();
							} catch (Exception e) {
							}
						}
					}
				}
			}
			// TODO remove when implement upsert for non-bulk API
			if (!getConnection().isUseBulkApi()) {
				canUseUpsert = false;
			}
			List<Record> mapped_fields = getColumnsForUpdate();
			if (!canUseUpsert && getConnection().isUseBulkApi()) {
				boolean idFound = false;
				for (Record mappedField : mapped_fields) {
					if ("Id".equals(mappedField.getFieldName())) {
						idFound = true;
						break;
					}
				}
				if (!idFound) {
					System.err.println("Cannot  Update data in bulk "
							+ "mode without 'Id' field mapped");
					return;
				}
			}

			ResultSet rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBase().getDataBaseInfo());

			SQLCreationData sqd = new SQLCreationData(oti.getSchemaTable()
					.getRecords(), getTableName());
			clearSynchronizationRecord();

			ArrayList<SObject> sObjsCreate = new ArrayList<SObject>();
			ArrayList<SObject> sObjsUpdate = new ArrayList<SObject>();
			int countCreate = 0;
			int countUpdate = 0;

			long restBatchSize = 0L;
			int currentBatch = 0;
			long currentBatchRecordSize = 0L;

			List<BatchRequest> batchRequestsList = new ArrayList<BatchRequest>();
			List<BatchInfo> batchInfoList = new ArrayList<BatchInfo>();
			String jobId = "";
			JobInfo jobResult = null;
			if (getConnection().isUseBulkApi()) {
				JobInfo job = new JobInfo();
				job.setObject(getTableName());
				if (canUseUpsert) {
					job.setOperation(OperationEnum.upsert);
					job.setExternalIdFieldName(identificationFields.get(0));
				} else {
					job.setOperation(OperationEnum.update);
					job.setExternalIdFieldName("Id");
				}

				jobResult = getSFDCrestConnection().createJob(job);
				jobId = jobResult.getId();
				System.out.println("New job ID=" + jobId + " in "
						+ jobResult.getState() + " state.");
				batchRequestsList.add(getSFDCrestConnection().createBatch(
						jobResult));
			}

			while (rs.next()) {
				MessageElement[] me;
				QueryResult qr = null;
				if (!canUseUpsert) {
					KeyInsensitiveMap values = DataBaseTools.GetDataFromRS(rs);

					// restrict full set of values to identification fields
					KeyInsensitiveMap selectDatas = new KeyInsensitiveMap();
					for (String substr : values.keySet()) {
						if (identificationFields.contains(substr)) {
							selectDatas.put(substr, values.get(substr, false));
						}
					}

					SQLQueryString sqs = DataBaseTools.CreateSelectString(
							getDataBaseInfo(), sqd, selectDatas);

					if (sqs == null) {
						return;
					}

					// String specialOperators[] = {"\\&", "\\|", "\\!", "\\{",
					// "\\}", "\\[", "\\]", "\\(", "\\)", "\\^", "~", "\\*",
					// ":",
					// "\"", "\\'", "\\+", "-"};
					// String specialOperators[] = {"\\'"};
					for (String key : values.keySet()) {
						Object obj = values.get(key, true);
						obj = ((JdbcObject) obj).getValue();
						if (obj instanceof String) {
							String str = (String) obj;

							// for (int i= 0; i < specialOperators.length; i++)
							// {
							// System.out.println("specialOperator=" +
							// specialOperators[i] + "index=" + i);
							String repl = str;
							if (str.matches("'")) {
								repl = str.replaceAll("'", "\\\\'");
							} else if (str.matches("(.*?)'(.*?)")) {
								repl = str.replaceAll("(.*?)'(.*?)",
										"$1\\\\\\\\'$2");
							}
							// String repl = str.replaceAll("(.*?)'(.*?)",
							// "$1\\\\'$2");

							// System.out.println(str);
							// }
							values.put(key, repl);
							// System.out.println(repl);
						}
					}

					sqs = getPreparedSQL(sqs, values);

					if (sqs == null) {
						continue;
					}
					// this code have to be implemented while fixing issue
					// APT-635
					// System.out.println(sqs.query);
					// sqs.query = checkIfQueryIsNotTooLong(sqs.query);
					qr = binding.query(sqs.query);

					if (qr.getSize() > 1) {
						ApplicationData.ProcessingProgress
								.Log(" More than one record matching the identification field(s) found.");
						continue;
					}
				}
				if (getConnection().isUseBulkApi()) {
					addDataToBatch(currentBatchRecordSize, fields, rs,
							restBatchSize, currentBatch, batchRequestsList,
							batchInfoList, jobResult);
				} else {

					List<String> fieldsToNull = new ArrayList<String>();
					me = createMessageElements(fields, rs, fieldsToNull,
							(inpuTI == null ? null : inpuTI.getRecords()));
					if (qr.getSize() == 1) {
						SObject[] sobjects = qr.getRecords();
						sobjects[0].set_any(me);
						String[] fieldsToNull2 = new String[fieldsToNull.size()];
						fieldsToNull.toArray(fieldsToNull2);
						// sobjects[0].setFieldsToNull(fieldsToNull2);
						sObjsUpdate.add(sobjects[0]);

						if (++countUpdate >= batchMaxCountUpdate) {
							if (!createObjects(sObjsUpdate, false)) {
								return;
							}
							sObjsUpdate.clear();
							countUpdate = 0;
						}
					} else {
						// here we have to check arrays for create and update
						// records
						SObject sobject = checkForRecord(me, sObjsUpdate);
						if (sobject != null) {
							// object found, than we have to update it instead
							// of inserting new one
							sobject.set_any(me);
						} else {
							sobject = checkForRecord(me, sObjsCreate);
							if (sobject != null) {
								sobject.set_any(me);
							} else {
								sobject = new SObject();
								sobject.setType(getTableName());
								sobject.set_any(me);
								sObjsCreate.add(sobject);
								if (++countCreate >= batchMaxCountCreate) {
									if (!createObjects(sObjsCreate, true)) {
										return;
									}
									sObjsCreate.clear();
									countCreate = 0;
								}
								recordsInserted++;
							}
						}
					}

					if (mode == AbstractDataBaseNode.SYNCHRONIZE_MODE) {
						addSynchronizationRecord(rs);
					}
				}
			}
			if (getConnection().isUseBulkApi()) {
				bulkDataSend(jobId, batchInfoList, batchRequestsList);
			} else {

				System.out.println("recordsInserted: "
						+ String.valueOf(recordsInserted));
				System.out.println("recordsUpdated: "
						+ String.valueOf(recordsUpdated));
				createObjects(sObjsCreate, true);
				createObjects(sObjsUpdate, false);

				if (mode == AbstractDataBaseNode.SYNCHRONIZE_MODE) {
					SQLQueryString sqs = DataBaseTools.CreateSelectString(
							getDataBaseInfo(), sqd, null);
					List<SynchronizationRecord> srsReal = createSynchronizationRecords(
							binding.query(sqs.query), fields);
					synchronization(srsReal, syncRecords, identificationFields);
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

	private MessageElement getMessageElementFromArrayByName(
			MessageElement[] meArr, String name) {
		for (MessageElement me : meArr) {
			if (me.getName().equals(name)) {
				return me;
			}
		}

		return null;
	}

	private SObject checkForRecord(MessageElement[] mElement,
			ArrayList<SObject> sObjects) {
		List<String> iFields = new ArrayList<String>(identificationFields);

		for (SObject row : sObjects) {
			boolean found = false;
			for (String field : iFields) {
				MessageElement source = getMessageElementFromArrayByName(row
						.get_any(), field);
				MessageElement dest = getMessageElementFromArrayByName(
						mElement, field);
				if (source == null || dest == null) {
					found = false;
					break;
				}
				if (compareTwoMessageElements(source, dest)) {
					found = true;
				} else {
					found = false;
					break;
				}
			}
			if (found) {
				return row;
			}
		}
		return null;
	}

	private boolean compareTwoMessageElements(MessageElement source,
			MessageElement dest) {
		if (source.getName().equals(dest.getName())
				&& source.getValue().equals(dest.getValue())) {
			return true;
		}
		return false;
	}

	private String checkIfQueryIsNotTooLong(String originalQuery)
			throws Exception {
		String result = "";
		if (originalQuery.length() > 10000) {
			System.err.println("Query for Salesforce is too long ("
					+ String.valueOf(originalQuery.length())
					+ " chars). Trying to reduce it.");
			List<String> queryParts = ApatarRegExp.getSubstrings(
					"(?i)select\\s(.*?)\\sfrom\\s(.*?)\\s(where.*)",
					originalQuery);
			String[] tables = queryParts.get(1).split(",");
			String fields = queryParts.get(0);
			for (String table_1 : tables) {
				String ff = table_1.trim() + "\\.";
				fields = fields.replaceAll(ff, "");
			}
			result = "SELECT " + fields + " FROM " + queryParts.get(1) + " "
					+ queryParts.get(2);
			if (result.length() > 10000) {
				throw new ApatarException(
						"Query to check if record exists in Salesforce is longer than 10000 chars. Salesforce cannot process queries longer than 10000 chars.");
			}
			System.out.println("Reduces query:");
			System.out.println(result);
			return result;
		}
		return originalQuery;
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
		TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
		try {
			if (DataBaseTools.getRecordsCount(ti.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo()) == 0) {
				System.out.println("No records to process.");
				return;
			}
		} catch (Exception e) {
			return;
		}
		if (mode == AbstractDataBaseNode.INSERT_MODE) {
			insertTDBtoRDB();
		} else {
			updateTDBtoRDB(null);
		}
		DataBaseTools.completeTransfer();
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		// binding can be empty
		if (getBinding(false, true, false) == null) {
			return;
		}
		try {
			getBinding(false, false, false).describeSObjects(
					new String[] { getTableName() });
			String selectSQL = new String("select Id from ");
			selectSQL += getTableName();
			QueryResult qr = getBinding(false, false, false).query(selectSQL);

			// getRecords returns null if there is no objects
			if (qr.getRecords() == null) {
				return;
			}

			List<String> ids = new ArrayList<String>();
			for (int i = 0; i < qr.getRecords().length; i++) {
				SObject con = qr.getRecords()[i];
				ids.add(con.getId());
			}

			// the salesforce allows to delete not more than 200 records at a
			// moment
			int stopIndex = 0;
			while (stopIndex < ids.size()) {
				int subsize = (ids.size() - stopIndex) > 199 ? 199 : ids.size()
						- stopIndex;
				List<String> sublist = ids.subList(stopIndex, stopIndex
						+ subsize);
				stopIndex += subsize;
				String str[] = new String[sublist.size()];
				DeleteResult[] dr = getBinding(false, false, false).delete(
						sublist.toArray(str));
				writeToLog(dr);
				// ApplicationData.ProcessingProgress.Log(String.format("%d
				// records deleted", subsize-countErrors));
			}

		} catch (InvalidSObjectFault e) {
			e.printStackTrace();
		} catch (UnexpectedErrorFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions action)
			throws Exception {
		sqlType.clear();
		List<Record> res = new ArrayList<Record>();
		DescribeSObjectResult[] describeSObjectResults;
		describeSObjectResults = getBinding(false, true, false)
				.describeSObjects(new String[] { getTableName() });
		Field[] fields = describeSObjectResults[0].getFields();
		for (Field element : fields) {
			String fieldType = element.getType().getValue();
			String fieldName = element.getName();
			System.out.println(fieldName + " (" + fieldType + ")");

			DBTypeRecord dbirec = DBTypeRecord.getRecordByOriginalType(
					getDataBaseInfo().getAvailableTypes(), fieldType);

			res.add(new Record(dbirec, fieldName, element.getByteLength(),
					element.isNillable(), dbirec.isSupportSign(), dbirec
							.isSupportPK()));

			sqlType.put(fieldName, dbirec.getSqlType());
		}
		return res;
	}

	private MessageElement[] createMessageElements(Field[] fields,
			ResultSet rs, List<String> isNull, List<Record> otherThanNull) {
		List<MessageElement> me = new ArrayList<MessageElement>();
		for (int i = 1; i < fields.length; i++) {
			try {
				Object value = rs.getObject(fields[i].getName());
				if (otherThanNull != null) {
					boolean doSetToNull = true;
					for (Record record : otherThanNull) {
						if (record.getFieldName().equals(fields[i].getName())) {
							doSetToNull = false;
							break;
						}
					}
					if (doSetToNull) {
						value = null;
					}
				}
				if (value != null && !value.equals("")) {
					MessageElement nme = newMessageElement(fields[i], value);
					if (nme != null) {
						me.add(nme);
					}
				} else {
					if (isNull != null) {
						if (fields[i].isNillable()) {
							isNull.add(fields[i].getName());
						}
					}
				}
			} catch (SQLException se) {
				// if there is no required field skip this field
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MessageElement[] me2 = new MessageElement[me.size()];
		me.toArray(me2);
		return me2;
	}

	private boolean writeToLog(SaveResult[] results) {
		for (SaveResult element : results) {
			writeToLog(element.getErrors());
		}
		// step
		if (!ApplicationData.ProcessingProgress.Step()) {
			return false;
		}
		return true;
	}

	private boolean writeToLog(DeleteResult[] results) {
		for (DeleteResult element : results) {
			writeToLog(element.getErrors());
		}
		// step
		if (!ApplicationData.ProcessingProgress.Step()) {
			return false;
		}
		return true;
	}

	private boolean writeToLog(com.apatar.salesforcecom.ws.Error[] errs) {
		if (errs == null || errs.length == 0) {
			return true;
		}
		for (int j = 0; j < errs.length; j++) {
			if (errs[j] != null
					&& !(ApplicationData.ProcessingProgress.Log("ERROR: "
							+ errs[j].getMessage()))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dataBaseInfo;
	}

	private List<SynchronizationRecord> createSynchronizationRecords(
			QueryResult qr, Field[] fields) {
		ArrayList<SynchronizationRecord> srs = new ArrayList<SynchronizationRecord>();
		for (int i = 0; i < qr.getRecords().length; i++) {
			SynchronizationRecord sr = new SynchronizationRecord();
			SObject con = qr.getRecords()[i];
			MessageElement[] objFields = con.get_any();

			for (int j = 0; j < objFields.length; j++) {
				String fn = objFields[j].getName();
				// if (identificationFields.contains(fn))
				sr.addField(new SynchronizationField(fn, GetValueObject(
						objFields[j], fields[j])));
			}
			srs.add(sr);
		}
		return srs;
	}

	@Override
	public void deleteRecordsInRDB(SynchronizationRecord rec) {
		Object obj = rec.getFieldValue("ID");
		try {
			binding = getBinding(false, true, false);
			binding.delete(new String[] { obj.toString() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SQLQueryString getPreparedSQL(SQLQueryString sqs,
			KeyInsensitiveMap data) throws Exception {
		// replace from end
		for (String field : sqs.queryOrder) {
			Object ob = data.get(field, true);
			if (ob instanceof JdbcObject) {
				ob = ((JdbcObject) ob).getValue();
			}
			String fieldvalue = null;
			if (ob != null) {
				fieldvalue = ob instanceof String ? String.format("'%s'", ob
						.toString()) : ob.toString();
			} else {
				ApplicationData.ProcessingProgress.Log("Value for Field "
						+ field + " unknown");
				fieldvalue = "NULL";
			}
			try {
				sqs.query = sqs.query.replaceFirst(field + "[\\s]*?=\\s*?\\?",
						field
								+ " = "
								+ fieldvalue.replaceAll("\\$",
										java.util.regex.Pattern
												.quote("\\\\\\$")));
			} catch (Exception e) {
				System.err.println("Error preparing SQL query.");
				System.err.println("sqs.query = `" + sqs.query + "`");
				System.err.println("field = `" + field + "`; fieldvalue = `"
						+ fieldvalue + "`");
				throw new ApatarException(
						"Error preparing SQL query. Message: `"
								+ e.getMessage() + "`");
			}
			/*
			 * StringTokenizer st = new StringTokenizer(sqs.query, "?"); if
			 * (!st.hasMoreTokens())\ return sqs; String str1 = ""; String str2
			 * = ""; try { str1 = st.nextToken(); } catch (Exception e) { str1 =
			 * ""; } try { str2 = st.nextToken(); } catch (Exception e) { str2 =
			 * ""; } sqs.query = str1 + fieldvalue +
			 * str2;//sqs.query.replaceFirst("\\?", fieldvalue);
			 */}
		return sqs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.apatar.core.AbstractDataBaseNode#executeUpdateQuery(java.lang.String)
	 */
	@Override
	public int executeUpdateQuery(String query) {
		int result = 0;
		try {
			QueryResult qr = getBinding(false, false, false).query(query);
			result = qr.getSize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.apatar.core.AbstractNonJdbcDataBaseNode#getTotalRecodrsCount(com.
	 * apatar.core.TableInfo)
	 */
	@Override
	public int getTotalRecodrsCount(TableInfo ti) {
		QueryResult qr = null;
		try {
			qr = getBinding(false, false, false).query(
					"select count(*) from " + ti.getTableName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (qr == null) {
			return 0;
		} else {
			return Integer.valueOf(qr.getRecords(0).toString());
		}
	}

	/**
	 * @return the soqlQuery
	 */
	public String getSoqlQuery() {
		return soqlQuery;
	}

	/**
	 * @param soqlQuery
	 *            the soqlQuery to set
	 */
	public void setSoqlQuery(String soqlQuery) {
		this.soqlQuery = soqlQuery;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the serviceURL
	 */
	public String getServiceURL() {
		return serviceURL;
	}

	/**
	 * @param serviceURL
	 *            the serviceURL to set
	 */
	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	/**
	 * @param sFDCrestConnection
	 *            the sFDCrestConnection to set
	 */
	public void setSFDCrestConnection(RestConnection sFDCrestConnection) {
		SFDCrestConnection = sFDCrestConnection;
	}

	/**
	 * @return the errorSCVfile
	 */
	public String getErrorCSVfile() {
		return errorCSVfile;
	}

	/**
	 * @param errorSCVfile
	 *            the errorSCVfile to set
	 */
	public void setErrorCSVfile(String errorSCVfile) {
		errorCSVfile = errorSCVfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		lastErrorMessage = "";
		try {
			getBinding(true, true, true);
			if (binding == null) {
				return false;
			}
		} catch (Exception e) {
			if (isLastErrorMessageEmpty()) {
				lastErrorMessage = LogUtils.GetExceptionMessage(e);
			}
			return false;
		}
		return true;
	}
}
