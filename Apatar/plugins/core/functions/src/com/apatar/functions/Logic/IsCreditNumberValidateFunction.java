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

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class IsCreditNumberValidateFunction extends AbstractApatarFunction{

	public Object execute(List list) {
		if (list == null || list.size()<1)
			return false;
		
		String cardNumber = list.get(0).toString();
		String digitsOnly = getDigitsOnly (cardNumber);
		
	    int sum = 0, digit = 0, addend = 0;
	    boolean timesTwo = false;

	    for (int i = digitsOnly.length () - 1; i >= 0; i--) {
	      digit = Integer.parseInt (digitsOnly.substring (i, i + 1));
	      if ( timesTwo ) {
	        addend = digit * 2;
	        if (addend > 9)
	          addend -= 9;
	      }
	      else {
	        addend = digit;
	      }
	      sum += addend;
	      timesTwo = !timesTwo;
	    }

	    int modulus = sum % 10;
	    return modulus == 0;
	}
	
	private String getDigitsOnly (String s) {
	    StringBuffer digitsOnly = new StringBuffer ();
	    char ch;
	    for (int i = 0; i < s.length (); i++) {
	      ch = s.charAt (i);
	      if( Character.isDigit (ch) )
	        digitsOnly.append (ch);
	    }
	    return digitsOnly.toString ();
	}
	
	static FunctionInfo fi = new FunctionInfo("Is Credit Number", 1, 1);
	static
	{
		fi.getCategories().add(FunctionCategory.ALL);
		fi.getCategories().add(FunctionCategory.String);
	}
	
	public FunctionInfo getFunctionInfo() {
		return fi;
	}
}
