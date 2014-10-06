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

package com.apatar.openedge;

import java.util.Properties;

import org.jdom.Element;

import com.apatar.core.JdbcParams;

	public class OpenEdgeJdbcParams extends JdbcParams {
		
		protected String version; 
		
		public OpenEdgeJdbcParams() {
			super();
			init();
		}

		public void init() {
			// default to version 10
			super.setJdbcDriver("com.ddtek.jdbc.openedge.OpenEdgeDriver");
			super.setDriverName("openedge");
			version = "10.1B";
		}
		
		public String getConnUrl() {
			if (version.matches("9(\\.)?\\d\\D")) {
				return "jdbc:JdbcProgress:T:" + getHost() + ":" + getPort() + ":" + getDbName();
			}
			return "jdbc:datadirect:openedge://" + getHost() + ":" + getPort() + ";databaseName=" + getDbName();
		}

		public Properties getProperties() {
			return null;
		}

		public void setProperties(Properties property) {

		}
		
		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			// version 9
			if (version.matches("9(\\.)?\\d\\D")) {
				super.setJdbcDriver("com.progress.sql.jdbc.JdbcProgressDriver");
			}
			// default to version 10
			else {
				super.setJdbcDriver("com.ddtek.jdbc.openedge.OpenEdgeDriver");
			}
			this.version = version;
		}
		
		public Element saveToElement() {
			Element element = super.saveToElement();
			element.setAttribute("version",	 this.getVersion());
			return element;
		}
		
		public void initFromElement(Element element) {
			super.initFromElement(element);
			this.setVersion(element.getAttributeValue("version"));
		}
}
