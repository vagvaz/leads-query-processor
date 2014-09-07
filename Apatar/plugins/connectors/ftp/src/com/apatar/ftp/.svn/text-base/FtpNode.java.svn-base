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

package com.apatar.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.finj.FTPClient;
import org.finj.FTPConstants;
import org.finj.FTPException;
import org.finj.RemoteFile;
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
import com.apatar.core.IPersistent;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.ftp.ui.FtpFolderTreeDescriptor;
import com.apatar.ftp.ui.FtpModeDescriptor;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class FtpNode extends AbstractNonJdbcDataBaseNode {

	private static int MEGABAIT = 1024 * 1024;

	private FTPClient ftp = null;
	private final List<String> uri = new ArrayList<String>();
	private final String separator = "/";

	private String innerFtpUri = "/";

	private final List<File> listTmpFiles = new ArrayList<File>();

	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			true, true, false);

	static {
		List<DBTypeRecord> rcList = dbi.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Text, "VARCHAR", 0, 255, false,
				false));
		rcList.add(new DBTypeRecord(ERecordType.Binary, "BLOB", 0,
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

	public FtpNode() {
		super();
		title = "FTP";
		mode = AbstractDataBaseNode.INSERT_MODE;
	}

	private FTPClient getBinding() {
		if (null == ftp) {
			ftp = new FTPClient();
		}

		if (ftp.isConnected()) {
			return ftp;
		} else {
			try {
				ftp.isVerbose(false);
				ftp.open(getConnectionInfo().getUrl(), getConnectionInfo()
						.getPort());
				ftp.login(getConnectionInfo().getLogin(), getConnectionInfo()
						.getPassword().getValue().toCharArray());

			} catch (FTPException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ftp;
	}

	private void logoutFtpConnect() {
		try {
			if (ftp.isConnected()) {
				ftp.close();
			}
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {
		JDialog wd = wizard.getDialog();

		wd.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wd),
					FtpFolderTreeDescriptor.IDENTIFIER, ApplicationData
							.classForName("com.apatar.ftp.FtpConnection"),
					"db_connector", "ftp");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new FtpFolderTreeDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					FtpModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(FtpFolderTreeDescriptor.IDENTIFIER,
					descriptor2);

			WizardPanelDescriptor descriptor3 = new FtpModeDescriptor(this,
					FtpFolderTreeDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(FtpModeDescriptor.IDENTIFIER,
					descriptor3);

			wizard.setKeyForReferringToDescription("help.connector.ftp");
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			wizard.showModalDialog();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FtpConnection getConnectionInfo() {
		return (FtpConnection) ApplicationData.getProject().getProjectData(
				connectionDataId).getData();
	}

	public void setInnerFtpUri(String uri) {
		innerFtpUri = uri;
	}

	public String getInnerFtpUri() {
		return innerFtpUri;
	}

	@Override
	public void afterEdit(boolean editRsult, AbstractApatarActions actions) {
		if (editRsult) {
			String innerUri = getInnerFtpUri().substring(0,
					getInnerFtpUri().length() - 1);
			String url = getConnectionInfo().getUrl();
			int index = -1;

			// in the first check inner uri, if it's empty - set url like title
			if (!"".equals(innerUri) && !separator.equals(innerUri)) {
				index = innerUri.lastIndexOf(separator);
				if (-1 != index) {
					setTitle(innerUri.substring(index + 1, innerUri.length()));
				}
			} else {
				index = url.lastIndexOf(separator);
				if (-1 != index) {
					setTitle(url.substring(index + 1, url.length()));
				} else {
					setTitle(url);
				}
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
		} else {
			IPersistent persistent = (ApplicationData.getProject()
					.getProjectData(connectionDataId));
			if (persistent != null) {
				(persistent).initFromElement(bakupProjectData);
			}
			return;
		}
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	@Override
	public ImageIcon getIcon() {
		return FtpUtils.READ_FTP_NODE_ICON;
	}

	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws Exception {
		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.removeAllRecord();

		List<DBTypeRecord> recs = dbi.getAvailableTypes();
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BLOB"), "Content", 20000, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "Name", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"VARCHAR"), "Path", 255, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BIGINT"), "Size", 8, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"DATE"), "Modified", 8, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BOOLEAN"), "Read", 1, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BOOLEAN"), "Write", 1, false, false, false));
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BOOLEAN"), "isFolder", 1, false, false, false));
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
		insertTDBtoRDB(mode);
	}

	private void insertTDBtoRDB(int mode) {
		try {
			TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
			ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
					ApplicationData.tempDataBase.getJdbcParams(),
					ApplicationData.getTempDataBaseInfo());

			getBinding().setWorkingDirectory(getInnerFtpUri());

			getBinding().setDataType(FTPConstants.IMAGE_DATA_TYPE);
			while (rs.next()) {

				int isFileOrDir = rs.getInt("isFolder");

				try {
					String path = rs.getString("Path").replace(" ", "_");
					if (path.length() > 0) {
						if (separator.equals(path.substring(0, 1))
								|| "\\".equals(path.substring(0, 1))) {
							path = path.substring(1, path.length());
						}
					}

					// if current record is file
					if (isFileOrDir == 0) {
						Blob blob = rs.getBlob("Content");

						if (blob != null) {
							InputStream is = blob.getBinaryStream();
							String name = rs.getString("Name")
									.replace(" ", "_");
							String dir = path.replace(name, "");

							if (!"".equals(dir)) {
								try {
									/*
									 * RemoteFile rf[] =
									 * getBinding().getFileDescriptors
									 * (getInnerFtpUri() + dir,
									 * getConnectionInfo().getPassive() );
									 */
									getBinding().setWorkingDirectory(
											getInnerFtpUri() + dir);
								} catch (Exception e) {
									getBinding().makeDirectory(
											getInnerFtpUri() + dir);
								}
								/*
								 * if( 0 == rf.length )
								 * getBinding().makeDirectory( dir );
								 */
							}

							// getBinding().setDataType(FTPConstants.IMAGE_DATA_TYPE);

							// File file = new File("temp.file");
							// ApplicationData.createFile(file, is);

							getBinding().putFile(is, name,
									getConnectionInfo().getPassive());

							is.close();

							getBinding().setWorkingDirectory(getInnerFtpUri());
						}
						// if current record is file
					} else if (isFileOrDir == 1) {
						// RemoteFile rf[] = getBinding().getFileDescriptors(
						// path, getConnectionInfo().getPassive() );

						// if( 0 == rf.length )
						getBinding().makeDirectory(path);
					}

					ApplicationData.ProcessingProgress.Log("Uploading file: "
							+ path);

					if (!ApplicationData.ProcessingProgress.Step()) {
						logoutFtpConnect();
						return;
					}

				} catch (FTPException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} finally {
			logoutFtpConnect();
			DataBaseTools.completeTransfer();
		}
	}

	@Override
	protected void TransformRDBtoTDB() {

		try {
			DataBaseTools.completeTransfer();
			uri.clear();
			uri.add(getInnerFtpUri());

			getBinding().setWorkingDirectory(uri.get(0));
			RemoteFile[] files = getBinding().getFileDescriptors(
					getConnectionInfo().getPassive());
			for (int i = 0; i < files.length; i++) {
				try {
					if (!ApplicationData.ProcessingProgress.Status()) {
						logoutFtpConnect();
						return;
					}

					if (!files[i].getName().equals(".")
							&& !files[i].getName().equals("..")) {
						createTable(files[i], uri.get(0) + files[i].getName());
					}
				} catch (Exception e) {
					ApplicationData.ProcessingProgress.Log(e);
					e.printStackTrace();
				}
			}

			deleteTmpFiles();

		} catch (Exception e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
			logoutFtpConnect();
		}
	}

	private void deleteTmpFiles() {
		File f = null;
		for (Iterator<File> it = listTmpFiles.iterator(); it.hasNext();) {
			f = it.next();
			f.delete();
		}
	}

	private void createTable(RemoteFile file, String path) throws Exception {

		KeyInsensitiveMap record = createRecord(file, path);

		if (!ApplicationData.ProcessingProgress.Step()) {
			return;
		}

		TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

		DataBaseInfo dbi = ApplicationData.getTempDataBase().getDataBaseInfo();
		DataBaseTools.insertData(new DataProcessingInfo(dbi, ti.getTableName(),
				ti.getRecords(), ApplicationData.getTempJDBC()), record);

		Object obj = record.get("Content", true);

		/*
		 * if (obj != null) ((InputStream)obj).close();
		 */

		if (file.isDirectory()) {
			uri.add(file.getName());
			uri.add(separator);

			String folderPath = "";
			for (Iterator it = uri.iterator(); it.hasNext();) {
				folderPath += it.next();
			}

			RemoteFile[] files = getBinding().getFileDescriptors(folderPath,
					getConnectionInfo().getPassive());
			for (int i = 0; i < files.length; i++) {
				if (!ApplicationData.ProcessingProgress.Status()) {
					logoutFtpConnect();
					return;
				}

				if (!files[i].getName().equals(".")
						&& !files[i].getName().equals("..")) {
					createTable(files[i], folderPath + files[i].getName());
				}
			}

			uri.remove(uri.size() - 1);
			uri.remove(uri.size() - 1);
		}
	}

	private KeyInsensitiveMap createRecord(RemoteFile file, String path) {
		KeyInsensitiveMap datas = new KeyInsensitiveMap();

		long size = file.size();

		if (size >= MEGABAIT) {
			ApplicationData.ProcessingProgress.Log("Read " + file.getPath()
					+ " size=" + size);
		}

		if (file.isDirectory()) {
			datas.put("isFolder", true);
		} else {
			datas.put("isFolder", false);

			File f = null;

			try {
				f = File.createTempFile("apatar", ".tmp");
				listTmpFiles.add(f);

				FileOutputStream out = new FileOutputStream(f);
				getBinding().getFile(out, path,
						getConnectionInfo().getPassive());
				out.close();
			} catch (FileNotFoundException e) {
				ApplicationData.ProcessingProgress.Log(e);
				e.printStackTrace();
			} catch (FTPException e) {
				ApplicationData.ProcessingProgress.Log(e);
				e.printStackTrace();
			} catch (IOException e) {
				ApplicationData.ProcessingProgress.Log(e);
				e.printStackTrace();
			}

			try {
				datas.put("Content", new FileInputStream(f));
			} catch (FileNotFoundException e) {
				ApplicationData.ProcessingProgress.Log(e);
				e.printStackTrace();
			}
		}

		datas.put("Name", file.getName());
		datas.put("Path", path.replace(getInnerFtpUri(), ""));
		datas.put("Size", file.size());
		datas.put("Modified", new Date(file.lastModified()));
		datas.put("Read", file.canRead());
		datas.put("Write", file.canWrite());

		ApplicationData.ProcessingProgress.Log("Downloading file: " + path);

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
	public void deleteAllRecordsInRDB() throws Exception {
		getBinding().setWorkingDirectory(getInnerFtpUri());
		RemoteFile[] files = getBinding().getFileDescriptors(
				getConnectionInfo().getPassive());

		removeFiles(files, getInnerFtpUri());
		logoutFtpConnect();
	}

	private void removeFiles(RemoteFile files[], String path) {
		try {
			for (int i = 0; i < files.length; i++) {

				if (!ApplicationData.ProcessingProgress.Step()) {
					logoutFtpConnect();
					return;
				}

				if (files[i].isDirectory() && !".".equals(files[i].getName())
						&& !"..".equals(files[i].getName())) {

					String folderPAth = path + files[i].getName() + separator;

					removeFiles(getBinding().getFileDescriptors(folderPAth,
							getConnectionInfo().getPassive()), folderPAth);

					getBinding().setWorkingDirectory(path);
					getBinding().removeDirectory(path + files[i].getName());
				} else if (files[i].isFile()) {
					getBinding().deleteFile(path + files[i].getName());
				}

				ApplicationData.ProcessingProgress.Log("Removing resource: "
						+ path + files[i].getName());
			}
		} catch (FileNotFoundException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} catch (FTPException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} catch (IOException e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		}
	}

	@Override
	public Element saveToElement() {
		Element e = super.saveToElement();
		e.setAttribute("innerUri", innerFtpUri);

		return e;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		innerFtpUri = e.getAttributeValue("innerUri");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		FtpConnection conn = (FtpConnection) ApplicationData.getProject()
				.getProjectData(connectionDataId).getData();
		if ("".equals(conn.getUrl())
				|| (!"".equals(conn.getUrl()) && conn.getPort() == 0)) {
			lastErrorMessage = "Url and port should not be empty";
			return false;
		} else {
			return true;
		}
	}

}
