/**
 * UserQueryRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class UserQueryRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long					serialVersionUID	= -5707691180106041950L;
	private java.lang.String					sessionKey;
	private java.lang.String					org;
	private java.lang.Integer					limit;
	private java.lang.Integer					offset;
	private com.apatar.editgrid.ws.UserQuery	body;

	public UserQueryRequest() {
	}

	public UserQueryRequest(java.lang.String sessionKey, java.lang.String org,
			java.lang.Integer limit, java.lang.Integer offset,
			com.apatar.editgrid.ws.UserQuery body) {
		this.sessionKey = sessionKey;
		this.org = org;
		this.limit = limit;
		this.offset = offset;
		this.body = body;
	}

	/**
	 * Gets the sessionKey value for this UserQueryRequest.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this UserQueryRequest.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the org value for this UserQueryRequest.
	 * 
	 * @return org
	 */
	public java.lang.String getOrg() {
		return org;
	}

	/**
	 * Sets the org value for this UserQueryRequest.
	 * 
	 * @param org
	 */
	public void setOrg(java.lang.String org) {
		this.org = org;
	}

	/**
	 * Gets the limit value for this UserQueryRequest.
	 * 
	 * @return limit
	 */
	public java.lang.Integer getLimit() {
		return limit;
	}

	/**
	 * Sets the limit value for this UserQueryRequest.
	 * 
	 * @param limit
	 */
	public void setLimit(java.lang.Integer limit) {
		this.limit = limit;
	}

	/**
	 * Gets the offset value for this UserQueryRequest.
	 * 
	 * @return offset
	 */
	public java.lang.Integer getOffset() {
		return offset;
	}

	/**
	 * Sets the offset value for this UserQueryRequest.
	 * 
	 * @param offset
	 */
	public void setOffset(java.lang.Integer offset) {
		this.offset = offset;
	}

	/**
	 * Gets the body value for this UserQueryRequest.
	 * 
	 * @return body
	 */
	public com.apatar.editgrid.ws.UserQuery getBody() {
		return body;
	}

	/**
	 * Sets the body value for this UserQueryRequest.
	 * 
	 * @param body
	 */
	public void setBody(com.apatar.editgrid.ws.UserQuery body) {
		this.body = body;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof UserQueryRequest)) {
			return false;
		}
		UserQueryRequest other = (UserQueryRequest) obj;
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
				&& ((limit == null && other.getLimit() == null) || (limit != null && limit
						.equals(other.getLimit())))
				&& ((offset == null && other.getOffset() == null) || (offset != null && offset
						.equals(other.getOffset())))
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
		if (getLimit() != null) {
			_hashCode += getLimit().hashCode();
		}
		if (getOffset() != null) {
			_hashCode += getOffset().hashCode();
		}
		if (getBody() != null) {
			_hashCode += getBody().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			UserQueryRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "UserQueryRequest"));
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
		elemField.setFieldName("limit");
		elemField.setXmlName(new javax.xml.namespace.QName("", "limit"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("offset");
		elemField.setXmlName(new javax.xml.namespace.QName("", "offset"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("body");
		elemField.setXmlName(new javax.xml.namespace.QName("", "body"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "UserQuery"));
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
