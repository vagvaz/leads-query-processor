package eu.leads.processor.system;

import com.jcraft.jsch.*;
import eu.leads.processor.conf.LQPConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by vagvaz on 8/21/14.
 */
public class LeadsProcessorBootstrapper2 {
    static String[] ips;
    static String[] components;
    static String[] configurationFiles;
    static String filename;
    static Logger logger =
            LoggerFactory.getLogger(LeadsProcessorBootstrapper2.class.getCanonicalName());

    static Map<String, HierarchicalConfiguration> componentsXml;
    static Map<String, Configuration> componentsConf;
    static Map<String, JsonObject> componentsJson;


    public static void main(String[] args) {
        if (checkArguments(args)) {
            LQPConfiguration.initialize(true);
            filename = args[0];
            LQPConfiguration.getInstance().loadFile(filename);
            ips = LQPConfiguration.getInstance().getConfiguration().getStringArray("processor.ips");
            components =
                    LQPConfiguration.getInstance().getConf().getStringArray("processor.components");
            configurationFiles = LQPConfiguration.getInstance().getConf()
                    .getStringArray("processor.configurationFiles");
        }
        //CompositeConfiguration xmlConfiguration  = (CompositeConfiguration) LQPConfiguration.getConf();
        XMLConfiguration xmlConfiguration = (XMLConfiguration) LQPConfiguration.getInstance().getConfigurations().get(filename);


        List<HierarchicalConfiguration> nodes = xmlConfiguration.configurationsAt("processor.component");

        Iterator it = xmlConfiguration.getKeys("processor.component");

        componentsXml = new HashMap<>();
        componentsConf = new HashMap<>();
        componentsJson = new HashMap<>();

        List Components2 = xmlConfiguration.getList("processor.component");
        for (HierarchicalConfiguration c : nodes) {
            ConfigurationNode node = c.getRootNode();
            System.out.println("Name: " + c.getString("name") + " Processors " + c.getString("numberOfProcessors"));
            componentsXml.put(c.getString("name"), c);
            if (c.containsKey("configurationFile")) {
                LQPConfiguration.getInstance().loadFile(c.getString("configurationFile"));
                XMLConfiguration subconf = (XMLConfiguration) LQPConfiguration.getInstance().getConfigurations().get(c.getString("configurationFile"));
                componentsConf.put(c.getString("name"), subconf);
                JsonObject modjson = convertConf2Json(subconf);
                modjson.putString("processors", c.getString("numberOfProcessors"));
                if (c.containsKey("Modname")) {
                    modjson.putString("id", c.getString("Modname") + "-default-uuid");
                    modjson.putString("Modname", c.getString("Modname"));

                }

                componentsJson.put(c.getString("name"), modjson);

                System.out.println("Final ModJson: " + modjson.encodePrettily().toString());
            }

        }


//        for (String configuration : configurationFiles) {
//            LQPConfiguration.getInstance().loadFile(configuration);
//        }
        int ip = 0;
        for(Map.Entry<String,JsonObject> e : componentsJson.entrySet())
        {//for (int component = 0; component < components.length; component++) {
            deployComponent(e.getKey(), ips[ip]);
            ip = (ip + 1) % ips.length;
        }
    }

    private static JsonObject convertConf2Json(XMLConfiguration subconf) {
        //System.out.println(" componentType: " + subconf.getString("componentType"));
        Iterator it = null;
        JsonObject ret = new JsonObject();

        if (subconf.containsKey("componentType")) {
            String componentType = subconf.getString("componentType");
            ret.putString("id", componentType + "-default-uuid");
            ret.putString("group", componentType);
            ret.putString("version", LQPConfiguration.getInstance().getConf().getString("processor.version"));
            ret.putString("groupId", LQPConfiguration.getConf().getString("processor.groupId"));

            ret.putString("componentType", componentType);
            List<HierarchicalConfiguration> servicenodes = subconf.configurationsAt("service");

            String type = subconf.getString("service.type");
            // System.out.println(" Type: " + type);
            List<HierarchicalConfiguration> nodes = subconf.configurationsAt("service.conf");

            // System.out.println("service  size " + servicenodes.size());
            JsonArray otherGroups = null;
            if (subconf.containsKey("otherGroups")) {
                otherGroups = new JsonArray();
                for (String group : subconf.getStringArray("otherGroups")) {
                    //System.out.println("Group: " + group);
                    otherGroups.addString(group);
                }
            }

            JsonObject service = null;
            JsonArray serviceA = new JsonArray();
            if (servicenodes.size() > 0) {
                service = new JsonObject();
                service.putString("type", type);
                JsonObject conf = new JsonObject();

                if (nodes.size() > 0) {
                    it = nodes.get(0).getKeys();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        conf = checkNumberInsert(conf, key, nodes.get(0));
                    }
                }
                conf.putString("id", componentType + ".$id." + type);
                conf.putString("group", "$group");

                JsonArray confa = new JsonArray();
                confa.addObject(conf);
                service.putArray("conf", confa);

                serviceA = new JsonArray();
                serviceA.addObject(service);
            }
            if (otherGroups != null)
                ret.putArray("otherGrouops", otherGroups);

            ret.putArray("services", serviceA);

        } else {
            it = subconf.getKeys();
            while (it.hasNext()) {
                String key = (String) it.next();
                ret = checkNumberInsert(ret, key, subconf);
            }
        }
        //System.out.println("ret Json:" + ret.encodePrettily().toString());

        return ret;
    }

    public static JsonObject checkNumberInsert(JsonObject in, String key, Configuration conf) {
        if (in == null) {
            in = new JsonObject();
        }
        if (conf.containsKey(key))
            try {
                //System.out.println(" key found  " + key + " value Int " + conf.getInt(key));
                in.putNumber(key, conf.getInt(key));
            } catch (ConversionException e) {
                try {
                    //System.out.println(" key found  " + key + " value Double " + conf.getDouble(key));
                    in.putNumber(key, conf.getInt(key));
                } catch (ConversionException e2) {
                    try {
                        //System.out.println(" key found  " + key + " value String " + conf.getString(key));
                        in.putString(key, conf.getString(key));
                    } catch (ConversionException e3) {
                        System.err.print("Cannot parse Value");
                    }
                }
            }
        return in;
    }


    private static void deployComponent(String component, String ip) {
        JsonObject modJson = componentsJson.get(component); //generateConfiguration(component);
        sendConfigurationTo(modJson, ip);
        String prefixCommand =
                LQPConfiguration.getInstance().getConf().getString("processor.ssh.username") + "@" + ip;
        String group = LQPConfiguration.getInstance().getConf().getString("processor.group");
        String version = LQPConfiguration.getInstance().getConf().getString("processor.version");
        //      String command = "vertx runMod " + group +"~"+ component + "-mod~" + version + " -conf /tmp/"+config.getString("id")+".json";
        String basedir = LQPConfiguration.getInstance().getConf().getString("processor.baseDir");
        String vertxComponent = null;
        if (!component.equals("webservice")) {
            vertxComponent = group + "~" + component + "-comp-mod~" + version;
        } else {
            vertxComponent = group + "~" + "processor-webservice~" + version;
        }

//        String command = " 'source ~/.bashrc; java -cp " + basedir
//                + "/lib/component-deployer.jar eu.leads.processor.system.LeadsComponentRunner "
//                + vertxComponent + " " + config.getString("group") + " /tmp/" + config
//                .getString("id")
//                + ".json '";
//        command = "/home/vagvaz/touchafile.sh ";
//        try {
//            ProcessBuilder builder =
//                    new ProcessBuilder("ssh", prefixCommand, command, vertxComponent,
//                            config.getString("group"),
//                            "/tmp/" + config.getString("id") + ".json");
//            Process p = builder.start();
//            //         Process p = Runtime.getRuntime().exec(prefixCommand+" "+command);
//            //         p.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private static void sendConfigurationTo(JsonObject config, String ip) {
        RandomAccessFile file = null;
        String tmpFile = "/tmp/" + config.getString("id") + ".json";
        try {
            file = new RandomAccessFile(tmpFile, "rw");
            file.writeBytes(config.encodePrettily().toString());
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String command = "scp " + tmpFile + " " + LQPConfiguration.getInstance().getConf()
                .getString("processor.ssh.username") + "@"
                + ip + ":" + tmpFile;

        System.out.println("Command Send: " + command);
//        try {
//            Process p = Runtime.getRuntime().exec(command);
//            p.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession("tr",ip,22);
            session.setPassword("12121212");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            ChannelSftp channel = null;
            channel = (ChannelSftp)session.openChannel("sftp");
            channel.connect();
            File localFile = new File(tmpFile);
            //If you want you can change the directory using the following line.
            channel.cd("/tmp/");
            channel.put(new FileInputStream(localFile),localFile.getName());
            channel.disconnect();
            session.disconnect();
            System.out.println("File successfull uploaded: " + localFile );

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }


    private static boolean checkArguments(String[] args) {
        boolean result = false;
        if (args.length == 1) {
            if (args[0].endsWith(".xml") || args[0].endsWith(".properties")) {
                result = true;
            }
        }
        return result;
    }
}
