
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.NamedThreadFactory;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.spi.SearchFactoryIntegrator;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.eviction.EvictionThreadPolicy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.persistence.leveldb.configuration.CompressionType;
import org.infinispan.persistence.leveldb.configuration.LevelDBStoreConfiguration;
import org.infinispan.persistence.leveldb.configuration.LevelDBStoreConfigurationBuilder;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.transaction.TransactionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gustavonalle
 */
public class IndexBenchmark2 {

  static int NUM_THREADS = 1;
  static int NUM_ELEMENTS_PER_THREAD = 150;
  static String fileLocation = "/tmp/test/";
    static float value;
 static Random random = new Random();

  public static int countIndex(Cache<?, ?> cache) {
    IndexReader indexReader = Search.getSearchManager(cache).getSearchFactory().getIndexReaderAccessor().open(Element2.class);
    return indexReader.numDocs();
  }

  public static String randSmallString() {
    char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < 50; i++) {
      char c = chars[random.nextInt(chars.length)];
      sb.append(c);
    }
    String randomString = sb.toString();
    return randomString;
  }

  public static void main(String[] args) throws InterruptedException {
random = new Random(0);
    GlobalConfiguration globalConfiguration = new GlobalConfigurationBuilder().transport().defaultTransport().clusterName("test").build();

    Configuration configuration0 = new ConfigurationBuilder()
            .clustering() //ok
            .cacheMode(CacheMode.DIST_SYNC) //ok
            .hash().numOwners(1) //ok
            .indexing().index(org.infinispan.configuration.cache.Index.LOCAL) //ok
            .addProperty("hibernate.search.default.indexBase", fileLocation + "/lucene/")
            .addProperty("hibernate.search.default.indexmanager", "near-real-time")
            .addProperty("hibernate.search.default.indexwriter.ram_buffer_size", "256")
            .addProperty("lucene_version", "LUCENE_CURRENT").
                    transaction().transactionMode(
                    TransactionMode.NON_TRANSACTIONAL) //ok
            .persistence().passivation(true)//ok
            .addStore(LevelDBStoreConfigurationBuilder.class) //ok
            .location(fileLocation + "/leveldb/data/")//ok
            .expiredLocation(fileLocation + "/leveldb/expire")//ok
            .implementationType(LevelDBStoreConfiguration.ImplementationType.JNI)//ok
            .blockSize(1024 * 1024 * 1024)// check
            .compressionType(CompressionType.SNAPPY)//check
            .cacheSize(1024 * 1024 * 1024)//check
            .fetchPersistentState(true)//ok
            .shared(false).purgeOnStartup(false).preload(false).compatibility().enable()//ok //.marshaller(new TupleMarshaller())
            .expiration().lifespan(-1).maxIdle(-1).wakeUpInterval(-1).reaperEnabled(
                    false).eviction().maxEntries(50000).strategy( //check
                    EvictionStrategy.LIRS).threadPolicy(EvictionThreadPolicy.PIGGYBACK)
            .build();


    Configuration configuration = new ConfigurationBuilder()
            .clustering() //ok
            .cacheMode(CacheMode.LOCAL) //ok
            .hash().numOwners(1) //ok
            .indexing().index(org.infinispan.configuration.cache.Index.LOCAL) //ok
            .addProperty("hibernate.search.default.indexBase", fileLocation + "/lucene/")
            .addProperty("hibernate.search.default.indexmanager", "near-real-time")
            .addProperty("hibernate.search.default.exclusive_index_use", "true") //check
            .addProperty("hibernate.search.default.indexwriter.ram_buffer_size", "256")
            .addProperty("lucene_version", "LUCENE_CURRENT").
                    transaction().transactionMode(
                    TransactionMode.NON_TRANSACTIONAL) //ok
            .persistence().passivation(true)//ok
            .addStore(LevelDBStoreConfigurationBuilder.class) //ok
            .location(fileLocation + "/leveldb/data/")//ok
            .expiredLocation(fileLocation + "/leveldb/expire")//ok
            .implementationType(LevelDBStoreConfiguration.ImplementationType.JNI)//ok
            .blockSize(1 * 1024 * 1024)// check
            .compressionType(CompressionType.NONE)//check
            .cacheSize(128 * 1024 * 1024)//check
            .fetchPersistentState(true)//ok
            .shared(false).purgeOnStartup(false).preload(false).compatibility().enable()//ok //.marshaller(new TupleMarshaller())
            .expiration().lifespan(-1).maxIdle(-1).wakeUpInterval(-1).reaperEnabled(
                    false).eviction().maxEntries(1024).strategy( //check
                    EvictionStrategy.LIRS).threadPolicy(EvictionThreadPolicy.PIGGYBACK)
            .build();

    DefaultCacheManager defaultCacheManager = new DefaultCacheManager(globalConfiguration, configuration);

    final Cache<Integer, Element2> cache = defaultCacheManager.getCache();

    Search.getSearchManager(cache).getSearchFactory().addClasses(Element2.class);
    IndexReader indexReader = Search.getSearchManager(cache).getSearchFactory().getIndexReaderAccessor().open(Element2.class);

    final AtomicInteger counter = new AtomicInteger(0);

    ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS, new NamedThreadFactory("Index Populator"));

    long start = System.currentTimeMillis();
      final ArrayList<Float> searchValue=new ArrayList<Float>();
    for (int i = 0; i < NUM_THREADS; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < NUM_ELEMENTS_PER_THREAD; j++) {
            int key = counter.incrementAndGet();
            value =nextFloat(-50, 150);
            cache.put(key, new Element2(value));
            if (key != 0 && key % 10 == 0) {
                searchValue.add(value);
              System.out.printf("\rInserted %d, index is at %d value " + value, key, countIndex(cache));
            }
          }
        }
      });
    }

    int total = NUM_ELEMENTS_PER_THREAD * NUM_THREADS;
    executorService.shutdown();

    while (countIndex(cache) != total) {
      Thread.sleep(100);
    }
    //10526
    //12K
    //12900

    long stop = System.currentTimeMillis();
      int inputSize = cache.size();
    System.out.println(" Inputsize " +inputSize +" Runtime Query: " + (System.currentTimeMillis()-stop) + " ms\n");
      stop = System.currentTimeMillis()+51;
    System.out.println("\nIndexing finished, Ops per sec: = " + total /((stop - start)/1000.0));

    // create query

    QueryBuilder e=   Search.getSearchManager(cache).buildQueryBuilderForClass(Element2.class).get();
      SearchFactoryIntegrator qf2 = Search.getSearchManager(cache).getSearchFactory();
      QueryBuilder e0=qf2.buildQueryBuilder().forEntity(Element2.class).get();
    //  org.apache.lucene.search.Query queryty= e.keyword().onField("attributeValue").matching(value).createQuery();
//e.range().onField("attributeValue").from(null).to(value).createQuery();
     //  wrap Lucene query in a org.infinispan.query.CacheQuery
    //CacheQuery cacheQuery = Search.getSearchManager(cache).getQuery(queryty);

    //List<Element2> list = null;//= cacheQuery.list();
 //List<Object> list=cacheQuery.list();

//      System.out.println("Search for #"+searchValue.size());
//      for(int k=0;k<searchValue.size()-1;k++) {
//          org.apache.lucene.search.Query  lucenequery=null;
////          System.out.println("1Search above: " + searchValue.get(k));// + "<->" + searchValue.get(k + 1));
////          lucenequery= e0.range().onField("attributeValue").above(searchValue.get(k)).excludeLimit().createQuery();
//          if(searchValue.get(k+1)>searchValue.get(k)) {
//              System.out.println("1Search between: " + searchValue.get(k) + "<->" + searchValue.get(k + 1));
//              //lucenequery= e.range().onField("attributeValue").from(searchValue.get(k)).to(searchValue.get(k + 1)).createQuery();
//              System.out.println("1Search above: " + searchValue.get(k));// + "<->" + searchValue.get(k + 1));
//              lucenequery= e.range().onField("attributeValue").above(searchValue.get(k)).createQuery();
//          } else {
//              System.out.println("2Search between: " +searchValue.get(k+1)+"<->"+searchValue.get(k) );
//              lucenequery= e.range().onField("attributeValue").from(searchValue.get(k+1)).to(searchValue.get(k)).createQuery();
//          }
//          CacheQuery cacheQuery = Search.getSearchManager(cache).getQuery(lucenequery);
//
//
//          List<Object> list2 = cacheQuery.list();
//          for(Object e2 : list2) {
//              System.out.println("Found "+ ((Element2)e2).toString());
//          }
//      }





//    QueryFactory qf = sm.getQueryFactory();
//    org.infinispan.query.dsl.Query lucenequery = qf.from(Element.class)
//              .having("attributeValue").eq("asdfasd")
//              .toBuilder().build();
    List<Element2> list = null;

    //System.out.println(" Results " + list.size() +" Runtime Query: " + (System.currentTimeMillis()-stop) + " ms\n");
      stop = System.currentTimeMillis();

    SearchManager sm = Search.getSearchManager(cache);
      QueryFactory qf = sm.getQueryFactory();

      for(int k=0;k<searchValue.size()-1;k++) {

          org.infinispan.query.dsl.Query lucenequery;
          if(searchValue.get(k+1)>searchValue.get(k)) {
              System.out.println("Search between: " + searchValue.get(k) + "<->" + searchValue.get(k + 1));
              lucenequery = qf.from(Element2.class)
                      .having("attributeValue").between(searchValue.get(k), searchValue.get(k + 1))
                      .toBuilder().build();
              lucenequery = qf.from(Element2.class)
                      .having("attributeValue").gt(searchValue.get(k)).and().having("attributeValue").lt( searchValue.get(k + 1))
                      .toBuilder().build();
          } else {
              System.out.println("Search between: " +searchValue.get(k+1)+"<->"+searchValue.get(k) );
              lucenequery = qf.from(Element2.class)
                      .having("attributeValue").between(searchValue.get(k + 1), searchValue.get(k))
                      .toBuilder().build();
              lucenequery = qf.from(Element2.class)
                      .having("attributeValue").gt(searchValue.get(k+1)).and().having("attributeValue").lt( searchValue.get(k))
                      .toBuilder().build();

          }
          list = lucenequery.list();
          for(Element2 e3 : list){
              System.out.println("Found "+ e3.toString());
          }
      }
      System.out.println(" Results " + list.size() +" Mean Runtime 2 Query: " + (System.currentTimeMillis()-stop)/(float)searchValue.size() + " ms\n");
    defaultCacheManager.stop();
  }

    public static float nextFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    public static int nextInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}