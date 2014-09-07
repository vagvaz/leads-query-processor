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

package com.apatar.strikeiron;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
import com.apatar.core.Record;
import com.apatar.core.TableInfo;
import com.apatar.strikeiron.ui.JLoginDialog;
import com.apatar.strikeiron.ui.JStrikeironDialog;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JSubProjectDialog;
import com.apatar.validate.ValidateNode;

public class StrikeironNode extends ValidateNode implements Strikeiron {
	String userName = "";
	String password = "";

	String verifiedDataName = "VerifiedData";
	Timer timer = new Timer(1440 * 60000, new TimerActionListener());

	ArrayList<Record> verificationRecs = new ArrayList<Record>();

	public StrikeironNode() {
		super();
		title = "      StrikeIron\nEmail Verification";
		verifiedDataName += new Date().getTime();
		DBTypeRecord dbtRec = DataConversionAlgorithm.bestRecordLookup(
				ApplicationData.tempDataBase.getDataBaseInfo()
						.getAvailableTypes(), ERecordType.Text, 255);
		verificationRecs.add(new Record(dbtRec, "email", 255, false));
		verificationRecs.add(new Record(dbtRec, "isValid", 255, false));
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
		 * "StrikeIron Email Verification", this, new
		 * String[]{INPUT_CONN_POINT},
		 * StrikeironNodeFactory.functionNodeFactory, false);
		 * dlg.setMessageForButtonDelay("Clear E-mails");
		 * dlg.setTitleForDelayDialog("Clear E-mails");
		 * dlg.setMessageForDelayDialog(" Clear temporary email database
		 * every:");
		 * dlg.setKeyForReferringToDescription("help.operation.strikeiron");
		 * dlg.setVisible(true); return true;
		 */
		if (dialogOk) {
			return JStrikeironDialog.showDialog(ApatarUiMain.MAIN_FRAME,
					"StrikeIron Email Verification", this,
					new String[] { INPUT_CONN_POINT },
					StrikeironNodeFactory.functionNodeFactory, false,
					"Clear E-mails", "Clear E-mails",
					"  Clear temporary email database every:",
					"help.operation.strikeiron") == JSubProjectDialog.OK_OPTION;
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

			// SQLQueryString sqs =
			// DataBaseTools.CreateSelectString(ApplicationData.getTempDataBaseInfo(),
			// new SQLCreationData(getTempVerificationRecords(),
			// getDBTempVerificationName()), null);
			// ResultSet rs2 = DataBaseTools.executeSelect(sqs,
			// ApplicationData.getTempJDBC());

			for (AbstractNode node : prj.getNodes().values()) {
				if (node instanceof StrikeironFunctionNode) {
					StrikeironFunctionNode siNode = (StrikeironFunctionNode) node;
					siNode.setPassword(password);
					siNode.setUserName(userName);
					siNode.setOwnerNode(this);
				}
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				FillInputColumnNodes(rs, INPUT_CONN_POINT);

				// execute the project
				com.apatar.core.Runnable rn = new com.apatar.core.Runnable();
				rn.execute(prj.getNodes().values());

				boolean result = calculateResult();

				TableInfo destinationTableInfo = (result == false) ? getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_FALSE)
						: getTiForConnection(ValidateNode.OUTPUT_CONN_POINT_TRUE);

				if (destinationTableInfo != null) {
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), rs, rsmd);
				}
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
		Element element = super.saveToElement();
		Element login = new Element("login");
		login.setAttribute("password", password);
		login.setAttribute("userName", userName);
		element.addContent(login);
		return element;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		Element login = e.getChild("login");
		if (login == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
		password = login.getAttributeValue("password");
		userName = login.getAttributeValue("userName");
		if (password == null || userName == null) {
			ApplicationData.COUNT_INIT_ERROR++;
		}
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

	public Map<String, ClassLoader> getFunctionLoader() {
		return StrikeironNodeFactory.functionLoader;
	}

}
