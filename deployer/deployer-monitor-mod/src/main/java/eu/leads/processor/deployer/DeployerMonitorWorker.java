package eu.leads.processor.deployer;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.nqe.NQEConstants;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by vagvaz on 8/27/14.
 */
public class DeployerMonitorWorker extends Verticle implements LeadsMessageHandler {
    private final String componentType = "deployer-monitor";
    JsonObject config;
    String deployerLogic;
    String nqeGroup;
    String deployerMonitor;
    ConcurrentMap<String, Integer> actionToLevelMap;
    ConcurrentMap<Integer, ConcurrentMap<String, Action>> monitoredActions;
    LogProxy log;
    Node com;
    String id;
    long timerID;
    long period;
    PeriodicCheckHandler periodicHandler;

    @Override
    public void start() {
        super.start();
        config = container.config();

        id = config.getString("id");
        deployerLogic = config.getString("deployer");
        nqeGroup = config.getString("nqe");
        period = config.getLong("period", 600000);
        deployerMonitor = id;
        actionToLevelMap = new ConcurrentHashMap<String, Integer>();
        monitoredActions = new ConcurrentHashMap<Integer, ConcurrentMap<String, Action>>();
        for (int i = -1; i < 4; i++) {
            //-1: Action is unclaimed (there is no NQE that started executing the operator.
            //0 : Action is monitored and it is ok
            //1 : Action has unknown status
            //2 : Action owner did not respond
            //3 : Action has failed either because of timed out or failed
            monitoredActions.put(i, new ConcurrentHashMap<String, Action>());
        }

        com = new DefaultNode();
        com.initialize(id, deployerMonitor, null, this, null, vertx);
        log = new LogProxy(config.getString("log"), com);
        periodicHandler =
            new PeriodicCheckHandler(id, deployerLogic, nqeGroup, log, com, actionToLevelMap,
                                        monitoredActions);
        timerID = vertx.setPeriodic(period, periodicHandler);

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
        if(type == null && type.equals("")){
           log.error(msg.toString());
           return;
        }
        if (type.equals("action")) {
            Action action = new Action(msg);
            String label = action.getLabel();
            Action newAction = null;
            //         action.setStatus(ActionStatus.INPROCESS.toString());

            switch (ActionStatus.valueOf(action.getStatus())) {
                case PENDING: //probably received an action from an external source
                    if (label.equals(NQEConstants.DEPLOY_OPERATOR)) {
                        action.getData().putString("replyTo", msg.getString("from"));
                        actionToLevelMap.put(action.getId(), -1);
                        monitoredActions.get(-1).put(action.getId(), action);

                    } else if (label.equals(NQEConstants.OPERATOR_COMPLETE)) {
                        log.error("Received Completed OPERATOR Action " + action.toString()
                                      + " with status PENDING");
                    } else if (label.equals(NQEConstants.OPERATOR_OWNER)) {
                        log.error("Received OWNER OPERATOR Action " + action.toString()
                                      + " with status PENDING");
                    } else if (label.equals(NQEConstants.OPERATOR_RUNNING_STATUS)) {
                        log.error("Received RUNNING STATUS OPERATOR Action " + action.toString()
                                      + " with status PENDING");
                    } else {
                        log.error("Unknown PENDING Action received " + action.toString());
                        return;
                    }
//                    action.setStatus(ActionStatus.INPROCESS.toString());
                    if (newAction != null) {
                        action.addChildAction(newAction.getId());
                        logAction(newAction);
                    }
                    logAction(action);
                    break;
                case INPROCESS:
                    if (label.equals(NQEConstants.DEPLOY_OPERATOR)) {
                        log.error("Received OPERATOR STARTED Action " + action.toString()
                                      + " with status INPROCESS");
                        return;
                    } else if (label.equals(NQEConstants.OPERATOR_COMPLETE)) {
                        log.error("Received Completed OPERATOR Action " + action.toString()
                                      + " with status INPROCESS");
                        return;
                    } else if (label.equals(NQEConstants.OPERATOR_OWNER)) {
                        Action claimedAction = monitoredActions.get(-1).remove(action.getId());
                        if (claimedAction == null) {
                            Integer currentLevel = actionToLevelMap.get(action.getId());
                            log.error("Received OPERATOR OWNER but action " + action.toString()
                                          + " was not in unclaimed level but in level "
                                          + currentLevel.toString());
                        }
                        monitoredActions.get(1).put(action.getId(), action);
                        actionToLevelMap.put(action.getId(), 1);

                    } else if (label.equals(NQEConstants.OPERATOR_RUNNING_STATUS)) {
                        Integer currentLevel = actionToLevelMap.get(action.getId());
                        if (currentLevel == null) {
                            log.error("Received OPERATOR STATUS but action " + action.toString()
                                          + " is not monitored " + currentLevel.toString());
                            return;
                        }
                        Action runningAction =
                            monitoredActions.get(currentLevel).remove(action.getId());
                        monitoredActions.get(0).put(action.getId(), action);
                        actionToLevelMap.put(action.getId(), 0);

                    } else {
                        log.error("Unknown INPROCESS Action received " + action.toString());
                        return;
                    }
                    logAction(action);
                    break;
                case COMPLETED: // the action either a part of a multistep workflow (INPROCESSING) or it could be processed.
                    if (label.equals(NQEConstants.DEPLOY_OPERATOR)) {
                       completeOperator(action);
                       log.error("Received OPERATOR STARTED Action " + action.toString()
                                      + " with status COMPLETED");
                        return;
                    } else if (label.equals(NQEConstants.OPERATOR_COMPLETE)) {
                        completeOperator(action);
                    } else if (label.equals(NQEConstants.OPERATOR_OWNER)) {
                        log.error("Received OPERATOR OWNER but action " + action.toString()
                                      + " with status COMPLETED");
                        return;
                    } else if (label.equals(NQEConstants.OPERATOR_RUNNING_STATUS)) {
                        log.error("Received OPERATOR STATUS but action " + action.toString()
                                      + " with status COMPLETED");
                        return;
                    } else {
                        log.error("Unknown INPROCESS Action received " + action.toString());
                        return;
                    }
                    logAction(action);

                    finalizeAction(action);
            }
        }
    }

    private void completeOperator(Action action) {

        Integer currentLevel = actionToLevelMap.get(action.getId());
        if (currentLevel == null) {
            log.error("Action " + action.toString() + " was not monitored by DeployerMonitor" + id);
            return;
        }
        Action completedAction = monitoredActions.get(currentLevel).remove(action.getId());
        if (completedAction == null) {
            log.error("Action " + action.toString() + " was not in level " + currentLevel.toString()
                          + " of DeployerMonitor" + id);
            checkAllLevelsForAndRemove(action);
            return;
        } else {
            com.sendTo(deployerLogic, action.asJsonObject());
            return;
        }
    }

    private void checkAllLevelsForAndRemove(Action action) {

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
        result.setData(new JsonObject());
        result.setResult(new JsonObject());
        result.setLabel("");
        result.setCategory("");
        return result;
    }
}
