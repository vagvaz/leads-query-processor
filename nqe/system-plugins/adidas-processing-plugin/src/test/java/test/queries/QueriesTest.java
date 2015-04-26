package test.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedSet;

import org.apache.commons.configuration.ConfigurationException;

import eu.leads.datastore.AbstractDataStore;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.URIVersion;
import eu.leads.datastore.impl.LeadsQueryInterface;
import eu.leads.processor.web.QueryResults;
import test.EnvironmentInit;

public class QueriesTest {

	public static void main(String[] args) throws ConfigurationException, IOException {
		EnvironmentInit.initiateEnv(false);
		
		AbstractDataStore ds = DataStoreSingleton.getDataStore();
		Properties mapping = DataStoreSingleton.getMapping();
		
//		final Cell cell = new Cell(mapping.getProperty("leads_core-lang"), "en", 0);
//		List<Cell> cells = new ArrayList<Cell>() {{ add(cell); }};
//		ds.putLeadsResourceMDFamily("abc", "123", mapping.getProperty("leads_core"), cells);
//		
//		SortedSet<URIVersion> familyVersions = ds.getLeadsResourceMDFamily("abc", mapping.getProperty("leads_core"), 1, null);
//		for(URIVersion uriv : familyVersions)
//			for(Entry<String, Cell> fam : uriv.getFamily().entrySet())
//				System.out.println(fam.getKey()+" : "+fam.getValue());
		
//		QueryResults rs = LeadsQueryInterface.send_query_and_wait("INSERT INTO default.page_core " +
//				 "(uri, ts, lang, textcontent, oldsentiment, "
//				 + "fqdnurl, maincontent, sentiment, type, pagerank) "
//				// + "VALUES ('abcd', 124, 'en', 'abc', '1.0', 'abc', 'abc', 1.0, 'type', 1.0);");
//				 + "VALUES ('abd', 125, 'en', NULL, NULL, NULL, NULL, 2.0, NULL, 1.0);");
		
		QueryResults rs = LeadsQueryInterface.sendQuery(
				"INSERT INTO default.adidas_keywords (keywords) VALUES "
				+ "('adidas',"
				+ " 'adidas boost',"
				+ " 'nike',"
				+ " 'nike free',"
				+ " 'asics',"
				+ " 'under armour',"
				+ " 'football',"
				+ " 'marathon');"
//				"SELECT * FROM default.adidas_keywords"
				);
		System.out.println(rs.getResult());
	}
	
}
