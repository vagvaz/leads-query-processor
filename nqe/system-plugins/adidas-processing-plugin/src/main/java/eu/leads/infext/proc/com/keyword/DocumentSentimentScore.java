package eu.leads.infext.proc.com.keyword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import eu.leads.PropertiesSingleton;
import eu.leads.processor.sentiment.Sentiment;
import eu.leads.processor.sentiment.SentimentAnalysisModule;
import eu.leads.processor.sentiment.SentimentAnalysisModuleOpt;

public class DocumentSentimentScore {
	
	private static String classifierName;
	private static SentimentAnalysisModule sentimentModuleEn;
//	private static SentimentAnalysisModuleOpt sentimentModuleEn;
	private String documentText;
	private String lang;
	private boolean isDocumentSetUp = false;
	
	public static void init() {
		classifierName = PropertiesSingleton.getResourcesDir()+"/classifiers/english.all.3class.distsim.crf.ser.gz";
		sentimentModuleEn =  SentimentAnalysisModule.getInstance(classifierName);
//		Properties samProps = new Properties();
//		samProps.put("srmodel", PropertiesSingleton.getResourcesDir()+"/classifiers/englishSR.ser.gz");
//		sentimentModuleEn =  SentimentAnalysisModuleOpt.getInstance(samProps);
	}
	
	/**
	 * 
	 * @param documentText text which is to be evaluated for sentiment of the phrase
	 * @param lang language of the text (sentiment will be calculated only for English)
	 */
	public DocumentSentimentScore(String documentText, String lang) {
		this.documentText = documentText;
		this.lang = lang;
	}
	
	/**
	 * Provides with sentiment for phrase assuming that some words might have been mentions with mispellings
	 * 
	 * @param foundKeywords list of keywords and their misspelled versions that were found in the text
	 * @param fuzziness what fuzziness was allowed for these keywords
	 * @return
	 */
	public Sentiment getSentimentForFuzzyPhrase(List<String> matchedKeywords, int fuzziness) {
		Sentiment sentiment = null;
		if(lang != null && lang.equals("en")) {
			if(!isDocumentSetUp) { 
				sentimentModuleEn.setupSearchDocument(this.documentText);
				isDocumentSetUp = true;
			}
			Map<String, String> fuzzyKeywords = findFuzzyKeywords(matchedKeywords, fuzziness);
			sentiment = sentimentModuleEn.getSentimentForFuzzyPhrase(fuzzyKeywords);
		}
		return sentiment;
	}

//	public Sentiment getSentimentForEntity(String targetEntity, String text, String lang) {
//		Sentiment sentiment = null;
//		if(lang != null && lang.equals("en")) {
//			sentiment = sentimentModuleEn.getSentimentForEntity(targetEntity, text);
//			System.out.println(sentiment);
//		}
//		return sentiment;
//	}

	private Map<String, String> findFuzzyKeywords(List<String> keywords, int fuzziness) {
		boolean [] blackList = new boolean[keywords.size()];
		Map<String, String> returnMap = new HashMap<String,String>();
		
		for(int i=0; i<keywords.size(); i++) {
			final String str1 = keywords.get(i);
			if(str1.length() > fuzziness && !blackList[i]) {
				//System.out.println(str1+" accepted.");				
				returnMap.put(str1, str1);
				for(int j=i+1; j<keywords.size(); j++) {
					final String str2 = keywords.get(j);
					if(str2.length() > fuzziness && !blackList[j]) {
						//System.out.println(str2+" accepted.");	
						int diff = StringUtils.getLevenshteinDistance(str1, str2);
						//System.out.println("Diff "+str1+" and "+str2+": "+diff);
						if(diff <= fuzziness) {
							returnMap.put(str2, str1);
							blackList[j] = true;
						}
					}
				}
			}
		}
		
		return returnMap;
	}
	
	public static void main(String[] args) {
		String text = "For 90 minutes, the plucky underdogs gave a pretty good account of themselves. Not content to merely defend against their heavily favoured opponents, they threatened on a number of occasions to break the deadlock and pull off a major shock.\n"
				+ "But in stoppage time, they let their guard down for a brief but fatal second and allowed the opposition’s star striker just enough space on the right edge of the box to size up and curl a delightful left-footed shot into the far corner of the net for the winning goal.";
		String tex2 = "I love dogs";
		String tex3 = "What a mighty difference two years can make. All these years, Adidas had a hard time trying to take a bite of the performance running footwear market; but the Boost launch last year suddenly changed all that. A singular cushioning platform has led to a reversal of fortunes for the German brand, leading to a tectonic shift in the running consumer’s perception – with a heavy tilt in its favour.";
		String[] entities = new String[] { "plucky underdogs", "striker" };
		String[] entitie2 = new String[] { "dogs" };
		String[] entitie3 = new String[] { "cushioning", "difference" };
		
		PropertiesSingleton.setResourcesDir("/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/resources");
		
		DocumentSentimentScore.init();		
		
		DocumentSentimentScore sentimentScore = new DocumentSentimentScore("Rarely do I find a running shoe that can be used for such a wide variety of workouts from long runs to track repetitions. "
				+ "But sometimes, a shoe just has it all. "
				+ "Meet the greatest Adidas Adios Boost, a lightweight trainer that works for both fast workouts and marathons. "
				+ "I first tested the shoe during my build-up for the 2014 Boston Marathon after a recommendation from one of my runners. "
				+ "At first, I didn’t love it. "
				+ "I wasn’t used to the midsole material and the lacing system is a bit cumbersome. "
				+ "But it grew on me and after a few weeks I loved every run in the Boost.",
				"en");
		
		// (1)
//		for (String entity : entitie3)
//			System.out.println(sentimentScore.getSentimentForEntity(entity, tex3, "en"));
		
		// (2)
//		Map<String, String> fk = sentimentScore.findFuzzyKeywords(new ArrayList() {{ add("adidas"); add("adida"); add("adid"); }}, 1);
//		System.out.println(fk);
		
		// (3)
		Sentiment sentiment = sentimentScore.getSentimentForFuzzyPhrase(
				new ArrayList() {{ add("adidas"); add("adios"); add("boost"); }}, 1);
		System.out.println(sentiment);
		// (3)
		sentiment = sentimentScore.getSentimentForFuzzyPhrase(
				new ArrayList() {{ add("running"); add("shoe"); }}, 1);
		System.out.println(sentiment);		
	}
}





