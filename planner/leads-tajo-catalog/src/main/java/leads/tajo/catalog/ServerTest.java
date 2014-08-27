package leads.tajo.catalog;


import static org.apache.tajo.TajoConstants.DEFAULT_DATABASE_NAME;
import static org.apache.tajo.TajoConstants.DEFAULT_TABLESPACE_NAME;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.TajoConstants;
import org.apache.tajo.catalog.CatalogClient;
import org.apache.tajo.catalog.CatalogService;
import org.apache.tajo.catalog.CatalogUtil;
import org.apache.tajo.catalog.FunctionDesc;
import org.apache.tajo.catalog.Schema;
import org.apache.tajo.catalog.TableDesc;
import org.apache.tajo.catalog.TableMeta;
import org.apache.tajo.catalog.proto.CatalogProtos.FunctionType;
import org.apache.tajo.catalog.proto.CatalogProtos.StoreType;
import org.apache.tajo.common.TajoDataTypes.Type;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.engine.function.builtin.SumInt;
import org.apache.tajo.util.CommonTestingUtil;
import org.apache.tajo.util.KeyValueSet;

/**
 * @author tr
 * 
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

//		mycatalogServer = new LeadsCatalog(null);
//		mycatalogServer.StartServer();

		// connect to the server in order to create the schemas
		TajoConf c = new TajoConf();

		catalog = new CatalogClient(c, "147.27.14.80", 5998);
		catalog.createTablespace(DEFAULT_TABLESPACE_NAME,
				"leadsfs://localhost:5998/warehouse");
		catalog.createDatabase(DEFAULT_DATABASE_NAME, DEFAULT_TABLESPACE_NAME);

		Schema schema = new Schema();
		schema.addColumn("name", Type.TEXT);
		schema.addColumn("empid", Type.INT4);
		schema.addColumn("deptname", Type.TEXT);

		Schema schema2 = new Schema();
		schema2.addColumn("deptname", Type.TEXT);
		schema2.addColumn("manager", Type.TEXT);
		schema2.addColumn("tmsp", Type.INT4);
		

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

		
		
		
		
		TableMeta meta = CatalogUtil.newTableMeta(StoreType.MEM);
	    TableDesc people = new TableDesc(
	            CatalogUtil.buildFQName(TajoConstants.DEFAULT_DATABASE_NAME, "employee"), schema, meta,
	            getTestDir());
	        catalog.createTable(people);

	   	
		TableDesc student = new org.apache.tajo.catalog.TableDesc(
				CatalogUtil.buildFQName(DEFAULT_DATABASE_NAME, "dept"),
				schema2, StoreType.MEM, new KeyValueSet(), getTestDir("student"));
		catalog.createTable(student);

		TableDesc score = new org.apache.tajo.catalog.TableDesc(
				CatalogUtil.buildFQName(DEFAULT_DATABASE_NAME, "score"),
				schema3, StoreType.MEM, new KeyValueSet(), getTestDir("score"));
		catalog.createTable(score);

		TableDesc score2 = new org.apache.tajo.catalog.TableDesc(
				CatalogUtil.buildFQName(DEFAULT_DATABASE_NAME, "score2"),
				schema4, StoreType.MEM, new KeyValueSet(), getTestDir("score2"));
		catalog.createTable(score2);

		TableDesc score3 = new org.apache.tajo.catalog.TableDesc(
				CatalogUtil.buildFQName(DEFAULT_DATABASE_NAME, "score3"),
				schema5, StoreType.MEM, new KeyValueSet(), getTestDir("score3"));
		catalog.createTable(score3);

	    FunctionDesc funcDesc = new FunctionDesc("sumtest", SumInt.class, FunctionType.AGGREGATION,
	            CatalogUtil.newSimpleDataType(Type.INT4),
	            CatalogUtil.newSimpleDataTypeArray(Type.INT4));


		catalog.createFunction(funcDesc);
		System.out.println("TestLeadsCatalogServer Started");
	}
}
