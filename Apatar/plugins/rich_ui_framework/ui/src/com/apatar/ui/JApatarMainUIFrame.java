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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;

import com.apatar.core.ApplicationData;

public class JApatarMainUIFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	//public static String FRAME_TITLE = "%s " + ApplicationData.VERSION;

	public static String FRAME_TITLE = "%s " + "LEADS EU - Graphical User Interface for Query Execution";

	
	public JApatarMainUIFrame(String str) {
		super(str);
		setIconImage(UiUtils.MAIN_ICON.getImage());
		addWindowListener(new ExitListener());
	}

	private class ExitListener extends WindowAdapter implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				ApatarUiMain.exit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		@Override
		public void windowClosing(WindowEvent e) {
			try {
				ApatarUiMain.exit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {
		if (ApplicationData.SILENT_RUN) {
			System.out
					.println("Command line parameter `silent_run` is set. Do not open window JApatarMainUIFrame.");
			super.setVisible(false);
		} else {
			super.setVisible(b);
		}
	}
}
