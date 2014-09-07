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

package com.apatar.ui.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class JCommentPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	JLabel title;
	JLabel comment;
	JLabel picture;
	
	public JCommentPanel() {
		super();
		setBackground(new Color(255,255,255));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500,60));
		content();
	}

	public void content() {
		title = new JLabel("");
		//Font fontTitle = title.getFont();
		//title.setFont(fontTitle.deriveFont((float)20.0));
		
		//JLabel lblTitle = new JLabel( this.transformFunc.getTitle() );
		title.setBorder(new EmptyBorder(15, 10, 10, 10));
		title.setFont(new Font("", Font.BOLD, 16));
		
		comment = new JLabel("");
		//Font fontComment = comment.getFont();
		//comment.setFont(fontComment.deriveFont((float)12.0));
		
		comment.setBorder(new EmptyBorder(5, 10, 10, 5));
		comment.setFont(new Font("", Font.ITALIC, 12));
		
		JPanel titlePanel = new JPanel(new GridLayout(2,1,10,10));
		
		titlePanel.add(title);
		titlePanel.add(comment);
		titlePanel.setBackground(new Color(255,255,255));
		
		this.add(titlePanel,BorderLayout.CENTER);
		this.add(new JLabel(),BorderLayout.NORTH);
	}
	
	public void setTitle(String text) {
		title.setText(text);
	}
	
	public void setComment(String text) {
		comment.setText(text);
	}
}
