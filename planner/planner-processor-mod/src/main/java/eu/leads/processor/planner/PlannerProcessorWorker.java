package eu.leads.processor.planner;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.EnsembleInfinispanManager;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.ConfigurationUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.imanager.IManagerConstants;
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
    private Node com;
    private String id;
    private String gr;
    private String workqueue;
    private String logic;
    private String schedHost;
    private String schedPort;
    private JsonObject config;
    private EventBus bus;
    private LeadsMessageHandler leadsHandler;
//    private LogProxy log;
    private InfinispanManager persistence;
    private Map<String, ActionHandler> handlers;
    private TaJoModule module;
    private JsonObject globalConfig;
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
       LQPConfiguration.getInstance().getConfiguration().setProperty("node.current.component",
           "planner");
      globalConfig = config.getObject("global");
      String publicIP = ConfigurationUtilities
          .getPublicIPFromGlobal(LQPConfiguration.getInstance().getMicroClusterName(), globalConfig);
      LQPConfiguration.getInstance().getConfiguration().setProperty(StringConstants.PUBLIC_IP,
          publicIP);
//        persistence = InfinispanClusterSingleton.getInstance().getManager();

       String schedulerUri = config.getObject("global").getString("scheduler");
       schedHost = schedulerUri.substring(0,schedulerUri.lastIndexOf(":"));
       schedPort = schedulerUri.substring(schedulerUri.lastIndexOf(":")+1);

        JsonObject msg = new JsonObject();
        msg.putString("processor", id + ".process");
        handlers = new HashMap<String, ActionHandler>();

//        log = new LogProxy(config.getString("log"), com);
        bus.send(workqueue + ".register", msg, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
               System.out.println(id + " Registration " + event.address().toString());
            }
        });

      System.out.println(id + " started....");
    }

    public void initialize(){
        persistence = new EnsembleInfinispanManager();
        persistence.startManager(LQPConfiguration.getInstance().getConfiguration().getString("node.ip")+":11222");
        handlers.put(QueryPlannerConstants.PROCESS_SQL_QUERY,
            new ProcessSQLQueryActionHandler(com, null, persistence, id, module,schedHost,schedPort,config.getObject("global")));
        handlers.put(QueryPlannerConstants.PROCESS_WORKFLOW_QUERY,
            new ProcessWorkflowQueryActionHandler(com, null, persistence, id, module,schedHost,schedPort,config.getObject("global")));
        handlers.put(QueryPlannerConstants.PROCESS_SPECIAL_QUERY,
            new ProcessSpecialQueryActionHandler(com, null, persistence, id, module,schedHost,
                schedPort,config.getObject("global")));
    }
    @Override
    public void handle(Message<JsonObject> message) {
        if(persistence == null){
            initialize();
        }
        try {
            JsonObject body = message.body();
            if (body.containsField("type")) {
                if (body.getString("type").equals("action")) {
                    Action action = new Action(body);
                    if(!action.getLabel().equals(IManagerConstants.QUIT)) {
                        ActionHandler ac = handlers.get(action.getLabel());
                        Action result = ac.process(action);
                        result.setStatus(ActionStatus.COMPLETED.toString());
                        com.sendTo(logic, result.asJsonObject());
                        message.reply();
                    }else{
                        //System.out.println(" Quit Planner ");

                        //persistence.stopManager();
                        System.out.println("Planner Processor ");
//                        log.error("Stopped Manager Exiting");
                        vertx.setTimer(100, new Handler<Long>() {
                            @Override
                            public void handle(Long aLong) {
                                System.out.println(" planner Processor Exiting ");
                                vertx.stop();
                                //System.exit(0);
                            }
                        });
                    }
                }
            } else {
                System.err.println(id
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
