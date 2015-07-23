package test.local;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import eu.leads.ProcessingFilterSingleton;
import eu.leads.datastore.impl.LeadsQueryInterface;
import eu.leads.infext.proc.com.indexing.KeywordsListSingletonExt;
import eu.leads.processor.AdidasProcessingPlugin;
import eu.leads.processor.core.Tuple;

public abstract class Test {

	private static Test test;
	private String tableName = "leads.page_content";
//	private String tableName = "default.webpages";
	private static String urlFilter = null;
	
	public static void main(String[] args) {
		String type = args[0];
		
		if(type.equals("system"))
			test = new SystemTest();
		else if(type.equals("plugin"))
			test = new PluginTest();
		
		if(args.length>1) urlFilter = args[1];
		
		test.execute();
	}

	protected AdidasProcessingPlugin plugin = null;
	protected String uri 	 = null;
	protected String content = null;
	protected Long   ts 	 = null;
	
	protected boolean init() {
		Configuration config;
		try {
			config = new XMLConfiguration(
//				"/home/ubuntu/.adidas/test/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf.xml");
					"/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf-test.xml");
			
			LeadsQueryInterface.setQueryMode(false);
			KeywordsListSingletonExt.testKeywordsMode(defineKeywordsMap());
			if(urlFilter!=null) ProcessingFilterSingleton.setFilterString(urlFilter);
			
			plugin = new AdidasProcessingPlugin();
			plugin.initialize(config, null);
			
			return true;
		} catch (ConfigurationException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	protected abstract void execute();	
	
	protected void run() {		
		
		if(this.uri == null || this.content == null)
			throw new IllegalStateException();
		
		/* BODY */
		System.err.println("-+-+-+- Running test for "+this.uri+" -+-+-+-");
		final String content = this.content;
		final Long ts = this.ts;
	    plugin.created(tableName+":"+this.uri,
				new Tuple() {
					private static final long serialVersionUID = 821216041925139493L; { 
					setAttribute("body", content);
					setAttribute("published", ts);
					setAttribute("headers", new HashMap<String,String>(){
						private static final long serialVersionUID = 101L;{ 
							put("Content-Type","text/html; charset=UTF-8");}});
					}}.asJsonObject().toString(),
				null);		
	}
	
	
	private Map<Long, Map<String, Object>> defineKeywordsMap() {
		Map<Long, Map<String, Object>> testKeywordsMap = new HashMap<>();
		
		HashMap<String, Object> keywordMap = new HashMap<>();
		keywordMap.put("leads_input_keywords-id",0L);
		keywordMap.put("leads_input_keywords-keywords","adidas");	
		keywordMap.put("leads_input_keywords-non_matching_words",0);
		keywordMap.put("leads_input_keywords-non_matching_chars",0);
		keywordMap.put("leads_input_keywords-distance_between_words",0);
		keywordMap.put("leads_input_keywords-in_order",false);
		testKeywordsMap.put(0L, keywordMap);
		keywordMap.put("leads_input_keywords-id",1L);
		keywordMap.put("leads_input_keywords-keywords","nike");	
		keywordMap.put("leads_input_keywords-non_matching_words",0);
		keywordMap.put("leads_input_keywords-non_matching_chars",0);
		keywordMap.put("leads_input_keywords-distance_between_words",0);
		keywordMap.put("leads_input_keywords-in_order",false);
		testKeywordsMap.put(1L, keywordMap);
		keywordMap.put("leads_input_keywords-id",2L);
		keywordMap.put("leads_input_keywords-keywords","wilson");	
		keywordMap.put("leads_input_keywords-non_matching_words",0);
		keywordMap.put("leads_input_keywords-non_matching_chars",0);
		keywordMap.put("leads_input_keywords-distance_between_words",0);
		keywordMap.put("leads_input_keywords-in_order",false);
		testKeywordsMap.put(2L, keywordMap);
		keywordMap.put("leads_input_keywords-id",3L);
		keywordMap.put("leads_input_keywords-keywords","new balance");	
		keywordMap.put("leads_input_keywords-non_matching_words",0);
		keywordMap.put("leads_input_keywords-non_matching_chars",0);
		keywordMap.put("leads_input_keywords-distance_between_words",0);
		keywordMap.put("leads_input_keywords-in_order",false);
		testKeywordsMap.put(3L, keywordMap);
		keywordMap.put("leads_input_keywords-id",4L);
		keywordMap.put("leads_input_keywords-keywords","head");	
		keywordMap.put("leads_input_keywords-non_matching_words",0);
		keywordMap.put("leads_input_keywords-non_matching_chars",0);
		keywordMap.put("leads_input_keywords-distance_between_words",0);
		keywordMap.put("leads_input_keywords-in_order",false);
		testKeywordsMap.put(4L, keywordMap);
		keywordMap.put("leads_input_keywords-id",5L);
		keywordMap.put("leads_input_keywords-keywords","under armour");	
		keywordMap.put("leads_input_keywords-non_matching_words",0);
		keywordMap.put("leads_input_keywords-non_matching_chars",0);
		keywordMap.put("leads_input_keywords-distance_between_words",0);
		keywordMap.put("leads_input_keywords-in_order",false);
		testKeywordsMap.put(5L, keywordMap);
		keywordMap.put("leads_input_keywords-id",6L);
		keywordMap.put("leads_input_keywords-keywords","asics");	
		keywordMap.put("leads_input_keywords-non_matching_words",0);
		keywordMap.put("leads_input_keywords-non_matching_chars",0);
		keywordMap.put("leads_input_keywords-distance_between_words",0);
		keywordMap.put("leads_input_keywords-in_order",false);
		testKeywordsMap.put(6L, keywordMap);		
//		keywordMap = new HashMap<>();
//		keywordMap.put("leads_input_keywords-id",1L);
//		keywordMap.put("leads_input_keywords-keywords","VFF Bikilas");	
//		keywordMap.put("leads_input_keywords-non_matching_words",0);
//		keywordMap.put("leads_input_keywords-non_matching_chars",1);
//		keywordMap.put("leads_input_keywords-distance_between_words",2);
//		keywordMap.put("leads_input_keywords-in_order",true);
//		testKeywordsMap.put(1L, keywordMap);	
//		keywordMap = new HashMap<>();
//		keywordMap.put("leads_input_keywords-id",2L);
//		keywordMap.put("leads_input_keywords-keywords","Hello my name");	
//		keywordMap.put("leads_input_keywords-non_matching_words",0);
//		keywordMap.put("leads_input_keywords-non_matching_chars",1);
//		keywordMap.put("leads_input_keywords-distance_between_words",2);
//		keywordMap.put("leads_input_keywords-in_order",true);
//		testKeywordsMap.put(2L, keywordMap);	
//		keywordMap = new HashMap<>();
//		keywordMap.put("leads_input_keywords-id",3L);
//		keywordMap.put("leads_input_keywords-keywords","is Pawel");	
//		keywordMap.put("leads_input_keywords-non_matching_words",0);
//		keywordMap.put("leads_input_keywords-non_matching_chars",1);
//		keywordMap.put("leads_input_keywords-distance_between_words",2);
//		keywordMap.put("leads_input_keywords-in_order",true);
//		testKeywordsMap.put(3L, keywordMap);	
//		keywordMap = new HashMap<>();
//		keywordMap.put("leads_input_keywords-id",4L);
//		keywordMap.put("leads_input_keywords-keywords","And I like");	
//		keywordMap.put("leads_input_keywords-non_matching_words",0);
//		keywordMap.put("leads_input_keywords-non_matching_chars",1);
//		keywordMap.put("leads_input_keywords-distance_between_words",2);
//		keywordMap.put("leads_input_keywords-in_order",true);
//		testKeywordsMap.put(4L, keywordMap);	
//		keywordMap = new HashMap<>();
//		keywordMap.put("leads_input_keywords-id",5L);
//		keywordMap.put("leads_input_keywords-keywords","running");	
//		keywordMap.put("leads_input_keywords-non_matching_words",0);
//		keywordMap.put("leads_input_keywords-non_matching_chars",1);
//		keywordMap.put("leads_input_keywords-distance_between_words",2);
//		keywordMap.put("leads_input_keywords-in_order",true);
//		testKeywordsMap.put(5L, keywordMap);	
		
		return testKeywordsMap;
	}
	
}
