package eu.leads.processor.planner;

import com.google.gson.Gson;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.plan.PlanNode;
import eu.leads.processor.core.plan.SQLPlan;
import eu.leads.processor.core.plan.SQLQuery;
import leads.tajo.module.TaJoModule;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.TajoConstants;
import org.apache.tajo.algebra.CreateTable;
import org.apache.tajo.algebra.Expr;
import org.apache.tajo.algebra.Insert;
import org.apache.tajo.algebra.OpType;
import org.apache.tajo.catalog.*;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.common.TajoDataTypes;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.engine.function.builtin.SumInt;
import org.apache.tajo.engine.json.CoreGsonHelper;
import org.apache.tajo.engine.planner.PlanningException;
import org.apache.tajo.engine.planner.logical.InsertNode;
import org.apache.tajo.engine.planner.logical.LogicalRootNode;
import org.apache.tajo.master.session.Session;
import org.apache.tajo.util.KeyValueSet;
import org.vertx.java.core.json.JsonArray;

/**
 * Created by vagvaz on 8/27/14.
 */
public class LogicalPlanVisitorTest {
    public static final java.lang.String TAJO_VERSION = "0.9.0-SNAPSHOT";
    public static final java.lang.String SYSTEM_CONF_FILENAME = "system_conf.xml";
    public static final java.lang.String SYSTEM_DIR_NAME = "system";
    public static final java.lang.String WAREHOUSE_DIR_NAME = "warehouse";
    public static final java.lang.String SYSTEM_RESOURCE_DIR_NAME = "resource";
    public static final java.lang.String RESULT_DIR_NAME = "RESULT";
    public static final java.lang.String INSERT_OVERWIRTE_OLD_TABLE_NAME = "OLD_TABLE";
    public static final java.lang.String DEFAULT_TABLESPACE_NAME = "default";
    public static final java.lang.String DEFAULT_DATABASE_NAME = "default";
    public static final java.lang.String DEFAULT_SCHEMA_NAME = "public";
    public static final java.lang.String EMPTY_STRING = "";
    private static TaJoModule Mymodule;
    private static CatalogClient catalog;

    public static void main(String[] args) throws Exception {
        Session session = new Session("0", "LeadsTestModule",
                                         DEFAULT_DATABASE_NAME);
        Mymodule = new TaJoModule();
        Mymodule.init_connection("127.0.0.1", 5998);
//        String line = "select count(sentiment) as pipo,links,url as lala,pagerank as foobar,count(domainname) as papari   from webpages where lala = 'ddsaf' and foobar =9 group by links,lala, foobar having papari > 5 order by papari";
           // "select dept.deptname,dept.tmsp from dept join ( select score,phone,deptname,sumtest(score) as tmsp from score group by score,phone, deptname) s on dept.deptname = s.deptname and s.tmsp = dept.tmsp";
        String line = "select url from webpages ";
        System.out.println(line);



        Expr expr = null;
        try {
            expr = Mymodule.parseQuery(line);

        } catch (Exception e) {
            e.printStackTrace();
        }
        SQLPlan plan = null;
        try {
            SQLQuery query = new SQLQuery("foo",line);
            plan = getLogicaSQLPlan(expr,query);
        } catch (PlanningException e) {
            e.printStackTrace();
        }
        if(plan == null) {
            System.err.println("PLAN is NULL");
            System.exit(-1);
        }
        String res = TaJoModule.Optimize(session, expr);
        Gson gson = new Gson();
        LogicalRootNode n = CoreGsonHelper.fromJson(res, LogicalRootNode.class);

            if (n.getChild() != null) {

                plan = new SQLPlan("queryId-custom", n);
                System.out.println(plan.asJsonObject().encodePrettily());
            } else {
                CreateTable table;
                System.out.println(res);
            }

        }














//        Expr expr = TaJoModule.parseQuery(line);
//        SQLPlan plan = null;
//        if(expr.getType().equals(OpType.Insert)){
//           plan = createInsertSQLPlan(session,expr);
//        }
//        if(plan == null) {
//        String res = TaJoModule.Optimize(session, expr);
//        Gson gson = new Gson();
//        LogicalRootNode n = CoreGsonHelper.fromJson(res, LogicalRootNode.class);
//
//            if (n.getChild() != null) {
//
//                plan = new SQLPlan("queryId-custom", n);
//                System.out.println(plan.asJsonObject().encodePrettily());
//            } else {
//                CreateTable table;
//                System.out.println(res);
//            }
//
//        }
//        else{
//            System.out.println(plan.asJsonObject().encodePrettily());
//        }


        //      List<LogicalPlan.QueryBlock> childblocks = plan.getChildBlocks(rootBlock);
        //      System.out.println("result\n"+res);
        //      System.out.println("explan " + explan);



    private static SQLPlan getLogicaSQLPlan(Expr expr, SQLQuery sqlQuery) throws PlanningException {
        SQLPlan result = null;
        Session session =
                new Session(sqlQuery.getId(), sqlQuery.getUser(), StringConstants.DEFAULT_DATABASE_NAME);
        if(expr.getType().equals(OpType.Insert)){
            result = createInsertSQLPlan(session,expr);
        }
        //Optimize plan
        String planAsString = null;


        try {
            planAsString = Mymodule.Optimize(session, expr);
        } catch (Exception e) {
            throw e;
        }
        LogicalRootNode n = CoreGsonHelper.fromJson(planAsString, LogicalRootNode.class);
        result = new SQLPlan(sqlQuery.getId(), n);
        return result;
    }








    private static SQLPlan createInsertSQLPlan(Session session, Expr expr) {
        SQLPlan result = new SQLPlan();
        LogicalRootNode rootNode = new LogicalRootNode(1);
        Insert opInsert = (Insert)expr;
        Expr subexpr = opInsert.getSubQuery();

//        insertNode.setInSchema(opInsert);
        try {
            String result2 = TaJoModule.Optimize( session,subexpr);
            Gson gson = new Gson();
            LogicalRootNode n = CoreGsonHelper.fromJson(result2, LogicalRootNode.class);
            result = new SQLPlan(n);
            PlanNode node = result.getNode(result.getQueryId()+".0");
            if(opInsert.hasTargetColumns()) {
                JsonArray array = new JsonArray(opInsert.getTargetColumns());
                node.getConfiguration().getObject("body").putArray("columnNames", array);
            }
            else{
                JsonArray array = new JsonArray();
                Schema tableSchema = TaJoModule.getTableSchema(opInsert.getTableName());
                for(Column c : tableSchema.getColumns()){
                    array.add(c.getSimpleName());
                }
                node.getConfiguration().getObject("body").putArray("columnNames",array);
            }
            result.updateNode(node);
        } catch (PlanningException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void TestsetUp() throws Exception {
        // connect to the server in order to create the schemas
        TajoConf c = new TajoConf();

        catalog = new CatalogClient(c, "localhost", 5998);
        catalog.createTablespace(DEFAULT_TABLESPACE_NAME,
                                    "leadsfs://localhost:5998/warehouse");
        catalog.createDatabase(DEFAULT_DATABASE_NAME, DEFAULT_TABLESPACE_NAME);

        Schema schema = new Schema();
        schema.addColumn("name", TajoDataTypes.Type.TEXT);
        schema.addColumn("empid", TajoDataTypes.Type.INT4);
        schema.addColumn("deptname", TajoDataTypes.Type.TEXT);

        Schema schema2 = new Schema();
        schema2.addColumn("deptname", TajoDataTypes.Type.TEXT);
        schema2.addColumn("manager", TajoDataTypes.Type.TEXT);
        schema2.addColumn("tmsp", TajoDataTypes.Type.INT4);


        Schema schema3 = new Schema();
        schema3.addColumn("deptname", TajoDataTypes.Type.TEXT);
        schema3.addColumn("score", TajoDataTypes.Type.INT4);
        schema3.addColumn("phone", TajoDataTypes.Type.INT4);
        schema3.addColumn("tmsp", TajoDataTypes.Type.INT4);

        Schema schema4 = new Schema();
        schema4.addColumn("deptname", TajoDataTypes.Type.TEXT);
        schema4.addColumn("score", TajoDataTypes.Type.INT4);
        schema4.addColumn("phone", TajoDataTypes.Type.INT4);
        schema4.addColumn("tmsp", TajoDataTypes.Type.INT4);

        Schema schema5 = new Schema();
        schema5.addColumn("deptname", TajoDataTypes.Type.TEXT);
        schema5.addColumn("score", TajoDataTypes.Type.INT4);
        schema5.addColumn("phone", TajoDataTypes.Type.INT4);


        TableMeta meta = CatalogUtil.newTableMeta(CatalogProtos.StoreType.MEM);
        TableDesc people = new TableDesc(
                                            CatalogUtil
                                                .buildFQName(TajoConstants.DEFAULT_DATABASE_NAME,
                                                                "employee"), schema, meta,
                                            new Path("testdir"));
        catalog.createTable(people);


        TableDesc student = new org.apache.tajo.catalog.TableDesc(
                                                                     CatalogUtil
                                                                         .buildFQName(DEFAULT_DATABASE_NAME,
                                                                                         "dept"),
                                                                     schema2,
                                                                     CatalogProtos.StoreType.MEM,
                                                                     new KeyValueSet(),
                                                                     new Path("student"));
        catalog.createTable(student);

        TableDesc score = new org.apache.tajo.catalog.TableDesc(
                                                                   CatalogUtil
                                                                       .buildFQName(DEFAULT_DATABASE_NAME,
                                                                                       "score"),
                                                                   schema3,
                                                                   CatalogProtos.StoreType.MEM,
                                                                   new KeyValueSet(),
                                                                   new Path("score"));
        catalog.createTable(score);

        TableDesc score2 = new org.apache.tajo.catalog.TableDesc(
                                                                    CatalogUtil
                                                                        .buildFQName(DEFAULT_DATABASE_NAME,
                                                                                        "score2"),
                                                                    schema4,
                                                                    CatalogProtos.StoreType.MEM,
                                                                    new KeyValueSet(),
                                                                    new Path("score2"));
        catalog.createTable(score2);

        TableDesc score3 = new org.apache.tajo.catalog.TableDesc(
                                                                    CatalogUtil
                                                                        .buildFQName(DEFAULT_DATABASE_NAME,
                                                                                        "score3"),
                                                                    schema5,
                                                                    CatalogProtos.StoreType.MEM,
                                                                    new KeyValueSet(),
                                                                    new Path("score3"));
        catalog.createTable(score3);

        FunctionDesc funcDesc =
            new FunctionDesc("sumtest", SumInt.class, CatalogProtos.FunctionType.AGGREGATION,
                                CatalogUtil.newSimpleDataType(TajoDataTypes.Type.INT4),
                                CatalogUtil.newSimpleDataTypeArray(TajoDataTypes.Type.INT4));


        catalog.createFunction(funcDesc);
    }
}
