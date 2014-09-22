package eu.leads.processor.nqe.operators;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import eu.leads.processor.common.LeadsMapperCallable;
import eu.leads.processor.common.LeadsReduceCallable;
import eu.leads.processor.nqe.operators.BasicOperator;
import eu.leads.processor.nqe.operators.mapreduce.GroupByMapper;
import eu.leads.processor.nqe.operators.mapreduce.GroupByReducer;
import eu.leads.processor.nqe.operators.mapreduce.SortMapper;
import eu.leads.processor.nqe.operators.mapreduce.SortReducer;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Properties;
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

    protected LeadsMapperCallable<String, String, String, String> MapperCAll;
    protected LeadsReduceCallable<String, String> ReducerCAll;

    public GroupByOperator() {
        super(OperatorType.GROUPBY);
    }

    @Override
    public void init(JsonObject config) {
        Properties configuration = null;
        setMapper(new GroupByMapper(configuration));
        setReducer(new GroupByReducer(configuration));
    }

    @Override
    public void execute() {
        DistributedExecutorService des = new DefaultExecutorService(InCache);

        List<Future<List<String>>> res = des.submitEverywhere(MapperCAll);
        for (Future<?> f : res)
            if (f.isDone())
                System.out.println("a Mapper Execution is done");
            else
                System.out.println("Mapper Execution not done");


        DistributedExecutorService des_inter = new DefaultExecutorService(
                CollectorCache);
        List<Future<String>> reducers_res=des_inter.submitEverywhere(ReducerCAll);
        for (Future<?> f : reducers_res) {
            if (f != null)
                if (f.isDone())
                    System.out.println("a Reducer Execution is done");
        }

    }

    @Override
    public void cleanup() {

    }
}
/* extends ExecutionPlanNode {


    private List<Column> columns;
    private List<Function> functions;

    @JsonCreator
    public GroupByOperator(@JsonProperty("name") String name, @JsonProperty("output") String output, @JsonProperty("columns") List<Column> groupByColumns, @JsonProperty("functions") List<Function> functions) {
        super(name, OperatorType.GROUPBY);
        this.columns = groupByColumns;
        this.functions = functions;
        setOutput(output);
        setOperatorType(OperatorType.GROUPBY);
        setType(OperatorType.toString(OperatorType.GROUPBY));
    }

    public GroupByOperator(String name) {
        super(name);
        setOutput(output);
        setOperatorType(OperatorType.GROUPBY);
        setType(OperatorType.toString(OperatorType.GROUPBY));
    }

    public GroupByOperator(PlanNode node) {
        super(node, OperatorType.GROUPBY);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(" ");
        for (Column c : columns) {
            builder.append(c.getWholeColumnName() + "\t");
        }
        if (functions.size() > 0) {
            builder.append(" computing functions ");
            for (Function f : functions)
                builder.append(f.toString() + ", ");
        }
        return getType() + builder.toString();
    }
}
*/