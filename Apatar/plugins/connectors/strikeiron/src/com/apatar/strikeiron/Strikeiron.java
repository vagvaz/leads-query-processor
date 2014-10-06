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
 


package com.apatar.strikeiron;

import java.util.List;
import java.util.Map;

import com.apatar.core.Record;

public interface Strikeiron {
	public String getPassword();

	public void setPassword(String password);

	public String getUserName();

	public void setUserName(String userName);
	
	public List<Record> getTempVerificationRecords();
	
	public String getDBTempVerificationName();
	
	public Map<String, ClassLoader> getFunctionLoader();
}

