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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import com.apatar.systemtray.Scheduling;

public class JDailyFrequencyPanel extends JPanel implements JobSchedule {

	/**
	 * 
	 */
	private static final long				serialVersionUID	= 7139187935401274704L;
	JSpinner								occursAt;
	JSpinner								dailyRepeatTaskEveryMinuteOrHour				= new JSpinner();
	JComboBox								typePeriod			= new JComboBox(
																		new String[] {
			"Minute", "Hour"											});
	JSpinner								startingAt;
	JSpinner								endingAt;

	JRadioButton							rbOccurrsAt			= new JRadioButton(
																		"Occurs once at: ",
																		true);
	JRadioButton							rbOccursEvery		= new JRadioButton(
																		"Occurs every :");

	private Scheduling						scheduling			= null;
	private ApatarSchedulingChangeAction	changeAction		= null;

	public JDailyFrequencyPanel(Scheduling scheduling,
			ApatarSchedulingChangeAction changeAaction) {
		super();
		this.scheduling = scheduling;
		this.changeAction = changeAaction;
		setBorder(BorderFactory.createTitledBorder("Daily frequency"));
		create();
		addListeners();
	}

	private void create() {
		SpinnerDateModel startDateModel = new SpinnerDateModel();
		SpinnerDateModel endDateModel = new SpinnerDateModel();
		startDateModel.setCalendarField(Calendar.AM_PM);
		endDateModel.setCalendarField(Calendar.AM_PM);

		Date date = new Date();

		SpinnerDateModel occursSm = new SpinnerDateModel(date, null, null,
				Calendar.HOUR_OF_DAY);
		occursAt = new JSpinner(occursSm);
		JSpinner.DateEditor occursDe = new JSpinner.DateEditor(occursAt,
				"HH:mm");
		occursAt.setEditor(occursDe);

		SpinnerDateModel startSm = new SpinnerDateModel(date, null, null,
				Calendar.HOUR_OF_DAY);
		startingAt = new JSpinner(startSm);
		JSpinner.DateEditor startDe = new JSpinner.DateEditor(startingAt,
				"HH:mm");
		startingAt.setEditor(startDe);

		SpinnerDateModel endSm = new SpinnerDateModel(date, null, null,
				Calendar.HOUR_OF_DAY);
		endingAt = new JSpinner(endSm);
		JSpinner.DateEditor endDe = new JSpinner.DateEditor(endingAt, "HH:mm");
		endingAt.setEditor(endDe);

		setLayout(new GridLayout(3, 1, 5, 5));

		JPanel panel1 = new JPanel();
		BoxLayout layout1 = new BoxLayout(panel1, BoxLayout.X_AXIS);
		panel1.setLayout(layout1);
		panel1.add(rbOccurrsAt);
		panel1.add(Box.createHorizontalStrut(10));
		panel1.add(occursAt);
		occursAt.setMinimumSize(new Dimension(100,
				occursAt.getPreferredSize().height));
		occursAt.setPreferredSize(new Dimension(100, occursAt
				.getPreferredSize().height));
		occursAt.setMaximumSize(new Dimension(100,
				occursAt.getPreferredSize().height));

		JPanel panel2 = new JPanel();
		BoxLayout layout2 = new BoxLayout(panel2, BoxLayout.X_AXIS);
		panel2.setLayout(layout2);
		panel2.add(rbOccursEvery);
		panel2.add(Box.createHorizontalStrut(10));
		panel2.add(dailyRepeatTaskEveryMinuteOrHour);
		panel2.add(Box.createHorizontalStrut(3));
		panel2.add(typePeriod);
		panel2.add(Box.createHorizontalStrut(20));
		panel2.add(new JLabel("Starting at:"));
		panel2.add(Box.createHorizontalStrut(15));
		panel2.add(startingAt);
		panel2.add(Box.createHorizontalStrut(10));
		dailyRepeatTaskEveryMinuteOrHour.setName("dailyRepeatTaskEveryMinuteOrHour");
		dailyRepeatTaskEveryMinuteOrHour.setMinimumSize(new Dimension(50,
				dailyRepeatTaskEveryMinuteOrHour.getPreferredSize().height));
		dailyRepeatTaskEveryMinuteOrHour.setPreferredSize(new Dimension(50,
				dailyRepeatTaskEveryMinuteOrHour.getPreferredSize().height));
		dailyRepeatTaskEveryMinuteOrHour.setMaximumSize(new Dimension(50,
				dailyRepeatTaskEveryMinuteOrHour.getPreferredSize().height));
		typePeriod.setMinimumSize(new Dimension(80,
				dailyRepeatTaskEveryMinuteOrHour.getPreferredSize().height));
		typePeriod.setPreferredSize(new Dimension(80,
				dailyRepeatTaskEveryMinuteOrHour.getPreferredSize().height));
		typePeriod.setMaximumSize(new Dimension(80,
				dailyRepeatTaskEveryMinuteOrHour.getPreferredSize().height));
		startingAt.setMinimumSize(new Dimension(100, startingAt
				.getPreferredSize().height));
		startingAt.setPreferredSize(new Dimension(100, startingAt
				.getPreferredSize().height));
		startingAt.setMaximumSize(new Dimension(100, startingAt
				.getPreferredSize().height));

		JPanel panel3 = new JPanel();
		BoxLayout layout3 = new BoxLayout(panel3, BoxLayout.X_AXIS);
		panel3.setLayout(layout3);
		panel3.add(Box.createHorizontalGlue());
		panel3.add(new JLabel("Ending at:"));
		panel3.add(Box.createHorizontalStrut(15));
		panel3.add(endingAt);
		panel3.add(Box.createHorizontalStrut(10));
		endingAt.setMinimumSize(new Dimension(100,
				endingAt.getPreferredSize().height));
		endingAt.setPreferredSize(new Dimension(100, endingAt
				.getPreferredSize().height));
		endingAt.setMaximumSize(new Dimension(100,
				endingAt.getPreferredSize().height));

		ButtonGroup group = new ButtonGroup();
		group.add(rbOccurrsAt);
		group.add(rbOccursEvery);
		dailyRepeatTaskEveryMinuteOrHour.setEnabled(false);
		typePeriod.setEnabled(false);
		startingAt.setEnabled(false);
		endingAt.setEnabled(false);

		dailyRepeatTaskEveryMinuteOrHour.setValue(1);

		add(panel1);
		add(panel2);
		add(panel3);

	}

	private void addListeners() {
		dailyRepeatTaskEveryMinuteOrHour.addChangeListener(changeAction);

		rbOccurrsAt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rbOccurrsAt.isSelected()) {
					occursAt.setEnabled(true);
					dailyRepeatTaskEveryMinuteOrHour.setEnabled(false);
					typePeriod.setEnabled(false);
					startingAt.setEnabled(false);
					endingAt.setEnabled(false);
				}
			}
		});
		rbOccursEvery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rbOccursEvery.isSelected()) {
					occursAt.setEnabled(false);
					dailyRepeatTaskEveryMinuteOrHour.setEnabled(true);
					typePeriod.setEnabled(true);
					startingAt.setEnabled(true);
					endingAt.setEnabled(true);
				}
			}
		});
	}

	public void fillSchedulerObjectFields(Scheduling scheduling) {
		if (rbOccurrsAt.isSelected()) {
			scheduling.setRepeatTaskEveryDWMtype(1, false);
			SpinnerDateModel modelOccursAt = (SpinnerDateModel) occursAt
					.getModel();
			GregorianCalendar calendarOccursAt = new GregorianCalendar();
			calendarOccursAt.setTime(modelOccursAt.getDate());
			scheduling.setRunTaskOnceAtTime(calendarOccursAt);

		} else {
			scheduling.setRepeatTaskEveryDWMtype(2, false);
			scheduling.setDailyRunType(typePeriod.getSelectedIndex() + 1);
			scheduling.setDailyRepeatTaskEveryMinuteOrHour((Integer) dailyRepeatTaskEveryMinuteOrHour
					.getValue(), false);
			SpinnerDateModel modelStartingAt = (SpinnerDateModel) startingAt
					.getModel();
			GregorianCalendar calendarStartingAt = new GregorianCalendar();
			calendarStartingAt.setTime(modelStartingAt.getDate());

			scheduling.setStartTaskAtTime(calendarStartingAt);

			SpinnerDateModel modelEndingAt = (SpinnerDateModel) endingAt
					.getModel();
			GregorianCalendar calendarEndingAt = new GregorianCalendar();
			calendarEndingAt.setTime(modelEndingAt.getDate());

			scheduling.setStopTaskAtTime(calendarEndingAt);
		}
	}

	public void readFromSchedulerObjectFields() {
		if (scheduling.getRepeatTaskEveryDWMtype() == 1) {
			rbOccurrsAt.setSelected(true);
			occursAt.setValue(scheduling.getRunTaskOnceAtTime().getTime());

		} else {
			rbOccursEvery.setSelected(true);

			dailyRepeatTaskEveryMinuteOrHour.setValue(scheduling.getDailyRepeatTaskEveryMinuteOrHour());
			typePeriod.setSelectedIndex(scheduling.getDailyRunType() - 1);
			startingAt.setValue(scheduling.getStartTaskAtTime().getTime());
			endingAt.setValue(scheduling.getStopTaskAtTime().getTime());

			dailyRepeatTaskEveryMinuteOrHour.setEnabled(true);
			typePeriod.setEnabled(true);
			startingAt.setEnabled(true);
			endingAt.setEnabled(true);
		}
	}

	/**
	 * @return the scheduling
	 */
	public Scheduling getScheduling() {
		return scheduling;
	}
}
