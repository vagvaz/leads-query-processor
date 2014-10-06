package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/6/14.
 */
public class UndeployPluginActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;
    private Cache<String, String> queriesCache;
    private Cache activePluginCache;
    public UndeployPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                       String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
        queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
        activePluginCache = (Cache) persistence.getPersisentCache(StringConstants.PLUGIN_ACTIVE_CACHE);
    }

    @Override
    public Action process(Action action) {
        System.out.println("Undeploy plugin action arrived: " + action.toString());

        Action result = action;
        JsonObject actionResult = new JsonObject();
        //READ parameters;
        String cacheName = ""; //action.getData().getString("");
        String pluginId = "";
        String username = "";
        JsonObject status = new JsonObject();
        if(activePluginCache.containsKey(cacheName+":"+pluginId+":jar")){
            if(activePluginCache.containsKey(cacheName+":"+pluginId+":owner")) {
                //Everything are fine so read the NQEProcessor that deployed the plugin
                String nqeOwner = (String) activePluginCache.get(cacheName + ":" + pluginId + ":owner");
                //Create Action to undeployPlugin
                JsonObject undeployAction = action.getData();
                undeployAction.putString("owner",nqeOwner);
                actionResult.putObject("undeployAction",undeployAction);
                status.putString("status",ActionStatus.COMPLETED.toString());

            }
            else{
                status.putString("status",ActionStatus.FAILED.toString());
            }
        }
        else{
            status.putString("status",ActionStatus.FAILED.toString());
        }
        actionResult.putObject("status",status);
        result.setResult(actionResult);
        result.setStatus(ActionStatus.COMPLETED.toString());
        return result;
    }



}
