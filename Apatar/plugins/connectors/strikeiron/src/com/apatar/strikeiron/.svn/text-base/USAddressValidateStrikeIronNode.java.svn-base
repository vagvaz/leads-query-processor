/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013



    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.



    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.



    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

 */

package com.apatar.strikeiron;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.Timer;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataConversionAlgorithm;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.ERecordType;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.Record;
import com.apatar.core.SchemaTable;
import com.apatar.core.TableInfo;
import com.apatar.functions.FunctionNode;
import com.apatar.strikeiron.ui.JLoginDialog;
import com.apatar.strikeiron.ui.JStrikeironDialog;
import com.apatar.strikeiron.ws.usaddress.USAddress;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JSubProjectDialog;
import com.apatar.validate.ValidateNode;

public class USAddressValidateStrikeIronNode extends ValidateNode implements
		Strikeiron {
	String userName;
	String password;

	public String verifiedDataName = "VerifiedData";
	Timer timer = new Timer(1440 * 60000, new TimerActionListener());

	public ArrayList<Record> verificationRecs = new ArrayList<Record>();

	public USAddressValidateStrikeIronNode() {
		super();
		title = "           StrikeIron\nUS Address Verification";
		verifiedDataName += new Date().getTime();
		DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Text, 255);
		verificationRecs.add(new Record(dbtRec, "address1", 255, false));
		verificationRecs.add(new Record(dbtRec, "address2", 255, false));
		verificationRecs.add(new Record(dbtRec, "cityStateZip", 255, false));
		verificationRecs.add(new Record(dbtRec, "urbanization", 255, false));
		verificationRecs.add(new Record(dbtRec, "firm", 255, false));

		verificationRecs.add(new Record(dbtRec, "USAddress___AddressLine1",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___AddressLine2",
				255, false));
		verificationRecs
				.add(new Record(dbtRec, "USAddress___State", 255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___ZipPlus4", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___Zip", 255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___ZipAddOn", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___CarrieRoute", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___PMB", 255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___PMBDesignator",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___DeliveryPoint",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___DPCheckDigit",
				255, false));
		verificationRecs
				.add(new Record(dbtRec, "USAddress___LACS", 255, false));
		verificationRecs
				.add(new Record(dbtRec, "USAddress___CMRA", 255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___DPV", 255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___DPVFootnote", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___RDI", 255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___RecordType", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___CongressDistrict",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___County", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___CountyNumber",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___StateNumber", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___PreDirection",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___StreetName", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___StreetType", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___PostDirection",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___Extension", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___ExtensionNumber",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___Village", 255,
				false));
		verificationRecs
				.add(new Record(dbtRec, "USAddress___City", 255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___ErrorMessage",
				255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___Status", 255,
				false));
		verificationRecs
				.add(new Record(dbtRec, "USAddress___Firm", 255, false));
		verificationRecs.add(new Record(dbtRec, "USAddress___Urbanzation", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "USAddress___StreetNumber",
				255, false));
		try {
			ApplicationData.tempDataBase.addTable(verifiedDataName,
					verificationRecs);
			timer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public TableInfo getDebugTableInfo() {
		return getTiForConnection(OUTPUT_CONN_POINT_TRUE);
	}

	@Override
	public ImageIcon getIcon() {
		return StrikeironUtils.STRIKEIRON_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		boolean dialogOk = false;
		if ((userName == null || userName.equals("")) || password == null) {
			JLoginDialog dlg = new JLoginDialog(this, ApatarUiMain.MAIN_FRAME);
			dlg.setVisible(true);
			dialogOk = dlg.isLoginSuccessful();
		} else {
			dialogOk = true;
		}
		/*
		 * JStrikeironDialog dlg=new JStrikeironDialog( ApatarUiMain.MAIN_FRAME,
		 * "StrikeIron US Address Verification", this, new
		 * String[]{INPUT_CONN_POINT},
		 * USAddressValidateStrikeIronNodeFactory.functionNodeFactory, false);
		 * dlg.setMessageForButtonDelay("Clear Addresses");
		 * dlg.setTitleForDelayDialog("Clear Addresses");
		 * dlg.setMessageForDelayDialog(" Clear temporary address database
		 * every:");
		 * dlg.setKeyForReferringToDescription("help.operation.strikeiron");
		 * dlg.setVisible(true); return true;
		 */
		if (dialogOk) {
			return JStrikeironDialog.showDialog(ApatarUiMain.MAIN_FRAME,
					"StrikeIron US Address Verification", this,
					new String[] { INPUT_CONN_POINT },
					USAddressValidateStrikeIronNodeFactory.functionNodeFactory,
					false, "Clear Addresses", "Clear Addresses",
					"  Clear temporary address database every:",
					"help.operation.strikeiron_address") == JSubProjectDialog.OK_OPTION;
		} else {
			return false;
		}
	}

	@Override
	public void Transform() {
		DataBaseTools.completeTransfer();
		TableInfo iTI = getTiForConnection(INPUT_CONN_POINT);
		ResultSet rs = null;
		try {
			rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());

			for (AbstractNode node : prj.getNodes().values()) {
				if (node instanceof StrikeironFunctionNode) {
					StrikeironFunctionNode siNode = (StrikeironFunctionNode) node;
					siNode.setPassword(password);
					siNode.setUserName(userName);
					siNode.setOwnerNode(this);
				}
			}

			while (rs.next()) {
				FillInputColumnNodes(rs, INPUT_CONN_POINT);

				// execute the project
				com.apatar.core.Runnable rn = new com.apatar.core.Runnable();
				rn.execute(prj.getNodes().values());

				USAddress usa = (USAddress) getAddressInfo();

				TableInfo destinationTableInfo;

				if (usa == null
						|| !usa.getAddressStatus().equalsIgnoreCase("VALID")) {
					KeyInsensitiveMap kim = DataBaseTools.GetDataFromRS(rs);
					kim.put("USAddress___ErrorMessage", usa
							.getAddressErrorMessage());
					destinationTableInfo = getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_FALSE);
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), kim);
				} else {
					KeyInsensitiveMap kim = DataBaseTools.GetDataFromRS(rs);
					destinationTableInfo = getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_TRUE);
					kim.put("USAddress___AddressLine1", new JdbcObject(usa
							.getAddressLine1(), Types.VARCHAR));
					kim.put("USAddress___AddressLine2", new JdbcObject(usa
							.getAddressLine2(), Types.VARCHAR));
					kim.put("USAddress___State", new JdbcObject(usa.getState(),
							Types.VARCHAR));
					kim.put("USAddress___ZipPlus4", new JdbcObject(usa
							.getZipPlus4(), Types.VARCHAR));
					kim.put("USAddress___Zip", new JdbcObject(usa.getZip(),
							Types.VARCHAR));
					kim.put("USAddress___ZipAddOn", new JdbcObject(usa
							.getZipAddOn(), Types.VARCHAR));
					kim.put("USAddress___CarrieRoute", new JdbcObject(usa
							.getCarrierRoute(), Types.VARCHAR));
					kim.put("USAddress___PMB", new JdbcObject(usa.getPMB(),
							Types.VARCHAR));
					kim.put("USAddress___PMBDesignator", new JdbcObject(usa
							.getPMBDesignator(), Types.VARCHAR));
					kim.put("USAddress___DeliveryPoint", new JdbcObject(usa
							.getDeliveryPoint(), Types.VARCHAR));
					kim.put("USAddress___DPCheckDigit", new JdbcObject(usa
							.getDPCheckDigit(), Types.VARCHAR));
					kim.put("USAddress___LACS", new JdbcObject(usa.getLACS(),
							Types.VARCHAR));
					kim.put("USAddress___CMRA", new JdbcObject(usa.getCMRA(),
							Types.VARCHAR));
					kim.put("USAddress___DPV", new JdbcObject(usa.getDPV(),
							Types.VARCHAR));
					kim.put("USAddress___DPVFootnote", new JdbcObject(usa
							.getDPVFootnote(), Types.VARCHAR));
					kim.put("USAddress___RDI", new JdbcObject(usa.getRDI(),
							Types.VARCHAR));
					kim.put("USAddress___RecordType", new JdbcObject(usa
							.getRecordType(), Types.VARCHAR));
					kim.put("USAddress___CongressDistrict", new JdbcObject(usa
							.getCongressDistrict(), Types.VARCHAR));
					kim.put("USAddress___County", new JdbcObject(usa
							.getCounty(), Types.VARCHAR));
					kim.put("USAddress___CountyNumber", new JdbcObject(usa
							.getCountyNumber(), Types.VARCHAR));
					kim.put("USAddress___StateNumber", new JdbcObject(usa
							.getStateNumber(), Types.VARCHAR));
					kim.put("USAddress___StreetNumber", new JdbcObject(usa
							.getStreetNumber(), Types.VARCHAR));
					kim.put("USAddress___PreDirection", new JdbcObject(usa
							.getPreDirection(), Types.VARCHAR));
					kim.put("USAddress___StreetName", new JdbcObject(usa
							.getStreetName(), Types.VARCHAR));
					kim.put("USAddress___StreetType", new JdbcObject(usa
							.getStreetType(), Types.VARCHAR));
					kim.put("USAddress___PostDirection", new JdbcObject(usa
							.getPostDirection(), Types.VARCHAR));
					kim.put("USAddress___Extension", new JdbcObject(usa
							.getExtension(), Types.VARCHAR));
					kim.put("USAddress___ExtensionNumber", new JdbcObject(usa
							.getExtensionNumber(), Types.VARCHAR));
					kim.put("USAddress___Village", new JdbcObject(usa
							.getVillage(), Types.VARCHAR));
					kim.put("USAddress___City", new JdbcObject(usa.getCity(),
							Types.VARCHAR));
					kim.put("USAddress___Firm", new JdbcObject(usa.getFirm(),
							Types.VARCHAR));
					kim.put("USAddress___Urbanzation", new JdbcObject(usa
							.getUrbanization(), Types.VARCHAR));
					kim.put("USAddress___Status", new JdbcObject(usa
							.getAddressStatus(), Types.VARCHAR));
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), kim);
				}

				/*
				 * if (destinationTableInfo != null)
				 * DataBaseTools.insertData(new
				 * DataProcessingInfo(ApplicationData
				 * .getTempDataBase().getDataBaseInfo(),
				 * destinationTableInfo.getTableName(),
				 * destinationTableInfo.getRecords(),
				 * ApplicationData.getTempJDBCConnection()), rs);
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public Element saveToElement() {
		return super.saveToElement();
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
	}

	class TimerActionListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			try {
				DataBaseTools.clearRecords(ApplicationData.tempDataBase
						.getDataBaseInfo(), ApplicationData.tempDataBase
						.getJdbcParams(), verifiedDataName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void setDelay(int delay) {
		timer.setDelay(delay);
		timer.restart();
	}

	public String getDBTempVerificationName() {
		return verifiedDataName;
	}

	public List<Record> getTempVerificationRecords() {
		return verificationRecs;
	}

	protected Object getAddressInfo() {
		for (Object node : prj.getNodes().values()) {
			if (!(node instanceof FunctionNode)) {
				continue;
			}
			FunctionNode vnode = (FunctionNode) node;
			if (vnode.getInlinePosition() != AbstractNode.LAST_POSITION) {
				continue;
			}

			return vnode.getResult();
		}
		return null;
	}

	@Override
	protected void setOutputSchemaTable() {
		super.setOutputSchemaTable();

		DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Text, 255);

		SchemaTable outputTrueST = getTiForConnection(OUTPUT_CONN_POINT_TRUE)
				.getSchemaTable();
		// outputTrueST.removeAllRecord();

		outputTrueST.addRecord(new Record(dbtRec, "USAddress___AddressLine1",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___AddressLine2",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___State", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___ZipPlus4", 255,
				false));
		outputTrueST
				.addRecord(new Record(dbtRec, "USAddress___Zip", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___ZipAddOn", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___CarrierRoute",
				255, false));
		outputTrueST
				.addRecord(new Record(dbtRec, "USAddress___PMB", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___PMBDesignator",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___DeliveryPoint",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___DPCheckDigit",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___LACS", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___CMRA", 255,
				false));
		outputTrueST
				.addRecord(new Record(dbtRec, "USAddress___DPV", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___DPVFootnote",
				255, false));
		outputTrueST
				.addRecord(new Record(dbtRec, "USAddress___RDI", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___RecordType",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec,
				"USAddress___CongressDistrict", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___County", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___CountyNumber",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___StateNumber",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___PreDirection",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___StreetName",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___StreetType",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___PostDirection",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___Extension", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec,
				"USAddress___ExtensionNumber", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___Village", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___City", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___Firm", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___Urbanzation",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___StreetNumber",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "USAddress___Status", 255,
				false));

		SchemaTable inputTrueST = getTiForConnection(OUTPUT_CONN_POINT_FALSE)
				.getSchemaTable();
		inputTrueST.addRecord(new Record(dbtRec, "USAddress___ErrorMessage",
				255, false));
	}

	public Map<String, ClassLoader> getFunctionLoader() {
		return StrikeironNodeFactory.functionLoader;
	}
}
