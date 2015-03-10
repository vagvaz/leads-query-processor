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
import eu.leads.processor.plugins.EventType;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.SerializationUtils;
import org.infinispan.commons.api.BasicCache;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
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
                                    String id) {
    this.com = com;
    this.log = log;
    this.persistence = persistence;
    this.id = id;
    pluginsCache = (BasicCache) persistence.getPersisentCache(StringConstants.PLUGIN_CACHE);
    Properties storageConf = new Properties();
    storageConf.setProperty("prefix", "/tmp/leads-tmp/");
    PluginManager.initialize(LeadsStorageFactory.LOCAL,storageConf);
  }

  @Override
  public Action process(Action action) {

      System.out.println("New deploy plugin action arrived: " + action.toString());
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
      if(deployPlugin.containsField("config")) {
        config = (XMLConfiguration) SerializationUtils.deserialize(deployPlugin.getBinary("config"));
      }
      List<EventType> eventList= new ArrayList<>(3);
      for (int index = 0; index < deployPlugin.getArray("events").size(); index++) {
        eventList.add((EventType) deployPlugin.getArray("events").get(index));
      }
      EventType[] events = (EventType[]) eventList.toArray();
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

