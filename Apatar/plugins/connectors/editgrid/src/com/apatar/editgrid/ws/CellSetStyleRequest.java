/**
 * CellSetStyleRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class CellSetStyleRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long				serialVersionUID	= -7693406593102071044L;
	private java.lang.String				sessionKey;
	private java.lang.String				workbook;
	private java.lang.String				range;
	private com.apatar.editgrid.ws.Style	body;

	public CellSetStyleRequest() {
	}

	public CellSetStyleRequest(java.lang.String sessionKey,
			java.lang.String workbook, java.lang.String range,
			com.apatar.editgrid.ws.Style body) {
		this.sessionKey = sessionKey;
		this.workbook = workbook;
		this.range = range;
		this.body = body;
	}

	/**
	 * Gets the sessionKey value for this CellSetStyleRequest.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this CellSetStyleRequest.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the workbook value for this CellSetStyleRequest.
	 * 
	 * @return workbook
	 */
	public java.lang.String getWorkbook() {
		return workbook;
	}

	/**
	 * Sets the workbook value for this CellSetStyleRequest.
	 * 
	 * @param workbook
	 */
	public void setWorkbook(java.lang.String workbook) {
		this.workbook = workbook;
	}

	/**
	 * Gets the range value for this CellSetStyleRequest.
	 * 
	 * @return range
	 */
	public java.lang.String getRange() {
		return range;
	}

	/**
	 * Sets the range value for this CellSetStyleRequest.
	 * 
	 * @param range
	 */
	public void setRange(java.lang.String range) {
		this.range = range;
	}

	/**
	 * Gets the body value for this CellSetStyleRequest.
	 * 
	 * @return body
	 */
	public com.apatar.editgrid.ws.Style getBody() {
		return body;
	}

	/**
	 * Sets the body value for this CellSetStyleRequest.
	 * 
	 * @param body
	 */
	public void setBody(com.apatar.editgrid.ws.Style body) {
		this.body = body;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof CellSetStyleRequest)) {
			return false;
		}
		CellSetStyleRequest other = (CellSetStyleRequest) obj;
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
				&& ((body == null && other.getBody() == null) || (body != null && body
						.equals(other.getBody())));
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
		if (getBody() != null) {
			_hashCode += getBody().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			CellSetStyleRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "CellSetStyleRequest"));
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
		elemField.setFieldName("body");
		elemField.setXmlName(new javax.xml.namespace.QName("", "body"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "Style"));
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
