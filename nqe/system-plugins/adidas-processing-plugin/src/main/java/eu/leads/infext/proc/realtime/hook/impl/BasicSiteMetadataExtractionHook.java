package eu.leads.infext.proc.realtime.hook.impl;

import java.io.IOException;
import java.util.HashMap;

import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.infext.proc.com.geo.SiteIPGeolocalization;
import eu.leads.infext.proc.realtime.hook.AbstractHook;
import eu.leads.utils.LEADSUtils;

public class BasicSiteMetadataExtractionHook extends AbstractHook {
	
	SiteIPGeolocalization siteGeolocalization = new SiteIPGeolocalization();

	@Override
	public HashMap<String, HashMap<String, Object>> retrieveMetadata(
			String siteUri, String timestamp,
			HashMap<String, HashMap<String, Object>> currentMetadata,
			HashMap<String, MDFamily> editableFamilies) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, Object>> process(
			HashMap<String, HashMap<String, Object>> parameters) {
		
		HashMap<String, Object> general0VersionParams = parameters.get("general0");
		String baseUrl = general0VersionParams.get("url").toString();
		
		String domainName = LEADSUtils.nutchUrlToFullyQualifiedDomainName(baseUrl);
		System.out.println(domainName);
		
		/*
		 * CORE
		 */
		String sitecountry = siteGeolocalization.getDomainCountry(domainName);
		/*
		 * CORE
		 */
		
		HashMap<String, Object> general0VersionResult = new HashMap<String, Object>();
		HashMap<String, HashMap<String, Object>> result = new HashMap<String, HashMap<String, Object>>();
		
		general0VersionResult.put("siteipcountry", sitecountry);
		result.put("general0", general0VersionResult);
		
		return result;
	}

}
