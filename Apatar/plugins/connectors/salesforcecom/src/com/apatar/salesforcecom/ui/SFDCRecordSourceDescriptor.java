/**
 *
 */
package com.apatar.salesforcecom.ui;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.apatar.core.AbstractDataBaseNode;
import com.apatar.core.LogUtils;
import com.apatar.core.RDBTable;
import com.apatar.salesforcecom.SalesforcecomNode;
import com.apatar.ui.ApatarUiMain;
import com.apatar.ui.wizard.JdbcRecordSourceDescriptor;
import com.apatar.ui.wizard.Wizard;

/**
 * @author Admin
 * 
 */
public class SFDCRecordSourceDescriptor extends JdbcRecordSourceDescriptor {

	public SFDCRecordSourceDescriptor(AbstractDataBaseNode node,
			Object backDescriptor, Object nextDescriptor) {
		super(node, backDescriptor, nextDescriptor);
	}

	@Override
	public void aboutToDisplayPanel() {
		getPanel().clear();
		try {

			for (RDBTable rtt : getNode().getTableList()) {
				getPanel().addTableName(rtt);
			}

			SalesforcecomNode sfdcNode = (SalesforcecomNode) getNode();
			getPanel().setSelectedValue(getNode().getTable());
			if (!sfdcNode.getSoqlQuery().equals("")) {
				getPanel().setSqlQuery(sfdcNode.getSoqlQuery());
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, LogUtils
					.GetExceptionMessage(e));
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, LogUtils
					.GetExceptionMessage(e));
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWizard().setTitleComment("Record Source");
		getWizard().setAdditionalComment(
				"Provides information on which records should be returned");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.apatar.ui.wizard.JdbcRecordSourceDescriptor#aboutToHidePanel(java
	 * .lang.String)
	 */
	@Override
	public int aboutToHidePanel(String actionCommand) {
		if (actionCommand.equals(Wizard.NEXT_BUTTON_ACTION_COMMAND)) {
			try {
				RDBTable table = getPanel().getSelectedValue();
				if (table == null) {
					JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
							"Please, select table name");
					return LEAVE_CURRENT_PANEL;
				}

				if (!table.isSupport()) {
					JOptionPane
							.showMessageDialog(
									getWizard().getDialog(),
									"Apatar is unable to get fields list of the selected table. Please select another table to proceed.");
					return LEAVE_CURRENT_PANEL;
				}
				getNode().setTable(table);
				SalesforcecomNode sfdcNode = (SalesforcecomNode) getNode();
				sfdcNode.setSoqlQuery(getPanel().getSqlQuery().getText());

			} catch (Exception e) {
				JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, e
						.getMessage());
				return LEAVE_CURRENT_PANEL;
			}
		}
		return CHANGE_PANEL;

	}

}
