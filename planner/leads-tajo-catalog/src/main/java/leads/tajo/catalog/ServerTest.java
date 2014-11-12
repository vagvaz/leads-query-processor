package leads.tajo.catalog;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.TajoConstants;
import org.apache.tajo.catalog.*;
import org.apache.tajo.catalog.proto.CatalogProtos.StoreType;
import org.apache.tajo.common.TajoDataTypes.Type;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.master.TajoMaster;

import java.io.IOException;
import java.util.UUID;

import static org.apache.tajo.TajoConstants.DEFAULT_DATABASE_NAME;
import static org.apache.tajo.TajoConstants.DEFAULT_TABLESPACE_NAME;

/**
 * @author tr
 */
public class ServerTest {
	private static CatalogService catalog = null;
	//private static MiniCatalogServer catalogServer;
	private static LeadsCatalog mycatalogServer = null;

	private static Log LOG = LogFactory.getLog("LeadsLog");
	static TajoConf conf = null;



	public static void main(String[] args) throws IOException {
	
		try {
			TestsetUp();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	
	  public static Path getTestDir(String dir) throws IOException {
		    Path path = new Path("target/test-data",dir);
		    FileSystem fs = FileSystem.getLocal(new Configuration());
		    cleanupTestDir(dir);
		    fs.mkdirs(path);

		    return fs.makeQualified(path);
		  }

		  public static void cleanupTestDir(String dir) throws IOException {
		    Path path = new Path(dir);
		    FileSystem fs = FileSystem.getLocal(new Configuration());
		    if(fs.exists(path)) {
		      fs.delete(path, true);
		    }
		  }

		  public static Path getTestDir() throws IOException {
		    String randomStr = UUID.randomUUID().toString();
		    Path path = new Path("target/test-data", randomStr);
		    FileSystem fs = FileSystem.getLocal(new Configuration());
		    if(fs.exists(path)) {
		      fs.delete(path, true);
		    }

		    fs.mkdirs(path);

		    return fs.makeQualified(path);
		  }
	
	public static void TestsetUp() throws Exception {

		mycatalogServer = new LeadsCatalog(null);
		mycatalogServer.StartServer();

		// connect to the server in order to create the schemas
		TajoConf c = new TajoConf();

		catalog = new CatalogClient(c, "localhost", 5998);
		catalog.createTablespace(DEFAULT_TABLESPACE_NAME,
				"leadsfs://localhost:5998/warehouse");
		catalog.createDatabase(DEFAULT_DATABASE_NAME, DEFAULT_TABLESPACE_NAME);

	    for (FunctionDesc funcDesc : TajoMaster.initBuiltinFunctions()) {
            System.out.println("Bultin func: " + funcDesc.getFuncType());
	        catalog.createFunction(funcDesc);
	      }

		Schema schema = new Schema();
		schema.addColumn("webpageurl", Type.TEXT);
		schema.addColumn("name", Type.TEXT);
		schema.addColumn("sentimentscore", Type.INT4);
//
		Schema schema2 = new Schema();
		schema2.addColumn("body ", Type.TEXT);
		schema2.addColumn("sentiment", Type.TEXT);
		schema2.addColumn("pagerank", Type.INT4);
        schema2.addColumn("domainname", Type.TEXT);
        schema2.addColumn("links", Type.TEXT);
        schema2.addColumn("url", Type.TEXT);
//
//        Schema schema3 = new Schema();
//		schema3.addColumn("deptname", Type.TEXT);
//		schema3.addColumn("score", Type.INT4);
//		schema3.addColumn("phone", Type.INT4);
//
//		Schema schema4 = new Schema();
//		schema4.addColumn("deptname", Type.TEXT);
//		schema4.addColumn("score", Type.INT4);
//		schema4.addColumn("phone", Type.INT4);
//
//		Schema schema5 = new Schema();
//		schema5.addColumn("deptname", Type.TEXT);
//		schema5.addColumn("score", Type.INT4);
//		schema5.addColumn("phone", Type.INT4);
//
//
		TableMeta meta = CatalogUtil.newTableMeta(StoreType.MEM);
	    TableDesc Entities = new TableDesc(
	            CatalogUtil.buildFQName(TajoConstants.DEFAULT_DATABASE_NAME, "entities"), schema, meta,
	            getTestDir());
	        catalog.createTable(Entities);
//
//
		TableDesc Webpages = new TableDesc(
				CatalogUtil.buildFQName(DEFAULT_DATABASE_NAME, "webpages"),schema2,meta, getTestDir());
		catalog.createTable(Webpages);


        catalog.createDatabase("internal", TajoConstants.DEFAULT_TABLESPACE_NAME);
        catalog.createDatabase("crawler", TajoConstants.DEFAULT_TABLESPACE_NAME);
        catalog.createDatabase("leads", TajoConstants.DEFAULT_TABLESPACE_NAME);
        catalog.createDatabase("adidas", TajoConstants.DEFAULT_TABLESPACE_NAME);


        schema = new Schema();
        schema.addColumn("uri", Type.TEXT);
        schema.addColumn("ts", Type.INT8);
        schema.addColumn("content", Type.TEXT);
        //PRIMARY KEY (uri, ts)
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("crawler", "content"),schema,meta, getTestDir()));


        schema = new Schema();
        schema.addColumn("uri", Type.TEXT);
        schema.addColumn("ts", Type.INT8);
        schema.addColumn("pagetypeassumption", Type.TEXT);
        schema.addColumn("ecomfeatures", Type.TEXT);
        schema.addColumn("extractioncandidates", Type.TEXT);
        schema.addColumn("successfulextractions", Type.TEXT);
        //PRIMARY KEY (uri, ts)
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("internal", "page"),schema,meta, getTestDir()));


        schema = new Schema();
        schema.addColumn("uri", Type.TEXT);
        schema.addColumn("ts", Type.INT8);
        schema.addColumn("dirassumption", Type.TEXT);
        schema.addColumn("ecomassumptionpagesno", Type.TEXT);
        schema.addColumn("pagesno", Type.TEXT);
        //PRIMARY KEY (uri, ts)
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("internal", "urldirectory"),schema,meta, getTestDir()));


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
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("internal", "urldirectory_ecom"),schema,meta, getTestDir()));



        schema = new Schema();
        schema.addColumn("uri", Type.TEXT);
        schema.addColumn("ts", Type.INT8);
        schema.addColumn("partid", Type.TEXT);
        schema.addColumn("keywords", Type.TEXT);
        schema.addColumn("relevance", Type.TEXT);
        schema.addColumn("sentiment", Type.TEXT);
        //	PRIMARY KEY (uri,ts,partid,keywords)
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("leads", "keywords"),schema,meta, getTestDir()));


        schema = new Schema();
        schema.addColumn("uri", Type.TEXT);
        schema.addColumn("ts", Type.INT8);
        schema.addColumn("fqdnurl", Type.TEXT);
        schema.addColumn("lang", Type.TEXT);
        schema.addColumn("maincontent", Type.TEXT);
        schema.addColumn("sentiment", Type.TEXT);
        schema.addColumn("textcontent", Type.TEXT);
        schema.addColumn("type", Type.TEXT);
        //	PRIMARY KEY (uri,ts)
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("leads", "page_core"),schema,meta, getTestDir()));

        schema = new Schema();
        schema.addColumn("uri", Type.TEXT);
        schema.addColumn("ts", Type.INT8);
        schema.addColumn("partid", Type.TEXT);
        schema.addColumn("resourceparttype", Type.TEXT);
        schema.addColumn("resourcepartvalue", Type.TEXT);
        //PRIMARY KEY (uri,ts,partid,resourceparttype)
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("leads", "resourcepart"),schema,meta, getTestDir()));


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
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("leads", "site"),schema,meta, getTestDir()));


        schema = new Schema();
        schema.addColumn("keywords", Type.TEXT);
        //	PRIMARY KEY (keywords)
        catalog.createTable(new TableDesc(
                CatalogUtil.buildFQName("adidas", "keywords"),schema,meta, getTestDir()));
//
//		TableDesc score = new org.apache.tajo.catalog.TableDesc(
//				CatalogUtil.buildFQName(DEFAULT_DATABASE_NAME, "score"),
//				schema3, StoreType.MEM, new KeyValueSet(), getTestDir("score"));
//		catalog.createTable(score);
//
//		TableDesc score2 = new org.apache.tajo.catalog.TableDesc(
//				CatalogUtil.buildFQName(DEFAULT_DATABASE_NAME, "score2"),
//				schema4, StoreType.MEM, new KeyValueSet(), getTestDir("score2"));
//		catalog.createTable(score2);
//
//		TableDesc score3 = new org.apache.tajo.catalog.TableDesc(
//				CatalogUtil.buildFQName(DEFAULT_DATABASE_NAME, "score3"),
//				schema5, StoreType.MEM, new KeyValueSet(), getTestDir("score3"));
//		catalog.createTable(score3);
//
//	    FunctionDesc funcDesc = new FunctionDesc("sumtest", SumInt.class, FunctionType.AGGREGATION,
//	            CatalogUtil.newSimpleDataType(Type.INT4),
//	            CatalogUtil.newSimpleDataTypeArray(Type.INT4));
//
//
//		catalog.createFunction(funcDesc);
		System.out.println("TestLeadsCatalogServer Started");
	}
}
