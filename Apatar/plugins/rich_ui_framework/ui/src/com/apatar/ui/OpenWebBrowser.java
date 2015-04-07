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

package com.apatar.ui;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.Ostermiller.util.Browser;
import com.apatar.core.ApatarException;
import com.apatar.core.ApplicationData;

public class OpenWebBrowser extends Thread {
	private String	url	= "";

	public OpenWebBrowser(String _url) throws ApatarException {
		try {
			url = ApplicationData.parseLinkLabel(_url);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Link url was not set.",
					"Error opening browser", JOptionPane.WARNING_MESSAGE);
		}

	}

	@Override
	public void run() {
		if (url.equals("")) {
			return;
		}
		Browser.init();
		try {
			Browser.displayURL(url, "Apatar");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
