/**
 * CellSetSizeRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class CellSetSizeRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8133203016604357351L;
	private java.lang.String	sessionKey;
	private java.lang.String	workbook;
	private java.lang.String	range;
	private java.lang.Integer	width;
	private java.lang.Integer	height;

	public CellSetSizeRequest() {
	}

	public CellSetSizeRequest(java.lang.String sessionKey,
			java.lang.String workbook, java.lang.String range,
			java.lang.Integer width, java.lang.Integer height) {
		this.sessionKey = sessionKey;
		this.workbook = workbook;
		this.range = range;
		this.width = width;
		this.height = height;
	}

	/**
	 * Gets the sessionKey value for this CellSetSizeRequest.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this CellSetSizeRequest.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the workbook value for this CellSetSizeRequest.
	 * 
	 * @return workbook
	 */
	public java.lang.String getWorkbook() {
		return workbook;
	}

	/**
	 * Sets the workbook value for this CellSetSizeRequest.
	 * 
	 * @param workbook
	 */
	public void setWorkbook(java.lang.String workbook) {
		this.workbook = workbook;
	}

	/**
	 * Gets the range value for this CellSetSizeRequest.
	 * 
	 * @return range
	 */
	public java.lang.String getRange() {
		return range;
	}

	/**
	 * Sets the range value for this CellSetSizeRequest.
	 * 
	 * @param range
	 */
	public void setRange(java.lang.String range) {
		this.range = range;
	}

	/**
	 * Gets the width value for this CellSetSizeRequest.
	 * 
	 * @return width
	 */
	public java.lang.Integer getWidth() {
		return width;
	}

	/**
	 * Sets the width value for this CellSetSizeRequest.
	 * 
	 * @param width
	 */
	public void setWidth(java.lang.Integer width) {
		this.width = width;
	}

	/**
	 * Gets the height value for this CellSetSizeRequest.
	 * 
	 * @return height
	 */
	public java.lang.Integer getHeight() {
		return height;
	}

	/**
	 * Sets the height value for this CellSetSizeRequest.
	 * 
	 * @param height
	 */
	public void setHeight(java.lang.Integer height) {
		this.height = height;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof CellSetSizeRequest)) {
			return false;
		}
		CellSetSizeRequest other = (CellSetSizeRequest) obj;
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
				&& ((sessionKey == null && other.getSessionKey() == null) || (sessionKey != null && sessionKey
						.equals(other.getSessionKey())))
				&& ((workbook == null && other.getWorkbook() == null) || (workbook != null && workbook
						.equals(other.getWorkbook())))
				&& ((range == null && other.getRange() == null) || (range != null && range
						.equals(other.getRange())))
				&& ((width == null && other.getWidth() == null) || (width != null && width
						.equals(other.getWidth())))
				&& ((height == null && other.getHeight() == null) || (height != null && height
						.equals(other.getHeight())));
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
		if (getSessionKey() != null) {
			_hashCode += getSessionKey().hashCode();
		}
		if (getWorkbook() != null) {
			_hashCode += getWorkbook().hashCode();
		}
		if (getRange() != null) {
			_hashCode += getRange().hashCode();
		}
		if (getWidth() != null) {
			_hashCode += getWidth().hashCode();
		}
		if (getHeight() != null) {
			_hashCode += getHeight().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			CellSetSizeRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "CellSetSizeRequest"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sessionKey");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sessionKey"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("workbook");
		elemField.setXmlName(new javax.xml.namespace.QName("", "workbook"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("range");
		elemField.setXmlName(new javax.xml.namespace.QName("", "range"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("width");
		elemField.setXmlName(new javax.xml.namespace.QName("", "width"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("height");
		elemField.setXmlName(new javax.xml.namespace.QName("", "height"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
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
