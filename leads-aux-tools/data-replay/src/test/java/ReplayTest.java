/**
 * Created by vagvaz on 4/13/15.
 */
public class ReplayTest {
   public static void main(String[] args) {
      ReplayTool tool = new ReplayTool("/tmp/leads-crawler-snapshot/","catalog-worker-default.webpages","catalog-worker-nutchWebBackup|planner-nutchWebBackup","192.168.1.5:11222");
      tool.replayNutch(true);
   }
}
