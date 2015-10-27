package eu.leads.processor.deployer;

import eu.leads.processor.core.DataType;
import eu.leads.processor.core.plan.SQLPlan;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 9/17/14.
 */
public class ExecutionPlan extends DataType {
    public ExecutionPlan(JsonObject object) {
        super(object);
    }

    public ExecutionPlan(SQLPlan sqlPlan) {
        super(sqlPlan.asJsonObject());
    }


}
