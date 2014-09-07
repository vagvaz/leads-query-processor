/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
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

package com.apatar.functions;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.java.plugin.standard.StandardPluginClassLoader;
import org.jdom.Element;

import com.apatar.core.AbstractApatarActions;
import com.apatar.core.AbstractNode;
import com.apatar.core.ApatarFunction;
import com.apatar.core.ConnectionPoint;
import com.apatar.core.FunctionInformation;
import com.apatar.core.FunctionsPlugin;
import com.apatar.core.NonOperationalNode;

public class FunctionNode extends NonOperationalNode {

	public static final String INPUT_CONN_POINT = "Input";
	public static final String OUTPUT_CONN_POINT = "Output";

	protected int countOutput;
	protected int countInput;

	String[] inputComment;
	String[] outputComment;

	protected ApatarFunction func = null;
	protected String nameClass;

	public FunctionNode(String classFunc, FunctionInformation fi) {
		super();
		countOutput = fi.getCountOutput();
		countInput = fi.getCountInput();
		title = fi.getDisplayName();
		nameClass = classFunc;
		inputComment = fi.getInputComments();
		outputComment = fi.getOutputComments();

		createFunction();
	}

	@Override
	public ImageIcon getIcon() {
		return FunctionUtils.SMALL_FUNCTION_ICON;
	}

	public boolean realEdit(AbstractApatarActions actions) {
		actions.configureFunctionAction(func);
		return true;
	}

	@Override
	public Element saveToElement() {

		Element saved = super.saveToElement();

		saved.setAttribute("countOutput", String.valueOf(countOutput));
		saved.setAttribute("countInput", String.valueOf(countInput));
		saved.setAttribute("classFunction", nameClass);
		saved.addContent(func.saveToElement());

		return saved;
	}

	@Override
	public void initFromElement(Element e) {
		nameClass = e.getAttributeValue("classFunction");
		countOutput = Integer.parseInt(e.getAttributeValue("countOutput"));
		countInput = Integer.parseInt(e.getAttributeValue("countInput"));
		createFunction();
		super.initFromElementWithoutConnectionPoint(e);

		// load validation function
		func.initFromElement(e.getChild(func.getClass().getName()));
	}

	protected void LoadFunc() {
		// fi name class is not specified
		if (nameClass == null) {
			return;
		}
		try {
			StandardPluginClassLoader transfunctionClass = (StandardPluginClassLoader) FunctionsPlugin.functionLoaders
					.get(nameClass);
			func = (ApatarFunction) transfunctionClass.loadClass(nameClass)
					.newInstance();
		} catch (Exception es) {
			es.printStackTrace();
		}
	}

	public String getNameClassTransformation() {
		return nameClass;
	}

	@Override
	public int getInlinePosition() {
		ConnectionPoint outputCP = getConnPoint(FunctionNode.OUTPUT_CONN_POINT + 1);
		if (outputCP == null || outputCP.getCountConnection() <= 0) {
			return AbstractNode.LAST_POSITION;
		}
		return AbstractNode.MIDDLE_POSITION;
	}

	public ApatarFunction getValidateFunc() {
		return func;
	}

	// the main transformation
	public void Transform() throws SocketException {
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
		setResult(func.execute(values));
	}

	private void createFunction() {
		LoadFunc();

		for (int i = 1; i <= countOutput; i++) {
			String id = OUTPUT_CONN_POINT + i;
			ConnectionPoint cpOut = new ConnectionPoint(id, false, this,
					outputComment != null ? outputComment[i - 1] : null, i);
			cpOut.setIsMultipleConnection(false);
			outputConnectionList.put(cpOut, null);
		}
		for (int i = 1; i <= countInput; i++) {
			String id = INPUT_CONN_POINT + i;
			ConnectionPoint cpIn = new ConnectionPoint(id, true, this,
					inputComment != null ? inputComment[i - 1] : null, i);
			cpIn.setIsMultipleConnection(false);
			inputConnectionList.add(cpIn);
		}
	}

}
