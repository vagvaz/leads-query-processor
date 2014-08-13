package eu.leads.processor.core;

/**
 * Created by vagvaz on 7/28/14.
 */

import eu.leads.processor.core.comp.ServiceStatus;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;


public class PersistenceManageVerticle extends ManageVerticle {
   protected final String serviceType = "persistenceService";
   static String persistVertcileId = null;
   static String persistAddress = null;
   static JsonObject persistConfig = null;
   boolean shouldStop = false;


   @Override
   public void start() {
      super.start();
//        initialize(config.getObject("conf"));
      initialize(container.config());
   }

   @Override
   public void startService() {
      super.startService();
      if(persistVertcileId == null) {
         persistVertcileId = "";

         getContainer().deployWorkerVerticle("eu.leads.processor.core.PersistenceVerticle", getContainer().config(), 1, false, new Handler<AsyncResult<String>>() {



            @Override
            public void handle(AsyncResult<String> asyncResult) {
               if (asyncResult.succeeded()) {
                  getContainer().logger().info("Persist Vertice has been deployed ID " + asyncResult.result());
                  persistVertcileId = asyncResult.result();
                  com.sendTo(parent, MessageUtils.createServiceStatusMessage(ServiceStatus.RUNNING, id, serviceType));

               } else {
                  getContainer().logger().fatal("Persist Verticle failed to deploy");
                  fail("Persist Verticle failed to deploy");
               }
            }
         });
      }

   }

   @Override
   public void initialize(JsonObject config) {
      super.initialize(config);
      persistAddress = id;
      JsonObject persistConfig = new JsonObject();
      persistConfig.putString("id", persistAddress);
      persistConfig.putString("log", config.getString("log"));
      com.sendTo(parent, MessageUtils.createServiceStatusMessage(ServiceStatus.INITIALIZED, id, serviceType));
   }

   @Override
   public void cleanup() {
      super.cleanup();
      persistConfig = null;
   }

   @Override
   public void stopService() {
      super.stopService();
      container.undeployVerticle(persistVertcileId, new Handler<AsyncResult<Void>>() {

         @Override
         public void handle(AsyncResult<Void> asyncResult) {
            if (asyncResult.succeeded()) {
               container.logger().info("Persist Vertice has been deployed ID " + asyncResult.result());
               persistVertcileId = null;
               com.sendTo(parent, MessageUtils.createServiceStatusMessage(ServiceStatus.STOPPED, id, serviceType));
               if (shouldStop)
                  stop();

            } else {
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
      JsonObject failMessage = MessageUtils.createServiceStatusMessage(ServiceStatus.FAILED, id, serviceType);
      failMessage.putString("message", message);
      com.sendTo(parent, failMessage);
   }
}
