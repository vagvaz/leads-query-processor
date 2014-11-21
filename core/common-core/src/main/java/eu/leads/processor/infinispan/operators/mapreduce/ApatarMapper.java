package eu.leads.processor.infinispan.operators.mapreduce;

import eu.leads.processor.core.LeadsMapper;
import eu.leads.processor.core.Tuple;
import org.infinispan.distexec.mapreduce.Collector;
import org.vertx.java.core.json.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ApatarMapper extends LeadsMapper<String, String, String, String> {

    public ApatarMapper(JsonObject configuration) {
        super(configuration);
    }

    public ApatarMapper(String configString) {
        super(configString);
    }


    @Override
    public void map(String key, String value, Collector<String, String> collector) {
//      System.out.println("Called for " + key + "     " + value);
        if (!isInitialized)
            intialize();
        StringBuilder builder = new StringBuilder();
//        String tupleId = key.substring(key.indexOf(":"));
        Tuple t = new Tuple(value);
        if(t.hasField("uri"))
            t.setAttribute("uri", transformUri(t.getAttribute("uri")));
        if(t.hasField("fqdnurl"))
            t.setAttribute("fqdnurl", transformUri(t.getAttribute("fqdnurl")));
        
        t.setAttribute("ts", transformTs(t.getNumberAttribute("ts")));

        collector.emit(key, t.asString());

    }

    private String transformTs(Number ts) {
        Date currentDatetime = new Date(ts.longValue());
        //System.out.println(" Ts: " + ts);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        //System.out.println(" DateString: " + df.format(currentDatetime));
        return df.format(currentDatetime);
    }

    private String transformUri(String nutchUrlBase) {

        String domainName = "";
        String url = "";

        String[] parts = nutchUrlBase.split(":");
        String nutchDomainName = parts[0];

        String[] words = nutchDomainName.split("\\.");

        for (int i = words.length - 1; i >= 0; i--) {
            domainName += words[i] + ".";
        }
        domainName = domainName.substring(0, domainName.length() - 1);

        if (parts.length == 2) {
            System.out.print("Parts[1]:" + parts[1]);
            String[] parts2 = parts[1].split("/");
            if (parts2[0].startsWith("http")) ;
            url = parts2[0] + "://" + domainName;
            for (int i = 1; i < parts2.length; i++) {
                url += "/" + parts2[i];
            }
        }
        return url;

    }

    private void intialize() {
        isInitialized = true;
//       System.err.println("-------------Initialize");
        super.initialize();
    }


}
