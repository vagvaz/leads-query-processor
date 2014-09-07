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

import com.apatar.ui.wizard.WizardPanelDescriptor;

/**
 * @author Konstantin Maximchik
 * 
 */
public class DatamapConverterStep4 extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "DATAMAPCONVERTER_PANEL4";
	private final DatamapConverterStep4Panel panel4;

	/**
	 * 
	 */
	public DatamapConverterStep4() {
		super();
		panel4 = new DatamapConverterStep4Panel();
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel4);
	}

	@Override
	public Object getNextPanelDescriptor() {
		return WizardPanelDescriptor.FINISH;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return DatamapConverterStep3.IDENTIFIER;
	}

	@Override
	public void aboutToDisplayPanel() {
		getWizard().setTitle("Step 4 of 4. Finish.");
		getWizard().setTitleComment("Conversion completed");
		getWizard().setAdditionalComment("Thank you for using Apatar!");
	}

}
