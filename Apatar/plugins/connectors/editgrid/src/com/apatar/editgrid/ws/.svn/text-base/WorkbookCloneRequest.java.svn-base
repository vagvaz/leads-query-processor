/**
 * WorkbookCloneRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class WorkbookCloneRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long				serialVersionUID	= 4497058007063461649L;
	private java.lang.String				sessionKey;
	private java.lang.String				src;
	private java.lang.String				workspace;
	private com.apatar.editgrid.ws.Workbook	body;

	public WorkbookCloneRequest() {
	}

	public WorkbookCloneRequest(java.lang.String sessionKey,
			java.lang.String src, java.lang.String workspace,
			com.apatar.editgrid.ws.Workbook body) {
		this.sessionKey = sessionKey;
		this.src = src;
		this.workspace = workspace;
		this.body = body;
	}

	/**
	 * Gets the sessionKey value for this WorkbookCloneRequest.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this WorkbookCloneRequest.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the src value for this WorkbookCloneRequest.
	 * 
	 * @return src
	 */
	public java.lang.String getSrc() {
		return src;
	}

	/**
	 * Sets the src value for this WorkbookCloneRequest.
	 * 
	 * @param src
	 */
	public void setSrc(java.lang.String src) {
		this.src = src;
	}

	/**
	 * Gets the workspace value for this WorkbookCloneRequest.
	 * 
	 * @return workspace
	 */
	public java.lang.String getWorkspace() {
		return workspace;
	}

	/**
	 * Sets the workspace value for this WorkbookCloneRequest.
	 * 
	 * @param workspace
	 */
	public void setWorkspace(java.lang.String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Gets the body value for this WorkbookCloneRequest.
	 * 
	 * @return body
	 */
	public com.apatar.editgrid.ws.Workbook getBody() {
		return body;
	}

	/**
	 * Sets the body value for this WorkbookCloneRequest.
	 * 
	 * @param body
	 */
	public void setBody(com.apatar.editgrid.ws.Workbook body) {
		this.body = body;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof WorkbookCloneRequest)) {
			return false;
		}
		WorkbookCloneRequest other = (WorkbookCloneRequest) obj;
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
				&& ((src == null && other.getSrc() == null) || (src != null && src
						.equals(other.getSrc())))
				&& ((workspace == null && other.getWorkspace() == null) || (workspace != null && workspace
						.equals(other.getWorkspace())))
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
		if (getSrc() != null) {
			_hashCode += getSrc().hashCode();
		}
		if (getWorkspace() != null) {
			_hashCode += getWorkspace().hashCode();
		}
		if (getBody() != null) {
			_hashCode += getBody().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			WorkbookCloneRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "WorkbookCloneRequest"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sessionKey");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sessionKey"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("src");
		elemField.setXmlName(new javax.xml.namespace.QName("", "src"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("workspace");
		elemField.setXmlName(new javax.xml.namespace.QName("", "workspace"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("body");
		elemField.setXmlName(new javax.xml.namespace.QName("", "body"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "Workbook"));
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
