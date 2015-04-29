package eu.leads.processor.infinispan.operators;

import eu.leads.processor.core.Tuple;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.infinispan.LeadsReducer;
import org.vertx.java.core.json.JsonObject;

import java.util.*;

/**
 * Created by vagvaz on 11/21/14.
 */
public class JoinReducer extends LeadsReducer<String,Tuple> {
//   transient JsonObject conf;
//   String configString;
   private transient String prefix;
   public JoinReducer(String s) {
      super(s);
      configString = s;
   }

   @Override
   public void initialize() {
      super.initialize();
      isInitialized = true;
      conf = new JsonObject(configString);
      prefix = outputCacheName+":";
//      prefix = outputCacheName+":";
//      outputCache = (Cache) InfinispanClusterSingleton.getInstance().getManager().getPersisentCache(conf.getString("output"));

   }

   @Override
   public void reduce(String reducedKey, Iterator<Tuple> iter,LeadsCollector collector) {
    if(!isInitialized)
       initialize();
      Map<String,List<Tuple>> relations = new HashMap<>();
      while(iter.hasNext()){
//         String jsonTuple = iter.next();
//         Tuple t = new Tuple(jsonTuple);
        Tuple t = null;
        Object c = iter.next();
        if(c instanceof Tuple )
          t = (Tuple)c;
        else{
          continue;
        }
         String table = t.getAttribute("__table");
         t.removeAttribute("__table");
         List<Tuple> tuples = relations.get(table);
         if(tuples == null){
            tuples = new ArrayList<>();
         }
         tuples.add(t);
         relations.put(table,tuples);
      }
      if(relations.size() < 2)
         return;
      ArrayList<List<Tuple>> arrays = new ArrayList<>(2);
      for(List<Tuple> a : relations.values()){
         arrays.add(a);
      }

      for(int i = 0; i < arrays.get(0).size(); i++){
         Tuple outerTuple = arrays.get(0).get(i);
         String outerKey = outerTuple.getAttribute("__tupleKey");
         for(int j = 0; j <  arrays.get(1).size(); j++){
            Tuple innerTuple = arrays.get(1).get(j);
            outerTuple.removeAttribute("tupleKey");
            String outerKey2 = innerTuple.getAttribute("__tupleKey");
            Tuple resultTuple = new Tuple(innerTuple, outerTuple,null);
            resultTuple.removeAttribute("tupleKey");
            String combinedKey = prefix + outerKey + "-" + outerKey2;
            resultTuple = prepareOutput(resultTuple);
            resultTuple = prepareOutput(resultTuple);
//            outputCache.put(combinedKey, resultTuple.asJsonObject().toString());
            collector.emit(prefix+combinedKey,resultTuple);
         }
      }
      return ;
   }
}
