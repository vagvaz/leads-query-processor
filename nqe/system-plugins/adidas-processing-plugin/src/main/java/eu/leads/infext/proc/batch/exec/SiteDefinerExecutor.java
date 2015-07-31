package eu.leads.infext.proc.batch.exec;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.Configuration;

import eu.leads.datastore.AbstractDataStore;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.URIVersion;
import eu.leads.infext.proc.batch.exec.part.AbstractPartialSiteDefiner;
import eu.leads.infext.proc.batch.exec.part.BlogNewsSiteDefiner;
import eu.leads.infext.proc.batch.exec.part.EcomSiteDefiner;
import eu.leads.infext.proc.batch.exec.part.EcomSiteDefinerMR;
import eu.leads.processor.SystemInit;


/***
 * 
 * The batch process defining the site to be run in the LEADS environment
 * 
 * @author amo_remix
 *
 */
public class SiteDefinerExecutor {
	private static AbstractDataStore dataStore;
	private static Properties mapping;
    
	public static void main(String[] args) throws Exception {
		
		XMLConfiguration conf = new XMLConfiguration(
				"/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf-test.xml");
		
		SystemInit.init(conf);
		
		dataStore = DataStoreSingleton.getDataStore();
		mapping =  DataStoreSingleton.getMapping();
		
		String fqdn = args[0];
		
		/////////////////////////
		/////////////////////////
		/////////////////////////
		
		// a. see if already defined
		SortedSet<URIVersion> dirMdFamilyVersions = dataStore.getLeadsResourceMDFamily(fqdn, mapping.getProperty("leads_urldirectory"), 1, null, true);
		
		boolean isEcom = false;
		boolean isNewsOrBlog = true;
		boolean isKnown = false;
		if(dirMdFamilyVersions != null && !dirMdFamilyVersions.isEmpty()) {
			URIVersion dirMdFamilyVersion = dirMdFamilyVersions.first();
			Map<String, Cell> dirMdFamily = dirMdFamilyVersion.getFamily();
			Cell dirAssumptionCell = dirMdFamily.get(mapping.getProperty("leads_urldirectory-dir_assumption"));
			if(dirAssumptionCell != null) {
				String dirAssumption = (String) dirAssumptionCell.getValue();
				if (Arrays.asList(mapping.getProperty("leads_urldirectory-dir_assumption-ecom_po"), 
								  mapping.getProperty("leads_urldirectory-dir_assumption-ecom_po_varia"), 
								  mapping.getProperty("leads_urldirectory-dir_assumption-ecom_varia"))
										.contains(dirAssumption))	{
					isEcom = true;
				}
				else if (Arrays.asList(mapping.getProperty("leads_urldirectory-dir_assumption-google_news"), 
									   mapping.getProperty("leads_urldirectory-dir_assumption-news"), 
									   mapping.getProperty("leads_urldirectory-dir_assumption-blog"))
									   	.contains(dirAssumption))	{
					isNewsOrBlog = true;
				}
				isKnown = true;
			}
		}
		
		// b. if unknown...
		if(!isKnown) {
			
			List<String> dirUris = dataStore.getResourceURIsOfDirectory(fqdn);
			int dirPagesNo = dirUris.size();
			HashMap<String,Integer> pagesNoMap = new HashMap<>();
			pagesNoMap.put("", dirPagesNo);
			
			// Check if Ecom
			AbstractPartialSiteDefiner partDefiner;
			if(conf.getBoolean("mapreduce"))
				partDefiner = new EcomSiteDefinerMR(fqdn,pagesNoMap,dirUris);
			else
				partDefiner = new EcomSiteDefiner(fqdn,pagesNoMap,dirUris);
			isEcom = partDefiner.defineAndStore();
			
			if(!isEcom) {	
				// Check if in the Google News stream
				partDefiner = new BlogNewsSiteDefiner(fqdn,pagesNoMap,dirUris);
				isNewsOrBlog = partDefiner.defineAndStore();
			}
			
		}
				
		/////////////////////////
		/////////////////////////
		/////////////////////////
		
		System.exit(0);
   	
	}
	
}
