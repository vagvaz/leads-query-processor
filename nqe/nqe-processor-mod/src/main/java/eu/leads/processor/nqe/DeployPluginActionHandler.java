package eu.leads.processor.nqe;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.infinispan.PluginHandlerListener;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.operators.Operator;
import eu.leads.processor.nqe.handlers.OperatorFactory;
import org.infinispan.commons.api.BasicCache;

import java.util.Map;

/**
 * Created by vagvaz on 9/23/14.
 */
public class DeployPluginActionHandler implements ActionHandler {
  private final Node com;
  private final LogProxy log;
  private final InfinispanManager persistence;
  private final String id;
  private Map<String,PluginHandlerListener> activeListeners;
  private BasicCache ownersPlugins;
  private BasicCache activePlugins;
  private BasicCache pluginRepository;

   public DeployPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence, String id) {
     this.com = com;
     this.log = log;
     this.persistence = persistence;
     this.id = id;
     ownersPlugins = (BasicCache) persistence.getPersisentCache(StringConstants.OWNERSCACHE);
     activePlugins = (BasicCache) persistence.getPersisentCache(StringConstants.PLUGIN_ACTIVE_CACHE);
     pluginRepository = (BasicCache) persistence.getPersisentCache(StringConstants.PLUGIN_CACHE);
   }

   @Override
   public Action process(Action action) {
     Action result = action;
     result.getData().putString("owner",id);
     Action ownerAction = new Action(result.asJsonObject().copy());
     ownerAction.setLabel(NQEConstants.OPERATOR_OWNER);
     ownerAction.setStatus(ActionStatus.INPROCESS.toString());
     com.sendTo(action.getData().getString("monitor"),ownerAction.asJsonObject());
     Operator operator = OperatorFactory.createOperator(com, persistence, log, result);
     if(operator != null) {
       operator.init(result.getData());
       operator.execute();
     }
     else{
       log.error("Could not get a valid operator to execute so operator FAILED");
       ownerAction.setLabel(NQEConstants.OPERATOR_FAILED);
       com.sendTo(action.getData().getString("monitor"),ownerAction.asJsonObject());
     }

     return result;

   }
}
