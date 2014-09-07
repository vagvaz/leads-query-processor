/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

package com.apatar.distinct.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.apatar.ui.ComponentBuilder;

public class JAutoLinkOptionsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public static final int CANCEL_OPTION=0;
	public static final int OK_OPTION=1;
	
	JCheckBox nameExactly;
	JCheckBox alias;
	JCheckBox nameContains;
	
	JCheckBox resetOutputs;
	JCheckBox allowMultipleMatching;
	
	JButton ok;
	JButton cancel;
	
	int option;
	
	public JAutoLinkOptionsDialog() throws HeadlessException {
		super();
		createDialog();
		setSize(290,260);
		setModal(true);
	}
	
	private void createDialog() {
		nameExactly = new JCheckBox("Names match exactly");
		alias = new JCheckBox("An input is an alias for on output (or vice versa)");
		nameContains = new JCheckBox("An input contains an output name (or vice versa)");
		
		resetOutputs = new JCheckBox("Reset outputs");
		allowMultipleMatching = new JCheckBox("Allow multiple matching");
		
		GridBagLayout layout=new GridBagLayout();
		getContentPane().setLayout(layout);
		
		GridBagConstraints con=new GridBagConstraints();
		con.insets = new Insets(2,5,2,5);
		con.gridwidth = GridBagConstraints.REMAINDER;
		con.fill = GridBagConstraints.BOTH;
		ComponentBuilder.makeComponent(createLinkPanel(),layout,con,getContentPane());
		ComponentBuilder.makeComponent(createGeneralPanel(),layout,con,getContentPane());
		ComponentBuilder.makeComponent(createButtonPanel(),layout,con,getContentPane());
		
		addListeners();
	}
	
	private JPanel createLinkPanel() {
		JPanel panel = new JPanel(new GridLayout(0,1,5,5));
		panel.setBorder(BorderFactory.createTitledBorder("Linc input columns to output columns where"));
		panel.add(nameExactly);
		//panel.add(alias);
		panel.add(nameContains);
		return panel;
	}
	private JPanel createGeneralPanel() {
		JPanel panel = new JPanel(new GridLayout(0,1,5,5));
		panel.setBorder(BorderFactory.createTitledBorder("Linc input columns to output columns where"));
		panel.add(resetOutputs);
		panel.add(allowMultipleMatching);
		return panel;
	}
	private JPanel createButtonPanel() {
		ok	   = new JButton("Ok");
        cancel = new JButton("Cancel");
        
        ok.setMinimumSize(cancel.getSize());

        JPanel buttonPanel = new JPanel();
        JSeparator separator = new JSeparator();
        Box buttonBox = new Box(BoxLayout.X_AXIS);
        
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(separator, BorderLayout.NORTH);
        
        buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));       
        buttonBox.add(ok);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(cancel);
        
        buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);
        
        return buttonPanel;
	}

	public boolean isCheckAlias() {
		return alias.isSelected();
	}

	public boolean isCheckAllowMultipleMatching() {
		return allowMultipleMatching.isSelected();
	}

	public boolean isCheckNameContains() {
		return nameContains.isSelected();
	}

	public boolean isCheckNameExactly() {
		return nameExactly.isSelected();
	}

	public boolean isCheckResetOutputs() {
		return resetOutputs.isSelected();
	}
	
	private void addListeners() {
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = OK_OPTION;
				setVisible(false);
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
			}
		});
	}
	
	public int showDialog() {
		setVisible(true);
		return option;
	}
}
