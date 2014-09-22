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
import com.apatar.cdyne.ws.demographix.GenderPercentagesCls;
import com.apatar.cdyne.ws.demographix.MaritalStatusPercentagesCls;
import com.apatar.cdyne.ws.demographix.PlaceInformationCls;
import com.apatar.cdyne.ws.demographix.RacePercentagesCls;
import com.apatar.cdyne.ws.demographix.SummaryInformation;
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

public class DemographicsCdyneNode extends ValidateNode implements Cdyne {
	String licenseKey;

	public String verifiedDataName = "VerifiedData";
	Timer timer = new Timer(1440 * 60000, new TimerActionListener());

	public ArrayList<Record> verificationRecs = new ArrayList<Record>();

	public DemographicsCdyneNode() {
		super();
		title = "CDYNE Demographics";
		verifiedDataName += new Date().getTime();

		DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Text, 255);
		DBTypeRecord boolDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Boolean, 255);
		DBTypeRecord intDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Numeric, 255);
		DBTypeRecord decDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Decimal, 255);

		verificationRecs.add(new Record(dbtRec, "addressLine1", 255, false));
		verificationRecs.add(new Record(dbtRec, "city", 255, false));
		verificationRecs.add(new Record(dbtRec, "zipCode", 255, false));
		verificationRecs.add(new Record(dbtRec, "stateAbbrev", 255, false));

		verificationRecs.add(new Record(dbtRec,
				"SummaryInformation_PlaceInformation___placeID", 255, false));
		verificationRecs
				.add(new Record(dbtRec,
						"SummaryInformation_PlaceInformation___stateAbbrev",
						255, false));
		verificationRecs.add(new Record(boolDbtRec,
				"SummaryInformation_PlaceInformation___rural", 255, false));

		verificationRecs.add(new Record(boolDbtRec, "SummaryInformation_error",
				255, false));
		verificationRecs.add(new Record(dbtRec,
				"SummaryInformation_errorString", 255, false));
		verificationRecs.add(new Record(intDbtRec,
				"SummaryInformation_medianAge", 255, false));
		verificationRecs.add(new Record(intDbtRec,
				"SummaryInformation_medianIncome", 255, false));
		verificationRecs.add(new Record(intDbtRec,
				"SummaryInformation_medianRoomsInHouse", 255, false));
		verificationRecs.add(new Record(intDbtRec,
				"SummaryInformation_medianHouseValue", 255, false));
		verificationRecs.add(new Record(intDbtRec,
				"SummaryInformation_medianVehicles", 255, false));

		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___neverMarried",
				255, false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___married", 255,
				false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___separated", 255,
				false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___marriedOther",
				255, false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___widowed", 255,
				false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___divorced", 255,
				false));

		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___asian", 255, false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___black", 255, false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___indian", 255, false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___mixed", 255, false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___nativeHawaiian", 255,
				false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___other", 255, false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___white", 255, false));

		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_GenderPercentages___female", 255, false));
		verificationRecs.add(new Record(decDbtRec,
				"SummaryInformation_GenderPercentages___male", 255, false));

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
		boolean oc = true;
		if (licenseKey == null || licenseKey.equals("")) {
			oc = JCdyneLoginDialog
					.showDialog(this, ApatarUiMain.MAIN_FRAME,
							"http://www.cdyne.com/products/demographics.aspx?refid=apatar");
		}
		if (oc) {
			return JCdyneDialog.showDialog(ApatarUiMain.MAIN_FRAME,
					"CDYNE Demographics Verification", this,
					new String[] { INPUT_CONN_POINT },
					DemographicsCdyneNodeFactory.functionNodeFactory, false,
					"Clear Demographics", "Clear Demographics",
					"  Clear temporary demographics database every:", null,
					"help.DataQualityServices.cdyne.demographics") == JSubProjectDialog.OK_OPTION;
		}
		return false;
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

				SummaryInformation si = (SummaryInformation) getDemographicsInfo();

				TableInfo destinationTableInfo;
				KeyInsensitiveMap kim = DataBaseTools.GetDataFromRS(rs);
				if (si.isError()) {
					kim.put("ErrorMessage", si.getErrorString());
					destinationTableInfo = getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_FALSE);
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), kim);
				} else {
					destinationTableInfo = getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_TRUE);

					PlaceInformationCls pi = si.getPlaceInformation();
					kim.put("SummaryInformation_PlaceInformation___placeID",
							new JdbcObject(pi.getPlaceID(), Types.VARCHAR));
					kim
							.put(
									"SummaryInformation_PlaceInformation___stateAbbrev",
									new JdbcObject(pi.getStateAbbrev(),
											Types.VARCHAR));
					kim.put("SummaryInformation_PlaceInformation___rural",
							new JdbcObject(pi.isRural(), Types.BOOLEAN));

					kim.put("SummaryInformation_error", new JdbcObject(si
							.isError(), Types.BOOLEAN));
					kim.put("SummaryInformation_errorString", new JdbcObject(si
							.getErrorString(), Types.VARCHAR));
					kim.put("SummaryInformation_medianAge", new JdbcObject(si
							.getMedianAge().intValue(), Types.INTEGER));
					kim.put("SummaryInformation_medianIncome", new JdbcObject(
							si.getMedianIncome(), Types.INTEGER));
					kim.put("SummaryInformation_medianRoomsInHouse",
							new JdbcObject(si.getMedianRoomsInHouse()
									.intValue(), Types.INTEGER));
					kim.put("SummaryInformation_medianHouseValue",
							new JdbcObject(si.getMedianHouseValue(),
									Types.INTEGER));
					kim.put("SummaryInformation_medianVehicles",
							new JdbcObject(si.getMedianVehicles().intValue(),
									Types.INTEGER));

					MaritalStatusPercentagesCls msp = si
							.getMaritalStatusPercentages();
					kim
							.put(
									"SummaryInformation_MaritalStatusPercentages___neverMarried",
									new JdbcObject(msp.getNeverMarried(),
											Types.DECIMAL));
					kim
							.put(
									"SummaryInformation_MaritalStatusPercentages___married",
									new JdbcObject(msp.getMarried(),
											Types.DECIMAL));
					kim
							.put(
									"SummaryInformation_MaritalStatusPercentages___separated",
									new JdbcObject(msp.getSeparated(),
											Types.DECIMAL));
					kim
							.put(
									"SummaryInformation_MaritalStatusPercentages___marriedOther",
									new JdbcObject(msp.getMarriedOther(),
											Types.DECIMAL));
					kim
							.put(
									"SummaryInformation_MaritalStatusPercentages___widowed",
									new JdbcObject(msp.getWidowed(),
											Types.DECIMAL));
					kim
							.put(
									"SummaryInformation_MaritalStatusPercentages___divorced",
									new JdbcObject(msp.getDivorced(),
											Types.DECIMAL));

					RacePercentagesCls rp = si.getRacePercentages();
					kim.put("SummaryInformation_RacePercentages___asian",
							new JdbcObject(rp.getAsian(), Types.DECIMAL));
					kim.put("SummaryInformation_RacePercentages___black",
							new JdbcObject(rp.getBlack(), Types.DECIMAL));
					kim.put("SummaryInformation_RacePercentages___indian",
							new JdbcObject(rp.getIndian(), Types.DECIMAL));
					kim.put("SummaryInformation_RacePercentages___mixed",
							new JdbcObject(rp.getMixed(), Types.DECIMAL));
					kim
							.put(
									"SummaryInformation_RacePercentages___nativeHawaiian",
									new JdbcObject(rp.getNativeHawaiian(),
											Types.DECIMAL));
					kim.put("SummaryInformation_RacePercentages___other",
							new JdbcObject(rp.getOther(), Types.DECIMAL));
					kim.put("SummaryInformation_RacePercentages___white",
							new JdbcObject(rp.getWhite(), Types.DECIMAL));

					GenderPercentagesCls gp = si.getGenderPercentages();
					kim.put("SummaryInformation_GenderPercentages___female",
							new JdbcObject(gp.getFemale(), Types.DECIMAL));
					kim.put("SummaryInformation_GenderPercentages___male",
							new JdbcObject(gp.getMale(), Types.DECIMAL));
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), kim);
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

	protected Object getDemographicsInfo() {
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
		DBTypeRecord intDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Numeric, 255);
		DBTypeRecord decDbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Decimal, 255);

		SchemaTable outputTrueST = getTiForConnection(OUTPUT_CONN_POINT_TRUE)
				.getSchemaTable();

		outputTrueST.addRecord(new Record(dbtRec,
				"SummaryInformation_PlaceInformation___placeID", 255, false));
		outputTrueST
				.addRecord(new Record(dbtRec,
						"SummaryInformation_PlaceInformation___stateAbbrev",
						255, false));
		outputTrueST.addRecord(new Record(boolDbtRec,
				"SummaryInformation_PlaceInformation___rural", 255, false));

		outputTrueST.addRecord(new Record(boolDbtRec,
				"SummaryInformation_error", 255, false));
		outputTrueST.addRecord(new Record(dbtRec,
				"SummaryInformation_errorString", 255, false));
		outputTrueST.addRecord(new Record(intDbtRec,
				"SummaryInformation_medianAge", 255, false));
		outputTrueST.addRecord(new Record(intDbtRec,
				"SummaryInformation_medianIncome", 255, false));
		outputTrueST.addRecord(new Record(intDbtRec,
				"SummaryInformation_medianRoomsInHouse", 255, false));
		outputTrueST.addRecord(new Record(intDbtRec,
				"SummaryInformation_medianHouseValue", 255, false));
		outputTrueST.addRecord(new Record(intDbtRec,
				"SummaryInformation_medianVehicles", 255, false));

		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___neverMarried",
				255, false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___married", 255,
				false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___separated", 255,
				false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___marriedOther",
				255, false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___widowed", 255,
				false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_MaritalStatusPercentages___divorced", 255,
				false));

		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___asian", 255, false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___black", 255, false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___indian", 255, false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___mixed", 255, false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___nativeHawaiian", 255,
				false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___other", 255, false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_RacePercentages___white", 255, false));

		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_GenderPercentages___female", 255, false));
		outputTrueST.addRecord(new Record(decDbtRec,
				"SummaryInformation_GenderPercentages___male", 255, false));

		SchemaTable inputTrueST = getTiForConnection(OUTPUT_CONN_POINT_FALSE)
				.getSchemaTable();
		inputTrueST.addRecord(new Record(dbtRec, "ErrorMessage", 255, false));

	}

	public Map<String, ClassLoader> getFunctionLoader() {
		return DemographicsCdyneNodeFactory.functionLoader;
	}

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}
}
