package eu.leads.processor.infinispan.operators;

import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.TupleComparator;
import eu.leads.processor.infinispan.LeadsBaseCallable;
import org.infinispan.context.Flag;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vagvaz on 2/20/15.
 */
public class SortCallableUpdated<K,V> extends LeadsBaseCallable<K,V> {

  private String[] sortColumns;
  private String[] types;
  private Boolean[] asceding;
  private Integer[] sign;
  transient private List<Tuple> tuples;
  private String output;
  transient String address;
  private String prefix;

//  public SortCallableUpdated(String configString, String output){
    public SortCallableUpdated(String[] sortColumns, Boolean[] ascending, String[] types, String output,
                                String prefix){
      super("{}", output);
      this.sortColumns = sortColumns;
      this.asceding = ascending;
      this.types = types;
      this.output = output;
      this.prefix = prefix;
  }

  @Override public void initialize() {
    super.initialize();
    address = inputCache.getCacheManager().getAddress().toString();
//    outputCache = (Cache) imanager.getPersisentCache(prefix+"."+address);
    outputCache = emanager.getCache(prefix+"."+address);
    tuples = new ArrayList<>(100);
  }

  @Override public String call() throws Exception {
    if(!isInitialized){
      initialize();
    }
    final ClusteringDependentLogic cdl = inputCache.getAdvancedCache().getComponentRegistry().getComponent
                                                                                                (ClusteringDependentLogic.class);
    for(Object key : inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()) {
      if (!cdl.localNodeIsPrimaryOwner(key))
        continue;
      V value = inputCache.get(key);

      if (value != null) {
        executeOn((K)key, value);
      }
    }
    finalize();
    return outputCache.getName();
  }

  @Override public void executeOn(K key, V value) {


//      String valueString = (String)value;
//      if(!valueString.equals("")){
//
//      tuples.add(new Tuple(valueString));
    if(value != null ){
      tuples.add((Tuple) value);
    }

  }

  @Override public void finalize(){
    Comparator<Tuple> comparator = new TupleComparator(sortColumns,asceding,types);
    Collections.sort(tuples, comparator);
    int counter = 0;
    String prefix = outputCache.getName();
    for (Tuple t : tuples) {
      outputToCache(prefix  + counter, t);

//      outputCache.put(outputCache.getName()  + counter, t);
      counter++;
    }
    tuples.clear();
    super.finalize();
  }
}
