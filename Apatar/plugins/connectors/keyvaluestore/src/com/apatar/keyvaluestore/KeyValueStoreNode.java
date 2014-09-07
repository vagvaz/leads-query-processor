package com.apatar.keyvaluestore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.print.attribute.standard.OutputDeviceAssigned;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.keyvaluestore.gui.KVSModeDescriptor;
import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class KeyValueStoreNode extends AbstractNonJdbcDataBaseNode{
	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", true, true,
			true, true, false);
	private String mapperPath, reducerPath, combinerPath, xmlPath, outputFolder;
	
	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public String getMapperPath() {
		return mapperPath;
	}

	public void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}

	public String getReducerPath() {
		return reducerPath;
	}

	public void setReducerPath(String reducerPath) {
		this.reducerPath = reducerPath;
	}

	public String getCombinerPath() {
		return combinerPath;
	}

	public void setCombinerPath(String combinerPath) {
		this.combinerPath = combinerPath;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public KeyValueStoreNode(){
		super();
		title = "MapReduce";
		mode = AbstractDataBaseNode.INSERT_MODE;
	}
	
	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}
	
	@Override
	public void createDatabaseParam(Wizard wizard) {
		// TODO Auto-generated method stub
		JDialog wd = wizard.getDialog();

		wd.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new KVSModeDescriptor(this,
					DBConnectionDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(DBConnectionDescriptor.IDENTIFIER,
					descriptor1);
			wizard.setCurrentPanel(DBConnectionDescriptor.IDENTIFIER,
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

			wizard.showModalDialog();
		//} catch (ClassNotFoundException e) {
		//	e.printStackTrace();
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
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		JarOutputStream mapperJar, reducerJar, combinerJar;
		mapperJar = new JarOutputStream(new FileOutputStream(outputFolder+"/reducer.jar"),manifest);
		add(new File(mapperPath),mapperJar);
		mapperJar.close();
		reducerJar = new JarOutputStream(new FileOutputStream(outputFolder+"/reducer.jar"),manifest);
		add(new File(reducerPath),reducerJar);
		reducerJar.close();
		combinerJar = new JarOutputStream(new FileOutputStream(outputFolder+"/combiner.jar"),manifest);
		add(new File(combinerPath),combinerJar);
		combinerJar.close();
	}
	
	private void add(File source, JarOutputStream target) throws IOException{
		BufferedInputStream in = null;
		try{
			if(source.isDirectory()){
				String name = source.getPath().replace("\\", "/");
				if(!name.isEmpty()){
					if(!name.endsWith("/"))
						name+="/";
					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
				}
				for(File nestedFile:source.listFiles())
					add(nestedFile, target);
				return;
			}
			JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(source));
			byte[] buffer = new byte[1024];
			while (true){
				int count = in.read(buffer);
				if(count==-1)
					break;
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		}
		finally
		{
			if (in!=null)
				in.close();
		}
	}
	/*
	@Override
	protected void TransformTDBtoRDB(int mode) throws Exception {
//		DataBaseTools.completeTransfer();
//		insertTDBtoRDB(mode);
//		DataBaseTools.completeTransfer();
		
	}
	@Override
	protected void TransformRDBtoTDB() throws Exception {
		// TODO Auto-generated method stub
		
	}*/
	@Override
	public List<RDBTable> getTableList() throws Exception {
		return null;
	}
	@Override
	public List<Record> getFieldList(AbstractApatarActions action)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void deleteAllRecordsInRDB() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ImageIcon getIcon() {
		return KeyValueStoreUtils.READ_KEYVALUESTORE_NODE_ICON;
	}
	@Override
	public boolean validateConnectionData() {
		KeyValueStoreParams params = (KeyValueStoreParams) ApplicationData.getProject()
				.getProjectData(connectionDataId).getData();
		if ("".equals(params.getMapperPath().toString())) {
			lastErrorMessage = "Mapper Path should not be empty";
			return false;
		}
		return true;
	}

	@Override
	protected void TransformTDBtoRDB(int mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void TransformRDBtoTDB() {
		// TODO Auto-generated method stub
		
	}
	

}
