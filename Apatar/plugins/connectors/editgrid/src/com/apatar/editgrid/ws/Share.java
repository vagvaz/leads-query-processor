/**
 * Share.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class Share implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4784540190788424699L;
	private java.lang.String	bookId;
	private java.lang.String	workbook;
	private java.lang.String	workspaceId;
	private java.lang.String	workspace;
	private java.lang.String	permission;

	public Share() {
	}

	public Share(java.lang.String bookId, java.lang.String workbook,
			java.lang.String workspaceId, java.lang.String workspace,
			java.lang.String permission) {
		this.bookId = bookId;
		this.workbook = workbook;
		this.workspaceId = workspaceId;
		this.workspace = workspace;
		this.permission = permission;
	}

	/**
	 * Gets the bookId value for this Share.
	 * 
	 * @return bookId
	 */
	public java.lang.String getBookId() {
		return bookId;
	}

	/**
	 * Sets the bookId value for this Share.
	 * 
	 * @param bookId
	 */
	public void setBookId(java.lang.String bookId) {
		this.bookId = bookId;
	}

	/**
	 * Gets the workbook value for this Share.
	 * 
	 * @return workbook
	 */
	public java.lang.String getWorkbook() {
		return workbook;
	}

	/**
	 * Sets the workbook value for this Share.
	 * 
	 * @param workbook
	 */
	public void setWorkbook(java.lang.String workbook) {
		this.workbook = workbook;
	}

	/**
	 * Gets the workspaceId value for this Share.
	 * 
	 * @return workspaceId
	 */
	public java.lang.String getWorkspaceId() {
		return workspaceId;
	}

	/**
	 * Sets the workspaceId value for this Share.
	 * 
	 * @param workspaceId
	 */
	public void setWorkspaceId(java.lang.String workspaceId) {
		this.workspaceId = workspaceId;
	}

	/**
	 * Gets the workspace value for this Share.
	 * 
	 * @return workspace
	 */
	public java.lang.String getWorkspace() {
		return workspace;
	}

	/**
	 * Sets the workspace value for this Share.
	 * 
	 * @param workspace
	 */
	public void setWorkspace(java.lang.String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Gets the permission value for this Share.
	 * 
	 * @return permission
	 */
	public java.lang.String getPermission() {
		return permission;
	}

	/**
	 * Sets the permission value for this Share.
	 * 
	 * @param permission
	 */
	public void setPermission(java.lang.String permission) {
		this.permission = permission;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Share)) {
			return false;
		}
		Share other = (Share) obj;
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
				&& ((bookId == null && other.getBookId() == null) || (bookId != null && bookId
						.equals(other.getBookId())))
				&& ((workbook == null && other.getWorkbook() == null) || (workbook != null && workbook
						.equals(other.getWorkbook())))
				&& ((workspaceId == null && other.getWorkspaceId() == null) || (workspaceId != null && workspaceId
						.equals(other.getWorkspaceId())))
				&& ((workspace == null && other.getWorkspace() == null) || (workspace != null && workspace
						.equals(other.getWorkspace())))
				&& ((permission == null && other.getPermission() == null) || (permission != null && permission
						.equals(other.getPermission())));
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
		if (getBookId() != null) {
			_hashCode += getBookId().hashCode();
		}
		if (getWorkbook() != null) {
			_hashCode += getWorkbook().hashCode();
		}
		if (getWorkspaceId() != null) {
			_hashCode += getWorkspaceId().hashCode();
		}
		if (getWorkspace() != null) {
			_hashCode += getWorkspace().hashCode();
		}
		if (getPermission() != null) {
			_hashCode += getPermission().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			Share.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "Share"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("bookId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "bookId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("workbook");
		elemField.setXmlName(new javax.xml.namespace.QName("", "workbook"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("workspaceId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "workspaceId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("workspace");
		elemField.setXmlName(new javax.xml.namespace.QName("", "workspace"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("permission");
		elemField.setXmlName(new javax.xml.namespace.QName("", "permission"));
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
