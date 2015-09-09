import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.TupleMarshaller;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.marshall.Marshaller;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vagvaz on 4/21/15.
 */
public class TestDeploy {
    public static void main(String[] args) throws MalformedURLException {
        String[] myargs = new String[1];
        LQPConfiguration.initialize();
        Marshaller Tmarshaller = new TupleMarshaller();
        EnsembleCacheManager manager = new EnsembleCacheManager("127.0.0.1" + ":11222");//,Tmarshaller);
        //                                                              .getConfiguration().getString("node.ip")+":11222");//,Tmarshaller);

        EnsembleCache web = manager
            .getCache("default.webpages", new ArrayList<>(manager.sites()), EnsembleCacheManager.Consistency.DIST);
        //    EnsembleCache myCache = manager.getCache("metrics",new ArrayList<>(manager.sites()),
        //        EnsembleCacheManager.Consistency.DIST);
        RemoteCacheManager cm = createRemoteCacheManager();
        RemoteCache myCache = cm.getCache("metrics");
        myargs[0] = "/home/vagvaz/test.properties";
        //    PluginDeployer.main(myargs);
        //    try {
        //      Thread.sleep(10000);
        //    } catch (InterruptedException e) {
        //      e.printStackTrace();
        //    }
        String content1 = "";
        URL url = new URL("http://www.bbc.com/news/uk-31545744");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null; ) {
                content1 += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String content = content1;

        //    web.put(, new Tuple() {{ setAttribute("body", content); }}.asJsonObject().toString());
        String key = "default.webpages:http://www.bbc.com/news/uk-31545744";
        Tuple t = new Tuple();
        for (int i = 0; i < 10; i++) {
            if (i > 0)
                key += Integer.toString(i);
            t.setAttribute("url", "http://www.bbc.com/news/uk-31545744" + i);
            t.setAttribute("domainName", "bbc.com" + i);
            t.setAttribute("responseCode", "responseCode" + i);
            t.setAttribute("body", "content");
            System.out.println("putting key " + key);
            web.put(key, t);
            //      try {
            //        Thread.sleep(4000);
            //      } catch (InterruptedException e) {
            //        e.printStackTrace();
            //      }
        }

        for (Object k : myCache.keySet()) {
            Tuple tt = (Tuple) myCache.get(k.toString());
            System.err.println("aa " + tt.toString());
        }

    }

    private static RemoteCacheManager createRemoteCacheManager() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("127.0.0.1").port(11222);
        return new RemoteCacheManager(builder.build());
    }

}
