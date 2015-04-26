package eu.leads.infext.proc.realtime.hook.impl;

import java.util.HashMap;

import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.infext.proc.realtime.hook.AbstractHook;
import eu.leads.utils.LEADSUtils;

public class FQDNDefiningHook extends AbstractHook {

	@Override
	public HashMap<String, HashMap<String, Object>> retrieveMetadata(
			String url, String timestamp,
			HashMap<String, HashMap<String, Object>> currentMetadata,
			HashMap<String, MDFamily> editableFamilies) {
		
		HashMap<String, HashMap<String, Object>> newMetadata = new HashMap<>();

		putLeadsMDIfNeeded(url, "new", "leads_core", 0, timestamp, true, currentMetadata, newMetadata, editableFamilies);
		
		return newMetadata;
	}

	@Override
	public HashMap<String, HashMap<String, Object>> process(
			HashMap<String, HashMap<String, Object>> parameters) {
		
		HashMap<String, Object> newVersionParams = parameters.get("new:leads_core");

		HashMap<String, Object> newVersionResult = new HashMap<String, Object>();
		HashMap<String, HashMap<String, Object>> result = new HashMap<String, HashMap<String, Object>>();
		
		HashMap<String, Object> newPage = parameters.get("new");
		String url = (String) newPage.get("uri");
		
		String fqdn = LEADSUtils.nutchUrlToFullyQualifiedDomainNameUrl(url);
		
		newVersionResult.put(mapping.getProperty("leads_core-fqdnurl"), fqdn);
		
		result.put("new:leads_core", newVersionResult);		
		
		return result;
	}

}
