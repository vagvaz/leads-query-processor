/*TODO recorded refactoring
 * класс JProgressDialog перемещён в пакет UI. Ранее в этом пакете уже был класс с таким же именем, но он не использовался и был удалён
 * *********************
 */

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

package com.apatar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import com.apatar.core.ApplicationData;
import com.apatar.core.IProcessingProgress;
import com.apatar.core.LogUtils;
import com.apatar.core.OperationalNode;
import com.apatar.core.TableInfo;

public class JProgressDialog extends JDialog implements IProcessingProgress {
	/**
	 *
	 */
	private static final long serialVersionUID = -1813294439933022740L;

	int iRecordsProcessed = 0;

	JTextArea lbl = null;

	JButton cancel = new JButton("Cancel");
	JButton close = new JButton("Close");

	Boolean cancelMode = false;

	OperationalNode debugNode;
	ArrayList<String> output = new ArrayList<String>();

	// if the debug node is passed to the dialog then this dialog is not
	// autoclose dialog
	// if there is no debug node passed then this dialog is autoclose

	public JProgressDialog(OperationalNode debugNode) {
		super(ApatarUiMain.MAIN_FRAME);
		// the node inside the dialog
		// the better solution can be found
		// but it will require good thread synchronization
		this.debugNode = debugNode;

		setSize(new Dimension(500, 500));
		setLayout(new BorderLayout(3, 3));

		lbl = new JTextArea();
		lbl.setSize(new Dimension(500, 400));
		getContentPane().add(new JScrollPane(lbl), BorderLayout.CENTER);

		// add panel for buttons
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout(3, 3));
		getContentPane().add(container, BorderLayout.SOUTH);

		container.add(cancel, BorderLayout.CENTER);

		synchronized (output) {
			output.add("Progress:");
		}

		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				cancelMode = true;
			}
		});

		if (debugNode != null) {
			// hide dialog by the close button
			// but don't cacnel or show preview until everything completes
			container.add(close, BorderLayout.WEST);
			close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					cancelMode = true;
					setVisible(false);
				}
			});

		}

		Timer tm = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateText();
			}
		});
		tm.start();
	}

	public void Finish() {
		this.Log("Finish");

		if (debugNode != null) {
			// if cancel was clicked don't show any preview
			if (!cancelMode) {
				TableInfo ti = debugNode.getDebugTableInfo();
				if (null == ti) {
					System.err.println("nothing to output");
					return;
				}
				if (!ApplicationData.SILENT_RUN) {
					JTablePreviewDialog jpp = new JTablePreviewDialog(
							ApplicationData.getTempJDBC(), ti.getTableName(),
							ApplicationData.getTempDataBaseInfo());
					jpp.setVisible(true);
				} else {
					System.out
							.println("Command line parameter `silent_run` is set. Do not open window JTablePreviewDialog.");
				}
			}
		} else {
			// if this is autoclose dialog then just close it
			setVisible(false);
		}
	}

	// ****************************************************************************
	// Progress log functions
	// ****************************************************************************
	public Boolean Log(String message) {
		synchronized (output) {
			output.add(message);
		}
		return !cancelMode;
	}

	public void Reset() {
		iRecordsProcessed = 0;
	}

	public Boolean Step() {
		synchronized (output) {
			if (output.get(output.size() - 1).contains("Records Processed ")) {
				output.remove(output.size() - 1);
			}
			output.add(String.format("Records Processed %s",
					++iRecordsProcessed));
		}
		return !cancelMode;
	}

	public void NodeEnter() {
		iRecordsProcessed = 0;
		synchronized (output) {
			output
					.add(String.format("Records Processed %s",
							iRecordsProcessed));
		}
	}

	protected void updateText() {
		String outStr = "";
		synchronized (output) {
			int iStart = output.size() - 1000;
			for (int i = iStart > 0 ? iStart : 0; i < output.size(); i++) {
				outStr += output.get(i) + '\n';
			}
		}

		lbl.setText(outStr);
	}

	public boolean Status() {
		return !cancelMode;
	}

	public boolean Log(Exception e) {
		e.printStackTrace();
		return this.Log(LogUtils.GetExceptionMessage(e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean arg0) {
		if (ApplicationData.SILENT_RUN) {
			System.out
					.println("Command line parameter `silent_run` is set. Do not open window JProgressDialog.");
			super.setVisible(false);
		} else {
			super.setVisible(arg0);
		}
	}
}
