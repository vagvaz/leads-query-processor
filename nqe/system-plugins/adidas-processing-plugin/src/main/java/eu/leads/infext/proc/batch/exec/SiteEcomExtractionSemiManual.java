package eu.leads.infext.proc.batch.exec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import org.apache.commons.configuration.XMLConfiguration;

import com.google.common.collect.ImmutableMap;

import eu.leads.datastore.AbstractDataStore;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.URIVersion;
import eu.leads.datastore.datastruct.StringPair;
import eu.leads.infext.proc.com.categorization.ecom.EcommerceClassification;
import eu.leads.infext.proc.com.categorization.ecom.EcommerceSiteExtractionSchemaDeterminer;
import eu.leads.infext.proc.com.categorization.ecom.model.EcomPageDictionary;
import eu.leads.infext.proc.com.categorization.ecom.model.EcomSiteDictionary;
import eu.leads.processor.SystemInit;
import eu.leads.utils.LEADSUtils;

public class SiteEcomExtractionSemiManual {
	private static AbstractDataStore dataStore;
	private static Properties mapping;
	
	/*
	 * 
	 */
	
	private static final String backcountryPattern = 
			"com\\.backcountry\\.www:http/((([^/]+-"
			+ "(cap|hat|sandal|glove|gloves|boot|shoe|shoes|bikini|wallet|jacket|sunglasses|bagpack)"
			+ "|"
			+ "(mens|womens)"
			+ ")-[^/]+|"
			+ "(hat|cap)"
			+ ")|"
			+ "(rc|bcs)"
			+ "/.+)";
	
	private static final String roadrunnersportsPattern = 
			"com\\.roadrunnersports\\.www:http/rrs/(products|brand|c|gear|mensshoes|womensshoes)/.+";	
	
	private static final String holabirdsportsPattern = 
			"com\\.holabirdsports\\.www:http/(.+-((wo|)mens).*\\.html|.*(/(wo|)men-s/).+)";
	
	private static final ImmutableMap<String,String> patternsMap = ImmutableMap
			.<String, String>builder()
			.put("www.backcountry.com",backcountryPattern)
			.put("www.roadrunnersports.com",roadrunnersportsPattern)
			.put("www.holabirdsports.com",holabirdsportsPattern)
			.build();
	
	/*
	 * 
	 */
	
	public static void main(String[] args) throws Exception {
		XMLConfiguration conf = new XMLConfiguration(
				"/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf-test.xml");
		
		SystemInit.init(conf);
		
		dataStore = DataStoreSingleton.getDataStore();
		mapping =  DataStoreSingleton.getMapping();
		
		String fqdn = args[0];
		
		SiteEcomExtractionSemiManual exec = new SiteEcomExtractionSemiManual();
		exec.execute(fqdn);
		
	}
	
	public void execute(String domainName) {
		List<String> domainUrls = getDomainUrls(domainName);
		domainUrls = filterDomainUrls(domainName,domainUrls);
		
		List<EcomPageDictionary> ecomPageDictionaries = getEcomPageDictionaries(domainUrls);
		
		Collections.shuffle(ecomPageDictionaries);
		
		List<EcomPageDictionary> shortEcomPageList = ecomPageDictionaries.size()>300 ? ecomPageDictionaries.subList(0, 300) : ecomPageDictionaries;
		
		// <------ Call of the determineExtractionSchema() method
		EcommerceSiteExtractionSchemaDeterminer schemaDeterminer = new EcommerceSiteExtractionSchemaDeterminer();
		boolean success = schemaDeterminer.determineExtractionSchema(shortEcomPageList);
		// ------>
		
		if(success) {
			EcomSiteDictionary ecomSiteDictionary = schemaDeterminer.getEcomSiteDictionary();
			List<EcomPageDictionary> productEcomPageList = schemaDeterminer.getProductEcomPageList();
			List<EcomPageDictionary> categoryEcomPageList = schemaDeterminer.getCategoryEcomPageList();
			
			store(domainName,ecomSiteDictionary,ecomPageDictionaries,productEcomPageList,categoryEcomPageList);
		}
	}
	
	/**
	 * 
	 * @param domainName
	 * @return
	 */
	private List<String> getDomainUrls(String domainName) {
		List<String> domainUrls = new ArrayList<String>();
		
		String domainUrl = LEADSUtils.fqdnToNutchUrl(domainName);
		domainUrls.addAll(dataStore.getResourceURIsOfDirectory(domainUrl));
		
		return domainUrls;
	}
	
	/**
	 * 
	 * @param domainName
	 * @param domainUrls
	 * @return
	 */
	private List<String> filterDomainUrls(String domainName, List<String> domainUrls) {
		List<String> filteredDomainUrls = new ArrayList<String>();
		
		String regex = patternsMap.get(domainName);
		
		for(String url : domainUrls) {
			String normurl = LEADSUtils.normalizeUri(url);
			if(normurl.matches(regex)) {
				filteredDomainUrls.add(url);
			}
		}
		
		return filteredDomainUrls;
	}
	
	/**
	 * 
	 * @param urls
	 * @return
	 */
	private List<EcomPageDictionary> getEcomPageDictionaries(List<String> urls) {
		List<EcomPageDictionary> ecomPageDictionaries = new ArrayList<EcomPageDictionary>();
		
		Map<String,List<String>> urlsMap = new HashMap<String, List<String>>();
		
		// TODO Group by normurl!
		for(String url : urls) {
			String normurl = LEADSUtils.normalizeUri(url);
			List<String> list = urlsMap.get(normurl);
			if(list==null)
				list = new ArrayList<>();
			list.add(url);
			urlsMap.put(normurl, list);
		}
		
		for(String normurl : urlsMap.keySet()) {
			List<String> list = urlsMap.get(normurl);
			String newestUrl = null;
			Long newestTs  = 0L;
			String newestContent = null;
			for(String url : list) {
				StringPair contentTimestamp = getContentTimestamp(url);
				String content = contentTimestamp.str1;
				String timestamp = contentTimestamp.str2;
				Long ts = Long.parseLong(timestamp);
				if(ts > newestTs) {
					newestUrl = url;
					newestTs  = ts;
					newestContent = content;
				}
			}
			
			EcomPageDictionary ecomPageDictionary = null;
			// Get from the storage
			ecomPageDictionary = getEcomPageFeatures(normurl);
			// If not there, count features!
			if(ecomPageDictionary == null)
				ecomPageDictionary = computeEcomPageFeatures(newestContent);
			
			if(ecomPageDictionary != null) {
				ecomPageDictionary.url = normurl;
				ecomPageDictionary.timestamp = newestTs.toString();
				ecomPageDictionary.content = newestContent;
				ecomPageDictionary.lang = "en";
				
				ecomPageDictionaries.add(ecomPageDictionary);
			}
		}
		
		return ecomPageDictionaries;
	}
	
	private StringPair getContentTimestamp(String uri) {
		StringPair contentTimestamp = new StringPair(null, null);
		SortedSet<URIVersion> uriCrawlerMdFamilyVersions = dataStore.getLeadsResourceMDFamily(uri, mapping.getProperty("leads_crawler_data"), 1, null, true);
		if(uriCrawlerMdFamilyVersions != null && !uriCrawlerMdFamilyVersions.isEmpty()) {
			URIVersion uriCrawlerMdFamilyVersion = uriCrawlerMdFamilyVersions.first();
			contentTimestamp.str2 = uriCrawlerMdFamilyVersion.getTimestamp();
			Map<String, Cell> uriCrawlerMdFamily = uriCrawlerMdFamilyVersion.getFamily();
			Cell contentCell = uriCrawlerMdFamily.get(mapping.getProperty("leads_crawler_data-content"));
			if(contentCell != null) {
				contentTimestamp.str1 = (String) contentCell.getValue();
			}
		}
		return contentTimestamp;
	}

	private EcomPageDictionary getEcomPageFeatures(String uri) {
		EcomPageDictionary ecomPageDictionary = null;
//		
//		SortedSet<URIVersion> uriCrawlerMdFamilyVersions = dataStore.getLeadsResourceMDFamily(uri, mapping.getProperty("leads_internal"), 1, null, true);
//		if(uriCrawlerMdFamilyVersions != null && !uriCrawlerMdFamilyVersions.isEmpty()) {
//			URIVersion uriCrawlerMdFamilyVersion = uriCrawlerMdFamilyVersions.first();
//			contentTimestamp.str2 = uriCrawlerMdFamilyVersion.getTimestamp();
//			Map<String, Cell> uriCrawlerMdFamily = uriCrawlerMdFamilyVersion.getFamily();
//			Cell contentCell = uriCrawlerMdFamily.get(mapping.getProperty("leads_crawler_data-content"));
//			if(contentCell != null) {
//				contentTimestamp.str1 = (String) contentCell.getValue();
//			}
//		}
//				
		return ecomPageDictionary;
	}	
	
	private EcomPageDictionary computeEcomPageFeatures(String content) {
		// <------ Call of the determinePageEcomFeatures() method
		System.out.println("content.length() "+content.length());
		EcommerceClassification ecomClassification = new EcommerceClassification(content, "en");
		boolean isCorrect = ecomClassification.determinePageEcomFeatures();
		// ------>

		EcomPageDictionary ecomPageDictionary = null;
		
		if(isCorrect) {
			if(ecomClassification.isEcomAssumption()) {
				ecomPageDictionary = new EcomPageDictionary();
				ecomPageDictionary.ecom_features = ecomClassification.getEcomFeatures();
				ecomPageDictionary.basketnode = ecomClassification.getBasketNode();
				ecomPageDictionary.buttonnode = ecomClassification.getButtonNode();
			}
		}
		
		return ecomPageDictionary;
	}
	
	/**
	 * 
	 * @param ecomSiteDictionary
	 * @param productEcomPageList
	 * @param categoryEcomPageList
	 */
	private boolean store(String domainName,
			EcomSiteDictionary ecomSiteDictionary,
			List<EcomPageDictionary> ecomPageList,
			List<EcomPageDictionary> productEcomPageList, 
			List<EcomPageDictionary> categoryEcomPageList) {
		if(ecomSiteDictionary != null) {
			
			String url = LEADSUtils.fqdnToNutchUrl(domainName);
			String timestamp = LEADSUtils.getTimestampString();
			String family = mapping.getProperty("leads_urldirectory_ecom");
			List<Cell> cells = new ArrayList<>();
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-name_extraction_tuples"), ecomSiteDictionary.nameExtractionTuples, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-price_extraction_tuples"), ecomSiteDictionary.priceExtractionTuples, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-product_cluster_center"), ecomSiteDictionary.productClusterCenter, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-category_cluster_center"), ecomSiteDictionary.categoryClusterCenter, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-product_cluster_50pc_dist"), ecomSiteDictionary.productCluster50pcDist, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-product_cluster_80pc_dist"), ecomSiteDictionary.productCluster80pcDist, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-category_cluster_50pc_dist"), ecomSiteDictionary.categoryCluster50pcDist, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-category_cluster_80pc_dist"), ecomSiteDictionary.categoryCluster80pcDist, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-scaler_mean"), ecomSiteDictionary.scalerMean, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory_ecom-scaler_std"), ecomSiteDictionary.scalerStd, 0) );
			dataStore.putLeadsResourceMDFamily(url, timestamp, family, cells);
			
			family = mapping.getProperty("leads_urldirectory");
			cells = new ArrayList<>();
			cells.add( new Cell(mapping.getProperty("leads_urldirectory-dir_assumption"), mapping.getProperty("leads_urldirectory-dir_assumption-ecom_varia"), 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory-pages_no"), 1000, 0) );
			cells.add( new Cell(mapping.getProperty("leads_urldirectory-is_ecom_assumption_pages_no"), 1000, 0) );
			dataStore.putLeadsResourceMDFamily(url, timestamp, family, cells);
			
			for(EcomPageDictionary ecomPage : productEcomPageList) {						
				family = mapping.getProperty("leads_internal");
				cells = new ArrayList<>();
				cells.add( new Cell(mapping.getProperty("leads_internal-ecom_features"), ecomPage.ecom_features, 0) );
				cells.add( new Cell(mapping.getProperty("leads_internal-page_type"), mapping.getProperty("leads_internal-page_type-ecom_product_offering_page"), 0) );
				url = ecomPage.url;
				timestamp = ecomPage.timestamp;
				dataStore.putLeadsResourceMDFamily(url, timestamp, family, cells);
			}
			for(EcomPageDictionary ecomPage : categoryEcomPageList) {						
				family = mapping.getProperty("leads_internal");
				cells = new ArrayList<>();
				cells.add( new Cell(mapping.getProperty("leads_internal-ecom_features"), ecomPage.ecom_features, 0) );
				cells.add( new Cell(mapping.getProperty("leads_internal-page_type"), mapping.getProperty("leads_internal-page_type-ecom_category_page"), 0) );
				url = ecomPage.url;
				timestamp = ecomPage.timestamp;
				dataStore.putLeadsResourceMDFamily(url, timestamp, family, cells);
			}
			
			// TADA!
			return true;	
		}
		return false;		
	}
	
	
	
}




