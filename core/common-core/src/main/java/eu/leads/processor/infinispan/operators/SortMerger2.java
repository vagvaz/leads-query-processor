package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.TupleComparator;
import org.apache.commons.collections.iterators.ReverseListIterator;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.*;

/**
 * Created by vagvaz on 11/24/14.
 */
public class SortMerger2 {
   //    private Map<String, String> input;
//    private String output;
   private final String prefix;
   private final Map<String, String> outputCache;
   private Vector<Integer> counters;
   private List<Tuple> values;
   private Vector<String> keys;
   private Vector<Map<String, String>> caches;
   private final TupleComparator comparator;
   private Vector<String> cacheNames;
   protected JsonObject inputSchema;
   protected JsonObject outputSchema;
   protected Map<String,String> outputMap;
   protected Map<String,JsonObject> targetsMap;
   InfinispanManager manager;
   private int batchSize = 10;
   private List<Tuple> batchTuples;
   private int perCache = 1;
   private long counter = 0;
   public SortMerger2(List<String> inputCaches, String output, TupleComparator comp, InfinispanManager manager, JsonObject conf) {

      prefix = output+":";
//        this.output = output;
//        input = inputMap;
      this.manager = manager;
      outputCache = manager.getPersisentCache(output);
      counters = new Vector<Integer>(inputCaches.size());
      values = new Vector<Tuple>(inputCaches.size());
      caches = new Vector<Map<String, String>>();
      cacheNames = new Vector<String>(inputCaches.size());
      keys = new Vector<String>(inputCaches.size());
      comparator = comp;
      batchTuples = new ArrayList<>(batchSize);
      for (String entry : inputCaches) {
         Cache cache  = (Cache) manager.getPersisentCache((entry));
         counters.add(0);
         keys.add(entry);
         caches.add(cache);
         Tuple t = getCurrentValue(keys.size() - 1);
         if (t == null) {
            counters.remove(counters.size()-1);
            caches.remove(caches.size()-1);
            manager.removePersistentCache(entry);
            keys.remove(keys.size()-1);
            continue;
         }
         values.add(t);
         cacheNames.add(entry);
      }
      perCache = batchSize / caches.size();
   }

   private Tuple getCurrentValue(int cacheIndex) {
      String key = keys.get(cacheIndex);
      Integer counter = counters.get(cacheIndex);
      String tmp = caches.get(cacheIndex).get(key  + counter.toString());
      if(tmp == null || tmp.equals(""))
         return null;
      return new Tuple(tmp);
   }

   private Tuple getNextValue(int cacheIndex) {
      String key = keys.get(cacheIndex);
      Integer counter = counters.get(cacheIndex);
      counter = counter + 1;
      String tmp = caches.get(cacheIndex).get(key +  counter.toString());
//      if (tmp == null) {
//         counters.remove(cacheIndex);
//         caches.remove(cacheIndex);
//         manager.removePersistentCache(cacheNames.elementAt(cacheIndex));
//         cacheNames.removeElementAt(cacheIndex);
//         keys.remove(cacheIndex);
//         values.remove(cacheIndex);
//         return null;
//      }
      counters.set(cacheIndex, counter);
//        String tmp = caches.get(cacheIndex).get(key +  counter.toString());
      if(tmp!= null && !tmp.equals(""))
         return new Tuple(tmp);
      else
         return null;
   }

   public void merge() {
//      Tuple nextValue = null;
//      Tuple t = null;
//      long counter = 0;
//      while (caches.size() > 0) {
//         int minIndex = findMinIndex(values);
//
//         t = values.get(minIndex);
////            t = prepareOutput(t);
//         outputCache.put(prefix + counter, t.asString());
//         counter++;
//         nextValue = getNextValue(minIndex);
//         if (nextValue != null)
//            values.set(minIndex, nextValue);
//      }
//      counters.clear();
//      counters = null;
//      for(String cache : keys){
//         manager.removePersistentCache(cache);
//      }
//      keys.clear();
//      keys = null;
//      values.clear();
//      values = null;
//      cacheNames.clear();
//      cacheNames = null;
//      for (Map<String, String> map : caches) {
//         map.clear();
//      }
//      caches.clear();
//      caches = null;
      Tuple nextValue = null;
      Tuple t = null;

      batchTuples.addAll(values);
      values.clear();
      while (caches.size()>0){
         batchTuples = readBatch(batchTuples);
         Collections.sort(batchTuples,comparator);
         values = readValues();

         processBatch(batchTuples, values);
         batchTuples.clear();

         batchTuples = new ArrayList<>(batchSize);
         batchTuples.addAll(values);
         values.clear();
      }
   }

   private void processBatch(List<Tuple> batchTuples, List<Tuple> values) {
      if(values.size() == 0){
         splitTuples(batchTuples);
         return;
      }
      Tuple nextValue = null;
      while( batchTuples.size() > 0 && values.size() > 0 ){
         long oldcounter = counter;
         int minIndex = findMinIndex(values);
         //         outputCache.put(prefix + counter, t.asString());
         Tuple currentTuple = values.get(minIndex);
         int cmp = comparator.compare(batchTuples.get(0), values.get(minIndex));
         while (cmp < 0) {

            Tuple t = batchTuples.remove(0);
            outputCache.put(prefix + counter, t.asString());
            if(batchTuples.size() == 0){
               return;
            }
            counter++;
            cmp = comparator.compare(batchTuples.get(0), values.get(minIndex));
         }
         if(oldcounter == counter){

            Tuple t = currentTuple;
            outputCache.put(prefix + counter, t.asString());
            counter++;
         }
         nextValue = getNextValueWithCleanUp(minIndex);
         if (nextValue != null)
            values.set(minIndex, nextValue);
      }
      if(values.size() == 0) {
         splitTuples(batchTuples);
      }
   }

   private Tuple getNextValueWithCleanUp(int cacheIndex) {
      String key = keys.get(cacheIndex);
      Integer counter = counters.get(cacheIndex);
      counter = counter + 1;
      String tmp = caches.get(cacheIndex).get(key +  counter.toString());
      if (tmp == null) {
         counters.remove(cacheIndex);
         caches.remove(cacheIndex);
         manager.removePersistentCache(cacheNames.elementAt(cacheIndex));
         cacheNames.removeElementAt(cacheIndex);
         keys.remove(cacheIndex);
         values.remove(cacheIndex);
         return null;
      }
      counters.set(cacheIndex, counter);
//        String tmp = caches.get(cacheIndex).get(key +  counter.toString());
         return new Tuple(tmp);
   }

   private void splitTuples(List<Tuple> batchTuples) {
      for (Tuple t : batchTuples){
//            t = prepareOutput(t);
         outputCache.put(prefix + counter, t.asString());
         counter++;
      }
   }

   private List<Tuple> readBatch(List<Tuple> currentBatch) {
      Tuple nextValue = null;
      List<Integer> tobeRemoved = new ArrayList<>();
      List<Tuple> result = new ArrayList<>(batchSize);
      result.addAll(currentBatch);
      for(int cache = 0; cache < caches.size(); cache++) {
         for (int i = 0; i < perCache; i++) {
            nextValue = getNextValue(cache);
            if(nextValue == null){
               tobeRemoved.add(cache);
               break;
            }
            result.add(nextValue);
         }
      }
      ListIterator<Integer> iterator = tobeRemoved.listIterator(tobeRemoved.size());
      while(iterator.hasPrevious()){
         removeCache(iterator.previous());
      }
      return result;
   }

   private List<Tuple> readValues() {
      int removed = 0;
      List<Tuple> result = new ArrayList<>(caches.size());
      for (int cache = 0; cache + removed < caches.size(); cache++) {
         Tuple t = getCurrentValue(cache);
         if (t == null) {
            counters.remove(cache);
            caches.remove(cache);
            manager.removePersistentCache(cacheNames.elementAt(cache));
            cacheNames.remove(cache);
            keys.remove(cache);
            removed++;
            cache--;
            continue;
         }
         result.add(t);
      }
      return result;
   }

   private void removeCache(int cacheIndex) {
         counters.remove(cacheIndex);
         caches.remove(cacheIndex);
         manager.removePersistentCache(cacheNames.elementAt(cacheIndex));
         cacheNames.removeElementAt(cacheIndex);
         keys.remove(cacheIndex);
//         values.remove(cacheIndex);
   }

   private int findMinIndex(List<Tuple> values) {
      int result = 0;
      Tuple curMin = values.get(0);
      for (int i = 1; i < values.size(); i++) {
         int cmp = comparator.compare(curMin, values.get(i));
         if (cmp > 0) {
            curMin = values.get(i);
            result = i;
         }

      }
      return result;

   }

//   protected Tuple prepareOutput(Tuple tuple){
//      if(outputSchema.toString().equals(inputSchema.toString())){
//         return tuple;
//      }
//      JsonObject result = new JsonObject();
//      List<String> toRemoveFields = new ArrayList<String>();
//      Map<String,String> toRename = new HashMap<String,String>();
//      for (String field : tuple.getFieldNames()) {
//         JsonObject ob = targetsMap.get(field);
//         if (ob == null)
//            toRemoveFields.add(field);
//         else {
//            toRename.put(field, ob.getObject("column").getString("name"));
//         }
//      }
//      tuple.removeAtrributes(toRemoveFields);
//      tuple.renameAttributes(toRename);
//      return tuple;
//   }
}
