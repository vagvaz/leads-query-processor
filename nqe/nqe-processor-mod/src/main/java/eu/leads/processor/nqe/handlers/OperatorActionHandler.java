package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.LeadsCollector;
import eu.leads.processor.common.LeadsMapper;
import eu.leads.processor.common.LeadsReducer;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.QueryContext;
import eu.leads.processor.core.plan.QueryState;
import eu.leads.processor.core.plan.QueryStatus;
import eu.leads.processor.core.plan.SQLQuery;
import eu.leads.processor.nqe.NQEConstants;
import eu.leads.processor.nqe.operators.*;
import eu.leads.processor.nqe.operators.mapreduce.*;
import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by vagvaz on 8/6/14.
 */
public class OperatorActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final PersistenceProxy persistence;
    private final String id;

    protected InfinispanManager iman;
    private String textFile;
    private transient Cache<?, ?> InCache;
    private transient Cache<?, List<?>> CollectorCache;
    private transient Cache<?, ?> OutCache;

    public OperatorActionHandler(Node com, LogProxy log, PersistenceProxy persistence, String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;




    }

    @Override
    public Action process(Action action) {
        Action result = action;
        try {
            JsonObject q = action.getData();
            // read monitor q.getString("monitor");
            if (q.containsField("sql")) {//SQL Query
                String user = q.getString("user");
                String sql = q.getString("sql");
                String uniqueId = generateNewQueryId(user);
                JsonObject actionResult = new JsonObject();
                SQLQuery query = new SQLQuery(user, sql);
                query.setId(uniqueId);
                QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");
                query.setQueryStatus(status);
                QueryContext context = new QueryContext(uniqueId);
                query.setContext(context);
                JsonObject queryStatus = status.asJsonObject();
                if (!persistence.put(StringConstants.QUERIESCACHE, uniqueId, query.asJsonObject())) {
                    actionResult.putString("error", "");
                    actionResult.putString("message", "Failed to add query " + sql + " from user " + user + " to the queries cache");

                }
                actionResult.putObject("status", query.getQueryStatus().asJsonObject());
                result.setResult(actionResult);
                result.setStatus(ActionStatus.COMPLETED.toString());
            } else if (q.containsField("mapreduce")) {//
                String user = q.getString("user");
                //String sql = q.getString("mapreduce");
                String operatorName = q.getString("operator");
                String uniqueId = generateNewQueryId(user);
                JsonObject actionResult = new JsonObject();
//             SQLQuery query = new SQLQuery(user, sql);
//             query.setId(uniqueId);
                QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");

                Operator Op =null;


                JsonObject configuration = new JsonObject();

                if (operatorName == NQEConstants.LIMIT_OP) {
                  Op = new LimitOperator();
                } else if (operatorName == NQEConstants.JOIN_OP) {
                  Op = new JoinOperator();
                } else if (operatorName == NQEConstants.GROUPBY_OP) {
                  Op = new GroupByOperator();
                } else if (operatorName == NQEConstants.SORT_OP) {
                    Op = new SortOperator();
                } else {
                    actionResult.putString("error", "Operator  Not found");
                    result.setResult(actionResult);
                    return result;
                }
                Op.init(configuration);

                Op.execute();


                JsonObject mapreduceStatus = status.asJsonObject();
//                if (!persistence.put(StringConstants.QUERIESCACHE, uniqueId, query.asJsonObject())) {
//                    actionResult.putString("error", "");
//                    actionResult.putString("message", "Failed to add query " + sql + " from user " + user + " to the queries cache");
//
//                }
//                actionResult.putObject("status", query.getQueryStatus().asJsonObject());

                //send msg to monitor operator completed

                result.setResult(actionResult);
                result.setStatus(ActionStatus.COMPLETED.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String generateNewQueryId(String prefix) {
        String candidateId = prefix + "." + UUID.randomUUID();
        while (persistence.contains(StringConstants.QUERIESCACHE, candidateId)) {
            candidateId = prefix + "." + UUID.randomUUID();
        }
        return candidateId;
    }
}


