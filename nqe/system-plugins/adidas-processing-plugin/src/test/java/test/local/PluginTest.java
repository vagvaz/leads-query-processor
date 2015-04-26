package test.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import eu.leads.processor.AdidasProcessingPlugin;
import eu.leads.processor.core.Tuple;
import eu.leads.utils.LEADSUtils;

public class PluginTest {
	
	public static void main(String[] args) throws ConfigurationException, UnsupportedEncodingException, IOException {
		Configuration config = new XMLConfiguration(
//				"/home/ubuntu/.adidas/test/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf.xml");
				"/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf.xml");
		
		AdidasProcessingPlugin plugin = new AdidasProcessingPlugin();
		plugin.initialize(config, null);
		
	    String content1 = "";
//	    String uri = "http://runblogger.com/minimalist-running";
	    String uri = "http://runblogger.com/2011/06/vibram-fivefingers-komodosport-review.html?replytocom=215625693";
	    String nutchUri = LEADSUtils.standardUrlToNutchUrl(uri);
	    
		URLConnection connection = new URL(uri).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		connection.connect();
	    URL url = new URL(uri);
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")))) {
	        for (String line; (line = reader.readLine()) != null;) {
	            content1 += line;
	        }
	    }
	    final String content = content1;
	    
	    plugin.created("default.webpages:"+nutchUri, 
				new Tuple() {{ 
					setAttribute("body", content); 
					setAttribute("published", System.currentTimeMillis());
					setAttribute("headers", new HashMap<String,String>() 
							{{ put("Content-Type","text/html; charset=UTF-8");}});
					}}.asJsonObject().toString(),
				null);
	}
	
}
