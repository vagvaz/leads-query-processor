

package com.apatar.functions.constant;

import java.util.List;

import javax.swing.ImageIcon;

import org.jdom.Element;

import com.apatar.core.ERecordType;
import com.apatar.functions.ConstantFunctionInfo;
import com.apatar.ui.FunctionCategory;
import com.apatar.ui.UiUtils;

public class ReducerFunction extends AbstractConstantApatarFunction {

	String value = "";
	
	public Object execute(List l) {
		return value;
	}

	static ConstantFunctionInfo fi = new ConstantFunctionInfo("Reducer", 0, 1, ERecordType.Text);
	static
	{
		//fi.getCategories().add(FunctionCategory.Constant);
		fi.getCategories().add(FunctionCategory.Map_Reduce);
	}
	
	public ConstantFunctionInfo getFunctionInfo() {
		return fi;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isEditable()
	{
		return true;
	}
	
	public ImageIcon getIcon() {
		return UiUtils.TEXT_COLUMN_NODE_ICON;
	}
	
	public void initFromElement(Element e) {
		super.initFromElement(e);
		value = e.getAttributeValue("value");
	}

	public Element saveToElement() {
		Element rv = super.saveToElement();
		rv.setAttribute("value", value);
		return rv;
	}
	
}
