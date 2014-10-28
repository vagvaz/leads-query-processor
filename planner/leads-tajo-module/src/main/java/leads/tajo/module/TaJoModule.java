/**
 *
 */
package leads.tajo.module;

import com.google.protobuf.TextFormat.ParseException;
import eu.leads.processor.common.StringConstants;
import grammar.LeadsSQLParser;
import grammar.LeadsSQLParser.SqlContext;
import grammar.SQLLexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.hadoop.fs.Path;
import org.apache.tajo.algebra.*;
import org.apache.tajo.catalog.*;
import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.common.TajoDataTypes;
import org.apache.tajo.conf.TajoConf;
import org.apache.tajo.engine.json.CoreGsonHelper;
import org.apache.tajo.engine.parser.SQLSyntaxError;
import org.apache.tajo.engine.planner.LeadsLogicalOptimizer;
import org.apache.tajo.engine.planner.LogicalPlan;
import org.apache.tajo.engine.planner.LogicalPlanner;
import org.apache.tajo.engine.planner.PlanningException;

import org.apache.tajo.master.session.Session;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * @author tr
 */
public class TaJoModule {

    private static LeadsSQLAnalyzer sqlAnalyzer = null;
    private static LogicalPlanner planner;
    private static LeadsLogicalOptimizer optimizer = null;
    private static CatalogClient catalog = null;
    private static TajoConf c = null;

    public TaJoModule() {
        c = new TajoConf();
        optimizer = new LeadsLogicalOptimizer(c);
        sqlAnalyzer = new LeadsSQLAnalyzer();
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
//        System.out.print(sql.length());
        String query = check_insert(sql);
        ANTLRInputStream input = new ANTLRInputStream(query);
        SQLLexer lexer = new SQLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LeadsSQLParser parser = new LeadsSQLParser(tokens);
        parser.setBuildParseTree(true);
        LeadsSQLAnalyzer visitor = new LeadsSQLAnalyzer();
        SqlContext context = parser.sql();
        if (context.statement() != null)
            return visitor.visitSql(context);

        return null;
    }

    private static String check_insert(String sql){
        sql = sql.trim();
        final String[] arr = sql.split(" ", 2);
        if(arr[0].equalsIgnoreCase("insert"))
            if(sql.toLowerCase().contains(" values")) {
                String[] newsql = sql.split("(?i)VALUES");
                sql = newsql[0] + " select " + newsql[1].replaceAll("\\(|\\)", "");
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
        TableDesc desc = new TableDesc(CatalogUtil
                .buildFQName(StringConstants.DEFAULT_DATABASE_NAME,ctCommand.getTableName()),newTableSchema,newTableMeta,tablePath);
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
            newPlan = planner.createPlan(session, expr);
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

    public static Set<String> getPrimaryColumn(String tableName) {
        TableDesc desc = catalog.getTableDesc(tableName);
        Set<String> result = new HashSet<>();
        for(Column c : desc.getSchema().getColumns()){
            result.add(c.getSimpleName());
        }
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
}
