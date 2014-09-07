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
 


package com.apatar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.apatar.core.ApplicationData;
import com.apatar.core.DateAndTimeSettings;

public class JParametersDateAndTimePanel extends JPanel {
	
	boolean createwithApplication = false;
	boolean reseted = false;
	
	JTextField defaultSample = new JTextField();
	JTextField customizeSample = new JTextField();
	
	JComboBox dateFormat;
	JComboBox dateSeparator;
	
	JComboBox timeFormat;
	JComboBox timeStandart;
	
	JButton reset = new JButton("Reset to System Settings");
	JButton saveAs = new JButton("Save as Deffault Application Settings");
	
	SimpleDateFormat defsdf = new SimpleDateFormat();
	SimpleDateFormat dmsdf;
	SimpleDateFormat asdf;
	
	Date currentDate = new Date();
	
	public JParametersDateAndTimePanel() {
		super();
		asdf = ApplicationData.APLICATION_DATE_SETTINGS.getFormat();
		dmsdf = ApplicationData.DATAMAP_DATE_SETTINGS.getFormat();
		createPanel();
		createListeners();
		defaultSample.setText(date2string(currentDate, ApplicationData.APLICATION_DATE_SETTINGS.getFormat()));
		init();
	}

	private void createPanel() {
		setLayout(new BorderLayout(10, 10));
		
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		defaultSample.setMaximumSize(new Dimension(350, defaultSample.getPreferredSize().height));
		defaultSample.setPreferredSize(new Dimension(350, defaultSample.getPreferredSize().height));
		defaultSample.setEditable(false);
		
		JPanel customizePanel = new JPanel();
		
		GridBagLayout layout=new GridBagLayout();
		customizePanel.setLayout(layout);
		
		GridBagConstraints con=new GridBagConstraints();
		con.insets = new Insets(2,5,2,5);
		con.fill = GridBagConstraints.BOTH;
		con.anchor = GridBagConstraints.LINE_END;
		
		JPanel paternPanel = new JPanel(new BorderLayout());
		dateFormat = new JComboBox(new String[] {
				"", 
				"M/d/yyyy",
				"M/d/yy",
				"MM/dd/yy" ,
				"MM/dd/yyyy",
				"yyyy/MM/dd",
				"dd/MM/yy",
				"dd/MM/yyyy"
		});
		dateFormat.setMaximumSize(new Dimension(100, dateFormat.getPreferredSize().height));
		dateFormat.setPreferredSize(new Dimension(100, dateFormat.getPreferredSize().height));
		dateSeparator = new JComboBox(new String[] {"None", "/", ".", "-"});
		dateSeparator.setMaximumSize(new Dimension(64, dateSeparator.getPreferredSize().height));
		dateSeparator.setPreferredSize(new Dimension(64, dateSeparator.getPreferredSize().height));
		timeFormat = new JComboBox(new String[] {"", "None", "hh:mm:ss", "hh:mm:ss.SSS"});
		timeFormat.setMaximumSize(new Dimension(100, timeFormat.getPreferredSize().height));
		timeFormat.setPreferredSize(new Dimension(100, timeFormat.getPreferredSize().height));
		timeStandart = new JComboBox(new String[] {"24 hrs", "12 hrs"});
		timeStandart.setMaximumSize(new Dimension(65, timeStandart.getPreferredSize().height));
		timeStandart.setPreferredSize(new Dimension(65, timeStandart.getPreferredSize().height));
		
		ComponentBuilder.makeComponent(new JComponentWithCommentPanel("Date format", dateFormat), layout, con, customizePanel);
		con.gridwidth = GridBagConstraints.REMAINDER;
		ComponentBuilder.makeComponent(new JComponentWithCommentPanel("Date separator", dateSeparator), layout, con, customizePanel);
		con.gridwidth = 1;
		ComponentBuilder.makeComponent(new JComponentWithCommentPanel("Time format", timeFormat), layout, con, customizePanel);
		con.gridwidth = GridBagConstraints.REMAINDER;
		ComponentBuilder.makeComponent(new JComponentWithCommentPanel("Time Standart", timeStandart, 10), layout, con, customizePanel);
		customizeSample.setMaximumSize(new Dimension(350, customizeSample.getPreferredSize().height));
		customizeSample.setPreferredSize(new Dimension(350, customizeSample.getPreferredSize().height));
		customizeSample.setEditable(false);
		
		paternPanel.add(customizePanel, BorderLayout.WEST);
		paternPanel.add(new JPanel(), BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 25, 10));
		buttonPanel.add(reset);
		buttonPanel.add(saveAs);
		
		mainPanel.add(new JComponentWithCommentPanel("Default application date and time settings", defaultSample));
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(paternPanel);
		mainPanel.add(Box.createVerticalStrut(7));
		mainPanel.add(new JComponentWithCommentPanel("Current datamap date and time settings:", customizeSample));
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(buttonPanel);
		
		add(mainPanel, BorderLayout.NORTH);
		add(new JPanel(), BorderLayout.CENTER);
	}
	
	public boolean run() {
		ApplicationData.APLICATION_DATE_SETTINGS.init(createwithApplication,
				asdf.toPattern());
		if (dateSeparator.getSelectedIndex() == 0 && !reseted) {
			if (JOptionPane.showConfirmDialog(ApatarUiMain.MAIN_FRAME, "No Date Separator was seleted. Would you like to save your settings without date separator?",
							"Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
				return false;
			}
		}
		if (createwithApplication) {
			ApplicationData.DATAMAP_DATE_SETTINGS.init(
					dmsdf.toPattern(), 
					dateFormat.getSelectedItem().toString(),
					dateSeparator.getSelectedItem().toString(),
					timeFormat.getSelectedItem().toString(),
					timeStandart.getSelectedItem().toString()
			);
		} else {
			ApplicationData.DATAMAP_DATE_SETTINGS.init(createwithApplication,
					dmsdf.toPattern());
		}
		return true;
	}
	
	private void createSimpleDateFormat() {
		String textDateFormat = dateFormat.getSelectedItem().toString();
		if (dateSeparator.getSelectedIndex() > 0)
			textDateFormat = textDateFormat.replaceAll("/", dateSeparator.getSelectedItem().toString());
		else
			textDateFormat = textDateFormat.replaceAll("/", "");
		int indexTimeFormat = timeFormat.getSelectedIndex();
		int indexTimeStandart = timeStandart.getSelectedIndex();
		String textTimeFormat = "";
		switch (indexTimeFormat) {
		case 2:
			switch (indexTimeStandart) {
			case 1:
				textTimeFormat = " K:m:s a";
				break;
			case 0:
				textTimeFormat = " H:m:s";
				break;
			}
			break;
		case 3:
			switch (indexTimeStandart) {
			case 1:
				textTimeFormat = " K:m:s.S a";
				break;
			case 0:
				textTimeFormat = " H:m:s.S";
				break;
			}
			break;
		}
		dmsdf = new SimpleDateFormat(textDateFormat + textTimeFormat);
		customizeSample.setText(date2string(currentDate, dmsdf));
		
		createwithApplication = true;
	}
	
	private void createListeners() {
		dateFormat.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				createSimpleDateFormat();
			}
		});
		timeFormat.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				createSimpleDateFormat();
			}
		});
		dateSeparator.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				createSimpleDateFormat();
			}
		});
		timeStandart.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				createSimpleDateFormat();
			}
		});
	
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetAllSettings();
				dmsdf = defsdf;
				asdf = defsdf;
				customizeSample.setText(date2string(currentDate, dmsdf));
				
				createwithApplication = false;
				reseted = true;
			}
		});
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dateSeparator.getSelectedIndex() == 0 && !reseted) {
					if (JOptionPane.showConfirmDialog(ApatarUiMain.MAIN_FRAME, "No Date Separator was seleted. Would you like to save your settings without date separator?",
									"Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
						return;
					}
				}
				asdf = new SimpleDateFormat(dmsdf.toPattern());
				defaultSample.setText(date2string(currentDate, asdf));
			}
		});
	}
	
	public String date2string(Date date, SimpleDateFormat format) {
        return format.format(date);
    }
	
	private void init() {
		resetAllSettings();
		if (ApplicationData.DATAMAP_DATE_SETTINGS != null) {
			DateAndTimeSettings dts = ApplicationData.DATAMAP_DATE_SETTINGS;
			createwithApplication = dts.isCreateWithApplication();
			if (createwithApplication) {
				dateFormat.setSelectedItem(dts.getDateFormat());
				dateSeparator.setSelectedItem(dts.getDateSeparator());
				timeFormat.setSelectedItem(dts.getTimeFormat());
				timeStandart.setSelectedItem(dts.getTimeStandart());
			}
		}
		customizeSample.setText(date2string(currentDate, ApplicationData.DATAMAP_DATE_SETTINGS.getFormat()));
	}
	
	private void resetAllSettings() {
		dateFormat.setSelectedIndex(0);
		dateSeparator.setSelectedIndex(0);
		
		timeFormat.setSelectedIndex(0);
		timeStandart.setSelectedIndex(0);
	}
}

