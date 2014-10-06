/**
 * WorkspaceGetRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class WorkspaceGetRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5222858165636030935L;
	private java.lang.String	sessionKey;
	private java.lang.String	workspace;

	public WorkspaceGetRequest() {
	}

	public WorkspaceGetRequest(java.lang.String sessionKey,
			java.lang.String workspace) {
		this.sessionKey = sessionKey;
		this.workspace = workspace;
	}

	/**
	 * Gets the sessionKey value for this WorkspaceGetRequest.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this WorkspaceGetRequest.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the workspace value for this WorkspaceGetRequest.
	 * 
	 * @return workspace
	 */
	public java.lang.String getWorkspace() {
		return workspace;
	}

	/**
	 * Sets the workspace value for this WorkspaceGetRequest.
	 * 
	 * @param workspace
	 */
	public void setWorkspace(java.lang.String workspace) {
		this.workspace = workspace;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof WorkspaceGetRequest)) {
			return false;
		}
		WorkspaceGetRequest other = (WorkspaceGetRequest) obj;
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
				&& ((workspace == null && other.getWorkspace() == null) || (workspace != null && workspace
						.equals(other.getWorkspace())));
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
		if (getWorkspace() != null) {
			_hashCode += getWorkspace().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			WorkspaceGetRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "WorkspaceGetRequest"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sessionKey");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sessionKey"));
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
