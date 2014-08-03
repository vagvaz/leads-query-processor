package eu.leads.processor.core;

import eu.leads.processor.core.comp.LeadsService;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.comp.ServiceStatus;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by vagvaz on 7/28/14.
 */
public abstract class ManageVerticle extends Verticle implements LeadsService{
    protected Node com;
    protected String id;
    protected String group;
    protected JsonObject config;
    protected ServiceStatus status = ServiceStatus.IDLE;
    protected PersistenceProxy persistenceProxy;
    protected LogProxy logProxy;
    protected ServiceHandler serviceHandler;
    protected String parent;

    @Override
    public void start() {
        super.start();
        config = container.config();
        id = config.getString("id");
        group = config.getString("group");
        parent = config.getString("parent");
//        initialize(config.getObject("conf"));

    }

    @Override
    public void initialize(JsonObject config) {
        setStatus(ServiceStatus.INITIALIZED);
       com = new DefaultNode();
       persistenceProxy = new PersistenceProxy(config.getString("persistence"), com);
       logProxy = new LogProxy(config.getString("log"),com);
       serviceHandler = new ServiceHandler(this,com,logProxy, persistenceProxy);
       com.initialize(id+".manage",group,null,serviceHandler,serviceHandler,this.getVertx());



    }

    @Override
    public void startService() {
        setStatus(ServiceStatus.RUNNING);
    }

    @Override
    public void cleanup() {
        setStatus(ServiceStatus.STOPPING);

    }

    @Override
    public void stopService() {
        setStatus(ServiceStatus.STOPPED);
    }

    @Override
    public ServiceStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    @Override
    public String getServiceId() {
        return id;
    }

    @Override
    public void fail(String message) {
        setStatus(ServiceStatus.FAILED);
        JsonObject errorMessage = new JsonObject();
        errorMessage.putString("status.message",message);
        persistenceProxy.store("status.message", errorMessage);

    }

}
