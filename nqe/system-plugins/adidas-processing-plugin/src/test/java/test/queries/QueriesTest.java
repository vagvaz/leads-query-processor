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
import test.EnvironmentInit;

public class QueriesTest {

	public static void main(String[] args) throws ConfigurationException, IOException {
		EnvironmentInit.initiateEnv(false);
		
		AbstractDataStore ds = DataStoreSingleton.getDataStore();
		Properties mapping = DataStoreSingleton.getMapping();
		
		final Cell cell = new Cell(mapping.getProperty("leads_core-lang"), "en", 0);
		List<Cell> cells = new ArrayList<Cell>() {{ add(cell); }};
		ds.putLeadsResourceMDFamily("abc", "123", mapping.getProperty("leads_core"), cells);
		
		SortedSet<URIVersion> familyVersions = ds.getLeadsResourceMDFamily("abc", mapping.getProperty("leads_core"), 1, null);
		for(URIVersion uriv : familyVersions)
			for(Entry<String, Cell> fam : uriv.getFamily().entrySet())
				System.out.println(fam.getKey()+" : "+fam.getValue());
	}
	
}
