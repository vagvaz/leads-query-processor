package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.storage.LeadsStorage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.QueryState;
import eu.leads.processor.core.plan.QueryStatus;
import eu.leads.processor.infinispan.operators.Operator;
import eu.leads.processor.nqe.MapReduceOperatorFactory;
import eu.leads.processor.nqe.NQEConstants;

import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.Properties;
import java.util.UUID;

/**
 * Created by Apostolos Nydriotis on 2015/06/22.
 */
public class ExecuteMapReduceJobActionHandler implements ActionHandler {

  private final Node com;
  private final LogProxy log;
  private final InfinispanManager persistence;
  private final String id;
  private Cache jobsCache;
  LeadsStorage storage;

  public ExecuteMapReduceJobActionHandler(Node com, LogProxy log, InfinispanManager persistence,
      String id,JsonObject globalConfig) {
    this.com = com;
    this.log = log;
    this.persistence = persistence;
    this.id = id;
    jobsCache = (Cache) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
    Properties storageConf = new Properties();
    storageConf.setProperty("prefix","/tmp/leads/");
    if(globalConfig!=null){
      if(globalConfig.containsField("hdfs.uri") && globalConfig.containsField("hdfs.prefix") && globalConfig.containsField("hdfs.user"))
      {
        storageConf.setProperty("hdfs.url", globalConfig.getString("hdfs.uri"));
        storageConf.setProperty("fs.defaultFS", globalConfig.getString("hdfs.uri"));
        storageConf.setProperty("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        storageConf.setProperty("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        storageConf.setProperty("prefix", globalConfig.getString("hdfs.prefix"));
        storageConf.setProperty("hdfs.user", globalConfig.getString("hdfs.user"));
        storageConf.setProperty("postfix", "0");
        System.out.println("USING HDFS yeah!");
        log.info("using hdfs: " + globalConfig.getString("hdfs.user")+ " @ "+ globalConfig.getString("hdfs.uri") + globalConfig.getString("hdfs.prefix") );
        storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.HDFS, storageConf);
      }else {
        log.info("No defined all hdfs parameters using local storage ");
        storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.LOCAL, storageConf);
      }
    }else {
      storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.LOCAL, storageConf);
    }
  }

  @Override
  public Action process(Action action) {
    Action result = new Action(action);
    result.setLabel(NQEConstants.EXECUTE_MAP_REDUCE_JOB);
    result.getData().putString("owner", id);
    String jobId = UUID.randomUUID().toString();
    if(result.getData().getObject("operator").containsField("direct")) {
      result.getData().getObject("operator").putString("id", jobId);
      QueryStatus queryStatus = new QueryStatus();
      queryStatus.setId(jobId);
      queryStatus.setStatus(QueryState.PENDING);
      jobsCache.put(jobId, queryStatus.toString());
      result.setResult(queryStatus.asJsonObject());
      result.setStatus(ActionStatus.COMPLETED.toString());
      System.out.println("Direct MR Execution");
    }
    else{
      Action ownerAction = new Action(result.asJsonObject().copy());
      ownerAction.setLabel(NQEConstants.OPERATOR_OWNER);
      ownerAction.setStatus(ActionStatus.INPROCESS.toString());
      com.sendTo(action.getData().getString("monitor"),ownerAction.asJsonObject());
    }

    // Maybe use OperatorFactory (and encompass MapReduceOperatorFactory's functionality there.
    // - Won't proceed with this (at least for now)as OperatorFactory uses "operatorType" as a flag
    //   while MapReduceOperatorFactory uses job's "name"
    Operator operator = MapReduceOperatorFactory.createOperator(com, persistence, log, result,storage);
    if (operator != null) {
      operator.init(result.getData());
      operator.execute();
    } else {
      log.error("Could not get a valid operator to execute so operator FAILED");
    }
    return result;
  }
}
