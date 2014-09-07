/*TODO recorded refactoring
 * добавлен класс ProcessingProgressActions реализующий методы класса AbstractProcessingProgressActions
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
package com.apatar.ui;

import com.apatar.core.AbstractNode;
import com.apatar.core.AbstractProcessingProgressActions;
import com.apatar.core.ApplicationData;
import com.apatar.core.OperationalNode;

/**
 * @author Konstantin Maximchik
 * 
 */
public class ProcessingProgressActions extends
		AbstractProcessingProgressActions {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractProcessingProgressActions#beforeStart()
	 */
	@Override
	public void beforeStart(AbstractNode debugNode) {
		JProgressDialog dlg = new JProgressDialog((OperationalNode) debugNode);
		ApplicationData.ProcessingProgress = dlg;
		dlg.setVisible(true);
	}

	public void afterStart() {

	}

	public void afterStop() {

	}

	public void beforeStart() {

	}

	public void beforeStop() {

	}

	public void dialogAction() {
	}

	public boolean customAction() {
		return false;
	}

	public boolean customDatabaseNodeAction() {
		return false;
	}

	public void dialogAction(String message) {

	}

}
