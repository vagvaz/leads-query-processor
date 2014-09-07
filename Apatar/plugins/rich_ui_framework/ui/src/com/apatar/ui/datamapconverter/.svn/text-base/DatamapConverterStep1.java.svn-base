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

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.apatar.core.CoreUtils;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

/**
 * @author Konstantin Maximchik
 * 
 */
public class DatamapConverterStep1 extends WizardPanelDescriptor {
	public static final String IDENTIFIER = "DATAMAPCONVERTER_PANEL1";
	private final DatamapConverterStep1Panel panel1;

	/**
	 * 
	 */
	public DatamapConverterStep1() {
		super();
		panel1 = new DatamapConverterStep1Panel();
		setPanelComponent(panel1);
	}

	@Override
	public Object getNextPanelDescriptor() {
		return DatamapConverterStep2.IDENTIFIER;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return null;
	}

	@Override
	public void aboutToDisplayPanel() {
		getWizard().setTitle("Step 1 of 4. Start.");
		getWizard().setTitleComment("Start conversion wizard");
		getWizard().setAdditionalComment(
				"Decide whether convert datamap or not");
	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			DataConverterWizard wiz = (DataConverterWizard) getWizard();
			try {
				wiz.getConverter().setOriginalContent(
						CoreUtils.loadFileAsString(new File(wiz
								.getPathToOriginalDatamap()), null));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Error opening datamap", JOptionPane.ERROR_MESSAGE);
				return LEAVE_CURRENT_PANEL;
			}
			return CHANGE_PANEL;
		}
		return CHANGE_PANEL;
	}
}
