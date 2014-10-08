package eu.leads.processor.deployer;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.nqe.NQEConstants;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by vagvaz on 9/16/14.
 */
public class PeriodicCheckHandler implements Handler<Long> {
    ConcurrentMap<String, Integer> actionToLevelMap;
    ConcurrentMap<Integer, Map<String, Action>> monitoredActions;
    String ownerId;
    String nqeGroup;
    LogProxy log;
    Node com;
    String deployerId;

    public PeriodicCheckHandler(String ownerId, String deployerId, String nqeGroup, LogProxy log,
                                   Node com,
                                   ConcurrentMap<String, Integer> actionToLevelMap,
                                   ConcurrentMap<Integer, Map<String, Action>> monitoredActions) {
        this.actionToLevelMap = actionToLevelMap;
        this.monitoredActions = monitoredActions;
        this.ownerId = ownerId;
        this.nqeGroup = nqeGroup;
        this.log = log;
        this.com = com;
        this.deployerId = deployerId;
    }

    @Override
    public void handle(Long event) {
        log.info("Running periodic Monitor check for DeployerMonitor " + ownerId);
        handleLevel3();
        handleLevel2();
        handleLevel1();
        handleLevel0();
        handleLevel_1();
    }

    private void handleLevel_1() {
        Map<String, Action> actions = monitoredActions.get(-1);
        Map<Action,Integer> tobeMoved = new HashMap<Action,Integer>();
        for (Map.Entry<String, Action> action : actions.entrySet()) {
            Action requestOwner = createNewAction(action.getValue());
            requestOwner.setLabel(NQEConstants.OPERATOR_GET_OWNER);
            requestOwner.getData().putString("replyTo",com.getId());
            com.sendToAllGroup(nqeGroup, requestOwner.asJsonObject());
            tobeMoved.put(action.getValue(), 1);
        }
        for(Map.Entry<Action,Integer> entry : tobeMoved.entrySet()){
            moveActionToLevel(entry.getKey(),entry.getValue());
        }
        tobeMoved.clear();
        actions.clear();
    }

    private void moveActionToLevel(Action action, int level) {
        Integer currentLevel = actionToLevelMap.remove(action.getId());
        Action oldAction = monitoredActions.get(currentLevel).remove(action.getId());
        actionToLevelMap.put(action.getId(), level);
        monitoredActions.get(level).put(action.getId(), action);

    }

    private void handleLevel0() {
        Map<String, Action> actions = monitoredActions.get(0);
        Map<Action,Integer> tobeMoved = new HashMap<Action,Integer>();
        for (Map.Entry<String, Action> action : actions.entrySet()) {
            moveActionToLevel(action.getValue(), 0);
        }
        for(Map.Entry<Action,Integer> entry : tobeMoved.entrySet()){
            moveActionToLevel(entry.getKey(),entry.getValue());
        }
        tobeMoved.clear();
        actions.clear();
    }

    private void handleLevel1() {
        Map<String, Action> actions = monitoredActions.get(1);
        HashMap<Action,Integer> tobeMoved = new HashMap<>();
        for (Map.Entry<String, Action> action : actions.entrySet()) {
            Action timedOutAction = action.getValue();
            String owner = timedOutAction.getData().getString("owner");
            Action requestStatus = createNewAction(timedOutAction);
            requestStatus.setLabel(NQEConstants.OPERATOR_GET_RUNNING_STATUS);
            requestStatus.getData().putString("replyTo",com.getId());
            com.sendTo(owner, requestStatus.asJsonObject());
            tobeMoved.put(action.getValue(), 2);
        }
        for(Map.Entry<Action,Integer> entry : tobeMoved.entrySet()){
            moveActionToLevel(entry.getKey(),entry.getValue());
        }
        tobeMoved.clear();
        actions.clear();
    }

    private void handleLevel2() {
        Map<String, Action> actions = monitoredActions.get(2);
        Map<Action,Integer> tobeMoved = new HashMap<Action,Integer>();
        for (Map.Entry<String, Action> action : actions.entrySet()) {
            Action timedOutAction = action.getValue();
            String owner = timedOutAction.getData().getString("owner");
            Action requestStatus = createNewAction(timedOutAction);
            requestStatus.setLabel(NQEConstants.OPERATOR_GET_RUNNING_STATUS);
           requestStatus.getData().putString("replyTo",com.getId());
            com.sendTo(owner, requestStatus.asJsonObject());
            tobeMoved.put(action.getValue(), 3);
        }
        for(Map.Entry<Action,Integer> entry : tobeMoved.entrySet()){
            moveActionToLevel(entry.getKey(),entry.getValue());
        }
        tobeMoved.clear();
        actions.clear();
    }

    private void handleLevel3() {
        Map<String, Action> actions = monitoredActions.get(3);
        for (Map.Entry<String, Action> action : actions.entrySet()) {
            Action failedAction = action.getValue();
            String owner = failedAction.getData().getString("owner");
            Action failed = createNewAction(failedAction);
            failedAction.setLabel(NQEConstants.OPERATOR_FAILED);
            failed.getData().putObject("failedAction", failedAction.asJsonObject());
            com.sendTo(deployerId, failedAction.asJsonObject());
        }
        monitoredActions.get(3).clear();
    }

    private Action createNewAction(Action action) {
        Action result = new Action();
        result.setId(UUID.randomUUID().toString());
        result.setTriggered(action.getId());
        result.setComponentType("deployerMonitor");
        result.setStatus(ActionStatus.PENDING.toString());
        result.setTriggers(new JsonArray());
        result.setOwnerId(this.ownerId);
        result.setProcessedBy("");
        result.setDestination("");
        result.setData(new JsonObject());
        result.setResult(new JsonObject());
        result.setLabel("");
        result.setCategory("");
        return result;
    }
}
