/*TODO recorded refactoring
 * введён абстрактный класс AbstractReadWriteXMLDataActions от
 * которого должны наследоваться классы UI отвечающие за обработку
 * ситуаций, когда необходима интерактивность при загрузке/сохранении
 * проекта
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
package com.apatar.core;

import javax.swing.JOptionPane;

import com.apatar.ui.ApatarUiMain;

/**
 * @author Konstantin Maximchik
 * 
 */
public abstract class AbstractReadWriteXMLDataActions {

	protected abstract void loadDateAndTimeSettings(String fileSrc)
			throws Throwable;

	public abstract boolean doReplaceDateTimeSettings();

	public abstract boolean doReplaceDatamapFile();

	/**
	 * shows JOptionPane.showConfirmDialog
	 * 
	 * @param message -
	 *            message text
	 * @param title -
	 *            dialog title
	 * @param messageType -
	 *            one of ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
	 *            QUESTION_MESSAGE
	 * @return true or false
	 */
	public static boolean showConfirmationDialog(String message, String title,
			int messageType) {
		int option = JOptionPane.showConfirmDialog(ApatarUiMain.MAIN_FRAME,
				message, title, JOptionPane.YES_NO_OPTION, messageType);
		if (option == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}
}
