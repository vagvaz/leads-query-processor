/**
 * Workbook.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public class Workbook implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7343352417893433456L;
	private java.lang.String	id;
	private java.lang.String	path;
	private java.lang.String	name;
	private java.lang.String	currRevId;
	private java.lang.String	workspaceId;
	private java.lang.String	workspace;
	private java.lang.String	organisationId;
	private java.lang.String	organisation;
	private java.lang.String	publicAccess;
	private int					isTemplate;
	private java.lang.Integer	isDeleted;
	private java.lang.String	timeZone;
	private java.lang.String	note;
	private java.lang.String	createUserId;
	private java.lang.String	createUser;
	private java.lang.String	createTime;
	private java.lang.String	lastModifiedUserId;
	private java.lang.String	lastModifiedUser;
	private java.lang.String	lastModifiedTime;
	private java.lang.String	accessToken;

	public Workbook() {
	}

	public Workbook(java.lang.String id, java.lang.String path,
			java.lang.String name, java.lang.String currRevId,
			java.lang.String workspaceId, java.lang.String workspace,
			java.lang.String organisationId, java.lang.String organisation,
			java.lang.String publicAccess, int isTemplate,
			java.lang.Integer isDeleted, java.lang.String timeZone,
			java.lang.String note, java.lang.String createUserId,
			java.lang.String createUser, java.lang.String createTime,
			java.lang.String lastModifiedUserId,
			java.lang.String lastModifiedUser,
			java.lang.String lastModifiedTime, java.lang.String accessToken) {
		this.id = id;
		this.path = path;
		this.name = name;
		this.currRevId = currRevId;
		this.workspaceId = workspaceId;
		this.workspace = workspace;
		this.organisationId = organisationId;
		this.organisation = organisation;
		this.publicAccess = publicAccess;
		this.isTemplate = isTemplate;
		this.isDeleted = isDeleted;
		this.timeZone = timeZone;
		this.note = note;
		this.createUserId = createUserId;
		this.createUser = createUser;
		this.createTime = createTime;
		this.lastModifiedUserId = lastModifiedUserId;
		this.lastModifiedUser = lastModifiedUser;
		this.lastModifiedTime = lastModifiedTime;
		this.accessToken = accessToken;
	}

	/**
	 * Gets the id value for this Workbook.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this Workbook.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the path value for this Workbook.
	 * 
	 * @return path
	 */
	public java.lang.String getPath() {
		return path;
	}

	/**
	 * Sets the path value for this Workbook.
	 * 
	 * @param path
	 */
	public void setPath(java.lang.String path) {
		this.path = path;
	}

	/**
	 * Gets the name value for this Workbook.
	 * 
	 * @return name
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Sets the name value for this Workbook.
	 * 
	 * @param name
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * Gets the currRevId value for this Workbook.
	 * 
	 * @return currRevId
	 */
	public java.lang.String getCurrRevId() {
		return currRevId;
	}

	/**
	 * Sets the currRevId value for this Workbook.
	 * 
	 * @param currRevId
	 */
	public void setCurrRevId(java.lang.String currRevId) {
		this.currRevId = currRevId;
	}

	/**
	 * Gets the workspaceId value for this Workbook.
	 * 
	 * @return workspaceId
	 */
	public java.lang.String getWorkspaceId() {
		return workspaceId;
	}

	/**
	 * Sets the workspaceId value for this Workbook.
	 * 
	 * @param workspaceId
	 */
	public void setWorkspaceId(java.lang.String workspaceId) {
		this.workspaceId = workspaceId;
	}

	/**
	 * Gets the workspace value for this Workbook.
	 * 
	 * @return workspace
	 */
	public java.lang.String getWorkspace() {
		return workspace;
	}

	/**
	 * Sets the workspace value for this Workbook.
	 * 
	 * @param workspace
	 */
	public void setWorkspace(java.lang.String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Gets the organisationId value for this Workbook.
	 * 
	 * @return organisationId
	 */
	public java.lang.String getOrganisationId() {
		return organisationId;
	}

	/**
	 * Sets the organisationId value for this Workbook.
	 * 
	 * @param organisationId
	 */
	public void setOrganisationId(java.lang.String organisationId) {
		this.organisationId = organisationId;
	}

	/**
	 * Gets the organisation value for this Workbook.
	 * 
	 * @return organisation
	 */
	public java.lang.String getOrganisation() {
		return organisation;
	}

	/**
	 * Sets the organisation value for this Workbook.
	 * 
	 * @param organisation
	 */
	public void setOrganisation(java.lang.String organisation) {
		this.organisation = organisation;
	}

	/**
	 * Gets the publicAccess value for this Workbook.
	 * 
	 * @return publicAccess
	 */
	public java.lang.String getPublicAccess() {
		return publicAccess;
	}

	/**
	 * Sets the publicAccess value for this Workbook.
	 * 
	 * @param publicAccess
	 */
	public void setPublicAccess(java.lang.String publicAccess) {
		this.publicAccess = publicAccess;
	}

	/**
	 * Gets the isTemplate value for this Workbook.
	 * 
	 * @return isTemplate
	 */
	public int getIsTemplate() {
		return isTemplate;
	}

	/**
	 * Sets the isTemplate value for this Workbook.
	 * 
	 * @param isTemplate
	 */
	public void setIsTemplate(int isTemplate) {
		this.isTemplate = isTemplate;
	}

	/**
	 * Gets the isDeleted value for this Workbook.
	 * 
	 * @return isDeleted
	 */
	public java.lang.Integer getIsDeleted() {
		return isDeleted;
	}

	/**
	 * Sets the isDeleted value for this Workbook.
	 * 
	 * @param isDeleted
	 */
	public void setIsDeleted(java.lang.Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Gets the timeZone value for this Workbook.
	 * 
	 * @return timeZone
	 */
	public java.lang.String getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets the timeZone value for this Workbook.
	 * 
	 * @param timeZone
	 */
	public void setTimeZone(java.lang.String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Gets the note value for this Workbook.
	 * 
	 * @return note
	 */
	public java.lang.String getNote() {
		return note;
	}

	/**
	 * Sets the note value for this Workbook.
	 * 
	 * @param note
	 */
	public void setNote(java.lang.String note) {
		this.note = note;
	}

	/**
	 * Gets the createUserId value for this Workbook.
	 * 
	 * @return createUserId
	 */
	public java.lang.String getCreateUserId() {
		return createUserId;
	}

	/**
	 * Sets the createUserId value for this Workbook.
	 * 
	 * @param createUserId
	 */
	public void setCreateUserId(java.lang.String createUserId) {
		this.createUserId = createUserId;
	}

	/**
	 * Gets the createUser value for this Workbook.
	 * 
	 * @return createUser
	 */
	public java.lang.String getCreateUser() {
		return createUser;
	}

	/**
	 * Sets the createUser value for this Workbook.
	 * 
	 * @param createUser
	 */
	public void setCreateUser(java.lang.String createUser) {
		this.createUser = createUser;
	}

	/**
	 * Gets the createTime value for this Workbook.
	 * 
	 * @return createTime
	 */
	public java.lang.String getCreateTime() {
		return createTime;
	}

	/**
	 * Sets the createTime value for this Workbook.
	 * 
	 * @param createTime
	 */
	public void setCreateTime(java.lang.String createTime) {
		this.createTime = createTime;
	}

	/**
	 * Gets the lastModifiedUserId value for this Workbook.
	 * 
	 * @return lastModifiedUserId
	 */
	public java.lang.String getLastModifiedUserId() {
		return lastModifiedUserId;
	}

	/**
	 * Sets the lastModifiedUserId value for this Workbook.
	 * 
	 * @param lastModifiedUserId
	 */
	public void setLastModifiedUserId(java.lang.String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	/**
	 * Gets the lastModifiedUser value for this Workbook.
	 * 
	 * @return lastModifiedUser
	 */
	public java.lang.String getLastModifiedUser() {
		return lastModifiedUser;
	}

	/**
	 * Sets the lastModifiedUser value for this Workbook.
	 * 
	 * @param lastModifiedUser
	 */
	public void setLastModifiedUser(java.lang.String lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	/**
	 * Gets the lastModifiedTime value for this Workbook.
	 * 
	 * @return lastModifiedTime
	 */
	public java.lang.String getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Sets the lastModifiedTime value for this Workbook.
	 * 
	 * @param lastModifiedTime
	 */
	public void setLastModifiedTime(java.lang.String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * Gets the accessToken value for this Workbook.
	 * 
	 * @return accessToken
	 */
	public java.lang.String getAccessToken() {
		return accessToken;
	}

	/**
	 * Sets the accessToken value for this Workbook.
	 * 
	 * @param accessToken
	 */
	public void setAccessToken(java.lang.String accessToken) {
		this.accessToken = accessToken;
	}

	private java.lang.Object	__equalsCalc	= null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Workbook)) {
			return false;
		}
		Workbook other = (Workbook) obj;
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
				&& ((id == null && other.getId() == null) || (id != null && id
						.equals(other.getId())))
				&& ((path == null && other.getPath() == null) || (path != null && path
						.equals(other.getPath())))
				&& ((name == null && other.getName() == null) || (name != null && name
						.equals(other.getName())))
				&& ((currRevId == null && other.getCurrRevId() == null) || (currRevId != null && currRevId
						.equals(other.getCurrRevId())))
				&& ((workspaceId == null && other.getWorkspaceId() == null) || (workspaceId != null && workspaceId
						.equals(other.getWorkspaceId())))
				&& ((workspace == null && other.getWorkspace() == null) || (workspace != null && workspace
						.equals(other.getWorkspace())))
				&& ((organisationId == null && other.getOrganisationId() == null) || (organisationId != null && organisationId
						.equals(other.getOrganisationId())))
				&& ((organisation == null && other.getOrganisation() == null) || (organisation != null && organisation
						.equals(other.getOrganisation())))
				&& ((publicAccess == null && other.getPublicAccess() == null) || (publicAccess != null && publicAccess
						.equals(other.getPublicAccess())))
				&& isTemplate == other.getIsTemplate()
				&& ((isDeleted == null && other.getIsDeleted() == null) || (isDeleted != null && isDeleted
						.equals(other.getIsDeleted())))
				&& ((timeZone == null && other.getTimeZone() == null) || (timeZone != null && timeZone
						.equals(other.getTimeZone())))
				&& ((note == null && other.getNote() == null) || (note != null && note
						.equals(other.getNote())))
				&& ((createUserId == null && other.getCreateUserId() == null) || (createUserId != null && createUserId
						.equals(other.getCreateUserId())))
				&& ((createUser == null && other.getCreateUser() == null) || (createUser != null && createUser
						.equals(other.getCreateUser())))
				&& ((createTime == null && other.getCreateTime() == null) || (createTime != null && createTime
						.equals(other.getCreateTime())))
				&& ((lastModifiedUserId == null && other
						.getLastModifiedUserId() == null) || (lastModifiedUserId != null && lastModifiedUserId
						.equals(other.getLastModifiedUserId())))
				&& ((lastModifiedUser == null && other.getLastModifiedUser() == null) || (lastModifiedUser != null && lastModifiedUser
						.equals(other.getLastModifiedUser())))
				&& ((lastModifiedTime == null && other.getLastModifiedTime() == null) || (lastModifiedTime != null && lastModifiedTime
						.equals(other.getLastModifiedTime())))
				&& ((accessToken == null && other.getAccessToken() == null) || (accessToken != null && accessToken
						.equals(other.getAccessToken())));
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
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getPath() != null) {
			_hashCode += getPath().hashCode();
		}
		if (getName() != null) {
			_hashCode += getName().hashCode();
		}
		if (getCurrRevId() != null) {
			_hashCode += getCurrRevId().hashCode();
		}
		if (getWorkspaceId() != null) {
			_hashCode += getWorkspaceId().hashCode();
		}
		if (getWorkspace() != null) {
			_hashCode += getWorkspace().hashCode();
		}
		if (getOrganisationId() != null) {
			_hashCode += getOrganisationId().hashCode();
		}
		if (getOrganisation() != null) {
			_hashCode += getOrganisation().hashCode();
		}
		if (getPublicAccess() != null) {
			_hashCode += getPublicAccess().hashCode();
		}
		_hashCode += getIsTemplate();
		if (getIsDeleted() != null) {
			_hashCode += getIsDeleted().hashCode();
		}
		if (getTimeZone() != null) {
			_hashCode += getTimeZone().hashCode();
		}
		if (getNote() != null) {
			_hashCode += getNote().hashCode();
		}
		if (getCreateUserId() != null) {
			_hashCode += getCreateUserId().hashCode();
		}
		if (getCreateUser() != null) {
			_hashCode += getCreateUser().hashCode();
		}
		if (getCreateTime() != null) {
			_hashCode += getCreateTime().hashCode();
		}
		if (getLastModifiedUserId() != null) {
			_hashCode += getLastModifiedUserId().hashCode();
		}
		if (getLastModifiedUser() != null) {
			_hashCode += getLastModifiedUser().hashCode();
		}
		if (getLastModifiedTime() != null) {
			_hashCode += getLastModifiedTime().hashCode();
		}
		if (getAccessToken() != null) {
			_hashCode += getAccessToken().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc	typeDesc	= new org.apache.axis.description.TypeDesc(
																			Workbook.class,
																			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://api.editgrid.com", "Workbook"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("path");
		elemField.setXmlName(new javax.xml.namespace.QName("", "path"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("name");
		elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("currRevId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "currRevId"));
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
		elemField.setFieldName("organisationId");
		elemField
				.setXmlName(new javax.xml.namespace.QName("", "organisationId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("organisation");
		elemField.setXmlName(new javax.xml.namespace.QName("", "organisation"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("publicAccess");
		elemField.setXmlName(new javax.xml.namespace.QName("", "publicAccess"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("isTemplate");
		elemField.setXmlName(new javax.xml.namespace.QName("", "isTemplate"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("isDeleted");
		elemField.setXmlName(new javax.xml.namespace.QName("", "isDeleted"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("timeZone");
		elemField.setXmlName(new javax.xml.namespace.QName("", "timeZone"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("note");
		elemField.setXmlName(new javax.xml.namespace.QName("", "note"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("createUserId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "createUserId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("createUser");
		elemField.setXmlName(new javax.xml.namespace.QName("", "createUser"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("createTime");
		elemField.setXmlName(new javax.xml.namespace.QName("", "createTime"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lastModifiedUserId");
		elemField.setXmlName(new javax.xml.namespace.QName("",
				"lastModifiedUserId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lastModifiedUser");
		elemField.setXmlName(new javax.xml.namespace.QName("",
				"lastModifiedUser"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lastModifiedTime");
		elemField.setXmlName(new javax.xml.namespace.QName("",
				"lastModifiedTime"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("accessToken");
		elemField.setXmlName(new javax.xml.namespace.QName("", "accessToken"));
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
