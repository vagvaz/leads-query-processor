/**
 * Created by tr on 21/4/2015.
 */
public class Replay {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Help, Replay args: ip path_of_data_keys prefixes webpagePrefixes delayms_per_put_tocache   \n(prefixes seperated with |)");
            System.exit(0);
        }
        int delay = 1000;
        if (args.length > 4)
            delay = Integer.parseInt(args[4]);

        System.out.println("Delay per put " + delay);
        ReplayTool tool = new ReplayTool(args[1]/*"/tmp/crawler-snapshot/"*/, args[3]/*"catalog-worker-default.webpages"*/, args[2]/*"catalog-worker-nutchWebBackup|planner-nutchWebBackup|imanager-nutchWebBackup|deployer-nutchWebBackup|nqe-nutchWebBackup"*/, args[0] + ":11222");
        tool.setDelay(delay);
        tool.replayNutch(true);
    }
}
