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

package com.apatar.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class JPaneToolbar extends JToolBar {
	private static final long serialVersionUID = 1L;

	public MouseListener mouseListener = new MouseListener(){

		public void mouseClicked(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
		}

		public void mouseEntered(MouseEvent e) {
			((JButton)e.getComponent()).setBorderPainted(true);
		}

		public void mouseExited(MouseEvent e) {
			((JButton)e.getComponent()).setBorderPainted(false);
		}
	};

	public JPaneToolbar() {
		super();
		setFloatable(false);
		setPreferredSize(new Dimension(0, 35));
	}
	
	
	
	public class ButtonActionListener implements ActionListener
	{
		Object object;
		Method method;
		public ButtonActionListener(Object object, Method method)
		{
			this.object = object;
			this.method = method;
		}
		
		// call the method
		public void actionPerformed(ActionEvent e) {
			try{
			method.invoke(object, new Object[]{});
			}catch(Exception ex)
			{ex.printStackTrace(); }
		}
	};
	
	// method should have void parameters
	public void AddButton(ImageIcon icon, boolean border, Object object, Method method)
	{
		JButton btn = new JButton(icon);
		btn.addActionListener(new ButtonActionListener(object, method));
		btn.addMouseListener(mouseListener);
		btn.setBorderPainted(border);
		this.add(btn);
	}
	
	// method should have void parameters
	public void AddButton(String text, boolean border, Object object, Method method)
	{
		JButton btn = new JButton(text);
		btn.addActionListener(new ButtonActionListener(object, method));
		btn.addMouseListener(mouseListener);
		btn.setBorderPainted(border);
		this.add(btn);
	}

}
