package eu.leads.processor.infinispan;

/**
 * Created by vagvaz on 8/19/15.
 */
public class ExecuteRunnable implements Runnable {
    Object key;
    Object value;
    LeadsBaseCallable callable;
    public ExecuteRunnable(LeadsBaseCallable callable){
        this.callable = callable;
    }

    public void setKeyValue(Object key, Object value){
        this.key = key;
        this.value = value;
    }
    @Override public void run() {
        callable.executeOn(key,value);
        callable.addRunnable(this);
    }
}
