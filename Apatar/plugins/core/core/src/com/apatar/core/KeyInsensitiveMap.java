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

package com.apatar.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class KeyInsensitiveMap {
	private HashMap<String, Object> map = new HashMap<String, Object>();
	private HashMap<String, Object> lowerCaseMap = new HashMap<String, Object>();
	
	//private HashMap<String, String> nameMap = new HashMap<String,String>();
	
	public void clear() {
		map.clear();
		lowerCaseMap.clear();
		//nameMap.clear();
	}
	
	public void put(String key, Object value) {
		map.put(key, value);
		String lowerCaseKey = key.toLowerCase().intern();
//System.out.println("KIM.put adding names: " + lowerCaseKey + ", " + key);
		lowerCaseMap.put(lowerCaseKey, value);
		//nameMap.put(lowerCaseKey, key);
	}

	public void putAll(Map<String, Object> map) {
//System.out.println("In putall");
		Set<Entry<String,Object>> entrySet = map.entrySet();
		for (Entry<String,Object> entry : entrySet) {
			// Will handle map, lowerCaseMap and nameMap puts
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public Object get(String key, boolean ignoreCase) {
		Object returnValue;
		if (ignoreCase) {
			returnValue = lowerCaseMap.get(key.toLowerCase());
		} else {
			returnValue = map.get(key);
		}
//System.out.println("Map.get: " + key + ", " + ignoreCase + ", " + returnValue);
		return returnValue;
	}
	
	public Object remove(String key, boolean ignoreCase) {
		// Either way, remove it from the lower case map and name map
		String lowerCaseKey = key.toLowerCase();
		lowerCaseMap.remove(lowerCaseKey);
		//nameMap.remove(lowerCaseKey);

		// Mixed case remove is more tricky
		if (ignoreCase) {
			for (String str : map.keySet()) {
				if (str.equalsIgnoreCase(key))
					return map.remove(str);
			}
			return null;
		}
		return map.remove(key);
	}
	
	public boolean containsKey(String key, boolean ignoreCase) {
		if (ignoreCase) {
			return lowerCaseMap.containsKey(key.toLowerCase());
		}
		return map.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	
	public Collection<Object> values() {
		return map.values();
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
	
	public int size() {
		return map.size();
	}
	
	public Map<String, Object> getMap() {
		return map;
	}
	
	/*public String getCaseSensitiveName(String caseInsensitiveName) {
		return nameMap.get(caseInsensitiveName.toLowerCase());
	}*/
}
