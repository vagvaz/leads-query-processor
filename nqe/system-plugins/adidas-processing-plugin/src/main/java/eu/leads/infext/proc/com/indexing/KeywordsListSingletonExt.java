package eu.leads.infext.proc.com.indexing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.sun.tools.internal.xjc.api.Mapping;

import eu.leads.PropertiesSingleton;
import eu.leads.datastore.DataStoreSingleton;

public class KeywordsListSingletonExt {

	private static KeywordsListSingletonExt klSingleton = null;
	
	private static boolean isTestKeywordsMode = false;
	private static Map<Long, Map<String, Object>> testKeywordsMap = null;
	
	protected Properties mapping = DataStoreSingleton.getMapping();
	
	private Map<Long, Map<String, Object>> keywordsMap = new HashMap<>();
	private List<Map<String, Object>> keywordslist = new ArrayList<>();
	
	public static void testKeywordsMode(Map<Long, Map<String, Object>> testKeywordsMap) {
		KeywordsListSingletonExt.isTestKeywordsMode = true;
		KeywordsListSingletonExt.testKeywordsMap    = testKeywordsMap;
	}
	
	private void fixTestKeywordsMap() {
		Map<Long, Map<String, Object>> testKeywordsMapNew = new HashMap<>();
		for(Entry<Long, Map<String, Object>> testKeywordEntry : testKeywordsMap.entrySet()) {
			Map<String, Object> testKeywordDefNew = new HashMap<>();
			Map<String, Object> testKeywordDef = testKeywordEntry.getValue();
			for(Entry<String, Object> entry : testKeywordDef.entrySet())
				testKeywordDefNew.put(mapping.getProperty(entry.getKey()), entry.getValue());
			testKeywordsMapNew.put(testKeywordEntry.getKey(), testKeywordDefNew);
		}
		testKeywordsMap = testKeywordsMapNew;
	}
	
	public static KeywordsListSingletonExt getInstance() {
		if(klSingleton == null)
			klSingleton = new KeywordsListSingletonExt();
		return klSingleton;
	}
	
	private KeywordsListSingletonExt() {
		init();
	}
	
	private void init() {
		if(KeywordsListSingletonExt.isTestKeywordsMode) { 
			fixTestKeywordsMap();
			keywordsMap = KeywordsListSingletonExt.testKeywordsMap;
		}
		else keywordsMap = DataStoreSingleton.getDataStore().getUsersKeywordsListExt();
		
		for(Entry<Long, Map<String, Object>> keywordRow : keywordsMap.entrySet()) {
			keywordslist.add(keywordRow.getValue());
		}
	}

	public List<Map<String, Object>> getKeywordsList() {
		return keywordslist;
	}
	
	public String getKeywordsFor(Long id) {
		return (String) keywordsMap.get(id).get(mapping.getProperty("leads_input_keywords-keywords"));
	}

	public Object getParameterFor(Long id, String parameter) {
		return keywordsMap.get(id).get(mapping.getProperty(parameter));
	}
	
}
