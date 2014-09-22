package eu.leads.processor.nqe.operators.testing;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import eu.leads.processor.nqe.operators.BasicOperator;
import eu.leads.processor.nqe.operators.OperatorType;
import org.vertx.java.core.json.JsonObject;
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import eu.leads.processor.plan.ExecutionPlanNode;
//import eu.leads.processor.sql.PlanNode;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 11/5/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect
public class OutputOperator extends BasicOperator {

    public OutputOperator() {
        super(OperatorType.OUTPUT);
    }

    @Override
    public void init(JsonObject config) {

    }

    @Override
    public void execute() {

    }

    @Override
    public void cleanup() {

    }
}/* extends ExecutionPlanNode {

    @JsonCreator
    public OutputOperator(@JsonProperty("name") String name) {
        super(name, OperatorType.OUTPUT);

    }

    public OutputOperator(PlanNode node) {
        super(node, OperatorType.OUTPUT);
    }

    @Override
    public String toString() {
        return "OUTPUT";
    }
}*/
