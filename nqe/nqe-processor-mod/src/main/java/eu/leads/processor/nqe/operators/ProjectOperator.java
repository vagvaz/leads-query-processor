package eu.leads.processor.nqe.operators;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import eu.leads.processor.nqe.operators.BasicOperator;
import eu.leads.processor.nqe.operators.OperatorType;
import org.vertx.java.core.json.JsonObject;



@JsonAutoDetect
public class ProjectOperator extends BasicOperator {

    public ProjectOperator() {
        super(OperatorType.PROJECT);
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
}