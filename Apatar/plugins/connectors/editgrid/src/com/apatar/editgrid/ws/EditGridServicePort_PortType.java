/**
 * EditGridServicePort_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.editgrid.ws;

public interface EditGridServicePort_PortType extends java.rmi.Remote {
    public com.apatar.editgrid.ws.SessionInfo doAuthGetSessionInfo(com.apatar.editgrid.ws.AuthGetSessionInfoRequest parameters) throws java.rmi.RemoteException;
    public java.lang.String doAuthGetSessionKey(com.apatar.editgrid.ws.AuthGetSessionKeyRequest parameters) throws java.rmi.RemoteException;
    public java.lang.String doAuthCreateSessionKey(com.apatar.editgrid.ws.AuthCreateSessionKeyRequest parameters) throws java.rmi.RemoteException;
    public java.lang.String doAuthCreateToken(com.apatar.editgrid.ws.AuthCreateTokenRequest parameters) throws java.rmi.RemoteException;
    public int doCellSetSize(com.apatar.editgrid.ws.CellSetSizeRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Cell[] doCellList(com.apatar.editgrid.ws.CellListRequest parameters) throws java.rmi.RemoteException;
    public int doCellClear(com.apatar.editgrid.ws.CellClearRequest parameters) throws java.rmi.RemoteException;
    public int doCellSetStyle(com.apatar.editgrid.ws.CellSetStyleRequest parameters) throws java.rmi.RemoteException;
    public int doCellInsert(com.apatar.editgrid.ws.CellInsertRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Cell[] doCellSetInput(com.apatar.editgrid.ws.CellSetInputRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Cell[] doCellSet(com.apatar.editgrid.ws.CellSetRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.User[] doUserList(com.apatar.editgrid.ws.UserListRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.User[] doUserQuery(com.apatar.editgrid.ws.UserQueryRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.User doUserCreate(com.apatar.editgrid.ws.UserCreateRequest parameters) throws java.rmi.RemoteException;
    public int doUserDelete(com.apatar.editgrid.ws.UserDeleteRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.User doUserGet(com.apatar.editgrid.ws.UserGetRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Share[] doShareQuery(com.apatar.editgrid.ws.ShareQueryRequest parameters) throws java.rmi.RemoteException;
    public int doShareSet(com.apatar.editgrid.ws.ShareSetRequest parameters) throws java.rmi.RemoteException;
    public int doShareDelete(com.apatar.editgrid.ws.ShareDeleteRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Share doShareGet(com.apatar.editgrid.ws.ShareGetRequest parameters) throws java.rmi.RemoteException;
    public int doWorkbookPurge(com.apatar.editgrid.ws.WorkbookPurgeRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Binary doWorkbookExport(com.apatar.editgrid.ws.WorkbookExportRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Workbook doWorkbookClone(com.apatar.editgrid.ws.WorkbookCloneRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Workbook doWorkbookCreateRemote(com.apatar.editgrid.ws.WorkbookCreateRemoteRequest parameters) throws java.rmi.RemoteException;
    public int doWorkbookRestore(com.apatar.editgrid.ws.WorkbookRestoreRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Workbook[] doWorkbookList(com.apatar.editgrid.ws.WorkbookListRequest parameters) throws java.rmi.RemoteException;
    public int doWorkbookImport(com.apatar.editgrid.ws.WorkbookImportRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Workbook[] doWorkbookQuery(com.apatar.editgrid.ws.WorkbookQueryRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Workbook doWorkbookCreate(com.apatar.editgrid.ws.WorkbookCreateRequest parameters) throws java.rmi.RemoteException;
    public int doWorkbookUpdate(com.apatar.editgrid.ws.WorkbookUpdateRequest parameters) throws java.rmi.RemoteException;
    public java.lang.String doWorkbookCreateAccessToken(com.apatar.editgrid.ws.WorkbookCreateAccessTokenRequest parameters) throws java.rmi.RemoteException;
    public int doWorkbookDelete(com.apatar.editgrid.ws.WorkbookDeleteRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Workbook doWorkbookGet(com.apatar.editgrid.ws.WorkbookGetRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.SheetObject[] doSheetObjectList(com.apatar.editgrid.ws.SheetObjectListRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Revision doRevisionMark(com.apatar.editgrid.ws.RevisionMarkRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Revision doRevisionRollback(com.apatar.editgrid.ws.RevisionRollbackRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Revision[] doRevisionQuery(com.apatar.editgrid.ws.RevisionQueryRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Revision doRevisionCreate(com.apatar.editgrid.ws.RevisionCreateRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Revision doRevisionUnmark(com.apatar.editgrid.ws.RevisionUnmarkRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Revision doRevisionGet(com.apatar.editgrid.ws.RevisionGetRequest parameters) throws java.rmi.RemoteException;
    public int doWorksheetMove(com.apatar.editgrid.ws.WorksheetMoveRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Worksheet[] doWorksheetList(com.apatar.editgrid.ws.WorksheetListRequest parameters) throws java.rmi.RemoteException;
    public int doWorksheetUpdateLinked(com.apatar.editgrid.ws.WorksheetUpdateLinkedRequest parameters) throws java.rmi.RemoteException;
    public int doWorksheetCreate(com.apatar.editgrid.ws.WorksheetCreateRequest parameters) throws java.rmi.RemoteException;
    public int doWorksheetUpdate(com.apatar.editgrid.ws.WorksheetUpdateRequest parameters) throws java.rmi.RemoteException;
    public int doWorksheetDelete(com.apatar.editgrid.ws.WorksheetDeleteRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Worksheet doWorksheetGet(com.apatar.editgrid.ws.WorksheetGetRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Workspace doWorkspaceCreate(com.apatar.editgrid.ws.WorkspaceCreateRequest parameters) throws java.rmi.RemoteException;
    public com.apatar.editgrid.ws.Workspace doWorkspaceGet(com.apatar.editgrid.ws.WorkspaceGetRequest parameters) throws java.rmi.RemoteException;
}
