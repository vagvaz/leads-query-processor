package eu.leads.processor.core.comp;

/**
 * Created by vagvaz on 7/31/14.
 */
public class DefaultControlComponent extends ComponentControlVerticle {
   
   @Override
   public void start() {
      super.start();
      System.out.println(container.config().toString());
      setup(container.config());
      startUp();
   }
}
