package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.QueryState;
import eu.leads.processor.core.plan.QueryStatus;
import eu.leads.processor.plugins.PluginManager;
import eu.leads.processor.plugins.PluginPackage;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.UUID;

/**
 * Created by vagvaz on 8/6/14.
 */
public class RegisterPluginActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;
    private Cache<String,String> queriesCache;

    public RegisterPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                       String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
        queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
    }
    @Override
    public Action process(Action action) {
        System.out.println("s New action arrived: " + action.toString());
        Action result = action;
        JsonObject actionResult = new JsonObject();
        try {
            JsonObject q =  action.getData();
            String user = q.getString("user");
            String uniqueId = generateNewQueryId(user);
            PluginPackage pack = new PluginPackage(q.getString("id"),q.getString("classname"));
            pack.setJar(q.getBinary("jar"));
            pack.setJar(q.getBinary("conf"));
            q.removeField("jar");
            QueryStatus status;
            if (PluginManager.uploadPlugin(pack)) {
                result.setStatus(ActionStatus.COMPLETED.toString());
                status = new QueryStatus(uniqueId, QueryState.COMPLETED, "");
            }
            else {
                status = new QueryStatus(uniqueId, QueryState.FAILED, "");
                result.setStatus(ActionStatus.FAILED.toString());
            }

            JsonObject queryStatus = status.asJsonObject();
            actionResult.putObject("status", queryStatus);
            result.setResult(actionResult);

        } catch (Exception e) {

            actionResult.putString("error", "");
            actionResult.putString("message",
                    "Failed to add query " + action.getData().toString() + "\n"
                            + " to the plugin cache");
        }

        System.out.println(" result " + result.toString());
        return result;
    }

    private String generateNewQueryId(String prefix) {
        String candidateId = prefix + "." + UUID.randomUUID();
        while (queriesCache.containsKey(candidateId)) {
            candidateId = prefix + "." + UUID.randomUUID();
        }
        return candidateId;
    }}


