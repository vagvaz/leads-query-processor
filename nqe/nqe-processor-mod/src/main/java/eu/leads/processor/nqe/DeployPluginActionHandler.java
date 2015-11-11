package eu.leads.processor.nqe;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.infinispan.PluginHandlerListener;
import eu.leads.processor.common.plugins.PluginManager;
import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.common.utils.storage.LeadsStorage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.conf.ConfigurationUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.WebUtils;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.plugins.EventType;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.SerializationUtils;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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
  private EnsembleCache pluginRepository;
  private LeadsStorage storage;
  private JsonObject globalConfig;
  private EnsembleCacheManager emanager;
  private String localEnsembleString;
  private String globalEnsembleString;
  List<RemoteCacheManager> remoteCacheManagers;

  public DeployPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence,
       String id, JsonObject globalConfig) {
     this.com = com;
     this.log = log;
     this.persistence = persistence;
     this.id = id;
//     ownersPlugins = (BasicCache) persistence.getPersisentCache(StringConstants.OWNERSCACHE);
//     activePlugins = (BasicCache) persistence.getPersisentCache(StringConstants.PLUGIN_ACTIVE_CACHE);
//     pluginRepository = (BasicCache) persistence.getPersisentCache(StringConstants.PLUGIN_CACHE);
     activeListeners = new ConcurrentHashMap<>();
     Properties storageConf = new Properties();
     storageConf.setProperty("prefix","/tmp/leads/");
     if(globalConfig!=null){
       if(globalConfig.containsField("hdfs.uri") && globalConfig.containsField("hdfs.prefix") && globalConfig.containsField("hdfs.user"))
       {
         storageConf.setProperty("hdfs.url", globalConfig.getString("hdfs.uri"));
         storageConf.setProperty("fs.defaultFS", globalConfig.getString("hdfs.uri"));
         storageConf.setProperty("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
         storageConf.setProperty("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
         storageConf.setProperty("prefix", globalConfig.getString("hdfs.prefix"));
         storageConf.setProperty("hdfs.user", globalConfig.getString("hdfs.user"));
         storageConf.setProperty("postfix", "0");
         System.out.println("USING HDFS yeah!");
         log.info("using hdfs: " + globalConfig.getString("hdfs.user")+ " @ "+ globalConfig.getString("hdfs.uri") + globalConfig.getString("hdfs.prefix") );
         storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.HDFS,storageConf);
       }else {
         log.info("No defined all hdfs parameters using local storage ");
         storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.LOCAL, storageConf);
       }
     }else {
       storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.LOCAL, storageConf);
     }



     String ensembleHost = ConfigurationUtilities.getEnsembleString(globalConfig);
    emanager = new EnsembleCacheManager(ensembleHost);
     ownersPlugins = emanager.getCache(StringConstants.OWNERSCACHE,new ArrayList<>(emanager.sites()),
         EnsembleCacheManager.Consistency.DIST);
     activePlugins = emanager.getCache(StringConstants.PLUGIN_ACTIVE_CACHE,new ArrayList<>(emanager.sites()),
         EnsembleCacheManager.Consistency.DIST);
     pluginRepository = emanager.getCache(StringConstants.PLUGIN_CACHE,new ArrayList<>(emanager.sites()),
         EnsembleCacheManager.Consistency.DIST);

    globalEnsembleString = WebUtils.computeEnsemleString(globalConfig);
    localEnsembleString = WebUtils.computeEnsembleString(globalConfig, LQPConfiguration.getInstance().getMicroClusterName());
    remoteCacheManagers = new ArrayList<>();
    for(String mc : globalConfig.getObject("microclouds").getFieldNames()){
      String ip = globalConfig.getObject("microclouds").getArray(mc).get(0);
      if(ip.contains(":")){
        ip = ip.split(":")[0];
      }
      remoteCacheManagers.add(createRemoteCacheManager(ip,11222));
    }
   }

  private  RemoteCacheManager createRemoteCacheManager(String ip, int port) {
    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.addServer().host(ip).port(port);
    return new RemoteCacheManager(builder.build());
  }
   @Override
   public Action process(Action action) {
     Action result = action;
     JsonObject reply = new JsonObject();
     if(action.getLabel().equals(NQEConstants.DEPLOY_PLUGIN)){
       JsonObject deployPlugin = action.getData();
       final String user = deployPlugin.getString("user");
       final String pluginId = deployPlugin.getString("pluginid");
       final String targetCache = deployPlugin.getString("cachename");
       XMLConfiguration config = null;
       if(deployPlugin.containsField("config")) {
         config = (XMLConfiguration) SerializationUtils.deserialize(deployPlugin.getBinary("config"));
       }
       List<EventType> eventList= new ArrayList<>(3);
       for (int index = 0; index < deployPlugin.getArray("events").size(); index++) {
         eventList.add(EventType.valueOf(deployPlugin.getArray("events").get(index).toString()));
       }
       final EventType[] events =  new EventType[eventList.size()];
       int counter = 0;
       for(EventType event : eventList){
         events[counter++] = event;
       }
       //CHeck plugin if exists

       PluginPackage plugin = (PluginPackage) EnsembleCacheUtils.getFromCache(pluginRepository,pluginId);
       //plugin.setConfig(config.toString().getBytes());
       if(plugin == null){
         result.setStatus(ActionStatus.FAILED.toString());
         reply.putString("status","FAIL");
         reply.putString("message","Could load plugin from plugin repository");
         result.setResult(reply);
         return result;
       }

       plugin.setUser(user);
       activePlugins.put(targetCache+":"+plugin.getId()+plugin.getUser(),plugin);
       final PluginHandlerListener[] listener = {null};
       Thread t = new Thread(new Runnable() {
         @Override public void run() {
           try {
             listener[0] = PluginManager
                 .deployPluginListenerWithEvents(pluginId, targetCache, user, events, persistence,
                     storage,localEnsembleString,globalEnsembleString,remoteCacheManagers);
             activeListeners.put(targetCache + ":" + pluginId + user, listener[0]);
           }
           catch(Exception e){
             System.err.println("Trying to deploy pawels plugin...");
             e.printStackTrace();
           }
         }
       });
    t.start();
//       PluginHandlerListener listener = PluginManager.deployPluginListenerWithEvents(pluginId,targetCache,
//           user,events,
//           emanager,storage);

       reply.putString("status","SUCCESS");
       reply.putString("message", "");
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
//       persistence.removeListener(listener, (Cache) persistence.getPersisentCache(cacheName));
       for(RemoteCacheManager remoteCacheManager : remoteCacheManagers){
         RemoteCache cache = remoteCacheManager.getCache(cacheName);
         cache.removeClientListener(listener);
       }
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
     System.err.println("DEPLOYYYEEEEEEEEEEEEEEDDDDDD ACTJION RETURN!");
     return result;

   }
}
