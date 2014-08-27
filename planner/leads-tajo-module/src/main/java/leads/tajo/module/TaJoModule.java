/**
 * 
 */
package leads.tajo.module;

import grammar.LeadsSQLParser;
import grammar.LeadsSQLParser.SqlContext;
import grammar.SQLLexer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
import org.apache.tajo.engine.planner.logical.LogicalNodeVisitor;
import org.apache.tajo.master.session.Session;
import org.apache.tajo.catalog.*;
import org.apache.tajo.conf.TajoConf;

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
			catalog = new CatalogClient(c, ip, port); // /TESTING	
			System.out.println("Connection to Catalog Server " + ip +':'+ port + " initialized !");
		} catch (IOException e) {
			catalog=null;
			System.out.println("Unable to connect to the catalog Server" + ip +':'+ port );
			e.printStackTrace();
		}
	}
	public static String Optimize( Session session,
			String sql) throws PlanningException {
		
		if (catalog == null) {
			System.err.println("Catalog is Uninitialized");
			return null;
		}
		try {
			Expr expr = sqlAnalyzer.parse(sql);

			// Expr expr = sqlAnalyzer.parse(sql);
			//System.out.println("Query: " + sql);
			//System.out.println("Parsed:" + expr.toJson());
			planner = new LogicalPlanner((CatalogService) catalog);


			LogicalPlan newPlan = planner.createPlan(session, expr);
			LogicalNode plan = newPlan.getRootBlock().getRoot();
			plan = optimizer.optimize(newPlan);

			//System.out.println("END");
			// return newPlan;
         LogicalNodeVisitor visitor;
			return CoreGsonHelper.getPrettyInstance().toJson(
					newPlan.getRootBlock().getRoot());
		} catch (SQLSyntaxError e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw new PlanningException("Parse Error");
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
			// System.out.print(context + " dsf ");
			return visitor.visitSql(context);
		// return null;
		return null;
	}

   public String Optimize(Session session, Expr expr) {
      return null;
   }
}
