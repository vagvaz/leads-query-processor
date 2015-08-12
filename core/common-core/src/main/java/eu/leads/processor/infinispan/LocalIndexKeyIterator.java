package eu.leads.processor.infinispan;

import org.infinispan.Cache;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by vagvaz on 16/07/15.
 */
public class LocalIndexKeyIterator implements Iterator<Object> {
    String key;
    Integer numberOfValues;
    Integer currentCounter;
    Cache<String,Object> dataCache;
    Future<Object> nextResult;
    public LocalIndexKeyIterator(String key, Integer counter,Map<String,Object> dataCache) {
        this.currentCounter = 0;
        this.numberOfValues = counter;
        this.key = key;
        this.dataCache  = (Cache<String, Object>) dataCache;
        nextResult = ((Cache<String, Object>) dataCache).getAsync(key+currentCounter);
    }


    @Override public boolean hasNext() {
        if(currentCounter <= numberOfValues) {
            return true;
        }
        return false;
    }

    @Override public Object next() {
        if(currentCounter <= numberOfValues){
            Object result = null;
            try {
                result = nextResult.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            currentCounter++;
            nextResult = dataCache.getAsync(key+currentCounter);
            return result;
            //            }
            //            throw new NoSuchElementException("LocalIndexIterator GOT NULL VALUE for  key " + key + " currentCounter " + currentCounter + " maximum " + numberOfValues);
        }
        throw new NoSuchElementException("LocalIndexIterator key " + key + " currentCounter " + currentCounter + " maximum " + numberOfValues);
    }

    @Override public void remove() {
        System.err.println("SHOULD NOT BE USED");
        //        if(currentCounter > 0) {
        //            dataCache.remove(key + (currentCounter - 1));
        //        }
    }
}
