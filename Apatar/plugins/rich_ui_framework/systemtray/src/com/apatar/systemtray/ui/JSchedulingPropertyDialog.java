/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.systemtray.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.apatar.systemtray.ApatarSchedulingWeeklyException;
import com.apatar.systemtray.Scheduling;
import com.apatar.systemtray.Tray;

public class JSchedulingPropertyDialog extends JDialog {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1287601956925465372L;
	JTable						taskTable;
	JButton						newButton;
	JButton						editButton;
	JButton						deleteButton;
	JButton						closeButton;
	JButton						startButton;
	JButton						stopButton;

	Tray						tray;

	public JSchedulingPropertyDialog(Tray tray) {
		super();
		setTitle("Apatar scheduling");
		this.tray = tray;
		createDialog();
		addListener();
	}

	private void createDialog() {
		setLayout(new BorderLayout(10, 10));

		setSize(500, 500);

		taskTable = new JTable(new DefaultTableModel(new Object[] { "Name",
				"Status", "Next" }, 0));

		add(new JScrollPane(taskTable), BorderLayout.CENTER);

		newButton = new JButton("New");
		editButton = new JButton("Edit");
		deleteButton = new JButton("Delete");
		closeButton = new JButton("Close");
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");

		JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
		buttonPanel.add(newButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(closeButton);

		add(buttonPanel, BorderLayout.SOUTH);

	}

	private void addListener() {
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Scheduling scheduling = tray.createScheduling(null);
				JEditJobScheduleDialog dlg = new JEditJobScheduleDialog(
						scheduling);
				dlg.setVisible(true);
				if (dlg.getOption() != JEditJobScheduleDialog.OK_OPTION) {
					return;
				}
				// dlg.fillSchedulerObjectFields(scheduling);
				addScheduling(scheduling);
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selRow = taskTable.getSelectedRow();
				if (selRow < 0) {
					return;
				}
				DefaultTableModel model = (DefaultTableModel) taskTable
						.getModel();
				while (selRow >= 0) {
					String name = taskTable.getModel().getValueAt(selRow, 0)
							.toString();
					model.removeRow(selRow);
					tray.deleteScheduling(name);
					selRow = taskTable.getSelectedRow();
				}
			}
		});

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] selRows = taskTable.getSelectedRows();
				for (int element : selRows) {
					String name = taskTable.getModel().getValueAt(element, 0)
							.toString();
					try {
						tray.startScheduling(name);
					} catch (RuntimeException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Task `" + name + "`",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] selRows = taskTable.getSelectedRows();
				for (int element : selRows) {
					String name = taskTable.getModel().getValueAt(element, 0)
							.toString();
					tray.stopScheduling(name);
				}
			}
		});

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int row = taskTable.getSelectedRow();
				if (row < 0) {
					return;
				}
				String name = taskTable.getModel().getValueAt(row, 0)
						.toString();
				Scheduling scheduling = tray.getScheduling(name);
				scheduling.cancelTask();
				JEditJobScheduleDialog dlg = new JEditJobScheduleDialog(
						scheduling);
				dlg.readFromSchedulerObjectFields();
				dlg.setVisible(true);

				if (dlg.getOption() != JEditJobScheduleDialog.OK_OPTION) {
					return;
				}
				// dlg.fillSchedulerObjectFields(scheduling);
				DefaultTableModel model = (DefaultTableModel) taskTable
						.getModel();
				model.setValueAt(scheduling.getDate(), row, 2);
				model.setValueAt(scheduling.getSchedulingName(), row, 0);
				try {
					scheduling.getNextTaskRunDate(true);
				} catch (ApatarSchedulingWeeklyException e) {
					System.err.println(e.getMessage());
				}
				model.setValueAt(scheduling.getTaskStatus(), row, 1);
			}
		});

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
	}

	public void renewTaskTable() {
		if (isVisible()) {
			DefaultTableModel model = (DefaultTableModel) taskTable.getModel();
			int totalRowsCount = model.getRowCount() - 1;
			for (int i = totalRowsCount; i >= 0; i--) {
				model.removeRow(i);
			}
			readSchedulings();
			repaint();
		}
	}

	private void readSchedulings() {
		for (Scheduling scheduling : tray.getSchedulings().values()) {
			addScheduling(scheduling);
		}
	}

	private void addScheduling(Scheduling scheduling) {
		tray.addScheduling(scheduling);
		DefaultTableModel model = (DefaultTableModel) taskTable.getModel();
		model.addRow(new Object[] { scheduling.getSchedulingName(),
				scheduling.getTaskStatus(), scheduling.getDate() });
	}
}
