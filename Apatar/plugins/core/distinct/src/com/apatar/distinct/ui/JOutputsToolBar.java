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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class JOutputsToolBar extends JToolBar {
	private static final long serialVersionUID = 1L;

	JButton edit;
	JButton reset;
	
	public JOutputsToolBar() {
		super(JToolBar.VERTICAL);
		setFloatable(false);
		setLayout(new GridLayout(2,1));
		edit	= new JButton("Edit");
		reset	= new JButton("Reset");
		
		edit.setBorderPainted(false);
		edit.addMouseListener(new ToolbarMouseListener());
		edit.setHorizontalAlignment(JButton.LEFT);
		edit.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		edit.setIcon(new ImageIcon(this.getClass().getResource("edit.png")));
		 
//		edit.setIcon( TransformUtils.EDIT );
		reset.setBorderPainted(false);
		reset.addMouseListener(new ToolbarMouseListener());
		reset.setHorizontalAlignment(JButton.LEFT);
		reset.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		edit.setIcon(new ImageIcon(this.getClass().getResource("reset.png")));
//		reset.setIcon( TransformUtils.RESET );
		
		add(edit);
		add(reset);
		
		addListeners();
	}
	
	private void addListeners() {
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JAutoLinkOptionsDialog dlg = new JAutoLinkOptionsDialog();
				if (dlg.showDialog()==JAutoLinkOptionsDialog.OK_OPTION) {
					autoLinkingNodes();
				}
			}
			
		});
	}
	
	private void autoLinkingNodes() {
		
	}
	
	public class ToolbarMouseListener implements MouseListener {

		public ToolbarMouseListener() {
		}
		
		public void mouseClicked(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
			e.getComponent().setBackground(Color.ORANGE);
			((JButton)e.getComponent()).setBorderPainted(true);
		}

		public void mouseExited(MouseEvent e) {
			((JButton)e.getComponent()).setBorderPainted(false);
			e.getComponent().setBackground(getBackground());
		}

	}
	
	
}
