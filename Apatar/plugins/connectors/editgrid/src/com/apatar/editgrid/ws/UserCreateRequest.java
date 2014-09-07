/**
 * UserCreateRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class UserCreateRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long			serialVersionUID	= 111824257643414765L;
	private java.lang.String			sessionKey;
	private java.lang.String			org;
	private com.apatar.editgrid.ws.User	body;

	public UserCreateRequest() {
	}

	public UserCreateRequest(java.lang.String sessionKey, java.lang.String org,
			com.apatar.editgrid.ws.User body) {
		this.sessionKey = sessionKey;
		this.org = org;
		this.body = body;
	}

	/**
	 * Gets the sessionKey value for this UserCreateRequest.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this UserCreateRequest.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the org value for this UserCreateRequest.
	 * 
	 * @return org
	 */
	public java.lang.String getOrg() {
		return org;
	}

	/**
	 * Sets the org value for this UserCreateRequest.
	 * 
	 * @param org
	 */
	public void setOrg(java.lang.String org) {
		this.org = org;
	}

	/**
	 * Gets the body value for this UserCreateRequest.
	 * 
	 * @return body
	 */
	public com.apatar.editgrid.ws.User getBody() {
		return body;
	}

	/**
	 * Sets the body value for this UserCreateRequest.
	 * 
	 * @param body
	 */
	public void setBody(com.apatar.editgrid.ws.User body) {
		this.body = body;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof UserCreateRequest)) {
			return false;
		}
		UserCreateRequest other = (UserCreateRequest) obj;
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
				&& ((org == null && other.getOrg() == null) || (org != null && org
						.equals(other.getOrg())))
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
		if (getOrg() != null) {
			_hashCode += getOrg().hashCode();
		}
		if (getBody() != null) {
			_hashCode += getBody().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			UserCreateRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "UserCreateRequest"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sessionKey");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sessionKey"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("org");
		elemField.setXmlName(new javax.xml.namespace.QName("", "org"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("body");
		elemField.setXmlName(new javax.xml.namespace.QName("", "body"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "User"));
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
