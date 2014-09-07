/**
 * Style.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class Style implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -320980311279992670L;
	private java.lang.String	fontname;
	private int					fontsize;
	private java.lang.String	fontcolor;
	private java.lang.String	bgcolor;

	public Style() {
	}

	public Style(java.lang.String fontname, int fontsize,
			java.lang.String fontcolor, java.lang.String bgcolor) {
		this.fontname = fontname;
		this.fontsize = fontsize;
		this.fontcolor = fontcolor;
		this.bgcolor = bgcolor;
	}

	/**
	 * Gets the fontname value for this Style.
	 * 
	 * @return fontname
	 */
	public java.lang.String getFontname() {
		return fontname;
	}

	/**
	 * Sets the fontname value for this Style.
	 * 
	 * @param fontname
	 */
	public void setFontname(java.lang.String fontname) {
		this.fontname = fontname;
	}

	/**
	 * Gets the fontsize value for this Style.
	 * 
	 * @return fontsize
	 */
	public int getFontsize() {
		return fontsize;
	}

	/**
	 * Sets the fontsize value for this Style.
	 * 
	 * @param fontsize
	 */
	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

	/**
	 * Gets the fontcolor value for this Style.
	 * 
	 * @return fontcolor
	 */
	public java.lang.String getFontcolor() {
		return fontcolor;
	}

	/**
	 * Sets the fontcolor value for this Style.
	 * 
	 * @param fontcolor
	 */
	public void setFontcolor(java.lang.String fontcolor) {
		this.fontcolor = fontcolor;
	}

	/**
	 * Gets the bgcolor value for this Style.
	 * 
	 * @return bgcolor
	 */
	public java.lang.String getBgcolor() {
		return bgcolor;
	}

	/**
	 * Sets the bgcolor value for this Style.
	 * 
	 * @param bgcolor
	 */
	public void setBgcolor(java.lang.String bgcolor) {
		this.bgcolor = bgcolor;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Style)) {
			return false;
		}
		Style other = (Style) obj;
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
				&& ((fontname == null && other.getFontname() == null) || (fontname != null && fontname
						.equals(other.getFontname())))
				&& fontsize == other.getFontsize()
				&& ((fontcolor == null && other.getFontcolor() == null) || (fontcolor != null && fontcolor
						.equals(other.getFontcolor())))
				&& ((bgcolor == null && other.getBgcolor() == null) || (bgcolor != null && bgcolor
						.equals(other.getBgcolor())));
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
		if (getFontname() != null) {
			_hashCode += getFontname().hashCode();
		}
		_hashCode += getFontsize();
		if (getFontcolor() != null) {
			_hashCode += getFontcolor().hashCode();
		}
		if (getBgcolor() != null) {
			_hashCode += getBgcolor().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			Style.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "Style"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("fontname");
		elemField.setXmlName(new javax.xml.namespace.QName("", "fontname"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("fontsize");
		elemField.setXmlName(new javax.xml.namespace.QName("", "fontsize"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("fontcolor");
		elemField.setXmlName(new javax.xml.namespace.QName("", "fontcolor"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("bgcolor");
		elemField.setXmlName(new javax.xml.namespace.QName("", "bgcolor"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
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
