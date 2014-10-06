/**
 *
 * this is auto generated code
 *
 */

package tuc.ws.hotelsInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
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

import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

public class HotelsInfoNode extends AbstractNonJdbcDataBaseNode{
	
	
	//1) startSymbolEdgingTableName, 2)finishSymbolEdgingTableName, 3) startSymbolEdgingFieldName, 4) finishSymbolEdgingFieldName, 
	//5) supportUpdateMode, 6) supportInsertMode, 7)supportClearData, 8)useTableName, 9)supportDeleteMode	
	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", false, true,false, true, false); 

	/*
	 * These maps will hold the values of the
	 * operations arguments and returns respectively.
	 * key 	 is arg/ret name  (String)
	 * value is arg/ret value (Object)
	 */
	private HashMap<String, Object> argVals;
	private HashMap<String, Object> retVals;

	/*
	 * Here availableTypes are added to DataBaseInfo
	 */
	static {
		List<DBTypeRecord> rcList = dbi.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.Numeric,"NUMERIC",0,(int) Math.pow(2, 32),false,false)); //gt iparxei 2 fores auto???
		rcList.add(new DBTypeRecord(ERecordType.Numeric,"NUMERIC",0,(int) Math.pow(2, 32),false,false));
		rcList.add(new DBTypeRecord(ERecordType.Text,"TEXT",0,65000,false,false));
		rcList.add(new DBTypeRecord(ERecordType.Decimal,"DECIMAL",0,65000,false,false));
	}

	public HotelsInfoNode(){
		super();
		title = "hotelsinfo";

		argVals = new HashMap<String, Object>();
		retVals = new HashMap<String, Object>();
	}
	public ImageIcon getIcon(){
		return HotelsInfoUtils.HOTELSINFO_NODE_ICON;
	}

	protected void TransformRDBtoTDB() {
	}

	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
		argVals.clear();
		TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
		if(ti!=null){ //there are arguments
			List<Record> recs = ti.getRecords();

			try {
				ResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),ApplicationData.getTempJDBC(), ApplicationData.getTempDataBaseInfo());

				while (rs.next()) {
					for (Record rec : recs) {
						Object obj = rs.getObject(rec.getFieldName());
						if (obj != null) {
							argVals.put(rec.getFieldName(), obj);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		DataBaseTools.completeTransfer();
	}

	public void Transform(){
		TransformTDBtoRDB(INSERT_MODE);

		try {
			/*
			 * Here we select the right method to call
			 * from ConnectorFunctions,  according to the selected
			 * table (web service operation)
			 */
			Class functionsClass = Class.forName("tuc.ws.hotelsInfo.HotelsInfoFunctions");
			Method method = functionsClass.getMethod(getTableName(),argVals.getClass(),
			getTiForConnection(OUT_CONN_POINT_NAME).getClass(),getFieldList(null).getClass(),getTableName().getClass(),dbi.getClass());
			method.invoke(functionsClass.newInstance(),argVals, getTiForConnection(OUT_CONN_POINT_NAME),getFieldList(null),getTableName(),dbi);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		TransformRDBtoTDB();
	}

	@Override
	public Element saveToElement() {
		return super.saveToElement();
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
	}

	public void createDatabaseParam(Wizard wizard) {
		JDialog wizardDialog = wizard.getDialog();
		wizardDialog.setTitle(title + " Property");

		try {
			WizardPanelDescriptor descriptor1 = new RecordSourceDescriptor(this, DBConnectionDescriptor.IDENTIFIER,TableModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(RecordSourceDescriptor.IDENTIFIER,descriptor1);

			WizardPanelDescriptor descriptor2 = new TableModeDescriptor(this,RecordSourceDescriptor.IDENTIFIER,WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,descriptor2);
			wizard.setCurrentPanel(RecordSourceDescriptor.IDENTIFIER, Wizard.NEXT_BUTTON_ACTION_COMMAND);
			wizard.showModalDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SchemaTable getExpectedShemaTable() {
		List<Record> dr = null;
		try {
			dr = BuildDestinationRecordList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		SchemaTable rv = new SchemaTable();
		rv.updateRecords(dr);
		return rv;
	}

	// build record list to write to real database
	private List<Record> BuildDestinationRecordList() throws Exception {
		HotelsInfoTable table = HotelsInfoTableList.getTableByName(getTableName());
		List<Record> rv = new ArrayList<Record>();
		for (Record rec : getFieldList(null)) {
			if (table.getArguments().containsKey(rec.getFieldName())) {
				rv.add(rec);
			}
		}
		return rv;
	}

	public void createSchemaTable(AbstractApatarActions actions)
		throws Exception {

		if (connectionDataId == -1) {
			return;
		}

		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME).getSchemaTable();
		st.updateRecords(getFieldList(null));
	}

	public void deleteAllRecordsInRDB() throws Exception {

	}

	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}

	public List<Record> getFieldList(AbstractApatarActions action)
		throws Exception {

		List<Record> rl = new ArrayList<Record>();
		HotelsInfoTable table = HotelsInfoTableList.getTableByName(getTableName());
		HashMap<String,Object> merged = new HashMap<String, Object>();

		merged.putAll(table.getArguments());
		merged.putAll(table.getReturns());

		for(String srt : merged.keySet()){
			DBTypeRecord rec = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(), (String)merged.get(srt));
			rl.add(new Record(rec,srt,65000,true,true,false));
		}

		return rl;
	}

	public List<RDBTable> getTableList() throws Exception {
		List<RDBTable> list = new ArrayList<RDBTable>();
		for (HotelsInfoTable table : HotelsInfoTableList.getHotelsInfoTables().values()) {
			list.add(new RDBTable(table.getTableName(), table.getMode()));
		}
		return list;
	}

}
