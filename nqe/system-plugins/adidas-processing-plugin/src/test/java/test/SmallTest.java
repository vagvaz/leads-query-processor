package test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.vertx.java.core.json.JsonObject;

import eu.leads.datastore.impl.LeadsQueryInterface;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.web.WebServiceClient;

public class SmallTest {
	
	public static void main(String[] args) throws InterruptedException {
//			WebServiceClient.initialize("http://10.0.2.15", 11222);
//			String table = "table1"; String key = "key1";
//			Map<String,Object> value = new HashMap<String,Object>() {{ put("prop1","val1"); }};
//			WebServiceClient.putObject(table, key, new JsonObject(value));
//			JsonObject json = WebServiceClient.getObject(table, key, new ArrayList<>(value.keySet()));
//			System.out.println(json);
			LQPConfiguration.initialize();
			InfinispanManager im = InfinispanClusterSingleton.getInstance().getManager();
			EmbeddedCacheManager ecm = im.getCacheManager();
			ecm.startCaches("name_of_started_cache");
			
			ConfigurationBuilder builder = new ConfigurationBuilder();
		    builder.addServer().host("10.0.2.15").port(11222);
		    RemoteCacheManager rcm = new RemoteCacheManager(builder.build());
		    RemoteCache<String, Object> cache = rcm.getCache("name_of_started_cache");
		    cache.put("key", "value");
		    Object obj = cache.get("key");
		    System.out.println(obj);
	}
	
}
