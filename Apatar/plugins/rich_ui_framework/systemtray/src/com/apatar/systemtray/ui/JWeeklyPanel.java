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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.apatar.systemtray.Scheduling;

public class JWeeklyPanel extends JPanel implements JobSchedule {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 2407842041364223417L;

	private Scheduling			scheduling			= null;

	JCheckBox					mon					= new JCheckBox("Mon");
	JCheckBox					tue					= new JCheckBox("Tue");
	JCheckBox					wed					= new JCheckBox("Wed");
	JCheckBox					thur				= new JCheckBox("Thur");
	JCheckBox					fri					= new JCheckBox("Fri");
	JCheckBox					sat					= new JCheckBox("Sat");
	JCheckBox					sun					= new JCheckBox("Sun");
	JSpinner					repeatTaskEveryDWM	= new JSpinner();

	public JWeeklyPanel(Scheduling scheduling,
			ApatarSchedulingChangeAction changeAction) {
		super();
		this.scheduling = scheduling;
		setBorder(BorderFactory.createTitledBorder("Weekly"));
		create(changeAction);
	}

	public void fillSchedulerObjectFields(Scheduling scheduling) {
		scheduling.setRepeatTaskEveryDWM((Integer) repeatTaskEveryDWM
				.getValue(), false);
		scheduling.setWeeklyRunAtDay(1, mon.isSelected());
		scheduling.setWeeklyRunAtDay(2, tue.isSelected());
		scheduling.setWeeklyRunAtDay(3, wed.isSelected());
		scheduling.setWeeklyRunAtDay(4, thur.isSelected());
		scheduling.setWeeklyRunAtDay(5, fri.isSelected());
		scheduling.setWeeklyRunAtDay(6, sat.isSelected());
		scheduling.setWeeklyRunAtDay(7, sun.isSelected());
	}

	public void readFromSchedulerObjectFields() {
		repeatTaskEveryDWM.setValue(scheduling.getRepeatTaskEveryDWM());
		for (int i = 0; i < 7; i++) {
			switch (i) {
				case 0:
					sun.setSelected(scheduling.getWeeklyRunAtDay(i + 1));
					break;
				case 1:
					mon.setSelected(scheduling.getWeeklyRunAtDay(i + 1));
					break;
				case 2:
					tue.setSelected(scheduling.getWeeklyRunAtDay(i + 1));
					break;
				case 3:
					wed.setSelected(scheduling.getWeeklyRunAtDay(i + 1));
					break;
				case 4:
					thur.setSelected(scheduling.getWeeklyRunAtDay(i + 1));
					break;
				case 5:
					fri.setSelected(scheduling.getWeeklyRunAtDay(i + 1));
					break;
				case 6:
					sat.setSelected(scheduling.getWeeklyRunAtDay(i + 1));
					break;
			}
		}
	}

	private void create(ApatarSchedulingChangeAction changeAction) {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		repeatTaskEveryDWM.setMinimumSize(new Dimension(60, repeatTaskEveryDWM
				.getPreferredSize().height));
		repeatTaskEveryDWM.setPreferredSize(new Dimension(60,
				repeatTaskEveryDWM.getPreferredSize().height));

		repeatTaskEveryDWM.setValue(1);
		repeatTaskEveryDWM.setName("repeatTaskEveryDWM");
		repeatTaskEveryDWM.addChangeListener(changeAction);

		JPanel periodPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		periodPnl.add(new JLabel("Every"));
		periodPnl.add(repeatTaskEveryDWM);
		periodPnl.add(new JLabel("week(s) on:"));

		JPanel daysPnl = new JPanel(new GridLayout(2, 5, 5, 5));
		daysPnl.add(mon);
		daysPnl.add(tue);
		daysPnl.add(wed);
		daysPnl.add(thur);
		daysPnl.add(fri);
		daysPnl.add(sat);
		daysPnl.add(sun);

		add(periodPnl);
		add(Box.createVerticalStrut(5));
		add(daysPnl);
	}

	/**
	 * @return the scheduling
	 */
	public Scheduling getScheduling() {
		return scheduling;
	}

}
