/**
 * Created by tr on 21/4/2015.
 */
public class Replay {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Help, Replay args: ip:port(,ip:port)+ path_of_data_keys prefixes webpagePrefixes delayms_per_put_tocache   \n(prefixes seperated with |) \n example java -jar data-replay.jar 10.106.0.33:11222(,10.106.0.33:11223) /home/ubuntu/snapshot6268/ test-nutchWebBackup default.webpages 30000");
            System.exit(0);
        }
        int delay = 1000;
        if (args.length > 4)
            delay = Integer.parseInt(args[4]);

        System.out.println("Delay per put " + delay + " targer:  "+ args[3]);
        ReplayTool tool = new ReplayTool(args[1]/*"/tmp/crawler-snapshot/"*/, args[3]/*"catalog-worker-default.webpages"*/, args[2]/*"catalog-worker-nutchWebBackup|planner-nutchWebBackup|imanager-nutchWebBackup|deployer-nutchWebBackup|nqe-nutchWebBackup"*/, args[0]);
        tool.setDelay(delay);
        tool.replayNutch(true);
    }
}
