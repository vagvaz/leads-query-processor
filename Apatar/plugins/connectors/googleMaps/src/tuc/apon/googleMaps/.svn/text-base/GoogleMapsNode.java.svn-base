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

package tuc.apon.googleMaps;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.apache.xmlrpc.XmlRpcException;
import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractNonJdbcDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.RDBTable;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.flickr.FlickrTable;
import com.apatar.flickr.FlickrTableList;

import com.apatar.ui.wizard.DBConnectionDescriptor;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

/**
 * @author apon
 */
public class GoogleMapsNode extends AbstractNonJdbcDataBaseNode {

	/*
	 * TODO: not sure about the arguments of the
	 * DataBaseInfo() constructor
	 */
	static DataBaseInfo dbi = new DataBaseInfo("", "", "", "", false, true,
			false, true, false);
	
	/*
	 * These maps will hold the values of the
	 * operations arguments and returns respectively.
	 * key 	 is arg/ret name  (String)
	 * value is arg/ret value (String)/(Object)
	 */
	private HashMap<String, Object> argVals;
	private HashMap<String, Object> retVals;


	static {
		List<DBTypeRecord> rcList = dbi.getAvailableTypes();
		rcList.add(new DBTypeRecord(ERecordType.LongText, "LONGVARCHAR", 0,
				255, false, false));
		rcList.add(new DBTypeRecord(ERecordType.Numeric, "NUMERIC",0,
				255, false, false));	//maybe 255 must be grater
	}

	public GoogleMapsNode() {
		super();
		inputConnectionList.remove(0);	//remove the default connection point and add our own manually
		inputConnectionList.add(new ConnectionPoint("inputA",true,this,false,1));
		inputConnectionList.add(new ConnectionPoint("inputB",true,this,false,2));
		title = "GoogleMaps";
		
		argVals = new HashMap<String, Object>();
		retVals = new HashMap<String, Object>();
	}

	@Override
	public DataBaseInfo getDataBaseInfo() {
		return dbi;
	}
	
	@Override
	public ImageIcon getIcon() {
		return GoogleMapsUtils.GOOGLE_MAPS_NODE_ICON;
	}

	@Override
	/*
	 * createDatabaseParam() also handles the node's GUI
	 */
	public void createDatabaseParam(Wizard wizard) {		
		JDialog wizardDialog = wizard.getDialog();
		wizardDialog.setTitle(title + " Property");
		
		try {
			WizardPanelDescriptor descriptor1 = new RecordSourceDescriptor(
					this, DBConnectionDescriptor.IDENTIFIER,
					TableModeDescriptor.IDENTIFIER);
			wizard.registerWizardPanel(RecordSourceDescriptor.IDENTIFIER,
					descriptor1);

			WizardPanelDescriptor descriptor2 = new TableModeDescriptor(this,
					RecordSourceDescriptor.IDENTIFIER,
					WizardPanelDescriptor.FINISH);
			wizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,
					descriptor2);

			wizard.setCurrentPanel(RecordSourceDescriptor.IDENTIFIER, 
					Wizard.NEXT_BUTTON_ACTION_COMMAND);

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
		GoogleMapsTable table = GoogleMapsTableList.getTableByName(getTableName());
		List<Record> rv = new ArrayList<Record>();

		for (Record rec : getFieldList(null)) {
			if (table.getArguments().containsKey(rec.getFieldName())) {
				rv.add(rec);
			}
		}
		return rv;
	}
	
	@Override
	public void createSchemaTable(AbstractApatarActions actions)
			throws ClassNotFoundException, SQLException, Exception {
		
		if (connectionDataId == -1) {
			return;
		}

		SchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME)
				.getSchemaTable();

		st.updateRecords(getFieldList(null));
	}

	/*
	 * This function loads data from Apatar's internal database to the real one.
	 * Practically it loads data from Apatar to the connector
	 * @see http://www.apatarforge.org/wiki/display/ADM/How+to+Create+Your+Own+Connector+or+Operation 
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#TransformTDBtoRDB(int)
	 */
	protected void TransformTDBtoRDB(int mode) {
		DataBaseTools.completeTransfer();
		argVals.clear();
		
		List<String> addresses = new LinkedList<String>();
		List<String> refAddresses = new LinkedList<String>();
		List<String> comments = new LinkedList<String>();
		
//		TableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);
		TableInfo tiA = getTiForConnection("inputA");
		TableInfo tiB = getTiForConnection("inputB");
		
		List<Record> recsInputA =null;
		if(tiA != null) recsInputA = tiA.getRecords();
		
		List<Record> recsInputB = null;
		if(tiB != null) recsInputB = tiB.getRecords();

		try {
			if(tiA!=null){
				ResultSet rs_inputA = DataBaseTools.getRSWithAllFields(tiA.getTableName(),
						ApplicationData.getTempJDBC(), ApplicationData
						.getTempDataBaseInfo());

				while (rs_inputA.next()) {
					for (Record rec : recsInputA) {
						Object obj = rs_inputA.getObject(rec.getFieldName());
						if (obj != null) {
							if(rec.getFieldName().equals("address")){
								addresses.add((String)obj);
							}
							else if( rec.getFieldName().equals("addressInfo")){
								comments.add((String)obj);
							}
							else if( rec.getFieldName().equals("referenceAddress")){
								refAddresses.add((String)obj);
							}
							else
								argVals.put(rec.getFieldName(), obj);
						}
					}
				}
			}
			if(tiB!=null){
				ResultSet rs_inputB = DataBaseTools.getRSWithAllFields(tiB.getTableName(),
						ApplicationData.getTempJDBC(), ApplicationData
						.getTempDataBaseInfo());

				while (rs_inputB.next()) {
					for (Record rec : recsInputB) {
						Object obj = rs_inputB.getObject(rec.getFieldName());
						if (obj != null) {
							if(rec.getFieldName().equals("address")){
								addresses.add((String)obj);
							}
							else if( rec.getFieldName().equals("addressInfo")){
								comments.add((String)obj);
							}
							else if( rec.getFieldName().equals("referenceAddress")){
								refAddresses.add((String)obj);
							}
							else
								argVals.put(rec.getFieldName(), obj);
						}
					}
				}
			}
			argVals.put("address", addresses);
			argVals.put("referenceAddress", refAddresses);
			argVals.put("addressInfo", comments);
//			argVals.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		DataBaseTools.completeTransfer();
	}

	/*
	 * This function loads data from the real database to Apatar's internal one.
	 * Practically it loads data from the connector to Apatar 
	 * @see http://www.apatarforge.org/wiki/display/ADM/How+to+Create+Your+Own+Connector+or+Operation 
	 * 
	 * @see com.apatar.core.AbstractDataBaseNode#TransformRDBtoTDB()
	 */
	protected void TransformRDBtoTDB() {
		try {
			DataBaseTools.completeTransfer();

			TableInfo outTI = getTiForConnection(OUT_CONN_POINT_NAME);
			
			DataProcessingInfo destinationTableInfo = new DataProcessingInfo(
					ApplicationData.getTempDataBase().getDataBaseInfo(), outTI
							.getTableName(), outTI.getRecords());

			List<KeyInsensitiveMap> list = new LinkedList<KeyInsensitiveMap>();
			
			List<String> addressList = (List<String>)argVals.get("address");
			List<String> refAddressList = (List<String>)argVals.get("referenceAddress");
			List<String> addressInfoList = (List<String>)argVals.get("addressInfo");
			
			boolean isRefListLarger = refAddressList.size() > addressList.size() ? true : false; 
			
			if(isRefListLarger){
				for(int i=0; i<refAddressList.size(); i++){
					
					KeyInsensitiveMap outputDataMap = new KeyInsensitiveMap();
					
					outputDataMap.put("referenceAddress", refAddressList.get(i));
					
					if(i<addressList.size()){
						outputDataMap.put("address", addressList.get(i));
						if(addressInfoList!=null && i<addressInfoList.size())
							outputDataMap.put("addressInfo", addressInfoList.get(i));
						else
							outputDataMap.put("addressInfo", "");
					}else{
						outputDataMap.put("address", "");
						outputDataMap.put("addressInfo", "");
					}
					
					list.add(outputDataMap);
				}
			}
			else{
				for(int i=0; i<addressList.size(); i++){
					
					KeyInsensitiveMap outputDataMap = new KeyInsensitiveMap();
					
					outputDataMap.put("address", addressList.get(i));
					if(addressInfoList!=null && i<addressInfoList.size())
						outputDataMap.put("addressInfo", addressInfoList.get(i));
					else
						outputDataMap.put("addressInfo", "");
					
					if(i<refAddressList.size())
						outputDataMap.put("referenceAddress", refAddressList.get(i));
					else
						outputDataMap.put("referenceAddress", "");
					
					list.add(outputDataMap);
				}
			}

			for (KeyInsensitiveMap data : list) {
				DataBaseTools.insertData(destinationTableInfo, data);
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
	}
	
	/*
	 * this method implements every action that can
	 * be performed by the node.
	 */
	public void Transform(){
		TransformTDBtoRDB(INSERT_MODE);
		try {
			
			Class functionsClass = Class.forName("tuc.apon.googleMaps.GoogleMapsFunctions");
			Method method = functionsClass.getMethod(getTableName(),argVals.getClass(),retVals.getClass());
			method.invoke(functionsClass.newInstance(),argVals, retVals);
	        
		} catch (Exception e) {
			ApplicationData.ProcessingProgress.Log(e);
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();	//This just closes some preparedStatements
		}
		TransformRDBtoTDB();
	}

	@Override
	/*
	 * saves node.
	 * This method is called to save node to an XML element
	 * (actually .aptr files are basically .xml files)
	 */
	public Element saveToElement() {
		return super.saveToElement();
	}

	@Override
	/*
	 * loads node.
	 * This method is called to retrieve node from an XML element
	 * (actually .aptr files are basically .xml files)
	 */
	public void initFromElement(Element e) {
		super.initFromElement(e);
	}

	@Override
	public List<RDBTable> getTableList() throws Exception {
		
		List<RDBTable> list = new ArrayList<RDBTable>();
		for (GoogleMapsTable table : GoogleMapsTableList.getGoogleMapsTables().values()) {
			list.add(new RDBTable(table.getTableName(), table.getMode()));
		}
		return list;
	}

	@Override
	public void deleteAllRecordsInRDB() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.apatar.core.AbstractDataBaseNode#getFieldList(com.apatar.core.AbstractApatarActions)
	 */
	public List<Record> getFieldList(AbstractApatarActions actions)
			throws Exception {

		List<Record> rl = new ArrayList<Record>();
		GoogleMapsTable table = GoogleMapsTableList.getTableByName(getTableName());

		HashMap<String,Object> merged = new HashMap<String, Object>();
		merged.putAll(table.getArguments());
		merged.putAll(table.getReturns());
		
		for(String srt : merged.keySet()){
			DBTypeRecord rec = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(), (String)merged.get(srt));
			rl.add(new Record(rec,srt,65000,true,true,false));
		}
		
		return rl;
	}
}