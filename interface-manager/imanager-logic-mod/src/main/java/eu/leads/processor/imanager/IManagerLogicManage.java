package eu.leads.processor.imanager;

import eu.leads.processor.core.ManageVerticle;
import eu.leads.processor.core.net.MessageUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/3/14.
 */
public class IManagerLogicManage extends ManageVerticle {
    protected final String serviceType = "imanager-logic";
    protected String plannerAddress;
    protected String imanagerAddress;
    protected String workerId;
    protected JsonObject workerConfig;

    @Override
    public void start() {
        super.start();
        initialize(config);
    }

    @Override
    public void initialize(JsonObject config) {
        super.initialize(config);
        plannerAddress = config.getString("planner");
        imanagerAddress = config.getString("imanager");
        workerConfig = new JsonObject();
        workerConfig.putString("planner", plannerAddress);
        workerConfig.putString("imanager", imanagerAddress);
        workerConfig.putString("log", config.getString("log"));
        workerConfig.putString("persistence", config.getString("persistence"));
        workerConfig.putString("id", id);
        workerConfig.putString("workqueue", config.getString("workqueue"));
        com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));

    }

    @Override
    public void startService() {
        super.startService();
        if (workerId == null) {
            workerId = "";
            container
                .deployWorkerVerticle(IManagerLogicWorker.class.getCanonicalName(), workerConfig, 1,
                                         false, new Handler<AsyncResult<String>>() {
                    @Override
                    public void handle(AsyncResult<String> event) {
                        if (event.succeeded()) {
                            workerId = event.result();
                            logProxy.info("IManagerLogicWorker has been deployed.");
                            JsonObject statusMessage = MessageUtils
                                                           .createServiceStatusMessage(status, id
                                                                                                   + ".manage",
                                                                                          serviceType);
                            com.sendTo(parent, statusMessage);
                        } else {
                            String msg = "IManagerLogWorker could not be deployed";

                            fail(msg);
                        }
                    }
                });
            //         com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void stopService() {
        super.stopService();
        if (workerId != null) {
            container.undeployModule(workerId);
            workerId = null;
        }
        com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));

    }

    @Override
    public void fail(String message) {
        super.fail(message);
        JsonObject msg = MessageUtils.createServiceStatusMessage(status, id, serviceType);
        msg.putString("message", message);
        com.sendTo(parent, msg);
    }

    @Override
    public String getServiceType() {
        return serviceType;
    }

    @Override
    public void exitService() {
        System.exit(-1);
    }


}
