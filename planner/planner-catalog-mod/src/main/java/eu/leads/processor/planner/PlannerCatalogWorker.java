package eu.leads.processor.planner;

import com.google.common.base.Preconditions;
import com.google.protobuf.ServiceException;
import eu.leads.processor.common.StringConstants;
import leads.tajo.catalog.LeadsCatalog;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.catalog.*;
import org.apache.tajo.catalog.function.Function;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.common.TajoDataTypes.Type;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.engine.function.annotation.Description;
import org.apache.tajo.engine.function.annotation.ParamOptionTypes;
import org.apache.tajo.engine.function.annotation.ParamTypes;
import org.apache.tajo.master.rm.TajoWorkerResourceManager;
import org.apache.tajo.util.ClassUtil;
import org.apache.tajo.util.KeyValueSet;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by vagvaz on 8/25/14.
 */
public class PlannerCatalogWorker extends Verticle {
  LeadsCatalog catalogServer = null;
  TajoConf conf = new TajoConf();

  @Override
  public void start() {
    super.start();

    //Read configuration
    JsonObject config = container.config();
    TajoConf conf = new TajoConf();
    if (System.getProperty(TajoConf.ConfVars.RESOURCE_MANAGER_CLASS.varname) != null) {
      String testResourceManager =
        System.getProperty(TajoConf.ConfVars.RESOURCE_MANAGER_CLASS.varname);
      Preconditions.checkState(testResourceManager.equals(TajoWorkerResourceManager.class
                                                            .getCanonicalName()));
      conf.set(TajoConf.ConfVars.RESOURCE_MANAGER_CLASS.varname,
                System.getProperty(TajoConf.ConfVars.RESOURCE_MANAGER_CLASS.varname));
    }

    conf.setInt(TajoConf.ConfVars.WORKER_RESOURCE_AVAILABLE_MEMORY_MB.varname,
                 config.getInteger("memory", 1024));
    float disks = Float.parseFloat(config.getString("disks", "1.0"));
    conf.setFloat(TajoConf.ConfVars.WORKER_RESOURCE_AVAILABLE_DISKS.varname, disks);

    Object clusterTestBuildDir = LeadsCatalog.setupClusterRandBuildDir();

    //conf.set(CatalogConstants.STORE_CLASS,"org.apache.tajo.catalog.store.MemStore");//
    conf.set(CatalogConstants.STORE_CLASS, config.getString("store",
                                                             "leads.tajo.catalog.LeadsMemStore"));//
    conf.set(CatalogConstants.CATALOG_URI, config
                                             .getString("uri",
                                                         "jdbc:derby:"
                                                           + clusterTestBuildDir
                                                           + "/db"));

    conf.setVar(TajoConf.ConfVars.CATALOG_ADDRESS,
                 config.getString("ip", "0.0.0.0")
                   + ":" +
                   config
                     .getNumber("port"));
    catalogServer = new LeadsCatalog(conf);
    try {
      catalogServer.StartServer();
      if (container.config().containsField("generateSchemas"))
        createInitialTables();
    } catch (Exception e) {
      container.logger().error("Problem Encountered when tried to create Initial Schemas " + e.getMessage());
    }

  }

  private void createInitialTables() {
    CatalogClient catalog = null;
    try {
      catalog = new CatalogClient(conf, "localhost", container.config().getInteger("port"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    catalog.createTablespace(StringConstants.DEFAULT_TABLE_SPACE,
                              "leadsfs://localhost:"+container.config().getInteger("port")+"/warehouse");
    catalog
      .createDatabase(StringConstants.DEFAULT_DATABASE_NAME, StringConstants.DEFAULT_TABLE_SPACE);

    try {
      if(initBuiltinFunctions().size() == 0)
        container.logger().error("\n\n\n\n\n\n\n SIZE 0 \n\n\n\n\n");
      for (FunctionDesc funcDesc : initBuiltinFunctions()) {
        container.logger().error("\n"+funcDesc.toString());
        catalog.createFunction(funcDesc);
      }
    } catch (ServiceException e) {
      e.printStackTrace();
    }


    Schema webPagesSchema = new Schema();
    webPagesSchema.addColumn("url",Type.TEXT);
    webPagesSchema.addColumn("domainname",Type.TEXT);
    webPagesSchema.addColumn("headers",Type.BLOB);
    webPagesSchema.addColumn("content",Type.TEXT);
    webPagesSchema.addColumn("responsecode",Type.INT4);
    webPagesSchema.addColumn("language",Type.TEXT);
    webPagesSchema.addColumn("charset",Type.TEXT);
    webPagesSchema.addColumn("responsetime",Type.INT4);
    webPagesSchema.addColumn("links",Type.TEXT_ARRAY);
    webPagesSchema.addColumn("title",Type.TEXT);
    webPagesSchema.addColumn("version",Type.DATE);
    webPagesSchema.addColumn("pagerank",Type.FLOAT8);
    webPagesSchema.addColumn("sentiment",Type.FLOAT8);

    Schema entitiesSchema = new Schema();
    entitiesSchema.addColumn("webpageurl",Type.TEXT);
    entitiesSchema.addColumn("name",Type.TEXT);
    entitiesSchema.addColumn("sentiment",Type.FLOAT8);
    entitiesSchema.addColumn("version",Type.DATE);







    TableMeta meta = CatalogUtil.newTableMeta(CatalogProtos.StoreType.SEQUENCEFILE);
    TableDesc entities  = new TableDesc(
                                        CatalogUtil
                                          .buildFQName(StringConstants.DEFAULT_DATABASE_NAME,
                                                        "entities"), entitiesSchema, meta,
                                        getTestDir("entities"));
    catalog.createTable(entities);


    TableDesc webpages = new org.apache.tajo.catalog.TableDesc(
                                                                CatalogUtil
                                                                  .buildFQName(StringConstants.DEFAULT_DATABASE_NAME,
                                                                                "webpages"),
                                                                webPagesSchema,
                                                                CatalogProtos.StoreType.MEM,
                                                                new KeyValueSet(),
                                                                getTestDir("webpages"));
    catalog.createTable(webpages);

  }


  public static List<FunctionDesc> initBuiltinFunctions() throws ServiceException {
    List<FunctionDesc> sqlFuncs = new ArrayList<FunctionDesc>();

    Set<Class> functionClasses = ClassUtil
                                   .findClasses(org.apache.tajo.catalog.function.Function.class,
                                                 "org.apache.tajo.engine.function");

    for (Class eachClass : functionClasses) {
      if(eachClass.isInterface() || Modifier.isAbstract(eachClass.getModifiers())) {
        continue;
      }
      Function function = null;
      try {
        function = (Function)eachClass.newInstance();
      } catch (Exception e) {
//        System.err(eachClass.toString() + " cannot instantiate Function class because of " + e.getMessage());
        continue;
      }
      String functionName = function.getClass().getAnnotation(Description.class).functionName();
      String[] synonyms = function.getClass().getAnnotation(Description.class).synonyms();
      String description = function.getClass().getAnnotation(Description.class).description();
      String detail = function.getClass().getAnnotation(Description.class).detail();
      String example = function.getClass().getAnnotation(Description.class).example();
      Type returnType = function.getClass().getAnnotation(Description.class).returnType();
      ParamTypes[] paramArray = function.getClass().getAnnotation(Description.class).paramTypes();

      String[] allFunctionNames = null;
      if(synonyms != null && synonyms.length > 0) {
        allFunctionNames = new String[1 + synonyms.length];
        allFunctionNames[0] = functionName;
        System.arraycopy(synonyms, 0, allFunctionNames, 1, synonyms.length);
      } else {
        allFunctionNames = new String[]{functionName};
      }

      for(String eachFunctionName: allFunctionNames) {
        for (ParamTypes params : paramArray) {
          ParamOptionTypes[] paramOptionArray;
          if(params.paramOptionTypes() == null ||
               params.paramOptionTypes().getClass().getAnnotation(ParamTypes.class) == null) {
            paramOptionArray = new ParamOptionTypes[0];
          } else {
            paramOptionArray = params.paramOptionTypes().getClass().getAnnotation(ParamTypes.class).paramOptionTypes();
          }

          Type[] paramTypes = params.paramTypes();
          if (paramOptionArray.length > 0)
            paramTypes = params.paramTypes().clone();

          for (int i=0; i < paramOptionArray.length + 1; i++) {
            FunctionDesc functionDesc = new FunctionDesc(eachFunctionName,
                                                          function.getClass(), function.getFunctionType(),
                                                          CatalogUtil.newSimpleDataType(returnType),
                                                          paramTypes.length == 0 ? CatalogUtil.newSimpleDataTypeArray() : CatalogUtil.newSimpleDataTypeArray(paramTypes));

            functionDesc.setDescription(description);
            functionDesc.setExample(example);
            functionDesc.setDetail(detail);
            sqlFuncs.add(functionDesc);

            if (i != paramOptionArray.length) {
              paramTypes = new Type[paramTypes.length +
                                      paramOptionArray[i].paramOptionTypes().length];
              System.arraycopy(params.paramTypes(), 0, paramTypes, 0, paramTypes.length);
              System.arraycopy(paramOptionArray[i].paramOptionTypes(), 0, paramTypes, paramTypes.length,
                                paramOptionArray[i].paramOptionTypes().length);
            }
          }
        }
      }
    }

    return sqlFuncs;
  }




  private Path getTestDir(String table) {
    return new Path(StringConstants.DEFAULT_PATH + "/" + table);
  }
}

//Schema schema = new Schema();
//schema.addColumn("webpageurl", Type.TEXT);
//  schema.addColumn("name", Type.TEXT);
//  schema.addColumn("sentimentscore", Type.INT4);
//  Schema schema2 = new Schema();
//  schema2.addColumn("body ", Type.TEXT);
//  schema2.addColumn("sentiment", Type.TEXT);
//  schema2.addColumn("pagerank", Type.INT4);
//  schema2.addColumn("domainname", Type.TEXT);
//  schema2.addColumn("links", Type.TEXT);
//  schema2.addColumn("url", Type.TEXT);
//
//  Schema schema3 = new Schema();
//  schema3.addColumn("deptname", Type.TEXT);
//  schema3.addColumn("score", Type.INT4);
//  schema3.addColumn("phone", Type.INT4);
//
//  Schema schema4 = new Schema();
//  schema4.addColumn("deptname", Type.TEXT);
//  schema4.addColumn("score", Type.INT4);
//  schema4.addColumn("phone", Type.INT4);
//
//  Schema schema5 = new Schema();
//  schema5.addColumn("deptname", Type.TEXT);
//  schema5.addColumn("score", Type.INT4);
//  schema5.addColumn("phone", Type.INT4);
//TableDesc score = new org.apache.tajo.catalog.TableDesc(
//                                                         CatalogUtil
//                                                           .buildFQName(StringConstants.DEFAULT_DATABASE_NAME,
//                                                                         "score"),
//                                                         schema3, CatalogProtos.StoreType.MEM,
//                                                         new KeyValueSet(),
//                                                         getTestDir("score"));
//catalog.createTable(score);
//
//  TableDesc score2 = new org.apache.tajo.catalog.TableDesc(
//  CatalogUtil
//  .buildFQName(StringConstants.DEFAULT_DATABASE_NAME,
//  "score2"),
//  schema4, CatalogProtos.StoreType.MEM,
//  new KeyValueSet(),
//  getTestDir("score2"));
//  catalog.createTable(score2);
//
//  TableDesc score3 = new org.apache.tajo.catalog.TableDesc(
//  CatalogUtil
//  .buildFQName(StringConstants.DEFAULT_DATABASE_NAME,
//  "score3"),
//  schema5, CatalogProtos.StoreType.MEM,
//  new KeyValueSet(),
//  getTestDir("score3"));
//  catalog.createTable(score3);
