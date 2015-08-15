package eu.leads.processor.core;

import com.sleepycat.persist.model.*;

/**
 * Created by vagvaz on 8/14/15.
 */
@Entity
public class TupleWrapper {
    @PrimaryKey
    String id;
    @SecondaryKey(relate= Relationship.MANY_TO_ONE)
    String key;
    private Tuple tuple;

    public TupleWrapper(){}

    public TupleWrapper(String key , int counter, Tuple tuple){
        id = key+counter;
        this.key = key;
        this.tuple = tuple;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Tuple getTuple() {
        return tuple;
    }

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }

    @Override public String toString() {
        return "TupleWrapper{" +
            "key='" + key + '\'' +
            ", tuple=" + tuple +
            '}';
    }
}
