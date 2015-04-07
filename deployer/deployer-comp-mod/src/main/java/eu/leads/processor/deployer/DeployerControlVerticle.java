package eu.leads.processor.deployer;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.comp.ComponentControlVerticle;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/27/14.
 */
public class DeployerControlVerticle extends ComponentControlVerticle {
    protected final String componentType = "deployer";
    private String plannerQueue;
    private String nqeQueue;

    @Override
    public void start() {
        plannerQueue = StringConstants.PLANNERQUEUE;
        nqeQueue = StringConstants.NODEEXECUTORQUEUE;
        super.start();
        setup(container.config());
        startUp();
    }

    @Override
    public void setup(JsonObject conf) {
        super.setup(conf);
        logicConfig.putString("deployer", StringConstants.DEPLOYERQUEUE);
        logicConfig.putString("nqe", nqeQueue);
        logicConfig.putString("monitor","deployer."+id+".monitor");

       processorConfig.putString("deployer", StringConstants.DEPLOYERQUEUE);
       processorConfig.putString("nqe",nqeQueue);
       processorConfig.putString("monitor","deployer."+id+".monitor");

    }

    @Override
    public void startUp() {
        super.startUp();
    }

    @Override
    public void stopComponent() {
        super.stopComponent();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void undeployAllModules() {
        super.undeployAllModules();
    }

    @Override
    public void reset(JsonObject conf) {
        super.reset(conf);
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void kill() {
        super.kill();
    }

    @Override
    public String getComponentType() {
        return componentType;
    }
}
