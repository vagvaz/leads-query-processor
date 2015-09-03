package eu.leads.processor.infinispan;

import eu.leads.processor.common.infinispan.*;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.index.*;
import eu.leads.processor.core.EngineUtils;
import eu.leads.processor.math.FilterOpType;
import eu.leads.processor.math.FilterOperatorNode;
import eu.leads.processor.math.FilterOperatorTree;
import eu.leads.processor.math.MathUtils;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.context.Flag;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.filter.KeyValueFilter;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
import java.util.*;

/**
 * Created by vagvaz on 2/18/15.
 */
public  abstract class LeadsBaseCallable <K,V> implements LeadsCallable<K,V>,

  DistributedCallable<K, V, String>, Serializable {
  protected String configString;
  protected String output;
  transient protected JsonObject conf;
  transient protected boolean isInitialized;
  transient protected EmbeddedCacheManager embeddedCacheManager;
  transient protected InfinispanManager imanager;
  transient protected Set<K> keys;
  transient protected  Cache<K,V> inputCache;
  transient protected EnsembleCache outputCache;
  protected String ensembleHost;
  transient protected Object luceneKeys;
  transient protected HashMap<String,Cache> indexCaches=null;
  transient protected FilterOperatorTree tree;

  //  transient protected RemoteCache outputCache;
//  transient protected RemoteCache ecache;
//  transient protected RemoteCacheManager emanager;
  transient protected EnsembleCacheManager emanager;
  transient protected EnsembleCache ecache;
  transient Logger profilerLog;
  protected ProfileEvent profCallable;
  public LeadsBaseCallable(String configString, String output){
    this.configString = configString;
    this.output = output;
    profilerLog  = LoggerFactory.getLogger("###PROF###" +  this.getClass().toString());
    profCallable = new ProfileEvent("Callable Construct" + this.getClass().toString(),profilerLog);
  }


  public String getEnsembleHost() {
    return ensembleHost;
  }

  public void setEnsembleHost(String ensembleHost) {
    this.ensembleHost = ensembleHost;
  }


//  public static RemoteCacheManager createRemoteCacheManager() {
//    ConfigurationBuilder builder = new ConfigurationBuilder();
//    builder.addServer().host(LQPConfiguration.getConf().getString("node.ip")).port(11222);
//    return new RemoteCacheManager(builder.build());
//  }
  @Override public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
    profilerLog  = LoggerFactory.getLogger("###PROF###" +  this.getClass().toString());
    profCallable.setProfileLogger(profilerLog);
    if(profCallable!=null) {
      profCallable.end("setEnv");
      profCallable.start("setEnvironment Callable ");
    }else
      profCallable = new ProfileEvent("setEnvironment Callable " + this.getClass().toString(),profilerLog);
    embeddedCacheManager = cache.getCacheManager();
    imanager = new ClusterInfinispanManager(embeddedCacheManager);
//    outputCache = (Cache) imanager.getPersisentCache(output);
    keys = inputKeys;
    this.inputCache = cache;
    ProfileEvent tmpprofCallable = new ProfileEvent("setEnvironment manager " + this.getClass().toString(),profilerLog);
    tmpprofCallable.start("Start LQPConfiguration");

    LQPConfiguration.initialize();
    tmpprofCallable.end();
    EnsembleCacheUtils.initialize();
    if(ensembleHost != null && !ensembleHost.equals("")) {
      tmpprofCallable.start("Start EnsemlbeCacheManager");
      profilerLog.error("EnsembleHost EXIST " + ensembleHost);
      System.err.println("EnsembleHost EXIST " + ensembleHost);
      emanager = new EnsembleCacheManager(ensembleHost);
      EnsembleCacheUtils.initialize(emanager);
//      emanager.start();
//      emanager = createRemoteCacheManager();
//      ecache = emanager.getCache(output,new ArrayList<>(emanager.sites()),
//          EnsembleCacheManager.Consistency.DIST);
    }
    else {
      profilerLog.error("EnsembleHost NULL");
      System.err.println("EnsembleHost NULL");
      tmpprofCallable.start("Start EnsemlbeCacheManager");
      emanager = new EnsembleCacheManager(LQPConfiguration.getConf().getString("node.ip") + ":11222");
      EnsembleCacheUtils.initialize(emanager);
//      emanager.start();
//            emanager = createRemoteCacheManager();
    }
    emanager.start();

    tmpprofCallable.end();
    tmpprofCallable.start("Get cache ");
    ecache = emanager.getCache(output,new ArrayList<>(emanager.sites()),
          EnsembleCacheManager.Consistency.DIST);
    tmpprofCallable.end();
      outputCache = ecache;
//outputCache =  emanager.getCache(output,new ArrayList<>(emanager.sites()),
//          EnsembleCacheManager.Consistency.DIST);

    initialize();
    profCallable.end("end_setEnv");

//    long start = System.currentTimeMillis();
//    executor = new ThreadPoolExecutor(threadBatch,5*threadBatch,5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
//    runnables = new ConcurrentLinkedDeque<>();
//    for (int i = 0; i <= 50*threadBatch; i++) {
//      runnables.add(new ExecuteRunnable(this));
//    }
//    long end  = System.currentTimeMillis();
//    System.err.println("runnables created in " + (end-start));
    EngineUtils.initialize();
  }


  @Override public String call() throws Exception {
    profCallable.end("call");
    if(!isInitialized){
      initialize();
    }
    profCallable.start("Call getComponent ()");
    final ClusteringDependentLogic cdl = inputCache.getAdvancedCache().getComponentRegistry().getComponent
                                                                                    (ClusteringDependentLogic.class);
    String compressedCacheName = inputCache.getName() +".compressed";
    if(inputCache.getCacheManager().cacheExists(compressedCacheName))
    {
      Cache compressedCache = inputCache.getCacheManager().getCache(compressedCacheName);
      for(Object l : compressedCache.getListeners()){
        if(l instanceof BatchPutListener){
          BatchPutListener listener = (BatchPutListener) l;
          listener.waitForPendingPuts();
        }
      }
    }
    int count = 0;
    profCallable.end();
    ProfileEvent profExecute = new ProfileEvent("Buildinglucece" + this.getClass().toString(), profilerLog);

    if(indexCaches!=null)
      if(indexCaches.size()>0) {
        System.out.print("Building Lucene query or qualinfo ");
        long start=System.currentTimeMillis();
        luceneKeys = createLuceneQuerys(indexCaches, tree.getRoot());
        System.out.println(" time: " + (System.currentTimeMillis() - start) / 1000.0);
        profExecute.end();
      }

    if(luceneKeys ==null) {
      profCallable.start("Iterate Over Local Data");
      System.out.println("Iterate Over Local Data");

      profExecute = new ProfileEvent("GetIteratble " + this.getClass().toString(), profilerLog);

//    for(Object key : inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()) {
//      if (!cdl.localNodeIsPrimaryOwner(key))
//        continue;
    Object filter = new LocalDataFilter<K,V>(cdl);
    CloseableIterable iterable = inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).filterEntries(
        (KeyValueFilter<? super K, ? super V>) filter);
//        .converter((Converter<? super K, ? super V, ?>) filter);
    profExecute.end();
    profExecute.start("ISPNIter");
    try {

      for (Object object : iterable) {
        profExecute.end();
        Map.Entry<K, V> entry = (Map.Entry<K, V>) object;

        //      V value = inputCache.get(key);
        K key = (K) entry.getKey();
        V value = (V) entry.getValue();

        if (value != null) {
//          profExecute.start("ExOn" + (++count));
//          ExecuteRunnable runable = EngineUtils.getRunnable();
//          runable.setKeyValue(key, value,this);
//          EngineUtils.submit(runable);
          executeOn((K) key, value);
//          profExecute.end();
	}
         profExecute.start("ISPNIter");
      }
      iterable.close();
      }
	catch(Exception e){
        iterable.close();
        profilerLog.error("Exception in LEADSBASEBACALLABE " + e.getClass().toString());
        PrintUtilities.logStackTrace(profilerLog, e.getStackTrace());
    }
    }else{
      profCallable.start("Search_Over_Indexed_Data");
      System.out.println("Search Over Indexed Data");


      HashSet<LeadsIndex> keys=null;
      if(luceneKeys instanceof LeadsBaseCallable.qualinfo)
      {
        System.out.print("Building Lucece query ");
        System.out.println("Single qualinfo building query");
        long start=System.currentTimeMillis();
        qualinfo l=(qualinfo)luceneKeys;
        keys=getLuceneSet(l);
        System.out.println(" time: " + (System.currentTimeMillis() - start) / 1000.0);
        profExecute.end();
      } else if(luceneKeys instanceof HashSet)
        keys=(HashSet<LeadsIndex>)luceneKeys;

      //to do use sketches to find out what to do
      try {
        System.out.println(" Callable Found Indexed "  +keys.size() + " results");

        for (LeadsIndex lst : keys) {
          //System.out.println(lst.getAttributeName()+":"+lst.getAttributeValue());
          K key = (K) lst.getKeyName();
          V value = inputCache.get(key);
          if (value != null) {
            profExecute.start("ExOn" + (++count));
            executeOn(key, value);
            profExecute.end();
          }
        }
      } catch (Exception e) {
        profilerLog.error("Exception in LEADSBASEBACALLABE " + e.getClass().toString());
        e.printStackTrace();
        PrintUtilities.logStackTrace(profilerLog, e.getStackTrace());
      }
    }
    profCallable.end();
    finalizeCallable();
    return embeddedCacheManager.getAddress().toString();
  }

  public void initialize(){
    if(isInitialized)
      return;
    isInitialized = true;
    if(configString != null || configString.length() > 0)
      conf = new JsonObject(configString);
  }

  @Override public void finalizeCallable(){
    try {
      profCallable.start("finalizeBaseCallable");
      EngineUtils.waitForAllExecute();
      EnsembleCacheUtils.waitForAllPuts();
//      emanager.stop();
//
//      ecache.stop();
//      outputCache.stop();
    }catch(Exception e){
        System.err.println("LEADS Base callable "+e.getClass().toString()+ " " + e.getMessage() + " cause ");
      profilerLog.error(("LEADS Base callable "+e.getClass().toString()+ " " + e.getMessage() + " cause "));
       PrintUtilities.logStackTrace(profilerLog,e.getStackTrace());
      }
    profCallable.end("finalizeBaseCallable");
  }

  public void outputToCache(Object key, Object value){
    EnsembleCacheUtils.putToCache(outputCache,key.toString(),value  );
  }




  public class qualinfo
  {
    String attributeName="";
    String attributeType="";
    FilterOpType opType;
    Object compValue=null;
    public qualinfo(String attributeName, String attributeType) {
      this.attributeName = attributeName;
      this.attributeType = attributeType;
      this.opType = opType;
      this.compValue = compValue;
    }

    public qualinfo( FilterOpType opType, qualinfo left, qualinfo right) throws Exception {
      this(left.attributeName, left.attributeType);
      complete(right);
      this.opType = opType;
    }
    public qualinfo(String attributeType, Object compValue) {
      this.attributeName = attributeName;
      this.attributeType = attributeType;
      this.opType = opType;
      this.compValue = compValue;
    }

    public qualinfo complete(qualinfo other) throws Exception {
      if(!this.attributeType.equals(other.attributeType)){
        throw new Exception("Different Types " + this.attributeType + " " +other.attributeType);
      }


      if(attributeName.isEmpty()) {
        if (!other.attributeName.isEmpty()) {
          this.attributeName = other.attributeName;
        }
      }
      else{
        if (other.compValue!=null) {
          this.compValue = other.compValue;
        }
      }

      return this;
    }
  }



  HashSet<LeadsIndex> getLuceneSet(qualinfo l){
    FilterConditionEndContext f = getHaving(l);
    FilterConditionContext fc = addCondition(f, l);
    return buildLucene(fc);
  }

  HashSet<LeadsIndex> buildLucene(FilterConditionContext fc){
    if(fc == null)
      return null;
    System.out.println("Lucene Filter: " + fc.toString());
    List<LeadsIndex> list = fc.toBuilder().build().list();
    return new HashSet<LeadsIndex>(list);
  }


  FilterConditionContext addCondition(FilterConditionEndContext f, qualinfo l){
    FilterConditionContext fc;
    switch (l.opType) {
      case EQUAL:
        fc= f.eq(l.compValue);
        break;
      case LIKE:
        fc= f.like((String)l.compValue);
        break;
      case GEQ:
        fc= f.gte(l.compValue);
        break;
      case GTH :
        fc= f.gt(l.compValue);
        break;
      case LEQ :
        fc= f.lte(l.compValue);
        break;
      case LTH:
        fc= f.lt(l.compValue);
        break;
      default:
        return null;
    }
    return fc;
  }

  FilterConditionEndContext getHaving(qualinfo l){
    SearchManager sm = org.infinispan.query.Search.getSearchManager(indexCaches.get(l.attributeName));
    QueryFactory qf = sm.getQueryFactory();
    org.infinispan.query.dsl.QueryBuilder Qb;
    if (l.attributeType.equals("TEXT"))
      Qb = qf.from(LeadsIndexString.class);
    else if (l.attributeType.startsWith("FLOAT4"))
      Qb = qf.from(LeadsIndexFloat.class);
    else if (l.attributeType.startsWith("FLOAT8"))
      Qb = qf.from(LeadsIndexDouble.class);
    else if (l.attributeType.startsWith("INT4"))
      Qb = qf.from(LeadsIndexInteger.class);
    else if (l.attributeType.startsWith("INT8"))
      Qb = qf.from(LeadsIndexLong.class);
    else
      Qb = qf.from(LeadsIndex.class);
    FilterConditionEndContext f = Qb.having("attributeValue");

    return f;
  }

  Object getSubLucene(HashMap<String, Cache> indexCaches, FilterOperatorNode root) {
    qualinfo left = null;
    qualinfo right = null;

    if (root == null)
      return null;
    Object oleft = getSubLucene(indexCaches, root.getLeft());
    Object oright = getSubLucene(indexCaches, root.getRight());


    if (oleft instanceof LeadsBaseCallable.qualinfo)
      left = (qualinfo) oleft;
    if (oright instanceof LeadsBaseCallable.qualinfo)
      right = (qualinfo) oright;

    try {
      switch (root.getType()) {
        case FIELD:
          String collumnName = root.getValueAsJson().getObject("body").getObject("column").getString("name");
          String type = root.getValueAsJson().getObject("body").getObject("column").getObject("dataType").getString("type");
          if (indexCaches.containsKey(collumnName)) {
            System.out.println("Found Cache for: " + collumnName);
            return new qualinfo(collumnName,type);
          }
          break;

        case CONST:
          JsonObject datum = root.getValueAsJson().getObject("body").getObject("datum");
          type = datum.getObject("body").getString("type");
          String ret ="";
          System.out.println("Callable Found Const: " + datum.getObject("body").toString());

          try {
            if (type.equals("TEXT"))
              return  new qualinfo(type, MathUtils.getTextFrom(root.getValueAsJson()));
            else if (type.equals("DATE"))
              System.err.print("Unable to Handle: " + root.getValueAsJson());
            else {
              Number a = datum.getObject("body").getNumber("val");
              if (a != null)
                return new qualinfo(type,a);
            }

          } catch (Exception e) {
            System.err.print("Error " + ret + " to type " + type +"" + e.getMessage());
          }
          return null;
        default:
          FilterOpType t = root.getType();
          if (t == FilterOpType.EQUAL || t == FilterOpType.LIKE || t == FilterOpType.GEQ || t == FilterOpType.GTH
                  || t == FilterOpType.LEQ || t == FilterOpType.LTH) {
            if (left != null && right != null)
              return new qualinfo(t,left,right);

          }
          System.out.println("Unable to Handle: " + root.getValueAsJson());

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  Object createLuceneQuerys(HashMap<String, Cache> indexCaches, FilterOperatorNode root) {
    Object result = new HashSet();
    Object oleft = null;
    Object oright = null;
    HashSet hleft = null;
    HashSet hright = null;

    qualinfo lleft = null;
    qualinfo lright = null;
    if (root==null)
      return null;

    oleft = createLuceneQuerys(indexCaches, root.getLeft());
    oright = createLuceneQuerys(indexCaches, root.getRight());

    if(oleft != null){
      if (oleft instanceof LeadsBaseCallable.qualinfo)
        lleft = (qualinfo) oleft;
      if (oleft instanceof HashSet)
        hleft = (HashSet) oleft;
    }
    if(oright != null){
      if (oright instanceof LeadsBaseCallable.qualinfo)
        lright = (qualinfo) oright;
      if (oright instanceof HashSet)
        hright = (HashSet) oright;
    }


    switch (root.getType()) {
      case AND: {
        if(lleft !=null && lright !=null) {
          System.out.println("SubQual " + root.getType());
          if(lleft.attributeName.equals(lright.attributeName))
          {
            FilterConditionEndContext f = getHaving(lleft);
            FilterConditionContext fc = addCondition(f, lleft);

            f = fc.and().having("attributeValue");

            fc = addCondition(f, lright);
            return buildLucene(fc);
          }
        }
        //create sets
        if(lleft!=null)
          hleft=getLuceneSet(lleft);
        if(lright!=null)
          hright=getLuceneSet(lright);

        if(hleft !=null && hright!=null) {
          System.out.println("Find Intersection #1: "+ hleft.size()+ " #2: "+ hright.size());
          hleft.retainAll(hright);
          return hleft;
        }
        //System.out.println("Fix AND with multiple indexes");
      }
      break;
      case OR: {
        if(lleft !=null && lright !=null) {
          System.out.println("OR SubQual " + root.getType());
          if(lleft.attributeName.equals(lright.attributeName))
          {
            FilterConditionEndContext f = getHaving(lleft);
            FilterConditionContext fc = addCondition(f, lleft);

            f = fc.or().having("attributeValue");

            fc = addCondition(f, lright);
            return buildLucene(fc);
          }
        }
        //create sets
        if(lleft!=null)
          hleft=getLuceneSet(lleft);
        if(lright!=null)
          hright=getLuceneSet(lright);

        if(hleft !=null && hright!=null) {
          System.out.println("Put all results together #1: "+ hleft.size()+ " #2: "+ hright.size());
          hleft.addAll(hright);
          return hleft;
        }
      }
      break;
      default: {
        System.out.println("SubQual " + root.getType());
        return  getSubLucene(indexCaches, root);

      }
//			if (left != null)
//				result.addAll(left);
//			if (right != null)
//				result.addAll(right);
    }
    return (((HashSet)result).isEmpty()) ? null : result;
  }




}
