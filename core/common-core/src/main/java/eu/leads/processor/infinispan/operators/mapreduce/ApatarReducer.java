package eu.leads.processor.infinispan.operators.mapreduce;

import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.core.LeadsReducer;
import eu.leads.processor.core.Tuple;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.Iterator;

//import eu.leads.processor.common.utils.SQLUtils;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 11/4/13
 * Time: 9:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class ApatarReducer extends LeadsReducer<String, String> {

    transient Cache<String, String> data;
    transient String prefix;

    public ApatarReducer(JsonObject configuration) {
        super(configuration);
    }

    public ApatarReducer(String configString) {
        super(configString);
    }

    @Override
    public void initialize() {

        isInitialized = true;
        super.initialize();
        imanager = new ClusterInfinispanManager(manager);
        prefix = outputCacheName + ":";
        data = (Cache<String, String>) InfinispanClusterSingleton.getInstance().getManager().getPersisentCache(outputCacheName);

    }

    @Override
    public String reduce(String key, Iterator<String> iterator) {
        //Reduce takes all the grouped Typles per key
//      System.out.println("running for " + key + " .");
        if (key == null || key.equals(""))
            return "";

        if (!isInitialized) initialize();

        Tuple t = null;
        //Iterate overall values
        while (iterator.hasNext()) {
            t = new Tuple(iterator.next()); //Check
        }

        key = key.split(":")[1]; // Get the key without the cachename
//        System.err.println("tout: " + t.toString());
        data.put(prefix + key, t.asString());
        return "";
    }

}
