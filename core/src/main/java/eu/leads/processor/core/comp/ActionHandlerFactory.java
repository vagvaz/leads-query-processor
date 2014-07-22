package eu.leads.processor.core.comp;

import com.google.common.base.Strings;

/**
 * Created by vagvaz on 7/13/14.
 */
public class ActionHandlerFactory {

  public ActionHandlerFactory() {
  }

  public static LeadsMessageHandler getActionHandler(String componentType) {
    LeadsMessageHandler result = null;
    if( !Strings.isNullOrEmpty(componentType)){
      if(componentType.equals(DefaultComponent.class.getCanonicalName())){
        result = new DefaultActionHandler();
      }
    }
    return result;
  }
}
