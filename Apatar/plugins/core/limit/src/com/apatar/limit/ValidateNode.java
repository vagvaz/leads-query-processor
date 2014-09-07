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

package com.apatar.limit;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.ApplicationData;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.FunctionsPluginLimit;
import com.apatar.core.TableInfo;
import com.apatar.core.TransparentNode;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JSubProjectDialog;

public class ValidateNode extends TransparentNode {

	public static final String INPUT_CONN_POINT = "input";
	public static final String OUTPUT_CONN_POINT_TRUE = "outputTrue";
	public static final String OUTPUT_CONN_POINT_FALSE = "outputFalse";

	public ValidateNode() {
		super();
		title = "Validate";

		outputConnectionList.put(new ConnectionPoint(OUTPUT_CONN_POINT_FALSE,
				false, this, true, "False", 2), new TableInfo("False"));
		outputConnectionList.put(new ConnectionPoint(OUTPUT_CONN_POINT_TRUE,
				false, this, true, "True", 1), new TableInfo("True"));
		inputConnectionList.add(new ConnectionPoint(INPUT_CONN_POINT, true,
				this, false));
	}

	@Override
	public ImageIcon getIcon() {
		return ValidateUtils.VALIDATE_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		return JSubProjectDialog.showDialog(ApatarUiMain.MAIN_FRAME,
				"Validation", this, new String[] { INPUT_CONN_POINT },
				FunctionsPluginLimit.getNodesFunction(), false,  //@@ <------------
				"help.operation.validate") == JSubProjectDialog.OK_OPTION;

	}

	@Override
	public Element saveToElement() {
		return super.saveToElement();
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
	}

	@Override
	public TableInfo getDebugTableInfo() {
		return getTiForConnection(OUTPUT_CONN_POINT_TRUE);
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
					DataBaseTools.completeTransfer();
					DataBaseTools.insertData(
							new DataProcessingInfo(ApplicationData
									.getTempDataBase().getDataBaseInfo(),
									destinationTableInfo.getTableName(),
									destinationTableInfo.getRecords(),
									ApplicationData.getTempJDBC()), rs, rsmd);
				}
				ApplicationData.ProcessingProgress.Step();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

}
