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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.apatar.core.DataDirection;

public class MsExcelUtils {

	public static final ImageIcon	READ_MSEXCEL_ICON		= new ImageIcon(
																	MsExcelNodeFactory.class
																			.getResource("16-excel.png"));
	public static final ImageIcon	READ_MSEXCEL_NODE_ICON	= new ImageIcon(
																	MsExcelNodeFactory.class
																			.getResource("32-excel.png"));

	public static List<String> getData(File file, String tableName,
			DataDirection dd, int num) throws BiffException, IOException {
		List<String> data = new ArrayList<String>();
		Workbook wb = Workbook.getWorkbook(file);
		Sheet sheet = wb.getSheet("tableName");
		Cell[] cells;
		if (dd == DataDirection.VerticalDirection) {
			cells = sheet.getColumn(num);
		}
		return data;
	}
}
