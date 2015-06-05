package eu.leads.infext.proc.com.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Explanation;

import eu.leads.infext.proc.com.indexing.model.KeywordMatchInfo;
import uk.co.flax.luwak.InputDocument;
import uk.co.flax.luwak.Matches;
import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.MonitorQuery;
import uk.co.flax.luwak.QueryMatch;
import uk.co.flax.luwak.intervals.IntervalsMatcher;
import uk.co.flax.luwak.intervals.IntervalsQueryMatch;
import uk.co.flax.luwak.intervals.IntervalsQueryMatch.Hit;
import uk.co.flax.luwak.matchers.ExplainingMatch;
import uk.co.flax.luwak.matchers.ExplainingMatcher;
import uk.co.flax.luwak.matchers.ScoringMatch;
import uk.co.flax.luwak.matchers.ScoringMatcher;
import uk.co.flax.luwak.matchers.SimpleMatcher;
import uk.co.flax.luwak.presearcher.TermFilteredPresearcher;
import uk.co.flax.luwak.queryparsers.LuceneQueryParser;
import uk.co.flax.luwak.queryrepresentation.ComplexNameQueryRepresentation;
import uk.co.flax.luwak.queryrepresentation.QueryRepresentation;
import uk.co.flax.luwak.queryrepresentation.StringQueryRepresentation;

public class LuLeadIndexer {
	
	private static final String DEFAULT_FIELD = "text";
	private Monitor monitor;
	
	private static LuLeadIndexer luwakIndexer = null;
	
	public static LuLeadIndexer getInstance() {
		if(luwakIndexer == null) {
			try {
				luwakIndexer = new LuLeadIndexer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return luwakIndexer;
	}
	
	private LuLeadIndexer() throws IOException {
		monitor = new Monitor(new LuceneQueryParser("field"), new TermFilteredPresearcher());
	}

	public boolean addKeywords(Long id,
			String [] keywords, 
			int nonMatchingWords,
			int nonMatchingChars,
			int distanceBetweenWords,
			boolean inOrder
			) throws IOException {	
		QueryRepresentation qr = new ComplexNameQueryRepresentation(keywords, nonMatchingWords, nonMatchingChars, distanceBetweenWords, inOrder);
		MonitorQuery mq = new MonitorQuery(id.toString(), qr);
		monitor.update(mq);
		System.out.println("Added keywords: "+keywords);
		return true;
	}
	
	public Map<String, List<KeywordMatchInfo>> searchDocument(String url, Map<String,String> contentParts) throws IOException {
		Map<String, List<KeywordMatchInfo>> partKeywords = new HashMap<String, List<KeywordMatchInfo>>();
		System.out.println("Searching keyword of uri: "+url);
		for(Entry<String, String> part : contentParts.entrySet()) {
			String name   = part.getKey();
			String content= part.getValue();
			List<KeywordMatchInfo> keywordsFound = new ArrayList<KeywordMatchInfo>();
			InputDocument doc = InputDocument.builder(url+":"+new Random().nextInt(1000)+":"+name)
	                .addField(DEFAULT_FIELD,content, new StandardAnalyzer())
	                .build();
			Matches<ScoringMatch> matches = monitor.match(doc, ScoringMatcher.FACTORY);
			
//			for(ScoringMatch match : matches.getMatches()) {
//				String queryId = match.getQueryId();
//				float score = match.getScore();
//				Long id = Long.parseLong(queryId);
//				keywordsFound.add(new KeywordScore(id, score));
//			}
			//
			Matches<ExplainingMatch> explMatches = monitor.match(doc, ExplainingMatcher.FACTORY);
			for(ExplainingMatch match : explMatches.getMatches()) {
				String queryId = match.getQueryId();
				Explanation explanation = match.getExplanation();
				Object[] matchedKeywords = getMatchedKeywords(explanation);;
				String matched = StringUtils.join(matchedKeywords," ");
				Float score = explanation.getValue();
				Long id = Long.parseLong(queryId);
				keywordsFound.add(new KeywordMatchInfo(id, matched, score));
			}
			partKeywords.put(name, keywordsFound);
		}
		return partKeywords;
	}
	
	private String [] getMatchedKeywords(Explanation explanation) {
		 List<String> allMatches = new ArrayList<String>();
		 
		 String description = explanation.toString();
		 System.out.println(description);
		 String pattern = DEFAULT_FIELD+":";
		 
		 int fieldNameIndex = description.indexOf(pattern, 0);
		 while(fieldNameIndex >= 0) {
			 int begIndex      = fieldNameIndex+pattern.length();
			 
			 int endIndexCand1 = description.indexOf(']', fieldNameIndex);
			 int endIndexCand2 = description.indexOf('^', fieldNameIndex);
			 int endIndex = Math.min(endIndexCand1 >= 0 ? endIndexCand1 : description.length()-1, endIndexCand2 >= 0 ? endIndexCand2 : description.length()-1);
			 
			 String word = description.substring(begIndex, endIndex);
			 allMatches.add(word);
			 
			 fieldNameIndex = description.indexOf(pattern, endIndex);
		 }
		 Set<String> hs = new TreeSet<>();
		 hs.addAll(allMatches);
		 allMatches.clear();
		 allMatches.addAll(hs);
		 return allMatches.toArray(new String[allMatches.size()]);
	}
	
	public static void main(String[] args) throws IOException {
		LuLeadIndexer li = LuLeadIndexer.getInstance();	
		li.addKeywords(0L, "adipure adidas".split("\\s+"), 0, 1, 2, false);
		li.addKeywords(1L, "who".split("\\s+"), 1, 0, 1, false);
		
		long checkpoint = System.currentTimeMillis();
//		Map<String, List<KeywordMatchInfo>> a = li.searchDocument("a", new HashMap<String, String>() {{ put("text", "Finally, today a long awaited premiere of a new adidas adipure adios Boost 2."); }});
		long finish1 = System.currentTimeMillis();
		Map<String, List<KeywordMatchInfo>> b = li.searchDocument("b", new HashMap<String, String>() {{ put("text", "adida adidas adipure adios Boost 2."); }});
		long finish2 = System.currentTimeMillis();
		Map<String, List<KeywordMatchInfo>> c = li.searchDocument("c", new HashMap<String, String>() {{ put("text", "who are you ho ho ho ho?"); }});
		long finish3 = System.currentTimeMillis();
		System.out.println(finish1-checkpoint);	
		System.out.println(finish2-finish1);	
		System.out.println(finish3-finish2);		
		
//		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		
		System.exit(0);
	}
	
}
