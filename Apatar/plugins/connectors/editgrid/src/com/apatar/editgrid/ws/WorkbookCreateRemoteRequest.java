/**
 * WorkbookCreateRemoteRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class WorkbookCreateRemoteRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long				serialVersionUID	= 6590106338319715830L;
	private java.lang.String				sessionKey;
	private java.lang.String				workspace;
	private java.lang.String				loadUrl;
	private java.lang.String				loadType;
	private java.lang.String				saveUrl;
	private java.lang.String				saveType;
	private java.lang.String				savePostParam;
	private java.lang.Integer				genName;
	private java.lang.Integer				temporary;
	private java.lang.String				tokenPermission;
	private com.apatar.editgrid.ws.Workbook	body;

	public WorkbookCreateRemoteRequest() {
	}

	public WorkbookCreateRemoteRequest(java.lang.String sessionKey,
			java.lang.String workspace, java.lang.String loadUrl,
			java.lang.String loadType, java.lang.String saveUrl,
			java.lang.String saveType, java.lang.String savePostParam,
			java.lang.Integer genName, java.lang.Integer temporary,
			java.lang.String tokenPermission,
			com.apatar.editgrid.ws.Workbook body) {
		this.sessionKey = sessionKey;
		this.workspace = workspace;
		this.loadUrl = loadUrl;
		this.loadType = loadType;
		this.saveUrl = saveUrl;
		this.saveType = saveType;
		this.savePostParam = savePostParam;
		this.genName = genName;
		this.temporary = temporary;
		this.tokenPermission = tokenPermission;
		this.body = body;
	}

	/**
	 * Gets the sessionKey value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return sessionKey
	 */
	public java.lang.String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the sessionKey value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param sessionKey
	 */
	public void setSessionKey(java.lang.String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Gets the workspace value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return workspace
	 */
	public java.lang.String getWorkspace() {
		return workspace;
	}

	/**
	 * Sets the workspace value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param workspace
	 */
	public void setWorkspace(java.lang.String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Gets the loadUrl value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return loadUrl
	 */
	public java.lang.String getLoadUrl() {
		return loadUrl;
	}

	/**
	 * Sets the loadUrl value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param loadUrl
	 */
	public void setLoadUrl(java.lang.String loadUrl) {
		this.loadUrl = loadUrl;
	}

	/**
	 * Gets the loadType value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return loadType
	 */
	public java.lang.String getLoadType() {
		return loadType;
	}

	/**
	 * Sets the loadType value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param loadType
	 */
	public void setLoadType(java.lang.String loadType) {
		this.loadType = loadType;
	}

	/**
	 * Gets the saveUrl value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return saveUrl
	 */
	public java.lang.String getSaveUrl() {
		return saveUrl;
	}

	/**
	 * Sets the saveUrl value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param saveUrl
	 */
	public void setSaveUrl(java.lang.String saveUrl) {
		this.saveUrl = saveUrl;
	}

	/**
	 * Gets the saveType value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return saveType
	 */
	public java.lang.String getSaveType() {
		return saveType;
	}

	/**
	 * Sets the saveType value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param saveType
	 */
	public void setSaveType(java.lang.String saveType) {
		this.saveType = saveType;
	}

	/**
	 * Gets the savePostParam value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return savePostParam
	 */
	public java.lang.String getSavePostParam() {
		return savePostParam;
	}

	/**
	 * Sets the savePostParam value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param savePostParam
	 */
	public void setSavePostParam(java.lang.String savePostParam) {
		this.savePostParam = savePostParam;
	}

	/**
	 * Gets the genName value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return genName
	 */
	public java.lang.Integer getGenName() {
		return genName;
	}

	/**
	 * Sets the genName value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param genName
	 */
	public void setGenName(java.lang.Integer genName) {
		this.genName = genName;
	}

	/**
	 * Gets the temporary value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return temporary
	 */
	public java.lang.Integer getTemporary() {
		return temporary;
	}

	/**
	 * Sets the temporary value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param temporary
	 */
	public void setTemporary(java.lang.Integer temporary) {
		this.temporary = temporary;
	}

	/**
	 * Gets the tokenPermission value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return tokenPermission
	 */
	public java.lang.String getTokenPermission() {
		return tokenPermission;
	}

	/**
	 * Sets the tokenPermission value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param tokenPermission
	 */
	public void setTokenPermission(java.lang.String tokenPermission) {
		this.tokenPermission = tokenPermission;
	}

	/**
	 * Gets the body value for this WorkbookCreateRemoteRequest.
	 * 
	 * @return body
	 */
	public com.apatar.editgrid.ws.Workbook getBody() {
		return body;
	}

	/**
	 * Sets the body value for this WorkbookCreateRemoteRequest.
	 * 
	 * @param body
	 */
	public void setBody(com.apatar.editgrid.ws.Workbook body) {
		this.body = body;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof WorkbookCreateRemoteRequest)) {
			return false;
		}
		WorkbookCreateRemoteRequest other = (WorkbookCreateRemoteRequest) obj;
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
				&& ((loadUrl == null && other.getLoadUrl() == null) || (loadUrl != null && loadUrl
						.equals(other.getLoadUrl())))
				&& ((loadType == null && other.getLoadType() == null) || (loadType != null && loadType
						.equals(other.getLoadType())))
				&& ((saveUrl == null && other.getSaveUrl() == null) || (saveUrl != null && saveUrl
						.equals(other.getSaveUrl())))
				&& ((saveType == null && other.getSaveType() == null) || (saveType != null && saveType
						.equals(other.getSaveType())))
				&& ((savePostParam == null && other.getSavePostParam() == null) || (savePostParam != null && savePostParam
						.equals(other.getSavePostParam())))
				&& ((genName == null && other.getGenName() == null) || (genName != null && genName
						.equals(other.getGenName())))
				&& ((temporary == null && other.getTemporary() == null) || (temporary != null && temporary
						.equals(other.getTemporary())))
				&& ((tokenPermission == null && other.getTokenPermission() == null) || (tokenPermission != null && tokenPermission
						.equals(other.getTokenPermission())))
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
		if (getLoadUrl() != null) {
			_hashCode += getLoadUrl().hashCode();
		}
		if (getLoadType() != null) {
			_hashCode += getLoadType().hashCode();
		}
		if (getSaveUrl() != null) {
			_hashCode += getSaveUrl().hashCode();
		}
		if (getSaveType() != null) {
			_hashCode += getSaveType().hashCode();
		}
		if (getSavePostParam() != null) {
			_hashCode += getSavePostParam().hashCode();
		}
		if (getGenName() != null) {
			_hashCode += getGenName().hashCode();
		}
		if (getTemporary() != null) {
			_hashCode += getTemporary().hashCode();
		}
		if (getTokenPermission() != null) {
			_hashCode += getTokenPermission().hashCode();
		}
		if (getBody() != null) {
			_hashCode += getBody().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			WorkbookCreateRemoteRequest.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "WorkbookCreateRemoteRequest"));
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
		elemField.setFieldName("loadUrl");
		elemField.setXmlName(new javax.xml.namespace.QName("", "loadUrl"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("loadType");
		elemField.setXmlName(new javax.xml.namespace.QName("", "loadType"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("saveUrl");
		elemField.setXmlName(new javax.xml.namespace.QName("", "saveUrl"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("saveType");
		elemField.setXmlName(new javax.xml.namespace.QName("", "saveType"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("savePostParam");
		elemField
				.setXmlName(new javax.xml.namespace.QName("", "savePostParam"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("genName");
		elemField.setXmlName(new javax.xml.namespace.QName("", "genName"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("temporary");
		elemField.setXmlName(new javax.xml.namespace.QName("", "temporary"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tokenPermission");
		elemField.setXmlName(new javax.xml.namespace.QName("",
				"tokenPermission"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
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
