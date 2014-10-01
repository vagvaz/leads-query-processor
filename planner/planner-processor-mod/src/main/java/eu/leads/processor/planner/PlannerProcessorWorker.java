package eu.leads.processor.planner;

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
import eu.leads.processor.planner.handlers.ProcessSQLQueryActionHandler;
import eu.leads.processor.planner.handlers.ProcessSpecialQueryActionHandler;
import eu.leads.processor.planner.handlers.ProcessWorkflowQueryActionHandler;
import leads.tajo.module.TaJoModule;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vagvaz on 8/18/14.
 */
public class PlannerProcessorWorker extends Verticle implements Handler<Message<JsonObject>> {
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
    Map<String, ActionHandler> handlers;
    TaJoModule module;

    @Override
    public void start() {
        super.start();
        config = container.config();
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
        module = new TaJoModule();
        module.init_connection(config.getString("catalog_ip", "localhost"),
                                  config.getInteger("catalog_port", 5998));



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
        handlers = new HashMap<String, ActionHandler>();
        handlers.put(QueryPlannerConstants.PROCESS_SQL_QUERY,
                        new ProcessSQLQueryActionHandler(com, log, persistence, id, module));
        handlers.put(QueryPlannerConstants.PROCESS_WORKFLOW_QUERY,
                        new ProcessWorkflowQueryActionHandler(com, log, persistence, id, module));
        handlers.put(QueryPlannerConstants.PROCESS_SPECIAL_QUERY,
                        new ProcessSpecialQueryActionHandler(com, log, persistence, id, module));
        log = new LogProxy(config.getString("log"), com);
        bus.send(workqueue + ".register", msg, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                log.info("Registration " + event.toString());
            }
        });

      log.info(" started....");
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
                    com.sendTo(logic, result.asJsonObject());
                    message.reply();
                }
            } else {
                log.error(id
                              + " received message from eventbus that does not contain type field  \n"
                              + message.toString());
            }
        } catch (Exception e) {
          JsonObject msg =  message.body();
          msg.putString("status",ActionStatus.FAILED.toString());
           com.sendTo(logic,msg);
            e.printStackTrace();
        }
    }
}
