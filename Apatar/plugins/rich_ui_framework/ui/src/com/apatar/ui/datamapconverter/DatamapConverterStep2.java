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

import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

/**
 * @author Konstantin Maximchik
 * 
 */
public class DatamapConverterStep2 extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "DATAMAPCONVERTER_PANEL2";
	private final DatamapConverterStep2Panel panel2;

	/**
	 * 
	 */
	public DatamapConverterStep2() {
		super();
		panel2 = new DatamapConverterStep2Panel();
		setPanelComponent(panel2);
	}

	@Override
	public Object getNextPanelDescriptor() {
		return DatamapConverterStep3.IDENTIFIER;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return DatamapConverterStep1.IDENTIFIER;
	}

	@Override
	public void aboutToDisplayPanel() {
		getWizard().setTitle("Step 2 of 4. Conversion.");
		getWizard().setTitleComment("Converting the datamap");
		getWizard().setAdditionalComment(
				"Please wait while process will be completed.");
	}

	@Override
	public void displayingPanel() {
		DataConverterWizard wiz = (DataConverterWizard) getWizard();
		wiz.getConverter().ConvertDatamap();
		panel2.getText().setText(
				"Conversion completed. \n"
						+ "To save it please click Next button");
	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			return CHANGE_PANEL;
		}
		return CHANGE_PANEL;
	}
}
