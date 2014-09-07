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

package com.apatar.cdyne;

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

import com.apatar.cdyne.ui.JCdyneDialog;
import com.apatar.cdyne.ui.JCdyneLoginDialog;
import com.apatar.cdyne.ws.PhoneReturn;
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
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JSubProjectDialog;
import com.apatar.validate.ValidateNode;

public class PhoneValidateCdyneNode extends ValidateNode implements Cdyne {
	String licenseKey;

	public String verifiedDataName = "VerifiedData";
	Timer timer = new Timer(1440 * 60000, new TimerActionListener());

	public ArrayList<Record> verificationRecs = new ArrayList<Record>();

	public PhoneValidateCdyneNode() {
		super();
		title = "         CDYNE\nPhone Verification";
		verifiedDataName += new Date().getTime();

		DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Text, 255);

		DBTypeRecord boolDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Boolean, 255);
		verificationRecs.add(new Record(dbtRec, "phone", 255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___company", 255,
				false));
		verificationRecs.add(new Record(boolDbtRec, "PhoneReturn___valid", 255,
				false));
		verificationRecs
				.add(new Record(dbtRec, "PhoneReturn___use", 255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___state", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn____switch", 255,
				false));
		verificationRecs
				.add(new Record(dbtRec, "PhoneReturn___RC", 255, false));
		verificationRecs
				.add(new Record(dbtRec, "PhoneReturn___OCN", 255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___originalNumber",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___cleanNumber",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___switchName",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___switchType",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___country", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___CLLI", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___prefixType",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___LATA", 255,
				false));
		verificationRecs
				.add(new Record(dbtRec, "PhoneReturn___sms", 255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___email", 255,
				false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___assignDate",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___telecomCity",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___telecomCounty",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___telecomState",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___telecomZip",
				255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn___timeZone", 255,
				false));
		verificationRecs
				.add(new Record(dbtRec, "PhoneReturn___lat", 255, false));
		verificationRecs.add(new Record(dbtRec, "PhoneReturn____long", 255,
				false));
		verificationRecs.add(new Record(boolDbtRec, "PhoneReturn___wireless",
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
		return CdyneUtils.CDYNE_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		boolean oc = false;
		if (licenseKey == null || licenseKey.equals("")) {
			oc = JCdyneLoginDialog
					.showDialog(this, ApatarUiMain.MAIN_FRAME,
							"http://www.cdyne.com/products/phone-verification.aspx?refid=apatar");
		}
		if (oc) {
			return JCdyneDialog
					.showDialog(
							ApatarUiMain.MAIN_FRAME,
							"CDYNE Phone Verification",
							this,
							new String[] { INPUT_CONN_POINT },
							PhoneValidateCdyneNodeFactory.functionNodeFactory,
							false,
							"Clear Phone",
							"Clear Phone",
							"  Clear temporary phone database every:",
							"http://www.cdyne.com/products/phone-verification.aspx?refid=apatar",
							"help.DataQualityServices.cdyne.phone") == JSubProjectDialog.OK_OPTION;
		}
		return false;
	}

	@Override
	public void Transform() {
		DataBaseTools.completeTransfer();

		if (licenseKey == null || licenseKey.equals("")) {
			ApplicationData.ProcessingProgress
					.Log(" CDYNE Phone Verification service access denied. To proceed, please specify your license key.");
		}

		TableInfo iTI = getTiForConnection(INPUT_CONN_POINT);
		ResultSet rs = null;
		try {
			rs = DataBaseTools.getRSWithAllFields(iTI.getTableName(),
					ApplicationData.getTempJDBC(), ApplicationData
							.getTempDataBaseInfo());

			for (AbstractNode node : prj.getNodes().values()) {
				if (node instanceof CdyneFunctionNode) {
					CdyneFunctionNode siNode = (CdyneFunctionNode) node;
					siNode.setLicenseKey(licenseKey);
					siNode.setOwnerNode(this);
				}
			}

			while (rs.next()) {
				FillInputColumnNodes(rs, INPUT_CONN_POINT);

				// execute the project
				com.apatar.core.Runnable rn = new com.apatar.core.Runnable();
				rn.execute(prj.getNodes().values());

				PhoneReturn pr = (PhoneReturn) getAddressInfo();

				TableInfo destinationTableInfo;
				KeyInsensitiveMap kim = DataBaseTools.GetDataFromRS(rs);
				if (pr != null && pr.isValid()) {
					destinationTableInfo = getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_TRUE);

					// kim.put("PhoneReturn___phone", new JdbcObject(p,
					// Types.VARCHAR));
					kim.put("PhoneReturn___company", new JdbcObject(pr
							.getCompany(), Types.VARCHAR));
					kim.put("PhoneReturn___valid", new JdbcObject(pr.isValid(),
							Types.BOOLEAN));
					kim.put("PhoneReturn___use", new JdbcObject(pr.getUse(),
							Types.VARCHAR));
					kim.put("PhoneReturn___state", new JdbcObject(
							pr.getState(), Types.VARCHAR));
					kim.put("PhoneReturn____switch", new JdbcObject(pr
							.get_switch(), Types.VARCHAR));
					kim.put("PhoneReturn___RC", new JdbcObject(pr.getRC(),
							Types.VARCHAR));
					kim.put("PhoneReturn___OCN", new JdbcObject(pr.getOCN(),
							Types.VARCHAR));
					kim.put("PhoneReturn___originalNumber", new JdbcObject(pr
							.getOriginalNumber(), Types.VARCHAR));
					kim.put("PhoneReturn___cleanNumber", new JdbcObject(pr
							.getCleanNumber(), Types.VARCHAR));
					kim.put("PhoneReturn___switchName", new JdbcObject(pr
							.getSwitchName(), Types.VARCHAR));
					kim.put("PhoneReturn___switchType", new JdbcObject(pr
							.getSwitchType(), Types.VARCHAR));
					kim.put("PhoneReturn___country", new JdbcObject(pr
							.getCountry(), Types.VARCHAR));
					kim.put("PhoneReturn___CLLI", new JdbcObject(pr.getCLLI(),
							Types.VARCHAR));
					kim.put("PhoneReturn___prefixType", new JdbcObject(pr
							.getPrefixType(), Types.VARCHAR));
					kim.put("PhoneReturn___LATA", new JdbcObject(pr.getLATA(),
							Types.VARCHAR));
					kim.put("PhoneReturn___sms", new JdbcObject(pr.getSms(),
							Types.VARCHAR));
					kim.put("PhoneReturn___email", new JdbcObject(
							pr.getEmail(), Types.VARCHAR));
					kim.put("PhoneReturn___assignDate", new JdbcObject(pr
							.getAssignDate(), Types.VARCHAR));
					kim.put("PhoneReturn___telecomCity", new JdbcObject(pr
							.getTelecomCity(), Types.VARCHAR));
					kim.put("PhoneReturn___telecomCounty", new JdbcObject(pr
							.getTelecomCounty(), Types.VARCHAR));
					kim.put("PhoneReturn___telecomState", new JdbcObject(pr
							.getTelecomState(), Types.VARCHAR));
					kim.put("PhoneReturn___telecomZip", new JdbcObject(pr
							.getTelecomZip(), Types.VARCHAR));
					kim.put("PhoneReturn___timeZone", new JdbcObject(pr
							.getTimeZone(), Types.VARCHAR));
					kim.put("PhoneReturn___lat", new JdbcObject(pr.getLat(),
							Types.VARCHAR));
					kim.put("PhoneReturn____long", new JdbcObject(
							pr.get_long(), Types.VARCHAR));
					kim.put("PhoneReturn___wireless", new JdbcObject(pr
							.isWireless(), Types.BOOLEAN));
				} else {
					destinationTableInfo = getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_FALSE);
				}
				DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
						.getTempDataBase().getDataBaseInfo(),
						destinationTableInfo.getTableName(),
						destinationTableInfo.getRecords(), ApplicationData
								.getTempJDBC()), kim);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}
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
		// outputTrueST.removeAllRecord(); yf
		DBTypeRecord boolDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Boolean, 255);
		outputTrueST.addRecord(new Record(dbtRec, "phone", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___company", 255,
				false));
		outputTrueST.addRecord(new Record(boolDbtRec, "PhoneReturn___valid",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___use", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___state", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn____switch", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___RC", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___OCN", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec,
				"PhoneReturn___originalNumber", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___cleanNumber",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___switchName",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___switchType",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___country", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___CLLI", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___prefixType",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___LATA", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___sms", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___email", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___assignDate",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___telecomCity",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec,
				"PhoneReturn___telecomCounty", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___telecomState",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___telecomZip",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___timeZone",
				255, false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn___lat", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "PhoneReturn____long", 255,
				false));
		outputTrueST.addRecord(new Record(boolDbtRec, "PhoneReturn___wireless",
				255, false));
	}

	public Map<String, ClassLoader> getFunctionLoader() {
		return PhoneValidateCdyneNodeFactory.functionLoader;
	}

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}
}
