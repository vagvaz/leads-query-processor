package eu.leads.processor.planner;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.comp.ComponentControlVerticle;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Iterator;

/**
 * Created by vagvaz on 8/18/14.
 */
public class PlannerControlVerticle extends ComponentControlVerticle {
    protected final String componentType = "planner";
    private String plannerQueue;
    private String deployerManageQueue;

    @Override
    public void start() {
        plannerQueue = StringConstants.PLANNERQUEUE;
        deployerManageQueue = StringConstants.DEPLOYERQUEUE;
        super.start();
        setup(container.config());
        startUp();
    }

    @Override
    public void setup(JsonObject conf) {
        super.setup(conf);
        logicConfig.putString("planner", plannerQueue);
        logicConfig.putString("deployer", deployerManageQueue);
        processorConfig.putString("planner", plannerQueue);
        logicConfig.putString("deployer", deployerManageQueue);
      JsonArray servicesArray = conf.getArray("services");
      Iterator<Object> servicesIterator = servicesArray.iterator();
      while(servicesIterator.hasNext())
      {
        JsonObject service = (JsonObject) servicesIterator.next();
        if(service.getString("type").equals("catalog")){
          String ip = service.getObject("conf").getString("ip");
          int port = service.getObject("conf").getInteger("port");
          processorConfig.putString("catalog_ip",ip);
          processorConfig.putNumber("catalog_port",port);
        }
      }
    }

    @Override
    public void startUp() {
        super.startUp();
    }

    @Override
    public void stopComponent() {
        super.stopComponent();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void undeployAllModules() {
        super.undeployAllModules();
    }

    @Override
    public void reset(JsonObject conf) {
        super.reset(conf);
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void kill() {
        super.kill();
    }

    @Override
    public String getComponentType() {
        return componentType;
    }
}
