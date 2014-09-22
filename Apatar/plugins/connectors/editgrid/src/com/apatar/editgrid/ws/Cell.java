/**
 * Cell.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class Cell implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4389259910840163206L;
	private java.lang.String	sheet;
	private java.lang.Integer	col;
	private java.lang.Integer	row;
	private java.lang.String	input;
	private java.lang.String	display;
	private java.lang.String	value;

	public Cell() {
	}

	public Cell(java.lang.String sheet, java.lang.Integer col,
			java.lang.Integer row, java.lang.String input,
			java.lang.String display, java.lang.String value) {
		this.sheet = sheet;
		this.col = col;
		this.row = row;
		this.input = input;
		this.display = display;
		this.value = value;
	}

	/**
	 * Gets the sheet value for this Cell.
	 * 
	 * @return sheet
	 */
	public java.lang.String getSheet() {
		return sheet;
	}

	/**
	 * Sets the sheet value for this Cell.
	 * 
	 * @param sheet
	 */
	public void setSheet(java.lang.String sheet) {
		this.sheet = sheet;
	}

	/**
	 * Gets the col value for this Cell.
	 * 
	 * @return col
	 */
	public java.lang.Integer getCol() {
		return col;
	}

	/**
	 * Sets the col value for this Cell.
	 * 
	 * @param col
	 */
	public void setCol(java.lang.Integer col) {
		this.col = col;
	}

	/**
	 * Gets the row value for this Cell.
	 * 
	 * @return row
	 */
	public java.lang.Integer getRow() {
		return row;
	}

	/**
	 * Sets the row value for this Cell.
	 * 
	 * @param row
	 */
	public void setRow(java.lang.Integer row) {
		this.row = row;
	}

	/**
	 * Gets the input value for this Cell.
	 * 
	 * @return input
	 */
	public java.lang.String getInput() {
		return input;
	}

	/**
	 * Sets the input value for this Cell.
	 * 
	 * @param input
	 */
	public void setInput(java.lang.String input) {
		this.input = input;
	}

	/**
	 * Gets the display value for this Cell.
	 * 
	 * @return display
	 */
	public java.lang.String getDisplay() {
		return display;
	}

	/**
	 * Sets the display value for this Cell.
	 * 
	 * @param display
	 */
	public void setDisplay(java.lang.String display) {
		this.display = display;
	}

	/**
	 * Gets the value value for this Cell.
	 * 
	 * @return value
	 */
	public java.lang.String getValue() {
		return value;
	}

	/**
	 * Sets the value value for this Cell.
	 * 
	 * @param value
	 */
	public void setValue(java.lang.String value) {
		this.value = value;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Cell)) {
			return false;
		}
		Cell other = (Cell) obj;
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((sheet == null && other.getSheet() == null) || (sheet != null && sheet
						.equals(other.getSheet())))
				&& ((col == null && other.getCol() == null) || (col != null && col
						.equals(other.getCol())))
				&& ((row == null && other.getRow() == null) || (row != null && row
						.equals(other.getRow())))
				&& ((input == null && other.getInput() == null) || (input != null && input
						.equals(other.getInput())))
				&& ((display == null && other.getDisplay() == null) || (display != null && display
						.equals(other.getDisplay())))
				&& ((value == null && other.getValue() == null) || (value != null && value
						.equals(other.getValue())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean	__hashCodeCalc	= false;

	@Override
	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getSheet() != null) {
			_hashCode += getSheet().hashCode();
		}
		if (getCol() != null) {
			_hashCode += getCol().hashCode();
		}
		if (getRow() != null) {
			_hashCode += getRow().hashCode();
		}
		if (getInput() != null) {
			_hashCode += getInput().hashCode();
		}
		if (getDisplay() != null) {
			_hashCode += getDisplay().hashCode();
		}
		if (getValue() != null) {
			_hashCode += getValue().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			Cell.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "Cell"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sheet");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sheet"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("col");
		elemField.setXmlName(new javax.xml.namespace.QName("", "col"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("row");
		elemField.setXmlName(new javax.xml.namespace.QName("", "row"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("input");
		elemField.setXmlName(new javax.xml.namespace.QName("", "input"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("display");
		elemField.setXmlName(new javax.xml.namespace.QName("", "display"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("value");
		elemField.setXmlName(new javax.xml.namespace.QName("", "value"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 */
	public static org.apache.axis.encoding.Serializer getSerializer(
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType,
				_xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType,
				_xmlType, typeDesc);
	}

}
