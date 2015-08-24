package eu.leads.processor.infinispan;

import eu.leads.processor.core.EngineUtils;

/**
 * Created by vagvaz on 8/19/15.
 */
public class ExecuteRunnable implements Runnable {
    Object key;
    Object value;
    private LeadsBaseCallable callable;

    public void setKeyValue(Object key, Object value,LeadsBaseCallable callable){
        this.key = key;
        this.value = value;
        this.callable = callable;
    }
    @Override public void run() {
        callable.executeOn(key,value);
        EngineUtils.addRunnable(this);
    }
}
