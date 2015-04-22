/**
 * Created by vagvaz on 4/20/15.
 */
public class DataRemoteReaderTest {
  public static void main(String[] args) {

    String conString = "192.168.1.76:11222";
    String ensemble  = "192.168.1.76:11223";
    boolean distributed = false;
    String prefix = "/tmp/leads-crawler-snapshot1/";
    String base = "catalog-worker";
    long delay =  10;
    String[] snapshotArgs = new String[3];
    snapshotArgs[0] = conString;
    snapshotArgs[1] = prefix;
    snapshotArgs[2] = base;
    String[] copyArgs = new String[4];
    copyArgs[0] = conString;
    copyArgs[1] = ensemble;
    copyArgs[2] = Long.toString(10L);
    copyArgs[3] = "n";

    DataSnapshot.main(snapshotArgs);
    DataCopy.main(copyArgs);
  }
}
