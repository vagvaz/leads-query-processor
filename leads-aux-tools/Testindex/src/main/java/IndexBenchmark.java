
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.NamedThreadFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.query.dsl.QueryBuilder;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.hibernate.search.annotations.*;

/**
 * @author gustavonalle
 */
public class IndexBenchmark {

  static int NUM_THREADS = 1;
  static int NUM_ELEMENTS_PER_THREAD = 500000;
  static String fileLocation = "/tmp/test/";
    static String value;


  public static int countIndex(Cache<?, ?> cache) {
    IndexReader indexReader = Search.getSearchManager(cache).getSearchFactory().getIndexReaderAccessor().open(Element.class);
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

    final Cache<Integer, Element> cache = defaultCacheManager.getCache();

    Search.getSearchManager(cache).getSearchFactory().addClasses(Element.class);
    IndexReader indexReader = Search.getSearchManager(cache).getSearchFactory().getIndexReaderAccessor().open(Element.class);

    final AtomicInteger counter = new AtomicInteger(0);

    ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS, new NamedThreadFactory("Index Populator"));

    long start = System.currentTimeMillis();
      final ArrayList<String> searchValue=new ArrayList<String>();
    for (int i = 0; i < NUM_THREADS; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < NUM_ELEMENTS_PER_THREAD; j++) {
            int key = counter.incrementAndGet();
            value = key + "-value"+randSmallString();
            cache.put(key, new Element(value));
            if (key != 0 && key % 10000 == 0) {
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
      stop = System.currentTimeMillis();
    System.out.println("\nIndexing finished, Ops per sec: = " + total /((stop - start)/1000));

    // create query

    QueryBuilder e=   org.infinispan.query.Search.getSearchManager(cache).buildQueryBuilderForClass(Element.class).get();
      org.apache.lucene.search.Query queryty= e.keyword().onField("attributeValue").matching(value).createQuery();

      // wrap Lucene query in a org.infinispan.query.CacheQuery
    CacheQuery cacheQuery = Search.getSearchManager(cache).getQuery(queryty);

    List list = null;//= cacheQuery.list();

//    QueryFactory qf = sm.getQueryFactory();
//    org.infinispan.query.dsl.Query lucenequery = qf.from(Element.class)
//              .having("attributeValue").eq("asdfasd")
//              .toBuilder().build();
//    List<Element> list = lucenequery.list();

   // System.out.println(" Results " + list.size() +" Runtime Query: " + (System.currentTimeMillis()-stop) + " ms\n");
      stop = System.currentTimeMillis();

    SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
      QueryFactory qf = sm.getQueryFactory();
      System.out.println("Search for #"+searchValue.size());
      for( String val:searchValue) {
          org.infinispan.query.dsl.Query lucenequery = qf.from(Element.class)
                  .having("attributeValue").eq(val)
                  .toBuilder().build();
          list = lucenequery.list();
      }
      System.out.println(" Results " + list.size() +" Mean Runtime 2 Query: " + (System.currentTimeMillis()-stop)/(float)searchValue.size() + " ms\n");
    defaultCacheManager.stop();
  }
}