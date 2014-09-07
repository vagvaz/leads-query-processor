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

package com.apatar.strikeiron.ui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import com.apatar.core.DataTransNode;
import com.apatar.strikeiron.Strikeiron;
import com.apatar.strikeiron.StrikeironNode;
import com.apatar.ui.JSubProjectDialog;
import com.apatar.ui.NodeFactory;

public class JStrikeironDialog extends JSubProjectDialog {

	String				messageForButtonDelay	= "Clear";
	String				messageForDelayDialog;
	String				titleForDelayDialog;

	StrikeironToolBar	toolBar					= new StrikeironToolBar(this);

	public JStrikeironDialog(JDialog owner, String name, DataTransNode node,
			String[] inputTableList, List<NodeFactory> utilities, Boolean tabbed)
			throws HeadlessException {
		super(owner, name, node, inputTableList, utilities, tabbed);
		getContentPane().add(toolBar, BorderLayout.NORTH);
	}

	public JStrikeironDialog(JFrame owner, String name, DataTransNode node,
			String[] inputTableList, List<NodeFactory> utilities, Boolean tabbed)
			throws HeadlessException {
		super(owner, name, node, inputTableList, utilities, tabbed);
		getContentPane().add(toolBar, BorderLayout.NORTH);
	}

	public class StrikeironToolBar extends JToolBar {

		JStrikeironDialog	owner;

		JButton				delay	= new JButton(messageForButtonDelay);
		JButton				login	= new JButton("Change Account");

		public StrikeironToolBar(final JStrikeironDialog owner) {
			super();
			this.owner = owner;

			getContentPane().add(this, BorderLayout.NORTH);
			add(login);
			add(delay);

			login.addMouseListener(mouseListener);
			login.setBorderPainted(false);
			delay.addMouseListener(mouseListener);
			delay.setBorderPainted(false);

			login.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JLoginDialog dlg = new JLoginDialog((Strikeiron) node,
							owner);
					dlg.setVisible(true);
				}
			});

			delay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JDelayForClearDialog dlg = new JDelayForClearDialog(owner,
							titleForDelayDialog, true);
					dlg.setMessage(messageForDelayDialog);
					dlg.setVisible(true);
					if (dlg.option == JDelayForClearDialog.OK_OPTION) {
						((StrikeironNode) node).setDelay(dlg.getDalay());
					}
				}
			});
		}

		public void setMessageForButtonDelay(String messageForButtonDelay) {
			delay.setText(messageForButtonDelay);
		}

		public MouseListener	mouseListener	= new MouseListener() {

													public void mouseClicked(
															MouseEvent arg0) {
													}

													public void mousePressed(
															MouseEvent arg0) {
													}

													public void mouseReleased(
															MouseEvent arg0) {
													}

													public void mouseEntered(
															MouseEvent e) {
														((JButton) e
																.getComponent())
																.setBorderPainted(true);
													}

													public void mouseExited(
															MouseEvent e) {
														((JButton) e
																.getComponent())
																.setBorderPainted(false);
													}
												};
	}

	public void setMessageForButtonDelay(String messageForButtonDelay) {
		toolBar.setMessageForButtonDelay(messageForButtonDelay);
	}

	public void setMessageForDelayDialog(String messageForDelayDialog) {
		this.messageForDelayDialog = messageForDelayDialog;
	}

	public void setTitleForDelayDialog(String titleForDelayDialog) {
		this.titleForDelayDialog = titleForDelayDialog;
	}

	public static int showDialog(JFrame owner, String name, DataTransNode node,
			String[] inputTableList, List<NodeFactory> utilities,
			Boolean tabbed, String messageForButtonDelay,
			String titleForDelayDialog, String messageForDelayDialog,
			String keyForReferringToDescription) {
		JStrikeironDialog dlg = new JStrikeironDialog(owner, name, node,
				inputTableList, utilities, tabbed);
		dlg.setMessageForButtonDelay(messageForButtonDelay);
		dlg.setTitleForDelayDialog(titleForDelayDialog);
		dlg.setMessageForDelayDialog(messageForDelayDialog);
		dlg.setKeyForReferringToDescription(keyForReferringToDescription);
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option;
	}

}
