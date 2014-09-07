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
 


package com.apatar.cdyne.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

public class JDelayForClearDialog extends JDialog {
	JRadioButton minute30 = new JRadioButton("30 minutes");
	JRadioButton hour1 = new JRadioButton("1 hour");
	JRadioButton hour6 = new JRadioButton("6 hour");
	JRadioButton hour12 = new JRadioButton("12 hour");
	JRadioButton hour24 = new JRadioButton("24 hour");
	JRadioButton never = new JRadioButton("Never");
	
	JButton bOk = new JButton("Ok");
	JButton bCancel = new JButton("Cancel");
	
	public static int CANCEL_OPTION = 0; 
	public static int OK_OPTION = 1;
	
	public int option = CANCEL_OPTION;
	
	String message = "   Clear temporary email database every:";
	JLabel messageLabel = new JLabel();
	
	public JDelayForClearDialog(Dialog arg0, String title, boolean arg1) throws HeadlessException {
		super(arg0, title, arg1);
		setLayout(new BorderLayout());
		
		setResizable(false);
		
		JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
		selectPanel.add(messageLabel);
		selectPanel.add(minute30);
		selectPanel.add(hour1);
		selectPanel.add(hour6);
		selectPanel.add(hour12);
		selectPanel.add(hour24);
		selectPanel.add(never);
		
		add(selectPanel, BorderLayout.CENTER);
		
		ButtonGroup group = new ButtonGroup();
		group.add(minute30);
		group.add(hour1);
		group.add(hour6);
		group.add(hour12);
		group.add(hour24);
		group.add(never);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(bOk);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(bCancel);
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(215, 210);
		
		hour24.setSelected(true);
		
		bOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				option = OK_OPTION;
				setVisible(false);
			}
			
		});
		bCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
			}
			
		});
	}
	
	public int getDalay() {
		if (minute30.isSelected())
			return 30 * 60000;
		if (hour1.isSelected())
			return 60 * 60000;
		if (hour6.isSelected())
			return 360 * 60000;
		if (hour12.isSelected())
			return 720 * 60000;
		if (hour24.isSelected())
			return 1440 * 60000;
		return 0;
	}

	public void setMessage(String message) {
		this.messageLabel.setText(message);
	}
	
}

