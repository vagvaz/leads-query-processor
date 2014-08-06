package eu.leads.processor.imanager;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.comp.ComponentControlVerticle;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/3/14.
 */
public class IManagerControlComponent extends ComponentControlVerticle {

   protected final String componentType = "imanager";
   private String plannerQueue;
   private String iManagerQueue;

   @Override
   public void start() {
      super.start();
      setup(config);
      plannerQueue = StringConstants.PLANNERQUEUE;
      iManagerQueue = StringConstants.IMANAGERQUEUE;
      startUp();
   }

   @Override
   public void setup(JsonObject conf) {
      super.setup(conf);
      logicConfig.putString("planner",plannerQueue);
      logicConfig.putString("imanager",iManagerQueue);
      processorConfig.putString("planner",plannerQueue);
      logicConfig.putString("imanager",iManagerQueue);
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
