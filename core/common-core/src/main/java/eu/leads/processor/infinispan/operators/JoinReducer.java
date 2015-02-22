package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.infinispan.LeadsReducer;
import eu.leads.processor.core.Tuple;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.*;

/**
 * Created by vagvaz on 11/21/14.
 */
public class JoinReducer extends LeadsReducer<String,String> {
   transient Cache outputCache;
   transient JsonObject conf;
   String configString;
   transient  String prefix ;
   public JoinReducer(String s, String output) {
      super(s);
      configString = s;
   }

   @Override
   public void initialize() {
      super.initialize();
      isInitialized = true;
      conf = new JsonObject(configString);
      prefix = outputCacheName+":";
      outputCache = (Cache) InfinispanClusterSingleton.getInstance().getManager().getPersisentCache(conf.getString("output"));

   }

   @Override
   public String reduce(String reducedKey, Iterator<String> iter) {
    if(!isInitialized)
       initialize();
      Map<String,List<Tuple>> relations = new HashMap<>();
      while(iter.hasNext()){
         String jsonTuple = iter.next();
         Tuple t = new Tuple(jsonTuple);
         String table = t.getAttribute("TableId");
         t.removeAttribute("TableId");
         List<Tuple> tuples = relations.get(table);
         if(tuples == null){
            tuples = new ArrayList<>();
         }
         tuples.add(t);
         relations.put(table,tuples);
      }
      if(relations.size() < 2)
         return "";
      ArrayList<List<Tuple>> arrays = new ArrayList<>(2);
      for(List<Tuple> a : relations.values()){
         arrays.add(a);
      }

      for(int i = 0; i < arrays.get(0).size(); i++){
         Tuple outerTuple = arrays.get(0).get(i);
         String outerKey = outerTuple.getAttribute("tupleKey");
         for(int j = 0; j <  arrays.get(1).size(); j++){
            Tuple innerTuple = arrays.get(1).get(j);
            outerTuple.removeAttribute("tupleKey");
            String outerKey2 = innerTuple.getAttribute("tupleKey");
            Tuple resultTuple = new Tuple(innerTuple, outerTuple,null);
            resultTuple.removeAttribute("tupleKey");
            String combinedKey = prefix + outerKey + "-" + outerKey2;
            resultTuple = prepareOutput(resultTuple);
            resultTuple = prepareOutput(resultTuple);
            outputCache.put(combinedKey, resultTuple.asJsonObject().toString());
         }
      }
      return  "";
   }
}
