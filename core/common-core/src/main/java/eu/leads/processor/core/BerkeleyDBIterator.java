package eu.leads.processor.core;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.SecondaryIndex;

import java.util.Iterator;

/**
 * Created by vagvaz on 8/14/15.
 */
public class BerkeleyDBIterator implements Iterator<Object> {
    EntityCursor<TupleWrapper> cursor;
    SecondaryIndex sindex;
    String key;
    public BerkeleyDBIterator(SecondaryIndex<String, String, TupleWrapper> secondaryIndex,String key) {
        sindex = secondaryIndex;
        this.key = key;
        initialize(key);
    }

    public void initialize(String key){
        this.key= key;
        try {
            cursor = sindex.subIndex(key).entities();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
    @Override public boolean hasNext() {
        try {
            boolean result = (cursor.current() != null);
            return result;
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override public Object next() {
        try {
            TupleWrapper wrapper = cursor.next();
            Object result = wrapper.getTuple();
            return result;
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public void remove() {

    }
}
