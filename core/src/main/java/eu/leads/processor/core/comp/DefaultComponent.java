package eu.leads.processor.core.comp;

import com.google.common.base.Strings;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.LogUtils;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vagvaz on 7/13/14.
 */
public class DefaultComponent extends Verticle implements Component {

  String id;
  String mainGroup;
  Set<String> secondaryGroups;
  final String componentType = DefaultComponent.class.getCanonicalName();
  String workQueueId;
  String persistenceId;
  String logId;

  String workQueueAddress;
  String persisentAddress;
  String completeAddress;
  String logAddress;
  PersistentStateProxy state;
  Set<String> processorAddress;
  LeadsMessageHandler actionHandler;
  LeadsMessageHandler failHandler;
  LeadsMessageHandler controlHandler;
  LogUtils logUtil;
  Node com;

  public DefaultComponent() {
  }

  /**
   * Getter for property 'componentType'.
   *
   * @return Value for property 'componentType'.
   */
  public String getComponentType() {
    return componentType;
  }

  @Override
  public void start() {
    super.start();
    id = container.config().getString("id");
    mainGroup = container.config().getString("group");
    if( Strings.isNullOrEmpty(id))
    {
      container.logger().fatal("Configuration does not have an id field", new Exception("id field in Configuration is undefined"));
    }
    if( Strings.isNullOrEmpty(mainGroup))
    {
      container.logger().fatal("Configuration does not have an group field", new Exception("group field in Configuration is undefined"));
    }
    workQueueAddress = id +".workqueue";
    completeAddress  = id +".completed";
    persisentAddress = id +".state";
    logAddress = id+".log";
    logUtil = new LogUtils(vertx.eventBus(),logId);
    secondaryGroups = new HashSet<String>();
    if(container.config().containsField("groups")){
      JsonArray array = container.config().getArray("groups");
      for(Object ob : array){
        String group = (String)ob;
        secondaryGroups.add(group);
      }
    }

    actionHandler = ActionHandlerFactory.getActionHandler(getComponentType());
    failHandler =  new DefaultFailHandler();
    controlHandler = new DefaultControlHandler(this);
    com = new DefaultNode();
    processorAddress = new HashSet<String>();
    if(!setup())
      container.logger().fatal("Could not setup component " + getId() + " type: " + getComponentType());
  }

  @Override
  public boolean setup() {
    boolean result = true;
    try {

      //Initialize communication node
      com.initialize(id, mainGroup, secondaryGroups, actionHandler, failHandler, vertx);

      //Add default control groups that the component MUST listen
      secondaryGroups.add("leads.processor.control");
      secondaryGroups.add("leads.processor.control." + mainGroup);
      secondaryGroups.add("leads.processor.control."+id);

      //Subscribe component to the control groups, controlHandler is responsible for processing those commands.
      com.subscribe("leads.processor.control",controlHandler);
      com.subscribe("leads.processor.control."+mainGroup,controlHandler);
      com.subscribe("leads.processor.control."+id,controlHandler);

      //Start log Verticle
      JsonObject logConfig = new JsonObject();
      logConfig.putString("id",logAddress);
      container.deployVerticle("eu.leads.processor.core.LogVerticle",logConfig,new Handler<AsyncResult<String>>(){

        @Override
        public void handle(AsyncResult<String> asyncResult) {
          if(asyncResult.succeeded()){
            container.logger().info("Log Vertice has been deployed ID " + asyncResult.result());
            logId = asyncResult.result();
          }
          else{
            container.logger().fatal("Log Verticle failed to deploy");
          }
        }
      });
      logConfig = null;

      //Start persistent state verticle. The implementation might be hazelcast or infinispan.
      String persistence = container.config().getString("persistance","hazelcast");
      JsonObject persistConfig = new JsonObject();
      persistConfig.putString("id",persisentAddress);
      persistConfig.putString("type",persistence);
      persistConfig.putString("log",logAddress);
      container.deployWorkerVerticle ("eu.leads.processor.core.PersistenceVerticle",persistConfig,1,false,new Handler<AsyncResult<String>>(){

        @Override
        public void handle(AsyncResult<String> asyncResult) {
          if(asyncResult.succeeded()){
            container.logger().info("Persistence Vertice has been deployed ID " + asyncResult.result());
            persistenceId = asyncResult.result();
          }
          else{
            container.logger().fatal("Persistence Verticle failed to deploy");
          }
        }
      });
      persistConfig = null;


      //Deploy workqueue module
      JsonObject workQueueConfig = new JsonObject();
      workQueueConfig.putString("address",this.workQueueAddress);
      //The default action timeout for all the component is 5 minutes 5 * 60 sec(=1000ms)
      workQueueConfig.putNumber("process_timeout", container.config().getInteger("action_timeout", 5 * 60 * 1000));
      container.deployModule(StringConstants.WORKQUEUE_MOD_NAME,workQueueConfig,new Handler<AsyncResult<String>>(){

        @Override
        public void handle(AsyncResult<String> asyncResult) {
          if(asyncResult.succeeded()){
            container.logger().info("WorkerQueue Module has been deployed ID " + asyncResult.result());
            workQueueId = asyncResult.result();
          }
          else{
            container.logger().fatal("WorkerQueue Module failed to deploy");
          }
        }
      });
      workQueueConfig = null;
      //Deploy processor
      //Create Default processor configuration
      JsonObject processorConfig = new JsonObject();
      processorConfig.putString("register",workQueueAddress);
      processorConfig.putString("completed",completeAddress);
      processorConfig.putString("log",logAddress);
      processorConfig.putString("persistence",persisentAddress);
      String processorPrefix = id+".processor.";
      int numOfProcessors = container.config().getInteger("processors_number",3);
      for(int proc = 0; proc < numOfProcessors; proc++){
        processorConfig.putString("id",processorPrefix+proc);
        container.deployWorkerVerticle(ProcessorFactory.getProcessorClassName(getComponentType()),processorConfig,1,false,new Handler<AsyncResult<String>>(){

          @Override
          public void handle(AsyncResult<String> result) {
            if(result.succeeded()){
              container.logger().info("Processor  Verticle has been deployed ID " + result.result());
              processorAddress.add(result.result());
            }
            else{
              container.logger().fatal("Processor Verticle failed to deploy");
            }
          }
        });
      }
      processorConfig = null;
    }catch(Exception e){
      result = false;
      container.logger().fatal(e.getMessage());
    }
    finally{
      return result;
    }

  }

  @Override
  public boolean startExecution() {
    return setup();
  }

  @Override
  public boolean stopExecution() {
    return cleanup();
  }

  @Override
  public boolean cleanup() {
    boolean result = true;
    try{

    }catch(Exception e){
      result = false;
    }
    finally{
      return result;
    }
  }

  @Override
  public void kill() {
    logUtil.info("Component " + this.toString() + " is going to be killed... ");
    System.exit(-1);
  }

  @Override
  public void processAction(JsonObject message) {
    actionHandler.handle(message);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }
}
