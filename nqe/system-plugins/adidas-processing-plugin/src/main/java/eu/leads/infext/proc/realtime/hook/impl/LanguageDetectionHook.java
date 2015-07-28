package eu.leads.infext.proc.realtime.hook.impl;

import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.infext.proc.com.lang.JavaLanguageDetection;
import eu.leads.infext.proc.realtime.hook.AbstractHook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LanguageDetectionHook extends AbstractHook {
	
	private List<String> fieldsGeneralList = new ArrayList<String> ( Arrays.asList("maincontent") );
	private List<String> fieldsAliasesList = new ArrayList<String> ( Arrays.asList("maincontent") );
	private String tableName = "maintable";
	private JavaLanguageDetection langdetection = JavaLanguageDetection.getInstance();
	
	public LanguageDetectionHook() {
		
	}
	
	@Override
	public HashMap<String, HashMap<String, String>> retrieveMetadata(String url, String timestamp, 
			HashMap<String, HashMap<String, String>> currentMetadata, HashMap<String, MDFamily> editableFamilies) {
		
		HashMap<String, HashMap<String, String>> newMetadata = new HashMap<>();

		putLeadsMDIfNeeded(url, "new", "leads_core", 0, null, currentMetadata, newMetadata, editableFamilies);
		
		return newMetadata;
	}

	@Override
	public HashMap<String, HashMap<String, String>> process(
			HashMap<String, HashMap<String, String>> parameters) {
		
		HashMap<String, String> newVersionParams = parameters.get("new:leads_core");

		HashMap<String, String> newVersionResult = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		
		String pageContent = newVersionParams.get(mapping.getProperty("leads_core-maincontent"));
		if(pageContent == null)
			pageContent = newVersionParams.get(mapping.getProperty("leads_core-textcontent")); // purely text content
		
		if(pageContent != null) {
			String lang = langdetection.detectLanguage(pageContent);
			newVersionResult.put(mapping.getProperty("leads_core-lang"), lang);
			
			result.put("new:leads_core", newVersionResult);
		}
		
		return result;
	}
	
//	@Override
//	public HashMap<String, HashMap<String, String>> retrieveMetadata(String url, String timestamp, HashMap<String, HashMap<String, String>> currentMetadata) {
//		
//		Map<String, Map<String, Object>> metadata = new HashMap<>();
//		
//		Map<String, Object> crawlerMetadata = new HashMap<String, Object>();
//		URIVersion uriVersion = DataStoreSingleton.getDataStore().getLeadsResourceMDFamily(url, mapping.getProperty("leads_core"), 1, null).last();
//		for(Entry<String, Cell> e : uriVersion.getFamily().entrySet()) {
//			Cell cell = e.getValue();
//			crawlerMetadata.put(cell.getKey(), cell.getValue());
//		}
//		metadata.put("new:leads_core", crawlerMetadata);
//
//		return metadata;
//	}

}
