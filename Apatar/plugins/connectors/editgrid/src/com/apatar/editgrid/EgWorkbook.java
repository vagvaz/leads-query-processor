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
package com.apatar.editgrid;

import com.apatar.core.ETableMode;
import com.apatar.core.RDBTable;
import com.apatar.editgrid.ws.Workbook;

/**
 * @author Konstantin Maximchik
 */
public class EgWorkbook extends RDBTable {

	private Workbook	egWorkbook	= null;

	/**
	 * @param name
	 * @param rwmode
	 */
	public EgWorkbook(String name, ETableMode rwmode) {
		super(name, rwmode);
	}

	/**
	 * @param name
	 * @param rwmode
	 */
	public EgWorkbook(String name, ETableMode rwmode, Workbook workbook) {
		super(name, rwmode);
		egWorkbook = workbook;
	}

	/**
	 * @param tableName
	 * @param mode
	 * @param comment
	 */
	public EgWorkbook(String tableName, ETableMode mode, String comment) {
		super(tableName, mode, comment);
	}

	/**
	 * @return the egWorkbook
	 */
	public Workbook getEgWorkbook() {
		return egWorkbook;
	}

	/**
	 * @param egWorkbook
	 *        the egWorkbook to set
	 */
	public void setEgWorkbook(Workbook egWorkbook) {
		this.egWorkbook = egWorkbook;
	}

}
