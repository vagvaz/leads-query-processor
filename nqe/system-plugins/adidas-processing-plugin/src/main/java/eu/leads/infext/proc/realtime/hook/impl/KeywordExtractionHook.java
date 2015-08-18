package eu.leads.infext.proc.realtime.hook.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;

import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.datastore.datastruct.UrlTimestamp;
import eu.leads.infext.proc.com.indexing.KeywordMatchInfo;
import eu.leads.infext.proc.com.indexing.KeywordsListSingletonExt;
import eu.leads.infext.proc.com.indexing.LeadsDocumentConceptSearchCall;
import eu.leads.infext.proc.com.keyword.RelevanceScore;
import eu.leads.infext.proc.com.keyword.DocumentSentimentScore;
import eu.leads.infext.proc.realtime.hook.AbstractHook;
import eu.leads.processor.sentiment.Sentiment;

public class KeywordExtractionHook extends AbstractHook {
	
	private RelevanceScore relevanceScorer = new RelevanceScore();
	
	
	public KeywordExtractionHook() {
		addKeywordsToIndexer();
		DocumentSentimentScore.init();
	}
	
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
		if(!partsMap.isEmpty())
			partsKeywordsMap = LeadsDocumentConceptSearchCall.searchDocument(url,partsMap);
		
		if(partsKeywordsMap != null && !partsKeywordsMap.isEmpty()) {
			for(Entry<String, List<KeywordMatchInfo>> partKeywords : partsKeywordsMap.entrySet()) {
				String partName = partKeywords.getKey();
				
				String partContent = partsMap.get(partName);
				DocumentSentimentScore sentimentScorer = new DocumentSentimentScore(partContent, lang);
				
				for(KeywordMatchInfo keywordMatchInfo : partKeywords.getValue()) {
				
					Long keywordId    = keywordMatchInfo.id;
					String matched	  = keywordMatchInfo.matched;
					Double relevance  = keywordMatchInfo.score;
					List<String> matchedKeywords = java.util.Arrays.asList(matched.split(" "));

					String keywords = KeywordsListSingletonExt.getInstance().getKeywordsFor(keywordId);
					int fuzziness = (int) KeywordsListSingletonExt.getInstance().getParameterFor(keywordId,"leads_input_keywords-non_matching_chars");
					
					Double sentimentScore = Double.NaN;
					Double relevanceScore = Double.NaN;
					
					// if not an ecom-specific part, do it!
					if(!partName.toLowerCase().contains("ecom")) {
						String text = partsMap.get(partName);
						// count sentiment
						//Sentiment sentiment = sentimentScorer.getSentimentForEntity(matched, partsMap.get(partName), lang);
						Sentiment sentiment = sentimentScorer.getSentimentForFuzzyPhrase(matchedKeywords, fuzziness);
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
	
	  private void addKeywordsToIndexer() {
			List<List<Object>> keywordsDef = new ArrayList<>();
			
			List<Map<String, Object>> keywordsList = KeywordsListSingletonExt.getInstance().getKeywordsList();
			for(Map<String, Object> keywordMap : keywordsList) {
				List<Object> keywordInfo = new ArrayList<>();
				
				Long id 					= (Long) keywordMap.get(mapping.getProperty("leads_input_keywords-id"));
				String keywordsString 		= (String) keywordMap.get(mapping.getProperty("leads_input_keywords-keywords"));	
				int nonMatchingWords 		= (int) keywordMap.get(mapping.getProperty("leads_input_keywords-non_matching_words"));
				int nonMatchingChars 		= (int) keywordMap.get(mapping.getProperty("leads_input_keywords-non_matching_chars"));
				int distanceBetweenWords 	= (int) keywordMap.get(mapping.getProperty("leads_input_keywords-distance_between_words"));
				boolean inOrder 		 	= (boolean) keywordMap.get(mapping.getProperty("leads_input_keywords-in_order"));

				keywordInfo.add(id);
				keywordInfo.add(keywordsString);
				keywordInfo.add(nonMatchingWords);
				keywordInfo.add(nonMatchingChars);
				keywordInfo.add(distanceBetweenWords);
				keywordInfo.add(inOrder);
				
				keywordsDef.add(keywordInfo);			
			}
			
			System.out.println("Adding keywords: "+keywordsDef);
			
			LeadsDocumentConceptSearchCall.addKeywords(keywordsDef);
	   }

}
