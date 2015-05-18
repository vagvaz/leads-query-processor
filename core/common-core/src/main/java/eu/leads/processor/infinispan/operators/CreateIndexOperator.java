package eu.leads.processor.infinispan.operators;

/**
 * Created by vagvaz on 10/26/14.
 */

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.math.MathUtils;
import org.infinispan.Cache;
import org.infinispan.versioning.utils.version.Version;
import org.infinispan.versioning.utils.version.VersionScalar;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//import org.infinispan.versioning.VersionedCache;
//import org.infinispan.versioning.impl.VersionedCacheTreeMapImpl;
//import org.infinispan.versioning.utils.version.Version;
//import org.infinispan.versioning.utils.version.VersionScalar;
//import org.infinispan.versioning.utils.version.VersionScalarGenerator;


public class CreateIndexOperator extends BasicOperator {
        public CreateIndexOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {
                super(com,persistence,log,action);
            }
        Cache  targetCache;
        JsonObject data;
        String key = "";
    String tableName;
        Version version = null;
    
                @Override
        public void init(JsonObject config) {
//                            super.init(config);
                    data = new JsonObject();
                    JsonArray columnNames = conf.getObject("CreateIndex").getArray("SortSpecs");
                    JsonArray values = conf.getObject("body").getArray("exprs");
                    JsonArray primaryArray = conf.getObject("Projection").getArray("TableName");
                    Set<String> primaryColumns = new HashSet<String>(primaryArray.toList());
                    Iterator<Object> columnIterator = columnNames.iterator();
                    Iterator<Object> valuesIterator = values.iterator();
                    if (values.size() != columnNames.size()) {
                        log.error("INSERT problem different size between values and columnNames");
                    }
                    tableName = conf.getObject("body").getString("tableName");
                    key = tableName + ":";
                    while (columnIterator.hasNext() && valuesIterator.hasNext()) {
                        String column = (String) columnIterator.next();
                        JsonObject jsonValue = (JsonObject) valuesIterator.next();
                        Object value = MathUtils.getValueFrom(jsonValue);
                        if (column.equalsIgnoreCase("version")) {
                            Object ob = MathUtils.getValueFrom(jsonValue);
                            if (ob instanceof String) {
                                SimpleDateFormat df = new SimpleDateFormat();
//                                    try {
//                                            version = new VersionScalar(df.parse((String) ob).getTime());
//                                        } catch (ParseException e) {
//                                            e.printStackTrace();
//                                        }
//                                }else if(ob instanceof Long){
//                                 version  = new VersionScalar ((Long)ob);
//                                }
//                        }
                                if (primaryColumns.contains(column)) {
                                    key = key + "," + value.toString();
                                }
//                                data.putValue(column, value);

                            }

                        }
                        if (primaryColumns.contains(column)) {
                            key = key + "," + value.toString();
                        }
                        data.putValue(column,value);
                    }

                }
    
         @Override
        public void execute() {
             targetCache = (Cache) manager.getPersisentCache(tableName);
//                VersionedCache versionedCache = new VersionedCacheTreeMapImpl(targetCache, new VersionScalarGenerator(),targetCache.getName());
                if(version == null){
                        version = new VersionScalar(System.currentTimeMillis());
                    }

             long size = targetCache.size();
             log.info("inserting into " + targetCache.getName() + " "

                     + key  +"     \n"+data.toString());
                targetCache.put(key,data);
//                targetCache.put(key,data.toString());
//                        versionedCache.put(key,data.toString(),version);
                if(targetCache.size() < size + 1 ){
                    log.error("Insert Failed " + targetCache.size());
                }
             cleanup();
            }
    
                @Override
        public void cleanup() {
                super.cleanup();
            }
    
            
            }
