package com.apatar.ui.schematable;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class ApatarDefaultCellEditor extends DefaultCellEditor {

	public ApatarDefaultCellEditor(JTextField textField) {
		super(textField);
	}

	public ApatarDefaultCellEditor(JComboBox comboBox) {
		super(comboBox);
	}

}
