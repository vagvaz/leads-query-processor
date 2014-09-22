package eu.leads.processor.nqe.operators;

import eu.leads.processor.common.*;
import eu.leads.processor.common.infinispan.InfinispanManager;
import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by tr on 19/9/2014.
 */
public abstract class MapReduceOperator extends BasicOperator{
    protected transient Cache<?, ?> InCache;
    protected transient Cache<?, List<?>> CollectorCache;
    protected transient Cache<?, ?> OutCache;

    public MapReduceOperator(OperatorType operatorType) {
        super(operatorType);
    }

    public void setMapper(LeadsMapper<?, ?, ?, ?> mapper) {
        Mapper = mapper;
        Mapper.initialize();
    }

    private LeadsMapper<?, ?, ?, ?> Mapper;
    private LeadsCollector<?, ?> Collector;

    public void setReducer(LeadsReducer<?, ?> reducer) {
        Reducer = reducer;
        Reducer.initialize();
    }

    private LeadsReducer<?, ?> Reducer;
    protected InfinispanManager iman;


    @Override
    public void init(JsonObject config) {


        InCache = (Cache<?, ?>) iman.getPersisentCache("InCache");
        CollectorCache = (Cache<?, List<?>>) iman
                .getPersisentCache("CollectorCache");
        OutCache = (Cache<?, ?>) iman.getPersisentCache("OutCache");

        Collector =new LeadsCollector(0, CollectorCache);



    }

    @Override /// Example do not use
    public void execute() {

//        DistributedExecutorService des = new DefaultExecutorService(InCache);
//
//        Future<? extends List<?>> res = des.submit(MapperCAll);
//
//        try {
//            if (res.get() != null)
//                System.out.println("Mapper Execution is done");
//            else
//                System.out.println("Mapper Execution not done");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        // System.out.println("testCollector Cache Size:"
//        //         + Collector.getCache().size());
//
//        DistributedExecutorService des_inter = new DefaultExecutorService(
//                CollectorCache);
//        List<Future<?>> reducers_res;
//        // reducers_res = des_inter
////                        .submitEverywhere(testReducerCAll);
////                for (Future<?> f : reducers_res) {
////                    if (f != null)
////                        if (f.get() != null)
////                            System.out.println("Reducer Execution is done");
////                }

    }

    @Override
    public void cleanup() {

    }
}
