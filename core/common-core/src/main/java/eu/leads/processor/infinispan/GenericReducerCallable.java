package eu.leads.processor.infinispan;

import eu.leads.processor.common.LeadsCollector;
import eu.leads.processor.common.utils.storage.LeadsStorage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;

import java.util.Properties;

/**
 * Created by vagvaz on 2/19/15.
 */
public class GenericReducerCallable<K, V> extends LeadsBaseCallable<K,Object > {
  private static final long serialVersionUID = 3724554288677416503L;
  //  private String outputCacheName;
  private String reducerJar;
  private String reducerClassName;
  private byte[] reducerConfig;
  private String storageType;
  private Properties storageConfiguration;
  private String outputCacheName;
  private LeadsCollector<K,V> collector;
  private String tmpdirPrefix;
  transient private LeadsReducer reducer;
  transient LeadsStorage storageLayer;

  public GenericReducerCallable(String configString, String output) {
    super(configString, output);
  }

  @Override
  public  void initialize(){
    //Call super initialization
    super.initialize();

    //download mapper from storage layer
    //instatiate and initialize with the given configuration
    storageLayer = LeadsStorageFactory.getInitializedStorage(storageType, storageConfiguration);
    String localMapJarPath = tmpdirPrefix+"/mapreduce/"+reducerJar+"_"+reducerClassName;
    storageLayer.download(reducerJar,localMapJarPath);
    reducer = initializeReducer(localMapJarPath, reducerClassName, reducerConfig);
    //initialize cllector
    collector.initializeCache(imanager);
    //    collector.setCombiner(combiner);
  }

  private LeadsReducer initializeReducer(String localMapJarPath, String reducerClassName,
                                          byte[] reducerConfig) {

    return null;
  }

  @Override public void executeOn(K key, Object value) {
    Iterable<V> iterable = (Iterable<V>) value;
    reducer.reduce(key,iterable.iterator(),collector);
  }
}
