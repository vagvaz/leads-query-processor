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
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.Properties;

/**
 * Created by vagvaz on 8/6/14.
 */
public class RegisterPluginActionHandler implements ActionHandler {
  private final Node com;
  private final LogProxy log;
  private final InfinispanManager persistence;
  private final String id;
  private Cache<String,String> queriesCache;

  public RegisterPluginActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                      String id) {
    this.com = com;
    this.log = log;
    this.persistence = persistence;
    this.id = id;
    queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
    Properties storageConf = new Properties();
    storageConf.setProperty("prefix", "/tmp/leads-tmp/");
    PluginManager.initialize(LeadsStorageFactory.LOCAL,storageConf);
  }
  @Override
  public Action process(Action action) {
    System.out.println("s New action arrived: " + action.toString());
    Action result = action;
    JsonObject actionResult = new JsonObject();
    actionResult.putString("message","");
    actionResult.putString("status","PENDING");
    result.setResult(actionResult);
    try {
      JsonObject pluginInfo =  action.getData();
      String user = pluginInfo.getString("user");
      String pluginId = pluginInfo.getString("pluginid");
      String pluginClass = pluginInfo.getString("pluginclass");
      byte[] configbytes = pluginInfo.getBinary("config");
      String jarPath = pluginInfo.getString("jar");
      PluginPackage newPlugin = new PluginPackage(pluginId,pluginClass);
      newPlugin.setJarFilename(jarPath);
      newPlugin.setConfig(configbytes);



      if (PluginManager.uploadInternalPlugin(newPlugin)) {
        result.setStatus(ActionStatus.COMPLETED.toString());
        actionResult.putString("status","SUCCESS");
      }
      else {
        actionResult.putString("status","FAIL");
        actionResult.putString("message","could not upload plugin");
        result.setStatus(ActionStatus.FAILED.toString());
        result.setResult(actionResult);
      }

      result.setResult(actionResult);

    } catch (Exception e) {

      actionResult.putString("error", "");
      actionResult.putString("message",
                              "Failed to add query " + action.getData().toString() + "\n"
                                + " to the plugin cache");
      result.setResult(actionResult);
    }

    //        System.out.println(" result " + result.toString());
    return result;
  }

}


