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

package com.apatar.email;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

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
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.email.ui.EmailModeDescriptor;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class EmailNode extends AbstractNonJdbcDataBaseNode {

	private Store store = null;
	private Folder folder = null;

	private boolean deleteAllInRDB = false;
	private int countAttach = 0;

	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			true, true, false);

	static {
		List<DBTypeRecord> rcList = dbi.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.LongText, "LONGVARCHAR", 0,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BINARY", 0,
				Long.MAX_VALUE, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "BIGINT", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Date, "DATE", 8, 8, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Boolean, "BOOLEAN", 1, 1,
				false, false));
		rcList.add(new DBTypeRecord(ERecordType.Text, "CHAR", 1, 1, false,
				false));
	}

	public EmailNode() {
		super();
		title = "E-mail";
		mode = AbstractDataBaseNode.INSERT_MODE;
	}

	private Session getSessionForSend(String host, String user,
			String password, boolean isSsl) {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.debug", "true");
		boolean authRequired = true;
		if ("".equals(user) && "".equals(password)) {
			props.put("mail.smtp.auth", "false");
			authRequired = false;
		} else {
			props.put("mail.smtp.auth", "true");
		}
		props.put("mail.smtp.port", String.valueOf(getConnectionInfo()
				.getOutgoingMailPort()));
		props.put("mail.transport.protocol", "smtp" + (isSsl ? "s" : ""));

		Session session;
		if (authRequired) {
			session = Session.getDefaultInstance(props, new Authentification(
					user, password));
		} else {
			session = Session.getDefaultInstance(props);
		}
		return session;
	}

	private Store getStoreForGet(String type, String server, String user,
			String password, boolean isSsl) {
		Properties props = System.getProperties();
		// props.setProperty("mail.store.protocol", "pop3" );
		if ("imap".equalsIgnoreCase(type)) {
			props.setProperty("mail.imap.port", String
					.valueOf(getConnectionInfo().getIncomingMailPort()));
		} else {
			props.setProperty("mail.pop3.port", String
					.valueOf(getConnectionInfo().getIncomingMailPort()));
		}
		Session session = Session.getInstance(props, new Authentification(user,
				password));
		session.setDebug(false);

		try {
			store = session.getStore(type.toLowerCase() + (isSsl ? "s" : ""));
			store.connect(server, user, password);
		} catch (NoSuchProviderException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} catch (MessagingException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		}

		return store;
	}

	private void closeStoreForGet() {
		try {
			if (null != folder && folder.isOpen()) {
				folder.close(false);
			}
			if (null != store) {
				store.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		JDialog wd = wizard.getDialog();

		wd.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wd),
					EmailModeDescriptor.IDENTIFIER, ApplicationData
							.classForName("com.apatar.email.EmailConnection"),
					"db_connector", "email");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new EmailModeDescriptor(this,
					DBConnectionDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(EmailModeDescriptor.IDENTIFIER,
					descriptor2);

			wizard.setKeyForReferringToDescription("help.connector.email");
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
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.removeAllRecord();

		List<DBTypeRecord> recs = dbi.getAvailableTypes();
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BIGINT"), "Id", 8, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "From", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "To", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "CC", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "Subject", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"LONGVARCHAR"), "Body", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"DATE"), "Date", 8, false, false, false));

		int count = getCountAttachedFiles();
		for (int i = 0; i < count; i++) {
			st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
					"BINARY"), "Attach_" + i, 20000, false, false, false));
			st
					.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(
							recs, "VARCHAR"), "AttachFileName_" + i, 255,
							false, false, false));
		}
	}

	private EmailConnection getConnectionInfo() {
		return (EmailConnection) ApplicationData.getProject().getProjectData(
				connectionDataId).getData();
	}

	@Override
	public void afterEdit(boolean editRsult, AbstractApatarActions actions) {
		String get = getConnectionInfo().getIncomingMailServer();
		String send = getConnectionInfo().getOutgoingMailServer();

		if (null == get || "".equals(get)) {
			if (null == send || "".equals(send)) {
				setTitle(title);
			} else {
				setTitle(send);
			}
		} else {
			setTitle(get);
		}

		try {
			createSchemaTable(null);
			SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
					.getSchemaTable();
			st.updateRecords(ApplicationData.convertToTempDbType(st
					.getRecords()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		try {
			DataBaseTools.completeTransfer();
			String server = getConnectionInfo().getOutgoingMailServer();
			String user = getConnectionInfo().getLogin();
			String password = getConnectionInfo().getPassword().getValue();
			boolean useSsl = getConnectionInfo().getUseSsl();
			Session sess = getSessionForSend(server, user, password, useSsl);

			TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
			ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
					ApplicationData.tempDataBase.getJdbcParams(),
					ApplicationData.getTempDataBaseInfo());

			while (rs.next()) {
				if (!ApplicationData.ProcessingProgress.Step()) {
					return;
				}

				String from = rs.getString("From");
				String to = rs.getString("To");
				String cc = rs.getString("CC");
				String subject = rs.getString("Subject");
				String body = rs.getString("Body");
				Date date = rs.getDate("Date");

				from = (null == from ? "" : from);
				to = (null == to ? "" : to);
				cc = (null == cc ? "" : cc);
				subject = (null == subject ? "" : subject);
				body = (null == body ? "" : body);

				Message msg = new MimeMessage(sess);

				try {
					if (null == from || "".equals(from)) {
						from = java.net.InetAddress.getLocalHost().toString();
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}

				msg.setFrom(new InternetAddress(from));
				msg.setRecipients(Message.RecipientType.TO, InternetAddress
						.parse(to, false));

				if (null != cc && !"".equals(cc)) {
					msg.setRecipients(Message.RecipientType.CC, InternetAddress
							.parse(cc, false));
				}

				msg.setSubject(subject);
				msg.setText(body);
				msg.setSentDate((date == null ? new Date() : date));

				if (0 < getCountAttachedFiles()) {

					MimeMultipart mime = new MimeMultipart();
					MimeBodyPart mbp1 = new MimeBodyPart();
					mbp1.setText(body);
					mime.addBodyPart(mbp1);
					msg.setContent(mime);
					// InputStream in = null;
					// BodyPart part = null;
					for (int i = 0; i < getCountAttachedFiles(); i++) {
						InputStream in = rs.getBinaryStream("Attach_" + i);
						if (in == null) {
							continue;
						}
						File attachFile = new File("atach" + i + ".cmp");
						ApplicationData.createFile(attachFile, in);
						DataHandler pdfDH = new DataHandler(new FileDataSource(
								attachFile));
						BodyPart part = new MimeBodyPart(in);
						part.setDataHandler(pdfDH);
						String fileName = rs.getString("AttachFileName_" + i);
						if (fileName != null) {
							part.setFileName(fileName);
						}
						mime.addBodyPart(part);
					}

					msg.setContent(mime);
				}

				Transport.send(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}

	@Override
	protected void TransformRDBtoTDB() {
		try {
			DataBaseTools.completeTransfer();
			String serverType = getConnectionInfo().getIncomingMailServerType()
					.toString();
			String server = getConnectionInfo().getIncomingMailServer();
			String user = getConnectionInfo().getLogin();
			String password = getConnectionInfo().getPassword().getValue();
			boolean ssl = getConnectionInfo().getUseSsl();

			if (null != server && !"".equalsIgnoreCase(server)) {

				try {
					folder = getStoreForGet(serverType, server, user, password,
							ssl).getDefaultFolder();

					if (folder == null) {
						closeStoreForGet();
						return;
					}
					folder = folder.getFolder("INBOX");

					if (folder == null) {
						closeStoreForGet();
						return;
					}
					folder.open(Folder.READ_ONLY);

					try {

						for (Message msg : folder.getMessages()) {
							if (!ApplicationData.ProcessingProgress.Step()) {
								closeStoreForGet();
								return;
							}
							createTable(msg);
							System.gc();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						closeStoreForGet();
					}

				} catch (MessagingException e) {
					e.printStackTrace();
				} finally {
					closeStoreForGet();
				}
			} else if (null != getTiForConnection(IN_CONN_POINT_NAME)) {
				TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);

				try {
					ResultSet rs = DataBaseTools.getRSWithAllFields(ti
							.getTableName(), ApplicationData.tempDataBase
							.getJdbcParams(), ApplicationData
							.getTempDataBaseInfo());

					try {
						createTable(rs);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	private void createTable(Message msg) throws Exception {

		KeyInsensitiveMap record = createRecord(msg);

		TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

		DataBaseInfo dbi = ApplicationData.getTempDataBase().getDataBaseInfo();
		DataBaseTools.insertData(new DataProcessingInfo(dbi, ti.getTableName(),
				ti.getRecords(), ApplicationData.getTempJDBC()), record);
	}

	private void createTable(ResultSet rs) throws Exception {
		TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

		if (rs.next()) {
			do {
				KeyInsensitiveMap record = new KeyInsensitiveMap();
				record.put("Id", new JdbcObject(rs.getInt("Id"), Record
						.getRecordByFieldName(ti.getRecords(), "Id")
						.getSqlType()));
				record.put("From", new JdbcObject(rs.getString("From"), Record
						.getRecordByFieldName(ti.getRecords(), "From")
						.getSqlType()));
				record.put("To", new JdbcObject(rs.getString("To"), Record
						.getRecordByFieldName(ti.getRecords(), "To")
						.getSqlType()));
				record.put("CC", new JdbcObject(rs.getString("CC"), Record
						.getRecordByFieldName(ti.getRecords(), "CC")
						.getSqlType()));
				record.put("Subject", new JdbcObject(rs.getString("Subject"),
						Record.getRecordByFieldName(ti.getRecords(), "Subject")
								.getSqlType()));
				record.put("Body", new JdbcObject(rs.getString("Body"), Record
						.getRecordByFieldName(ti.getRecords(), "Body")
						.getSqlType()));
				record.put("Date", new JdbcObject(rs.getDate("Date"), Record
						.getRecordByFieldName(ti.getRecords(), "Date")
						.getSqlType()));

				if (0 < getCountAttachedFiles()) {
					for (int i = 0; i < getCountAttachedFiles(); i++) {
						record.put("Attach_" + i, rs.getBinaryStream("Attach_"
								+ i));
					}
				}

				DataBaseInfo dbi = ApplicationData.getTempDataBase()
						.getDataBaseInfo();
				DataBaseTools.insertData(new DataProcessingInfo(dbi, ti
						.getTableName(), ti.getRecords(), ApplicationData
						.getTempJDBC()), record);

			} while (rs.next());
		}// if
	}

	private KeyInsensitiveMap createRecord(Message msg) {
		KeyInsensitiveMap datas = new KeyInsensitiveMap();

		try {
			Address from[] = msg.getFrom();
			Address to[] = msg.getRecipients(Message.RecipientType.TO);
			Address cc[] = msg.getRecipients(Message.RecipientType.CC);
			String textBody = "";
			String contentType = msg.getContentType();

			if (-1 != contentType.indexOf("text/plain")) {
				textBody = msg.getContent().toString();
			} else if (-1 != contentType.indexOf("multipart/mixed")) {
				MimeMultipart mime = (MimeMultipart) msg.getContent();

				BodyPart bodyPart = null;

				int attached = 0;
				for (int i = 0; i < mime.getCount(); i++) {
					bodyPart = mime.getBodyPart(i);
					if (bodyPart.getFileName() == null) {
						Object content = bodyPart.getContent();
						if (content instanceof MimeMultipart) {
							bodyPart = ((MimeMultipart) content).getBodyPart(0);
						}
						textBody = bodyPart.getContent().toString();
					} else {
						if (attached < countAttach) {
							datas.put("Attach_" + attached, bodyPart
									.getInputStream());
							datas.put("AttachFileName_" + attached, bodyPart
									.getFileName());
						}
						attached++;
					}
				}
			}

			String fromStr = "";
			String toStr = "";
			String ccStr = "";

			if (null != from) {
				for (Address addr : from) {
					fromStr += addr.toString() + ", ";
				}
				if (2 <= fromStr.length()) {
					fromStr = fromStr.substring(0, fromStr.length() - 2);
				}
			}

			if (null != to) {
				for (Address addr : to) {
					toStr += addr.toString() + ", ";
				}
				if (2 <= toStr.length()) {
					toStr = toStr.substring(0, toStr.length() - 2);
				}
			}

			if (null != cc) {
				for (Address addr : cc) {
					ccStr += addr.toString() + ", ";
				}
				if (2 <= ccStr.length()) {
					ccStr = ccStr.substring(0, ccStr.length() - 2);
				}
			}

			String subj = msg.getSubject();
			if (subj == null) {
				subj = "";
			}
			Object sentDate = msg.getSentDate();
			if (sentDate == null) {
				sentDate = "";
			}
			datas.put("Id", msg.getMessageNumber());
			datas.put("From", fromStr);
			datas.put("To", toStr);
			datas.put("CC", ccStr);
			datas.put("Subject", subj);
			datas.put("Body", textBody);
			datas.put("Date", sentDate);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return datas;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		return null;
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {
		return null;
	}

	@Override
	public boolean isDeleteAllInRDB() {
		return deleteAllInRDB;
	}

	@Override
	public void setDeleteAllInRDB(boolean deleteAllInRDB) {
		this.deleteAllInRDB = deleteAllInRDB;
	}

	public int getCountAttachedFiles() {
		return countAttach;
	}

	public void setCountAttachedFiles(int count) {
		countAttach = count;
	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		String serverType = getConnectionInfo().getIncomingMailServerType()
				.toString();
		String server = getConnectionInfo().getIncomingMailServer();
		String user = getConnectionInfo().getLogin();
		String password = getConnectionInfo().getPassword().getValue();
		boolean ssl = getConnectionInfo().getUseSsl();

		if (null != server && !"".equalsIgnoreCase(server)) {
			try {
				folder = getStoreForGet(serverType, server, user, password, ssl)
						.getDefaultFolder();

				if (folder == null) {
					closeStoreForGet();
					return;
				}
				folder = folder.getFolder("INBOX");

				if (folder == null) {
					closeStoreForGet();
					return;
				}
				folder.open(Folder.READ_WRITE);
				folder.setFlags(folder.getMessages(), new Flags(
						Flags.Flag.DELETED), true);

			} catch (MessagingException e) {
				e.printStackTrace();
			} finally {
				closeStoreForGet();
			}
		}// if
	}

	@Override
	public ImageIcon getIcon() {
		return EmailUtils.READ_EMAIL_NODE_ICON;
	}

	@Override
	public Element saveToElement() {
		Element e = super.saveToElement();
		e.setAttribute("countAttach", String.valueOf(getCountAttachedFiles()));

		return e;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		String cnt = e.getAttributeValue("countAttach");

		setCountAttachedFiles((null == cnt || "".equals(cnt) ? 0 : Integer
				.parseInt(cnt)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		if ("".equals(getConnectionInfo().getIncomingMailServer())
				&& "".equals(getConnectionInfo().getOutgoingMailServer())) {
			lastErrorMessage = "Incoming or Outgoing server address should not be empty";
			return false;
		}
		if (!"".equals(getConnectionInfo().getIncomingMailServer())
				&& getConnectionInfo().getIncomingMailPort() == 0) {
			lastErrorMessage = "Incoming server port should not be zero";
			return false;
		}
		if (!"".equals(getConnectionInfo().getIncomingMailServer())
				&& getConnectionInfo().getOutgoingMailPort() == 0) {
			lastErrorMessage = "Outgoing server port should not be zero";
			return false;
		}

		return true;
	}

}
