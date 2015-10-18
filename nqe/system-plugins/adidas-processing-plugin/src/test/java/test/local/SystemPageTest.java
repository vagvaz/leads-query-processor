package test.local;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.json.JSONObject;

import eu.leads.datastore.AbstractDataStore;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.URIVersion;
import eu.leads.datastore.impl.LeadsQueryInterface;
import eu.leads.processor.web.QueryResults;

public class SystemPageTest extends Test {

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
			String uri = urlFilter;
			String content = null;
			QueryResults rs = LeadsQueryInterface.execute("SELECT * FROM default.webpages WHERE url='"+uri+"';");
			for(String row : rs.getResult()) {
				JSONObject jsonRow = new JSONObject(row);
				Map<String,Cell> columnsMap = new HashMap<String, Cell>();
				System.out.println(jsonRow.keySet());
				System.out.println(jsonRow.get("default.webpages.url"));
				this.uri = jsonRow.getString("default.webpages.url");
				this.ts  = jsonRow.getLong("default.webpages.ts");
				this.content = jsonRow.getString("default.webpages.body");
				break;
			}
			
			/* RUN */
			run();
			
		}
	}

}
