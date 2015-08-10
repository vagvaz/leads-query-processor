/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.leads.crawler.c4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import eu.leads.datastore.DataStoreSingleton;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class LeadsWP5DemoCrawlController {
	
	private static int numberOfCrawlers;
	private static CrawlConfig crawlConfig;
	private static String parametersFile = "/leads/workm30/leads-query-processor/"
			+ "nqe/system-plugins/adidas-processing-plugin/"
			+ "src/main/java/"
			+ "eu/leads/crawler/c4j/seedlist_ecom_m36.properties";
	private Properties properties = new Properties();
	
	Configuration config;
	
	private void init() {
		InputStream input = null;
		try {
			input = new FileInputStream(parametersFile);
			// load a properties file
			properties.load(input);
			config = new XMLConfiguration(
						"/leads/workm30/leads-query-processor/"
						+ "nqe/system-plugins/adidas-processing-plugin/"
						+ "adidas-processing-plugin-conf-test.xml");
			DataStoreSingleton.configureDataStore(config);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public LeadsWP5DemoCrawlController() {
		init();
	}
	
	static {
	    
	    /*
	     * numberOfCrawlers shows the number of concurrent threads that should
	     * be initiated for crawling.
	     */
	    numberOfCrawlers = 1;
	    crawlConfig = new CrawlConfig();
	    /*
	     * Be polite: Make sure that we don't send more than 1 request per
	     * second (1000 milliseconds between requests).
	     */
	    crawlConfig.setPolitenessDelay(5000);
	    /*
	     * You can set the maximum crawl depth here. The default value is -1 for
	     * unlimited depth
	     */
	    crawlConfig.setMaxDepthOfCrawling(-1);
	    /*
	     * This config parameter can be used to set your crawl to be resumable
	     * (meaning that you can resume the crawl from a previously
	     * interrupted/crashed crawl). Note: if you enable resuming feature and
	     * want to start a fresh crawl, you need to delete the contents of
	     * rootFolder manually.
	     */
	    crawlConfig.setResumableCrawling(false);
	}

  public void crawlDomain(String domain) throws Exception {
	String propString = properties.getProperty(domain);
	if(propString != null) {
		String [] propValues = propString.split(";");
	    /*
	     * You can set the maximum number of pages to crawl. The default value
	     * is -1 for unlimited number of pages
	     */
	    crawlConfig.setMaxPagesToFetch(Integer.parseInt(propValues[2]));
	    /*
	     * crawlStorageFolder is a folder where intermediate crawl data is
	     * stored.
	     */
	    String suffix = null;
	    if(domain.indexOf('/') > -1) suffix = domain.substring(0, domain.indexOf('/'));
	    else suffix = domain;
	    String crawlStorageFolder = "/data/crawled/"+suffix;
	    crawlConfig.setCrawlStorageFolder(crawlStorageFolder);
	    /*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(crawlConfig);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		controller.addSeed(propValues[1]);
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		ExchangeInfoAntipattern.setDomain(domain);
		controller.start(LeadsWP5DemoCrawler.class, numberOfCrawlers);
	}
  }
  
//  public static void main(String[] args) throws Exception {
//	  LeadsWP5DemoCrawlController controller = new LeadsWP5DemoCrawlController();
//	  Properties properties = controller.getProperties();
//	  for(Object key : properties.keySet())
//		  controller.crawlDomain(key.toString());
//  }
  
  public static void main(String[] args) {
	LeadsWP5DemoCrawlController controller = new LeadsWP5DemoCrawlController();
	String site = args[0];
	try {
		controller.crawlDomain(site);
	} catch (Throwable e) {
		e.printStackTrace();
		System.exit(-1);
	}
	System.exit(0);
  }
  
}

