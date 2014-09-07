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

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import com.apatar.core.ApatarException;
import com.apatar.core.ApplicationData;

public class MouseHyperLinkEvent implements MouseListener {

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent me) {
		final JLabel label = (JLabel) me.getComponent();
		if (me.getButton() == MouseEvent.BUTTON1) {
			if (me.getComponent() instanceof JLabel) {
				Thread th;
				try {
					th = new OpenWebBrowser(label.getText());
					th.run();
				} catch (ApatarException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (me.getButton() == MouseEvent.BUTTON3) {
				JPopupMenu menu = new JPopupMenu();
				try {
					final String url = ApplicationData.parseLinkLabel(label
							.getText());
					menu.add(new AbstractAction("Open") {

						public void actionPerformed(ActionEvent arg0) {
							OpenWebBrowser owb;
							try {
								owb = new OpenWebBrowser(url);
								owb.run();
							} catch (ApatarException e) {
								e.printStackTrace();
							}

						}
					});
					menu.add(new AbstractAction("Copy Shortcut") {

						public void actionPerformed(ActionEvent arg0) {
							ClipboardTool cp = new ClipboardTool();
							cp.setClipboardContents(url);
						}
					});
					menu.show(label, me.getX(), me.getY());
				} catch (ApatarException ae) {
					ae.printStackTrace();
				}
			}
		}

	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

}
