package eu.leads.processor.nqe;

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.nqe.handlers.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashMap;
import java.util.Map;

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

   @Override
   public void start() {
      super.start();
      leadsHandler = new LeadsMessageHandler() {
         @Override
         public void handle(JsonObject event) {
            if (event.getString("type").equals("unregister")) {
               JsonObject msg = new JsonObject();
               msg.putString("processor", id + ".process");
               com.sendWithEventBus(workqueue + ".unregister", msg);
               stop();
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
      handlers.put(NQEConstants.RUN_OPERATOR,new OperatorActionHandler(com,log,persistence,id));
      handlers.put(NQEConstants.DEPLOY_PLUGIN,new DeployPluginActionHandler(com,log,persistence,id));
      handlers.put(NQEConstants.DEPLOY_PLUGIN,new UnDeployPluginActionHandler(com,log,persistence,id));
      log = new LogProxy(config.getString("log"),com);

      bus.send(workqueue + ".register", msg, new Handler<Message<JsonObject>>() {
         @Override
         public void handle(Message<JsonObject> event) {
            log.info("Registration " + event.toString());
         }
      });
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
               result.setStatus(ActionStatus.COMPLETED.toString());
               com.sendTo(logic,result.asJsonObject());
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
