/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013

 

    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.

 

    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.

 

    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

*/
 


package com.apatar.openedge;

import java.io.File;

public class DriverProperties {
	String name;
	String path;
	String originalName;
	
	boolean updatedPath = false;
	
	boolean deleted = false;
	
	public DriverProperties(String path) {
		super();
		this.path = path;
		this.name = (new File(path).getName());
		this.originalName = this.name; 
	}
	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
		this.originalName = this.name;
		this.name = (new File(path).getName());
		updatedPath = true;
	}
	
	public String toString() {
		return name;
	}
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public boolean isUpdatedPath() {
		return updatedPath;
	}
	public void setUpdatedPath(boolean updatedPath) {
		this.updatedPath = updatedPath;
	}
}

