import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.NutchTransformer;
import org.apache.avro.generic.GenericData;
import org.apache.commons.lang.StringUtils;
import org.apache.nutch.storage.WebPage;
import org.vertx.java.core.json.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by vagvaz on 02/08/15.
 */
public class PushData {
    private static int delay;
    private static int skip = 0;

    public static void main(String[] args) {
        OutputHandler dummy = new DummyOutputHandler();
        LQPConfiguration.initialize();
        List<Object> configList = LQPConfiguration.getInstance().getConfiguration().getList("ignorelist");
        String[] desiredDomains = null;
        if(configList != null) {
            desiredDomains = new String[configList.size()];
            int index = 0;
            for (Object domain : configList) {
                String uri = (String) domain;
                String nutchUri = (uri);
                desiredDomains[index++] = nutchUri;
            }
        }

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

        if(args.length > 3){
            delay = Integer.parseInt(args[3]);
            System.out.println("Using delay");
        }

        if(args.length > 4) {
            skip = Integer.parseInt(args[4]);
            System.out.println("skip " + skip);
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
        Set<String> keys = new HashSet<String>();

        while (inputHandler.hasNext()) {
            // Map.Entry<String,WebPage> entry;
            //            entry = (Map.Entry<String,WebPage>) inputHandler.next();
            Map.Entry<String, GenericData.Record> entry = inputHandler.next();
            processed++;
            if (processed % 100 == 0) {
                System.err.println("processed " + processed);
            }
            if(skip > 0) {
                skip--;
                continue;
            }
            //            Map.Entry<String,GenericData.Record> entry = (Map.Entry<String, GenericData.Record>) inputHandler.next();
            //            if(entry != null)
            //           System.err.println("key: " + entry.getKey() + " value " + entry.getValue().toString() +"\ncontent ==" + );
            dummy.append(entry.getKey(), entry);
//            JsonObject ob = new JsonObject(entry.toString());

            System.out.println("---------------------------------------------------------------");
            if (entry == null) {
                continue;
            }
            if ((entry.getValue().get(entry.getValue().getSchema().getField("content").pos()) != null)) {
                Tuple tuple = transformer.transform(entry.getValue());
                dummy.append(tuple.getAttribute("default.webpages.url"), tuple);
                //                outputHandler.append(tuple.getAttribute("url"), tuple);
                //                outputHandler2.append(tuple.getAttribute("url"), new JsonObject(tuple.toString()).encodePrettily());
                //                outputHandler3.append(entry.getValue().get(entry.getValue().getSchema().getField("url").pos()).toString(), entry.getValue().toString());
                String key_url = tuple.getAttribute("default.webpages.url");
                if(desiredDomains != null) {
                    if (!StringUtils.startsWithAny(key_url, desiredDomains)) {
                        rejected++;
                        if (rejected % 100 == 0) {
                            System.err.println("rejected " + rejected);
                        }
                        continue;
                    }
                }
                String key_ts = tuple.getAttribute("default.webpages.ts");
                String key = "default.webpages:" + key_url + "," + key_ts;
                keys.add(key);

                outputHandler.append("default.webpages:" + key_url + "," + key_ts, tuple);
                if(delay > 0){
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                counter++;
                if (counter % 100 == 0) {
                    System.err.println("read " + counter);
                }
            } else {
                rejected++;
                if (rejected % 100 == 0) {
                    System.err.println("rejected " + rejected);
                }
            }
        }
        System.out.println("processed " + processed + " rejected " + rejected + " read " + counter);
        inputHandler.close();
        outputHandler.close();
        System.err.println("Size of keys: " + keys.size());
        System.exit(0);
    }
    private static String transformUri(String standardUrl) {
        String nutchUrl = "";
        URL url_;
        try {
            url_ = new URL(standardUrl);

            String authority = url_.getAuthority();
            String protocol  = url_.getProtocol();
            String file      = url_.getFile();

            String [] authorityParts = authority.split("\\.");
            for(int i=authorityParts.length-1; i>=0; i--)
                nutchUrl += authorityParts[i] + ".";
            nutchUrl = nutchUrl.substring(0, nutchUrl.length()-1);
            nutchUrl += ":" + protocol;
            nutchUrl += file;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return nutchUrl;
    }
}
