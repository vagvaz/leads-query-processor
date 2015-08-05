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
public class ReadData {
    public static void main(String[] args) {
        OutputHandler dummy = new DummyOutputHandler();

        InputHandler inputHandler = new GoraInputHandler();
        Properties inputConfig = new Properties();
        inputConfig.setProperty("limit",Integer.toString(20000));
        inputConfig.setProperty("batchSize",Integer.toString(100));
        inputConfig.setProperty("connectionString", "clusterinfo.unineuchatel.ch:11224");
        inputConfig.setProperty("offset", Integer.toString(800));

//        InputHandler<String,GenericData.Record> inputHandler = new FileInputHandler();
//        Properties inputConfig = new Properties();
//        inputConfig.setProperty("baseDir","/tmp/leads/unine");
////        inputConfig.setProperty("prefix","crawling");
//        inputConfig.setProperty("limit","60");
//        inputConfig.put("valueClass", (new GenericData.Record(WebPage.SCHEMA$)));
//        inputConfig.put("keyClass",String.class);



        inputHandler.initialize(inputConfig);

        Properties outputConfig = new Properties();
        outputConfig.setProperty("nutchData","true");
        outputConfig.setProperty("baseDir","/tmp/leads/unine");
        outputConfig.setProperty("filename","crawling");
        outputConfig.setProperty("valueThreshold", "3");
        OutputHandler outputHandler = new FileHandlerOutput<String,WebPage>();
//        outputHandler.initialize(outputConfig);
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
            Map.Entry<String,WebPage> entry = (Map.Entry<String, WebPage>) inputHandler.next();
            processed++;
            if(processed % 100 == 0){
                System.err.println("processed " + processed);
            }
//            Map.Entry<String,GenericData.Record> entry = (Map.Entry<String, GenericData.Record>) inputHandler.next();
//            if(entry != null)
//           System.err.println("key: " + entry.getKey() + " value " + entry.getValue().toString() +"\ncontent ==" + );
//            Tuple tuple = transformer.transform(entry.getValue());
//            outputHandler.append(tuple.getAttribute("url"), tuple);
//            dummy.append(tuple.getAttribute("url"), tuple);
            if((entry.getValue().get(entry.getValue().getSchema().getField("content").pos()) != null)) {
                outputHandler.append(entry.getKey(), entry.getValue());
                dummy.append(entry.getKey(), entry.getValue());
                counter++;
                if(counter % 100 == 0){
                    System.err.println("read " +counter);
                    break;
                }
            }else{
//                System.err.println("reject cause not having content");
                rejected++;
                if(rejected % 100==0){
                    System.err.println("rejected " + rejected);
                }
            }
        }
        inputHandler.close();
        outputHandler.close();

    }
}
