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
 


package com.apatar.amazon.s3.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.apatar.amazon.s3.AmazonS3Connection;
import com.apatar.amazon.s3.AmazonS3Node;
import com.apatar.amazon.s3.Bucket;
import com.apatar.core.ApplicationData;
import com.apatar.ui.wizard.RecordSourceDescriptor;
import com.apatar.ui.wizard.TableModeDescriptor;
import com.apatar.ui.wizard.WizardPanelDescriptor;
import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.ListAllMyBucketsResponse;

public class ListBucketDescriptor extends WizardPanelDescriptor {
	public static final String IDENTIFIER = "LIST_BUCKET_PANEL";
	
	AmazonS3Node node;
	JListBucketPanel panel = new JListBucketPanel(this);
	public ListBucketDescriptor(AmazonS3Node node) {
		super();
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel);
		this.node = node;
	}
	
	List<Bucket> buckets = new ArrayList<Bucket>();
	
	public Object getNextPanelDescriptor() {
        return TableModeDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() {
    	return RecordSourceDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() {
    	AmazonS3Connection aS3C = (AmazonS3Connection)ApplicationData.getProject().getProjectData(node.getConnectionDataID()).getData();
		AWSAuthConnection conn = new AWSAuthConnection(aS3C.getAccessKeyID().getValue(), aS3C.getSecretAccessKey().getValue());
		try {
			ListAllMyBucketsResponse bucketResponse = conn.listAllMyBuckets(null);
			List listBucket = bucketResponse.entries;
			buckets.clear();
			for (Object obj : listBucket) {
				buckets.add(new Bucket(((com.amazon.s3.Bucket)obj).name));
			}
			panel.fillBuckets(buckets);
			panel.setConnection(conn);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void displayingPanel() {
    }

    public int aboutToHidePanel(String actionCommand) {
    	try {
			node.setBuckets(panel.getSelectedBucket());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return CHANGE_PANEL;
    }
	
}

