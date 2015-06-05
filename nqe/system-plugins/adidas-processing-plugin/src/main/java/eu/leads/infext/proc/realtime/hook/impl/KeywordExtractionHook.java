package eu.leads.infext.proc.realtime.hook.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;

import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.datastore.datastruct.UrlTimestamp;
import eu.leads.infext.proc.com.indexing.KeywordMatchInfo;
import eu.leads.infext.proc.com.indexing.KeywordsListSingletonExt;
import eu.leads.infext.proc.com.indexing.LeadsDocumentConceptSearchCall;
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
		
		System.out.println("KeywordExtractionHook.0");

		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		HashMap<String, Object> newKeywordsResult = new HashMap<>();
		HashMap<String,HashMap<String, Object>> newSpecificKeywordsResultsList = new HashMap<String,HashMap<String, Object>>();
		
//		List<String> keywordsList = KeywordsListSingleton.getInstance().getKeywordsList();
//		DocumentKeywordSearch keywordSearch = new DocumentKeywordSearch();
		
		HashMap<String, Object> newResourceParts = parameters.get("new:leads_resourceparts");
		HashMap<String, Object> newCore = parameters.get("new:leads_core");
		HashMap<String, Object> newMD = parameters.get("new");
		String url = (String) newMD.get("uri");

		String lang = newCore.get(mapping.get(("leads_core-lang"))).toString();
		
		Map<String, String> partsMap = new HashMap<>();
		for(Entry<String, Object> resPart : newResourceParts.entrySet())
			partsMap.put(resPart.getKey(), resPart.getValue().toString());

		HashMap<String, List<KeywordMatchInfo>> partsKeywordsMap = new HashMap<>();
		partsKeywordsMap = LeadsDocumentConceptSearchCall.searchDocument(url,partsMap);
		
		if(partsKeywordsMap != null) {
			for(Entry<String, List<KeywordMatchInfo>> partKeywords : partsKeywordsMap.entrySet()) {
				String partName = partKeywords.getKey();
				
				for(KeywordMatchInfo keywordMatchInfo : partKeywords.getValue()) {
				
					Long keywordId    = keywordMatchInfo.id;
					String matched	  = keywordMatchInfo.matched;
					Double relevance  = keywordMatchInfo.score;

					String keywords = KeywordsListSingletonExt.getInstance().getKeywordsFor(keywordId);
					
					Double sentimentScore = Double.NaN;
					Double relevanceScore = Double.NaN;
					
					// if not an ecom-specific part, do it!
					if(!partName.toLowerCase().contains("ecom")) {
						// count sentiment
						Sentiment sentiment = sentimentScorer.getSentimentForEntity(matched, partsMap.get(partName), lang);
						if(sentiment != null)
							sentimentScore = sentiment.getValue();
						
						relevanceScore = relevance;
					}
					
					String keyWord = partName+":"+keywords;
					
					HashMap<String, Object> keywordMap = new HashMap<>();
					keywordMap.put(mapping.getProperty("leads_keywords-sentiment"), sentimentScore);
					keywordMap.put(mapping.getProperty("leads_keywords-relevance"), relevanceScore);
					
					newSpecificKeywordsResultsList.put("new:leads_keywords:"+keyWord, keywordMap);
					newKeywordsResult.put(keyWord, null);
				
				}
			}
		}
		
		result.put("new:leads_keywords", newKeywordsResult);
		result.putAll(newSpecificKeywordsResultsList);
		
		return result;
	}

}
