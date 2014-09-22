/*TODO recorded refactoring
 * введён класс ReadWriteXMLDataUi вызывающий методы класса ReadWriteXMLData
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
package com.apatar.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.apatar.core.AbstractReadWriteXMLDataActions;
import com.apatar.core.Project;
import com.apatar.core.ReadWriteXMLData;
import com.apatar.ui.datamapconverter.DataConverterWizard;

/**
 * @author Konstantin Maximchik
 * 
 */
public class ReadWriteXMLDataUi extends AbstractReadWriteXMLDataActions {

	private final ReadWriteXMLData rwXMLdata;

	public ReadWriteXMLDataUi() {
		super();
		rwXMLdata = new ReadWriteXMLData();
	}

	@Override
	public boolean doReplaceDateTimeSettings() {
		return AbstractReadWriteXMLDataActions
				.showConfirmationDialog(
						"Current DataMap date and time settings differ from your default application configuration.\nIf you wish to use the settings of the DataMap, click Yes.\nIf you wish to use your default settings within this DataMap, click No.\n(In this case, make sure to change the date and time format in  functions within the DataMap.",
						"Warning!", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	protected void loadDateAndTimeSettings(String fileSrc) throws Exception {
		rwXMLdata.loadDateAndTimeSettings(fileSrc, this);
	}

	/*
	 * TODO recorded refactoring в класс ReadWriteXMLDataUi в метод readXMLData
	 * добавлен вызов метода проверки версии датамапы. Если датамапа создавалась
	 * в версии старше чем 1.2 то запускается визард, предлагающий
	 * конвертировать датамапу в текущую версию *********************
	 */
	public String readXMLData(String fileSrc, Project project) throws Exception {
		if (rwXMLdata.isDatamapOlderThan12(fileSrc)) {
			DataConverterWizard wiz = new DataConverterWizard(
					ApatarUiMain.MAIN_FRAME, fileSrc);

			if (!wiz.StartWizard()) {
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
						"Wrong datamap format", "Error opening datamap",
						JOptionPane.ERROR_MESSAGE);
				return null;
			} else {
				fileSrc = wiz.getPathToConvertedDatamap();
			}
		}
		loadDateAndTimeSettings(fileSrc);
		return rwXMLdata.readXMLData(fileSrc, project);
	}

	@Override
	public boolean doReplaceDatamapFile() {
		return AbstractReadWriteXMLDataActions.showConfirmationDialog(
				"Do you want to replace the file?", "Warning!",
				JOptionPane.WARNING_MESSAGE);
	}

	public File writeXMLData(String fileSrc, Project project,
			boolean autoReplace) throws IOException {
		return rwXMLdata.writeXMLData(fileSrc, project, autoReplace, this);
	}
}
