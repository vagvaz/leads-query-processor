package eu.leads.infext.proc.realtime.env.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.infext.proc.realtime.hook.impl.FQDNDefiningHook;
import eu.leads.infext.proc.realtime.hook.impl.KeywordExtractionHook;
import eu.leads.infext.proc.realtime.hook.impl.LanguageDetectionHook;
import eu.leads.infext.proc.realtime.hook.impl.PageCheckHook;
import eu.leads.infext.proc.realtime.hook.impl.TextContentExtractionHook;
import eu.leads.infext.proc.realtime.hook.impl.ValuableContentExtractionHook;
import eu.leads.infext.proc.realtime.wrapper.AbstractProcessing;
import eu.leads.infext.proc.realtime.wrapper.AllAtOnceProcessing;
import eu.leads.utils.LEADSUtils;


public class PageProcessingPojo extends AbstractExecutionPojo {
	
	private void applyStageOne() {
		AbstractProcessing fqdnProc			  = new AllAtOnceProcessing(new FQDNDefiningHook());
		AbstractProcessing textContentProc 	  = new AllAtOnceProcessing(new TextContentExtractionHook());
		AbstractProcessing languageProc       = new AllAtOnceProcessing(new LanguageDetectionHook());
		processingQueue.add(fqdnProc);
		processingQueue.add(textContentProc);
		processingQueue.add(languageProc);		
	}
	
	private void applyStateTwo() {
		AbstractProcessing pageContentCheck   = new AllAtOnceProcessing(new PageCheckHook());
		AbstractProcessing extractionProc	  = new AllAtOnceProcessing(new ValuableContentExtractionHook());
		AbstractProcessing keywordExtr		  = new AllAtOnceProcessing(new KeywordExtractionHook());	
		processingQueue.add(pageContentCheck);
		processingQueue.add(extractionProc);
		processingQueue.add(keywordExtr);
	}
	
	public PageProcessingPojo() throws Exception {
		applyStageOne();
		applyStateTwo();
	}
	
	public PageProcessingPojo(Integer [] stages) throws Exception {
		List<Integer> stagesList = Arrays.asList(stages);
		if(stagesList.contains(1)) applyStageOne();
		if(stagesList.contains(2)) applyStateTwo();
	}

	@Override
	public void execute(String uri, String timestamp, String cacheName, HashMap<String, Object> cacheColumns) {
		
		HashMap<String, Object> newMain = new HashMap<>();
		newMain.put("uri", uri);
		newMain.put("timestamp", timestamp);
		
		HashMap<String, HashMap<String, Object>> metadata = new HashMap<>();
		metadata.put("new",	newMain);
		metadata.put("new:"+LEADSUtils.propertyValueToKey(mapping,cacheName), cacheColumns);
		HashMap<String,MDFamily> editableFamilies = new HashMap<>();
		
		try {
			/* Process */
			System.out.println("Processing...");
			for(AbstractProcessing proc : processingQueue) {
				/* TIME */ long start = System.currentTimeMillis();
				try {
					proc.process(uri, timestamp, metadata, editableFamilies);
				} catch(Exception e) {
					System.err.println("Exception caught in "+proc.getHookName());
					e.printStackTrace();
				}
				/* TIME */ double duration   = (System.currentTimeMillis()-start)/1000.0;
				/* TIME */ System.err.println("+++ Execution time of "+proc.getHookName()+": "+duration+" s");
			}
			
			/* Store */
			System.out.println("Storing...");
			store(metadata, editableFamilies, timestamp);
			
		} catch (IllegalStateException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void store(HashMap<String, HashMap<String, Object>> metadata, HashMap<String,MDFamily> editableFamilies, String timestamp) {
		
		String now = timestamp;
		
		postProcessingHacks(metadata);
		
		for(Entry<String,MDFamily> editedFamily : editableFamilies.entrySet()) {
			String familyKey = editedFamily.getKey();
			
			String url = editedFamily.getValue().urlTimestamp.url;
			String ts = editedFamily.getValue().urlTimestamp.timestamp == null ? now : editedFamily.getValue().urlTimestamp.timestamp;
			String family = editedFamily.getValue().family;
			String familyName = mapping.getProperty(family);
			System.out.println(familyKey);
			System.out.println(url);
			System.out.println(ts);
			System.out.println(familyName);
			
			if(familyKey.equals("new:leads_resourceparts")) {
				HashMap<String, Object> resourceParts = metadata.get("new:leads_resourceparts");
				
				HashMap<String, Object> partsTypeValuesMap = new HashMap<>();
				for(Entry<String, Object> resPart : resourceParts.entrySet()) {
					String resTypeNIndex = resPart.getKey();
//					String resType = resTypeNIndex.substring(0, resTypeNIndex.length()-4); // cut ':xxx'
//					String restIndex = resTypeNIndex.substring(0, resTypeNIndex.length()-4);
					Object resValue = resPart.getValue();
					partsTypeValuesMap.put(resTypeNIndex, resValue);
				}
				DataStoreSingleton.getDataStore().putLeadsResourcePartsMD(url, ts, partsTypeValuesMap);
			}
			else if(familyKey.equals("new:leads_keywords")) {
				HashMap<String, Object> keywords = metadata.get("new:leads_keywords");
				
				for(String key : keywords.keySet()) {
					String [] keyparts = key.split(":");
					String element = keyparts[2];
					String partid = keyparts[0]+":"+keyparts[1];
					HashMap<String, Object> keywordFamilyMap = metadata.get("new:leads_keywords:"+key);
					List<Cell> cells = new ArrayList<>();
					for(Entry<String, Object> newMetaColumn : keywordFamilyMap.entrySet()) {
						cells.add(new Cell(newMetaColumn.getKey(), newMetaColumn.getValue(), 0));
						String value = newMetaColumn.getValue().toString();
						System.out.println(newMetaColumn.getKey()+" -> "+ (value.length()>80 ? value.replace("\n", "").replace("\r", "").substring(0, 80) : newMetaColumn.getValue()));
					}					
					DataStoreSingleton.getDataStore().putLeadsResourceElementsMDFamily(url, ts, partid, element, null, cells);
				}
				
			}
			else if(familyKey.startsWith("new:leads_keywords:")) 
				continue;
			else {				
				HashMap<String,Object> mdFamilyMap = metadata.get(familyKey);
				
				if(familyKey.equals("new:leads_core")) 
					mdFamilyMap.remove(mapping.getProperty("leads_core-textcontent"));
				
				List<Cell> cells = new ArrayList<>();
				for(Entry<String, Object> newMetaColumn : mdFamilyMap.entrySet()) {
					if(newMetaColumn.getValue() != null)
						if(newMetaColumn.getKey() != null && !newMetaColumn.getKey().equals("uri"))
							if(!newMetaColumn.getKey().equals("ts")) {
								cells.add(new Cell(newMetaColumn.getKey(), newMetaColumn.getValue(), 0));
								System.out.println(newMetaColumn.getKey()+" -> "+ (newMetaColumn.getValue().toString().length()>80 ? newMetaColumn.getValue().toString().replace("\n", "").replace("\r", "").substring(0, 80) : newMetaColumn.getValue()));
					}
				}
				
				if(!cells.isEmpty())
					DataStoreSingleton.getDataStore().putLeadsResourceMDFamily(url, ts, familyName, cells);
			}
			
		}
		

		
		
//		for(Entry<String, HashMap<String, String>> metaFamilyEntry : metadata.entrySet()) {
//			
//			String familyKey = metaFamilyEntry.getKey();
//			HashMap<String, String> newMetaFamily = metaFamilyEntry.getValue();
//			String [] familyKeyParts = familyKey.split(":");
//			if(familyKeyParts.length == 2) {
//				
//				String version = familyKeyParts[0];
//				String family  = familyKeyParts[1];
//				
//				if(version.equals("new")) {
//					family =  mapping.getProperty(family);
//					System.out.println(family);			
//					
//					List<Cell> cells = new ArrayList<>();
//					for(Entry<String, String> newMetaColumn : newMetaFamily.entrySet()) {
//						cells.add(new Cell(newMetaColumn.getKey(), newMetaColumn.getValue(), 0));
//						System.out.println(newMetaColumn.getKey()+" -> "+ (newMetaColumn.getValue().length()>80 ? newMetaColumn.getValue().replace("\n", "").replace("\r", "").substring(0, 80) : newMetaColumn.getValue()));
//					}
//					
//					DataStoreSingleton.getDataStore().putLeadsResourceMDFamily(url, ts, family, cells);
//				}
//			}
//		}
		
		System.out.println();
	}

	private void postProcessingHacks(HashMap<String, HashMap<String, Object>> metadata) {

		HashMap<String, Object> resourceParts = metadata.get("new:leads_resourceparts");
		
		if(resourceParts != null) {

			Object name = resourceParts.get("ecom_prod_name:000");
			Object currency = resourceParts.get("ecom_prod_currency.000");
			if(name!=null && (currency==null || currency.equals("null"))) {
				resourceParts.put("ecom_prod_currency:000", "USD");
				metadata.put("new:leads_resourceparts", resourceParts);
			}
			
			if(name!=null && name.toString().contains("Holabird Sports")) {
				resourceParts.clear();
				metadata.put("new:leads_resourceparts", resourceParts);
			}
			
		}
		
	}

//	public static void main(String[] args) {
//		PageProcessingPojo ep = new PageProcessingPojo();
//		ep.execute();
//	}

}
