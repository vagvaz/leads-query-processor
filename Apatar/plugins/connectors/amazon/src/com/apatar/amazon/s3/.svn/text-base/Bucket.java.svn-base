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
 


package com.apatar.amazon.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.ListBucketResponse;
import com.amazon.s3.ListEntry;

public class Bucket {
	String name;
	List<String> objectKeys = new ArrayList<String>();
	
	public Bucket(String name) {
		super();
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public void addObjectKey(String key) {
		objectKeys.add(key);
	}
	
	public void removeObjectKey(String key) {
		objectKeys.remove(key);
	}
	
	public void updateBucket(List<String> keys) {
		objectKeys.clear();
		objectKeys.addAll(keys);
	}

	public String getName() {
		return name;
	}

	public List<String> getObjectKeys() {
		return objectKeys;
	}
	
	public static List<String> getAllKeys(String name, AWSAuthConnection conn) throws MalformedURLException, IOException {
		String lastKey = "";
		List<String> result = new ArrayList<String>();
		int count = 1000;
		while(count >= 1000) {
			ListBucketResponse response = conn.listBucket(name, "", lastKey, 1000, null);
			for (Object entryObj : response.entries) {
				ListEntry entry = (ListEntry)entryObj;
				result.add(entry.key);
				lastKey = entry.key;
			}
			count = response.entries.size();
		}
		return result;
	}
}

