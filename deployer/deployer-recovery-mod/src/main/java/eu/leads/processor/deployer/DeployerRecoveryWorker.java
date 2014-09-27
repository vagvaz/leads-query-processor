package eu.leads.processor.deployer;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.planner.QueryPlannerConstants;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.UUID;

/**
 * Created by vagvaz on 8/27/14.
 */
public class DeployerRecoveryWorker extends Verticle implements LeadsMessageHandler {
    private final String componentType = "planner";
    JsonObject config;
    String deployer;
    String planner;
    LogProxy log;
    PersistenceProxy persistence;
    Node com;
    String id;
    String workQueueAddress;
    ObjectMapper mapper;

    @Override
    public void start() {
        super.start();
        config = container.config();
        deployer = config.getString("deployer");
        planner = config.getString("planner");
        workQueueAddress = config.getString("workqueue");
        id = config.getString("id");
        com = new DefaultNode();
        com.initialize(id, deployer, null, this, null, vertx);
        log = new LogProxy(config.getString("log"), com);
        persistence = new PersistenceProxy(config.getString("persistence"), com, vertx);
        persistence.start();
        mapper = new ObjectMapper();

    }

    @Override
    public void stop() {
        super.stop();
        com.unsubscribeFromAll();
    }

    @Override
    public void handle(JsonObject msg) {
        String type = msg.getString("type");
        String from = msg.getString(MessageUtils.FROM);
        String to = msg.getString(MessageUtils.TO);
        if (type.equals("action")) {
            Action action = new Action(msg);
            String label = action.getLabel();
            Action newAction = null;
            action.setProcessedBy(id);
            //         action.setStatus(ActionStatus.INPROCESS.toString());

            switch (ActionStatus.valueOf(action.getStatus())) {
                case PENDING: //probably received an action from an external source
                    //               if (label.equals(DeployerConstants.PROCESS_QUERY)) {
                    action.getData().putString("replyTo", msg.getString("from"));
                    com.sendWithEventBus(workQueueAddress, action.asJsonObject());
                    //               } else if (label.equals(QueryPlannerConstants.PROCESS_SPECIAL_QUERY)) {
                    action.getData().putString("replyTo", msg.getString("from"));
                    com.sendWithEventBus(workQueueAddress, action.asJsonObject());
                    //               } else {
                    log.error("Unknown PENDING Action received " + action.toString());
                    //                  return;
                    //               }
                    action.setStatus(ActionStatus.INPROCESS.toString());
                    if (newAction != null) {
                        action.addChildAction(newAction.getId());
                        logAction(newAction);
                    }
                    logAction(action);
                    break;
                case INPROCESS: //  probably received an action from internal source (processors)
                case COMPLETED: // the action either a part of a multistep workflow (INPROCESSING) or it could be processed.
                    if (label.equals(QueryPlannerConstants.PROCESS_SQL_QUERY)) {
                        com.sendTo(action.getData().getString("replyTo"), action.getResult());
                    } else if (label.equals(QueryPlannerConstants.PROCESS_SPECIAL_QUERY)) {
                        com.sendTo(action.getData().getString("replyTo"), action.getResult());
                    } else {
                        log.error("Unknown COMPLETED OR INPROCESS Action received " + action
                                                                                          .toString());
                        return;
                    }
                    finalizeAction(action);
            }
        }
    }

    private void finalizeAction(Action action) {
        //TODO
        //1 inform monitor about completion (if it is completed in this logic each action requires 1 step processing
        //2 remove from processing if necessary
        //3 update action to persistence service
    }


    private void logAction(Action action) {
        //TODO
        //1 inform monitor about action.
        //2 add action to processing set
        //3 update action to persistence service
    }

    private Action createNewAction(Action action) {
        Action result = new Action();
        result.setId(UUID.randomUUID().toString());
        result.setTriggered(action.getId());
        result.setComponentType(componentType);
        result.setStatus(ActionStatus.PENDING.toString());
        result.setTriggers(new JsonArray());
        result.setOwnerId(this.id);
        result.setProcessedBy("");
        result.setDestination("");
        result.setData(null);
        result.setResult(null);
        result.setLabel("");
        result.setCategory("");
        return result;
    }

}
