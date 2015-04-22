package eu.leads.infext.proc.realtime.hook.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;





import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.datastore.datastruct.URIVersion;
import eu.leads.infext.proc.com.lang.JavaLanguageDetection;
import eu.leads.infext.proc.realtime.hook.AbstractHook;
import eu.leads.utils.LEADSUtils;

public class LanguageDetectionHook extends AbstractHook {
	
	private List<String> fieldsGeneralList = new ArrayList<String> ( Arrays.asList("maincontent") );
	private List<String> fieldsAliasesList = new ArrayList<String> ( Arrays.asList("maincontent") );
	private String tableName = "maintable";
	private JavaLanguageDetection langdetection = JavaLanguageDetection.getInstance();
	
	public LanguageDetectionHook() {
		
	}
	
	@Override
	public HashMap<String, HashMap<String, Object>> retrieveMetadata(String url, String timestamp, 
			HashMap<String, HashMap<String, Object>> currentMetadata, HashMap<String, MDFamily> editableFamilies) {
		
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
		
		Object pageContent = newVersionParams.get(mapping.getProperty("leads_core-maincontent"));
		if(pageContent == null)
			pageContent = newVersionParams.get(mapping.getProperty("leads_core-textcontent")); // purely text content
		
		if(pageContent != null) {
			String lang = langdetection.detectLanguage(pageContent.toString());
			newVersionResult.put(mapping.getProperty("leads_core-lang"), lang);
			
			result.put("new:leads_core", newVersionResult);
		}
		
		return result;
	}
	
//	@Override
//	public HashMap<String, HashMap<String, Object>> retrieveMetadata(String url, String timestamp, HashMap<String, HashMap<String, Object>> currentMetadata) {
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
