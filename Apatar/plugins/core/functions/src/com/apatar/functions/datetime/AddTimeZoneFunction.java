package com.apatar.functions.datetime;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;
import com.apatar.ui.UiUtils;

public class AddTimeZoneFunction extends AbstractApatarFunction {

	private int value;

	public Object execute(List l) {
		Object inputValue = l.get(0);
		if (inputValue instanceof Date) {
			try {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime((Date) inputValue);
				calendar.add(11, getValue());
				return new Timestamp(calendar.getTimeInMillis());
			} catch (Exception e) {
				return inputValue;
			}
		} else {
			return inputValue;
		}
	}

	static FunctionInfo fi = new FunctionInfo("Add TimeZone", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.Date_and_Time);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
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

	public ImageIcon getIcon() {
		return UiUtils.DATE_COLUMN_NODE_ICON;
	}

	@Override
	public void initFromElement(Element e) {
		super.initFromElement(e);
		value = Integer.parseInt(e.getAttributeValue("value"));
	}

	@Override
	public Element saveToElement() {
		Element rv = super.saveToElement();
		rv.setAttribute("value", "" + value);
		return rv;
	}
}
