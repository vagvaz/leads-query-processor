/**
 *
 */
package leads.tajo.module;

import com.google.protobuf.TextFormat.ParseException;
import eu.leads.processor.common.StringConstants;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.tajo.ConfigKey;
import org.apache.tajo.OverridableConf;
import org.apache.tajo.SessionVars;
import org.apache.tajo.TajoConstants;
import org.apache.tajo.algebra.*;
import org.apache.tajo.catalog.*;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.common.TajoDataTypes;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.engine.json.CoreGsonHelper;
import org.apache.tajo.engine.parser.*;
import org.apache.tajo.engine.query.QueryContext;
import org.apache.tajo.plan.LogicalOptimizer;
import org.apache.tajo.plan.LogicalPlan;
import org.apache.tajo.plan.LogicalPlanner;
import org.apache.tajo.plan.PlanningException;
import org.apache.tajo.session.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * @author tr
 */
public class TaJoModule {

    private static LeadsSQLAnalyzer sqlAnalyzer = null;
    private static LogicalPlanner planner;
    private static LogicalOptimizer optimizer = null;
    private static CatalogClient catalog = null;
    private static TajoConf c = null;
    private static HashMap<String,Set<String>> primaryKeys = null;

    private static UserGroupInformation dummyUserInfo;
    private static QueryContext defaultContext;


    static {
        try {
            dummyUserInfo = UserGroupInformation.getCurrentUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public TaJoModule() {
        c = new TajoConf();
        optimizer = new LogicalOptimizer(c);
        sqlAnalyzer = new LeadsSQLAnalyzer();
        defaultContext = createContext(c,null);
        initializePrimaryColumns();
    }

    public static String Optimize(Session session,
                                     String sql) throws PlanningException, ParseException {

        if (catalog == null) {
            System.err.println("Catalog is Uninitialized");
            return null;
        }
        try {
            Expr expr = sqlAnalyzer.parse(sql);
           if( (expr instanceof UnaryOperator) || expr instanceof BinaryOperator)
               return Optimize(session, expr);
           else
              return expr.toJson();
        } catch (SQLSyntaxError e) {
            throw new SQLSyntaxError("Parse Error" + e.getMessage());
        }
    }

    public static Schema getTableSchema(String tableName) {
        TableDesc result = catalog.getTableDesc(StringConstants.DEFAULT_DATABASE_NAME, tableName);

        return result.getLogicalSchema();
    }

    public static Expr parseQuery(String sql) {
        System.out.print(sql.length());
        sql=check_insert(  sql);
        ANTLRInputStream input = new ANTLRInputStream(sql);
        LeadsSQLLexer lexer = new LeadsSQLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LeadsSQLParser parser = new LeadsSQLParser(tokens);
        parser.setBuildParseTree(true);
        LeadsSQLAnalyzer visitor = new LeadsSQLAnalyzer();
        LeadsSQLParser.SqlContext context = parser.sql();
        if (context.statement() != null)
            return visitor.visitSql(context);

        return null;
    }

    protected static String check_insert(String sql){
        sql = sql.trim();
        final String[] arr = sql.split(" ", 2);
        if(arr[0].equalsIgnoreCase("insert")) {
            //
            if(sql.toLowerCase().contains("values")) {
                String[] newsql = sql.split("(?i)VALUES");
                sql = newsql[0] + " select " + newsql[1].replaceAll("\\(|\\)", "");
                System.out.print("Fixed sql: "+sql);
            }
//			else{
//				System.out.print("Error incorrect insert syntax "+sql);
//				return null;
//			}

        }
        if(arr[0].equalsIgnoreCase("update")) {
            //
            String tablename = sql.trim().split(" ")[1]; //assuming after update is tablename
            if(!sql.toLowerCase().contains("set") ||!sql.toLowerCase().contains("where") ) {
                System.out.print("Error incorrect update syntax "+sql);
                return null;
            }
            int start = sql.toLowerCase().indexOf("set")+3;
            int end = sql.toLowerCase().indexOf("where");
            String columnValuePairs = sql.substring(start,end);
            String [] pairs = columnValuePairs.split(",");
            String collumns = " (";
            String values = " SELECT ";
            for(String pair:pairs)
            {
                if(collumns.length()>2) {
                    collumns += ", ";
                    values +=  ", ";
                }
                String [] collumnValue = pair.trim().split("=");
                if(collumnValue.length!=2){
                    System.out.print("Error incorrect update syntax "+sql);
                    return null;
                }
                collumns += collumnValue[0] ;
                values += collumnValue[1];
            }
            collumns+=")";
            sql = " INSERT OVERWRITE INTO "+ tablename  + " " + collumns + values + " FROM " + tablename + " "+ sql.substring(end);
            System.out.print("Fixed sql: "+sql);
        }
        if(arr[0].equalsIgnoreCase("delete")){
            //
            String tablename = sql.trim().split(" ")[2]; //assuming after update is tablename
            if(!sql.toLowerCase().contains("from") ||!sql.toLowerCase().contains("where") ) {
                System.out.print("Error incorrect update syntax "+sql);
                return null;
            }

            int end = sql.toLowerCase().indexOf("where");

            String values = " SELECT *";

            sql = " DELETE FROM "+ tablename  + " SELECT * FROM " + tablename + " "+ sql.substring(end);
            System.out.print("Fixed sql: "+sql);
        }

        return sql;

    }

    public static boolean createTable(CreateTable ctCommand)
    {
        boolean result = false;
        if(ctCommand==null){
            return result;
        }
        ColumnDefinition[] columns = ctCommand.getTableElements();
        Schema newTableSchema = new Schema();
        for(ColumnDefinition c : columns){
            newTableSchema.addColumn(c.getColumnName(), TajoDataTypes.Type.valueOf(c.getTypeName()));
        }
        TableMeta newTableMeta = CatalogUtil.newTableMeta(CatalogProtos.StoreType.SEQUENCEFILE);
        Path tablePath = getTablePath(ctCommand.getTableName());
        TableDesc desc =// new TableDesc(CatalogUtil
               // .buildFQName(StringConstants.DEFAULT_DATABASE_NAME,ctCommand.getTableName()),newTableSchema,newTableMeta,tablePath);
        CatalogUtil.newTableDesc(CatalogUtil
                .buildFQName(StringConstants.DEFAULT_DATABASE_NAME,ctCommand.getTableName()), newTableSchema, newTableMeta, tablePath);
        return catalog.createTable(desc);
    }

    private static Path getTablePath(String tableName) {
        return new Path(StringConstants.DEFAULT_PATH + "/" + tableName);
    }

    public static String Optimize(Session session, Expr expr) throws PlanningException {
        if (catalog == null) {
            // catalog.
            System.err.println("Catalog is Uninitialized");
            return null;
        }

        planner = new LogicalPlanner((CatalogService) catalog);

        LogicalPlan newPlan = null;
        try {
            newPlan = planner.createPlan(createContext(c,session), expr);
        } catch (PlanningException e) {
            throw new PlanningException("Unable to Create Plan: " + e.getMessage());
        }

        try {
            optimizer.optimize(newPlan);
        } catch (PlanningException e) {
            throw new PlanningException("Unable to Optimize Plan: " + e.getMessage());
        }
        return CoreGsonHelper.getPrettyInstance().toJson(
                                                            newPlan.getRootBlock().getRoot());
    }

    public void init_connection(String ip, int port) {
        try {
            catalog = new CatalogClient(c, ip, port);
            System.out
                .println("Connection to Catalog Server " + ip + ':' + port + " initialized !");
        } catch (IOException e) {
            catalog = null;
            System.out.println("Unable to connect to the catalog Server" + ip + ':' + port);
            e.printStackTrace();
        }
    }

    //FIX IT USE CATALOG INFO NOW !!
    private void initializePrimaryColumns(){
        primaryKeys = new HashMap<>();
        HashSet<String> commonKeys = new HashSet<>();
        commonKeys.add("uri");
        commonKeys.add("ts");
        primaryKeys.put(/*"crawler.*/"content", (Set<String>) commonKeys.clone());
        primaryKeys.put(/*"internal.*/"page", (Set<String>) commonKeys.clone());
        primaryKeys.put(/*"internal.*/"urldirectory", (Set<String>) commonKeys.clone());
        primaryKeys.put(/*"internal.*/"urldirectory_ecom", (Set<String>) commonKeys.clone());
        primaryKeys.put(/*"leads.*/"page_core", (Set<String>) commonKeys.clone());
        primaryKeys.put(/*"leads.*/"site", (Set<String>) commonKeys.clone());

        HashSet<String> Keys = (HashSet<String>) commonKeys.clone();
        Keys.add("partid");
        Keys.add("keywords");
        primaryKeys.put(/*"leads.*/"keywords", (Set<String>) Keys.clone());

        Keys = (HashSet<String>) commonKeys.clone();
        Keys.add("partid");
        Keys.add("resourceparttype");
        primaryKeys.put(/*"leads.*/"resourcepart", (Set<String>) Keys.clone());

        Keys= new HashSet<>();
        Keys.add("keywords");
        primaryKeys.put(/*"adidas.*/"adidas_keywords", (Set<String>) Keys.clone());

        Keys= new HashSet<>();
        Keys.add("uservisits");
        primaryKeys.put(/*"adidas.*/"sourceIP", (Set<String>) Keys.clone());
        primaryKeys.put(/*"adidas.*/"destURL", (Set<String>) Keys.clone());

        Keys= new HashSet<>();
        Keys.add("rankings");
        primaryKeys.put(/*"adidas.*/"pageURL", (Set<String>) Keys.clone());
    }

    public static Set<String> getPrimaryColumn(String tableName) {
        if(primaryKeys.containsKey(tableName))
            return primaryKeys.get(tableName);

        TableDesc desc = catalog.getTableDesc(tableName);
        Set<String> result = new HashSet<>();

        for(Column c : desc.getSchema().getColumns())
            result.add(c.getSimpleName());

        return result;
    }


    public void dropTable(DropTable expr) {
      String tableName = expr.getTableName();
       if(tableName.startsWith(StringConstants.DEFAULT_DATABASE_NAME+".")){
          catalog.dropTable(tableName);
       }
       else{
          catalog.dropTable(StringConstants.DEFAULT_DATABASE_NAME+"."+tableName);
       }
    }

    public static QueryContext createContext(TajoConf conf, Session session) {

        QueryContext context = null;
        if(session==null)
            context = new QueryContext(conf, createDummySession());
        else
            context = new QueryContext(conf, session);

        OverridableConf userSessionVars;
        userSessionVars = new OverridableConf(new TajoConf(), new ConfigKey.ConfigType[]{ConfigKey.ConfigType.SESSION});
        SessionVars[] var0 = SessionVars.values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            SessionVars var = var0[var2];
            String value = System.getProperty(var.keyname());
            if(value != null) {
                userSessionVars.put(var, value);
            }
        }
        context.putAll(userSessionVars.getAllKeyValus());
        return context;
    }

    public static Session createDummySession() {
        return new Session(UUID.randomUUID().toString(), dummyUserInfo.getUserName(), TajoConstants.DEFAULT_DATABASE_NAME);
    }
}
