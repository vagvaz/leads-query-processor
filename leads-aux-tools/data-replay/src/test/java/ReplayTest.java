/**
 * Created by vagvaz on 4/13/15.
 */
public class ReplayTest {
   public static void main(String[] args) {
      ReplayTool tool = new ReplayTool("/tmp/leads-crawler-snapshot/","catalog-worker-default.webpages","planner-nutchWebBackup","192.168.1.5:11222");
      tool.replayNutch(true);
   }
}
