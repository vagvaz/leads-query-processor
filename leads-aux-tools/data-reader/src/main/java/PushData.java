import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.NutchTransformer;
import org.apache.avro.generic.GenericData;
import org.apache.nutch.storage.WebPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vagvaz on 02/08/15.
 */
public class PushData {
    public static void main(String[] args) {
        OutputHandler dummy = new DummyOutputHandler();

//        InputHandler inputHandler = new GoraInputHandler();
//        Properties inputConfig = new Properties();
//        inputConfig.setProperty("limit",Integer.toString(200000));
//        inputConfig.setProperty("batchSize",Integer.toString(1000));
//        inputConfig.setProperty("connectionString", "clusterinfo.unineuchatel.ch:11225");
//        inputConfig.setProperty("offset", Integer.toString(55000));

        String baseDir = "/tmp/leads/nutchRaw";
        if (args.length > 0) {
            baseDir = args[0];
        }
        System.out.println("BaseDir " + baseDir);

        InputHandler<String, GenericData.Record> inputHandler = new FileInputHandler();
        Properties inputConfig = new Properties();
        inputConfig.setProperty("baseDir", baseDir);
        inputConfig.setProperty("prefix", "nutch");
        inputConfig.setProperty("limit", "2000000");
        inputConfig.put("valueClass", (new GenericData.Record(WebPage.SCHEMA$)));
        inputConfig.put("keyClass", String.class);
//
//
//
        inputHandler.initialize(inputConfig);

        Properties outputConfig = new Properties();
        outputConfig.setProperty("nutchData", "true");
        outputConfig.setProperty("baseDir", "/tmp/leads/transform");
        outputConfig.setProperty("filename", "tuples");
        outputConfig.setProperty("valueThreshold", "10000");
        String tablename = "emptyName";
        if (args.length > 1) {
            tablename = args[1];
            outputConfig.setProperty("cacheName", tablename);
            System.out.println(" cacheName: " + tablename);
        }

        if (args.length > 2) {
            outputConfig.setProperty("remote", args[2]);
            System.out.println(" remoteString: " + args[2]);
        }



        OutputHandler outputHandler = new CacheOutputHandler();
        outputHandler.initialize(outputConfig);

        LQPConfiguration.initialize();
        List<String> mappings = LQPConfiguration.getInstance().getConfiguration().getList("nutch.mappings");
        Map<String, String> nutchToLQE = new HashMap<String, String>();

        for (String mapping : mappings) {
            String[] keyValue = mapping.split(":");
            nutchToLQE.put(keyValue[0].trim(), keyValue[1].trim());
        }
        NutchTransformer transformer = new NutchTransformer(nutchToLQE);
        int counter = 0;
        int rejected = 0;
        int processed = 0;
        while (inputHandler.hasNext()) {
            // Map.Entry<String,WebPage> entry;
//            entry = (Map.Entry<String,WebPage>) inputHandler.next();
            Map.Entry<String, GenericData.Record> entry = inputHandler.next();
            processed++;
            if (processed % 100 == 0) {
                System.err.println("processed " + processed);
            }
//            Map.Entry<String,GenericData.Record> entry = (Map.Entry<String, GenericData.Record>) inputHandler.next();
//            if(entry != null)
//           System.err.println("key: " + entry.getKey() + " value " + entry.getValue().toString() +"\ncontent ==" + );
//            dummy.append(tuple.getAttribute("url"), tuple);
            if (entry == null) {
                continue;
            }
            if ((entry.getValue().get(entry.getValue().getSchema().getField("content").pos()) != null)) {
                Tuple tuple = transformer.transform(entry.getValue());
//                outputHandler.append(tuple.getAttribute("url"), tuple);
//                outputHandler2.append(tuple.getAttribute("url"), new JsonObject(tuple.toString()).encodePrettily());
//                outputHandler3.append(entry.getValue().get(entry.getValue().getSchema().getField("url").pos()).toString(), entry.getValue().toString());
                String key_url =  tuple.getAttribute("default.webpages.url");
                String key_ts =  tuple.getAttribute("default.webpages.ts");
                outputHandler.append(key_url+","+key_ts, tuple);

                //   dummy.append(entry.getKey(), entry.getValue());
                counter++;
                if (counter % 100 == 0) {
                    System.err.println("read " + counter);
                }
            } else {
//                System.err.println("reject cause not having content");
                rejected++;
                if (rejected % 100 == 0) {
                    System.err.println("rejected " + rejected);
                }
            }
        }
        System.out.println("processed " + processed + " rejected " + rejected + " read " + counter);
        inputHandler.close();
        System.out.println("-------------------------INPUT HANDLER CLOSED");
        outputHandler.close();
        System.out.println("--------------------------------------------------OUTPUT HANDLER CLOSED");
    }
}
