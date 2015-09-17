package eu.leads.processor.imanager;

import eu.leads.processor.core.ManageVerticle;
import eu.leads.processor.core.PidFileUtil;
import eu.leads.processor.core.comp.ServiceStatus;
import eu.leads.processor.core.net.MessageUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

import java.io.*;
import java.lang.management.ManagementFactory;


/**
 * Created by vagvaz on 8/6/14.
 */
public class IManagerProcessorManage extends ManageVerticle {
    final String serviceType = "imanager-processor";
    String workerId = null;

    @Override
    public void start() {
        super.start();
        initialize(config);
    }

    @Override
    public void initialize(JsonObject config) {
        super.initialize(config);
        com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));
    }

    @Override
    public void startService() {
        super.startService();
        if (workerId == null) {
            container
                .deployWorkerVerticle(IManageProcessorWorker.class.getCanonicalName(), config, 1,
                                         false, new Handler<AsyncResult<String>>() {
                    @Override
                    public void handle(AsyncResult<String> event) {
                        if (event.succeeded()) {
                            workerId = event.result();
                            logProxy.info("IManagerProcessorWorker " + config.getString("id")
                                              + " has been deployed");

                            com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id,
                                    serviceType));
                            try {
                                PidFileUtil.createPidFile(new File("/tmp/iman.pid"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            logProxy.info("IManagerProcessorWorker " + config.getString("id")
                                              + " failed to deploy");
                            stopService();
                        }
                    }
                });
        }


    }



    void writePid(String filename){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        pid = pid.split("@")[0];
        writer.println(pid);
        writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void stopService() {
        super.stopService();
        if (workerId != null)
            container.undeployModule(workerId);
        workerId = null;
        com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType));

    }

    @Override
    public ServiceStatus getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(ServiceStatus status) {
        super.setStatus(status);
    }

    @Override
    public String getServiceId() {
        return super.getServiceId();
    }

    @Override
    public void fail(String message) {
        super.fail(message);

        com.sendTo(parent, MessageUtils.createServiceStatusMessage(status, id, serviceType)
                               .putString("message", message));

    }

    @Override
    public String getServiceType() {
        return serviceType;
    }

//    @Override
//    public void exitService() {
//        System.exit(-1);
//    }
}
