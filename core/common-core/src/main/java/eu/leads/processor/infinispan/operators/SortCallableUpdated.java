package eu.leads.processor.infinispan.operators;

import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.TupleComparator;
import org.infinispan.Cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vagvaz on 2/20/15.
 */
public class SortCallableUpdated<K,V> extends  LeadsSQLCallable<K,V> {

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
    outputCache = (Cache) imanager.getPersisentCache(prefix+"."+address);

  }

  @Override public void executeOn(K key, V value) {


      String valueString = (String)value;
      if(valueString.equals("")){

      tuples.add(new Tuple(valueString));
    }

  }

  @Override public void finalize(){
    Comparator<Tuple> comparator = new TupleComparator(sortColumns,asceding,types);
    Collections.sort(tuples, comparator);
    int counter = 0;
    for (Tuple t : tuples) {
      outputCache.put(outputCache.getName()  + counter, t.asString());
      counter++;
    }
    tuples.clear();

  }
}
