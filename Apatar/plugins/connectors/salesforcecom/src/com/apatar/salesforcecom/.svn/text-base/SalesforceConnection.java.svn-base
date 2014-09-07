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

package com.apatar.salesforcecom;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.FolderPath;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class SalesforceConnection implements IPersistent {
	protected String userName = "";
	protected PasswordString password = new PasswordString();
	protected boolean sandbox = false;
	protected String sandboxURL = "https://test.salesforce.com/services/Soap/u/18.0";
	protected String salesforceURL = "https://www.salesforce.com/services/Soap/u/18.0";
	protected String bulkURL = "";
	protected FolderPath errorCSVpath = new FolderPath();
	protected boolean useBulkApi = false;
	protected boolean returnDeletedRecords = false;

	protected boolean proxySupport = false;
	protected String proxyHost;
	protected String proxyPort;
	protected String proxyUser;
	protected PasswordString proxyPassword = new PasswordString();

	public SalesforceConnection() {

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String value) {
		userName = value;
	}

	public PasswordString getPassword() {
		return password;
	}

	public void setPassword(PasswordString value) {
		password = value;
	}

	public boolean isSandbox() {
		return sandbox;
	}

	public void setSandbox(boolean value) {
		sandbox = value;
	}

	public String getSandboxURL() {
		return sandboxURL;
	}

	public void setSandboxURL(String value) {
		sandboxURL = value;
	}

	public String getSalesforceURL() {
		return salesforceURL;
	}

	public void setSalesforceURL(String value) {
		salesforceURL = value;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public PasswordString getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(PasswordString proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isProxySupport() {
		return proxySupport;
	}

	public void setProxySupport(boolean proxySupport) {
		this.proxySupport = proxySupport;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);
		if (ApplicationData.SalesForceAppExchange) {
			e.setAttribute("userName", "");
			e.addContent(new PasswordString("").saveToElement());
		} else {
			e.setAttribute("userName", userName);
			e.addContent(getPassword().saveToElement());
		}
		e.setAttribute("bulkURL", "" + getBulkURL());
		e.setAttribute("sandbox", "" + isSandbox());
		e.setAttribute("useBulkApi", "" + isUseBulkApi());
		e.setAttribute("returnDeletedRecords", "" + isReturnDeletedRecords());
		e.setAttribute("sandboxURL", "" + getSandboxURL());
		e.setAttribute("salesforceURL", "" + getSalesforceURL());
		e.setAttribute("errorCSVpath", "" + getErrorCSVpath().getPath());
		Element elProxy = new Element("Proxy");
		elProxy.setAttribute("support", "" + isProxySupport());
		elProxy.setAttribute("host", "" + getProxyHost());
		elProxy.setAttribute("port", "" + getProxyPort());
		elProxy.setAttribute("userName", "" + getProxyUser());
		elProxy.setAttribute("password", "" + getProxyPassword().getValue());

		e.addContent(elProxy);

		System.out.println(ApplicationData.SalesForceAppExchange);

		return e;
	}

	public void initFromElement(Element e) {
		if (e == null) {
			return;
		}
		if (!ApplicationData.SalesForceAppExchange) {
			userName = e.getAttributeValue("userName");
			// password.setValue( e.getAttributeValue("password") );
			PersistentUtils.InitObjectFromChild(password, e, false);
		}
		setSandbox(Boolean.parseBoolean(e.getAttributeValue("sandbox")));
		try {
			setUseBulkApi(Boolean.parseBoolean(e
					.getAttributeValue("useBulkApi")));
		} catch (Exception e1) {
			setUseBulkApi(false);
		}
		String sandboxURL = e.getAttributeValue("sandboxURL");
		String bulkURL = e.getAttributeValue("bulkURL");
		try {
			getErrorCSVpath().setPath(e.getAttributeValue("errorCSVpath"));
		} catch (Exception e2) {
		}
		// String salesforceURL = e.getAttributeValue("salesforceURL");
		if (null != sandboxURL) {
			setSandboxURL(sandboxURL);
		}
		// TODO check if this is necessary
		if (null != salesforceURL) {
			setSalesforceURL(salesforceURL);
		}
		if (null != bulkURL) {
			setBulkURL(bulkURL);
		}

		try {
			setReturnDeletedRecords(Boolean.valueOf(e
					.getAttributeValue("returnDeletedRecords")));
		} catch (Exception e1) {
			setReturnDeletedRecords(false);
		}

		Element elProxy = e.getChild("Proxy");
		if (elProxy != null) {
			setProxySupport(Boolean
					.parseBoolean(e.getAttributeValue("support")));
			setProxyHost(elProxy.getAttributeValue("host"));
			setProxyPort(elProxy.getAttributeValue("port"));
			setProxyUser(elProxy.getAttributeValue("userName"));
			getProxyPassword().setValue(elProxy.getAttributeValue("password"));
		}
	}

	public boolean IsEmpty() {
		return userName.length() == 0 || password.getValue().length() == 0;
	}

	/**
	 * @return the bulkURL
	 */
	public String getBulkURL() {
		return bulkURL;
	}

	/**
	 * @param bulkURL
	 *            the bulkURL to set
	 */
	public void setBulkURL(String bulkURL) {
		this.bulkURL = bulkURL;
	}

	/**
	 * @return the returnDeletedRecords
	 */
	public boolean isReturnDeletedRecords() {
		return returnDeletedRecords;
	}

	/**
	 * @param returnDeletedRecords
	 *            the returnDeletedRecords to set
	 */
	public void setReturnDeletedRecords(boolean returnDeletedRecords) {
		this.returnDeletedRecords = returnDeletedRecords;
	}

	/**
	 * @return the useBulkApi
	 */
	public boolean isUseBulkApi() {
		return useBulkApi;
	}

	/**
	 * @param useBulkApi
	 *            the useBulkApi to set
	 */
	public void setUseBulkApi(boolean useBulkApi) {
		this.useBulkApi = useBulkApi;
	}

	/**
	 * @return the errorCSVpath
	 */
	public FolderPath getErrorCSVpath() {
		if (errorCSVpath == null) {
			errorCSVpath = new FolderPath();
		}
		return errorCSVpath;
	}

	/**
	 * @param errorCSVpath
	 *            the errorCSVpath to set
	 */
	public void setErrorCSVpath(FolderPath errorCSVpath) {
		this.errorCSVpath = errorCSVpath;
	}

}
