package eu.leads.processor.plugins.pagerank;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.plugins.PluginInterface;
import eu.leads.processor.plugins.pagerank.graph.DSPM;
import org.apache.commons.configuration.Configuration;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.util.Iterator;
import java.util.List;

public class PagerankPlugin implements PluginInterface {

    private String id;
    protected List<String> attributes;
    private DSPM myDSPM;
    private Logger log = LoggerFactory.getLogger(PagerankPlugin.class);
    private ObjectMapper my_mapper;

    @Override
    public void setId(String s) {
        this.id = s;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getClassName() {
        return PagerankPlugin.class.getCanonicalName();
    }

    @Override
    public void initialize(Configuration configuration, InfinispanManager infinispanManager) {

        my_mapper = new ObjectMapper();
        attributes = configuration.getList("attributes");

        myDSPM = new DSPM(Integer.parseInt(configuration.getString("R"))-1, configuration,
            infinispanManager, Integer.parseInt(configuration.getString("rseed")));

        //testing(configuration);
    }

    /*private void testing(Configuration configuration){
        DenseDoubleMatrix1D google = null;
        try {
            google = Utils.readData_retrieveGooglePR(configuration.getString("input"), configuration.getString("tempPath"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            myDSPM.realTimeFilling(configuration.getString("tempPath"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(Utils.evaluate(google, myDSPM));
        File file = new File(configuration.getString("tempPath"));
        file.delete();

        System.exit(0);
    }*/

    @Override
    public void cleanup() {
    }

    @Override
    public void modified(Object key, Object value, Cache<Object, Object> objectObjectCache) {
        processCrawled(key, value);
    }

    @Override
    public void created(Object key, Object value, Cache<Object, Object> objectObjectCache) {
        processCrawled(key, value);
    }

    @Override
    public void removed(Object o, Object o2, Cache<Object, Object> objectObjectCache) {
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public void setConfiguration(Configuration configuration) {

    }

    private void processCrawled(Object key, Object value) {

        JsonObject p = null;
        p = (JsonObject) value;
        //remove self-loops (meaningless concerning PageRank)
        String skey = key.toString();
        Iterator<Object> iterator = p.getArray("links").iterator();
        while(iterator.hasNext()) {
            String u = (String) iterator.next();
            //           for (URL u : p.getLinks()) {
            if (!(skey.equals(u.toString()))) {
                myDSPM.processEdge(skey, u.toString());
            }
        }
    }
}

