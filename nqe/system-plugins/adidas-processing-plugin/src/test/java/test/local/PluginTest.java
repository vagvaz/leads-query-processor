package test.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import eu.leads.processor.AdidasProcessingPlugin;
import eu.leads.processor.core.Tuple;

public class PluginTest {
	
	public static void main(String[] args) throws ConfigurationException, UnsupportedEncodingException, IOException {
		Configuration config = new XMLConfiguration(
				"/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf.xml");
		
		AdidasProcessingPlugin plugin = new AdidasProcessingPlugin();
		plugin.initialize(config, null);
		
	     String content1 = "";
	     URL url = new URL("http://www.theguardian.com/sport/2015/apr/09/lewis-hamilton-new-mercedes-f1-contract");
	     try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
	         for (String line; (line = reader.readLine()) != null;) {
	             content1 += line;
	         }
	     }
	     final String content = content1;
	     
	     plugin.created("default.webpages:http://www.theguardian.com/sport/2015/apr/09/lewis-hamilton-new-mercedes-f1-contract", 
				new Tuple() {{ setAttribute("body", content); }}.asJsonObject().toString(),
				null);
	}
	
}
