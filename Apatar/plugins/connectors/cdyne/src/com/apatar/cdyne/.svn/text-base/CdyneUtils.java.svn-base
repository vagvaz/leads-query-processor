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


package com.apatar.cdyne;

import java.io.File;
import java.rmi.RemoteException;

import javax.swing.ImageIcon;
import javax.xml.rpc.ServiceException;

import com.apatar.cdyne.ws.licenseCheck.LicenseCheckLocator;
import com.apatar.cdyne.ws.licenseCheck.LicenseCheckSoap_PortType;

public class CdyneUtils {

	public static final ImageIcon CDYNE_ICON = new ImageIcon(CdyneFunctionNodeFactory.class
	        .getResource("Cdyne-32.png"));
	public static final ImageIcon SMALL_CDYNE_ICON = new ImageIcon(CdyneFunctionNodeFactory.class
	        .getResource("Cdyne-16.png"));
	public static File functionFile = new File("Cdynefunctions.xml");
	
	public static int licenseCheck(String key) throws ServiceException, RemoteException {
		LicenseCheckLocator locator = new LicenseCheckLocator();
		LicenseCheckSoap_PortType port = locator.getLicenseCheckSoap();
		return port.getKeyInfo(key);
	}
	
	public static String getTextMessage(int kode) {
		switch (kode) {
			case -3: return "Service error";
			case -2: return "Key not found";
			case -1: return "Not a guid";
			case 0:  return "Valid license key";
			case 1:  return "Disabled key";
			case 2:  return "Suspended key";
			case 3:  return "Valid demo key";
			case 4:  return "Exhausted demo key";
		}
		return "Unknown error";
	}
	
}

