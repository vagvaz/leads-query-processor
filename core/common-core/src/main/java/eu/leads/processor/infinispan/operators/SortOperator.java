package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.TupleComparator;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.LeadsMapper;
import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.distexec.DistributedTask;
import org.infinispan.distexec.DistributedTaskBuilder;
import org.infinispan.remoting.transport.Address;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 10/29/13
 * Time: 1:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class SortOperator extends BasicOperator {
//    List<Column> columns;
   transient protected String[] sortColumns;
   transient protected Boolean[] asceding;
   transient protected String[] types;
   transient  protected long rowcount = Long.MAX_VALUE;


   public long getRowcount() {
      return rowcount;
   }


    private LeadsMapper<String,String,String,String> mapper;


   public SortOperator(Node com, InfinispanManager persistence,LogProxy log, Action action) {
      super(com, persistence,log, action);
      JsonArray sortKeys = conf.getObject("body").getArray("sortKeys");
      Iterator<Object> sortKeysIterator = sortKeys.iterator();
      sortColumns = new String[sortKeys.size()];
      asceding = new Boolean[sortKeys.size()];
      types = new  String[sortKeys.size()];
      int counter = 0;
      while(sortKeysIterator.hasNext()){
         JsonObject sortKey = (JsonObject) sortKeysIterator.next();
         sortColumns[counter] = sortKey.getObject("sortKey").getString("name");
         asceding[counter] = sortKey.getBoolean("ascending");
         types[counter] = sortKey.getObject("sortKey").getObject("dataType").getString("type");
         counter++;
      }
      if(conf.containsField("limit")){
         rowcount = conf.getObject("limit").getObject("body").getLong("fetchFirstNum");
      }
   }

   @Override
    public void init(JsonObject config) {
//        super.init(config); //fix set correctly caches names
        //fix configuration
       init_statistics(this.getClass().getCanonicalName());
    }

   @Override
   public void run() {
       long startTime = System.nanoTime();
      Cache inputCache = (Cache) this.manager.getPersisentCache(getInput());
      Cache beforeMerge = (Cache)this.manager.getPersisentCache(getOutput()+".merge");
      Cache outputCache = (Cache) manager.getPersisentCache(getOutput());
      DistributedExecutorService des = new DefaultExecutorService(inputCache);
     String prefix = UUID.randomUUID().toString();
      SortCallableUpdated<String,Tuple> callable = new SortCallableUpdated(sortColumns,asceding,types,getOutput()+".merge",prefix);
//      SortCallable callable = new SortCallable(sortColumns,asceding,types,getOutput()+".merge",UUID.randomUUID().toString());
      DistributedTaskBuilder builder = des.createDistributedTaskBuilder( callable);
      builder.timeout(1, TimeUnit.HOURS);
      DistributedTask task = builder.build();
      for(Address cacheNodes : inputCache.getAdvancedCache().getRpcManager().getMembers()){
        String tmpCacheName = prefix+"."+cacheNodes.toString();
        manager.getPersisentCache(tmpCacheName);
      }
      List<Future<String>> res = des.submitEverywhere(task);
      List<String> addresses = new ArrayList<String>();
      try {
         if (res != null) {
            for (Future<?> result : res) {
               addresses.add((String) result.get());
            }
            System.out.println("sort callable  Execution is done on " + addresses.get(addresses.size()-1));
         }
         else
         {
            System.out.println("sort callable Execution not done");
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (ExecutionException e) {
         e.printStackTrace();
      }
//      Merge outputs
      TupleComparator comparator = new TupleComparator(sortColumns,asceding,types);
      SortMerger2 merger = new SortMerger2(addresses, getOutput(),comparator,manager,conf,getRowcount());
      merger.merge();
//       Cache outputCache = (Cache) manager.getPersisentCache(getOutput());
      for(String cacheName : addresses){
         manager.removePersistentCache(cacheName);
      }
      manager.removePersistentCache(beforeMerge.getName());
//Single
//      List<Tuple> tuples = new ArrayList<>();
//      String prefix = getOutput()+":";
//      try {
//         CloseableIterable<Map.Entry<String, String>> iterable =
//                 inputCache.getAdvancedCache().filterEntries(new AcceptAllFilter());
//         for (Map.Entry<String, String> entry : iterable) {
//
//
//            String valueString = (String) entry.getValue();
//            if (valueString.equals(""))
//               continue;
//            tuples.add(new Tuple(valueString));
//         }
//      }catch (Exception e){
//         e.printStackTrace();
//      }
//      Comparator<Tuple> comparator = new TupleComparator(sortColumns,asceding,types);
//      Collections.sort(tuples, comparator);
//      int counter = 0;
//      for (Tuple t : tuples) {
////         while(outputCache.size() != counter+1) {
//            outputCache.put(prefix + counter, t.asString());
////         }
//         counter++;
//      }
//      tuples.clear();
      cleanup();
      //Store Values for statistics
//      updateStatistics(inputCache.size(), manager.getPersisentCache(getOutput()).size(), System.nanoTime() - startTime);
       updateStatistics(inputCache,null,outputCache);
   }

   @Override
    public void execute() {  //Need Heavy testing
        super.execute();
    }

    @Override
    public void cleanup() {
       super.cleanup();

    }


    public Boolean[] getAscending() {
        return this.asceding;
    }

    public void setAscending(Boolean[] ascending) {
        this.asceding = ascending;
    }


/*
    List<Boolean> ascending;

    public SortOperator(String name) {
        super(name, OperatorType.SORT);
    }

    public SortOperator(PlanNode node) {
        super(node, OperatorType.SORT);
    }

    @JsonCreator
    public SortOperator(@JsonProperty("name") String name, @JsonProperty("output") String output, @JsonProperty("columns") List<Column> orderByColumns, @JsonProperty("asceding") List<Boolean> ascendingOrder) {
        super(name, OperatorType.SORT);
        setOutput(output);
        this.columns = orderByColumns;
        this.ascending = ascendingOrder;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(" ");
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getTable() != null)
                builder.append(columns.get(i).getWholeColumnName() + " " + (ascending.get(i) ? " ASC " : " DESC "));
            else
                builder.append(columns.get(i).getColumnName() + " " + (ascending.get(i) ? " ASC " : " DESC "));
        }
        return getType() + builder.toString();
    }
*/}
