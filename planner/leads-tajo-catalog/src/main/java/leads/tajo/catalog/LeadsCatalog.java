/**
 *
 */
package leads.tajo.catalog;

import com.google.common.base.Preconditions;
import eu.leads.processor.conf.LQPConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tajo.catalog.CatalogConstants;
import org.apache.tajo.catalog.CatalogServer;
import org.apache.tajo.catalog.CatalogService;
import org.apache.tajo.catalog.MiniCatalogServer;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.conf.TajoConf.ConfVars;
import org.apache.tajo.master.rm.TajoWorkerResourceManager;
import org.apache.tajo.util.NetUtils;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author tr
 */
public class LeadsCatalog {
    static TajoConf conf = null;
    private static CatalogService catalog = null;
    private static MiniCatalogServer catalogServer;
    private static Log LOG = LogFactory.getLog("LeadsLog");

    public LeadsCatalog(TajoConf inconf) {
        if (inconf != null)
            LeadsCatalog.conf = new TajoConf(inconf);
        else {
            conf = new TajoConf();
            LQPConfiguration.initialize(true);
            LQPConfiguration.getInstance().loadFile("catalog.xml");

            if (System.getProperty(ConfVars.RESOURCE_MANAGER_CLASS.varname) != null) {
                String testResourceManager =
                    System.getProperty(ConfVars.RESOURCE_MANAGER_CLASS.varname);
                Preconditions.checkState(testResourceManager.equals(TajoWorkerResourceManager.class
                                                                        .getCanonicalName()));
                conf.set(ConfVars.RESOURCE_MANAGER_CLASS.varname,
                            System.getProperty(ConfVars.RESOURCE_MANAGER_CLASS.varname));
            }
            //		    conf.setInt(ConfVars.WORKER_RESOURCE_AVAILABLE_MEMORY_MB.varname, 1024);
            //		    conf.setFloat(ConfVars.WORKER_RESOURCE_AVAILABLE_DISKS.varname, 2.0f);
            conf.setInt(ConfVars.WORKER_RESOURCE_AVAILABLE_MEMORY_MB.varname,
                           LQPConfiguration.getConf().getInt("planner.catalog.memory", 1024));
            conf.setFloat(ConfVars.WORKER_RESOURCE_AVAILABLE_DISKS.varname,
                             LQPConfiguration.getConf().getFloat("planner.catalog.disks", 1.0f));

            Object clusterTestBuildDir = null;
            //this.standbyWorkerMode = conf.getVar(ConfVars.RESOURCE_MANAGER_CLASS)
            //    .indexOf(TajoWorkerResourceManager.class.getName()) >= 0;
            //conf.set(CommonTestingUtil.TAJO_TEST, "TRUE");
            if (clusterTestBuildDir == null) {
                clusterTestBuildDir = setupClusterRandBuildDir();
            }

            //conf.set(CatalogConstants.STORE_CLASS,"org.apache.tajo.catalog.store.MemStore");//
            conf.set(CatalogConstants.STORE_CLASS, LQPConfiguration.getConf()
                                                       .getString("planner.catalog.store",
                                                                     "leads.tajo.catalog.LeadsMemStore"));//
            conf.set(CatalogConstants.CATALOG_URI, LQPConfiguration.getConf()
                                                       .getString("planner.catalog.uri",
                                                                     "jdbc:derby:"
                                                                         + clusterTestBuildDir
                                                                         + "/db"));
            LOG.info("Apache Derby repository is set to " + LQPConfiguration.getConf()
                                                                .getString("planner.catalog.uri",
                                                                              "jdbc:derby:"
                                                                                  + clusterTestBuildDir
                                                                                  + "/db"));
            conf.setVar(ConfVars.CATALOG_ADDRESS,
                           LQPConfiguration.getConf().getString("planner.catalog.ip", "0.0.0.0")
                               + ":" +
                               LQPConfiguration.getConf()
                                   .getString("planner.catalog.port", "5998"));



        }
        catalog = null;
        System.out.println("LeadsCatalog Server init");
    }

    public static File getTestDir() {
        return new File(System.getProperty("j",
                                              "test-data"));
    }

    public static File getTestDir(final String subdirName) {
        return new File(getTestDir(), subdirName);
    }

    public static File setupClusterRandBuildDir() {
        String randomStr = UUID.randomUUID().toString();
        String dirStr = getTestDir(randomStr).toString();
        File dir = new File(dirStr).getAbsoluteFile();
        // Have it cleaned up on exit
        dir.deleteOnExit();
        return dir;
    }

    public CatalogService getCatalog() {
        return catalog;
    }

    ;

    public boolean StartServer() throws Exception {
        try {
            catalogServer = new MiniCatalogServer(conf);
            CatalogServer catServer = catalogServer.getCatalogServer();
            InetSocketAddress sockAddr = catServer.getBindAddress();
            conf.setVar(ConfVars.CATALOG_ADDRESS, NetUtils.normalizeInetSocketAddress(sockAddr));
        } catch (Exception e) {
            System.err.println("Unable to Start Server" + e.getMessage());
            return false;
        }
        return true;
    }

}
