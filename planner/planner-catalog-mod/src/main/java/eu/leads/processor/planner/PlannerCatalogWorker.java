package eu.leads.processor.planner;

import leads.tajo.catalog.LeadsCatalog;
import org.apache.tajo.conf.TajoConf;
import org.vertx.java.platform.Verticle;

/**
 * Created by vagvaz on 8/25/14.
 */
public class PlannerCatalogWorker  extends Verticle{
   LeadsCatalog catalogServer = null;
   @Override
   public void start() {
      super.start();
      TajoConf conf = new TajoConf();
      //Read configuration
      catalogServer = new LeadsCatalog(null);
      try {
         catalogServer.StartServer();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
