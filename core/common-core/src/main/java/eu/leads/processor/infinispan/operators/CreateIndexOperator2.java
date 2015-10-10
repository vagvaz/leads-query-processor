package eu.leads.processor.infinispan.operators;

/**
 * Created by vagvaz on 10/26/14.
 */

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.apache.tajo.algebra.CreateIndex;
import org.apache.tajo.algebra.JsonHelper;
import org.apache.tajo.algebra.Projection;
import org.apache.tajo.algebra.Relation;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.Arrays;
import java.util.HashSet;


public class CreateIndexOperator2 extends BasicOperator {

  //transient protected EnsembleCacheManager emanager;
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
    //TODO CHECK CATALOG
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
    //ArrayList<Cache> sketchCaches= new ArrayList<>();
    //System.out.println(" TableName: " + tableName);
    if(!tableName.startsWith( StringConstants.DEFAULT_DATABASE_NAME))
     tableName = StringConstants.DEFAULT_DATABASE_NAME + "." + tableName;
    long count=0;
    for(String col:columns) {
       for(String node:nodes) {
         long c= (long) sketchesM.get(node + ":" + col + ":size"); //1 col
         System.out.println("Tuples on node: " + c);
         count +=c;
       }
      break;
    }
    for(String col:columns){
      Cache<Integer,Integer> tmp =(Cache) manager.getPersisentCache(tableName + "." + col + ".sketch");

      //sketchCaches.add(tmp);
      System.out.println("Creating DistCMSketch " + tableName + "." + col + ".sketch");

      int finalyArray [][];
      int w,d;
      String tmpnode = nodes.iterator().next();
      //nodes.remove(tmpnode);
      System.out.println("Getting node :" + tmpnode);
      w =  (int)sketchesM.get(tmpnode+":"+col+":w");
      d =  (int)sketchesM.get(tmpnode+":"+col+":d");
      finalyArray = new int[w][d];
     // int finalyArray [][]=(int[][]) sketchesM.get(tmpnode+":"+col+":array");
      System.out.println("Found nodes: " + nodes.size() + " " + Arrays.toString(nodes.toArray()));


      for(String node:nodes){
        System.out.println("Found node " + node);
        int [][] array= (int[][]) sketchesM.get(node+":"+col+":array");
        if(array!=null)
        for(int x=0;x<w;x++)
          for(int y=0;y<d;y++)
            finalyArray[x][y]+=array[x][y];
        else
          System.err.println("Unable to find array: " +node+":"+col+":array");
      }
      tmp.put(-1, w);
      tmp.put(-2, d);
      for(int x=0;x<w;x++)
        for(int y=0;y<d;y++)
          if(finalyArray[x][y]>-1)
            if(tmp.containsKey(y*w+x))
              tmp.put(y*w+x,tmp.get(y*w+x)+finalyArray[x][y]);
            else
              tmp.put(y*w+x, finalyArray[x][y]);

      System.out.println((nodes.size()+1)+" Sketches merged!");
    }


    sketchesM.clear();
    System.out.println("Keys removed");
    System.out.println("Overall Tuples Indexed: " + count + " on table "+ tableName);
    Cache<String,Long> TableSizeCache= (Cache) manager.getPersisentCache("TablesSize");
    TableSizeCache.put(tableName, count);

    super.cleanup();
  }

  @Override
  public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {

  }

  @Override public String getContinuousListenerClass() {
    return null;
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
