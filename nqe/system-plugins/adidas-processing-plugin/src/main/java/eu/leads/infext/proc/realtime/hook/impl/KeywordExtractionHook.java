package eu.leads.infext.proc.realtime.hook.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.datastore.datastruct.UrlTimestamp;
import eu.leads.infext.proc.com.indexing.DocumentKeywordSearch;
import eu.leads.infext.proc.com.indexing.KeywordsListSingleton;
import eu.leads.infext.proc.com.keyword.RelevanceScore;
import eu.leads.infext.proc.com.keyword.SentimentScore;
import eu.leads.infext.proc.realtime.hook.AbstractHook;
import eu.leads.processor.sentiment.Sentiment;

public class KeywordExtractionHook extends AbstractHook {
	
	private SentimentScore sentimentScorer = new SentimentScore();
	private RelevanceScore relevanceScorer = new RelevanceScore();
	
	@Override
	public HashMap<String, HashMap<String, Object>> retrieveMetadata(
			String url, String timestamp,
			HashMap<String, HashMap<String, Object>> currentMetadata,
			HashMap<String, MDFamily> editableFamilies) {
		
		HashMap<String, HashMap<String, Object>> newMetadata = new HashMap<>();		

		putLeadsMDIfNeeded(url, "new", "leads_core", 0, timestamp, true, currentMetadata, newMetadata, null);
		putLeadsMDIfNeeded(url, "new", "leads_resourceparts", 0, timestamp, true, currentMetadata, newMetadata, null);
		putLeadsMDIfNeeded(url, "new", "leads_keywords", 0, timestamp, true, currentMetadata, newMetadata, editableFamilies);
		
		return newMetadata;
	}

	@Override
	public HashMap<String, HashMap<String, Object>> process(
			HashMap<String, HashMap<String, Object>> parameters) {

		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		HashMap<String, Object> newKeywordsResult = new HashMap<>();
		HashMap<String,HashMap<String, Object>> newSpecificKeywordsResultsList = new HashMap<String,HashMap<String, Object>>();
		
		List<String> keywordsList = KeywordsListSingleton.getInstance().getKeywordsList();
		
		/* TEMP */ keywordsList.add("Grand Prix"); 
		
		DocumentKeywordSearch keywordSearch = new DocumentKeywordSearch();
		
		HashMap<String, Object> newResourceParts = parameters.get("new:leads_resourceparts");
		HashMap<String, Object> newCore = parameters.get("new:leads_core");
		HashMap<String, Object> newMD = parameters.get("new");
		
		for(Entry<String, Object> resPart : newResourceParts.entrySet()) {
			String resTypeNIndex = resPart.getKey();
			String resType  = resTypeNIndex.substring(0, resTypeNIndex.length()-4); // cut ':xxx'
			String resIndex = resTypeNIndex.substring(resTypeNIndex.length()-3);
			Object resValue = resPart.getValue();
			
			if(resValue != null)
				keywordSearch.addDocument(resType, resIndex, resValue.toString());
		}
		
		String lang = newCore.get(mapping.get(("leads_core-lang"))).toString();
		
		for(String keywords : keywordsList) {
			String [] keywordsArray = keywords.split("\\s+");
			
			// UrlTimestamp to be changed later! It's just about these are two strings. Parttype:Partid
			HashMap<UrlTimestamp, Double> partsIds = keywordSearch.searchKeywords(keywordsArray);
			
			for(UrlTimestamp partId : partsIds.keySet()) {
				String key = partId.url+":"+partId.timestamp;
				String content = newResourceParts.get(key).toString();
				
				Double sentimentScore = Double.NaN;
				Double relevanceScore = Double.NaN;
				
				// if not an ecom-specific part, do it!
				if(!partId.url.toLowerCase().contains("ecom")) {
					// count sentiment
					Sentiment sentiment = sentimentScorer.getSentimentForEntity(keywords, content, lang);
					if(sentiment != null)
						sentimentScore = sentiment.getValue();
					// count relevance
					relevanceScore = partsIds.get(partId);
				}
				
				String keyWord = key+":"+keywords;
				
				HashMap<String, Object> keywordMap = new HashMap<>();
				keywordMap.put(mapping.getProperty("leads_keywords-sentiment"), sentimentScore);
				keywordMap.put(mapping.getProperty("leads_keywords-relevance"), relevanceScore);
				
				newSpecificKeywordsResultsList.put("new:leads_keywords:"+keyWord, keywordMap);
				newKeywordsResult.put(keyWord, null);
			}
			
		}
		
		result.put("new:leads_keywords", newKeywordsResult);
		result.putAll(newSpecificKeywordsResultsList);
		
		return result;
	}

}
