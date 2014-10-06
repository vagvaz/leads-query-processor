package com.apatar.output;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

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
import com.apatar.core.FolderPath;
import com.apatar.core.IPersistent;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.output.ui.OutputModeDescriptor;
//import com.apatar.filesystem.ui.FSModeDescriptor;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class OutputNode extends AbstractNonJdbcDataBaseNode {

	private static int MEGABAIT = 1024 * 1024;

	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			true, true, false);

	private final String separator = "/";

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

	public OutputNode() {
		super();
		title = "Output";
		mode = AbstractDataBaseNode.INSERT_MODE;
		outputConnectionList.clear();
	}

	@Override
	protected void TransformRDBtoTDB() {
		DataBaseTools.completeTransfer();
		OutputParams fp = ((OutputParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData());
		File file = new File(fp.getDirectory().getPath());
		try {
			File[] fl = file.listFiles();
			for (File element : fl) {
				createTable(element, file.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
		insertTDBtoRDB(mode);
		DataBaseTools.completeTransfer();
	}

	@Override
	public void createDatabaseParam(Wizard wizard) {

		JDialog wd = wizard.getDialog();

		wd.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new DBConnectionDescriptor(
					this, new JPropertySheetPage(wd),
					OutputModeDescriptor.IDENTIFIER, ApplicationData
							.classForName("com.apatar.output.OutputParams"),
					"db_connector", "output");
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new OutputModeDescriptor(this,
					DBConnectionDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard
					.registerWizardPanel(OutputModeDescriptor.IDENTIFIER,
							descriptor2);

			wizard
					.setKeyForReferringToDescription("help.connector.file_system");
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
		/*
		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();
		st.removeAllRecord();
		OutputParams fp = ((OutputParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData());

		File file = new File(fp.getDirectory().getPath());

		List<DBTypeRecord> recs = dbi.getAvailableTypes();
		st.addRecord(new Record(DBTypeRecord.getRecordByOriginalType(recs,
				"BLOB"), "Content", file.length(), false, false, false));
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
				*/

	}

	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		OutputParams fp = ((OutputParams) ApplicationData.getProject()
				.getProjectData(getConnectionDataID()).getData());
		File[] files = new File(fp.getDirectory().getPath()).listFiles();
		for (File element : files) {
			element.delete();
		}
	}

	@Override
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {
		return null;
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		return null;
	}

	@Override
	public ImageIcon getIcon() {
		return OutputUtils.READ_FS_NODE_ICON;
	}

	private void createTable(File file, String path) throws Exception {

		KeyInsensitiveMap record = createRecord(file, path);

		if (!ApplicationData.ProcessingProgress.Step()) {
			return;
		}

		TableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);

		DataBaseInfo dbi = ApplicationData.getTempDataBase().getDataBaseInfo();
		DataBaseTools.insertData(new DataProcessingInfo(dbi, ti.getTableName(),
				ti.getRecords(), ApplicationData.getTempJDBC()), record);

		Object obj = record.get("Content", false);
		if (obj != null) {
			((InputStream) obj).close();
		}

		if (file.isDirectory()) {
			File[] fl = file.listFiles();
			for (File element : fl) {
				createTable(element, path);
			}
		}
	}

	private KeyInsensitiveMap createRecord(File file, String path)
			throws IOException {
		KeyInsensitiveMap datas = new KeyInsensitiveMap();

		long size = file.length();
		if (size >= MEGABAIT) {
			ApplicationData.ProcessingProgress.Log("Read " + file.getPath()
					+ " size=" + size);
		}

		if (file.isDirectory()) {
			datas.put("isFolder", true);
		} else {
			datas.put("isFolder", false);
			datas.put("Content", new FileInputStream(file));
		}

		path = file.getPath().replace(path, "");
		path = path.replaceAll("\\\\", separator);

		datas.put("Name", file.getName());
		datas.put("Path", path);
		datas.put("Size", file.length());
		datas.put("Modified", new Date(file.lastModified()));
		datas.put("Read", file.canRead());
		datas.put("Write", file.canWrite());

		return datas;
	}

	private void insertTDBtoRDB(int mode) {
		try {
			TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
			ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),
					ApplicationData.tempDataBase.getJdbcParams(),
					ApplicationData.getTempDataBaseInfo());

			OutputParams fp = ((OutputParams) ApplicationData.getProject()
					.getProjectData(getConnectionDataID()).getData());
			while (rs.next()) {
				try {
					File newFile = new File(fp.getDirectory().getPath()
							+ separator + rs.getString("Path"));
					if (mode == AbstractDataBaseNode.UPDATE_MODE) {
						if (newFile.exists()) {
							continue;
						}
					}

					int byteArray = rs.getInt("isFolder");
					if (byteArray == 0) {
						String parent = newFile.getParent();
						File dirParent = new File(parent);
						if (!dirParent.exists()) {
							dirParent.mkdirs();
						}
						newFile.createNewFile();
					} else {
						newFile.mkdirs();
					}

					InputStream blob = rs.getBinaryStream("Content");
					if (blob != null) {
						// InputStream is = blob.getBinaryStream();
						ApplicationData.createFile(newFile, blob);
						blob.close();
					}

					byteArray = rs.getInt("Write");
					if (byteArray == 0) {
						newFile.setReadOnly();
					}
					Date date = rs.getDate("Modified");
					if (date != null) {
						long modified = rs.getDate("Modified").getTime();
						newFile.setLastModified(modified);
					}

					if (newFile.length() >= MEGABAIT) {
						ApplicationData.ProcessingProgress.Log("Copy "
								+ newFile.getPath());
					}

					if (!ApplicationData.ProcessingProgress.Step()) {
						return;
					}

				} catch (IOException e) {
					ApplicationData.ProcessingProgress.Log(e.getMessage());
				} catch (SQLException e) {
					ApplicationData.ProcessingProgress.Log(e.getMessage());
				}
			}
			rs.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	@Override
	public void afterEdit(boolean editRsult, AbstractApatarActions actions) {
		/*
		try {
			if (editRsult) {
				createSchemaTable(actions);

				SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
						.getSchemaTable();
				st.updateRecords(ApplicationData.convertToTempDbType(st
						.getRecords()));

				OutputParams fp = ((OutputParams) ApplicationData.getProject()
						.getProjectData(getConnectionDataID()).getData());
				File file = new File(fp.getDirectory().getPath());
				title = file.getName();
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
		*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#validateConnectionData()
	 */
	@Override
	public boolean validateConnectionData() {
		OutputParams params = (OutputParams) ApplicationData.getProject()
				.getProjectData(connectionDataId).getData();
		FolderPath fd = new FolderPath();
		fd.setPath("Giannis");
		params.setDirectory(fd);
		if ("".equals(params.getDirectory().toString())) {
			lastErrorMessage = "Directory should not be empty";
			return false;
		}
		return true;
	}

}
