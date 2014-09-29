package eu.leads.processor.web;

import eu.leads.processor.core.DataType;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vagvaz on 3/7/14.
 */
public class QueryResults  extends DataType {

    public QueryResults() {
     super();
        setId("");
        setMax(-1);
        setMin(-1);
        setResult(new ArrayList<String>());

    }



    public QueryResults(String queryId) {
        setId(queryId);
        setMax(-1);
        setMin(-1);
        setResult(new ArrayList<String>());

    }

    public QueryResults(JsonObject jsonObject) {
        super(jsonObject);
    }

    public String getId() {
        return data.getString("id");
    }

    public void setId(String id) {
        data.putString("id",id);
    }

    public long getMin() {
        return data.getLong("min");
    }

    public void setMin(long min) {
        data.putNumber("min",min);
    }

    public long getMax() {
        return data.getLong("max");
    }

    public void setMax(long max) {
        data.putNumber("max",max);
    }

    public List<String> getResult() {
        List<String> result = new ArrayList<String>();
        JsonArray array = new JsonArray(data.getString("result"));

        Iterator<Object> iterator = array.iterator();
        while(iterator.hasNext()){
            result.add((String) iterator.next());
        }
        return result;
    }

    public void setResult(List<String> result) {

        JsonArray resultArray = new JsonArray();

        for(String t : result){
            resultArray.add(t);
        }
        data.putArray("result",resultArray);
    }

    public String getMessage() {
        return data.getString("message");
    }

    public void setMessage(String message) {
        data.putString("message",message);
    }

    @Override
    public String toString() {
       return  data.encodePrettily();
    }

    public long getSize() {
      return  data.getLong("size");
    }

    public void setSize(long size) {
        data.putNumber("size",size);
    }
}
