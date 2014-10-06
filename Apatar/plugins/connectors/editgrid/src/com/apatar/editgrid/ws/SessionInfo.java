/**
 * SessionInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class SessionInfo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 417735160030862285L;
	private java.lang.String	sessionKey;
	private java.lang.String	userId;
	private java.lang.String	user;

	public SessionInfo() {
	}

	public SessionInfo(java.lang.String sessionKey, java.lang.String userId,
			java.lang.String user) {
		this.sessionKey = sessionKey;
		this.userId = userId;
		this.user = user;
	}

	/**
	 * Gets the sessionKey value for this SessionInfo.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this SessionInfo.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the userId value for this SessionInfo.
	 * 
	 * @return userId
	 */
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	 * Sets the userId value for this SessionInfo.
	 * 
	 * @param userId
	 */
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the user value for this SessionInfo.
	 * 
	 * @return user
	 */
	public java.lang.String getUser() {
		return user;
	}

	/**
	 * Sets the user value for this SessionInfo.
	 * 
	 * @param user
	 */
	public void setUser(java.lang.String user) {
		this.user = user;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SessionInfo)) {
			return false;
		}
		SessionInfo other = (SessionInfo) obj;
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
				&& ((userId == null && other.getUserId() == null) || (userId != null && userId
						.equals(other.getUserId())))
				&& ((user == null && other.getUser() == null) || (user != null && user
						.equals(other.getUser())));
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
		if (getUserId() != null) {
			_hashCode += getUserId().hashCode();
		}
		if (getUser() != null) {
			_hashCode += getUser().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			SessionInfo.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "SessionInfo"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sessionKey");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sessionKey"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("userId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "userId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("user");
		elemField.setXmlName(new javax.xml.namespace.QName("", "user"));
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
