package eu.leads.infext.proc.realtime.hook.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import eu.leads.datastore.datastruct.MDFamily;
import eu.leads.infext.proc.com.maincontent.LeadsMainContentExtraction;
import eu.leads.infext.proc.realtime.hook.AbstractHook;

public class ValuableContentExtractionHook extends AbstractHook {
	
	private LeadsMainContentExtraction leadsMainContentExtraction = new LeadsMainContentExtraction();

	@Override
	public HashMap<String, HashMap<String, Object>> retrieveMetadata(
			String url, String timestamp,
			HashMap<String, HashMap<String, Object>> currentMetadata,
			HashMap<String, MDFamily> editableFamilies) {
		
		HashMap<String, HashMap<String, Object>> newMetadata = new HashMap<>();
		
		putLeadsMDIfNeeded(url, "new", "leads_internal", 0, timestamp, true, currentMetadata, newMetadata, editableFamilies);
		putLeadsMDIfNeeded(url, "new", "leads_resourceparts", 0, timestamp, true, currentMetadata, newMetadata, editableFamilies);
		
		return newMetadata;
	}

	@Override
	public HashMap<String, HashMap<String, Object>> process(
			HashMap<String, HashMap<String, Object>> parameters) {
		
		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		HashMap<String, Object> newInternalResult = new HashMap<>();
		HashMap<String, Object> newResourcePartResult = new HashMap<>();
		
		HashMap<String, Object> newMD = parameters.get("new");
		HashMap<String, Object> newCrawled = parameters.get("new:leads_crawler_data");
		HashMap<String, Object> newInternal = parameters.get("new:leads_internal");
		
		String content = newCrawled.get(mapping.get("leads_crawler_data-content")).toString();
		Object candidatesExtractionJSONObj	= newInternal.get(mapping.get("leads_internal-extraction_candidates"));

		if(candidatesExtractionJSONObj != null) {
			String candidatesExtractionJSON = candidatesExtractionJSONObj.toString();
			
			HashMap<String, List<String>> extractedValues = leadsMainContentExtraction.extract(content, candidatesExtractionJSON);
			String successfulExtractionJSON = leadsMainContentExtraction.getLastSuccessfulExtractionJSON();
			
			System.out.println("--- EXTRACTION SUCCESSFUL ---");
			System.out.println("Extracted values for keys:" + extractedValues.keySet());
			//System.out.println(successfulExtractionJSON);
			System.out.println("--- ----------------- ---");

			for(Entry<String, List<String>> e : extractedValues.entrySet()) {
				String extractedType = e.getKey();
				List<String> extractedVals = e.getValue();
				int index = 0;
				for(int i=0; i<extractedVals.size(); i++) {
					String val = extractedVals.get(i);
					if(!val.trim().isEmpty()) {
						newResourcePartResult.put(extractedType+String.format(":%03d", index), extractedVals.get(i));
						index++;
					}
				}
			}
			
			newInternalResult.put(mapping.getProperty("leads_internal-successful_extractions"), successfulExtractionJSON);
			result.put("new:leads_internal", newInternalResult);
			result.put("new:leads_resourceparts", newResourcePartResult);
		}
		else {
			System.out.println("--- NOTHING 2 EXTRACT ---");
			System.out.println("--- ----------------- ---");
		}
		
		return result;
	}

}
