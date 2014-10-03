package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.LeadsNodeType;
import eu.leads.processor.nqe.NQEConstants;
import eu.leads.processor.nqe.operators.Operator;
import eu.leads.processor.nqe.operators.WGSOperator;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 9/23/14.
 */
public class OperatorFactory {
   public static Operator createOperator(Node com, InfinispanManager persistence, Action action) {
      Operator result = null;
      try {
         JsonObject actionData = action.getData();
         // read monitor q.getString("monitor");
         String operatorType = actionData.getString("operatorType");
         if (operatorType.equals(LeadsNodeType.WGS_URL.toString())) {//SQL Query
            result = new WGSOperator(com,persistence,action);

         }
         else if(operatorType.equals(NQEConstants.PPPQ_OPERATOR.toString())){

         }
         else{
            //SQL Operators
            result = SQLOperatorFactory.getOperator(com,persistence,action);
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
      return result;
   }
}
