/**
 * Created by vagvaz on 4/20/15.
 */
public class DataCopy {
  public static void main(String[] args) {
    if(args.length != 4){
      System.err.println("Usage: program remoteCacheString otherString delay distributed?(y/n)");
      System.exit(-1);
    }

    String remoteCache = args[0];
    String ensemble = args[1];
    long delay = Long.parseLong(args[2]);
    boolean distributed = false;
    if(args[3].toLowerCase().startsWith("y"))
      distributed= true;

    DataRemoteReader reader = new DataRemoteReader(remoteCache);
    long counter = reader.storeToRemoteCache(ensemble,distributed,delay);
    System.out.println("Copied " + counter + " nutch webpages to " + ensemble +" distributed = " + distributed +
                         " with delay "+delay);
    System.exit(0);
  }
}
