package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.plugins.PluginManager;
import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.apache.commons.configuration.XMLConfiguration;
import org.infinispan.commons.api.BasicCache;
import org.vertx.java.core.json.JsonObject;

import java.util.Properties;

/**
 * Created by vagvaz on 8/6/14.
 */
public class DeployPluginActionHandler implements ActionHandler {
  private final Node com;
  private final LogProxy log;
  private final InfinispanManager persistence;
  private final String id;
  private BasicCache pluginsCache;

  public DeployPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                    String id, JsonObject global) {
    this.com = com;
    this.log = log;
    this.persistence = persistence;
    this.id = id;
    pluginsCache = (BasicCache) persistence.getPersisentCache(StringConstants.PLUGIN_CACHE);
    Properties storageConf = new Properties();
    storageConf.setProperty("prefix", "/tmp/leads/");
    if(global!=null){

      if(global.containsField("hdfs.uri") && global.containsField("hdfs.prefix") && global.containsField("hdfs.user"))
      {
        storageConf.setProperty("hdfs.url", global.getString("hdfs.uri"));
        storageConf.setProperty("fs.defaultFS", global.getString("hdfs.uri"));
        storageConf.setProperty("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        storageConf.setProperty("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        storageConf.setProperty("prefix", global.getString("hdfs.prefix"));
        storageConf.setProperty("hdfs.user", global.getString("hdfs.user"));
        storageConf.setProperty("postfix", "0");
        System.out.println("USING HDFS yeah!");
        log.info("using hdfs: " + global.getString("hdfs.user")+ " @ "+ global.getString("hdfs.uri") + global.getString("hdfs.prefix") );

        PluginManager.initialize(LeadsStorageFactory.HDFS,storageConf);
      }else
        PluginManager.initialize(LeadsStorageFactory.LOCAL,storageConf);
    }else
      PluginManager.initialize(LeadsStorageFactory.LOCAL,storageConf);

  }

  @Override
  public Action process(Action action) {

      System.out.println("New deploy plugin action arrived ");

      Action result = action;
      JsonObject actionResult = new JsonObject();
      actionResult.putString("status","UNKNOWN");
      actionResult.putString("message","");
      result.setResult(actionResult);
    try{
      JsonObject deployPlugin = action.getData();
      String user = deployPlugin.getString("user");
      String pluginId = deployPlugin.getString("pluginid");
      String targetCache = deployPlugin.getString("cachename");
      XMLConfiguration config = null;
//      if(deployPlugin.containsField("config")) {
//        config = (XMLConfiguration) SerializationUtils.deserialize(deployPlugin.getBinary("config"));
//      }
//      List<EventType> eventList= new ArrayList<>(3);
//      for (int index = 0; index < deployPlugin.getArray("events").size(); index++) {
//        String eventString = deployPlugin.getArray("events").get(index);
//        eventList.add(EventType.valueOf(eventString));
//      }
//      EventType[] events = (EventType[]) eventList.toArray();
      //CHeck plugin if exists
      PluginPackage plugin = (PluginPackage) pluginsCache.get(pluginId);
      if(plugin == null) {
        actionResult.putString("status","FAIL");
        actionResult.putString("message","Could not find plugin in the Repository");
        result.setResult(actionResult);
        return result;
      }

      // if (PluginManager.deployPlugin(q.getString("pluginid"), config, q.getString("cachename"), ev)) {
      result.setStatus(ActionStatus.COMPLETED.toString());
      actionResult.putString("status","SUCCESS");
      result.setResult(actionResult);

    } catch (Exception e) {

      actionResult.putString("error", "");
      actionResult.putString("message",
                              "Failed to add query " + action.getData().toString() + "\n"
                                + " to the plugin cache");
      result.setResult(actionResult);
    }

    System.out.println(" result " + result.toString());
    return result;
  }

}

