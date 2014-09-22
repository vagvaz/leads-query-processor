/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013

 

    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.

 

    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.

 

    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

 */

package com.apatar.strikeiron;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.apatar.core.ApatarFunction;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.DataBaseTools;
import com.apatar.core.FunctionInformation;
import com.apatar.core.NonOperationalNode;
import com.apatar.functions.FunctionNode;

public class StrikeironFunctionNode extends FunctionNode {

	String password;
	String userName;
	Strikeiron node;

	public StrikeironFunctionNode(String classFunc, FunctionInformation fi) {
		super(classFunc, fi);
	}

	@Override
	protected void LoadFunc() {
		// fi name class is not specified
		if (nameClass == null) {
			return;
		}

		ClassLoader transfunctionClass = FunctionLoder.functionLoaders
				.get(nameClass);
		try {
			func = (ApatarFunction) transfunctionClass.loadClass(nameClass)
					.newInstance();
		} catch (Exception es) {
			es.printStackTrace();
		}
	}

	// the main transformation
	@Override
	public void Transform() throws SocketException {
		DataBaseTools.completeTransfer();
		// read input values from connectors
		// put them in list and pass to the transform function
		List<Object> values = new ArrayList<Object>();
		for (ConnectionPoint cpt : getIncomingConnPoints()) {
			if (cpt.getConnectors().size() > 0) {
				NonOperationalNode non = (NonOperationalNode) cpt
						.getConnectors().get(0).getBegin().getNode();
				values.add(non.getResult());
			} else {
				values.add(null);
			}
		}
		setResult(((StrikeIronFunction) func).execute(values, userName,
				password, node));
		DataBaseTools.completeTransfer();

	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setOwnerNode(Strikeiron node) {
		this.node = node;
	}

}
