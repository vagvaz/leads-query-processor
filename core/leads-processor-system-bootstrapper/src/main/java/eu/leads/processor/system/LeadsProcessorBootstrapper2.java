package eu.leads.processor.system;

import com.jcraft.jsch.*;
//import eu.leads.processor.conf.LQPConfiguration;
import org.apache.commons.configuration.*;
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

    static Map<String, String> screensIPmap;
    static Configuration xmlConfiguration=null;
    static CompositeConfiguration conf=null;
    static JsonObject globalJson;

    static int pagecounter= 0;
    public static void main(String[] args)  {
        globalJson = new JsonObject();
        conf = new CompositeConfiguration();
        org.apache.log4j.BasicConfigurator.configure();
        if (checkArguments(args)) {
            //LQPConfiguration.initialize(true);
            filename = args[0];
            //LQPConfiguration.getInstance().loadFile(filename);
            //ips = LQPConfiguration.getInstance().getConfiguration().getStringArray("processor.ips");
          /*  components =
                    LQPConfiguration.getInstance().getConf().getStringArray("processor.components");
            configurationFiles = LQPConfiguration.getInstance().getConf()
                    .getStringArray("processor.configurationFiles");*/
        }
        try {
            xmlConfiguration = new XMLConfiguration(filename);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            System.err.println("Unable to load configuration, Please check the file: " + filename);
            System.exit(-1);
        }
        conf.addConfiguration(xmlConfiguration);
        ips = conf.getStringArray("processor.ips");

        //XMLConfiguration xmlConfiguration = (XMLConfiguration) LQPConfiguration.getInstance().getConfigurations().get(filename);

        List<HierarchicalConfiguration> nodes = ((XMLConfiguration) xmlConfiguration).configurationsAt("processor.component");

        globalJson = checkandget(conf,"hdfsuser",globalJson);
        globalJson = checkandget(conf,"hdfsprefix",globalJson);
        globalJson = checkandget(conf,"scheduler",globalJson);
        globalJson = checkandget(conf,"hdfsuri",globalJson);



        componentsXml = new HashMap<>();
        componentsConf = new HashMap<>();
        componentsJson = new HashMap<>();
        screensIPmap= new HashMap<>();
        //String baseDir = LQPConfiguration.getInstance().getConf().getString("processor.ssh.baseDir");
        String baseDir = conf.getString("processor.ssh.baseDir");

         for (HierarchicalConfiguration c : nodes) {
            ConfigurationNode node = c.getRootNode();
            logger.info("Loading configuration, Name: " + c.getString("name") + " Processors " + c.getString("numberOfProcessors"));
            componentsXml.put(c.getString("name"), c);
            if (c.containsKey("configurationFile")) {
                //LQPConfiguration.getInstance().loadFile(c.getString("configurationFile"));

                String filePathname = baseDir+c.getString("configurationFile");
                //LQPConfiguration.getInstance().loadFile(filePathname);
                XMLConfiguration subconf = null;//(XMLConfiguration) LQPConfiguration.getInstance().getConfigurations().get(filePathname);
                try {
                    subconf = new XMLConfiguration(filePathname);
                } catch (ConfigurationException e) {
                    System.err.println("File " + filePathname + " not found, fix configuration files. \nExiting stopping boot!!!!!!!");
                    System.exit(1);
                }
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
        //System.out.println(remoteExecute("localhost", "ls /tmp/boot-conf/"));
        String remoteJson = remoteExecute("localhost", "sudo -S -p '' salt-cloud -l 'quiet' --out='json' -c "+ baseDir+"salt -m "+ baseDir+"salt/leads_query-engine.map --query\n");
        HashMap<String,JsonArray> mcIps = new HashMap<>();
        JsonArray microclouds = new JsonArray();

        if(remoteJson!=null)
        {
            JsonObject remoteJ = new JsonObject(remoteJson);
            Set<String> MicroClouds = remoteJ.getFieldNames();
            JsonObject newmc=null;
            for(String mc : MicroClouds){
                System.out.print("Found mc " + mc);
                JsonObject stacj = remoteJ.getObject(mc);
                Set<String> openstacks = stacj.getFieldNames();
                for(String mcnodes : openstacks) {
                    System.out.print("Found openstack " + mcnodes);
                    JsonObject machinesJ = stacj.getObject(mcnodes);
                    Set<String> McMachines = machinesJ.getFieldNames();
                    for(String machine : McMachines) {
                        JsonObject machineDesc = machinesJ.getObject(machine);
                        System.out.print("Found machine " + machine + " Ip " + machineDesc.getArray("public_ips").toString());
                        //to do fix
                        mcIps.put(mc,machineDesc.getArray("public_ips"));
                    }
                }
                newmc = new JsonObject();
                newmc.putArray(mc, mcIps.get(mc));
                microclouds.addObject(newmc);
            }
        }
        globalJson.putArray("microclouds",microclouds);
        //runRemotely("test","localhost","vertx");
        int ip = 0;
        //String sleep = LQPConfiguration.getInstance().getConf().getString("processor.ssh.startWaitSeconds");
        String sleep = conf.getString("processor.ssh.startWaitSeconds");
        int sleepTime = 5;
        if(sleep!=null)
        {
            try {
                int parsedInt = Integer.parseInt(sleep);
                sleepTime= parsedInt;
            }catch (NumberFormatException e){
                ;
            }
        }
        boolean started = false;
        for (Map.Entry<String, JsonObject> e : componentsJson.entrySet()) {
            //JSch jsch = new JSch();
           // Session session = createSession( jsch,  ips[ip] );
            if(started)
                for(int t = 0; t<sleepTime; t++) {
                    System.out.print(" .");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            deployComponent(e.getKey(), ips[ip]);
            ip = (ip + 1) % ips.length;
            System.out.print("\nWaiting for " + sleepTime + " seconds to start the next module.");

            //break;
            started=true;
        }
    }
    private static JsonObject checkandget(Configuration conf, String attribute, JsonObject inputJson){
        if (conf.containsKey(attribute))
            inputJson.putString(attribute, conf.getString(attribute));
        return inputJson;
    }

    private static JsonObject convertConf2Json(XMLConfiguration subconf) {
        //System.out.println(" componentType: " + subconf.getString("componentType"));
        Iterator it = null;
        JsonObject ret = new JsonObject();
        JsonArray otherGroups = null;
        ret.putObject("global",globalJson);
        if (subconf.containsKey("componentType")) {
            String componentType = subconf.getString("componentType");
            ret.putString("id", componentType + "-default-" + UUID.randomUUID().toString());
            ret.putString("group", componentType);


//            ret.putString("version", LQPConfiguration.getInstance().getConf().getString("processor.version"));
//            ret.putString("groupId", LQPConfiguration.getConf().getString("processor.groupId"));
            ret.putString("version", conf.getString("processor.version"));
            ret.putString("groupId", conf.getString("processor.groupId"));



            if(componentType.toLowerCase().equals("leads.log.sink")){ //Special case for the log sink module
                if (subconf.containsKey("groups")) {
                    otherGroups = new JsonArray();
                    for (String group : subconf.getStringArray("groups"))
                        otherGroups.addString(group);
                    ret.putArray("groups", otherGroups);
                }
                return ret;
            }
            ret.putString("componentType", componentType);
            List<HierarchicalConfiguration> servicenodes = subconf.configurationsAt("service");

            String type = subconf.getString("service.type");
            // System.out.println(" Type: " + type);
            List<HierarchicalConfiguration> nodes = subconf.configurationsAt("service.conf");

            // System.out.println("service  size " + servicenodes.size());

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
       // Configuration c = LQPConfiguration.getInstance().getConf();

        String group = conf.getString("processor.groupId");
        String version = conf.getString("processor.version");
        //      String command = "vertx runMod " + group +"~"+ component + "-mod~" + version + " -conf /tmp/"+config.getString("id")+".json";
        String remotedir = conf.getString("processor.ssh.remoteDir");
        String vertxComponent = null;
        if (modJson.containsField("modName"))
            vertxComponent = group + "~" + modJson.getString("modName") + "~" + version;
        else
            vertxComponent = group + "~" + component + "-comp-mod~" + version;

        String command = "vertx runMod " + vertxComponent + " -conf " + remotedir + "R" + modJson.getString("id") + ".json";
        if (conf.containsKey("processor.vertxArg"))
            command += " -" + conf.getString("processor.vertxArg");

        //System.out.println(command);

        runRemotely(modJson.getString("id"), ip, command);

  }

    public static void runRemotely(String id, String ip, String command){
        runRemotely( id,  ip,  command, false);
    }

    public static void runRemotely(String id, String ip, String command, boolean existingWindow) {
        String command1;
        if(existingWindow)
        {
            String session_name;
            if(screensIPmap.containsKey(ip)) {
                session_name = screensIPmap.get(ip);
                // run top within that bash session
                String command0 = "cd ~/.vertx_mods && screen -S " + session_name ;
                // run top within that bash session
                command1 = command0 +   " screen -S  " + session_name + " -p " + (pagecounter++) +" -X stuff $\"" + " bash -l &&" +command + "\\r\"";//ping 147.27.18.1";
            }
            else
            {
                session_name = "shell_" + ip;
                screensIPmap.put(ip, session_name );
                String command0 = "cd ~/.vertx_mods && screen -AmdS " + session_name + " bash -l";
                // run top within that bash session
                command1 = command0 + " && " + "screen -S  " + session_name + " -p \" + (pagecounter++) +\" -X stuff $\"" + command + "\\r\"";//ping 147.27.18.1";
            }



        }else {
            String command0 = "cd ~/.vertx_mods &&  screen -AmdS shell_" + id + " bash -l";
            // run top within that bash session
            command1 = command0 + " && " + "screen -S shell_" + id + " -p 0 -X stuff $\"" + command + "\\r\"";//ping 147.27.18.1";
        }

        System.out.print("Cmd: " + command1);
        logger.info("Execution command: " + command1);
        //command1 =command;
        remoteExecute( ip,  command1);

    }

    private static String remoteExecute(String ip, String command1){
        String ret="";
        try {
            JSch jsch = new JSch();

            Session session = createSession(jsch, ip);
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command1);
            OutputStream out=channel.getOutputStream();
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            //((ChannelExec) channel).setPty(true);


            InputStream in = channel.getInputStream();
            channel.connect();
            if(command1.contains("sudo") && conf.containsKey("processor.ssh.password"))
            {
                out.write((conf.getString("processor.ssh.password")+"\n").getBytes());
                out.flush();
            }
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                   // System.out.print(new String(tmp, 0, i));
                    ret+=new String(tmp, 0, i);
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
            if(!ret.isEmpty()){
                System.out.println(ret);
            }
            channel.disconnect();
            session.disconnect();


            if(channel.getExitStatus()==0)
                logger.info("Remote execution DONE");
            else
                logger.error("Unsuccessful execution");



        } catch (Exception e) {
            logger.error("Remote execution error: " + e.getMessage());

        }
        return ret;
    }


    private static boolean sendConfigurationTo(JsonObject config, String ip) {
        RandomAccessFile file = null;
        //String remoteDir = LQPConfiguration.getInstance().getConf().getString("processor.ssh.remoteDir");
        String remoteDir = conf.getString("processor.ssh.remoteDir");
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

//        String username = LQPConfiguration.getInstance().getConf()
//                .getString("processor.ssh.username");
        String username = conf.getString("processor.ssh.username");

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

//        String username = LQPConfiguration.getInstance().getConf()
//                .getString("processor.ssh.username");
        String username = conf.getString("processor.ssh.username");
        Session session = jsch.getSession(username, ip, 22);

        if (xmlConfiguration.containsKey("processor.ssh.rsa")) {
            String privateKey = conf.getString("processor.ssh.rsa");
            logger.info("ssh identity added: " + privateKey);
            jsch.addIdentity(privateKey);
            session = jsch.getSession(username, ip, 22);

        } else if (xmlConfiguration.containsKey("processor.ssh.password"))
            session.setPassword(conf.getString("processor.ssh.password"));
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
