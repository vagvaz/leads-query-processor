package eu.leads.processor.nqe.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.json.JsonObject;

import java.util.Properties;

/**
 * Created by tr on 30/8/2014.
 */
public abstract class BasicOperator extends Thread implements Operator{
    protected JsonObject conf;
    protected Action action;
    protected InfinispanManager manager;
    protected Node com;

    protected BasicOperator(Action action) {
        conf = action.getData();
        this.action = action;
    }
    protected BasicOperator(Node com, InfinispanManager manager,Action action){
       super(com.getId()+"-operator-thread");
       this.com = com;
       this.manager = manager;
       this.action = action;
       this.conf = action.getData().getObject("operator").getObject("configuration");
    }


   @Override
   public void init(JsonObject config) {
     this.conf = config;
   }

   @Override
   public void execute() {
      start();
   }

   @Override
   public void cleanup() {
      action.setStatus(ActionStatus.COMPLETED.toString());
      if(com != null)
         com.sendTo(action.getData().getString("owner"),action.asJsonObject());
      else
         System.err.println("PROBLEM Uninitialized com");
   }

   @Override
    public JsonObject getConfiguration() {
        return conf;
    }

    @Override
    public void setConfiguration(JsonObject config) {
        conf = config;
    }

   @Override
   public String getInput() {
      return action.getData().getObject("operator").getArray("inputs").iterator().next().toString();
   }

   @Override
   public void setInput(String input) {
      conf.putString("input",input);
   }

   @Override
   public String getOutput() {
      return action.getData().getObject("operator").getString("id");
   }

   @Override
   public void setOutput(String output) {
      conf.putString("output",output);
   }

   @Override
   public void setOperatorParameters(JsonObject parameters) {
      conf = parameters;
   }

   @Override
   public JsonObject getOperatorParameters() {
      return conf;
   }
}
