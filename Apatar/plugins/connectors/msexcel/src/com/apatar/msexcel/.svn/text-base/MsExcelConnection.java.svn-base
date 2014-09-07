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

package com.apatar.msexcel;

import java.io.File;

import org.jdom.Element;

import com.apatar.core.ApplicationData;
import com.apatar.core.DataDirection;
import com.apatar.core.IPersistent;
import com.apatar.core.PasswordString;
import com.apatar.core.PersistentUtils;

public class MsExcelConnection implements IPersistent {

	private PasswordString password = new PasswordString();
	private File file = null;
	private Enum<DataDirection> dataDirection = DataDirection.VerticalDirection;
	private int offset = 1;
	private int firstFieldPosition = 1;
	private int lastFieldPosition = 2;
	private boolean FirstFieldName = true;
	private boolean SkipEmptyRecord = false;

	public MsExcelConnection() {
		if (ApplicationData.DEBUG) {
			password = new PasswordString("");
			file = new File("/");
		}
	}

	public MsExcelConnection(PasswordString password, File file) {
		this.password = password;
		this.file = file;
	}

	public PasswordString getPassword() {
		return password;
	}

	public void setPassword(PasswordString password) {
		this.password = password;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Enum<DataDirection> getDataDirection() {
		return dataDirection;
	}

	public void setDataDirection(Enum<DataDirection> dataDirection) {
		this.dataDirection = dataDirection;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getFirstFieldPosition() {
		return firstFieldPosition;
	}

	public void setFirstFieldPosition(int firstFieldPosition) {
		this.firstFieldPosition = firstFieldPosition;
	}

	public int getLastFieldPosition() {
		return lastFieldPosition;
	}

	public void setLastFieldPosition(int lastFieldPosition) {
		this.lastFieldPosition = lastFieldPosition;
	}

	public boolean isFirstFieldName() {
		return FirstFieldName;
	}

	public void setFirstFieldName(boolean firstFieldName) {
		FirstFieldName = firstFieldName;
	}

	public boolean isSkipEmptyRecord() {
		return SkipEmptyRecord;
	}

	public void setSkipEmptyRecord(boolean skipEmptyRecord) {
		SkipEmptyRecord = skipEmptyRecord;
	}

	public Element saveToElement() {
		Element e = PersistentUtils.CreateElement(this);

		if (null == file) {
			e.setAttribute("filePath", "");
		} else {
			e.setAttribute("filePath", file.getPath());
		}

		e.addContent(getPassword().saveToElement());

		e.setAttribute("offset", "" + offset);
		e.setAttribute("FirstFieldName", "" + FirstFieldName);
		e.setAttribute("SkipEmptyRecord", "" + SkipEmptyRecord);
		e.setAttribute("firstFieldPosition", "" + firstFieldPosition);
		e.setAttribute("lastFieldPosition", "" + lastFieldPosition);
		e.setAttribute("dataDirection", "" + dataDirection);

		return e;
	}

	public void initFromElement(Element e) {
		if (e == null) {
			ApplicationData.COUNT_INIT_ERROR++;
			return;
		}
		file = new File(e.getAttributeValue("filePath"));

		PersistentUtils.InitObjectFromChild(password, e, false);

		String attribut = e.getAttributeValue("offset");
		if (attribut != null) {
			offset = Integer.parseInt(attribut);
		} else {
			ApplicationData.COUNT_INIT_ERROR++;
		}
		attribut = e.getAttributeValue("dataDirection");
		if (attribut != null) {
			if ("HorisontalDirection".equals(attribut)) {
				dataDirection = DataDirection.HorisontalDirection;
			} else {
				dataDirection = DataDirection.VerticalDirection;
			}
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
	}

}
