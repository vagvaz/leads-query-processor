package eu.leads.processor.nqe.operators;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import eu.leads.processor.core.LeadsMapperCallable;
import eu.leads.processor.core.LeadsReduceCallable;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.nqe.operators.mapreduce.GroupByMapper;
import eu.leads.processor.nqe.operators.mapreduce.GroupByReducer;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 10/29/13
 * Time: 1:19 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect
//@JsonDeserialize(converter = GroupByJsonDelegate.class)
public class GroupByOperator extends MapReduceOperator {


    List<String> groupByColumns;

    public GroupByOperator(Node com, InfinispanManager persistence, Action action) {

       super(com, persistence, action);

       JsonArray columns = conf.getObject("body").getArray("groupingColumns");
       Iterator<Object> columnIterator = columns.iterator();
       groupByColumns = new ArrayList<>(columns.size());

       while(columnIterator.hasNext()){
         JsonObject columnObject = (JsonObject) columnIterator.next();
         groupByColumns.add(columnObject.getString("name"));
       }

       JsonArray functions = conf.getObject("body").getArray("aggrFunctions");
       Iterator<Object> funcIterator = functions.iterator();
       List<JsonObject> aggregates = new ArrayList<>(functions.size());
       while(funcIterator.hasNext()){
          aggregates.add((JsonObject) funcIterator.next());
       }
   }

   @Override
    public void init(JsonObject config) {
        super.init(config);
        setMapper(new GroupByMapper(conf.toString()));
        setReducer(new GroupByReducer(conf.toString()));
    }

    @Override
    public void execute() {
      super.execute();

    }

    @Override
    public void cleanup() {
      super.cleanup();
    }
}
