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
 


package com.apatar.amazon.s3.function;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.apatar.amazon.s3.AmazonS3Function;
import com.apatar.amazon.s3.AmazonS3RequestParameters;
import com.apatar.core.ETableMode;
import com.apatar.core.KeyInsensitiveMap;
import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.ListAllMyBucketsResponse;

public class GetListAllMyBucketsAmazonS3Function extends AmazonS3Function {

	
	public GetListAllMyBucketsAmazonS3Function(Object id, String displayName, List<AmazonS3RequestParameters> request, List<AmazonS3RequestParameters> response) {
		super(id, displayName, request, response, true, false);
		mode = ETableMode.ReadOnly;
	}

	public List<KeyInsensitiveMap> execute(KeyInsensitiveMap attributes,
			AWSAuthConnection conn) throws MalformedURLException, IOException {
		ListAllMyBucketsResponse bucketResponse = conn.listAllMyBuckets(null);
		List listBucket = bucketResponse.entries;
		List<KeyInsensitiveMap> lkiMap = new ArrayList<KeyInsensitiveMap>();
		for (Object obj : listBucket) {
			KeyInsensitiveMap kiMap = new KeyInsensitiveMap();
			com.amazon.s3.Bucket bucket = (com.amazon.s3.Bucket)obj;
			
			kiMap.put("name", bucket.name);
			kiMap.put("creationDate", bucket.creationDate);
			lkiMap.add(kiMap);
		}
		return lkiMap;
	}

}

