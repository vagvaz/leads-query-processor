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

package propertysheet;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.apatar.core.ApplicationData;
import com.apatar.core.ProjectData;

public class JPropertySheetPage extends JPanel {
	private static final long serialVersionUID = 1L;

	protected JComboBox existentProjectDataList;
	protected JConnectionPanel panel = new JConnectionPanel();
	protected JButton buttonNew;
	protected JButton buttonDelete;

	protected String dataType;
	protected String dataSubType;
	protected Class classType;

	protected JDialog owner = null;

	protected ProjectData currentProjectData;

	public long getDataId() {
		return currentProjectData.getId();
	}

	public JPropertySheetPage(JDialog ownerDialog) {
		super();
		owner = ownerDialog;
	}

	public ProjectData getProjectData() {
		return currentProjectData;
	}

	public ProjectData init(long dataId, Class classType, String type,
			String subtype) throws Exception {
		dataType = type;
		dataSubType = subtype;
		this.classType = classType;

		Map existData = ApplicationData.getProject().getProjectDatas(dataType,
				dataSubType);
		// if data of this type don't exists at all
		if (dataId == -1) {
			if (existData.size() > 0) {
				currentProjectData = (ProjectData) existData.values().toArray()[0];
			} else {
				currentProjectData = new ProjectData(type, subtype,
						"default connection", classType.newInstance());
				dataId = currentProjectData.getId();
				ApplicationData.getProject().addProjectData(currentProjectData);
			}
		} else {
			currentProjectData = ApplicationData.getProject().getProjectData(
					dataId);
			if (currentProjectData.getData() == null) {
				currentProjectData.setData(classType.newInstance());
				currentProjectData.setType(type);
				currentProjectData.setSubType(subtype);
			}
		}

		existData = ApplicationData.getProject().getProjectDatas(
				currentProjectData.getType(), currentProjectData.getSubType());

		existentProjectDataList = new JComboBox(existData.values().toArray());
		createComponent();
		return currentProjectData;
	}

	protected void createComponent() {
		setLayout(new BorderLayout(5, 5));
		add(existentProjectDataList, BorderLayout.NORTH);

		buttonNew = new JButton("New Connection");
		buttonNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					JPropertyNameDialog dlg = new JPropertyNameDialog(owner,
							"Enter property names", true);
					dlg.setVisible(true);
					if (dlg.isOk()) {
						ProjectData prjData = new ProjectData(dataType,
								dataSubType, "", classType.newInstance());
						prjData.setName(dlg.getNameConnector());
						ApplicationData.getProject().addProjectData(prjData);

						existentProjectDataList.addItem(prjData);
						existentProjectDataList.setSelectedItem(prjData);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		buttonDelete = new JButton("Delete Connection");
		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);

				// shouldn't delete the last one
				// !!! remove this condtion and ask Valery Shikunets to fix all
				// the bugs
				// about this (2 - 3 days of bugs)
				if (existentProjectDataList.getItemCount() > 1) {
					ApplicationData.getProject().removeProjectData(
							currentProjectData);
					existentProjectDataList.removeItem(currentProjectData);
				}
			}
		});

		GridLayout buttonGrid = new GridLayout(1, 2, 10, 10);
		JPanel panelButton = new JPanel(buttonGrid);
		panelButton.add(buttonNew);
		panelButton.add(buttonDelete);
		add(panelButton, BorderLayout.SOUTH);

		add(panel, BorderLayout.CENTER);

		// is called when combob box item is selected (100% of Valery Shikunets)
		existentProjectDataList.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				Object item = existentProjectDataList.getSelectedItem();

				if (item instanceof ProjectData) {
					ProjectData prjData = (ProjectData) existentProjectDataList
							.getSelectedItem();
					currentProjectData = prjData;
					panel.updateBean(prjData.getData());
				}
			}
		});

		existentProjectDataList.setSelectedItem(currentProjectData);
		panel.updateBean(currentProjectData.getData());
	}

}
