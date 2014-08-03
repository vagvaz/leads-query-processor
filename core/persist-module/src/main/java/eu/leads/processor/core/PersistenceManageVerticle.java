package eu.leads.processor.core;

/**
 * Created by vagvaz on 7/28/14.
 */
import eu.leads.processor.core.comp.ServiceStatus;
import eu.leads.processor.core.net.MessageUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;


public class PersistenceManageVerticle extends ManageVerticle {
    String persistVertcileId = null;
    String persistAddress = null;
    JsonObject persistConfig = null;
    boolean shouldStop = false;
    protected final String serviceType = "persistenceService";

    @Override
    public void start() {
        super.start();
//        initialize(config.getObject("conf"));
        initialize(container.config());
    }

    @Override
    public void startService() {
        super.startService();
        container.deployWorkerVerticle("eu.leads.processor.core.PersistenceVerticle",persistConfig,1,false,new Handler<AsyncResult<String>>(){

            @Override
            public void handle(AsyncResult<String> asyncResult) {
                if(asyncResult.succeeded()){
                    container.logger().info("Persist Vertice has been deployed ID " + asyncResult.result());
                    persistVertcileId = asyncResult.result();
                    com.sendTo(group, MessageUtils.createServiceStatusMessage(ServiceStatus.RUNNING,id,serviceType));

                }
                else{
                    container.logger().fatal("Persist Verticle failed to deploy");
                    fail("Persist Verticle failed to deploy");
                }
            }
        });


    }

    @Override
    public void initialize(JsonObject config) {
        super.initialize(config);
        persistAddress = id;
        JsonObject persistConfig = new JsonObject();
        persistConfig.putString("id",persistAddress);
        persistConfig.putString("log",config.getString("log"));
        com.sendTo(group, MessageUtils.createServiceStatusMessage(ServiceStatus.INITIALIZED,id,serviceType));
    }

    @Override
    public void cleanup() {
        super.cleanup();
        persistConfig = null;
    }

    @Override
    public void stopService() {
        super.stopService();
        container.undeployVerticle(persistVertcileId,new Handler<AsyncResult<Void>>(){

            @Override
            public void handle(AsyncResult<Void> asyncResult) {
                if(asyncResult.succeeded()){
                    container.logger().info("Persist Vertice has been deployed ID " + asyncResult.result());
                    com.sendTo(group, MessageUtils.createServiceStatusMessage(ServiceStatus.STOPPED,id,serviceType));
                    if(shouldStop)
                        stop();
                }
                else{
                    container.logger().fatal("Persist Verticle failed to undeploy");
                    fail("Persist Verticle failed to undeploy");
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
