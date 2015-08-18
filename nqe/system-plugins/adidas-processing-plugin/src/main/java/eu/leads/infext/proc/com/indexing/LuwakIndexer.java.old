package eu.leads.infext.proc.com.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import eu.leads.datastore.DataStoreSingleton;
import uk.co.flax.luwak.InputDocument;
import uk.co.flax.luwak.Matches;
import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.MonitorQuery;
import uk.co.flax.luwak.QueryMatch;
import uk.co.flax.luwak.matchers.SimpleMatcher;
import uk.co.flax.luwak.presearcher.TermFilteredPresearcher;
import uk.co.flax.luwak.queryparsers.LuceneQueryParser;
import uk.co.flax.luwak.queryrepresentation.ComplexNameQueryRepresentation;
import uk.co.flax.luwak.queryrepresentation.QueryRepresentation;
import uk.co.flax.luwak.queryrepresentation.StringQueryRepresentation;

public class LuwakIndexer {
	
	private Monitor monitor;
	private int querycount = 0;
	private KeywordsListSingletonExt keywordsListSingleton = null;
	protected Properties mapping = DataStoreSingleton.getMapping();
	
	private static LuwakIndexer luwakIndexer = null;
	
	public static LuwakIndexer getInstance() {
		if(luwakIndexer == null) {
			try {
				luwakIndexer = new LuwakIndexer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return luwakIndexer;
	}
	
	private LuwakIndexer() throws IOException {
		monitor = new Monitor(new LuceneQueryParser("field"), new TermFilteredPresearcher());
		keywordsListSingleton = KeywordsListSingletonExt.getInstance();
		initKeywords();
	}
	
	private void initKeywords() {
		List<Map<String, Object>> keywordsList = keywordsListSingleton.getKeywordsList();
		for(Map<String, Object> keywordRow : keywordsList) {
			Long id 					= (Long) keywordRow.get(mapping.getProperty("leads_input_keywords-id"));
			String keywordsString 		= (String) keywordRow.get(mapping.getProperty("leads_input_keywords-keywords"));
			keywordsString				= keywordsString.toLowerCase();
			String [] keywords		 	= keywordsString.split("\\s+");			
			int nonMatchingWords 		= (int) keywordRow.get(mapping.getProperty("leads_input_keywords-non_matching_words"));
			int nonMatchingChars 		= (int) keywordRow.get(mapping.getProperty("leads_input_keywords-non_matching_chars"));
			int distanceBetweenWords 	= (int) keywordRow.get(mapping.getProperty("leads_input_keywords-distance_between_words"));
			boolean inOrder 		 	= (boolean) keywordRow.get(mapping.getProperty("leads_input_keywords-in_order"));
			try {
				addKeywords(id, keywords, nonMatchingWords, nonMatchingChars, distanceBetweenWords, inOrder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean addKeywords(Long id,
			String [] keywords, 
			int nonMatchingWords,
			int nonMatchingChars,
			int distanceBetweenWords,
			boolean inOrder
			) throws IOException {	
		QueryRepresentation qr = new ComplexNameQueryRepresentation(keywords, nonMatchingWords, nonMatchingChars, distanceBetweenWords, inOrder);
		MonitorQuery mq = new MonitorQuery(id.toString(), qr);
		monitor.update(mq);
		return true;
	}
	
	public Map<String, List<Long>> searchDocument(String url, Map<String,String> contentParts) throws IOException {
		Map<String, List<Long>> partKeywords = new HashMap<String, List<Long>>();
		for(Entry<String, String> part : contentParts.entrySet()) {
			String name   = part.getKey();
			String content= part.getValue();
			List<Long> keywordsFound = new ArrayList<Long>();
			InputDocument doc = InputDocument.builder(url+":"+new Random().nextInt(1000)+":"+name)
	                .addField("text",content, new StandardAnalyzer())
	                .build();
			Matches<QueryMatch> matches = monitor.match(doc, SimpleMatcher.FACTORY);
			for(QueryMatch match : matches.getMatches()) {
				String queryId = match.getQueryId();
				Long id = Long.parseLong(queryId);
				keywordsFound.add(id);
			}
			partKeywords.put(name, keywordsFound);
		}
		return partKeywords;
	}
	
	public static void main(String[] args) throws IOException {
		LuwakIndexer li = LuwakIndexer.getInstance();
		
		long checkpoint = System.currentTimeMillis();
		Map<String, List<Long>> a = li.searchDocument("", new HashMap<String, String>() {{ put("text", "Finally, today a long awaited premiere of a new adida adipure adios Boost 2."); }});
		long finish1 = System.currentTimeMillis();
		Map<String, List<Long>> b = li.searchDocument("", new HashMap<String, String>() {{ put("text", "adidas adipure adios Boost 2."); }});
		long finish2 = System.currentTimeMillis();
		Map<String, List<Long>> c = li.searchDocument("", new HashMap<String, String>() {{ put("text", "who are you ho ho ho ho?"); }});
		long finish3 = System.currentTimeMillis();
		System.out.println(finish1-checkpoint);	
		System.out.println(finish2-finish1);	
		System.out.println(finish3-finish2);		
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		
		System.exit(0);
	}
	
}
