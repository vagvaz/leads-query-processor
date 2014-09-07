/*
 _______________________
 Apatar Open Source Data Integration
 Copyright (C) 2005-2008, Apatar, Inc.
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
package com.apatar.ui.datamapconverter;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Konstantin Maximchik
 * 
 */
public class DatamapConverterStep1Panel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3958477525682444569L;

	private final JTextArea text;

	/**
	 * 
	 */
	public DatamapConverterStep1Panel() {
		super();
		text = new JTextArea(
				"Your datamap version is older than your application version.\n"
						+ "To open it you can convert the datamap to newer format\n"
						+ "Click Next button", 5, 10);
		text.setEditable(false);
		text.setBackground(getBackground());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(text);
	}

}
