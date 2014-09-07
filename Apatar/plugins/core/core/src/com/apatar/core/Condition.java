/*TODO recorded refactoring
 * в класс Condition добавлено наследование от интерфейса IPersistent
 **********************
 */

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

package com.apatar.core;

import org.jdom.Element;

public class Condition implements IPersistent {
	String column1;
	String column2;

	public Condition() {
		super();
	}

	public Condition(String column1, String column2) {
		super();
		this.column1 = column1;
		this.column2 = column2;
	}

	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public String getColumn2() {
		return column2;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}

	public Element saveToElement() {
		Element el = new Element("Condition");
		el.setAttribute("column1", column1);
		el.setAttribute("column2", column2);
		return el;
	}

	public void initFromElement(Element element) {
		column1 = element.getAttributeValue("column1");
		column2 = element.getAttributeValue("column2");
	}
}
