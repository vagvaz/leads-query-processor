package eu.leads.processor.nqe;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.infinispan.PluginHandlerListener;
import eu.leads.processor.common.plugins.PluginManager;
import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.common.utils.storage.LeadsStorage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.plugins.EventType;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.SerializationUtils;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vagvaz on 9/23/14.
 */
public class DeployPluginActionHandler implements ActionHandler {
  private final Node com;
  private final LogProxy log;
  private final InfinispanManager persistence;
  private final String id;
  private Map<String,PluginHandlerListener> activeListeners;
  private Map<String,String> informEndpoints;
  private BasicCache ownersPlugins;
  private BasicCache activePlugins;
  private BasicCache pluginRepository;
  private LeadsStorage storage;

   public DeployPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence, String id) {
     this.com = com;
     this.log = log;
     this.persistence = persistence;
     this.id = id;
     ownersPlugins = (BasicCache) persistence.getPersisentCache(StringConstants.OWNERSCACHE);
     activePlugins = (BasicCache) persistence.getPersisentCache(StringConstants.PLUGIN_ACTIVE_CACHE);
     pluginRepository = (BasicCache) persistence.getPersisentCache(StringConstants.PLUGIN_CACHE);
     Properties conf = new Properties();
     conf.setProperty("prefix","/tmp/leads/");
     storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.LOCAL,conf);
   }

   @Override
   public Action process(Action action) {
     Action result = action;
     JsonObject reply = new JsonObject();
     if(action.getLabel().equals(NQEConstants.DEPLOY_PLUGIN)){
       JsonObject deployPlugin = action.getData();
       String user = deployPlugin.getString("user");
       String pluginId = deployPlugin.getString("pluginid");
       String targetCache = deployPlugin.getString("cachename");
       XMLConfiguration config = null;
       if(deployPlugin.containsField("config")) {
         config = (XMLConfiguration) SerializationUtils.deserialize(deployPlugin.getBinary("config"));
       }
       List<EventType> eventList= new ArrayList<>(3);
       for (int index = 0; index < deployPlugin.getArray("events").size(); index++) {
         eventList.add((EventType) deployPlugin.getArray("events").get(index));
       }
       EventType[] events = (EventType[]) eventList.toArray();
       //CHeck plugin if exists
       PluginPackage plugin = (PluginPackage) pluginRepository.get(pluginId);
       if(plugin == null){
         result.setStatus(ActionStatus.FAILED.toString());
         reply.putString("status","FAIL");
         reply.putString("message","Could load plugin from plugin repository");
         result.setResult(reply);
         return result;
       }
       activePlugins.put(activePlugins.getName()+":"+plugin.getId()+plugin.getUser(),plugin);
       PluginHandlerListener listener = PluginManager.deployPluginListener(pluginId,targetCache,user,
                                                                            persistence,storage);
       reply.putString("status","SUCCESS");
       reply.putString("message","");
       action.setResult(reply);
       ownersPlugins.put(plugin.getId() + plugin.getUser(), id);

     }
     else if(action.getLabel().equals(NQEConstants.UNDEPLOY_PLUGIN)){
       JsonObject undeployPlugin = action.getData();
       String cacheName = undeployPlugin.getString("cachename");
       String pluginId = undeployPlugin.getString("pluginid");
       String username = undeployPlugin.getString("user");
       PluginManager plugin =
         (PluginManager) activePlugins.get(cacheName+":"+pluginId+username);
       if(plugin == null){
         result.setStatus(ActionStatus.FAILED.toString());
         reply.putString("status","FAIL");
         reply.putString("message","plugin " + pluginId + " for "  + cacheName);
         result.setResult(reply);
         return result;
       }
       PluginHandlerListener listener = activeListeners.get(cacheName+":"+pluginId+username);
       persistence.removeListener(listener, (Cache) persistence.getPersisentCache(cacheName));
       reply.putString("status","SUCCESS");
       reply.putString("message","");
       action.setResult(reply);
     }
     else{

     }
//     result.getData().putString("owner",id);
//     Action ownerAction = new Action(result.asJsonObject().copy());
//     ownerAction.setLabel(NQEConstants.OPERATOR_OWNER);
//     ownerAction.setStatus(ActionStatus.INPROCESS.toString());
//     com.sendTo(action.getData().getString("monitor"),ownerAction.asJsonObject());
//     Operator operator = OperatorFactory.createOperator(com, persistence, log, result);
//     if(operator != null) {
//       operator.init(result.getData());
//       operator.execute();
//     }
//     else{
//       log.error("Could not get a valid operator to execute so operator FAILED");
//       ownerAction.setLabel(NQEConstants.OPERATOR_FAILED);
//       com.sendTo(action.getData().getString("monitor"),ownerAction.asJsonObject());
//     }
     return result;

   }
}
