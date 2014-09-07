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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;

import com.apatar.systemtray.Scheduling;

public class JMonthlyPanel extends JPanel implements JobSchedule {

	/**
	 * 
	 */
	private static final long	serialVersionUID		= -4104935986449430921L;
	JSpinner					monthlyRunAtDayOfMonth	= new JSpinner();
	JSpinner					repeatTaskEveryDWM1		= new JSpinner();

	JRadioButton				rbDay					= new JRadioButton(
																"Day", true);
	JRadioButton				rbThe					= new JRadioButton(
																"The");

	JComboBox					numberDay				= new JComboBox(
																new Object[] {
			"1st", "2nd", "3rd", "4th", "last"					});
	JComboBox					dayOfWeek				= new JComboBox(
																new Object[] {
			"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
			"Sunday"											});
	JSpinner					repeatTaskEveryDWM2		= new JSpinner();

	private Scheduling			scheduling				= null;

	public JMonthlyPanel(Scheduling scheduling,
			ApatarSchedulingChangeAction changeAction) {
		super();
		this.scheduling = scheduling;
		create(changeAction);
		addListeners();
	}

	public void fillSchedulerObjectFields(Scheduling scheduling) {
		if (rbDay.isSelected()) {
			scheduling.setMonthlyRunType(1);
			scheduling.setRepeatTaskEveryDWM((Integer) repeatTaskEveryDWM1
					.getValue(), false);
			scheduling.setMonthlyRunAtDayOfMonth(
					(Integer) monthlyRunAtDayOfMonth.getValue(), false);
		} else {
			scheduling.setMonthlyRunType(2);
			scheduling.setRepeatTaskEveryDWM((Integer) repeatTaskEveryDWM2
					.getValue(), false);
			scheduling
					.setMonthlyNumberOfDayOfWeek(numberDay.getSelectedIndex() + 1);
			scheduling
					.setMonthlyRunAtDayOfWeek(dayOfWeek.getSelectedIndex() + 1);
		}
	}

	public void readFromSchedulerObjectFields() {
		if (scheduling.getMonthlyRunType() == 1) {
			monthlyRunAtDayOfMonth.setValue(scheduling
					.getMonthlyRunAtDayOfMonth());
			repeatTaskEveryDWM1.setValue(scheduling.getRepeatTaskEveryDWM());
			rbDay.setSelected(true);

			numberDay.setEnabled(false);
			dayOfWeek.setEnabled(false);
			repeatTaskEveryDWM2.setEnabled(false);
		} else {
			numberDay
					.setSelectedIndex(scheduling.getMonthlyNumberOfDayOfWeek() - 1);
			dayOfWeek
					.setSelectedIndex(scheduling.getMonthlyRunAtDayOfWeek() - 1);
			repeatTaskEveryDWM2.setValue(scheduling.getRepeatTaskEveryDWM());
			rbThe.setSelected(true);

			numberDay.setEnabled(true);
			dayOfWeek.setEnabled(true);
			repeatTaskEveryDWM2.setEnabled(true);
		}
	}

	private void create(ApatarSchedulingChangeAction changeAction) {
		setBorder(BorderFactory.createTitledBorder("Monthly"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(panel1);
		add(Box.createVerticalStrut(5));
		add(panel2);

		ButtonGroup group = new ButtonGroup();
		group.add(rbDay);
		group.add(rbThe);

		monthlyRunAtDayOfMonth.setMinimumSize(new Dimension(50,
				monthlyRunAtDayOfMonth.getPreferredSize().height));
		monthlyRunAtDayOfMonth.setPreferredSize(new Dimension(50,
				monthlyRunAtDayOfMonth.getPreferredSize().height));
		monthlyRunAtDayOfMonth.setValue(1);
		monthlyRunAtDayOfMonth.setName("monthlyRunAtDayOfMonth");
		monthlyRunAtDayOfMonth.addChangeListener(changeAction);

		repeatTaskEveryDWM1.setMinimumSize(new Dimension(35,
				repeatTaskEveryDWM1.getPreferredSize().height));
		repeatTaskEveryDWM1.setPreferredSize(new Dimension(35,
				repeatTaskEveryDWM1.getPreferredSize().height));
		repeatTaskEveryDWM1.setValue(1);
		repeatTaskEveryDWM1.setName("repeatTaskEveryDWM");
		repeatTaskEveryDWM1.addChangeListener(changeAction);
		panel1.add(rbDay);
		panel1.add(monthlyRunAtDayOfMonth);
		panel1.add(new JLabel("of every"));
		panel1.add(repeatTaskEveryDWM1);
		panel1.add(new JLabel("month(s)"));

		repeatTaskEveryDWM2.setMinimumSize(new Dimension(35,
				repeatTaskEveryDWM2.getPreferredSize().height));
		repeatTaskEveryDWM2.setPreferredSize(new Dimension(35,
				repeatTaskEveryDWM2.getPreferredSize().height));
		repeatTaskEveryDWM2.setValue(1);
		repeatTaskEveryDWM2.setName("repeatTaskEveryDWM");
		repeatTaskEveryDWM2.addChangeListener(changeAction);
		numberDay.setMinimumSize(new Dimension(50,
				numberDay.getPreferredSize().height));
		numberDay.setPreferredSize(new Dimension(50, numberDay
				.getPreferredSize().height));
		dayOfWeek.setMinimumSize(new Dimension(100, dayOfWeek
				.getPreferredSize().height));
		dayOfWeek.setPreferredSize(new Dimension(100, dayOfWeek
				.getPreferredSize().height));
		panel2.add(rbThe);
		panel2.add(numberDay);
		panel2.add(dayOfWeek);
		panel2.add(new JLabel("of every"));
		panel2.add(repeatTaskEveryDWM2);
		panel2.add(new JLabel("month(s)"));

		numberDay.setEnabled(false);
		dayOfWeek.setEnabled(false);
		repeatTaskEveryDWM2.setEnabled(false);
	}

	private void addListeners() {
		rbDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rbDay.isSelected()) {
					monthlyRunAtDayOfMonth.setEnabled(true);
					repeatTaskEveryDWM1.setEnabled(true);

					numberDay.setEnabled(false);
					dayOfWeek.setEnabled(false);
					repeatTaskEveryDWM2.setEnabled(false);
				}
			}
		});
		rbThe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rbThe.isSelected()) {
					monthlyRunAtDayOfMonth.setEnabled(false);
					repeatTaskEveryDWM1.setEnabled(false);

					numberDay.setEnabled(true);
					dayOfWeek.setEnabled(true);
					repeatTaskEveryDWM2.setEnabled(true);
				}
			}
		});
	}

	/**
	 * @return the scheduling
	 */
	public Scheduling getScheduling() {
		return scheduling;
	}

}
