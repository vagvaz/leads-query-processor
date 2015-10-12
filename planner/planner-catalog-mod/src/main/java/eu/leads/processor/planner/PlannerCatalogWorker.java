package eu.leads.processor.planner;

import com.google.common.base.Preconditions;
import com.google.protobuf.ServiceException;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.conf.ConfigurationUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.imanager.IManagerConstants;
import leads.tajo.catalog.LeadsCatalog;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.catalog.*;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.common.TajoDataTypes.Type;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.engine.function.annotation.Description;
import org.apache.tajo.engine.function.annotation.ParamOptionTypes;
import org.apache.tajo.engine.function.annotation.ParamTypes;
import org.apache.tajo.function.Function;
import org.apache.tajo.master.rm.TajoWorkerResourceManager;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
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
  JsonObject globalConfig;
  private EventBus bus;

  @Override
  public void start() {
    super.start();
    bus= vertx.eventBus();

    bus.registerHandler("leads.processor.control", new Handler<Message>() {
            @Override
            public void handle(Message message) {
              System.err.println("  " + message.toString());

              JsonObject body = (JsonObject)message.body();
              if (body.containsField("type")) {
                if (body.getString("type").equals("action")) {
                  Action action = new Action(body);
                  if(!action.getLabel().equals(IManagerConstants.QUIT)) {

                    System.err.println("Continue");
                  }else{

                    System.err.println("planner Stopping Manager Exiting");
                    catalogServer.StopServer();
                    System.err.println("planner Exiting");

                    vertx.setTimer(3000, new Handler<Long>() {
                      @Override
                      public void handle(Long aLong) {
                        System.out.println(" planner Exiting ");
                        //vertx.stop();
                        System.exit(0);
                      }
                    });
                  }
                }

              }
            }
          });

    LQPConfiguration.initialize();
    LQPConfiguration.getInstance().getConfiguration().setProperty("node.current.component",
        "catalog-worker");
    globalConfig = container.config().getObject("global");
    String publicIP = ConfigurationUtilities
        .getPublicIPFromGlobal(LQPConfiguration.getInstance().getMicroClusterName(), globalConfig);
    LQPConfiguration.getInstance().getConfiguration().setProperty(StringConstants.PUBLIC_IP,publicIP);
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
    float disks =config.getNumber("disks", 1).floatValue();

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
      //Start Catalog Server
      boolean catalogServerStarted = false;
      int count = 0;
      while (!catalogServerStarted){
          container.logger().info("Trying to start CatalogServer.");
          catalogServer = new LeadsCatalog(conf);
          try {
              catalogServerStarted = catalogServer.StartServer();
//              catalogServerStarted=true;
          }catch(Exception e){
            container.logger().error("Problem when starting CatalogServer retrying");

          }
          if(count > 2)
          {
              container.logger().error("Failed to Started Catalog Server Exiting");
              System.exit(-1);
          }
          count++;
      }
      try {
          if (container.config().containsField("generateSchemas"))
              createInitialTables();
      } catch (Exception e) {
        catalogServer=null;
        container.logger().error("Problem Encountered when tried to create Initial Schemas " + e.getMessage());
        container.logger().error("\n\n\n\n\n\n\n SIZE 0 \n\n\n\n\n");
       container.logger().error("neither the tables nor the functions have been created...");
        }

    }

  private void createInitialTables() {
    CatalogClient catalog = null;
    try {
      catalog = new CatalogClient(conf, "localhost", container.config().getInteger("port"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    if(!catalog.existTablespace(StringConstants.DEFAULT_TABLE_SPACE))
          catalog.createTablespace(StringConstants.DEFAULT_TABLE_SPACE,
            "leadsfs://localhost:" + container.config().getInteger("port") + "/warehouse");
    else
      System.out.println("TableSpace "+ StringConstants.DEFAULT_TABLE_SPACE +" exists");
    if(!catalog.existDatabase(StringConstants.DEFAULT_DATABASE_NAME))
       catalog.createDatabase(StringConstants.DEFAULT_DATABASE_NAME, StringConstants.DEFAULT_TABLE_SPACE);
    else
      System.out.println("DataBase name "+ StringConstants.DEFAULT_DATABASE_NAME +"  exists");

    System.out.println("Loading functions");
    try {
      int k = -29;
      List<FunctionDesc> builtin = initFunctions("org.apache.tajo.engine.function");
      if ((k = builtin.size()) == 0) {
        container.logger().error("BUILDING FUNCTION NOT FOOUND\n\n\n\n\n\n\n SIZE 0 \n\n\n\n\n");
      } else {
        System.out.println("Found Builtin Functions  = " + k);
        container.logger().info("Found Builtin Functions  = " + k);
      }
      for (FunctionDesc funcDesc : builtin) {
        //container.logger().info(funcDesc.toString()+" " + funcDesc.getFuncType());
        // System.out.println(funcDesc.getFuncType());
        catalog.createFunction(funcDesc);
      }
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    System.out.println(catalog.getFunctions().size() + " functions loaded.");

    Schema webPagesSchema = new Schema();
    webPagesSchema.addColumn("url",Type.TEXT);
    webPagesSchema.addColumn("domainname",Type.TEXT);
    webPagesSchema.addColumn("body",Type.TEXT);
    webPagesSchema.addColumn("responsecode",Type.INT8);
    webPagesSchema.addColumn("language",Type.TEXT);
    webPagesSchema.addColumn("charset",Type.TEXT);
    webPagesSchema.addColumn("responsetime",Type.INT8);
    webPagesSchema.addColumn("links",Type.TEXT);
    webPagesSchema.addColumn("title",Type.TEXT);
    webPagesSchema.addColumn("ts",Type.INT8);
    webPagesSchema.addColumn("pagerank",Type.FLOAT8);
    webPagesSchema.addColumn("sentiment",Type.FLOAT8);

    Schema entitiesSchema = new Schema();
    entitiesSchema.addColumn("webpageurl",Type.TEXT);
    entitiesSchema.addColumn("name",Type.TEXT);
    entitiesSchema.addColumn("sentimentscore",Type.FLOAT8);
//    entitiesSchema.addColumn("version",Type.DATE);


    //TableMeta meta = CatalogUtil.newTableMeta(CatalogProtos.StoreType.SEQUENCEFILE);
    TableMeta meta = CatalogUtil.newTableMeta(CatalogProtos.StoreType.MEM );

    TableDesc entities  = new TableDesc(CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME,"entities"), entitiesSchema, meta, getTestDir("entities").toUri());
    //catalog.createTable(entities);
    createTable(catalog, entities);

//    TableDesc webpages = new TableDesc(CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME,"webpages"), webPagesSchema, CatalogProtos.StoreType.MEM,
//                                                                new KeyValueSet(),
//                                                                getTestDir("webpages"));
    TableDesc webpages = new TableDesc(CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME,"webpages"), webPagesSchema, meta, getTestDir("webpages").toUri());
    //catalog.createTable(webpages);
    createTable(catalog,webpages);

    Schema testwebPagesSchema = new Schema();
    testwebPagesSchema.addColumn("url",Type.TEXT);
    testwebPagesSchema.addColumn("domainname",Type.TEXT);
    testwebPagesSchema.addColumn("responsecode", Type.INT8);
    TableDesc TESTwebpages = new TableDesc(CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME, "testpages"), testwebPagesSchema, meta, getTestDir("testpages").toUri());
    //catalog.createTable(TESTwebpages);
    createTable(catalog, TESTwebpages);

    String databaseName = StringConstants.DEFAULT_DATABASE_NAME;
    String tableName = "defaultname";
    //New schema

    //catalog.createDatabase("internal", StringConstants.DEFAULT_TABLESPACE_NAME);
    //catalog.createDatabase("crawler", StringConstants.DEFAULT_TABLESPACE_NAME);
    //catalog.createDatabase("leads", StringConstants.DEFAULT_TABLESPACE_NAME);
    //catalog.createDatabase("adidas", StringConstants.DEFAULT_TABLESPACE_NAME);


    Schema schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("ts", Type.INT8);
    schema.addColumn("content", Type.TEXT);
    //PRIMARY KEY (uri, ts)
    //databaseName = "crawler";
    tableName = "content";
    //catalog.createTable(new TableDesc(
    //        CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));

    schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("ts", Type.INT8);
    schema.addColumn("pagetypeassumption", Type.TEXT);
    schema.addColumn("ecomfeatures", Type.TEXT);
    schema.addColumn("extractioncandidates", Type.TEXT);
    schema.addColumn("successfulextractions", Type.TEXT);
    //PRIMARY KEY (uri, ts)
    //databaseName = "internal";
    tableName = "page";

    //catalog.createTable(new TableDesc(
    //        CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));

    schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("ts", Type.INT8);
    schema.addColumn("dirassumption", Type.TEXT);
    schema.addColumn("ecomassumptionpagesno", Type.TEXT);
    schema.addColumn("pagesno", Type.TEXT);
    //PRIMARY KEY (uri, ts)
    //databaseName = "internal";
    tableName = "urldirectory";
    //catalog.createTable(new TableDesc(
    //        CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));

    schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("ts", Type.INT8);
    schema.addColumn("isatbbuttonindir", Type.TEXT);
    schema.addColumn("atbbuttonextractionlog", Type.TEXT);
    schema.addColumn("nameextractiontuples", Type.TEXT);
    schema.addColumn("priceextractiontuples", Type.TEXT);
    schema.addColumn("productclustercenter", Type.TEXT);
    schema.addColumn("categoryclustercenter", Type.TEXT);
    schema.addColumn("productcluster50pcdist", Type.TEXT);
    schema.addColumn("productcluster80pcdist", Type.TEXT);
    schema.addColumn("categorycluster50pcdist", Type.TEXT);
    schema.addColumn("categorycluster80pcdist", Type.TEXT);
    schema.addColumn("scalermean", Type.TEXT);
    schema.addColumn("scalerstd", Type.TEXT);
    //PRIMARY KEY (uri, ts)
    //databaseName = "internal";
    tableName = "urldirectory_ecom";
    //catalog.createTable(new TableDesc(
    //        CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));


    schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("ts", Type.INT8);
    schema.addColumn("partid", Type.TEXT);
    schema.addColumn("keywords", Type.TEXT);
    schema.addColumn("relevance", Type.TEXT);
    schema.addColumn("sentiment", Type.FLOAT8);
    //	PRIMARY KEY (uri,ts,partid,keywords)
    //databaseName = "leads";
    tableName = "keywords";
    //catalog.createTable(new TableDesc(
    //        CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));

    schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("ts", Type.INT8);
    schema.addColumn("fqdnurl", Type.TEXT);
    schema.addColumn("lang", Type.TEXT);
    schema.addColumn("maincontent", Type.TEXT);
    schema.addColumn("contentdate", Type.TEXT);
    schema.addColumn("oldsentiment", Type.TEXT);
    schema.addColumn("textcontent", Type.TEXT);
    schema.addColumn("type", Type.TEXT);
    schema.addColumn("sentiment", Type.FLOAT8);
    schema.addColumn("pagerank", Type.FLOAT8);

    //	PRIMARY KEY (uri,ts)
    //databaseName = "leads";
    tableName = "page_core";
    //catalog.createTable(new TableDesc(
    //        CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));

    schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("textcontent", Type.TEXT);
    //databaseName = "leads";
    tableName = "page_textcontent";

    //catalog.createTable(new TableDesc(
    //        CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));

    schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("ts", Type.INT8);
    schema.addColumn("partid", Type.TEXT);
    schema.addColumn("resourceparttype", Type.TEXT);
    schema.addColumn("resourcepartvalue", Type.TEXT);
    //PRIMARY KEY (uri,ts,partid,resourceparttype)
    //databaseName = "leads";
    tableName = "resourcepart";
    //catalog.createTable(new TableDesc(
    //        CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));


    schema = new Schema();
    schema.addColumn("uri", Type.TEXT);
    schema.addColumn("ts", Type.INT8);
    schema.addColumn("category", Type.TEXT);
    schema.addColumn("country", Type.TEXT);
    schema.addColumn("domaincountry", Type.TEXT);
    schema.addColumn("ipgeoinfo", Type.TEXT);
    schema.addColumn("mainlanguages", Type.TEXT);
    schema.addColumn("whoiscountry", Type.TEXT);
    //	PRIMARY KEY (uri,ts)
    //databaseName = "leads";
    tableName = "site";
    //      catalog.createTable(new TableDesc(
//              CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));


    schema = new Schema();
    schema.addColumn("keywords", Type.TEXT);
    //	PRIMARY KEY (keywords)
    //databaseName = "adidas";
    tableName = "adidas_keywords";//"keywords";
    //      catalog.createTable(new TableDesc(
//              CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));

    //##############################

    schema = new Schema();
    schema.addColumn("pageurl", Type.TEXT);
    schema.addColumn("pagerank", Type.INT8);
    schema.addColumn("avgduration", Type.INT8);
    //PRIMARY KEY (pageURL)
    tableName = "rankings";
    (schema.getColumn("pageurl")).setPrimaryKey(true);
//      catalog.createTable(new TableDesc(
//              CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));


    schema = new Schema();
    schema.addColumn("sourceip", Type.TEXT);
    schema.addColumn("desturl", Type.TEXT);
    schema.addColumn("visitdate", Type.TEXT);
    schema.addColumn("adrevenue", Type.FLOAT8);
    schema.addColumn("useragent", Type.TEXT);
    schema.addColumn("countrycode", Type.TEXT);
    schema.addColumn("languagecode", Type.TEXT);
    schema.addColumn("searchWord", Type.TEXT);
    schema.addColumn("duration", Type.INT8);
    //PRIMARY KEY (sourceIP,destURL)
    (schema.getColumn("sourceip")).setPrimaryKey(true);
    (schema.getColumn("desturl")).setPrimaryKey(true);

    tableName = "uservisits";
//    catalog.createTable(new TableDesc(
//            CatalogUtil.buildFQName(databaseName, tableName),schema,meta, getTestDir(databaseName+"."+tableName).toUri()));
    createTable(catalog, new TableDesc(
            CatalogUtil.buildFQName(databaseName, tableName), schema, meta, getTestDir(databaseName + "." + tableName).toUri()));

  }
  public boolean createTable(CatalogClient catalog,   final TableDesc desc){
    if(!catalog.existsTable(desc.getName()))
      return catalog.createTable(desc);
    else
      System.out.println(" Table " + desc.getName()+ " exists ..");
    return false;
  }


  public static List<FunctionDesc> initFunctions(String class_dir) throws ServiceException {
    List<FunctionDesc> sqlFuncs = new ArrayList<FunctionDesc>();

    Set<Class> functionClasses = ClassUtil
                                   .findClasses(org.apache.tajo.function.Function.class,
                                                 class_dir);

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
