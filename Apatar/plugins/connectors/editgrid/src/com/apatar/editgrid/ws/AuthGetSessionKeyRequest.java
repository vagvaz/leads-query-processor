/**
 * AuthGetSessionKeyRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class AuthGetSessionKeyRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5615991716707887953L;
	private java.lang.String	appKey;
	private java.lang.String	token;

	public AuthGetSessionKeyRequest() {
	}

	public AuthGetSessionKeyRequest(java.lang.String appKey,
			java.lang.String token) {
		this.appKey = appKey;
		this.token = token;
	}

	/**
	 * Gets the appKey value for this AuthGetSessionKeyRequest.
	 * 
	 * @return appKey
	 */
	public java.lang.String getAppKey() {
		return appKey;
	}

	/**
	 * Sets the appKey value for this AuthGetSessionKeyRequest.
	 * 
	 * @param appKey
	 */
	public void setAppKey(java.lang.String appKey) {
		this.appKey = appKey;
	}

	/**
	 * Gets the token value for this AuthGetSessionKeyRequest.
	 * 
	 * @return token
	 */
	public java.lang.String getToken() {
		return token;
	}

	/**
	 * Sets the token value for this AuthGetSessionKeyRequest.
	 * 
	 * @param token
	 */
	public void setToken(java.lang.String token) {
		this.token = token;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof AuthGetSessionKeyRequest)) {
			return false;
		}
		AuthGetSessionKeyRequest other = (AuthGetSessionKeyRequest) obj;
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
				&& ((appKey == null && other.getAppKey() == null) || (appKey != null && appKey
						.equals(other.getAppKey())))
				&& ((token == null && other.getToken() == null) || (token != null && token
						.equals(other.getToken())));
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
		if (getAppKey() != null) {
			_hashCode += getAppKey().hashCode();
		}
		if (getToken() != null) {
			_hashCode += getToken().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			AuthGetSessionKeyRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "AuthGetSessionKeyRequest"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("appKey");
		elemField.setXmlName(new javax.xml.namespace.QName("", "appKey"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("token");
		elemField.setXmlName(new javax.xml.namespace.QName("", "token"));
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
