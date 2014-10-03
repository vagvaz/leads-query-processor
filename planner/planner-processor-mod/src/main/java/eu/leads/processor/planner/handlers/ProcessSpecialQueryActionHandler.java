package eu.leads.processor.planner.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.RecursiveCallQuery;
import eu.leads.processor.core.plan.SQLPlan;
import eu.leads.processor.core.plan.SpecialQuery;
import eu.leads.processor.core.plan.WGSUrlDepthNode;
import leads.tajo.module.TaJoModule;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.catalog.TableDesc;
import org.apache.tajo.catalog.TableMeta;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.engine.planner.logical.ScanNode;
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
    private Cache<String,String> queriesCache;
    public ProcessSpecialQueryActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                               String id, TaJoModule module) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
        this.module = module;
       queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
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
            TableDesc desc = new TableDesc("default.webpages",module.getTableSchema("webpages"),meta, testPath );
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
        return candidatePlans;
    }
}
