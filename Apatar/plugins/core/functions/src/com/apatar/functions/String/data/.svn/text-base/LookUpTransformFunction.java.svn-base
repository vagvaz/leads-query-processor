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

package com.apatar.functions.String.data;

import java.util.List;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class LookUpTransformFunction extends AbstractApatarFunction {
	public Object execute(List list) {
		int numberForSearch = 0;
		
		try{
			numberForSearch = Integer.parseInt( (String)list.get(list.size()-1) );  
		} catch(Exception e ){
			return list.get(0);
		}
		
		if( list.size() < numberForSearch)
			return list.get(0);
		return list.get(numberForSearch-1);
	}
	
	static FunctionInfo fi = new FunctionInfo("Look Up", 1, 1);
	static 
	{
		fi.getCategories().add(FunctionCategory.String);
	}
	
	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
