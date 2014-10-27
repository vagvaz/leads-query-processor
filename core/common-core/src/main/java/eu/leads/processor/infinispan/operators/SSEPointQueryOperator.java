package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * Created by vagvaz on 10/27/14.
 */
public class SSEPointQueryOperator extends BasicOperator {
    private String targetCacheName;
//    Map<> encryptedDB;
//    Map<> encryptedIndex;
    Map<String,String> metaCache;

    protected SSEPointQueryOperator(Node com, InfinispanManager manager, LogProxy log, Action action) {
        super(com, manager, log, action);
    }


    @Override
    public void init(JsonObject config) {
        super.init(conf);
        targetCacheName = conf.getString("targetCache");

//        metaCache = manager.getPersisentCache(tar);
    }

    @Override
    public void run() {

    }
}
