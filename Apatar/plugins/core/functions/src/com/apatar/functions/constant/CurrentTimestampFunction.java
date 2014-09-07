package com.apatar.functions.constant;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.ERecordType;
import com.apatar.functions.ConstantFunctionInfo;
import com.apatar.ui.FunctionCategory;
import com.apatar.ui.UiUtils;

public class CurrentTimestampFunction extends AbstractConstantApatarFunction {
	private int	value;

	public Object execute(List l) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.HOUR_OF_DAY, value);
		return new Timestamp(cal.getTimeInMillis());
	}

	static ConstantFunctionInfo	fi	= new ConstantFunctionInfo(
											"Current Timestamp Constant", 0, 1,
											ERecordType.Timestamp);
	static {
		fi.getCategories().add(FunctionCategory.Constant);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	@Override
	public ConstantFunctionInfo getFunctionInfo() {
		return fi;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public ImageIcon getIcon() {
		return UiUtils.DATETIME_COLUMN_NODE_ICON;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		value = Integer.valueOf(e.getAttributeValue("value"));
	}

	@Override
	public Element saveToElement() {
		Element rv = super.saveToElement();
		rv.setAttribute("value", "" + String.valueOf(value));
		return rv;
	}
}
