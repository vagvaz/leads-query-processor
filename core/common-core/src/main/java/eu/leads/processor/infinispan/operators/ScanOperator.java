package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.LeadsNodeType;
import eu.leads.processor.core.plan.PlanNode;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.infinispan.LeadsMapperCallable;
import eu.leads.processor.infinispan.operators.mapreduce.GroupByMapper;
import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.distexec.DistributedTask;
import org.infinispan.distexec.DistributedTaskBuilder;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by vagvaz on 9/22/14.
 */
public class ScanOperator extends BasicOperator {

   boolean joinOperator;
   boolean groupByOperator;
   boolean sortOperator;

   public ScanOperator(Node com, InfinispanManager persistence,LogProxy log, Action action) {
      super(com,persistence,log,action);
   }

   //  public FilterOperator(PlanNode node) {
   //      super(node, OperatorType.FILTER);
   //  }




   @Override
   public void init(JsonObject config) {
      inputCache = (Cache) manager.getPersisentCache(getInput());
      if(conf.containsField("next")){
         if(conf.getString("next.type").equals(LeadsNodeType.GROUP_BY.toString())){
               groupByOperator = true;//new GroupByOperator(this.com,this.manager,this.log,this.action);
//            PlanNode node = new PlanNode(conf.getObject("next").asObject());
//            groupByOperator.setIntermediateCacheName(node.getNodeId()+".intermediate");
//            groupByOperator.init(conf.getObject("next").getObject("configuration"));
         } else if(conf.getString("next.type").equals(LeadsNodeType.JOIN.toString())){
            joinOperator = true;// new JoinOperator(this.com,this.manager,this.log,this.action);
//            PlanNode node = new PlanNode(conf.getObject("next").asObject());
//            joinOperator.setIntermediateCacheName(node.getNodeId()+".intermediate");
//            joinOperator.init(conf.getObject("next").getObject("configuration"));
         } else if (conf.getString("next.type").equals(LeadsNodeType.SORT.toString())){
            sortOperator = true;//new SortOperator(this.com,this.manager,this.log,this.action);
//            sortOperator.init(conf.getObject("next").getObject("configuration"));
            System.err.println("SORT SCAN NOT IMPLEMENTED YET");
         }else{
            System.err.println(conf.getString("next.type") + " SCAN NOT IMPLEMENTED YET");
         }
      }
   }

   @Override
   public void execute() {
      super.execute();
   }

   @Override
   public void cleanup() {

      System.err.println("CLEANING UP " );
      super.cleanup();
   }

   @Override
   public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {
      Set<String> targetMC = getTargetMC();
      for(String mc : targetMC){
         if(!conf.containsField("next")) {
            createCache(mc,getOutput());
         }
         else{
            createCache(mc, getOutput() + ".data", "localIndexListener");
         }
      }
   }

   @Override
   public void setupMapCallable() {
      inputCache = (Cache) manager.getPersisentCache(getInput());
      LeadsCollector collector = new LeadsCollector<>(0, getOutput());
      mapperCallable = new ScanCallableUpdate<>(conf.toString(),getOutput(),collector);
   }

   @Override
   public String getOutput(){
      String result  = super.getOutput();
      if(groupByOperator){
         result = conf.getObject("next").getString("id")+".intermediate";
      }
      if(joinOperator){
         result = conf.getObject("next").getString("id")+".intermediate";
      }
      if(sortOperator){

      }
      return result;
   }
   @Override
   public void setupReduceCallable() {

   }

   @Override
   public boolean isSingleStage() {
      return true;
   }


}
