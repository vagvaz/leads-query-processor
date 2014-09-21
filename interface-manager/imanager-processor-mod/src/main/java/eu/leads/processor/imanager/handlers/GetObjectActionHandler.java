package eu.leads.processor.imanager.handlers;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/6/14.
 */
public class GetObjectActionHandler implements ActionHandler {

    Node com;
    LogProxy log;
    PersistenceProxy persistence;
    String id;

    public GetObjectActionHandler(Node com, LogProxy log, PersistenceProxy persistence, String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
    }

    @Override
    public Action process(Action action) {
        Action result = new Action(action);
        try {
            String cacheName = action.getData().getString("table");
            String key = action.getData().getString("key");
            JsonObject actionResult = persistence.get(cacheName, key);
            if (actionResult.getString("status").equals("ok")) {
                //               com.sendTo(from, result.getObject("result"));
                result.setResult(actionResult.getObject("result"));
            } else {
                actionResult.putString("error", "");
                result.setResult(actionResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
