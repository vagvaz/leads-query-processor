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

package com.apatar.distinct.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.apatar.core.ApplicationData;
import com.apatar.core.TableInfo;
import com.apatar.distinct.DistinctNode;
import com.apatar.ui.JDefaultContextMenu;
import com.apatar.ui.MouseHyperLinkEvent;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.UiUtils;

public class JDistinctDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5422763355632834805L;

	DistinctNode node;

	JList list;

	private JButton ok;

	private JButton cancel;
	
    public static int OK_OPTION = 1;
    public static int CANCEL_OPTION = 0;
    
    int option = CANCEL_OPTION;
	
	private JLabel keyForReferringToDescriptionLabel;
    public void setKeyForReferringToDescription(String keyForReferringToDescription) {
    	String url = ApplicationData.getGadgetHelpProperty(keyForReferringToDescription);
    	//keyForReferringToDescriptionLabel.setText("<html><a href='" + url + "'>View operation guide</a></html>");
    }

	public JDistinctDialog(final DistinctNode node) throws HeadlessException {

		super(ApatarUiMain.MAIN_FRAME, "Distinct");
		this.node = node;
		setModal(true);
		setSize(300, 250);
		setLayout(new BorderLayout());

		String titles[] = {};

		TableInfo info = node.getTiForConnection(DistinctNode.INPUT_CONN_POINT);
		if (info != null) {
			int count = info.getSchemaTable().getRecords().size();
			titles = new String[count];
			for (int i = 0; i < count; i++) {
				titles[i] = info.getSchemaTable().getRecords().get(i).getFieldName();
			}
		}
		list = new JList(titles);
		list.setComponentPopupMenu( new JDefaultContextMenu(list) );
		JScrollPane pane = new JScrollPane(list);
		getContentPane().add(pane, BorderLayout.CENTER);

		List<String> selectedFields = node.getSelectedFields();
		int indices[] = null;
		if (selectedFields != null) {
			indices = new int[selectedFields.size()];
			Iterator it = selectedFields.iterator();

			int i = 0;
			while (it.hasNext()) {
				String columnName = it.next().toString();
				for (int k = 0; k < titles.length; k++) {
					if (columnName.equals(titles[k])) {
						indices[i] = k;
					}
				}
				i++;

			}
			list.setSelectedIndices(indices);
		}
		ok = new JButton("Ok");
		cancel = new JButton("Cancel");

		ok.setPreferredSize(cancel.getPreferredSize());
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				List<String> selectedFields = new ArrayList<String>();

				int[] selectedIndices = list.getSelectedIndices();
				Object[] selectedValues = (Object[]) list.getSelectedValues();
				for (int i = 0; i < selectedIndices.length; i++) {
					selectedFields.add(selectedValues[i].toString());
				}
				node.setSelectedFields(selectedFields);
				
				option = OK_OPTION;
				setVisible(false);
				//dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option = CANCEL_OPTION;
				setVisible(false);
				//dispose();
			}
		});

		JPanel buttonPanel = new JPanel();
		JSeparator separator = new JSeparator();
		Box buttonBox = new Box(BoxLayout.X_AXIS);

		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(separator, BorderLayout.NORTH);

		buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		
		keyForReferringToDescriptionLabel = new JLabel();
        keyForReferringToDescriptionLabel.setFont( UiUtils.NORMAL_SIZE_12_FONT );
        keyForReferringToDescriptionLabel.addMouseListener( new MouseHyperLinkEvent() );
        keyForReferringToDescriptionLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        
        buttonBox.add(keyForReferringToDescriptionLabel);
        buttonBox.add(Box.createHorizontalStrut(20));
		
		buttonBox.add(ok);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(cancel);

		buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	}
	
	public static int showDialog(final DistinctNode node) {
		JDistinctDialog dlg = new JDistinctDialog(node);
		dlg.setKeyForReferringToDescription("help.operation.distinct");
		dlg.setVisible(true);
		dlg.dispose();
		return dlg.option;
	}

}
