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

package com.apatar.functions.String;

import java.util.List;

import com.apatar.core.IntValueAbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class PadLeftTransformFunction extends IntValueAbstractApatarFunction {

	public Object execute(List list) {
		int numEndSubString;
		try{
			numEndSubString = getValue();  
		} catch(Exception e ){ return null; }
		
		if( list.get(0) instanceof String){
			String str = list.get(0).toString(); 
			for(int j=0; j<numEndSubString; j++)
				str = " ".concat(str);
				
			return str ;
		} else
			return list.get(0);
			
	}
	
	static FunctionInfo fi = new FunctionInfo("Pad Left", 1, 1);
	static 
	{
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}
	
	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
