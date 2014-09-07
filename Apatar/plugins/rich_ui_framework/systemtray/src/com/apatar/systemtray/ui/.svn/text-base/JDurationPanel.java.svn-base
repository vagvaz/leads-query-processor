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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;

import com.apatar.systemtray.Scheduling;
import com.toedter.calendar.JDateChooser;

public class JDurationPanel extends JPanel implements JobSchedule {

	/**
	 * 
	 */
	private static final long				serialVersionUID	= 6130429543042715096L;
	JDateChooser							startDate			= new JDateChooser(
																		new Date(),
																		"MM/dd/yyyy");
	JDateChooser							endDate				= new JDateChooser(
																		new Date(),
																		"MM/dd/yyyy");

	JRadioButton							rbEndDate			= new JRadioButton(
																		"End date: ");
	JRadioButton							rbNoEndDate			= new JRadioButton(
																		"No end date: ",
																		true);

	Scheduling								scheduling			= null;
	JSpinner								totalNumberTaskRuns	= new JSpinner();
	private ApatarSchedulingChangeAction	changeAction;

	public JDurationPanel(Scheduling scheduling,
			ApatarSchedulingChangeAction changeAction) {
		super();
		this.scheduling = scheduling;
		this.changeAction = changeAction;
		setBorder(BorderFactory.createTitledBorder("Duration"));
		create();
		addListeners();
	}

	private void create() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));

		JPanel startDatePanel = new JPanel(new GridLayout(3, 2, 3, 3));
		JPanel endDatePanel = new JPanel(new GridLayout(3, 2, 3, 3));

		add(startDatePanel);
		add(endDatePanel);

		startDatePanel.add(new JLabel("Start date: "));
		startDatePanel.add(startDate);

		startDatePanel.add(new JLabel());
		startDatePanel.add(new JLabel());

		startDatePanel.add(new JLabel("Total number of runs"));
		startDatePanel.add(new JLabel("(0 - unlimited)"));

		ButtonGroup groupEndDate = new ButtonGroup();
		groupEndDate.add(rbEndDate);
		groupEndDate.add(rbNoEndDate);
		endDatePanel.add(rbEndDate);
		endDatePanel.add(endDate);

		endDatePanel.add(rbNoEndDate);
		endDatePanel.add(new JLabel());

		endDatePanel.add(totalNumberTaskRuns);
		endDatePanel.add(new JLabel());

		endDate.setEnabled(false);

		startDate.setMinimumSize(new Dimension(90,
				endDate.getPreferredSize().height));
		startDate.setPreferredSize(new Dimension(90,
				endDate.getPreferredSize().height));
		startDate.setMaximumSize(new Dimension(90,
				endDate.getPreferredSize().height));
		endDate.setMinimumSize(new Dimension(90,
				endDate.getPreferredSize().height));
		endDate.setPreferredSize(new Dimension(90,
				endDate.getPreferredSize().height));
		endDate.setMaximumSize(new Dimension(90,
				endDate.getPreferredSize().height));
		totalNumberTaskRuns.setMaximumSize(new Dimension(90,
				totalNumberTaskRuns.getPreferredSize().height));
		totalNumberTaskRuns.setName("totalNumberTaskRuns");
	}

	private void addListeners() {
		rbEndDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rbEndDate.isSelected()) {
					endDate.setEnabled(true);
				}
			}
		});
		rbNoEndDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rbNoEndDate.isSelected()) {
					endDate.setEnabled(false);
				}
			}
		});
		totalNumberTaskRuns.addChangeListener(this.changeAction);
	}

	public void fillSchedulerObjectFields(Scheduling scheduling) {

		scheduling.setStartTaskAtDate((GregorianCalendar) startDate
				.getCalendar());
		scheduling.setTotalNumberTaskRuns((Integer) totalNumberTaskRuns
				.getValue(), false);
		if (rbEndDate.isSelected()) {
			scheduling.setStopTaskAtDate((GregorianCalendar) endDate
					.getCalendar());
		} else {
			scheduling.setStopTaskAtDate(null);
		}
	}

	public void readFromSchedulerObjectFields() {
		startDate.setCalendar(scheduling.getStartTaskAtDate());
		totalNumberTaskRuns.setValue(scheduling.getTotalNumberTaskRuns());
		if (scheduling.getStopTaskAtDate() == null) {
			rbNoEndDate.setSelected(false);
			endDate.setEnabled(false);
		} else {
			endDate.setCalendar(scheduling.getStopTaskAtDate());
			rbEndDate.setSelected(true);
			endDate.setEnabled(true);
		}
	}

}
