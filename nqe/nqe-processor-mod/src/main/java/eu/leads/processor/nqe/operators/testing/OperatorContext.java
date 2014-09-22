package eu.leads.processor.nqe.operators.testing;

//import eu.leads.processor.query.Query;
import org.vertx.java.core.json.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 9/22/13
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class OperatorContext {
    String user;
    JsonObject query;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public JsonObject getQuery() {
        return query;
    }

    public void setQuery(JsonObject query) {
        this.query = query;
    }
}
