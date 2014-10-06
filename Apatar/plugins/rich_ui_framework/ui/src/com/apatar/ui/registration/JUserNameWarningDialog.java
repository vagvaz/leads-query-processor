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
 


package com.apatar.ui.registration;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.apatar.ui.ComponentBuilder;
import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.UiUtils;

public class JUserNameWarningDialog extends JDialog {

	JCheckBox boxShow = new JCheckBox("Don't show this message again");
	JButton close = new JButton("Close");
	
	public JUserNameWarningDialog(Dialog owner, String email) throws HeadlessException {
		super(owner, true);
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		setSize(450, 150);
		setLocationRelativeTo(owner);
		
		setTitle("UserName Warning");
		
		GridBagConstraints constraintValue = new GridBagConstraints();
		Insets insets=new Insets(3,5,5,5);
		constraintValue.anchor=GridBagConstraints.WEST;
    	constraintValue.gridwidth=GridBagConstraints.REMAINDER;
    	constraintValue.insets=insets;
    	constraintValue.fill=GridBagConstraints.BOTH;
    	constraintValue.weightx=1.0;
		
		JTextArea comment = new JTextArea("Your registered user name is '" + email + 
				"'. It appears at public pages on Apatar websites. " +
				"To change your user name, please, visit");
		
		JLabel apatarLinkLabel = new JLabel("<html><a href='http://www.apatarforge.org/profile/index.php'>http://www.apatarforge.org/profile/index.php</a></html>");
		
		apatarLinkLabel.addMouseListener( new MouseHyperLinkEvent() );
		apatarLinkLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		
    	comment.setEditable(false);
    	comment.setOpaque(false);
    	comment.setLineWrap(true);
    	comment.setWrapStyleWord(true);
    	comment.setFont(UiUtils.NORMAL_SIZE_12_FONT);
    	comment.setBorder(null);
    	
    	ComponentBuilder.makeComponent(comment, layout, constraintValue, this);
    	ComponentBuilder.makeComponent(apatarLinkLabel, layout, constraintValue, this);
    	ComponentBuilder.makeComponent(boxShow, layout, constraintValue, this);
    	constraintValue.fill=GridBagConstraints.NONE;
    	constraintValue.anchor=GridBagConstraints.CENTER;
    	ComponentBuilder.makeComponent(close, layout, constraintValue, this);
    	
    	close.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
    		
    	});
	}
	
}

