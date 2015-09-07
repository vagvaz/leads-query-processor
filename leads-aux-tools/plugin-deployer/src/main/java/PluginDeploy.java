import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.TupleMarshaller;
import org.infinispan.commons.marshall.Marshaller;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;

import java.util.ArrayList;

/**
 * Created by vagvaz on 4/21/15.
 */
public class PluginDeploy {
    public static void main(String[] args) {

        LQPConfiguration.initialize();
        Marshaller Tmarshaller = new TupleMarshaller();
        EnsembleCacheManager manager =
            new EnsembleCacheManager(LQPConfiguration.getInstance().getConfiguration().getString("node.ip") + ":11222",
                Tmarshaller);

        EnsembleCache web = manager
            .getCache("default.webpages", new ArrayList<>(manager.sites()), EnsembleCacheManager.Consistency.DIST);
        EnsembleCache myCache =
            manager.getCache("mycache", new ArrayList<>(manager.sites()), EnsembleCacheManager.Consistency.DIST);

        PluginDeployer.main(args);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Tuple t = new Tuple();
        for (int i = 0; i < 100; i++) {
            t.setAttribute("url", "url" + i);
            t.setAttribute("domainName", "domainName" + i);
            t.setAttribute("responseCode", "responseCode" + i);
            t.setAttribute("another", "another1");
            web.put(Integer.toString(i), t);
        }

        for (int i = 0; i < 100; i++) {
            byte[] b = (byte[]) myCache.get(Integer.toString(i));
            System.err.println("aa");
        }

    }
}
