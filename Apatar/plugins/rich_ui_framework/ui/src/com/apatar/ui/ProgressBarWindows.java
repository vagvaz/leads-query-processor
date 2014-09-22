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

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressBarWindows extends JDialog {

	public static int			CANCEL_OPTION		= 0;
	public static int			STOP_OPTION			= 0;
	public static int			STOP_CANCEL_OPTION	= 0;

	private BoundedRangeModel	model;

	JDialog						parent;

	// private ProgressBarRelated pbr;

	public ProgressBarWindows(JDialog parent) throws HeadlessException {
		super(parent);
		this.parent = parent;
		// this.pbr = pbr;
	}

	public void showProgressBarWindow(BoundedRangeModel m,
			final ProgressBarRelated pbr, final String title, final int option) {
		model = m;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JProgressBar progress = new JProgressBar(model);
				progress.setStringPainted(true);
				JPanel progressPanel = new JPanel();
				progressPanel.add(progress);

				if (option == CANCEL_OPTION || option == STOP_CANCEL_OPTION) {
					JButton cancel = new JButton("Cancel");
					progressPanel.add(cancel);

					cancel.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							pbr.cancelProgress(null);
							// hideProgressBarWindow();
						}

					});
				}
				if (option == STOP_OPTION || option == STOP_CANCEL_OPTION) {
					JButton stop = new JButton("Stop");
					progressPanel.add(stop);

					stop.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							pbr.stopProgress(null);
							// hideProgressBarWindow();
						}

					});
				}

				// dialog = new JDialog();
				setModal(true);
				setTitle(title);
				// setUndecorated(true);

				getContentPane().add(progressPanel);
				setSize(300, 60);
				setLocationRelativeTo(parent);
				setVisible(true);
			}
		});
	}

	public void hideProgressBarWindow() {
		dispose();
		model = null;
	}

	public void setInt(final int value) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (model != null) {
					model.setValue(value);
				}
				/*
				 * if(value == 101) hideProgressBarWindow();
				 */
			}
		});
	}

	public void setMaxValue(final int value) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				model.setMaximum(value);
			}
		});
	}

}