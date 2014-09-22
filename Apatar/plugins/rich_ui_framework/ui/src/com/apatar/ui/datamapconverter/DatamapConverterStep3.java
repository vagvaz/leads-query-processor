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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.apatar.core.CoreUtils;
import com.apatar.ui.ApatarFileFilter;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

/**
 * @author Konstantin Maximchik
 * 
 */
public class DatamapConverterStep3 extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "DATAMAPCONVERTER_PANEL3";
	private final DatamapConverterStep3Panel panel3;

	/**
	 * 
	 */
	public DatamapConverterStep3() {
		super();
		panel3 = new DatamapConverterStep3Panel();
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel3);
	}

	@Override
	public Object getNextPanelDescriptor() {
		return DatamapConverterStep4.IDENTIFIER;
	}

	@Override
	public Object getBackPanelDescriptor() {
		return DatamapConverterStep2.IDENTIFIER;
	}

	@Override
	public void aboutToDisplayPanel() {
		getWizard().setTitle("Step 3 of 4. Saving converted datamap.");
		getWizard().setTitleComment("It's time to save you converted datamap.");
		getWizard().setAdditionalComment("Select way how to save datamap.");
	}

	@Override
	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			try {
				if (panel3.getDoOverwrite().isSelected()) {
					doOverwrite();
					getWiz().setPathToConvertedDatamap(
							getWiz().getPathToOriginalDatamap());
				} else {
					if (!doSaveAs()) {
						return LEAVE_CURRENT_PANEL;
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Error saving datamap", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			return CHANGE_PANEL;
		}
		return CHANGE_PANEL;
	}

	private DataConverterWizard getWiz() {
		return (DataConverterWizard) getWizard();
	}

	private void doOverwrite() throws IOException {
		CoreUtils.saveFileFromString(new File(getWiz()
				.getPathToOriginalDatamap()), null, getWiz().getConverter()
				.getConvertedContent());
	}

	private boolean doSaveAs() throws IOException {
		JFileChooser fileChooser = new JFileChooser(System
				.getProperty("user.home"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		File original = new File(getWiz().getPathToOriginalDatamap());

		fileChooser.setCurrentDirectory(new File(original.getParent()));

		fileChooser.setFileFilter(new ApatarFileFilter());
		fileChooser.setSelectedFile(original);
		int returnValue = fileChooser.showSaveDialog(ApatarUiMain.MAIN_FRAME);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			getWiz().setPathToConvertedDatamap(
					fileChooser.getSelectedFile().toString());
			CoreUtils.saveFileFromString(fileChooser.getSelectedFile(), null,
					getWiz().getConverter().getConvertedContent());
			return true;
		} else {
			return false;
		}
	}
}
