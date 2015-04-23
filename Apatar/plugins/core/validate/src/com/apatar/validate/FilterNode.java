/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

 */

package com.apatar.validate;

import javax.swing.ImageIcon;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.FunctionsPlugin;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.JSubProjectDialog;

public class FilterNode extends ValidateNode {

	public FilterNode() {
		super();
		title = "Filter";
		outputConnectionList
				.remove(getConnPoint(ValidateNode.OUTPUT_CONN_POINT_FALSE));
	}

	@Override
	public ImageIcon getIcon() {
		return ValidateUtils.FILTER_ICON;
	}

	@Override
	public boolean realEdit(AbstractApatarActions actions) {
		return JSubProjectDialog.showDialog(ApatarUiMain.MAIN_FRAME,
				"Filtration", this, new String[] { INPUT_CONN_POINT },
				FunctionsPlugin.getNodesFunctionFilter(), false,
				"help.operation.filter") == JSubProjectDialog.OK_OPTION;

	}

}
