/**
 * 
 */
package leads.tajo.module;

import grammar.LeadsSQLParser;
import grammar.LeadsSQLParser.SqlContext;
import grammar.SQLLexer;

import java.io.IOException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.tajo.algebra.Expr;
import org.apache.tajo.catalog.CatalogService;
import org.apache.tajo.engine.json.CoreGsonHelper;
import org.apache.tajo.engine.parser.SQLSyntaxError;
import org.apache.tajo.engine.planner.LeadsLogicalOptimizer;
import org.apache.tajo.engine.planner.LogicalPlan;
import org.apache.tajo.engine.planner.LogicalPlanner;
import org.apache.tajo.engine.planner.PlanningException;
import org.apache.tajo.engine.planner.logical.LogicalNode;
import org.apache.tajo.engine.planner.logical.NodeType;
import org.apache.tajo.master.session.Session;
import org.apache.tajo.catalog.*;
import org.apache.tajo.conf.TajoConf;

import com.google.protobuf.TextFormat.ParseException;

/**
 * @author tr
 * 
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

	public void init_connection(String ip, int port){
		try {
			catalog = new CatalogClient(c, ip, port);	
			System.out.println("Connection to Catalog Server " + ip +':'+ port + " initialized !");
		} catch (IOException e) {
			catalog=null;
			System.out.println("Unable to connect to the catalog Server" + ip +':'+ port );
			e.printStackTrace();
		}
	}
	public static String Optimize( Session session,
			String sql) throws PlanningException,ParseException {
		
		if (catalog == null) {
			System.err.println("Catalog is Uninitialized");
			return null;
		}
		try {
			Expr expr = sqlAnalyzer.parse(sql);
			return Optimize(session, expr);
		} catch (SQLSyntaxError e) {
			throw new SQLSyntaxError("Parse Error" + e.getMessage());
		}
	}

	public static Expr parseQuery(String sql) {
		System.out.print(sql.length());
		ANTLRInputStream input = new ANTLRInputStream(sql);
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
<<<<<<< HEAD
=======


>>>>>>> lefteris
		return CoreGsonHelper.getPrettyInstance().toJson(
				newPlan.getRootBlock().getRoot());
   }
}
