/**
 * Soap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.apatar.salesforcecom.ws;

public interface Soap_PortType extends java.rmi.Remote {

    /**
     * Login to the Salesforce.com SOAP Api
     */
    public com.apatar.salesforcecom.ws.LoginResult login(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault, com.apatar.salesforcecom.ws.LoginFault;

    /**
     * Describe an sObject
     */
    public com.apatar.salesforcecom.ws.DescribeSObjectResult describeSObject(java.lang.String sObjectType) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Describe a number sObjects
     */
    public com.apatar.salesforcecom.ws.DescribeSObjectResult[] describeSObjects(java.lang.String[] sObjectType) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Describe the Global state
     */
    public com.apatar.salesforcecom.ws.DescribeGlobalResult describeGlobal() throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Describe all the data category groups available for a given
     * set of types
     */
    public com.apatar.salesforcecom.ws.DescribeDataCategoryGroupResult[] describeDataCategoryGroups(java.lang.String[] sObjectType) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Describe the data category group structures for a given set
     * of pair of types and data category group name
     */
    public com.apatar.salesforcecom.ws.DescribeDataCategoryGroupStructureResult[] describeDataCategoryGroupStructures(com.apatar.salesforcecom.ws.DataCategoryGroupSobjectTypePair[] pairs, boolean topCategoriesOnly) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Describe the layout of an sObject
     */
    public com.apatar.salesforcecom.ws.DescribeLayoutResult describeLayout(java.lang.String sObjectType, java.lang.String[] recordTypeIds) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Describe the layout of the SoftPhone
     */
    public com.apatar.salesforcecom.ws.DescribeSoftphoneLayoutResult describeSoftphoneLayout() throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Describe the tabs that appear on a users page
     */
    public com.apatar.salesforcecom.ws.DescribeTabSetResult[] describeTabs() throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Create a set of new sObjects
     */
    public com.apatar.salesforcecom.ws.SaveResult[] create(com.apatar.salesforcecom.ws.SObject[] sObjects) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Update a set of sObjects
     */
    public com.apatar.salesforcecom.ws.SaveResult[] update(com.apatar.salesforcecom.ws.SObject[] sObjects) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Update or insert a set of sObjects based on object id
     */
    public com.apatar.salesforcecom.ws.UpsertResult[] upsert(java.lang.String externalIDFieldName, com.apatar.salesforcecom.ws.SObject[] sObjects) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Merge and update a set of sObjects based on object id
     */
    public com.apatar.salesforcecom.ws.MergeResult[] merge(com.apatar.salesforcecom.ws.MergeRequest[] request) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Delete a set of sObjects
     */
    public com.apatar.salesforcecom.ws.DeleteResult[] delete(java.lang.String[] ids) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Undelete a set of sObjects
     */
    public com.apatar.salesforcecom.ws.UndeleteResult[] undelete(java.lang.String[] ids) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Empty a set of sObjects from the recycle bin
     */
    public com.apatar.salesforcecom.ws.EmptyRecycleBinResult[] emptyRecycleBin(java.lang.String[] ids) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Get a set of sObjects
     */
    public com.apatar.salesforcecom.ws.SObject[] retrieve(java.lang.String fieldList, java.lang.String sObjectType, java.lang.String[] ids) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.MalformedQueryFault, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Submit an entity to a workflow process or process a workitem
     */
    public com.apatar.salesforcecom.ws.ProcessResult[] process(com.apatar.salesforcecom.ws.ProcessRequest[] actions) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * convert a set of leads
     */
    public com.apatar.salesforcecom.ws.LeadConvertResult[] convertLead(com.apatar.salesforcecom.ws.LeadConvert[] leadConverts) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Logout the current user, invalidating the current session.
     */
    public void logout() throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Logs out and invalidates session ids
     */
    public com.apatar.salesforcecom.ws.InvalidateSessionsResult[] invalidateSessions(java.lang.String[] sessionIds) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Get the IDs for deleted sObjects
     */
    public com.apatar.salesforcecom.ws.GetDeletedResult getDeleted(java.lang.String sObjectType, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Get the IDs for updated sObjects
     */
    public com.apatar.salesforcecom.ws.GetUpdatedResult getUpdated(java.lang.String sObjectType, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Create a Query Cursor
     */
    public com.apatar.salesforcecom.ws.QueryResult query(java.lang.String queryString) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.MalformedQueryFault, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault, com.apatar.salesforcecom.ws.InvalidQueryLocatorFault;

    /**
     * Create a Query Cursor, including deleted sObjects
     */
    public com.apatar.salesforcecom.ws.QueryResult queryAll(java.lang.String queryString) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.MalformedQueryFault, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault, com.apatar.salesforcecom.ws.InvalidQueryLocatorFault;

    /**
     * Gets the next batch of sObjects from a query
     */
    public com.apatar.salesforcecom.ws.QueryResult queryMore(java.lang.String queryLocator) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault, com.apatar.salesforcecom.ws.InvalidQueryLocatorFault;

    /**
     * Search for sObjects
     */
    public com.apatar.salesforcecom.ws.SearchResult search(java.lang.String searchString) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidSObjectFault, com.apatar.salesforcecom.ws.MalformedSearchFault, com.apatar.salesforcecom.ws.InvalidFieldFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Gets server timestamp
     */
    public com.apatar.salesforcecom.ws.GetServerTimestampResult getServerTimestamp() throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Set a user's password
     */
    public com.apatar.salesforcecom.ws.SetPasswordResult setPassword(java.lang.String userId, java.lang.String password) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault, com.apatar.salesforcecom.ws.InvalidNewPasswordFault;

    /**
     * Reset a user's password
     */
    public com.apatar.salesforcecom.ws.ResetPasswordResult resetPassword(java.lang.String userId) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.InvalidIdFault, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Returns standard information relevant to the current user
     */
    public com.apatar.salesforcecom.ws.GetUserInfoResult getUserInfo() throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;

    /**
     * Send outbound email
     */
    public com.apatar.salesforcecom.ws.SendEmailResult[] sendEmail(com.apatar.salesforcecom.ws.Email[] messages) throws java.rmi.RemoteException, com.apatar.salesforcecom.ws.UnexpectedErrorFault;
}
