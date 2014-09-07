/*TODO recorded refactoring
 * класс ETLFileFilter переименован в ApatarFileFilter и перемещён в пакет UI
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

/*TODO recorded refactoring
 * в класс ApatarFileFilter добавлен конструктор без параметров, который устанавливает расширение по-умолчанию "aptr"
 * *********************
 */
package com.apatar.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ApatarFileFilter extends FileFilter {

	String ext;

	public ApatarFileFilter() {
		super();
		ext = "aptr";
	}

	public ApatarFileFilter(String ext) {
		super();
		if ((null == ext) || ("".equals(ext))) {
			this.ext = "aptr";
		} else {
			this.ext = ext;
		}
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		} else {
			String file = f.getName();
			if (file.toLowerCase().endsWith(ext)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String getDescription() {
		return (ext.toLowerCase() + ", " + ext.toUpperCase());
	}
}
