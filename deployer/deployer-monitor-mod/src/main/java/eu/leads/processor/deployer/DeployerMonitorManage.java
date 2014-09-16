package eu.leads.processor.deployer;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.ManageVerticle;
import eu.leads.processor.core.net.MessageUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * Created by vagvaz on 8/27/14.
 */
public class DeployerMonitorManage extends ManageVerticle {
   protected final String serviceType = "planner-logic";
   protected String monitorAddress;
   protected String deployerAddress;
   protected String nqeAddress;


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
      monitorAddress = config.getString("monitor");
      deployerAddress = config.getString("deployer");
      nqeAddress = config.getString("nqe");
      workerConfig = new JsonObject();
      workerConfig.putString("nqe",nqeAddress);
      workerConfig.putString("monitor",monitorAddress);
      workerConfig.putString("deployer", deployerAddress);
      workerConfig.putString("log",config.getString("log"));
      workerConfig.putString("persistence",config.getString("persistence"));
      workerConfig.putString("id",id);
      workerConfig.putString("workqueue",config.getString("workqueue"));
      com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));
   }

   @Override
   public void startService() {
      super.startService();
      if(workerId == null) {
         workerId = "";
         container.deployWorkerVerticle(DeployerMonitorWorker.class.getCanonicalName(), workerConfig, 1, false, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> event) {
               if (event.succeeded()) {
                  workerId = event.result();
                  logProxy.info("DeployerMonitorWorker has been deployed.");
                  JsonObject statusMessage = MessageUtils.createServiceStatusMessage(status, id + ".manage", serviceType);
                  com.sendTo(parent, statusMessage);
               } else {
                  String msg = "DeployerMonitorWorker could not be deployed";
                  fail(msg);
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
      if(workerId != null){
         container.undeployModule(workerId);
         workerId = null;
      }
      com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));

   }

   @Override
   public void fail(String message) {
      super.fail(message);
      JsonObject msg =  MessageUtils.createServiceStatusMessage(status,id,serviceType);
      msg.putString("message",message);
      com.sendTo(parent,msg);
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
