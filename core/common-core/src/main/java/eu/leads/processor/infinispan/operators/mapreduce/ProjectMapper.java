package eu.leads.processor.infinispan.operators.mapreduce;

import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.LeadsMapper;
import eu.leads.processor.core.Tuple;
import org.infinispan.Cache;
import org.infinispan.distexec.mapreduce.Collector;
import org.vertx.java.core.json.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 11/4/13
 * Time: 8:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProjectMapper extends LeadsMapper<String, String, String, String> implements Serializable {
    transient private Cache<String, String> output = null;
    transient private String prefix = "";


   public ProjectMapper(JsonObject configuration) {
        super(configuration);
    }
   public ProjectMapper(String configString){super(configString);}
    transient protected InfinispanManager imanager;
    @Override
    public void initialize() {
        isInitialized = true;
        super.initialize();

        imanager = new ClusterInfinispanManager(manager);
        prefix = conf.getString("output") + ":";
        output = (Cache<String, String>) imanager.getPersisentCache(conf.getString("output"));
    }

    @Override
    public void map(String key, String value, Collector<String, String> collector) {
        if (!isInitialized)
            initialize();

        progress();
        String tupleId = key.substring(key.indexOf(':') + 1);
        Tuple projected = new Tuple(value);
        handlePagerank(projected);
        projected = prepareOutput(projected);
        output.put(prefix + tupleId, projected.asString());
    }

   protected Tuple prepareOutput(Tuple tuple){
      if(outputSchema.toString().equals(inputSchema.toString())){
         return tuple;
      }
      JsonObject result = new JsonObject();
      List<String> toRemoveFields = new ArrayList<String>();
      Map<String,String> toRename = new HashMap<String,String>();
      for (String field : tuple.getFieldNames()) {
         JsonObject ob = targetsMap.get(field);
         if (ob == null)
            toRemoveFields.add(field);
         else {
            toRename.put(field, ob.getObject("column").getString("name"));
         }
      }
      tuple.removeAtrributes(toRemoveFields);
      tuple.renameAttributes(toRename);
      return tuple;
   }
}
