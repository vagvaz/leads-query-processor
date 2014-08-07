package eu.leads.processor.core.comp;

import eu.leads.processor.core.ManageVerticle;
import eu.leads.processor.core.net.MessageUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/31/14.
 */
public class DefaultProcessorManage extends ManageVerticle {
   protected final String serviceType = "default-processor";
   protected String workerId;

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
      container.deployWorkerVerticle("eu.leads.processor.core.comp.DefaultWorkerProcessor", config, 1, false, new Handler<AsyncResult<String>>() {
         @Override
         public void handle(AsyncResult<String> event) {
            if (event.succeeded()) {
               workerId = event.result();
               setStatus(ServiceStatus.RUNNING);

            } else {
               logProxy.error("Deploying Default Worker verticle failed. ");
               setStatus(ServiceStatus.FAILED);
            }
            com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));
         }
      });
//      com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));
   }

   @Override
   public void cleanup() {
      super.cleanup();
   }

   @Override
   public void stopService() {
      super.stopService();
      container.undeployVerticle(workerId);
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
   }

   @Override
   public String getServiceType() {
      return serviceType;
   }

   @Override
   public void exitService() {
      stop();
   }
}
