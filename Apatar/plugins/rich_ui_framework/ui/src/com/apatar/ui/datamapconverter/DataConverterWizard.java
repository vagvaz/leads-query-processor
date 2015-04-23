/*TODO recorded refactoring
 * пакет com.apatar.ui.datamapconverter реализует визард конвертации датамапов из версий старше 1.2 в текущую
 * *********************
 */

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

import java.awt.Frame;

import com.apatar.core.DatamapConverter;
import com.apatar.ui.wizard.Wizard;
import com.apatar.ui.wizard.WizardPanelDescriptor;

/**
 * @author Konstantin Maximchik
 * 
 */
public class DataConverterWizard extends Wizard {

	private String pathToOriginalDatamap;
	private String pathToConvertedDatamap = "";
	private final DatamapConverter converter = new DatamapConverter();

	/**
	 * @return the pathToConvertedDatamap
	 */
	public String getPathToConvertedDatamap() {
		return pathToConvertedDatamap;
	}

	/**
	 * @param pathToConvertedDatamap
	 *            the pathToConvertedDatamap to set
	 */
	public void setPathToConvertedDatamap(String pathToConvertedDatamap) {
		this.pathToConvertedDatamap = pathToConvertedDatamap;
	}

	/**
	 * @param owner
	 */
	public DataConverterWizard(Frame owner, String pathToDatamap) {
		super(owner);
		pathToOriginalDatamap = pathToDatamap;
		WizardPanelDescriptor descriptor1 = new DatamapConverterStep1();
		registerWizardPanel(DatamapConverterStep1.IDENTIFIER, descriptor1);

		WizardPanelDescriptor descriptor2 = new DatamapConverterStep2();
		registerWizardPanel(DatamapConverterStep2.IDENTIFIER, descriptor2);

		WizardPanelDescriptor descriptor3 = new DatamapConverterStep3();
		registerWizardPanel(DatamapConverterStep3.IDENTIFIER, descriptor3);

		WizardPanelDescriptor descriptor4 = new DatamapConverterStep4();
		registerWizardPanel(DatamapConverterStep4.IDENTIFIER, descriptor4);

		setCurrentPanel(DatamapConverterStep1.IDENTIFIER,
				Wizard.NEXT_BUTTON_ACTION_COMMAND);
	}

	public boolean StartWizard() {
		int res = showModalDialog();
		if (Wizard.FINISH_RETURN_CODE == res) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the pathToOriginalDatamap
	 */
	public String getPathToOriginalDatamap() {
		return pathToOriginalDatamap;
	}

	/**
	 * @param pathToOriginalDatamap
	 *            the pathToOriginalDatamap to set
	 */
	public void setPathToOriginalDatamap(String pathToOriginalDatamap) {
		this.pathToOriginalDatamap = pathToOriginalDatamap;
	}

	/**
	 * @return the converter
	 */
	public DatamapConverter getConverter() {
		return converter;
	}
}
