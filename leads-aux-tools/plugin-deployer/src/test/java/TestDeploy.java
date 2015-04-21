import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;

/**
 * Created by vagvaz on 4/21/15.
 */
public class TestDeploy {
  public static void main(String[] args) {
    String[] myargs = new String[1];
    LQPConfiguration.initialize();

    EnsembleCacheManager manager = new EnsembleCacheManager(LQPConfiguration.getInstance()
                                                              .getConfiguration().getString("node.ip")+":11222");
    EnsembleCache web  = manager.getCache("default.webpages");
    EnsembleCache myCache = manager.getCache("mycache");
    myargs[0] = "/home/vagvaz/test.properties";
    PluginDeployer.main(myargs);
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Tuple t = new Tuple();
    for(int i = 0; i < 100;i++){
      t.setAttribute("url","url"+i);
      t.setAttribute("domainName","domainName"+i);
      t.setAttribute("responseCode","responseCode"+i);
      t.setAttribute("another","another1");
      web.put(Integer.toString(i),t);
    }

    for(int i = 0; i < 100; i++){
      byte[] b = (byte[]) myCache.get(Integer.toString(i));
      System.err.println("aa");
    }

  }
}
