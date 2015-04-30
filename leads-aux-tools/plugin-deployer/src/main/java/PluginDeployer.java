import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.plugins.EventType;
import eu.leads.processor.web.WebServiceClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by vagvaz on 4/21/15.
 */
public class PluginDeployer {
  public static void main(String[] args) {
    int chunkSize=0;
    String propertiesFile = "";
    if(args.length == 1) {
      propertiesFile = args[0];
    }
    else{
      System.out.println("Usage program propertiesFile");
      System.exit(-1);
    }
    System.out.println("Using properties file " + propertiesFile);
    LQPConfiguration.initialize();
    LQPConfiguration.getInstance().loadFile(propertiesFile);
    String pluginJar = LQPConfiguration.getInstance().getConfiguration().getString("plugin.jar");
    String pluginId  = LQPConfiguration.getInstance().getConfiguration().getString("plugin.id");
    String pluginConf = LQPConfiguration.getInstance().getConfiguration().getString("plugin.conf");
    String pluginClass = LQPConfiguration.getInstance().getConfiguration().getString("plugin.class");
    List<String> events = LQPConfiguration.getInstance().getConfiguration().getList("plugin.events");
    String cache = LQPConfiguration.getInstance().getConfiguration().getString("plugin.cache");
    String webserviceHost = LQPConfiguration.getInstance().getConfiguration().getString("qe.webservice.host");
    String user = LQPConfiguration.getInstance().getConfiguration().getString("plugin.user");
    boolean upload = LQPConfiguration.getInstance().getConfiguration().getBoolean("plugin.upload");

    boolean undeploy  = false;
    if(LQPConfiguration.getInstance().getConfiguration().containsKey("plugin.undeploy")){
      undeploy = true;
    }

    if(LQPConfiguration.getInstance().getConfiguration().containsKey("upload.chunksize")){
      chunkSize = LQPConfiguration.getInstance().getConfiguration().getInt("upload.chunksize");
    }

    int port = LQPConfiguration.getInstance().getConfiguration().getInt("qe.webservice.port");
    try {
      WebServiceClient.initialize(webserviceHost, port);
      if (WebServiceClient.checkIfOnline()) {
        System.out.println("webservice online");
      } else {
        System.err.println("web service Configuration offline review conf in " + propertiesFile);
        System.exit(-1);
      }
      if (undeploy) {
        System.out.println("plugin.undeploy exists so undeploying " + pluginId + " from " + cache + " for "
                + "user ");
        WebServiceClient.undeployPlugin(user, pluginId, cache);
        System.exit(0);
      }
      PluginPackage plugin = new PluginPackage(pluginId, pluginClass, pluginJar, pluginConf);
      System.out.println("Uploading plugin");
        if (upload) {
          if (chunkSize > 0)
            WebServiceClient.submitPlugin(user, plugin, chunkSize);
          else
            WebServiceClient.submitPlugin(user, plugin);

        EventType[] e = new EventType[events.size()];
        int c = 0;
        for (String event : events) {
          if (event.startsWith("create")) {
            e[c] = EventType.CREATED;

          } else if (event.startsWith("modify")) {
            e[c] = EventType.MODIFIED;
          } else if (event.startsWith("remove")) {
            e[c] = EventType.REMOVED;
          }
          c++;
        }
        if (e.length == 0) {
          e = EventType.ALL;
        }
        WebServiceClient.deployPlugin(user, pluginId, null, cache, e);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
