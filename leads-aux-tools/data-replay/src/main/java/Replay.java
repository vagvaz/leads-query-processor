/**
 * Created by tr on 21/4/2015.
 */
public class Replay {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Help, Replay args: ip path_of_data_keys delayms_per_1000_put_tocache");
            System.exit(0);
        }
        int delay = 1000;
        if (args.length == 3)
            delay = Integer.parseInt(args[2]);

        ReplayTool tool = new ReplayTool(args[1]/*"/tmp/crawler-snapshot/"*/, "catalog-worker-default.webpages", "catalog-worker-nutchWebBackup|planner-nutchWebBackup|imanager-nutchWebBackup|deployer-nutchWebBackup|nqe-nutchWebBackup", args[0] + ":11222");
        tool.setDelay(delay);
        tool.replayNutch(true);
    }
}
