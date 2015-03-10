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
    private Cache ownersCache;
    private Cache activePluginCache;
    public UndeployPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                       String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
        ownersCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.OWNERSCACHE);
        activePluginCache = (Cache) persistence.getPersisentCache(StringConstants.PLUGIN_ACTIVE_CACHE);
    }

    @Override
    public Action process(Action action) {
        System.out.println("Undeploy plugin action arrived: " + action.toString());

        Action result = action;
        JsonObject actionResult = new JsonObject();
        JsonObject reply = new JsonObject();
        reply.putString("status","UNKNOWN");
        actionResult.putObject("reply",reply);
        JsonObject undeployAction = new JsonObject();
        JsonObject undeployPlugin = action.getData();
        //READ parameters;
//      req.putString("pluginid",pluginId);
//      req.putString("cachename",cacheName);
//      req.putString("user",username);
        String cacheName = undeployPlugin.getString("cachename");
        String pluginId = undeployPlugin.getString("pluginid");
        String username = undeployPlugin.getString("user");

        if(activePluginCache.containsKey(activePluginCache.getName()+":"+pluginId+username)){
            if(ownersCache.containsKey(pluginId+username)) {
                //Everything are fine so read the NQEProcessor that deployed the plugin
                String nqeOwner = (String) ownersCache.get(pluginId+username);
                //Create Action to undeployPlugin
                undeployAction = action.getData();
                undeployAction.putString("owner",nqeOwner);
                actionResult.putObject("undeployAction",undeployAction);
                result.setStatus(ActionStatus.COMPLETED.toString());
                reply.putString("status","SUCCESS");
                actionResult.putObject("reply",reply);
                actionResult.putObject("undeployAction",undeployAction);
            }
            else{
                reply.putString("status","FAIL");
                reply.putString("message","could find owner of plugin");
                result.setStatus(ActionStatus.COMPLETED.toString());
                actionResult.putObject("reply",reply);

            }
        }
        else{
          reply.putString("status","FAIL");
          reply.putString("message","could find owner of plugin");
          result.setStatus(ActionStatus.COMPLETED.toString());
          actionResult.putObject("reply", reply);
        }
        result.setResult(actionResult);
        result.setStatus(ActionStatus.COMPLETED.toString());
        return result;
    }
}
