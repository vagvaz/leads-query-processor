package test.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.impl.LeadsQueryInterface;
import eu.leads.infext.proc.com.indexing.KeywordsListSingletonExt;
import eu.leads.processor.AdidasProcessingPlugin;
import eu.leads.processor.core.Tuple;
import eu.leads.utils.LEADSUtils;

public class PluginTest extends Test {

	@Override
	protected void execute() {
		
		/* INIT */
		boolean initiated = init();
		
		if(initiated) {
			
			/* PREPARE URI AND CONTENT LISTS */
			List<String> stUriList = new ArrayList<>();
//			uriList.add("http://www.runnersworld.com/");
			stUriList.add("http://www.runnersworld.com/running-shoes/first-look-adidas-ultra-boost");
//			uriList.add("http://runblogger.com/");
//			uriList.add("http://runblogger.com/2015/03/nike-wildhorse-2-trail-shoe-review.html");
//			uriList.add("http://www.solereview.com/");
//			uriList.add("http://www.solereview.com/adidas-ultra-boost-review/");
//			uriList.add("http://www.bbc.com/sport/0/");
//			uriList.add("http://www.bbc.com/sport/0/football/33125007");
//			uriList.add("http://footwearnews.com/");
//			uriList.add("http://footwearnews.com/2015/fn-spy/celebrity-style/celebrity-shoe-style-what-they-wear-to-the-gym-36072/");
//			uriList.add("http://www.runningshoesguru.com/");
//			uriList.add("http://www.runningshoesguru.com/2013/02/adidas-energy-boost-review/");
//			uriList.add("http://running.competitor.com/");
//			uriList.add("http://running.competitor.com/2014/11/photos/6-fast-flats-2014-new-york-city-marathon_117188");
			
			for(String uri : stUriList) {
				retrieveContent(uri);
			}
				
			}
	}
	
	private void retrieveContent(String uri) {
		String nutchUri = LEADSUtils.standardUrlToNutchUrl(uri);
	    String content1 = "";
		URLConnection connection;
		try {
			connection = new URL(uri).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();
		    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")))) {
		        for (String line; (line = reader.readLine()) != null;) {
		            content1 += line;
		        }
		    }
		    
		    this.content = content1;
		    this.uri = nutchUri;
		    this.ts = System.currentTimeMillis();

			/* RUN */
			run();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}







