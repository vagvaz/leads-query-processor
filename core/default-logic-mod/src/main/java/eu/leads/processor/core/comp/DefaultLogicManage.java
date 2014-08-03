package eu.leads.processor.core.comp;

import eu.leads.processor.core.ManageVerticle;
import eu.leads.processor.core.net.MessageUtils;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/31/14.
 */
public class DefaultLogicManage extends ManageVerticle implements LeadsMessageHandler {
   protected final String serviceType = "default-logic";
   protected String listenGroup;
   protected String publishGroup;
   protected String componentType;
   protected String workQueue;

   @Override
   public void start() {
      super.start();
      this.initialize(container.config());
   }

   @Override
   public void initialize(JsonObject config) {
      super.initialize(config);
      listenGroup = config.getString("listen");
      publishGroup = config.getString("publish");
      workQueue = config.getString("workqueue");
      componentType = config.getString("componentType");
      com.sendTo(parent, MessageUtils.createServiceStatusMessage(status,id,serviceType));

   }

   @Override
   public void startService() {
      super.startService();
      com.subscribe(componentType,this);
      com.subscribe(listenGroup,this);
      com.sendTo(parent,MessageUtils.createServiceStatusMessage(status,id,serviceType));
      if(config.containsField("start")){
         JsonObject msg = new JsonObject();
         msg.putString("type","pingpong");
         msg.putString("count",Integer.toString(1));
         com.sendWithEventBus(publishGroup, msg);
      }
   }

   @Override
   public void cleanup() {
      super.cleanup();
   }

   @Override
   public void stopService() {
      super.stopService();
      com.sendTo(parent,MessageUtils.createServiceStatusMessage(status,id,serviceType));
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

   @Override
   public void handle(JsonObject msg) {
      if(msg.getString("type").equals("pingpong")){
         long i = Long.parseLong(msg.getString("count"));
         logProxy.info("Received pingpong msg from " + msg.getString("from") + msg.toString());
         msg.putString("replyTo",publishGroup);
         com.sendWithEventBus(workQueue,msg);
      }
   }
}
