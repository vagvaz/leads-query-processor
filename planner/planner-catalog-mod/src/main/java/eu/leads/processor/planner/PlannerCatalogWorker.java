package eu.leads.processor.planner;

import com.google.protobuf.ServiceException;
import eu.leads.processor.common.StringConstants;
import leads.tajo.catalog.LeadsCatalog;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.TajoConstants;
import org.apache.tajo.catalog.*;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.common.TajoDataTypes.DataType;
import org.apache.tajo.common.TajoDataTypes.Type;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.master.TajoMaster;
import org.apache.tajo.util.KeyValueSet;
import org.vertx.java.platform.Verticle;

import java.io.IOException;

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
      catalogServer = new LeadsCatalog(null);
      try {
         catalogServer.StartServer();
         createInitialTables();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void createInitialTables() {
      CatalogClient catalog = null;
      try {
         catalog = new CatalogClient(conf, "localhost", 5998);
      } catch (IOException e) {
         e.printStackTrace();
      }
      catalog.createTablespace(StringConstants.DEFAULT_TABLE_SPACE,
                                      "leadsfs://localhost:5998/"+StringConstants.DEFAULT_TABLE_SPACE);
      catalog.createDatabase(StringConstants.DEFAULT_DATABASE_NAME, StringConstants.DEFAULT_TABLE_SPACE);

      try {
         for (FunctionDesc funcDesc : TajoMaster.initBuiltinFunctions()) {
            catalog.createFunction(funcDesc);
         }
      } catch (ServiceException e) {
         e.printStackTrace();
      }

      Schema schema = new Schema();
      schema.addColumn("webpageurl", Type.TEXT);
      schema.addColumn("name", Type.TEXT);
      schema.addColumn("sentimentscore", Type.INT4);
      Schema schema2 = new Schema();
      schema2.addColumn("body ", Type.TEXT);
      schema2.addColumn("sentiment", Type.TEXT);
      schema2.addColumn("pagerank", Type.INT4);
      schema2.addColumn("domainname", Type.TEXT);
      schema2.addColumn("links", Type.TEXT);
      schema2.addColumn("url", Type.TEXT);

      Schema schema3 = new Schema();
      schema3.addColumn("deptname", Type.TEXT);
      schema3.addColumn("score", Type.INT4);
      schema3.addColumn("phone", Type.INT4);

      Schema schema4 = new Schema();
      schema4.addColumn("deptname", Type.TEXT);
      schema4.addColumn("score", Type.INT4);
      schema4.addColumn("phone", Type.INT4);

      Schema schema5 = new Schema();
      schema5.addColumn("deptname", Type.TEXT);
      schema5.addColumn("score", Type.INT4);
      schema5.addColumn("phone", Type.INT4);


      TableMeta meta = CatalogUtil.newTableMeta(CatalogProtos.StoreType.SEQUENCEFILE);
      TableDesc Entities = new TableDesc(
                                                CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME, "entities"), schema, meta,
                                                getTestDir("entities"));
      catalog.createTable(Entities);


      TableDesc Webpages = new org.apache.tajo.catalog.TableDesc(
                                                                        CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME, "webpages"),
                                                                        schema2, CatalogProtos.StoreType.MEM, new KeyValueSet(), getTestDir("webpages"));
      catalog.createTable(Webpages);

      TableDesc score = new org.apache.tajo.catalog.TableDesc(
                                                                     CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME, "score"),
                                                                     schema3, CatalogProtos.StoreType.MEM, new KeyValueSet(), getTestDir("score"));
      catalog.createTable(score);

      TableDesc score2 = new org.apache.tajo.catalog.TableDesc(
                                                                      CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME, "score2"),
                                                                      schema4, CatalogProtos.StoreType.MEM, new KeyValueSet(), getTestDir("score2"));
      catalog.createTable(score2);

      TableDesc score3 = new org.apache.tajo.catalog.TableDesc(
                                                                      CatalogUtil.buildFQName(StringConstants.DEFAULT_DATABASE_NAME, "score3"),
                                                                      schema5, CatalogProtos.StoreType.MEM, new KeyValueSet(), getTestDir("score3"));
      catalog.createTable(score3);
   }

   private Path getTestDir(String table) {
      return new Path(StringConstants.DEFAULT_PATH+"/"+table);
   }
}
