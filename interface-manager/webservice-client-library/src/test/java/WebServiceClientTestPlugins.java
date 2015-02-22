import eu.leads.processor.plugins.EventType;
import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.web.QueryStatus;
import eu.leads.processor.web.WebServiceClient;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by vagvaz on 8/23/14.
 */
public class WebServiceClientTestPlugins {
    private static String host;
    private static int port;

    public static void main(String[] args) throws IOException {
        host = "http://localhost";
        port = 8080;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        try {
            if(WebServiceClient.initialize(host, port))
                System.out.println("Server is Up");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        JsonObject object = new JsonObject();
//        object.putString("name", "vag");
//        object.putString("surname", "vaz");
//        object.putString("age", "18");
//        object.putString("id", "91818111");
//        try {
//            WebServiceClient.putObject("testCache", object.getString("id"), object);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JsonObject mapObject =
//            WebServiceClient.getObject("testCache", object.getString("id"), null);
//        if (mapObject.toString().equals(object.toString())) {
//            System.out.println("Equals " + object.toString() + "\n" + mapObject.toString());
//        } else {
//            System.out.println("PROBLEM");
//            System.out.println(object.toString());
//            System.out.println(mapObject.toString());
//        }

        String Example =  " SELECT deptname from dept" + "\r\n";

        byte [] barray = new byte[2000];

        for(int i =0; i < barray.length; i++)
            barray[i]=120;
        String Query=new String(barray,0,barray.length);
        System.out.println("Query Size " + Query.length());

        PluginPackage testpackage = new PluginPackage("56", "aclass");
        testpackage.setJar(barray);


        System.out.print("Serialized size: " + barray.length);
       QueryStatus currentStatus = WebServiceClient.submitPlugin("vagvaz", testpackage);



//        WebServiceClient.submitQuery("vagvaz",
//                Example);
       // QueryStatus currentStatus = WebServiceClient.getQueryStatus(status.getId());
        //System.out.println("s: " + status.toString());
        System.out.println("o: " + currentStatus.toString());
        XMLConfiguration config = new XMLConfiguration();

        System.out.println("Query Size " + Query.length());


        System.out.print("Serialized size: " + barray.length);

        currentStatus = WebServiceClient.deployPlugin("vagvaz", testpackage.getId(),config,"webpages", EventType.CREATEANDMODIFY);
        System.out.println("o: " + currentStatus.toString());

    }

    private static String createDataSize(int msgSize) {
    // Java chars are 2 bytes
    msgSize = msgSize/2;
    msgSize = msgSize * 1024;
    StringBuilder sb = new StringBuilder(msgSize);
    for (int i=0; i<msgSize; i++) {
        sb.append('a');
    }
    return sb.toString();
  }
}
