/**
 * Created by vagvaz on 4/20/15.
 */
public class DataSnapshot {
  public static void main(String[] args) {
    if(args.length != 3){
      System.err.println("Usage: program remoteCacheString prefix basePath");
      System.exit(-1);
    }

    String remoteCache = args[0];
    String prefix = args[1];
    String basePath = args[2];

    DataRemoteReader reader = new DataRemoteReader(remoteCache);
    long counter = reader.storeToFile(prefix,basePath);
    System.out.println("We saved " + counter + " nutch webpages into " + prefix +"/" + basePath + "-"+
                         "nutchWebBackup0.keys");
    System.exit(0);
  }
}
