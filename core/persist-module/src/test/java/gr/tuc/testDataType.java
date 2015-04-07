package gr.tuc;

import eu.leads.processor.core.plan.QueryState;
import eu.leads.processor.core.plan.QueryStatus;
import eu.leads.processor.core.plan.ReadStatus;
import eu.leads.processor.core.plan.SQLQuery;

/**
 * Created by vagvaz on 8/4/14.
 */
public class testDataType {
    public static void main(String[] args) {
        SQLQuery query = new SQLQuery("testuser", " test sql");
        query.setId("foobar");
        query.setQueryStatus(new QueryStatus("", QueryState.PENDING, ""));
        ReadStatus read = query.getReadStatus();
        read.setMin(0);
        read.setMax(10);
        read.setSize(1000);
        read.setReadFully(true);
        query.setReadStatus(read);

        System.out.println(query.toString());
        System.out.println(read.toString());
    }
}
