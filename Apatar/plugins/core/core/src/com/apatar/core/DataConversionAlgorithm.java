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

import java.util.ArrayList;
import java.util.List;

public class DataConversionAlgorithm 
{
	public static DBTypeRecord bestRecordLookup(List<DBTypeRecord> databasetypes, 
			Record original) 
	{
		DBTypeRecord returnCandidate = bestRecordLookupNoError(databasetypes, original);
		
		if (returnCandidate == null) {
			System.out.println(String.format("Type %s is not processed correctly", original.getOriginalType()));

			// selecte the best type from binary set as a template for this type
			for (DBTypeRecord dbt : databasetypes)
				if (dbt.getType() == ERecordType.Binary) {
					return dbt;
				}
		}
		return returnCandidate;
	}

	public static DBTypeRecord bestRecordLookup(DataBaseInfo dbi, Record original)
	{
		return bestRecordLookup(dbi.getAvailableTypes(), original );
	}
	
	public static boolean recordTypeFit(Record first, Record second)
	{
		if (first.getType() == second.getType())
			return true;
		return false;
	}
	
	public static DBTypeRecord bestRecordLookup(List<DBTypeRecord> databasetypes, ERecordType rt, long length) {
		DBTypeRecord candidate = null;
		List<DBTypeRecord> identicalType = new ArrayList<DBTypeRecord>();
		for (DBTypeRecord rec : databasetypes) {
			if (rec.getType() == rt) {
				identicalType.add(rec);
				if (rec.getLength() >= length) {
					if (candidate == null) {
						candidate = rec;
						continue;
					}
					if (rec.getLength() < candidate.getLength())
						candidate = rec;
				}
			}
		}
		if (candidate == null) {
			candidate = identicalType.get(0);
			for(DBTypeRecord rec : identicalType) {
				if (candidate.getLength() < rec.getLength()) {
					candidate = rec;
				}
			}
		}
		return candidate;
	}
	
	public static DBTypeRecord bestRecordLookupNoError(List<DBTypeRecord> databasetypes, 
			Record original)
	{
		DBTypeRecord returnCandidate = null;
		List<DBTypeRecord> lookup = new ArrayList<DBTypeRecord>();
		List<DBTypeRecord> results = new ArrayList<DBTypeRecord>();
		lookup.addAll(databasetypes);
	
		//!!!!!!!!!!!! first step of filtration is the type step
		for(DBTypeRecord rec : lookup)
		{
			if (rec.getType() == original.getType())
				results.add(rec);
		}
		
		// if result is equeal
		if (results.size() > 0)
			returnCandidate = results.get(0);

		//!!!!!!!!!!!! second step is the maximum length limitation
		lookup.clear();
		lookup.addAll(results);
		results.clear();
		//best length
		long nBestLength = 0;
		for (DBTypeRecord rec : lookup)
		{
			if (rec.getLength() >= original.getLength())
				results.add(rec);
			
			if (rec.getLength() > nBestLength &&
					nBestLength != original.getLength())
			{
				nBestLength = rec.getLength();
				returnCandidate = rec;
			}
		}
		
		
		//!!!!!!!!!!!! second step is the limitation by signed/unsigned
		if (original.isSigned())
		{
			lookup.clear();
			lookup.addAll(results);
			results.clear();
			for (DBTypeRecord rec : lookup)
			{
				if (rec.isSupportSign())
					results.add(rec);
			}
		}
		
		if (results.size() > 0)
			returnCandidate = results.get(0);
		
		// the fourth step is the PK abilities
		if (original.isPrimaryKey())
		{
			lookup.clear();
			lookup.addAll(results);
			results.clear();
			for (DBTypeRecord rec : lookup)
			{
				if (rec.isSupportPK())
					results.add(rec);
			}
		}
		
		if (results.size() > 0)
			returnCandidate = results.get(0);
	
		return returnCandidate;
	}
}
