package eu.leads.processor.nqe.operators;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import eu.leads.processor.common.Tuple;
import eu.leads.processor.nqe.operators.BasicOperator;
import eu.leads.processor.nqe.operators.OperatorType;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

//import eu.leads.processor.plan.ExecutionPlanNode;
//import eu.leads.processor.sql.PlanNode;
//import net.sf.jsqlparser.statement.select.Limit;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 10/29/13
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonAutoDetect
public class LimitOperator extends BasicOperator {
    boolean sorted = false;
    final ConcurrentMap<String, String> inputMap=null;
    final ConcurrentMap<String, String> data=null;
    public LimitOperator() {
        super(OperatorType.LIMIT);
    }
    public String prefix;

    @Override
    public void init(JsonObject config) {
        ///How to initialize what ?
    }

    @Override
    public void execute() {
        int counter = 0;
        if (sorted) {
            int sz = inputMap.size();
            for (counter = 0; counter < limit.getRowCount() && counter < sz; counter++) {
                String tupleValue = inputMap.get(input + counter);
                Tuple t = new Tuple(tupleValue);
                handlePagerank(t);
                data.put(prefix + Integer.toString(counter), t.asString());
            }
        } else {
            for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                if (counter >= limit.getRowCount())
                    break;
                String tupleId = entry.getKey().substring(entry.getKey().indexOf(":") + 1);
                Tuple t = new Tuple(entry.getValue());
                handlePagerank(t);
                data.put(prefix + tupleId, t.asString());
                counter++;
            }
        }
    }
    private void handlePagerank(Tuple t) {
        if (t.hasField("pagerank")) {
            if (!t.hasField("url"))
                return;
            String pagerankStr = t.getAttribute("pagerank");
//            Double d = Double.parseDouble(pagerankStr);
//            if (d < 0.0) {

//                try {
////                    d = LeadsPrGraph.getPageDistr(t.getAttribute("url"));
//                    d = (double) LeadsPrGraph.getPageVisitCount(t.getAttribute("url"));
//                    System.out.println("vs cnt " + LeadsPrGraph.getTotalVisitCount());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                t.setAttribute("pagerank", d.toString());
//            }
        }
    }

    @Override
    public void cleanup() {

    }
}/*ExecutionPlanNode {
    private Limit limit;

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public LimitOperator(String name) {
        super(name, OperatorType.LIMIT);
    }


    private LimitOperator(PlanNode node) {
        super(node, OperatorType.LIMIT);
    }

    @JsonCreator
    public LimitOperator(@JsonProperty("name") String name, @JsonProperty("output") String output, @JsonProperty("limit") Limit limit) {
        super(name, OperatorType.LIMIT);
        setOutput(output);
        this.limit = limit;
    }

    @Override
    public String toString() {
        return getType() + " " + limit.getRowCount();
    }
}*/
