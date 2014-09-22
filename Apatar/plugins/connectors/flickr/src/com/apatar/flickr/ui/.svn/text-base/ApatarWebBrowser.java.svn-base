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

package com.apatar.flickr.ui;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;


//import org.jdesktop.jdic.browser.BrowserEngineManager;
import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;

import com.apatar.core.ApplicationData;

public class ApatarWebBrowser extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private static ApatarWebBrowser thisClass	= null;
	private static JProgressBar status			= null;
	private static WebBrowser webBrowser		= null;
	
	private static class BrowserEvent implements WebBrowserListener {

		public void downloadStarted(WebBrowserEvent arg0) {
			status.setValue(0);
			thisClass.repaint();
		}

		public void downloadCompleted(WebBrowserEvent e) {
			WebBrowser browser = ((WebBrowser)e.getSource());
			
			if( ((WebBrowser)e.getSource()).getURL() != null ){
				String url = browser.getURL().toString();
				
				if( url.startsWith( "http://www.flickr.com") ){
					thisClass.dispose();
				} else if( url.startsWith("https://login.yahoo.com")  ) {
					browser.setSize(790, 390);
					thisClass.setSize(800, 400);
				}
				
				status.setValue(100);
				thisClass.repaint();
			}
		}

		public void downloadProgress(WebBrowserEvent arg0) {
			status.setValue(status.getValue()+2);
		}

		public void downloadError(WebBrowserEvent arg0) {}

		public void documentCompleted(WebBrowserEvent e) {}

		public void titleChange(WebBrowserEvent arg0) {}

		public void statusTextChange(WebBrowserEvent arg0) {}

		public void initializationCompleted(WebBrowserEvent arg0) {}

	};
	
	public ApatarWebBrowser(JFrame frame){
		this(frame, "");
	}
	
	public ApatarWebBrowser(JFrame frame, String url ){
		super(frame);
		thisClass = this;

		try{
			setTitle("Apatar - Flickr Authorization");
			setSize(100, 100);
			
			URL httpUrl = new URL(url);
			
			webBrowser = new WebBrowser(httpUrl);
			webBrowser.setSize(100, 90);

			webBrowser.addWebBrowserListener(new BrowserEvent());
			WebBrowser.setDebug( ApplicationData.DEBUG );
			
			
			status = new JProgressBar(0, 100);
			
			getContentPane().add(webBrowser, BorderLayout.CENTER);
			getContentPane().add(status, BorderLayout.SOUTH);
			pack();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
}
