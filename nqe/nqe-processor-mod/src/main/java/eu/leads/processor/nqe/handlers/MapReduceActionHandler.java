package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.*;
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
import eu.leads.processor.nqe.operators.mapreduce.*;
import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by vagvaz on 8/6/14.
 */
public class MapReduceActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final PersistenceProxy persistence;
    private final String id;

    protected InfinispanManager iman;
    private String textFile;
    private transient Cache<?, ?> InCache;
    private transient Cache<?, List<?>> CollectorCache;
    private transient Cache<?, ?> OutCache;

    public MapReduceActionHandler(Node com, LogProxy log, PersistenceProxy persistence, String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;

        InCache = (Cache<?, ?>) iman.getPersisentCache("InCache");
        CollectorCache = (Cache<?, List<?>>) iman
                .getPersisentCache("CollectorCache");
        OutCache = (Cache<?, ?>) iman.getPersisentCache("OutCache");


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
                String operation = q.getString("operator");
                String uniqueId = generateNewQueryId(user);
                JsonObject actionResult = new JsonObject();
//             SQLQuery query = new SQLQuery(user, sql);
//             query.setId(uniqueId);
                QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");

                DistributedExecutorService des = new DefaultExecutorService(InCache);

                //Create Mapper
                //Create Reducer
                LeadsMapper<?, ?, ?, ?> Mapper;
                LeadsCollector<?, ?> Collector=new LeadsCollector(0, CollectorCache);
                LeadsReducer<?, ?> Reducer;

                Properties configuration = new Properties();

                if (operation == NQEConstants.GROUPBY_OP) {
                    Mapper = new GroupByMapper(configuration);
                    Reducer = new GroupByReducer(configuration);
                } else if (operation == NQEConstants.JOIN_OP) {
                    Mapper = new JoinMapper(configuration);
                    Reducer = new JoinReducer(configuration);
                } else if (operation == NQEConstants.JOIN_OP) {
                    Mapper = new JoinMapper(configuration);
                    Reducer = new JoinReducer(configuration);
                } else if (operation == NQEConstants.SORT_OP) {
                    Mapper = new SortMapper(configuration);
                    Reducer = new SortReducer(configuration);

                    // else { //custom mapreduce process
                } else {
                    actionResult.putString("error", operation + "  Not found");
                    result.setResult(actionResult);
                    return result;
                }


                LeadsMapperCallable<?, ?, ?, ?> MapperCAll = new LeadsMapperCallable(InCache, Collector, Mapper);


                LeadsReduceCallable<?, ?> testReducerCAll = new LeadsReduceCallable(OutCache, Reducer);

                System.out.println("InCache Cache Size:" + InCache.size());

                Future<? extends List<?>> res = des.submit(MapperCAll);

                try {
                    if (res.get() != null)
                        System.out.println("Mapper Execution is done");
                    else
                        System.out.println("Mapper Execution not done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                // System.out.println("testCollector Cache Size:"
                //         + Collector.getCache().size());

                DistributedExecutorService des_inter = new DefaultExecutorService(
                        CollectorCache);
                List<Future<?>> reducers_res = des_inter
                        .submitEverywhere(testReducerCAll);
                for (Future<?> f : reducers_res) {
                    if (f != null)
                        if (f.get() != null)
                            System.out.println("Reducer Execution is done");
                }

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


