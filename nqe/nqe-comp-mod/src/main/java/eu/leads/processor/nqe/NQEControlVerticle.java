package eu.leads.processor.nqe;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.comp.ComponentControlVerticle;
import org.vertx.java.core.json.JsonObject;

/**
 * edited by Krambo on 29/8/14.
 */
public class NQEControlVerticle extends ComponentControlVerticle {

   protected final String componentType = "nqe";
   private String plannerQueue;
   private String iManagerQueue;

   @Override
   public void start() {
      plannerQueue = StringConstants.PLANNERQUEUE;
      iManagerQueue = StringConstants.IMANAGERQUEUE;
      super.start();
      setup(container.config());
      startUp();
   }

   @Override
   public void setup(JsonObject conf) {
      super.setup(conf);
      this.logicConfig.putString("planner",plannerQueue);
      this.logicConfig.putString("imanager",iManagerQueue);
      this.processorConfig.putString("planner",plannerQueue);
      this.logicConfig.putString("imanager",iManagerQueue);
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
