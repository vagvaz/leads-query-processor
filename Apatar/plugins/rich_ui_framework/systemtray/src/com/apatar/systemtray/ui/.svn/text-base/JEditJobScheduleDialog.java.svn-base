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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.apatar.systemtray.ApatarSchedulingWeeklyException;
import com.apatar.systemtray.Scheduling;
import com.apatar.ui.ApatarFileFilter;

public class JEditJobScheduleDialog extends JDialog implements JobSchedule {

	/**
	 *
	 */
	private static final long				serialVersionUID	= 4204106750708205626L;
	static final int						OCCUR_DAILY			= 1;
	static final int						OCCUR_WEEKLY		= 2;
	static final int						OCCUR_MONTHLY		= 3;

	public static final int					OK_OPTION			= 1;
	public static final int					CANCEL_OPTION		= 0;

	int										option				= CANCEL_OPTION;

	private int								height;

	private Scheduling						scheduling			= null;

	JPanel									namePanel;
	JPanel									occursPanel;
	JPanel									intervalPanel;
	JPanel									filePanel;
	JDailyFrequencyPanel					dailyFrequencyPanel;
	JDurationPanel							durationPanel;

	JTextField								nameField			= new JTextField();

	JDailyPanel								dailyPanel;
	JWeeklyPanel							weeklyPanel;
	JMonthlyPanel							monthlyPanel;

	CardLayout								intervalLayout		= new CardLayout();

	int										occur				= 1;

	JRadioButton							daily				= new JRadioButton(
																		"Daily");
	JRadioButton							weekly				= new JRadioButton(
																		"Weekly");
	JRadioButton							monthly				= new JRadioButton(
																		"Monthly");

	JTextField								nameFile			= new JTextField();
	JButton									selectFile			= new JButton(
																		"...");

	JButton									ok					= new JButton(
																		"Ok");

	private ApatarSchedulingChangeAction	changeListener		= null;

	public JEditJobScheduleDialog(Scheduling scheduling)
			throws HeadlessException {
		super();
		this.scheduling = scheduling;
		changeListener = new ApatarSchedulingChangeAction(this.scheduling);
		dailyFrequencyPanel = new JDailyFrequencyPanel(scheduling,
				changeListener);
		durationPanel = new JDurationPanel(scheduling, changeListener);
		setModal(true);
		setTitle("New or Edit Task");
		setSize(485, 455);
		setResizable(false);
		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));

		createNamePanel();

		createOccurs();
		createIntervalPanel();
		createFilePanel();
		JPanel mainOccursPanel = createMainOccurs();
		setIdenticalWidth(new Component[] { mainOccursPanel,
				dailyFrequencyPanel, durationPanel });
		getContentPane().add(namePanel);
		getContentPane().add(mainOccursPanel);
		getContentPane().add(dailyFrequencyPanel);
		getContentPane().add(durationPanel);
		getContentPane().add(filePanel);
		getContentPane().add(ok);
		addListeners();
	}

	private void addListeners() {
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				option = OK_OPTION;
				String path = nameFile.getText();
				if (path == null || path.equals("")) {
					JOptionPane.showMessageDialog(null,
							"Please, specify the filename of your project.");
					return;
				}
				File file = new File(path);
				if (!file.isFile()) {
					JOptionPane.showMessageDialog(null,
							"The filename is incorrect. Please, "
									+ "specify correct filename.");
					return;
				}
				// first check if no errors given
				Scheduling tempSched = new Scheduling(null, null);
				boolean confirmClosing = false;
				fillSchedulerObjectFields(tempSched);
				try {
					if (null == tempSched.getNextTaskRunDate(true)) {
						JOptionPane.showConfirmDialog(null,
								"Wrong next task run date/time. "
										+ "Correct schedule settings",
								"Error setting up task schedule",
								JOptionPane.OK_OPTION,
								JOptionPane.WARNING_MESSAGE);
					} else {
						if (tempSched.getNextTaskRunDate().before(
								new GregorianCalendar())) {
							String message = tempSched.getLastWarningMessage();
							JOptionPane
									.showConfirmDialog(
											null,
											"Task will never run. "
													+ (message == null ? (tempSched
															.getNextTaskRunDate() == null ? ""
															: "Calculated next run date is: "
																	+ tempSched
																			.getNextTaskRunDate()
																			.getTime()
																			.toString())
															: "Warning message is:\n"
																	+ message),
											"Error setting up task schedule",
											JOptionPane.OK_OPTION,
											JOptionPane.WARNING_MESSAGE);
						} else {
							confirmClosing = true;
						}
					}
					if (confirmClosing) {
						fillSchedulerObjectFields(scheduling);
						scheduling.getNextTaskRunDate(true);
						setVisible(false);
					}
				} catch (ApatarSchedulingWeeklyException e) {
					System.err.println(e.getMessage());
					JOptionPane.showMessageDialog(null, e.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private void createOccurs() {
		occursPanel = new JPanel(new GridLayout(3, 1, 5, 5));
		occursPanel.setBorder(BorderFactory.createTitledBorder("Occurs"));

		ButtonGroup groupOccurs = new ButtonGroup();
		groupOccurs.add(daily);
		groupOccurs.add(weekly);
		groupOccurs.add(monthly);

		daily.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeOccur(OCCUR_DAILY);
			}
		});
		weekly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeOccur(OCCUR_WEEKLY);
			}
		});
		monthly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeOccur(OCCUR_MONTHLY);
			}
		});

		daily.setSelected(true);

		occursPanel.add(daily);
		occursPanel.add(weekly);
		occursPanel.add(monthly);
	}

	void createNamePanel() {
		namePanel = new JPanel();
		BoxLayout layout = new BoxLayout(namePanel, BoxLayout.X_AXIS);
		namePanel.setLayout(layout);

		namePanel.add(new JLabel("Name"));

		namePanel.add(Box.createHorizontalStrut(5));
		namePanel.add(nameField);

		height = nameField.getPreferredSize().height;
		nameField.setMinimumSize(new Dimension(428, height));

		nameField.setPreferredSize(new Dimension(428, height));
		nameField.setMaximumSize(new Dimension(428, height));
	}

	void createFilePanel() {
		filePanel = new JPanel();
		BoxLayout layout = new BoxLayout(filePanel, BoxLayout.X_AXIS);
		filePanel.add(new JLabel("Project"));
		filePanel.add(Box.createHorizontalStrut(5));
		filePanel.setLayout(layout);
		filePanel.add(nameFile);
		filePanel.add(Box.createHorizontalStrut(5));
		filePanel.add(selectFile);

		nameFile.setMinimumSize(new Dimension(370, height));
		nameFile.setPreferredSize(new Dimension(370, height));
		nameFile.setMaximumSize(new Dimension(370, height));

		selectFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser(System
						.getProperty("user.dir"));
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setFileFilter(new ApatarFileFilter("aptr"));

				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File fileSrc = fileChooser.getSelectedFile();
					nameFile.setText(fileSrc.getPath());
				}
			}

		});
	}

	void changeOccur(int occur) {
		this.occur = occur;
		switch (occur) {
			case OCCUR_DAILY:
				intervalLayout.show(intervalPanel, "Day");
				break;
			case OCCUR_MONTHLY:
				intervalLayout.show(intervalPanel, "Month");
				break;
			case OCCUR_WEEKLY:
				intervalLayout.show(intervalPanel, "Week");
				break;
		}
	}

	private void createIntervalPanel() {
		intervalLayout = new CardLayout();
		intervalPanel = new JPanel(intervalLayout);
		dailyPanel = new JDailyPanel(scheduling, changeListener);
		weeklyPanel = new JWeeklyPanel(scheduling, changeListener);
		monthlyPanel = new JMonthlyPanel(scheduling, changeListener);
		intervalPanel.add("Day", dailyPanel);
		intervalPanel.add("Week", weeklyPanel);
		intervalPanel.add("Month", monthlyPanel);
	}

	private JPanel createMainOccurs() {
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.add(occursPanel);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(intervalPanel);
		return panel;
	}

	private void setIdenticalWidth(Component[] comps) {
		int maxWidth = 0;
		for (Component element : comps) {
			int width = element.getPreferredSize().width;
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		for (Component element : comps) {
			element.setPreferredSize(new Dimension(maxWidth, element
					.getPreferredSize().height));
		}
	}

	public void fillSchedulerObjectFields(Scheduling scheduling) {
		if (daily.isSelected()) {
			scheduling.setRecurringType(1);
			dailyPanel.fillSchedulerObjectFields(scheduling);
		}
		if (weekly.isSelected()) {
			scheduling.setRecurringType(2);
			weeklyPanel.fillSchedulerObjectFields(scheduling);

		}
		if (monthly.isSelected()) {
			scheduling.setRecurringType(3);
			monthlyPanel.fillSchedulerObjectFields(scheduling);
		}

		scheduling.setSchedulingName(nameField.getText());
		scheduling.setPathToDatamap(nameFile.getText());

		dailyFrequencyPanel.fillSchedulerObjectFields(scheduling);
		durationPanel.fillSchedulerObjectFields(scheduling);

	}

	public void readFromSchedulerObjectFields() {
		dailyFrequencyPanel.readFromSchedulerObjectFields();
		nameFile.setText(scheduling.getPathToDatamap());
		nameField.setText(scheduling.getSchedulingName());
		durationPanel.readFromSchedulerObjectFields();
		switch (scheduling.getReccuringType()) {
			case 1:
				daily.setSelected(true);
				dailyPanel.readFromSchedulerObjectFields();
				changeOccur(OCCUR_DAILY);
				break;
			case 2:
				weekly.setSelected(true);
				weeklyPanel.readFromSchedulerObjectFields();
				changeOccur(OCCUR_WEEKLY);
				break;
			case 3:
				monthly.setSelected(true);
				monthlyPanel.readFromSchedulerObjectFields();
				changeOccur(OCCUR_MONTHLY);
				break;
		}
	}

	public int getOption() {
		return option;
	}

	/**
	 * @return the changeListener
	 */
	public ApatarSchedulingChangeAction getChangeListener() {
		return changeListener;
	}

}
