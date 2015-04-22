import eu.leads.processor.conf.LQPConfiguration;

/**
 * Created by vagvaz on 4/13/15.
 */
public class ReplayTest {
   public static void main(String[] args) {
      LQPConfiguration.initialize();
      ReplayTool tool = new ReplayTool("/tmp/leads-crawler-snapshot1","catalog-worker-default.webpages","catalog-worker-nutchWebBackup|planner-nutchWebBackup|imanager-nutchWebBackup|deployer-nutchWebBackup|nqe-nutchWebBackup",
                                              LQPConfiguration.getInstance().getConfiguration().getString("node.ip")+":11222");
      tool.replayNutch(true);
   }
}
