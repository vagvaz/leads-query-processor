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

package com.apatar.ldap;

import java.util.Properties;

import org.jdom.Element;

import com.apatar.core.JdbcParams;

public class LDAPParams extends JdbcParams {
	
	String baseDN;
	
	public LDAPParams() {
		super();
		init();
	}
	
	protected void init() {
		super.setJdbcDriver("com.novell.sql.LDAPDriver");
		super.setPort(389);
	}

	public String getConnUrl() {
		return "jdbc:ldap://" + host +
        ";user=" + userName + 
        ";password=" + password.getValue() +
        ";baseDN=" + baseDN  + 
        ";useCleartext=true";
	}

	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}
	
	public void FillProperties(Properties prp)
	{
	}
	
	public Element saveToElement() {
		Element el = super.saveToElement();
		Element baseDNElement = new Element("baseDN");
		baseDNElement.setText(baseDN);
		el.addContent(baseDNElement);
		return el;
	}
	
	public void initFromElement(Element node) {
		super.initFromElement(node);
		baseDN = node.getChildText("baseDN");
	}

	public Properties getProperties() {
		return null;
	}

	public void setProperties(Properties property) {}
	
}
