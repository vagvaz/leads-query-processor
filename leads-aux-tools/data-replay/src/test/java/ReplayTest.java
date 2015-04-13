/**
 * Created by vagvaz on 4/13/15.
 */
public class ReplayTest {
   public static void main(String[] args) {
      ReplayTool tool = new ReplayTool("/tmp/leads-crawler-snapshot/","catalog-worker-default.webpages","planner-nutchWebBackup","");
      tool.replayNutch();
   }
}
