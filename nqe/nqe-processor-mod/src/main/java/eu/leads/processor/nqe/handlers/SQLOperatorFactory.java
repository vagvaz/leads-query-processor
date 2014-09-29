package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.LeadsNodeType;
import eu.leads.processor.nqe.operators.*;
import eu.leads.processor.nqe.operators.mapreduce.IntersectOperator;
import eu.leads.processor.nqe.operators.mapreduce.ScanOperator;
import eu.leads.processor.nqe.operators.mapreduce.UnionOperator;

/**
 * Created by vagvaz on 9/23/14.
 */
public class SQLOperatorFactory {
   public static Operator getOperator(Node com, InfinispanManager persistence, Action action) {
      Operator result = null;
      String implemenationType = action.getData().getString("implementation");
      LeadsNodeType operatorType = LeadsNodeType.valueOf(action.getData().getString("operatorType"));
      switch(operatorType){
         case ROOT:
            break;
         case EXPRS:
            break;
         case PROJECTION:
            result = new ProjectOperator(com,persistence,action);
            break;
         case LIMIT:
            result = new LimitOperator(com,persistence,action);
            break;
         case SORT:
            result = new SortOperator(com,persistence,action);
            break;
         case HAVING:
            result = new FilterOperator(com,persistence,action);
            break;
         case GROUP_BY:
            result = new GroupByOperator(com,persistence,action);
            break;
         case WINDOW_AGG:
            break;
         case SELECTION:
            result = new FilterOperator(com,persistence,action);
            break;
         case JOIN:
            result = new JoinOperator(com,persistence,action);
            break;
         case UNION:
            result = new UnionOperator(com,persistence,action);
            break;
         case EXCEPT:
            break;
         case INTERSECT:
            result = new IntersectOperator(com,persistence,action);
            break;
         case TABLE_SUBQUERY:
            break;
         case SCAN:
            result = new ScanOperator(com,persistence,action);
            break;
         case PARTITIONS_SCAN:
            break;
         case BST_INDEX_SCAN:
            break;
         case STORE:
            break;
         case INSERT:
            break;
         case DISTINCT_GROUP_BY:
            break;
         case CREATE_DATABASE:
            break;
         case DROP_DATABASE:
            break;
         case CREATE_TABLE:
            break;
         case DROP_TABLE:
            break;
         case ALTER_TABLESPACE:
            break;
         case ALTER_TABLE:
            break;
         case TRUNCATE_TABLE:
            break;
         case WGS_URL:
            break;
         case OUTPUT_NODE:
            break;
      }
      return result;
   }
}
