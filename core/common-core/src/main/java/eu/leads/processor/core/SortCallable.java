package eu.leads.processor.core;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * Created by vagvaz on 9/24/14.
 */
public class SortCallable<K,V> implements

   DistributedCallable<K, V, String>, Serializable {
   transient private Cache<K,V> cache;
   private String[] sortColumns;
   private String[] types;
   private Boolean[] asceding;
   private Integer[] sign;
   transient  private Set<K> keys;
   transient private Cache out;
   private String output;
   transient String address;
   public SortCallable(String[] sortColumns,Boolean[] ascending, String[] types,String output){
      this.sortColumns = sortColumns;
      this.asceding = ascending;
      this.types = types;
      this.output = output;
   }

   @Override
   public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
      this.cache = cache;
      keys = inputKeys;
      address = this.cache.getCacheManager().getAddress().toString();
      out = this.cache.getCacheManager().getCache(address+":");
   }

   @Override
   public String call() throws Exception {
      ArrayList<Tuple> tuples =  new ArrayList<Tuple>();
      for(V value : cache.values())
      {
         tuples.add(new Tuple((String)value));
      }
      Comparator<Tuple> comparator = new TupleComparator(sortColumns,asceding,types);
      Collections.sort(tuples, comparator);
      int counter = 0;
      for (Tuple t : tuples) {
         out.put(address + ":" + counter, t.asString());
         counter++;
      }
      tuples.clear();
      return out.getName();
   }
}
