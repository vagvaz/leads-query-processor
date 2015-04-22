package eu.leads.processor.web;

/**
 * Created by angelos on 27/03/15.
 */
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class MonitorQueryBody {

    String queryId;
    String websocketId;
    String username;
    String object;

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getWebsocketId() {
        return websocketId;
    }

    public void setWebsocketId(String websocketId) {
        this.websocketId = websocketId;
    }

    public String getUserName() {
        return websocketId;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return queryId + ":" + websocketId + ":" + username + " -> \n->" + object;
    }
}