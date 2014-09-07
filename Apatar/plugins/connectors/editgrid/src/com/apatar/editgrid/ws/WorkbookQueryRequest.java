/**
 * WorkbookQueryRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class WorkbookQueryRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long						serialVersionUID	= 8258871001968808406L;
	private java.lang.String						sessionKey;
	private java.lang.String						workspace;
	private java.lang.Integer						shared;
	private java.lang.Integer						deleted;
	private java.lang.Integer						limit;
	private java.lang.Integer						offset;
	private com.apatar.editgrid.ws.WorkbookQuery	body;

	public WorkbookQueryRequest() {
	}

	public WorkbookQueryRequest(java.lang.String sessionKey,
			java.lang.String workspace, java.lang.Integer shared,
			java.lang.Integer deleted, java.lang.Integer limit,
			java.lang.Integer offset, com.apatar.editgrid.ws.WorkbookQuery body) {
		this.sessionKey = sessionKey;
		this.workspace = workspace;
		this.shared = shared;
		this.deleted = deleted;
		this.limit = limit;
		this.offset = offset;
		this.body = body;
	}

	/**
	 * Gets the sessionKey value for this WorkbookQueryRequest.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this WorkbookQueryRequest.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the workspace value for this WorkbookQueryRequest.
	 * 
	 * @return workspace
	 */
	public java.lang.String getWorkspace() {
		return workspace;
	}

	/**
	 * Sets the workspace value for this WorkbookQueryRequest.
	 * 
	 * @param workspace
	 */
	public void setWorkspace(java.lang.String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Gets the shared value for this WorkbookQueryRequest.
	 * 
	 * @return shared
	 */
	public java.lang.Integer getShared() {
		return shared;
	}

	/**
	 * Sets the shared value for this WorkbookQueryRequest.
	 * 
	 * @param shared
	 */
	public void setShared(java.lang.Integer shared) {
		this.shared = shared;
	}

	/**
	 * Gets the deleted value for this WorkbookQueryRequest.
	 * 
	 * @return deleted
	 */
	public java.lang.Integer getDeleted() {
		return deleted;
	}

	/**
	 * Sets the deleted value for this WorkbookQueryRequest.
	 * 
	 * @param deleted
	 */
	public void setDeleted(java.lang.Integer deleted) {
		this.deleted = deleted;
	}

	/**
	 * Gets the limit value for this WorkbookQueryRequest.
	 * 
	 * @return limit
	 */
	public java.lang.Integer getLimit() {
		return limit;
	}

	/**
	 * Sets the limit value for this WorkbookQueryRequest.
	 * 
	 * @param limit
	 */
	public void setLimit(java.lang.Integer limit) {
		this.limit = limit;
	}

	/**
	 * Gets the offset value for this WorkbookQueryRequest.
	 * 
	 * @return offset
	 */
	public java.lang.Integer getOffset() {
		return offset;
	}

	/**
	 * Sets the offset value for this WorkbookQueryRequest.
	 * 
	 * @param offset
	 */
	public void setOffset(java.lang.Integer offset) {
		this.offset = offset;
	}

	/**
	 * Gets the body value for this WorkbookQueryRequest.
	 * 
	 * @return body
	 */
	public com.apatar.editgrid.ws.WorkbookQuery getBody() {
		return body;
	}

	/**
	 * Sets the body value for this WorkbookQueryRequest.
	 * 
	 * @param body
	 */
	public void setBody(com.apatar.editgrid.ws.WorkbookQuery body) {
		this.body = body;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof WorkbookQueryRequest)) {
			return false;
		}
		WorkbookQueryRequest other = (WorkbookQueryRequest) obj;
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
						.equals(other.getWorkspace())))
				&& ((shared == null && other.getShared() == null) || (shared != null && shared
						.equals(other.getShared())))
				&& ((deleted == null && other.getDeleted() == null) || (deleted != null && deleted
						.equals(other.getDeleted())))
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
		if (getWorkspace() != null) {
			_hashCode += getWorkspace().hashCode();
		}
		if (getShared() != null) {
			_hashCode += getShared().hashCode();
		}
		if (getDeleted() != null) {
			_hashCode += getDeleted().hashCode();
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
																			WorkbookQueryRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "WorkbookQueryRequest"));
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
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("shared");
		elemField.setXmlName(new javax.xml.namespace.QName("", "shared"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deleted");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deleted"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
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
				"http://api.editgrid.com", "WorkbookQuery"));
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
