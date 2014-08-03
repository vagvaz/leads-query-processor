package eu.leads.processor.core;

import eu.leads.processor.core.comp.ServiceStatus;
import eu.leads.processor.core.net.MessageUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/28/14.
 */
public class LogManageVerticle extends ManageVerticle {
    String logVertcileId = null;
    String logAddress = null;
    JsonObject logConfig = null;
    protected final String serviceType = "logService";
    boolean shouldStop = false;

   @Override
    public void start() {
        super.start();
        initialize(container.config());
    }

    @Override
    public void startService() {
        super.startService();
        container.deployVerticle("eu.leads.processor.core.LogVerticle",logConfig,new Handler<AsyncResult<String>>(){

            @Override
            public void handle(AsyncResult<String> asyncResult) {
                if(asyncResult.succeeded()){
                    container.logger().info("Log Vertice has been deployed ID " + asyncResult.result());
                    logVertcileId = asyncResult.result();
                    com.sendTo(group, MessageUtils.createServiceStatusMessage(ServiceStatus.RUNNING,id,serviceType));

                }
                else{
                    container.logger().fatal("Log Verticle failed to deploy");
                    fail("Log Verticle failed to deploy");
                }
            }
        });


    }

    @Override
    public void initialize(JsonObject config) {
        super.initialize(config);
        logAddress = id;//".log";
        JsonObject logConfig = new JsonObject();
        logConfig.putString("id",logAddress);
        com.sendTo(group, MessageUtils.createServiceStatusMessage(ServiceStatus.INITIALIZED,id,serviceType));
    }

    @Override
    public void cleanup() {
        super.cleanup();
        logConfig = null;
    }

    @Override
    public void stopService() {
        super.stopService();
        container.undeployVerticle(logVertcileId,new Handler<AsyncResult<Void>>(){

            @Override
            public void handle(AsyncResult<Void> asyncResult) {
                if(asyncResult.succeeded()){
                    container.logger().info("Log Vertice has been deployed ID " + asyncResult.result());
                    com.sendTo(group, MessageUtils.createServiceStatusMessage(ServiceStatus.STOPPED,id,serviceType));
                    if(shouldStop)
                        stop();

                }
                else{
                    container.logger().fatal("Log Verticle failed to undeploy");
                    fail("Log Verticle failed to undeploy");
                }
            }
        });
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
    public String getServiceType() {
        return serviceType;
    }

    @Override
    public void exitService() {
        shouldStop = true;
        stopService();
    }

    @Override
    public void fail(String message) {
        super.fail(message);
        JsonObject failMessage =  MessageUtils.createServiceStatusMessage(ServiceStatus.FAILED,id,serviceType);
        failMessage.putString("message",message);
        com.sendTo(group, failMessage);

    }
}
