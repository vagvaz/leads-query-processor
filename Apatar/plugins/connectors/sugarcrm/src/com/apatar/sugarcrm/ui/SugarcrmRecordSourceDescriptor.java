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

package com.apatar.sugarcrm.ui;

import java.util.List;

import javax.swing.DefaultBoundedRangeModel;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.RDBTable;
import com.apatar.sugarcrm.SugarcrmNode;
import com.apatar.ui.ProgressBarRelated;
import com.apatar.ui.ProgressBarWindows;
import com.apatar.ui.wizard.RecordSourceDescriptor;

public class SugarcrmRecordSourceDescriptor extends RecordSourceDescriptor
		implements ProgressBarRelated {

	boolean					isgetFields	= true;
	boolean					isCancel	= false;
	AbstractDataBaseNode	node;

	public SugarcrmRecordSourceDescriptor(AbstractDataBaseNode node,
			Object backDescriptor, Object nextDescriptor) {
		super(node, backDescriptor, nextDescriptor);
		this.node = node;
	}

	@Override
	public void aboutToDisplayPanel() {
		panel.clear();
		// try {
		isgetFields = true;
		isCancel = false;
		getWizard().setTitleComment("Record Source");
		getWizard().setAdditionalComment(
				"Provides information on which records should be returned");
		start(node, this);
		panel.setSelectedValue(node.getTable());
	}

	public void cancelProgress(Object obj) {
		isCancel = true;
	}

	public void stopProgress(Object obj) {
		isgetFields = false;
	}

	private void start(final AbstractDataBaseNode node,
			final ProgressBarRelated pbr) {
		Thread process = new Thread(new Runnable() {
			public void run() {
				ProgressBarWindows pw = new ProgressBarWindows(getWizard()
						.getDialog());
				pw.showProgressBarWindow(new DefaultBoundedRangeModel(0, 0, 0,
						100), pbr, "Verifying list of supported tables...",
						ProgressBarWindows.STOP_CANCEL_OPTION);
				List<RDBTable> tables = null;
				try {
					tables = node.getTableList();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				pw.setMaxValue(tables.size() - 1);

				int i = 1;
				for (RDBTable rtt : tables) {
					if (isgetFields) {
						try {
							if (((SugarcrmNode) node).getFields(rtt
									.getTableName()).length < 1) {
								rtt.setTableName(rtt.getTableName() + "*");
								rtt.setSupport(false);
							}
						} catch (Exception e) {
							// e.printStackTrace();
							rtt.setTableName(rtt.getTableName() + "*");
							rtt.setSupport(false);
						}
					} else {
						pw.hideProgressBarWindow();
					}
					panel.addTableName(rtt);
					pw.setInt(i++);

					if (isCancel) {
						break;
					}
				}
				try {
					pw.hideProgressBarWindow();
				} catch (RuntimeException e) {
				}
			}
		});
		process.start();
	}

}
