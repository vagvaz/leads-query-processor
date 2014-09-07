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

package com.apatar.functions.ui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.apatar.functions.ReplaceIfObject;
import com.apatar.functions.String.ReplaceIfTransformFunction;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JColumnsNotEditableTable;

public class JReplaceIfDialog extends JDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = 644190474123112811L;
	JColumnsNotEditableTable tableSchema;
	JButton addField;
	JButton removeField;
	JCheckBoxMenuItem caseSencitive = new JCheckBoxMenuItem("Case sensitive",
			true);
	JCheckBoxMenuItem replacePartValue = new JCheckBoxMenuItem(
			"Replace a part of a value", false);
	JCheckBoxMenuItem treatNullAsEmptyString = new JCheckBoxMenuItem(
			"Treat null as empty string", false);
	JMenu optionsMenu;
	int number = 1;
	private JMenuBar menubar;

	public JReplaceIfDialog(ReplaceIfTransformFunction func)
			throws HeadlessException {
		super(ApatarUiMain.MAIN_FRAME);
		createDialog();
		generateListeners();
		setSize(500, 500);
		setModal(true);
		DefaultTableModel model = (DefaultTableModel) tableSchema.getModel();

		if (func.getObjects() == null) {
			return;
		}
		number = 1;
		for (ReplaceIfObject obj : func.getObjects()) {
			tableSchema.removeAll();
			model.insertRow(tableSchema.getRowCount(), new Object[] { number++,
					obj.getInput(), obj.getReplase() });
		}
		caseSencitive.setSelected(func.isCaseSencitive());
		replacePartValue.setSelected(func.isReplacePartValue());
		treatNullAsEmptyString.setSelected(func.isTreatNullAsEmptyString());
	}

	private void createDialog() {
		setLayout(new BorderLayout());
		menubar = new JMenuBar();

		optionsMenu = new JMenu("Options");
		optionsMenu.add(caseSencitive);
		optionsMenu.add(replacePartValue);
		optionsMenu.add(treatNullAsEmptyString);

		menubar.add(optionsMenu);

		JToolBar panelButton = new JToolBar();

		addField = new JButton(new ImageIcon(this.getClass().getResource(
				"add.png")));
		removeField = new JButton(new ImageIcon(this.getClass().getResource(
				"delete.png")));
		// primaryKey = new JButton(new
		// ImageIcon(this.getClass().getResource("primary.png")));

		addField.setToolTipText("Add column");
		removeField.setToolTipText("Remove selected column");
		// primaryKey.setToolTipText("Set/Reset primary key");

		panelButton.add(addField);
		panelButton.add(removeField);
		// panelButton.add(primaryKey);
		panelButton.setFloatable(false);

		List<Integer> columns = new ArrayList<Integer>();
		columns.add(0);
		tableSchema = new JColumnsNotEditableTable(new DefaultTableModel(
				new Object[][] {}, new Object[] { "Condition", "Input Value",
						"Replace With" }), columns);

		TableColumn column = tableSchema.getColumn("Condition");
		column.setMinWidth(100);
		column.setMaxWidth(100);

		setJMenuBar(menubar);
		add(panelButton, BorderLayout.NORTH);
		add(new JScrollPane(tableSchema), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}

	private void generateListeners() {

		treatNullAsEmptyString.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				optionsMenu.setVisible(true);
			}
		});

		addField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((DefaultTableModel) tableSchema.getModel()).insertRow(
						tableSchema.getRowCount(), new Object[] { number++, "",
								"" });
			}
		});
		removeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] selRows = tableSchema.getSelectedRows();
				if (selRows.length > 0) {
					DefaultTableModel model = (DefaultTableModel) tableSchema
							.getModel();
					for (int selRow : selRows) {
						model.removeRow(tableSchema.getSelectedRow());
					}
					model.fireTableDataChanged();
					number = 1;

					for (int i = 0; i < model.getRowCount(); i++) {
						model.setValueAt(number++, i, 0);
					}
				}
			}
		});
	}

	public List<ReplaceIfObject> getReplaceObjects() {
		List<ReplaceIfObject> objs = new ArrayList<ReplaceIfObject>();
		for (int i = 0; i < tableSchema.getRowCount(); i++) {
			String input = tableSchema.getValueAt(i, 1).toString();
			String replace = tableSchema.getValueAt(i, 2).toString();
			ReplaceIfObject obj = new ReplaceIfObject(input, replace);
			objs.add(obj);
		}
		return objs;
	}

	public boolean isCaseSensitive() {
		return caseSencitive.isSelected();
	}

	private final JButton buttonOk = new JButton("Ok");
	private final JButton buttonCancel = new JButton("Cancel");

	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = 1;

	public int option;

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		// panel.add(Box.createHorizontalStrut(5));
		// panel.add(caseSencitive);
		// panel.add(Box.createHorizontalStrut(5));
		// panel.add(replacePartValue);
		panel.add(Box.createHorizontalGlue());
		panel.add(buttonOk);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(buttonCancel);

		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				option = OK_OPTION;
				setVisible(false);
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				option = CANCEL_OPTION;
				setVisible(false);
			}
		});

		return panel;
	}

	public boolean isReplacePartValue() {
		return replacePartValue.isSelected();
	}

	public boolean isTreatNullAsEmptyString() {
		return treatNullAsEmptyString.isSelected();
	}
}
