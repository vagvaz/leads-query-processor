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

import eu.leads.PropertiesSingleton;
import eu.leads.datastore.DataStoreSingleton;

public class KeywordsListSingletonExt {

	private static KeywordsListSingletonExt klSingleton = null;
	
	protected Properties mapping = DataStoreSingleton.getMapping();
	
	private Map<Long, Map<String, Object>> keywordsMap = new HashMap<>();
	private List<Map<String, Object>> keywordslist = new ArrayList<>();
	
	public static KeywordsListSingletonExt getInstance() {
		if(klSingleton == null)
			klSingleton = new KeywordsListSingletonExt();
		return klSingleton;
	}
	
	private KeywordsListSingletonExt() {
		init();
	}
	
	private void init() {
		keywordsMap = DataStoreSingleton.getDataStore().getUsersKeywordsListExt();
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
	
}
