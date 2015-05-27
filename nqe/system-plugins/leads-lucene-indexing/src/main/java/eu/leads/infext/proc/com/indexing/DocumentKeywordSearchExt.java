package eu.leads.infext.proc.com.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.xml.builders.SpanNearBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import eu.leads.datastore.datastruct.UrlTimestamp;
import eu.leads.infext.proc.com.keyword.RelevanceScore;
import eu.leads.infext.proc.com.keyword.model.Relevance;


public class DocumentKeywordSearchExt {

    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching
    private StandardAnalyzer analyzer = new StandardAnalyzer();
    // 1. create the index
    private Directory index = new RAMDirectory();
    private IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
    
    private IndexWriter w = null;
    
    public DocumentKeywordSearchExt() {
		try {
			w = new IndexWriter(index, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Returns documents when the keywords are found together with relevance score
     */
	public HashMap<UrlTimestamp,Double> searchKeywords(String [] keywords, 
			int nonMatchingWords,
			int nonMatchingChars,
			int distanceBetweenWords,
			boolean inOrder
			) {
		
		HashMap<UrlTimestamp,Double> docsWithKeywords = new HashMap<>();
		
		BooleanQuery query = new BooleanQuery();

		// Construct the terms since they will be used more than once
		SpanQuery[] clauses = new SpanQuery[keywords.length];
		for(int i=0; i<keywords.length; i++) {
			Term term = new Term("text", keywords[i]);
			//
			clauses[i] = new SpanMultiTermQueryWrapper<FuzzyQuery>(new FuzzyQuery(term,nonMatchingChars));
			query.add(new FuzzyQuery(term,nonMatchingChars), Occur.SHOULD);
		}
		SpanNearQuery spanQuery = new SpanNearQuery(clauses, distanceBetweenWords, inOrder);
		spanQuery.setBoost(5f);
		query.add(spanQuery, Occur.SHOULD);
		
		query.setMinimumNumberShouldMatch(keywords.length-nonMatchingWords);
		System.out.println(query.toString());
		
		try {
			collector = TopScoreDocCollector.create(hitsPerPage, true);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			
			for(int i=0;i<hits.length;++i) {
			    int docId = hits[i].doc;
			    float score = hits[i].score;
			    System.out.println(docId + ": "+score);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return docsWithKeywords;
	}
	
	public boolean addDocument(String url, Map<String,String> contentParts) {
		try {
			for(Entry<String, String> part : contentParts.entrySet()) {
				Map<String,String> partMeta = new HashMap<>();
				partMeta.put("url", url);
				partMeta.put("part", part.getKey());
				partMeta.put("text", part.getValue());
				addDoc(w, partMeta);
			}
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	private int hitsPerPage = 10;
	private IndexReader reader;
	private IndexSearcher searcher;
	private TopScoreDocCollector collector;
	
	public boolean commitDocSet() {
			try {
				if(w.isLocked(index)) w.close();
				reader = DirectoryReader.open(index);
				searcher = new IndexSearcher(reader);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
	}
	
	private int getWordLength(String text) {
		String trimmed = text.trim();
		int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
		return words;
	}
	
	private void addDoc(IndexWriter w, Map<String,String> docProps) throws IOException {
	    Document doc = new Document();
	    FieldType type = new FieldType();
	    type.setIndexed(true);
	    type.setStored(true);
	    type.setStoreTermVectors(true);
	    type.setStoreTermVectorOffsets(true);
	    type.setStoreTermVectorPositions(true);
	    for(Map.Entry<String, String> part : docProps.entrySet())
	    	doc.add(new Field(part.getKey(), part.getValue(), type));
	    w.addDocument(doc);
	}
	
	/////////////////////////////////////////////////////
	public static void main(String[] args) {
		DocumentKeywordSearchExt dks = new DocumentKeywordSearchExt();
		
		Map<String, String> contentParts = new HashMap<>();
		contentParts.put("title",   "New adidas Boosty is great!");
		contentParts.put("article", "Finally, today a long awaited premiere of a new qrewgv adipur varvr aefaed product: adios muchacho wqeCWE ESd wefEd Boost 2.");
		String url = "http://www.sport.eu/";
		dks.addDocument(url,contentParts);
		dks.commitDocSet();
		
		long start = System.currentTimeMillis();
		System.out.println( dks.searchKeywords(new String [] {"fuck"}, 
				0, 0, 0, true) );
		long start1 = System.currentTimeMillis();
		System.out.println( dks.searchKeywords(new String [] {"boost","great"}, 
				0, 1, 0, true) );
		long checkpoint = System.currentTimeMillis();
		System.out.println( dks.searchKeywords(new String [] {"adidas","adipure","adios","boost","2"}, 
				1, 1, 5, false) );
		long finish = System.currentTimeMillis();
		System.out.println(start1-start);
		System.out.println(checkpoint-start1);
		System.out.println(finish-checkpoint);		
	}
	
}






