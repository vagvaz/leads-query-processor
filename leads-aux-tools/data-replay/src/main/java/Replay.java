import eu.leads.processor.conf.LQPConfiguration;

/**
 * Created by tr on 21/4/2015.
 */
public class Replay {

      public static void main(String[] args) {
          if(args.length <1){
              System.err.println("Please enter Ip address");
              System.exit(0);
          }
        LQPConfiguration.initialize();
          ReplayTool tool = new ReplayTool("/tmp/crawler-snapshot/","catalog-worker-default.webpages","catalog-worker-nutchWebBackup|planner-nutchWebBackup|imanager-nutchWebBackup|deployer-nutchWebBackup|nqe-nutchWebBackup",args[0]+":11222");
          tool.replayNutch(true);
    }
}
