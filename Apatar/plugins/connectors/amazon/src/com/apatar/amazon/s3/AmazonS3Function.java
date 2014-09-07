/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

package com.apatar.amazon.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.apatar.core.ETableMode;
import com.apatar.core.KeyInsensitiveMap;
import com.amazon.s3.AWSAuthConnection;

public abstract class AmazonS3Function {
	
	Object id;
	String displayName;
	
	protected List<AmazonS3RequestParameters> request = new ArrayList<AmazonS3RequestParameters>();
	protected List<AmazonS3RequestParameters> response = new ArrayList<AmazonS3RequestParameters>();
	
	protected boolean workingWithBucket;
	protected boolean allowManually = true;
	
	protected ETableMode mode = ETableMode.ReadWrite;
	
	public AmazonS3Function(Object id, String displayName, List<AmazonS3RequestParameters> request, List<AmazonS3RequestParameters> response, boolean workingWithBucket) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.request = request;
		this.response = response;
		this.workingWithBucket = workingWithBucket;
	}
	
	public AmazonS3Function(Object id, String displayName, List<AmazonS3RequestParameters> request, List<AmazonS3RequestParameters> response, boolean workingWithBucket, boolean allowManually) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.request = request;
		this.response = response;
		this.workingWithBucket = workingWithBucket;
		this.allowManually = allowManually;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public Object getId() {
		return id;
	}
	
	public String toString() {
		return displayName;
	}
	
	public boolean isAllowManually() {
		return allowManually;
	}

	public boolean isWorkingWithBucket() {
		return workingWithBucket;
	}
	
	public ETableMode getMode() {
		return mode;
	}

	public void setMode(ETableMode mode) {
		this.mode = mode;
	}

	public abstract List<KeyInsensitiveMap> execute(KeyInsensitiveMap attributes, AWSAuthConnection conn) throws MalformedURLException, IOException;
}
