package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.infinispan.LeadsMapper;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.math.FilterOperatorTree;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.distexec.mapreduce.Collector;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by vagvaz on 11/21/14.
 */
public class JoinMapper extends LeadsMapper<String,String,String,String> {

//   private String configString;
   transient private String tableName;
   transient InfinispanManager iimanager;
   public JoinMapper(String s,String outputCacheName) {
      super(s);
   }

   @Override
   public void map(String key,String value, Collector<String,String> collector) {
      if(!isInitialized)
         initialize();
//      conf = new JsonObject(configString);
      System.out.println("k " + key + " " + value);
      CloseableIterable<Map.Entry<String, String>> iterable = null;
      Cache cache = (Cache) iimanager.getPersisentCache(key);
      iterable =
              cache.getAdvancedCache().filterEntries(new AcceptAllFilter());
      for (Map.Entry<String, String> outerEntry : iterable) {
         Tuple t = new Tuple(outerEntry.getValue());
        String outkey = getOutkey(conf,t);
         t.setAttribute("TableId",tableName);
         String tkey = outerEntry.getKey().substring(outerEntry.getKey().indexOf(":")+1,outerEntry.getKey().length());
         t.setAttribute("tupleKey",tkey);
         System.out.println(key +  " outkey " + outkey + " " + t.asJsonObject().toString());
         collector.emit(outkey,t.asString());
      }
      iterable.close();
      System.out.println(tableName);
      tableName = null;
   }

   private String getOutkey(JsonObject conf, Tuple t) {
      String result ="";
      if(tableName == null)
         resolveTableName(t);
      JsonArray array = conf.getArray(tableName);
      Iterator<Object> iterator = array.iterator();
      while(iterator.hasNext()){
         String attName = (String) iterator.next();
         result += t.getGenericAttribute(attName).toString();
      }
      return result;
   }

   private void resolveTableName(Tuple t) {
      for(String f : t.getFieldNames()){
         int index = f.lastIndexOf(".");
         if(index < 0 ) continue;
         String candidate = f.substring(0,f.lastIndexOf("."));
         if(conf.containsField(candidate))
         {
            tableName = candidate;
            return;
         }
      }
   }

   public void initialize(){
      super.initialize();
      isInitialized = true;
      iimanager = InfinispanClusterSingleton.getInstance().getManager();
      conf = getJoinColumns(conf);
   }

   private JsonObject getJoinColumns(JsonObject conf) {
      JsonObject result = conf;
      JsonObject joinQual = conf.getObject("body").getObject("joinQual");
      FilterOperatorTree tree = new FilterOperatorTree(joinQual);
      Map<String,List<String>> joinColumsn = tree.getJoinColumns();
      for(Map.Entry<String, List<String>> j : joinColumsn.entrySet()){
         result.putArray(j.getKey(), new JsonArray(j.getValue()));
      }
      return conf;
   }
}
