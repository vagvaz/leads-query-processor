package eu.leads.processor.planner.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.*;
import eu.leads.processor.planner.PlanUtils;
import leads.tajo.module.TaJoModule;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.catalog.TableDesc;
import org.apache.tajo.catalog.TableMeta;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.plan.logical.ScanNode;
import org.apache.tajo.util.KeyValueSet;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vagvaz on 8/19/14.
 */
public class ProcessSpecialQueryActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;
    private final TaJoModule module;
  private final Cache statisticsCache;
  private Cache<String,String> queriesCache;
    private String schedHost;
    private String schedPort;
  private String currentCluster;
  private JsonObject globalInformation;


  public ProcessSpecialQueryActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                               String id, TaJoModule module,String schedHost,String
                                                                                               schedPort,
                                           JsonObject globalInformation) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
        this.module = module;
      this.schedHost = schedHost;
      this.schedPort = schedPort;
    this.globalInformation = globalInformation;
      statisticsCache = (Cache) persistence.getPersisentCache(StringConstants.STATISTICS_CACHE);
       queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
      currentCluster = LQPConfiguration.getInstance().getMicroClusterName();
    }

    @Override
    public Action process(Action action) {
        Action result = action;
        SpecialQuery specialQuery = new SpecialQuery(action.getData().getObject("query"));
        if (specialQuery.getSpecialQueryType().equals("rec_call")) {
            RecursiveCallQuery query = new RecursiveCallQuery(specialQuery);

            ScanNode node = new ScanNode(0);

            Path testPath = new Path("test-webpages-path");
            TableMeta meta = new TableMeta(CatalogProtos.StoreType.SEQUENCEFILE,new KeyValueSet());
            TableDesc desc = new TableDesc("default.webpages",module.getTableSchema("webpages"),meta, testPath.toUri() );
            node.init(desc);
            WGSUrlDepthNode rootNode = new WGSUrlDepthNode(1);
            rootNode.setUrl(query.getUrl());
            rootNode.setDepth(query.getDepth());
            rootNode.setChild(node);
            SQLPlan plan = new SQLPlan(query.getId(), rootNode);
            Set<SQLPlan> candidatePlans = new HashSet<SQLPlan>();
            candidatePlans.add(plan);
            Set<SQLPlan> evaluatedPlans = evaluatePlansFromScheduler(candidatePlans);
            SQLPlan selectedPlan = choosePlan(evaluatedPlans);
            query.setPlan(selectedPlan);
            JsonObject actionResult = new JsonObject();
            actionResult.putString("status", "ok");
            actionResult.putObject("query", query.asJsonObject());
            result.setStatus(ActionStatus.COMPLETED.toString());
            result.setResult(actionResult);
        }else if(specialQuery.getSpecialQueryType().equals("ppq_call")){
           PPPQCallQuery query = new PPPQCallQuery(specialQuery);

           ScanNode node = new ScanNode(0);

           Path testPath = new Path("test-"+query.getCache()+"-path");
           TableMeta meta = new TableMeta(CatalogProtos.StoreType.SEQUENCEFILE,new KeyValueSet());
           TableDesc desc = new TableDesc(query.getCache(),module.getTableSchema("entities"),meta, testPath.toUri() );
           node.init(desc);
           EncryptedPointQueryNode rootNode = new EncryptedPointQueryNode(1);
           rootNode.setCache(query.getCache());
           rootNode.setToken(query.getToken());
           rootNode.setChild(node);
           SQLPlan plan = new SQLPlan(query.getId(), rootNode);
           Set<SQLPlan> candidatePlans = new HashSet<SQLPlan>();
           candidatePlans.add(plan);
           Set<SQLPlan> evaluatedPlans = evaluatePlansFromScheduler(candidatePlans);
           SQLPlan selectedPlan = choosePlan(evaluatedPlans);
           query.setPlan(selectedPlan);
           JsonObject actionResult = new JsonObject();
           actionResult.putString("status", "ok");
           actionResult.putObject("query", query.asJsonObject());
           result.setStatus(ActionStatus.COMPLETED.toString());
           result.setResult(actionResult);
        }
        return result;
    }

    private SQLPlan choosePlan(Set<SQLPlan> evaluatedPlans) {
        //Iterate over the evaluated plans and use a heuristic method to choose a plan.
        SQLPlan plan = evaluatedPlans.iterator().next();
        return plan;
    }

    private Set<SQLPlan> evaluatePlansFromScheduler(Set<SQLPlan> candidatePlans) {
        //Transform each plan to scheduler like format.
        //Annotate each operator with k,q
        //Send Request to Scheduler and receive Evaluations.
      Set<SQLPlan> result = new HashSet<>();
      for(SQLPlan plan : candidatePlans){
        JsonObject p = plan.getPlanGraph().copy();
//        p = PlanUtils.handleRootOutputNodes(p);
        p = PlanUtils.updateKeyspaceParameter(p);
        p = PlanUtils.numberStages(p);
        p = PlanUtils.annotatePlan(statisticsCache, p);
        JsonObject annotatedPlan = null;
//        try {
          JsonObject schedulerRep = PlanUtils.getSchedulerRep(p,currentCluster);
          System.err.println(schedulerRep.encodePrettily());
          annotatedPlan = PlanUtils.emulateScheduler(schedulerRep,globalInformation);
//          annotatedPlan = WP4Client.evaluatePlan(schedulerRep, schedHost, schedPort);
//        } catch (IOException e) {
//          log.error("Exception  e " + e.getMessage());
//          SQLQuery query = new SQLQuery( new JsonObject(queriesCache.get(plan.getQueryId())));
//          failQuery(new Exception("Could not access the scheduler"),query);            }
//        if(annotatedPlan == null){
//          SQLQuery query = new SQLQuery( new JsonObject(queriesCache.get(plan.getQueryId())));
//          failQuery(new Exception("Could not access the scheduler"),query);
//          return result;
//        }
        JsonObject updatedPlan = PlanUtils.updateInformation(plan.getPlanGraph(),annotatedPlan.getObject("stages"),globalInformation);
        updatedPlan = PlanUtils.updateTargetEndpoints(updatedPlan);
        System.err.println(updatedPlan.encodePrettily());
        plan.setPlanGraph(updatedPlan);
        result.add(plan);
      }
      return result;
    }

  private void failQuery(Exception e, SQLQuery sqlQuery) {
    QueryStatus status = sqlQuery.getQueryStatus();
    status.setErrorMessage(e.getMessage());
    status.setStatus(QueryState.FAILED);
    sqlQuery.setQueryStatus(status);
    queriesCache.put(sqlQuery.getId(), sqlQuery.asJsonObject().toString());
  }
}
