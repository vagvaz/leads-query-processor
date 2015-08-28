package eu.leads.processor.infinispan.operators;

/**
 * Created by vagvaz on 10/26/14.
 */

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.math.FilterOperatorNode;
import eu.leads.processor.math.FilterOperatorTree;
import eu.leads.processor.math.MathUtils;
import org.apache.hadoop.util.hash.Hash;
import org.apache.tajo.algebra.CreateIndex;
import org.apache.tajo.algebra.JsonHelper;
import org.apache.tajo.algebra.Projection;
import org.apache.tajo.algebra.Relation;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIteratorSet;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.*;


public class CreateIndexOperator2 extends BasicOperator {

  transient protected EnsembleCacheManager emanager;
  String tableName;

  public CreateIndexOperator2(Node com, InfinispanManager persistence, LogProxy log, Action action) {
    super(com, persistence, log, action);
    if(this.action.getData().getObject("operator").getString("id")==null){
      System.out.println("Create Index Action: " + this.action.toString());
      this.action.getData().getObject("operator").putString("id","Cindex");
    }
  }

  @Override
  public void init(JsonObject config) {
    String CreateIndexJ = conf.getString("rawquery");
    CreateIndex newExpr = JsonHelper.fromJson(CreateIndexJ, CreateIndex.class);
    tableName = (((Relation) ((Projection) newExpr.getChild()).getChild())).getName();
    inputCache = (Cache) manager.getPersisentCache(tableName);

  }

  @Override
  public void cleanup() {
    System.out.println("Merging Sketches");
    Cache<String,Object> sketchesM= (Cache) manager.getPersisentCache("sketchMerge");
    HashSet<String> columns=new HashSet<>();
    HashSet<String> nodes=new HashSet<>();
    HashSet<String> value=new HashSet<>();
    for (String key : sketchesM.getAdvancedCache().keySet()) {
      String keys[] = key.split(":");
      nodes.add(keys[0]);
      columns.add(keys[1]);
      value.add(keys[2]);
    }
    System.out.println("Found nodes:" + nodes.size() + " " + Arrays.toString(nodes.toArray()) );
    System.out.println("Found columns:" + columns.size() + " " + Arrays.toString(columns.toArray()) );
    System.out.println("Found value:" + value.size() + " " + Arrays.toString(value.toArray()) );
    ArrayList<Cache> sketchCaches= new ArrayList<>();

    for(String col:columns){
      Cache<Integer,Integer> tmp =(Cache) manager.getPersisentCache(tableName + "." + col + ".sketch");
      sketchCaches.add(tmp);
      log.info("Creating DistCMSketch " + tableName + "." + col + ".sketch");

      int array [][]=null;
      for(String node:nodes){
        int w =  (int)sketchesM.get(node+":"+col+":w");
        int d =  (int)sketchesM.get(node+":"+col+":d");
        System.out.println("Found node" + node );
        array= (int[][]) sketchesM.get(node+":"+col+":array");

        tmp.put(-1,w);
        tmp.put(-2,d);

        for(int x=0;x<w;x++)
          for(int y=0;y<d;y++)
            if(array[x][y]>-1)
              if(tmp.containsKey(y*w+x))
                tmp.put(y*w+x,tmp.get(y*w+x)+array[x][y]);
              else
                tmp.put(y*w+x, array[x][y]);

      }
      System.out.println(nodes.size()+" Sketches merged");
      for (String key : sketchesM.getAdvancedCache().keySet()) {
        sketchesM.remove(key);
      }
      System.out.println("Keys removed");
    }

    super.cleanup();
  }

  @Override
  public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {

  }

  @Override
  public void setupMapCallable() {
     mapperCallable = new CreateIndexCallable<>(conf.toString(),getOutput());
  }

  @Override
  public void setupReduceCallable() {

  }

  @Override
  public boolean isSingleStage() {
    return true;
  }

}