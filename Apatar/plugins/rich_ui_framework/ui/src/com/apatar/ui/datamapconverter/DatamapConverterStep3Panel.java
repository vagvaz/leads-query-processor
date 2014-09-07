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

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @author Konstantin Maximchik
 * 
 */
public class DatamapConverterStep3Panel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1913534662097891697L;
	private JRadioButton doOverwrite = new JRadioButton("Overwrite old datamap");
	private JRadioButton doSaveAsNew = new JRadioButton("Save as new datamap");

	/**
	 * 
	 */
	public DatamapConverterStep3Panel() {
		super();
		add(doOverwrite);
		add(doSaveAsNew);
		add(Box.createVerticalGlue());

		ButtonGroup group = new ButtonGroup();
		group.add(doOverwrite);
		group.add(doSaveAsNew);

		doOverwrite.setSelected(true);
	}

	/**
	 * @return the doOverwrite
	 */
	public JRadioButton getDoOverwrite() {
		return doOverwrite;
	}

	/**
	 * @param doOverwrite
	 *            the doOverwrite to set
	 */
	public void setDoOverwrite(JRadioButton doOverwrite) {
		this.doOverwrite = doOverwrite;
	}

	/**
	 * @return the doSaveAsNew
	 */
	public JRadioButton getDoSaveAsNew() {
		return doSaveAsNew;
	}

	/**
	 * @param doSaveAsNew
	 *            the doSaveAsNew to set
	 */
	public void setDoSaveAsNew(JRadioButton doSaveAsNew) {
		this.doSaveAsNew = doSaveAsNew;
	}

}
