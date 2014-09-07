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

package com.apatar.functions.Logic;

import java.util.List;

import com.apatar.core.ValueAbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

import java.util.regex.*;

public class FindRegExpFunction extends ValueAbstractApatarFunction {
	
	static FunctionInfo fi = new FunctionInfo("Match RegExp", 1, 1);

	public Object execute(List list) {
		boolean found_matches = false;		
		String expression = getValue();
		
		if( list.size() == 1 )
			if( (list.get(0) instanceof String)
					&& (expression instanceof String) ) {
				
				if (list.get(0).toString().length() == 0)
					return false;
				System.out.println("MatchRegExp: expression = `"+expression+"`");
				System.out.println("MatchRegExp: String = `"+list.get(0)+"`");
				try {
					Pattern patt = Pattern.compile(expression);
					Matcher match =  patt.matcher(list.get(0).toString());
					found_matches = match.matches();
				} catch (Exception e) {
					System.err.println("MatchRegExp: expression = `"+expression+"`");
					System.err.println("MatchRegExp: String = `"+list.get(0)+"`");
					System.err.println(e.getMessage());
					return false;
				}
			}
		return found_matches;
	}
	
	static
	{
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}
	
	
}
