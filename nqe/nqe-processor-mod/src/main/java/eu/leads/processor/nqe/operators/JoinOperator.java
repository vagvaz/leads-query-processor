package eu.leads.processor.nqe.operators;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import eu.leads.processor.common.LeadsMapperCallable;
import eu.leads.processor.common.LeadsReduceCallable;
import eu.leads.processor.nqe.operators.BasicOperator;
import eu.leads.processor.nqe.operators.OperatorType;
import eu.leads.processor.nqe.operators.mapreduce.JoinMapper;
import eu.leads.processor.nqe.operators.mapreduce.JoinReducer;
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
 * Date: 11/7/13
 * Time: 8:34 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect
public class JoinOperator extends MapReduceOperator {
    protected LeadsMapperCallable<String, String, String, String> MapperCAll;
    protected LeadsReduceCallable<String, String> ReducerCAll;
    public JoinOperator() {
        super(OperatorType.JOIN);
    }

    @Override
    public void init(JsonObject config) {
        super.init(config); //fix set correctly caches names
        //fix configuration
        Properties configuration = null;
        setMapper(new JoinMapper(configuration));
        setReducer(new JoinReducer(configuration));
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
            //clean caches?
    }
}/* ExecutionPlanNode {

    Table left;
    Table right;
    Column leftColumn;
    Column rightColumn;


    @JsonCreator
    public JoinOperator(@JsonProperty("name") String name, @JsonProperty("output") String output, @JsonProperty("left") Table leftTable, @JsonProperty("right") Table rightTable, @JsonProperty("leftColumn") Column leftColumn, @JsonProperty("rightColumn") Column rightColumn) {
        super(name, OperatorType.JOIN);
        this.left = leftTable;
        this.right = rightTable;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
        setOutput(output);
        setOperatorType(OperatorType.JOIN);
        setType(OperatorType.toString(OperatorType.JOIN));
    }

    public Table getLeft() {
        return left;
    }

    public void setLeft(Table left) {
        this.left = left;
    }

    public Table getRight() {
        return right;
    }

    public void setRight(Table right) {
        this.right = right;
    }

    public Column getLeftColumn() {
        return leftColumn;
    }

    public void setLeftColumn(Column leftColumn) {
        this.leftColumn = leftColumn;
    }

    public Column getRightColumn() {
        return rightColumn;
    }

    public void setRightColumn(Column rightColumn) {
        this.rightColumn = rightColumn;
    }

    public JoinOperator(String name) {
        super(name);
    }


//    public JoinOperator(@JsonProperty("name") String name, @JsonProperty("operatorType") OperatorType type,@JsonProperty("output") String output, Table right, Table left, Column rightColumn,Column leftColumn) {
//        super(name, OperatorType.JOIN);
//        this.left = left;
//        this.right = right;
//        this.leftColumn = leftColumn;
//        this.rightColumn = rightColumn;
//        setOutput(output);
//        setOperatorType(OperatorType.JOIN);
//        setType(OperatorType.toString(OperatorType.JOIN));
//    }

    public JoinOperator(PlanNode node) {
        super(node, OperatorType.JOIN);
    }

    @Override
    public String toString() {
        return getType() + " " + left.getName() + "." + leftColumn.getColumnName() + " = " + right.getName() + "." + rightColumn.getColumnName();
    }
}
*/