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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.apatar.systemtray.Scheduling;

public class JDailyPanel extends JPanel implements JobSchedule {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4831532079472945630L;
	private Scheduling			scheduling			= null;
	JSpinner					repeatTaskEveryDWM	= new JSpinner();

	public JDailyPanel(Scheduling schedulig,
			ApatarSchedulingChangeAction changeAction) {
		super();
		this.scheduling = schedulig;
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		setBorder(BorderFactory.createTitledBorder("Daily"));
		add(new JLabel("Every"));
		add(repeatTaskEveryDWM);
		add(new JLabel("day(s)"));
		repeatTaskEveryDWM.setName("repeatTaskEveryDWM");
		repeatTaskEveryDWM.setValue(1);

		repeatTaskEveryDWM.setMinimumSize(new Dimension(50, repeatTaskEveryDWM
				.getPreferredSize().height));
		repeatTaskEveryDWM.setPreferredSize(new Dimension(50,
				repeatTaskEveryDWM.getPreferredSize().height));
		repeatTaskEveryDWM.addChangeListener(changeAction);
	}

	public void fillSchedulerObjectFields(Scheduling scheduling) {
		scheduling.setRepeatTaskEveryDWM((Integer) repeatTaskEveryDWM
				.getValue(), false);
	}

	public void readFromSchedulerObjectFields() {
		repeatTaskEveryDWM.setValue(scheduling.getRepeatTaskEveryDWM());
	}

	/**
	 * @return the scheduling
	 */
	public Scheduling getScheduling() {
		return scheduling;
	}

}
