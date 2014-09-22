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

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ProperCaseTransformFunction extends AbstractApatarFunction{

	public String getTitle() {
		return "Proper Case";
	}

	public Object execute(List list) {
		String resultStr;
		char charsArr[];
		
		if( list.get(0) instanceof String ){
				
			resultStr = list.get(0).toString();
			if( resultStr.length() > 0 ){
				charsArr = resultStr.toCharArray();
				charsArr[0] = Character.toUpperCase(charsArr[0]); 
				resultStr = String.valueOf(charsArr[0]);
					
				for(int j=1; j<charsArr.length; j++){
					if( Character.isSpaceChar(charsArr[j-1] ) )
						charsArr[j] = Character.toUpperCase(charsArr[j]);
					else
						charsArr[j] = Character.toLowerCase(charsArr[j]);
					resultStr = resultStr.concat( String.valueOf(charsArr[j]) );
				}
				return resultStr;	
			}
		}
		else
			return list.get(0);
		
		return list.get(0);
	}

	static FunctionInfo fi = new FunctionInfo("Proper Case", 1, 1);
	static 
	{
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}
	
	public FunctionInfo getFunctionInfo() {
		return fi;
	}
}
