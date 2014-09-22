package eu.leads.processor.nqe.operators;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.leads.processor.plan.ExecutionPlanNode;
import eu.leads.processor.sql.PlanNode;
import net.sf.jsqlparser.schema.Column;*/
import eu.leads.processor.common.Column;
import eu.leads.processor.common.LeadsMapperCallable;
import eu.leads.processor.common.LeadsReduceCallable;
import eu.leads.processor.nqe.operators.BasicOperator;
import eu.leads.processor.nqe.operators.MapReduceOperator;
import eu.leads.processor.nqe.operators.OperatorType;
import eu.leads.processor.nqe.operators.mapreduce.SortReducer;
import eu.leads.processor.nqe.operators.mapreduce.SortMapper;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 10/29/13
 * Time: 1:12 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect
public class SortOperator extends MapReduceOperator {
    List<Column> columns;
    private List<Boolean> ascending;
    protected LeadsMapperCallable<String, String, String, String> MapperCAll;
    protected LeadsReduceCallable<String, String>  ReducerCAll;
    public SortOperator() {
        super(OperatorType.SORT);
    }

    @Override
    public void init(JsonObject config) {
        super.init(config); //fix set correctly caches names
        //fix configuration
        Properties configuration = null;
        setMapper(new SortMapper(configuration)); //set and initialize mapper fix it
        setReducer(new SortReducer(configuration));
    }

    @Override
    public void execute() {  //Need Heavy testing
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


    public List<Boolean> getAscending() {
        return ascending;
    }

    public void setAscending(List<Boolean> ascending) {
        this.ascending = ascending;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
/*
    List<Boolean> ascending;

    public SortOperator(String name) {
        super(name, OperatorType.SORT);
    }

    public SortOperator(PlanNode node) {
        super(node, OperatorType.SORT);
    }

    @JsonCreator
    public SortOperator(@JsonProperty("name") String name, @JsonProperty("output") String output, @JsonProperty("columns") List<Column> orderByColumns, @JsonProperty("asceding") List<Boolean> ascendingOrder) {
        super(name, OperatorType.SORT);
        setOutput(output);
        this.columns = orderByColumns;
        this.ascending = ascendingOrder;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(" ");
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getTable() != null)
                builder.append(columns.get(i).getWholeColumnName() + " " + (ascending.get(i) ? " ASC " : " DESC "));
            else
                builder.append(columns.get(i).getColumnName() + " " + (ascending.get(i) ? " ASC " : " DESC "));
        }
        return getType() + builder.toString();
    }
*/}
