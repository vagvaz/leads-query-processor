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

package com.apatar.flickr;

import java.util.HashMap;
import java.util.Map;

import com.apatar.core.ETableMode;
import com.apatar.flickr.function.DownloadPhotoFlickrTable;
import com.apatar.flickr.function.PagingFlickrTable;
import com.apatar.flickr.function.UploadPhotoFlickrTable;
import com.apatar.flickr.objects.Function;

public class FlickrTableList{

	private static Map<String, FlickrTable> flickrTables = new HashMap<String, FlickrTable>();
	
	// utility function
	private static void PutAttributeList(HashMap<String, Object> map, String[] attList)
	{
		map.clear();
		for (String str : attList)
			map.put(str, String.format("./@%s", str));
	}
	
	private static void PutTagAttributeList(HashMap<String, Object> map, String pathToTag, String prefix, String[] attList)
	{
		for (String str : attList)
			map.put(prefix+"_"+str, String.format(pathToTag+"/@%s", str));
	}
	
	private static void PutNulls(HashMap<String, Object> map , String[] values)
	{
		map.clear();
		for (String str : values)
			map.put(str, null);
	}
	
	
	static{
		//***************************************************
		// 					flickr.activity
		//***************************************************
		HashMap<String, Object> optionals = new HashMap<String, Object>(); 
		HashMap<String, Object> returns = new HashMap<String, Object>();
		
		optionals.clear();
		PutAttributeList(returns, new String[]{"type", "id", "owner", "primary", "secret", "server", "comments", "views", "photos", "more", "notes", "faves",
				"commentsold", "commentsnew", "notesold", "notesnew"});
		
		flickrTables.put("flickr.activity.userComments", new PagingFlickrTable("flickr.activity.userComments", 
				"Returns a list of recent activity on photos commented on by the calling user", ETableMode.ReadOnly, 
				true, "//item",
				optionals, returns, 50));


		optionals.clear();
		optionals.put("timeframe", "365d");
		PutAttributeList(returns, new String[]{"type", "id", "owner", "primary", "secret", "server", "comments", "views", "photos", "more", "notes", "faves",
				"commentsold", "commentsnew", "notesold", "notesnew"});
		flickrTables.put("flickr.activity.userPhotos", new PagingFlickrTable("flickr.activity.userPhotos", "Returns a list of recent activity on photos belonging to the calling user", ETableMode.ReadOnly, 
				true, "//item", 
				optionals, returns, 50));
		
		
		
		//*********************************************************************
		//flickr.blogs
		//***************************************************
		optionals.clear();
		PutAttributeList(returns, new String[]{"id", "name", "needpassword", "url"});
		flickrTables.put("flickr.blogs.getList", new FlickrTable("flickr.blogs.getList", "Get a list of configured blogs for the calling user", ETableMode.ReadOnly, 
				false, "//blog",
				optionals, returns));
		
		returns.clear();
		PutNulls(optionals, new String[] {
				"blog_id", //(Required) 
				//The id of the blog to post to 
				"photo_id", //(Required) 
				//The id of the photo to blog 
				"title", //(Required) 
				//The blog post title 
				"description", //(Required) 
				//The blog post body 
				"blog_password", //(Optional) 
				//The password for the blog (used when the blog does not have a stored password). 
		});
		flickrTables.put("flickr.blogs.postPhoto", new FlickrTable("flickr.blogs.postPhoto", "Post photo", ETableMode.WriteOnly, 
				false, "//blog",
				optionals, returns));
		
		
		
		/*
		flickrTables.add(new FlickrTable("flickr.blogs.getList", "", ETableMode.ReadOnly, false, null));
	    
		tbl		= new FlickrTable("flickr.blogs.postPhoto");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "blog_id" );
		args.add( "photo_id" );
		args.add( "title" );
		args.add( "description" );
		args.add( "blog_password" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    */

	
		//*********************************************************************
		//flickr.contacts
		//***************************************************
		optionals.clear();
		optionals.put("filter", "both");
		PutAttributeList(returns, new String[]{"nsid", "username", "iconserver", "realname", "friend", "family", "ignored"});
		flickrTables.put("flickr.contacts.getList", new PagingFlickrTable("flickr.contacts.getList", "Get a list of contacts for the calling user", ETableMode.ReadOnly, 
				true, "//contact",
				optionals, returns, 1000));
		
		/*
		tbl		= new FlickrTable("flickr.contacts.getPublicList");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "page" );
		args.add( "per_page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "user_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    */
		
		
		//***************************
		//favorites
		//***************************
		optionals.put("photo_id ", null);
		returns.clear();
		flickrTables.put("flickr.favorites.add", new FlickrTable("flickr.favorites.add", "Adds a photo to a user's favorites list", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.write));
		
		/*optionals.put("photo_id ", null);
		returns.clear();
		flickrTables.add(new FlickrTable("flickr.favorites.add", "", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.write));
		*/
		
		optionals.put("photo_id ", null);
		returns.clear();
		flickrTables.put("flickr.favorites.remove", new FlickrTable("flickr.favorites.remove", "Removes a photo from a user's favorites list", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.write));
		
/*
		//favorites
		tbl		= new FlickrTable("flickr.favorites.add");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.favorites.getList");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "user_id" );
		args.add( "extras" );
		args.add( "page" );
		args.add( "per_page" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );

		tbl		= new FlickrTable("flickr.favorites.getPublicList");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "user_id" );
		args.add( "extras" );
		args.add( "page" );
		args.add( "per_page" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.favorites.remove");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "photo_id" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );
	    
		//groups
		tbl		= new FlickrTable("flickr.groups.browse");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "cat_id" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );

		tbl		= new FlickrTable("flickr.groups.getInfo");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "group_id" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.groups.search");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "text" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		//groups.pools
		tbl		= new FlickrTable("flickr.groups.pools.add");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "group_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.groups.pools.getContext");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "group_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.groups.pools.getGroups");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "page" );
		args.add( "per_page" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.groups.pools.getPhotos");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "page" );
		args.add( "per_page" );
		args.add( "extras" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "group_id" );
		args.add( "tags" );
		args.add( "user_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.groups.pools.remove");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "group_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		//interestingness
		tbl		= new FlickrTable("flickr.interestingness.getList");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "date" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    	
		//people
		tbl		= new FlickrTable("flickr.people.findByEmail");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "find_email" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );

		tbl		= new FlickrTable("flickr.people.findByUsername");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "username" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.people.getInfo");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "user_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.people.getInfo");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "user_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.people.getPublicGroups");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "user_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.people.getPublicPhotos");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "user_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.people.getUploadStatus");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );
*/	    
		
		//********************************************
		// groups
		//********************************************
		optionals.clear();
		returns.clear();
		flickrTables.put("flickr.groups.browse", new FlickrTable("flickr.groups.browse", "Browse the group category tree, finding groups and sub-categories", ETableMode.WriteOnly, 
				true, "",
				optionals, returns));//?????

		//********************************************
		// groups.pools
		//********************************************
		PutNulls(optionals, new String[] {
				"photo_id", //(Required) 
				//The id of the photo to blog 
				"group_id", // (Required) 
				//The NSID of the group who's pool the photo is to be added to. 
		});
		returns.clear();
		flickrTables.put("flickr.groups.pools.add", new FlickrTable("flickr.groups.pools.add", "Add a photo to a group's pool", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.write));
		
		PutNulls(optionals, new String[] {
				"photo_id", //(Required) 
				//The id of the photo to blog 
				"group_id", // (Required) 
				//The NSID of the group who's pool the photo is to be added to. 
		});
		returns.clear();
		flickrTables.put("flickr.groups.pools.remove", new FlickrTable("flickr.groups.pools.remove", "Remove a photo from a group pool", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.write));
		
		optionals.clear();
		PutTagAttributeList(returns, "//group", "group", new String[]{"nsid", "name", "admin", "privacy", "photos", "iconserver"});
		flickrTables.put("flickr.groups.pools.getGroups", new PagingFlickrTable("flickr.groups.pools.getGroups", "Returns a list of groups to which you can add photos", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.read, 400));
		
		optionals.clear();
		optionals.put("group_id", new Function("flickr.groups.pools.getGroups", "nsid"));
		optionals.put("extras" , "date_upload, date_taken, owner_name, icon_server, original_format, last_update, geo, tags, machine_tags");
		PutTagAttributeList(returns, "//group", "group", new String[]{"nsid", "name", "admin", "privacy", "photos", "iconserver"});
		flickrTables.put("flickr.groups.pools.getPhotos", new PagingFlickrTable("flickr.groups.pools.getPhotos", "Returns a list of pool photos for a given group", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.read, 400));
		
		returns.clear();
		returns.put("prevphoto_id", "//prevphoto/@id");
		returns.put("prevphoto_secret", "//prevphoto/@secret");
		returns.put("prevphoto_url", "//prevphoto/@url");
		returns.put("prevphoto_title", "//prevphoto/@title");
		returns.put("nextphoto_id", "//nextphoto/@id");
		returns.put("nextphoto_secret", "//nextphoto/@secret");
		returns.put("nextphoto_url", "//nextphoto/@url");
		returns.put("nextphoto_title", "//nextphoto/@title");
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to fetch the context for. 
				"group_id",// (Required) 
				//The nsid of the group who's pool to fetch the photo's context for. 

		});
		flickrTables.put("flickr.groups.pools.getContext", new FlickrTable("flickr.people.findByUsername", "Return a user's NSID, given their username", ETableMode.ReadWrite, 
				true, "",
				optionals, returns, FlickrPermission.read));
		
		//***********************************************************************
		//			flickr.photos
		//***********************************************************************
		PutNulls(optionals, new String[]{"photo", "title", "description", "is_public", "is_friend", "is_family"});
		returns.clear();
		returns.put("photoid", "//photoid/text()");
		flickrTables.put("_UploadPhoto", new UploadPhotoFlickrTable("_UploadPhoto", "Upload photo", ETableMode.ReadWrite, 
				false, "//photoid",
				optionals, returns,
				FlickrPermission.write,
				"http://api.flickr.com/services/upload/"));
		
		PutNulls(optionals, new String[]{"photo_id"});
		returns.clear();
		returns.put("photo", "");
		flickrTables.put("_DownloadPhoto", new DownloadPhotoFlickrTable("_DownloadPhoto", "Download photo", ETableMode.ReadWrite, 
				false, "",
				optionals, returns,
				FlickrPermission.read));

		//**********************************
		// photos
		//**********************************
		returns.clear();
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to add tags to. 
				"tags"// (Required) 
				//The tags to add to the photo. 
		});
		flickrTables.put("flickr.photos.addTags", new FlickrTable("flickr.photos.addTags", "Add tags to a photo", ETableMode.WriteOnly, 
				true, "",
				optionals, returns));
		
		returns.clear();
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to add tags to. 
		});
		flickrTables.put("flickr.photos.delete", new FlickrTable("flickr.photos.delete", "Delete a photo from flickr", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.delete));
		
		returns.clear();
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to add tags to. 
				"tag_id" // (Required) 
		});
		flickrTables.put("flickr.photos.removeTag", new FlickrTable("flickr.photos.removeTag", "Remove a tag from a photo", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.write));
		
		returns.clear();
		PutTagAttributeList(returns, "//set", "set", new String[]{"id", "title"});
		PutTagAttributeList(returns, "//pool", "pool", new String[]{"id", "title"});
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to add tags to. 
		});
		flickrTables.put("flickr.photos.getAllContexts", new FlickrTable("flickr.photos.getAllContexts", "Returns all visible sets and pools the photo belongs to", ETableMode.ReadWrite, 
				true, "",
				optionals, returns));
		
		PutAttributeList(returns, new String[]{"id", "owner", "secret", "server", "username", "title"});
		PutNulls(optionals, new String[]{
				"count", // (Optional) 
				//Number of photos to return. Defaults to 10, maximum 50. This is only used if single_photo is not passed. 
				"just_friends", // (Optional) 
				//set as 1 to only show photos from friends and family (excluding regular contacts). 
				"single_photo", // (Optional) 
				//Only fetch one photo (the latest) per contact, instead of all photos in chronological order. 
				"include_self", // (Optional) 
				//Set to 1 to include photos from the calling user. 
				"extras", // (Optional) 
				//A comma-delimited list of extra information to fetch for each returned record. Currently supported fields are: license, date_upload, date_taken, owner_name, icon_server, original_format, last_update. 

		});
		flickrTables.put("flickr.photos.getContactsPhotos", new FlickrTable("flickr.photos.getContactsPhotos", "Fetch a list of recent photos from the calling users' contacts", ETableMode.ReadWrite, //?????
				true, "",
				optionals, returns));
		
		PutAttributeList(returns, new String[]{"id", "owner", "secret", "server", "username", "title"});
		PutNulls(optionals, new String[]{
				"user_id", // (Required) 
				//The NSID of the user to fetch photos for. 
				"count", // (Optional) 
				//Number of photos to return. Defaults to 10, maximum 50. This is only used if single_photo is not passed. 
				"just_friends", // (Optional) 
				//set as 1 to only show photos from friends and family (excluding regular contacts). 
				"single_photo", // (Optional) 
				//Only fetch one photo (the latest) per contact, instead of all photos in chronological order. 
				"include_self", // (Optional) 
				//Set to 1 to include photos from the calling user. 
				"extras", // (Optional) 
				//A comma-delimited list of extra information to fetch for each returned record. Currently supported fields are: license, date_upload, date_taken, owner_name, icon_server, original_format, last_update. 

		});
		flickrTables.put("flickr.photos.getPublicContactsPhotos", new FlickrTable("flickr.photos.getContactsPublicPhotos", "Fetch a list of recent public photos from a users' contacts", ETableMode.ReadWrite, //?????
				true, "",
				optionals, returns));
		
		PutAttributeList(returns, new String[]{"id", "secret", "title", "url"});
		PutNulls(optionals, new String[]{
				"photo_id"
		});
		flickrTables.put("flickr.photos.getContext", new FlickrTable("flickr.photos.getContext", "Returns next and previous photos for a photo in a photostream", ETableMode.ReadWrite, //?????
				true, "",
				optionals, returns));
		
		PutAttributeList(returns, new String[]{"count", "fromdate", "todate"});
		PutNulls(optionals, new String[]{
				"dates", // (Optional) 
				//A comma delimited list of unix timestamps, denoting the periods to return counts for. They should be specified smallest first. 
				"taken_dates", // (Optional) 
				//A comma delimited list of mysql datetimes, denoting the periods to return counts for. They should be specified smallest first. 

		});
		flickrTables.put("flickr.photos.getContext", new FlickrTable("flickr.photos.getContext", "", ETableMode.ReadWrite, //?????
				true, "",
				optionals, returns));
		
		PutAttributeList(returns, new String[]{"tagspace", "tagspaceid", "tag", "label"});
		returns.put("raw", ".//raw/text()");
		PutNulls(optionals, new String[]{
				"photo_id", // (Required) 
				//The id of the photo to fetch information for. 
				"secret" // (Optional) 
				//The secret for the photo. If the correct secret is passed then permissions checking is skipped. This enables the 'sharing' of individual photos by passing around the id and secret. 
		});
		flickrTables.put("flickr.photos.getContext", new FlickrTable("flickr.photos.getContext", "", ETableMode.ReadWrite, //?????
				true, "",
				optionals, returns));
		
		PutAttributeList(returns, new String[]{"count", "fromdate", "todate"});
		PutNulls(optionals, new String[]{
				"photo_id", // (Required) 
				//The id of the photo to fetch information for. 
				"secret", // (Optional) 
				//The secret for the photo. If the correct secret is passed then permissions checking is skipped. This enables the 'sharing' of individual photos by passing around the id and secret. 
		});
		flickrTables.put("flickr.photos.getExif", new FlickrTable("flickr.photos.getExif", "Retrieves a list of EXIF/TIFF/GPS tags for a given photo", ETableMode.ReadWrite, //?????
				true, "",
				optionals, returns));
		
		PutAttributeList(returns, new String[]{"id", "secret", "server",
			"isfavorite", "license", "rotation",
			"originalsecret", "originalformat"});
		PutTagAttributeList(returns, ".//owner", "owner", new String[] {
				"nsid", "username",
				"realname", "location"
		});
		returns.put("title", ".//title");
		returns.put("description", ".//description");
		PutTagAttributeList(returns, ".//visibility", "visibility", new String[]{
			"ispublic", "isfriend", "isfamily"	
		});
		
		PutTagAttributeList(returns, ".//dates", "dates", new String[]{
				"posted", "taken", "takengranularity", "lastupdate"
		});
		
		PutTagAttributeList(returns, ".//permissions", "permissions", new String[]{
				"permcomment", "permaddmeta"
		});
		PutTagAttributeList(returns, ".//editability", "editability", new String[]{
				"cancomment", "canaddmeta"
		});
		returns.put("comments", ".//comments");
		PutNulls(optionals, new String[]{
				"photo_id", // (Required) 
				//The id of the photo to fetch information for. 
				"secret", // (Optional) 
				//The secret for the photo. If the correct secret is passed then permissions checking is skipped. This enables the 'sharing' of individual photos by passing around the id and secret. 
		});
		flickrTables.put("flickr.photos.getInfo", new FlickrTable("flickr.photos.getInfo", "Get information about a photo", ETableMode.ReadWrite, //?????
				true, "",
				optionals, returns));
		
		FlickrTable photosGetSizes = new FlickrTable("flickr.photos.getSizes", "Get information about a photo", ETableMode.ReadWrite, //?????
				true, "",
				null, null);
		photosGetSizes.setHidden(true);
		flickrTables.put("flickr.photos.getSizes", photosGetSizes);
		
		PutNulls(optionals, new String[]{
				"photo_id", // (Required) 
				//The id of the photo to set information for. 
				"title", // (Required) 
				//The title for the photo. 
				"description", // (Required) 
				//The description for the photo. 
		});
		returns.clear();
		flickrTables.put("flickr.photos.setMeta", new FlickrTable("flickr.photos.setMeta", "Set the meta information for a photo", ETableMode.WriteOnly, //?????
				true, "",
				optionals, returns,
				FlickrPermission.write));
		
		
		//***********************************
		// people
		//***********************************
		
		returns.clear();
		returns.put("nsid", "./@nsid");
		PutNulls(optionals, new String[]{
				"username"// (Required) 
		});
		flickrTables.put("flickr.people.findByEmail", new FlickrTable("flickr.people.findByEmail", "Return a user's NSID, given their email address", ETableMode.ReadWrite, 
				true, "",
				optionals, returns, FlickrPermission.read));
		
		returns.clear();
		returns.put("nsid", "./@nsid");
		PutNulls(optionals, new String[]{
				"find_email"// (Required) 
		});
		flickrTables.put("flickr.people.findByUsername", new FlickrTable("flickr.people.findByUsername", "", ETableMode.ReadWrite, 
				true, "",
				optionals, returns, FlickrPermission.read));
		
		//***********************************
		// photos.comments
		// **********************************
		returns.clear();
		returns.put("prevphoto_id", "//prevphoto/@id");
		returns.put("prevphoto_secret", "//prevphoto/@secret");
		returns.put("prevphoto_url", "//prevphoto/@url");
		returns.put("prevphoto_title", "//prevphoto/@title");
		returns.put("nextphoto_id", "//nextphoto/@id");
		returns.put("nextphoto_secret", "//nextphoto/@secret");
		returns.put("nextphoto_url", "//nextphoto/@url");
		returns.put("nextphoto_title", "//nextphoto/@title");
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to fetch the context for. 
				"group_id",// (Required) 
				//The nsid of the group who's pool to fetch the photo's context for. 

		});
		flickrTables.put("flickr.people.findByUsername", new FlickrTable("flickr.people.findByUsername", "", ETableMode.ReadWrite, 
				true, "",
				optionals, returns, FlickrPermission.read));
		
		/*	
		
		//photos
		flickrTable.ass(new FlickrTable("");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "tags" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		tbl		= new FlickrTable("flickr.photos.delete");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );

		tbl		= new FlickrTable("flickr.photos.getAllContexts");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.getContactsPhotos");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "count" );
		args.add( "just_friends" );
		args.add( "single_photo" );
		args.add( "include_self" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.getContactsPublicPhotos");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "user_id" );
		args.add( "count" );
		args.add( "just_friends" );
		args.add( "single_photo" );
		args.add( "include_self" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.getContext");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.getCounts");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "dates" );
		args.add( "taken_dates" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.getExif");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "secret" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.getFavorites");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "page" );
		args.add( "per_page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.getInfo");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "secret" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.getNotInSet");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "privacy_filter" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "min_upload_date" );
		args.add( "max_upload_date" );
		args.add( "min_taken_date" );
		args.add( "max_taken_date" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.getPerms");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.getRecent");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.getSizes");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.getUntagged");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "privacy_filter" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "min_upload_date" );
		args.add( "max_upload_date" );
		args.add( "min_taken_date" );
		args.add( "max_taken_date" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.getWithGeoData");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "privacy_filter" );
		args.add( "sort" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "min_upload_date" );
		args.add( "max_upload_date" );
		args.add( "min_taken_date" );
		args.add( "max_taken_date" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.getWithoutGeoData");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "privacy_filter" );
		args.add( "sort" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "min_upload_date" );
		args.add( "max_upload_date" );
		args.add( "min_taken_date" );
		args.add( "max_taken_date" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.recentlyUpdated");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "min_date" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.removeTag");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "tag_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.search");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		args.add( "extras" );
		args.add( "privacy_filter" );
		args.add( "sort" );
		args.add( "per_page" );
		args.add( "page" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "user_id" );
		args.add( "tags" );
		args.add( "tag_mode" );
		args.add( "text" );
		args.add( "min_upload_date" );
		args.add( "max_upload_date" );
		args.add( "min_taken_date" );
		args.add( "max_taken_date" );
		args.add( "license" );
		args.add( "bbox" );
		args.add( "accuracy" );
		args.add( "machine_tags" );
		args.add( "machine_tag_mode" );
		args.add( "group_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.setDates");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "date_posted" );
		args.add( "date_taken" );
		args.add( "date_taken_granularity" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );

		tbl		= new FlickrTable("flickr.photos.setMeta");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "title" );
		args.add( "description" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.setPerms");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "is_public" );
		args.add( "is_friend" );
		args.add( "is_family" );
		args.add( "perm_comment" );
		args.add( "perm_addmeta" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.setTags");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "tags" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		//photos.comments
		tbl		= new FlickrTable("flickr.photos.comments.addComment");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "comment_text" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );

		tbl		= new FlickrTable("flickr.photos.comments.deleteComment");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "comment_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.comments.editComment");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "comment_id" );
		args.add( "comment_text" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.comments.getList");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		//photos.geo
		tbl		= new FlickrTable("flickr.photos.geo.getLocation");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.geo.getPerms");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.geo.removeLocation");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.geo.setLocation");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "lat" );
		args.add( "lon" );
		args.add( "accuracy" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.geo.setLocation");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "is_public" );
		args.add( "is_contact" );
		args.add( "is_friend" );
		args.add( "is_family" );
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		tbl		= new FlickrTable("flickr.photos.geo.setPerms");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "is_public" );
		args.add( "is_contact" );
		args.add( "is_friend" );
		args.add( "is_family" );
		args.add( "photo_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    

		//photos.licenses
		tbl		= new FlickrTable("flickr.photos.licenses.getInfo");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		flickrTables.add( tbl );

		tbl		= new FlickrTable("flickr.photos.licenses.setLicense");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "license_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
	    
		//photos.notes
		tbl		= new FlickrTable("flickr.photos.notes.add");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "note_x" );
		args.add( "note_y" );
		args.add( "note_w" );
		args.add( "note_h" );
		args.add( "note_text" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );

		tbl		= new FlickrTable("flickr.photos.notes.delete");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "note_id" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		tbl		= new FlickrTable("flickr.photos.notes.edit");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "note_id" );
		args.add( "note_x" );
		args.add( "note_y" );
		args.add( "note_w" );
		args.add( "note_h" );
		args.add( "note_text" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
	    
		//photos.transform
		tbl		= new FlickrTable("flickr.photos.transform.rotate");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "photo_id" );
		args.add( "degrees" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );
		
		//photos.upload
		tbl		= new FlickrTable("flickr.photos.upload.checkTickets");
		args	= new ArrayList<String>();
		args.add( "api_key" );
		tbl.setPrivateArguments( args );
		args	= new ArrayList<String>();
		args.add( "tickets" );
		tbl.setPublicArguments( args );
		flickrTables.add( tbl );*/
		
		//photosets
		
		//************************************
		// photos.licenses
		//************************************
		optionals.clear();
		PutAttributeList(returns, new String[]{
			"id", "name", "url"
		});
		flickrTables.put("flickr.photos.licenses.getInfo", new FlickrTable("flickr.photos.licenses.getInfo", "", ETableMode.ReadWrite, 
				true, "//license",
				optionals, returns, FlickrPermission.read));
		returns.clear();
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				"license_id",// (Required) 

		});
		flickrTables.put("flickr.photos.licenses.setLicense", new FlickrTable("flickr.photos.licenses.setLicense", "", ETableMode.ReadWrite, 
				true, "",
				optionals, returns, FlickrPermission.write));
		
		//***********************************
		// photos.notes
		//***********************************
		returns.clear();
		returns.put("id", "./@id");
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to add a note to 
				"note_x",// (Required) 
				//The left coordinate of the note 
				"note_y",// (Required) 
				//The top coordinate of the note 
				"note_w",// (Required) 
				//The width of the note 
				"note_h",// (Required) 
				//The height of the note 
				"note_text",// (Required) 
				//The description of the note 
		});
		flickrTables.put("flickr.photos.notes.add", new FlickrTable("flickr.photos.notes.add", "", ETableMode.ReadWrite, 
				true, "//note",
				optionals, returns, FlickrPermission.write));
		
		returns.clear();
		PutNulls(optionals, new String[]{
				"note_id",// (Required) 
			//	The id of the note to delete 
		});
		flickrTables.put("flickr.photos.notes.delete", new FlickrTable("flickr.photos.notes.delete", "", ETableMode.WriteOnly, 
				true, "",
				optionals, returns, FlickrPermission.write));
		
		returns.clear();
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to add a note to 
				"note_x",// (Required) 
				//The left coordinate of the note 
				"note_y",// (Required) 
				//The top coordinate of the note 
				"note_w",// (Required) 
				//The width of the note 
				"note_h",// (Required) 
				//The height of the note 
				"note_text",// (Required) 
				//The description of the note 
		});
		flickrTables.put("flickr.photos.notes.edit", new FlickrTable("flickr.photos.notes.edit", "", ETableMode.WriteOnly, 
				true, "//note",
				optionals, returns, FlickrPermission.write));
		
		//***********************************
		// photos.transform
		//***********************************
		returns.clear();
		PutNulls(optionals, new String[]{
				"photo_id",// (Required) 
				//The id of the photo to rotate. 
				"degrees",// (Required) 
				//The amount of degrees by which to rotate the photo (clockwise) from it's current orientation. Valid values are 90, 180 and 270. 
		});
		flickrTables.put("flickr.photos.transform.rotate", new FlickrTable("flickr.photos.transform.rotate", "Rotate a photo", ETableMode.WriteOnly, 
				true, "//note",
				optionals, returns, FlickrPermission.write));
		
		//**********************************
		// reflection
		//**********************************
		returns.clear();
		optionals.clear();
		flickrTables.put("flickr.reflection.getMethods", new FlickrTable("flickr.reflection.getMethods", "Returns a list of available flickr API methods", ETableMode.ReadOnly, //?????
				true, "//metod",
				optionals, returns));
		

	    /** flickr.photosets.addPhoto
	    * flickr.photosets.create
	    * flickr.photosets.delete
	    * flickr.photosets.editMeta
	    * flickr.photosets.editPhotos
	    * flickr.photosets.getContext
	    * flickr.photosets.getInfo
	    * flickr.photosets.getList
	    * flickr.photosets.getPhotos
	    * flickr.photosets.orderSets
	    * flickr.photosets.removePhoto

	photosets.comments

	    * flickr.photosets.comments.addComment
	    * flickr.photosets.comments.deleteComment
	    * flickr.photosets.comments.editComment
	    * flickr.photosets.comments.getList

	reflection

	    * flickr.reflection.getMethodInfo
	    * flickr.reflection.getMethods

	tags

	    * flickr.tags.getHotList
	    * flickr.tags.getListPhoto
	    * flickr.tags.getListUser
	    * flickr.tags.getListUserPopular
	    * flickr.tags.getListUserRaw
	    * flickr.tags.getRelated

	test

	    * flickr.test.echo
	    * flickr.test.login
	    * flickr.test.null

	urls

	    * flickr.urls.getGroup
	    * flickr.urls.getUserPhotos
	    * flickr.urls.getUserProfile
	    * flickr.urls.lookupGroup
	    * flickr.urls.lookupUser*/

		
	}
	
	public static Map<String, FlickrTable> getFlickrTablesList(){
		return flickrTables;
	}
	
	public static FlickrTable getTableByName( String name ){
		return flickrTables.get(name);
	}
	
}
