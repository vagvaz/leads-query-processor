package eu.leads.processor.deployer;

import eu.leads.processor.core.ManageVerticle;
import eu.leads.processor.core.comp.ServiceStatus;
import eu.leads.processor.core.net.MessageUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/27/14.
 */
public class DeployerProcessorManage extends ManageVerticle {
    final String serviceType = "planner-processor";
    String workerId = null;

    @Override
    public void start() {
        super.start();
        initialize(config);
    }

    @Override
    public void initialize(JsonObject config) {
        super.initialize(config);
        com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));
    }

    @Override
    public void startService() {
        super.startService();
        if (workerId == null) {
            workerId = "";
            container
                .deployWorkerVerticle(DeployerProcessorWorker.class.getCanonicalName(), config, 1,
                                         false, new Handler<AsyncResult<String>>() {
                    @Override
                    public void handle(AsyncResult<String> event) {
                        if (event.succeeded()) {
                            workerId = event.result();
                            logProxy.info("DeployerProcessorWorker " + config.getString("id")
                                              + " has been deployed");
                            com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id,
                                                                                          serviceType));
                        } else {
                            logProxy.info("DeployerProcessorWorker " + config.getString("id")
                                              + " failed to deploy");
                            stopService();
                        }
                    }
                });
        }

    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void stopService() {
        super.stopService();
        if (workerId != null && !workerId.equals(""))
            container.undeployModule(workerId);
        workerId = null;
        com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));

    }

    @Override
    public ServiceStatus getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(ServiceStatus status) {
        super.setStatus(status);
    }

    @Override
    public String getServiceId() {
        return super.getServiceId();
    }

    @Override
    public void fail(String message) {
        super.fail(message);

        com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType)
                               .putString("message", message));

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
