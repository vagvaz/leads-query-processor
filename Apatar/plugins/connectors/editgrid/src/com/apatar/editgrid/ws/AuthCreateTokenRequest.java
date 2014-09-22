/**
 * AuthCreateTokenRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class AuthCreateTokenRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7754669096188365908L;
	private java.lang.String	appKey;
	private java.lang.String	onSuccess;
	private java.lang.String	onFailure;
	private java.lang.String	infinite;
	private java.lang.String	orgAdmin;
	private java.lang.String	sysAdmin;
	private java.lang.String	readonly;

	public AuthCreateTokenRequest() {
	}

	public AuthCreateTokenRequest(java.lang.String appKey,
			java.lang.String onSuccess, java.lang.String onFailure,
			java.lang.String infinite, java.lang.String orgAdmin,
			java.lang.String sysAdmin, java.lang.String readonly) {
		this.appKey = appKey;
		this.onSuccess = onSuccess;
		this.onFailure = onFailure;
		this.infinite = infinite;
		this.orgAdmin = orgAdmin;
		this.sysAdmin = sysAdmin;
		this.readonly = readonly;
	}

	/**
	 * Gets the appKey value for this AuthCreateTokenRequest.
	 * 
	 * @return appKey
	 */
	public java.lang.String getAppKey() {
		return appKey;
	}

	/**
	 * Sets the appKey value for this AuthCreateTokenRequest.
	 * 
	 * @param appKey
	 */
	public void setAppKey(java.lang.String appKey) {
		this.appKey = appKey;
	}

	/**
	 * Gets the onSuccess value for this AuthCreateTokenRequest.
	 * 
	 * @return onSuccess
	 */
	public java.lang.String getOnSuccess() {
		return onSuccess;
	}

	/**
	 * Sets the onSuccess value for this AuthCreateTokenRequest.
	 * 
	 * @param onSuccess
	 */
	public void setOnSuccess(java.lang.String onSuccess) {
		this.onSuccess = onSuccess;
	}

	/**
	 * Gets the onFailure value for this AuthCreateTokenRequest.
	 * 
	 * @return onFailure
	 */
	public java.lang.String getOnFailure() {
		return onFailure;
	}

	/**
	 * Sets the onFailure value for this AuthCreateTokenRequest.
	 * 
	 * @param onFailure
	 */
	public void setOnFailure(java.lang.String onFailure) {
		this.onFailure = onFailure;
	}

	/**
	 * Gets the infinite value for this AuthCreateTokenRequest.
	 * 
	 * @return infinite
	 */
	public java.lang.String getInfinite() {
		return infinite;
	}

	/**
	 * Sets the infinite value for this AuthCreateTokenRequest.
	 * 
	 * @param infinite
	 */
	public void setInfinite(java.lang.String infinite) {
		this.infinite = infinite;
	}

	/**
	 * Gets the orgAdmin value for this AuthCreateTokenRequest.
	 * 
	 * @return orgAdmin
	 */
	public java.lang.String getOrgAdmin() {
		return orgAdmin;
	}

	/**
	 * Sets the orgAdmin value for this AuthCreateTokenRequest.
	 * 
	 * @param orgAdmin
	 */
	public void setOrgAdmin(java.lang.String orgAdmin) {
		this.orgAdmin = orgAdmin;
	}

	/**
	 * Gets the sysAdmin value for this AuthCreateTokenRequest.
	 * 
	 * @return sysAdmin
	 */
	public java.lang.String getSysAdmin() {
		return sysAdmin;
	}

	/**
	 * Sets the sysAdmin value for this AuthCreateTokenRequest.
	 * 
	 * @param sysAdmin
	 */
	public void setSysAdmin(java.lang.String sysAdmin) {
		this.sysAdmin = sysAdmin;
	}

	/**
	 * Gets the readonly value for this AuthCreateTokenRequest.
	 * 
	 * @return readonly
	 */
	public java.lang.String getReadonly() {
		return readonly;
	}

	/**
	 * Sets the readonly value for this AuthCreateTokenRequest.
	 * 
	 * @param readonly
	 */
	public void setReadonly(java.lang.String readonly) {
		this.readonly = readonly;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof AuthCreateTokenRequest)) {
			return false;
		}
		AuthCreateTokenRequest other = (AuthCreateTokenRequest) obj;
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
				&& ((onSuccess == null && other.getOnSuccess() == null) || (onSuccess != null && onSuccess
						.equals(other.getOnSuccess())))
				&& ((onFailure == null && other.getOnFailure() == null) || (onFailure != null && onFailure
						.equals(other.getOnFailure())))
				&& ((infinite == null && other.getInfinite() == null) || (infinite != null && infinite
						.equals(other.getInfinite())))
				&& ((orgAdmin == null && other.getOrgAdmin() == null) || (orgAdmin != null && orgAdmin
						.equals(other.getOrgAdmin())))
				&& ((sysAdmin == null && other.getSysAdmin() == null) || (sysAdmin != null && sysAdmin
						.equals(other.getSysAdmin())))
				&& ((readonly == null && other.getReadonly() == null) || (readonly != null && readonly
						.equals(other.getReadonly())));
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
		if (getOnSuccess() != null) {
			_hashCode += getOnSuccess().hashCode();
		}
		if (getOnFailure() != null) {
			_hashCode += getOnFailure().hashCode();
		}
		if (getInfinite() != null) {
			_hashCode += getInfinite().hashCode();
		}
		if (getOrgAdmin() != null) {
			_hashCode += getOrgAdmin().hashCode();
		}
		if (getSysAdmin() != null) {
			_hashCode += getSysAdmin().hashCode();
		}
		if (getReadonly() != null) {
			_hashCode += getReadonly().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			AuthCreateTokenRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "AuthCreateTokenRequest"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("appKey");
		elemField.setXmlName(new javax.xml.namespace.QName("", "appKey"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("onSuccess");
		elemField.setXmlName(new javax.xml.namespace.QName("", "onSuccess"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("onFailure");
		elemField.setXmlName(new javax.xml.namespace.QName("", "onFailure"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("infinite");
		elemField.setXmlName(new javax.xml.namespace.QName("", "infinite"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("orgAdmin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "orgAdmin"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sysAdmin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sysAdmin"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("readonly");
		elemField.setXmlName(new javax.xml.namespace.QName("", "readonly"));
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
