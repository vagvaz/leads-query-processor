package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.encrypt.CStore;
import eu.leads.processor.encrypt.ServerSide;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;

/**
 * Created by vagvaz on 10/27/14.
 */
public class SSEPointQueryOperator extends BasicOperator {
    private String targetCacheName;
//    Map<> encryptedDB;
//    Map<> encryptedIndex;

   String token;
    public SSEPointQueryOperator(Node com, InfinispanManager manager, LogProxy log, Action action) {
        super(com, manager, log, action);
    }


    @Override
    public void init(JsonObject config) {
//        super.init(conf);
        targetCacheName = conf.getObject("body").getString("cache");
        token = conf.getObject("body").getString("token");
    }

   @Override
   public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {

   }

  @Override public String getContinuousListenerClass() {
    return null;
  }

  @Override
   public void setupMapCallable() {

   }

   @Override
   public void setupReduceCallable() {

   }

   @Override
   public boolean isSingleStage() {
      return false;
   }

   @Override
    public void run() {
       Cache metadata = (Cache) manager.getPersisentCache(targetCacheName);
       JsonObject object = new JsonObject((String) metadata.get("metadata"));
       int svalue = Integer.parseInt(object.getString("svalue"));
       int bvalue = Integer.parseInt(object.getString("bvalue"));
       ServerSide server = new ServerSide(svalue,bvalue,manager,conf.getString("realOutput"));
       Cache index = (Cache) manager.getPersisentCache(object.getString("index"));
       Cache db = (Cache) manager.getPersisentCache(object.getString("db"));
       CStore cstore = new CStore(db,index,bvalue,svalue);
       Cache tmpOutput = (Cache) manager.getPersisentCache(conf.getString("realOutput"));
       try {
          server.TSetRetrieve(cstore,token,tmpOutput,true);
       } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
       } catch (InvalidAlgorithmParameterException e) {
          e.printStackTrace();
       }
       cleanup();
    }
}
