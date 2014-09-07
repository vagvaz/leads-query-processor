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

package com.apatar.editgrid;

import java.io.File;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.DataDirection;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class EditgridConnection implements IPersistent {

	private String organization = "";
	private File docToImport = null;
	private boolean overwriteDocOnImport = false;
	private String userName = "";
	private PasswordString password = new PasswordString();
	private String appKey = "";
	private Enum<DataDirection> dataDirection = DataDirection.VerticalDirection;
	private int offset = 1;
	private int firstFieldPosition = 1;
	private int lastFieldPosition = 2;
	private boolean FirstFieldName = true;
	private boolean SkipEmptyRecord = false;

	public EditgridConnection() {
	}

	public EditgridConnection(String url, String login, String pass) {
		userName = login;
		password.setValue(pass);
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);

		e.setAttribute("organization", organization);
		e.setAttribute("userName", userName);
		e.setAttribute("appKey", appKey);

		e.addContent(getPassword().saveToElement());
		e.setAttribute("offset", "" + offset);
		e.setAttribute("FirstFieldName", "" + FirstFieldName);
		e.setAttribute("SkipEmptyRecord", "" + SkipEmptyRecord);
		e.setAttribute("firstFieldPosition", "" + firstFieldPosition);
		e.setAttribute("lastFieldPosition", "" + lastFieldPosition);
		e
				.setAttribute(
						"dataDirection",
						""
								+ (dataDirection == DataDirection.VerticalDirection ? "VerticalDirection"
										: "HorisontalDirection"));
		Element docToImport = new Element("docToImport");
		try {
			docToImport.setText(getDocToImport().getPath());
		} catch (Exception e1) {
			docToImport.setText("");
		}
		docToImport.setAttribute("overwriteDocOnImport", String
				.valueOf(getOverwriteDocOnImport()));
		e.addContent(docToImport);
		return e;
	}

	public void initFromElement(Element e) {
		if (e == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}

		organization = e.getAttributeValue("organization");
		userName = e.getAttributeValue("userName");
		appKey = e.getAttributeValue("appKey");

		if (appKey == null || userName == null) {
			ApplicationData.COUNT_INIT_ERROR++;
		}

		String attribut = e.getAttributeValue("offset");
		if (attribut != null) {
			offset = Integer.parseInt(attribut);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		attribut = e.getAttributeValue("firstFieldPosition");
		if (attribut != null) {
			firstFieldPosition = Integer.parseInt(attribut);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		attribut = e.getAttributeValue("lastFieldPosition");
		if (attribut != null) {
			lastFieldPosition = Integer.parseInt(attribut);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		attribut = e.getAttributeValue("SkipEmptyRecord");
		if (attribut != null) {
			SkipEmptyRecord = Boolean.parseBoolean(attribut);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		attribut = e.getAttributeValue("FirstFieldName");
		if (attribut != null) {
			FirstFieldName = Boolean.parseBoolean(attribut);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		attribut = e.getAttributeValue("dataDirection");
		if (attribut != null) {
			if ("VerticalDirection".equals(attribut)) {
				dataDirection = DataDirection.VerticalDirection;
			} else {
				dataDirection = DataDirection.HorisontalDirection;
			}
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		try {
			setDocToImport(new File(e.getChildText("docToImport")));
		} catch (Exception e1) {
			System.err.println("Error opening file. Message: `"
					+ e1.getMessage() + "`");
		}
		try {
			setOverwriteDocOnImport(Boolean.valueOf(e.getChild("docToImport")
					.getAttributeValue("overwriteDocOnImport")));
		} catch (Exception e1) {
			setOverwriteDocOnImport(false);
		}
		PersistentUtils.InitObjectFromChild(password, e);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String login) {
		userName = login;
	}

	public PasswordString getPassword() {
		return password;
	}

	public void setPassword(PasswordString password) {
		this.password = password;
	}

	/**
	 * @return the appKey
	 */
	public String getAppKey() {
		return appKey;
	}

	/**
	 * @param appKey
	 *            the appKey to set
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/**
	 * @return the dataDirection
	 */
	public Enum<DataDirection> getDataDirection() {
		return dataDirection;
	}

	/**
	 * @param dataDirection
	 *            the dataDirection to set
	 */
	public void setDataDirection(Enum<DataDirection> dataDirection) {
		this.dataDirection = dataDirection;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset
	 *            the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the firstFieldPosition
	 */
	public int getFirstFieldPosition() {
		return firstFieldPosition;
	}

	/**
	 * @param firstFieldPosition
	 *            the firstFieldPosition to set
	 */
	public void setFirstFieldPosition(int firstFieldPosition) {
		this.firstFieldPosition = firstFieldPosition;
	}

	/**
	 * @return the lastFieldPosition
	 */
	public int getLastFieldPosition() {
		return lastFieldPosition;
	}

	/**
	 * @param lastFieldPosition
	 *            the lastFieldPosition to set
	 */
	public void setLastFieldPosition(int lastFieldPosition) {
		this.lastFieldPosition = lastFieldPosition;
	}

	/**
	 * @return the firstFieldName
	 */
	public boolean isFirstFieldName() {
		return FirstFieldName;
	}

	/**
	 * @param firstFieldName
	 *            the firstFieldName to set
	 */
	public void setFirstFieldName(boolean firstFieldName) {
		FirstFieldName = firstFieldName;
	}

	/**
	 * @return the skipEmptyRecord
	 */
	public boolean isSkipEmptyRecord() {
		return SkipEmptyRecord;
	}

	/**
	 * @param skipEmptyRecord
	 *            the skipEmptyRecord to set
	 */
	public void setSkipEmptyRecord(boolean skipEmptyRecord) {
		SkipEmptyRecord = skipEmptyRecord;
	}

	public boolean isVerticalDirection() {
		return dataDirection == DataDirection.VerticalDirection ? true : false;
	}

	public int getMaxRows() {
		return isVerticalDirection() ? 65535 : 255;
	}

	public int getFirstDataRow() {
		return FirstFieldName ? offset + 1 : offset;
	}

	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @param organization
	 *            the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * @return the docToImport
	 */
	public File getDocToImport() {
		return docToImport;
	}

	/**
	 * @param docToImport
	 *            the docToImport to set
	 */
	public void setDocToImport(File docToImport) {
		this.docToImport = docToImport;
	}

	/**
	 * @return the overwriteDocOnImport
	 */
	public boolean getOverwriteDocOnImport() {
		return overwriteDocOnImport;
	}

	/**
	 * @param overwriteDocOnImport
	 *            the overwriteDocOnImport to set
	 */
	public void setOverwriteDocOnImport(boolean overwriteDocOnImport) {
		this.overwriteDocOnImport = overwriteDocOnImport;
	}
}
