package com.apatar.functions.constant;

import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.ERecordType;
import com.apatar.functions.ConstantFunctionInfo;
import com.apatar.ui.FunctionCategory;
import com.apatar.ui.UiUtils;

public class LimitFunction extends AbstractConstantApatarFunction {
	long value;

	public Object execute(List l) {
		return value;
	}

	static ConstantFunctionInfo fi = new ConstantFunctionInfo("Limit Value", 0, 1, ERecordType.Numeric);
	static
	{
		//fi.getCategories().add(FunctionCategory.Constant);@@
		fi.getCategories().add(FunctionCategory.Limit);
	}

	public ConstantFunctionInfo getFunctionInfo() {
		return fi;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public boolean isEditable()
	{
		return true;
	}

	public ImageIcon getIcon() {
		return UiUtils.NUMERIC_COLUMN_NODE_ICON;
	}

	public void initFromElement(Element e) {
		super.initFromElement(e);
		value = Long.parseLong(e.getAttributeValue("value"));
	}

	public Element saveToElement() {
		Element rv = super.saveToElement();
		rv.setAttribute("value", ""+value);
		return rv;
	}
}
