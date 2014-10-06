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

package com.apatar.rss.ui;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import com.apatar.rss.RssElement;

public class JChannelPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2007708881280912440L;

	public JChannelPanel() {
		super();
		createPanel();
	}

	HashMap<Integer, RssElement> elements = new HashMap<Integer, RssElement>();

	JRssTable tableSchema;
	/*
	 * JButton addField; JButton removeField; JComboBox nameField;
	 */
	DefaultTableModel model = new DefaultTableModel(new Object[][] {},
			new Object[] { "Name", "Value" });

	private void createPanel() {
		setLayout(new BorderLayout());
		// JToolBar panelButton = new JToolBar();

		/*
		 * addField = new JButton(new
		 * ImageIcon(this.getClass().getResource("add.png"))); removeField = new
		 * JButton(new ImageIcon(this.getClass().getResource("delete.png")));
		 * 
		 * addField.setToolTipText("Add column");
		 * removeField.setToolTipText("Remove selected column");
		 * primaryKey.setToolTipText("Set/Reset primary key");
		 * 
		 * panelButton.add(addField); panelButton.add(removeField);
		 * panelButton.add(primaryKey); panelButton.setFloatable(false);
		 */

		tableSchema = new JRssTable(model);

		add(new JScrollPane(tableSchema), BorderLayout.CENTER);
	}

	public void fillSchema(RssElement elem) {
		model.setRowCount(0);
		elements.clear();

		row = 0;

		insertElementToTable(elem);
	}

	int row = 0;

	private void insertElementToTable(RssElement elem) {
		for (RssElement childElem : elem.getChildrens()) {
			if (!childElem.isHidden()) {
				model.insertRow(row, new Object[] { childElem,
						childElem.getValue() });
				elements.put(row++, childElem);
				insertElementToTable(childElem);
			}
		}
	}

	public boolean fillValueToRssElements() {
		int rowCount = tableSchema.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			RssElement elem = elements.get(i);
			Object obj = tableSchema.getValueAt(i, 1);
			if (obj != null && !obj.toString().equals("")) {
				elem.setValue(obj.toString());
			} else {
				return false;
			}
		}
		return true;
	}
}
