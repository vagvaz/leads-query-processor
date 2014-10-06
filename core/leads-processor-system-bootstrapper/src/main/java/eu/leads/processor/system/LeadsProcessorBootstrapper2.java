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
import java.util.*;

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


    public static void main(String[] args){
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
        XMLConfiguration xmlConfiguration = (XMLConfiguration) LQPConfiguration.getInstance().getConfigurations().get(filename);

        List<HierarchicalConfiguration> nodes = xmlConfiguration.configurationsAt("processor.component");

        Iterator it = xmlConfiguration.getKeys("processor.component");

        componentsXml = new HashMap<>();
        componentsConf = new HashMap<>();
        componentsJson = new HashMap<>();

        List Components2 = xmlConfiguration.getList("processor.component");
        for (HierarchicalConfiguration c : nodes) {
            ConfigurationNode node = c.getRootNode();
            logger.info("Loading configuration, Name: " + c.getString("name") + " Processors " + c.getString("numberOfProcessors"));
            componentsXml.put(c.getString("name"), c);
            if (c.containsKey("configurationFile")) {
                LQPConfiguration.getInstance().loadFile(c.getString("configurationFile"));
                XMLConfiguration subconf = (XMLConfiguration) LQPConfiguration.getInstance().getConfigurations().get(c.getString("configurationFile"));
                componentsConf.put(c.getString("name"), subconf);
                JsonObject modjson = convertConf2Json(subconf);
                modjson.putString("processors", c.getString("numberOfProcessors"));
                if (c.containsKey("modName")) {
                    modjson.putString("id", c.getString("modName") + "-default-" + UUID.randomUUID().toString());
                    modjson.putString("modName", c.getString("modName"));

                }
                componentsJson.put(c.getString("name"), modjson);
            }
        }
        //runRemotely("test","localhost","vertx");
        int ip = 0;

        for (Map.Entry<String, JsonObject> e : componentsJson.entrySet()) {
            //JSch jsch = new JSch();
           // Session session = createSession( jsch,  ips[ip] );
            deployComponent(e.getKey(), ips[ip]);
            ip = (ip + 1) % ips.length;
            //break;

        }
    }

    private static JsonObject convertConf2Json(XMLConfiguration subconf) {
        //System.out.println(" componentType: " + subconf.getString("componentType"));
        Iterator it = null;
        JsonObject ret = new JsonObject();

        if (subconf.containsKey("componentType")) {
            String componentType = subconf.getString("componentType");
            ret.putString("id", componentType + "-default-" + UUID.randomUUID().toString());
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

                //JsonArray confa = new JsonArray();
                //confa.addObject(conf);
                service.putObject("conf", conf);

                serviceA = new JsonArray();
                serviceA.addObject(service);
            }
            if (otherGroups != null)
                ret.putArray("otherGroups", otherGroups);

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
        if (in == null)
            in = new JsonObject();

        if (conf.containsKey(key))
            try {
                //System.out.println(" key found  " + key + " value Int " + conf.getInt(key));
                in.putNumber(key, conf.getInt(key));
            } catch (ConversionException eInt) {
                try {
                    //System.out.println(" key found  " + key + " value Double " + conf.getDouble(key));
                    in.putNumber(key, conf.getDouble(key));
                } catch (ConversionException eDouble) {
                    try {
                        //System.out.println(" key found  " + key + " value String " + conf.getString(key));
                        in.putBoolean(key, conf.getBoolean(key));
                    } catch (ConversionException eBoolean) {
                        try {
                            //System.out.println(" key found  " + key + " value String " + conf.getString(key));
                            in.putString(key, conf.getString(key));
                        } catch (ConversionException eString) {
                            System.err.print("Cannot parse Value");
                        }
                    }
                }
            }
        return in;
    }


    private static void deployComponent(String component, String ip) {
        JsonObject modJson = componentsJson.get(component); //generateConfiguration(component);

        sendConfigurationTo(modJson, ip);
        Configuration c = LQPConfiguration.getInstance().getConf();

        String group = c.getString("processor.groupId");
        String version = c.getString("processor.version");
        //      String command = "vertx runMod " + group +"~"+ component + "-mod~" + version + " -conf /tmp/"+config.getString("id")+".json";
        String remotedir = c.getString("processor.ssh.remoteDir");
        String vertxComponent = null;
        if (modJson.containsField("modName"))
            vertxComponent = group + "~" + modJson.getString("modName") + "~" + version;
        else
            vertxComponent = group + "~" + component + "-comp-mod~" + version;

        String command = "vertx runMod " + vertxComponent + " -conf " + remotedir + "R" + modJson.getString("id") + ".json";
        if (c.containsKey("processor.vertxArg"))
            command += " -" + c.getString("processor.vertxArg");

        //System.out.println(command);

        runRemotely(modJson.getString("id"), ip, command);

    }


    public static void runRemotely(String id, String ip, String command) {
        String command0 = "screen -AmdS shell_" + id + " bash -l";
        // run top within that bash session
        String command1 = command0 + " && " + "screen -S shell_" + id + " -p 0 -X stuff $\"" + command + "\\r\"";//ping 147.27.18.1";
        //System.out.print("Cmd" + command1);
        logger.info("Execution command: " + command1);
        //command1 =command;
        try {
            JSch jsch = new JSch();

            Session session = createSession( jsch,  ip );
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    logger.info("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();
            logger.info("Remote execution DONE");
            //if(channel.getExitStatus()==-1)


        } catch (Exception e) {
            logger.error("Remote execution error: " + e.getMessage());

        }

    }


    private static boolean sendConfigurationTo(JsonObject config, String ip) {
        RandomAccessFile file = null;
        String remoteDir = LQPConfiguration.getInstance().getConf().getString("processor.ssh.remoteDir");
        String tmpFile = remoteDir + config.getString("id") + ".json";
        //System.out.println("Write " + tmpFile +" Final ModJson: " + config.encodePrettily().toString());
        try {
            file = new RandomAccessFile(tmpFile, "rw");
            file.writeBytes(config.encodePrettily().toString());
            file.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("FileNotFoundException error: " + e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException error: " + e.getMessage());
            return false;
        }

        String username = LQPConfiguration.getInstance().getConf()
                .getString("processor.ssh.username");

        JSch jsch = new JSch();
        Session session = null;
        try {
            session = createSession( jsch,  ip );
            ChannelSftp channel = null;
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            File localFile = new File(tmpFile);
            //If you want you can change the directory using the following line.
            channel.cd(remoteDir);
            channel.put(new FileInputStream(localFile), "R" + localFile.getName());
            channel.disconnect();
            session.disconnect();
            logger.info("File successful uploaded: " + localFile);
            return true;

        } catch (JSchException e) {
            e.printStackTrace();
            logger.error("Ssh error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("File not found : " + e.getMessage());
        } catch (SftpException e) {
            logger.error("Sftp error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;

    }

    private static Session createSession(JSch jsch, String ip ) throws JSchException {

        String username = LQPConfiguration.getInstance().getConf()
                .getString("processor.ssh.username");
        Session session = jsch.getSession(username, ip, 22);

        if (LQPConfiguration.getInstance().getConf()
                .containsKey("processor.ssh.rsa")) {
            String privateKey = LQPConfiguration.getInstance().getConf().getString("processor.ssh.rsa");
            logger.info("ssh identity added: " + privateKey);
            jsch.addIdentity(privateKey);
            session = jsch.getSession(username, ip, 22);

        } else if (LQPConfiguration.getInstance().getConf()
                .containsKey("processor.ssh.password"))
            session.setPassword(LQPConfiguration.getInstance().getConf()
                    .getString("processor.ssh.password"));
        else
        {
            logger.error("No ssh credentials, no password either key ");
            System.out.println("No ssh credentials, no password either key ");
        }
        session.setConfig("StrictHostKeyChecking", "no");

        session.connect();
        System.out.println("Connected");
        return session;

    }



    private static boolean checkArguments(String[] args) {
        boolean result = false;
        if (args.length == 1)
            if (args[0].endsWith(".xml") || args[0].endsWith(".properties"))
                result = true;

        return result;
    }
}
