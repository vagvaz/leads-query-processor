package eu.leads.processor.nqe;

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.nqe.handlers.OperatorActionHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashMap;
import java.util.Map;

import static eu.leads.processor.core.ActionStatus.INPROCESS;
import static eu.leads.processor.core.ActionStatus.valueOf;

/**
 * Created by vagvaz on 8/6/14.
 */
public class NQEProcessorWorker extends Verticle implements Handler<Message<JsonObject>> {
   Node com;
   String id;
   String gr;
   String workqueue;
   String logic;
   JsonObject config;
   EventBus bus;
   LeadsMessageHandler leadsHandler;
   LogProxy log;
   InfinispanManager persistence;
   Map<String,ActionHandler> handlers;
   Map<String,Action> activeActions;
   @Override
   public void start() {
      super.start();
      activeActions = new HashMap<String,Action>();
      leadsHandler = new LeadsMessageHandler() {
         @Override
         public void handle(JsonObject event) {
            if (event.getString("type").equals("unregister")) {
               JsonObject msg = new JsonObject();
               msg.putString("processor", id + ".process");
               com.sendWithEventBus(workqueue + ".unregister", msg);
               stop();
            }
            else if(event.getString("type").equals("action")){
               Action action = new Action(event);
               switch(valueOf(action.getStatus())){
                  case COMPLETED:
                     if(action.getLabel().equals(NQEConstants.DEPLOY_OPERATOR)){
                       log.info("Operator: " + action.getData().getString("operatorType") + " is completed");
                       com.sendTo(action.getData().getString("monitor"),action.asJsonObject());
                       activeActions.remove(action.getId());
                     }
                     else{
                        log.error("COMPLETED Action " + action.toString() + "Received by NQEProcessor but cannot be handled" );
                     }
                     break;
                  case PENDING:
                     if(action.getLabel().equals(NQEConstants.OPERATOR_GET_RUNNING_STATUS)){
                        Action runningAction = new Action(action.asJsonObject().copy());
                        runningAction.setLabel(NQEConstants.OPERATOR_RUNNING_STATUS);
                        com.sendTo(action.getData().getString("replyTo"),runningAction.asJsonObject());
                     }
                     else if(action.getLabel().equals(NQEConstants.OPERATOR_GET_OWNER)){
                        Action runningAction = new Action(action.asJsonObject().copy());
                        runningAction.setLabel(NQEConstants.OPERATOR_OWNER);
                        runningAction.getData().putString("owner",com.getId());
                        runningAction.setStatus(INPROCESS.toString());
                        com.sendTo(action.getData().getString("replyTo"),runningAction.asJsonObject());
                     }
                     else{
                        log.error("PENDING Action " + action.toString() + "Received by NQEProcessor but cannot be handled" );
                     }
                     break;
                  case INPROCESS:
                     log.error("INPROCESS Action " + action.toString() + "Received by NQEProcessor but cannot be handled" );
                     break;
                  case FAILED:
                     if(action.getLabel().equals(NQEConstants.DEPLOY_OPERATOR)){
                        log.info("Operator: " + action.getData().getString("operatorType") + " failed");
                        com.sendTo(logic,action.asJsonObject());
                        activeActions.remove(action.getId());
                     }
                     else{
                        log.error("FAILED Action " + action.toString() + "Received by NQEProcessor but cannot be handled" );
                     }
                     break;
                  default:
                     break;

               }
            }

         }
      };
      bus = vertx.eventBus();
      config = container.config();
      id = config.getString("id");
      gr = config.getString("group");
      logic = config.getString("logic");
      workqueue = config.getString("workqueue");
      com = new DefaultNode();
      com.initialize(id, gr, null, leadsHandler, leadsHandler, vertx);
      bus.registerHandler(id + ".process", this);
      LQPConfiguration.initialize();
      persistence = InfinispanClusterSingleton.getInstance().getManager();
      JsonObject msg = new JsonObject();
      msg.putString("processor", id + ".process");
      handlers = new HashMap<String,ActionHandler>();
      handlers.put(NQEConstants.DEPLOY_OPERATOR,new OperatorActionHandler(com,log,persistence,id));
      handlers.put(NQEConstants.DEPLOY_PLUGIN,new DeployPluginActionHandler(com,log,persistence,id));
      handlers.put(NQEConstants.UNDEPLOY_PLUGIN,new UnDeployPluginActionHandler(com,log,persistence,id));
      log = new LogProxy(config.getString("log"),com);

      bus.send(workqueue + ".register", msg, new Handler<Message<JsonObject>>() {
         @Override
         public void handle(Message<JsonObject> event) {
            log.info("Registration " + event.toString());
         }
      });

     log.info("started ....");
   }

   @Override
   public void handle(Message<JsonObject> message) {
      try {
         JsonObject body = message.body();
         if (body.containsField("type")) {
            if (body.getString("type").equals("action")) {
               Action action = new Action(body);
               ActionHandler ac = handlers.get(action.getLabel());
               Action result = ac.process(action);
//               result.setStatus(ActionStatus.COMPLETED.toString());
//               com.sendTo(logic,result.asJsonObject());
               message.reply();
            }
         } else {
            log.error(id + " received message from eventbus that does not contain type field  \n" + message.toString());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
