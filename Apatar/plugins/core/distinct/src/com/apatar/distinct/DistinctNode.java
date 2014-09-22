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

package com.apatar.distinct;

import java.net.SocketException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.ApplicationData;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.Record;
import com.apatar.core.TableInfo;
import com.apatar.core.TransparentNode;
import com.apatar.distinct.ui.JDistinctDialog;

public class DistinctNode extends TransparentNode {

	public static final String INPUT_CONN_POINT = "input";

	public static final String OUTPUT_CONN_POINT = "output";

	private List<String> selectedFields = new ArrayList<String>();

	public DistinctNode() {
		super();
		title = "Distinct";
		outputConnectionList.put(new ConnectionPoint(OUTPUT_CONN_POINT, false,
				this, false), new TableInfo());
		inputConnectionList.add(new ConnectionPoint(INPUT_CONN_POINT, true,
				this, false));
	}

	@Override
	public ImageIcon getIcon() {
		return DistinctUtils.DISTINCT_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		return JDistinctDialog.showDialog(this) == JDistinctDialog.OK_OPTION;
		/*
		 * transformDialog.setKeyForReferringToDescription("help.operation.distinct"
		 * ); transformDialog.setVisible(true); return true;
		 */
	}

	@Override
	public Element saveToElement() {
		Element distinctNode = super.saveToElement();
		Element selFields = new Element("distinctFields");
		distinctNode.addContent(selFields);
		for (String value : selectedFields) {
			Element slField = new Element("distinctField");
			slField.setText(value);
			selFields.addContent(slField);
		}
		return distinctNode;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		selectedFields.clear();

		Element element = e.getChild("distinctFields");
		if (element != null) {
			List fields = element.getChildren("distinctField");

			for (Iterator it = fields.iterator(); it.hasNext();) {
				Element el = (Element) it.next();
				el.getChildren("distinctField");
				selectedFields.add(el.getText());
			}
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
	}

	@Override
	public TableInfo getDebugTableInfo() {
		return getTiForConnection(OUTPUT_CONN_POINT);
	}

	@Override
	public void Transform() throws SocketException {
		try {
			DataBaseTools.completeTransfer();
			TableInfo inpar = getTiForConnection(DistinctNode.INPUT_CONN_POINT);
			TableInfo outpar = getTiForConnection(DistinctNode.OUTPUT_CONN_POINT);

			List<Record> distRec = new ArrayList<Record>();
			List<Record> inRec = inpar.getSchemaTable().getRecords();
			for (String fieldName : selectedFields) {
				Record rec = Record.getRecordByFieldName(inRec, fieldName);
				if (rec != null) {
					distRec.add(rec);
				}
			}

			DataProcessingInfo dpiSrc = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), inpar.getTableName(),
					distRec, ApplicationData.getTempJDBC());

			DataProcessingInfo dpiDest = new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(),
					outpar.getTableName(),
					outpar.getSchemaTable().getRecords(), ApplicationData
							.getTempJDBC());

			// transfer data with restrictions
			Map<String, String> modes = new HashMap<String, String>();
			modes.put("%post_select%", "DISTINCT");
			DataBaseTools.TransferData(dpiSrc, dpiDest,
					AbstractDataBaseNode.INSERT_MODE, null, modes, true);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataBaseTools.completeTransfer();
		}

	}

	public List<String> getOutputFieldsFromMetaData(ResultSetMetaData metaData)
			throws SQLException {
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			list.add(metaData.getColumnName(i).toString());
		}
		return list;

	}

	public List<String> getSelectedFields() {
		return selectedFields;
	}

	public void setSelectedFields(List<String> selectedFields) {
		this.selectedFields = selectedFields;
	}

	/*
	 * public void BeforeExecute() { SchemaTable inST =
	 * getTiForConnection(INPUT_CONN_POINT).getSchemaTable(); List<Record>
	 * records = inST.getRecords(); List<Record> comparable = new
	 * ArrayList<Record>(); for (String field : selectedFields) { Record rc =
	 * Record.getRecordByFieldName(records, field); if (rc != null) {
	 * comparable.add(rc); break; } }
	 * 
	 * if (comparable.size() == 0)
	 * getTiForConnection(OUTPUT_CONN_POINT).getSchemaTable
	 * ().updateRecords(records); else
	 * getTiForConnection(OUTPUT_CONN_POINT).getSchemaTable
	 * ().updateRecords(comparable); super.BeforeExecute(); }
	 */

	/*
	 * public void st.updateRecords(getFieldList(false)) { TableInfo oTI =
	 * getTiForConnection(OUTPUT_CONN_POINT); List<Record> recs = oTI = }
	 */
}
