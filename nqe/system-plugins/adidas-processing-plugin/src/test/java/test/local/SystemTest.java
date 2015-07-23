package test.local;

import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import eu.leads.datastore.AbstractDataStore;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.URIVersion;

public class SystemTest extends Test {

	@Override
	protected void execute() {
		/* INIT */
		boolean initiated = init();
		
		if(initiated) {
			
			AbstractDataStore ds = DataStoreSingleton.getDataStore();
			Properties mapping = DataStoreSingleton.getMapping();
			//
			String familyName = mapping.getProperty("leads_crawler_data");
			String contentColName = mapping.getProperty("leads_crawler_data-content");
			
			/* GET URI AND CONTENT */
			String uri = ds.getFamilyNextUri(familyName);
			String content = null;
			while(uri != null) {
				SortedSet<URIVersion> familySet = ds.getLeadsResourceMDFamily(uri, familyName, 1, null, false);
				for(URIVersion ver : familySet) {
					Map<String,Cell> versionColumns = ver.getFamily();
					Cell cell = versionColumns.get(contentColName);
					Object oContent = cell.getValue();
					Long ts = ver.getTimestampL();
					if(oContent != null) { 
						content = oContent.toString();
						this.uri = uri;
						this.ts  = ts;
						this.content = content;
					}
				}
				
				/* RUN */
				run();
				
				uri = ds.getFamilyNextUri(familyName);
			}
			
		}
	}

}
