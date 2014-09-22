/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

��� This program is free software; you can redistribute it and/or modify
��� it under the terms of the GNU General Public License as published by
��� the Free Software Foundation; either version 2 of the License, or
��� (at your option) any later version.

��� This program is distributed in the hope that it will be useful,
��� but WITHOUT ANY WARRANTY; without even the implied warranty of
��� MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the
��� GNU General Public License for more details.

��� You should have received a copy of the GNU General Public License along
��� with this program; if not, write to the Free Software Foundation, Inc.,
��� 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

/**
 * SugarsoapPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package com.apatar.sugarcrm.ws;

public interface SugarsoapPortType extends java.rmi.Remote {
    public java.lang.String create_session(java.lang.String user_name, java.lang.String password) throws java.rmi.RemoteException;
    public java.lang.String end_session(java.lang.String user_name) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Contact_detail[] contact_by_email(java.lang.String user_name, java.lang.String password, java.lang.String email_address) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.User_detail[] user_list(java.lang.String user_name, java.lang.String password) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Contact_detail[] search(java.lang.String user_name, java.lang.String password, java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String track_email(java.lang.String user_name, java.lang.String password, java.lang.String parent_id, java.lang.String contact_ids, java.util.Date date_sent, java.lang.String email_subject, java.lang.String email_body) throws java.rmi.RemoteException;
    public java.lang.String create_contact(java.lang.String user_name, java.lang.String password, java.lang.String first_name, java.lang.String last_name, java.lang.String email_address) throws java.rmi.RemoteException;
    public java.lang.String create_lead(java.lang.String user_name, java.lang.String password, java.lang.String first_name, java.lang.String last_name, java.lang.String email_address) throws java.rmi.RemoteException;
    public java.lang.String create_account(java.lang.String user_name, java.lang.String password, java.lang.String name, java.lang.String phone, java.lang.String website) throws java.rmi.RemoteException;
    public java.lang.String create_opportunity(java.lang.String user_name, java.lang.String password, java.lang.String name, java.lang.String amount) throws java.rmi.RemoteException;
    public java.lang.String create_case(java.lang.String user_name, java.lang.String password, java.lang.String name) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Set_entry_result login(com.apatar.sugarcrm.ws.User_auth user_auth, java.lang.String application_name) throws java.rmi.RemoteException;
    public int is_loopback() throws java.rmi.RemoteException;
    public int seamless_login(java.lang.String session) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_entry_list_result get_entry_list(java.lang.String session, java.lang.String module_name, java.lang.String query, java.lang.String order_by, int offset, java.lang.String[] select_fields, int max_results, int deleted) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_entry_result get_entry(java.lang.String session, java.lang.String module_name, java.lang.String id, java.lang.String[] select_fields) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_entry_result get_entries(java.lang.String session, java.lang.String module_name, java.lang.String[] ids, java.lang.String[] select_fields) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Set_entry_result set_entry(java.lang.String session, java.lang.String module_name, com.apatar.sugarcrm.ws.Name_value[] name_value_list) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Set_entries_result set_entries(java.lang.String session, java.lang.String module_name, com.apatar.sugarcrm.ws.Name_value[][] name_value_lists) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Set_entry_result set_note_attachment(java.lang.String session, com.apatar.sugarcrm.ws.Note_attachment note) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Return_note_attachment get_note_attachment(java.lang.String session, java.lang.String id) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Error_value relate_note_to_module(java.lang.String session, java.lang.String note_id, java.lang.String module_name, java.lang.String module_id) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_entry_result get_related_notes(java.lang.String session, java.lang.String module_name, java.lang.String module_id, java.lang.String[] select_fields) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Error_value logout(java.lang.String session) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Module_fields get_module_fields(java.lang.String session, java.lang.String module_name) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Module_list get_available_modules(java.lang.String session) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Error_value update_portal_user(java.lang.String session, java.lang.String portal_name, com.apatar.sugarcrm.ws.Name_value[] name_value_list) throws java.rmi.RemoteException;
    public java.lang.String test(java.lang.String string) throws java.rmi.RemoteException;
    public java.lang.String get_user_id(java.lang.String session) throws java.rmi.RemoteException;
    public java.lang.String get_user_team_id(java.lang.String session) throws java.rmi.RemoteException;
    public java.lang.String get_server_time() throws java.rmi.RemoteException;
    public java.lang.String get_gmt_time() throws java.rmi.RemoteException;
    public java.lang.String get_server_version() throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_relationships_result get_relationships(java.lang.String session, java.lang.String module_name, java.lang.String module_id, java.lang.String related_module, java.lang.String related_module_query, int deleted) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Error_value set_relationship(java.lang.String session, com.apatar.sugarcrm.ws.Set_relationship_value set_relationship_value) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Set_relationship_list_result set_relationships(java.lang.String session, com.apatar.sugarcrm.ws.Set_relationship_value[] set_relationship_list) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Set_entry_result set_document_revision(java.lang.String session, com.apatar.sugarcrm.ws.Document_revision note) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_entry_list_result search_by_module(java.lang.String user_name, java.lang.String password, java.lang.String search_string, java.lang.String[] modules, int offset, int max_results) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_entry_list_result_encoded sync_get_modified_relationships(java.lang.String session, java.lang.String module_name, java.lang.String related_module, java.lang.String from_date, java.lang.String to_date, int offset, int max_results, int deleted, java.lang.String module_id, java.lang.String[] select_fields, java.lang.String[] ids, java.lang.String relationship_name, java.lang.String deletion_date, int php_serialize) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_sync_result_encoded get_modified_entries(java.lang.String session, java.lang.String module_name, java.lang.String[] ids, java.lang.String[] select_fields) throws java.rmi.RemoteException;
    public com.apatar.sugarcrm.ws.Get_sync_result_encoded get_attendee_list(java.lang.String session, java.lang.String module_name, java.lang.String id) throws java.rmi.RemoteException;
}
