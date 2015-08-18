import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.NutchTransformer;
import org.apache.avro.generic.GenericData;
import org.apache.nutch.storage.WebPage;
import org.vertx.java.core.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
 /**
 * A Factory for {@link DataStore}s. DataStoreFactory instances are thread-safe.
 */

/**
 * Created by vagvaz on 02/08/15.
 */
public class ReadData {
    public static void main(String[] args) {
        OutputHandler dummy = new DummyOutputHandler();

        InputHandler inputHandler = new GoraInputHandler();
        Properties inputConfig = new Properties();
        inputConfig.setProperty("limit",Integer.toString(1000000));
        inputConfig.setProperty("batchSize",Integer.toString(1000));
        //inputConfig.setProperty("connectionString", "clusterinfo.unineuchatel.ch:11225");
        String ensembleString = "clusterinfo.unineuchatel.ch:11225";
        ensembleString = "192.42.43.31:11222;192.42.43.31:11223;192.42.43.31:11224;192.42.43.31:11225;192.42.43.31:11226;192.42.43.31:11227;192.42.43.31:11228;192.42.43.31:11229;192.42.43.31:11230";
        if(args.length>0)
            ensembleString  = args[0];
        System.out.println("Using  connection String: " + ensembleString);
        inputConfig.setProperty("connectionString", ensembleString );
        inputConfig.setProperty("offset", Integer.toString(55000));

//        InputHandler<String,GenericData.Record> inputHandler = new FileInputHandler();
//        Properties inputConfig = new Properties();
//        inputConfig.setProperty("baseDir", "/tmp/leads/unine");
////        inputConfig.setProperty("prefix","crawling");
//        inputConfig.setProperty("limit", "2000000");
//        inputConfig.put("valueClass", (new GenericData.Record(WebPage.SCHEMA$)));
//        inputConfig.put("keyClass", String.class);
//
//
//
        inputHandler.initialize(inputConfig);

        Properties outputConfig = new Properties();
        outputConfig.setProperty("nutchData","false");
        outputConfig.setProperty("baseDir", "/tmp/leads/transform");
        outputConfig.setProperty("filename", "tuples");
        outputConfig.setProperty("valueThreshold", "10000");
        OutputHandler outputHandler = new FileHandlerOutput<String,Tuple>();
        outputHandler.initialize(outputConfig);

//        outputConfig.setProperty("nutchData","false");
//        outputConfig.setProperty("baseDir","/tmp/leads/sampling");
//        outputConfig.setProperty("filename","tuples");
//        outputConfig.setProperty("valueThreshold", "3");
       // OutputHandler outputHandler2 = new FileHandlerOutput<String,String>();
       // outputHandler2.initialize(outputConfig);
//        outputConfig.setProperty("filename", "nutch");
        //OutputHandler outputHandler3 = new FileHandlerOutput<String,String>();
        //outputHandler3.initialize(outputConfig);

        //        outputHandler = new DummyOutputHandler();
        LQPConfiguration.initialize();
        List<String> mappings = LQPConfiguration.getInstance().getConfiguration().getList("nutch.mappings");
        Map<String,String> nutchToLQE = new HashMap<String,String>();

        for(String mapping : mappings ){
            String[] keyValue = mapping.split(":");
            nutchToLQE.put(keyValue[0].trim(),keyValue[1].trim());
        }
        NutchTransformer transformer = new NutchTransformer(nutchToLQE);
        int counter =0 ;
        int rejected = 0;
        int processed = 0;
        while(inputHandler.hasNext()){
           // Map.Entry<String,WebPage> entry = (Map.Entry<String,WebPage>) inputHandler.next();
            Map.Entry<String,GenericData.Record> entry = (Map.Entry<String, GenericData.Record>) inputHandler.next();
            processed++;
            if(processed % 100 == 0){
                System.err.println("processed " + processed);
            }
//            Map.Entry<String,GenericData.Record> entry = (Map.Entry<String, GenericData.Record>) inputHandler.next();
//            if(entry != null)
//           System.err.println("key: " + entry.getKey() + " value " + entry.getValue().toString() +"\ncontent ==" + );
//            dummy.append(tuple.getAttribute("url"), tuple);
            if(entry == null){
                continue;
            }
            if((entry.getValue().get(entry.getValue().getSchema().getField("content").pos()) != null)) {
                Tuple tuple = transformer.transform(entry.getValue());
                outputHandler.append(tuple.getAttribute("url"), tuple);
//                outputHandler2.append(tuple.getAttribute("url"), new JsonObject(tuple.toString()).encodePrettily());
//                outputHandler3.append(entry.getValue().get(entry.getValue().getSchema().getField("url").pos()).toString(), entry.getValue().toString());

//                outputHandler.append(entry.getKey(), entry.getValue());

                dummy.append(entry.getKey(), entry.getValue());
                counter++;
                if(counter % 100 == 0){
                    System.err.println("read " +counter);
                }
            }else{
//                System.err.println("reject cause not having content");
                rejected++;
                if(rejected % 100==0){
                    System.err.println("rejected " + rejected);
                }
            }
        }
        System.out.println("processed " + processed + " rejected " + rejected + " read " + counter);
        inputHandler.close();
        outputHandler.close();

    }
}
