/*TODO recorded refactoring
 * введён класс ApatarActions реализующий события UI происходящие в процессе
 * настройки нод.
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

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import propertysheet.JPropertySheetDialog;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.AbstractNode;
import com.apatar.core.ApatarFunction;
import com.apatar.core.NonOperationalNode;
import com.apatar.ui.wizard.Wizard;

/**
 * @author Konstantin Maximchik
 * 
 */
public class ApatarActions extends AbstractApatarActions {

	protected Window win = null;

	public ApatarActions(AbstractNode node) {
		super(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.apatar.core.AbstractApatarActions#dialogAction()
	 */
	@Override
	public void dialogAction(String message) {
		JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, message);
	}

	public boolean customAction() {
		return false;
	}

	@Override
	public void beforeStart() {

	}

	@Override
	public boolean callRegistrationMethod() {
		return false;
	}

	public void afterStart() {

	}

	public void afterStop() {

	}

	public void beforeStop() {

	}

	public boolean customDatabaseNodeAction() {
		try {

			Wizard wizard = new Wizard(ApatarUiMain.MAIN_FRAME);

			wizard.setTitle(node.getTitle() + " Property");

			AbstractDataBaseNode node = (AbstractDataBaseNode) this.node;
			node.createDatabaseParam(wizard);

			return wizard.getReturnCode() == Wizard.FINISH_RETURN_CODE;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean configureFunctionAction(ApatarFunction func) {
		if (win != null && func != null && func.isEditable()) {
			JPropertySheetDialog res = (win instanceof JDialog) ? new JPropertySheetDialog(
					(JDialog) win, func)
					: new JPropertySheetDialog((JFrame) win, func);
			res.setVisible(true);
		}
		return true;
	}

	@Override
	public boolean configureConstantFunctionAction(ApatarFunction func) {
		if (func != null && func.isEditable()) {
			try {
				JPropertySheetDialog res = (win instanceof JDialog) ? new JPropertySheetDialog(
						(JDialog) win, func)
						: new JPropertySheetDialog((JFrame) win, func);
				res.setVisible(true);
				NonOperationalNode node = (NonOperationalNode) this.node;
				node.setResult(func.execute(null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * @return the win
	 */
	public Window getWin() {
		return win;
	}

	/**
	 * @param win
	 *            the win to set
	 */
	public void setWin(Window win) {
		this.win = win;
	}
}
