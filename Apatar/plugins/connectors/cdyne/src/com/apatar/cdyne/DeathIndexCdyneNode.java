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
import com.apatar.cdyne.ws.deathindex.DeceasedInfo;
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

public class DeathIndexCdyneNode extends ValidateNode implements Cdyne {
	String licenseKey;

	public String verifiedDataName = "VerifiedData";
	Timer timer = new Timer(1440 * 60000, new TimerActionListener());

	public ArrayList<Record> verificationRecs = new ArrayList<Record>();

	public DeathIndexCdyneNode() {
		super();
		title = "           CDYNE\n DeathIndex Verification";
		verifiedDataName += new Date().getTime();

		DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Text, 255);
		DBTypeRecord boolDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Boolean, 255);
		DBTypeRecord dateDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Date, 255);

		verificationRecs.add(new Record(dbtRec, "ssn", 255, false));

		verificationRecs.add(new Record(dbtRec, "__SSN", 255, false));
		verificationRecs.add(new Record(dbtRec, "lastName", 255, false));
		verificationRecs.add(new Record(dbtRec, "nameSuffix", 255, false));
		verificationRecs.add(new Record(dbtRec, "firstName", 255, false));
		verificationRecs.add(new Record(dbtRec, "middleName", 255, false));
		verificationRecs.add(new Record(dbtRec, "verified", 255, false));
		verificationRecs.add(new Record(dateDbtRec, "birthDate", 255, false));
		verificationRecs.add(new Record(dateDbtRec, "deathDate", 255, false));
		verificationRecs
				.add(new Record(dbtRec, "zipLastResidence", 255, false));
		verificationRecs.add(new Record(dbtRec, "zipLumpSumPay", 255, false));
		verificationRecs.add(new Record(boolDbtRec, "match", 255, false));
		verificationRecs.add(new Record(dbtRec, "errorText", 255, false));

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
		boolean dialogOk = false;
		if (licenseKey == null || licenseKey.equals("")) {
			JCdyneLoginDialog dlg = new JCdyneLoginDialog(this,
					ApatarUiMain.MAIN_FRAME, null);
			dlg.setVisible(true);
			dialogOk = dlg.isLoginSuccessful();
		} else {
			dialogOk = true;
		}
		if (dialogOk) {
			return JCdyneDialog.showDialog(ApatarUiMain.MAIN_FRAME,
					"Cdyne Death Index Verification", this,
					new String[] { INPUT_CONN_POINT },
					DeathIndexCdyneNodeFactory.functionNodeFactory, false,
					"Clear Death Index", "Clear Death Index",
					"  Clear temporary death index database every:", null,
					"help.DataQualityServices.cdyne.deathindex") == JSubProjectDialog.OK_OPTION;
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

				DeceasedInfo di = (DeceasedInfo) getDeceasedInfo();

				TableInfo destinationTableInfo;
				KeyInsensitiveMap kim = DataBaseTools.GetDataFromRS(rs);
				if (!di.isMatch()) {
					kim.put("errorText", di.getErrorText());
					destinationTableInfo = getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_FALSE);
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), kim);
				} else {
					destinationTableInfo = getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_TRUE);

					kim
							.put("__SSN", new JdbcObject(di.getSSN(),
									Types.VARCHAR));
					kim.put("lastName", new JdbcObject(di.getLastName(),
							Types.VARCHAR));
					kim.put("nameSuffix", new JdbcObject(di.getNameSuffix(),
							Types.VARCHAR));
					kim.put("firstName", new JdbcObject(di.getFirstName(),
							Types.VARCHAR));
					kim.put("middleName", new JdbcObject(di.getMiddleName(),
							Types.VARCHAR));
					kim.put("verified", new JdbcObject(di.getVerified(),
							Types.VARCHAR));
					kim.put("birthDate", new JdbcObject(new Date(di
							.getBirthDate().getTimeInMillis()), Types.DATE));
					kim.put("deathDate", new JdbcObject(new Date(di
							.getDeathDate().getTimeInMillis()), Types.DATE));
					kim.put("zipLastResidence", new JdbcObject(di
							.getZipLastResidence(), Types.VARCHAR));
					kim.put("zipLumpSumPay", new JdbcObject(di
							.getZipLumpSumPay(), Types.VARCHAR));
					kim.put("match",
							new JdbcObject(di.isMatch(), Types.BOOLEAN));
					kim.put("errorText", new JdbcObject(di.getErrorText(),
							Types.VARCHAR));

					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), kim);
				}
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

	protected Object getDeceasedInfo() {
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
		DBTypeRecord boolDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Boolean, 255);
		DBTypeRecord dateDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Date, 255);

		SchemaTable outputTrueST = getTiForConnection(OUTPUT_CONN_POINT_TRUE)
				.getSchemaTable();

		outputTrueST.addRecord(new Record(dbtRec, "SSN", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "lastName", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "nameSuffix", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "firstName", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "middleName", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "verified", 255, false));
		outputTrueST.addRecord(new Record(dateDbtRec, "birthDate", 255, false));
		outputTrueST.addRecord(new Record(dateDbtRec, "deathDate", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "zipLastResidence", 255,
				false));
		outputTrueST.addRecord(new Record(dbtRec, "zipLumpSumPay", 255, false));
		outputTrueST.addRecord(new Record(boolDbtRec, "match", 255, false));
		outputTrueST.addRecord(new Record(dbtRec, "errorText", 255, false));

		SchemaTable inputTrueST = getTiForConnection(OUTPUT_CONN_POINT_FALSE)
				.getSchemaTable();
		inputTrueST.addRecord(new Record(dbtRec, "errorText", 255, false));

	}

	public Map<String, ClassLoader> getFunctionLoader() {
		return DeathIndexCdyneNodeFactory.functionLoader;
	}

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}
}
