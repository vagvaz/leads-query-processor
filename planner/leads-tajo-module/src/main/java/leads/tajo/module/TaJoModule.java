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
import org.apache.tajo.engine.planner.LeadsLogicalOptimizer;
import org.apache.tajo.engine.planner.LogicalPlan;
import org.apache.tajo.engine.planner.LogicalPlanner;
import org.apache.tajo.engine.planner.PlanningException;
import org.apache.tajo.engine.planner.logical.LogicalNode;
import org.apache.tajo.master.session.Session;
import org.apache.tajo.catalog.*;
import org.apache.tajo.conf.TajoConf;

/**
 * @author tr
 * 
 */
public class TaJoModule {

	private static SQLAnalyzer sqlAnalyzer = null;
	private static LogicalPlanner planner;
	private static LeadsLogicalOptimizer optimizer = null;
	private static CatalogClient catalog = null;
	private static TajoConf c = null;

	public TaJoModule() {
		c = new TajoConf();
		optimizer = new LeadsLogicalOptimizer(c);
		sqlAnalyzer = new SQLAnalyzer();
		
		try {
			catalog = new CatalogClient(c, "127.0.0.1", 5998); // /TESTING
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String Optimize( Session session,
			String sql) throws PlanningException {
		
		if (catalog == null) {
			// System.err.println();
			throw new PlanningException("No catalog initialized");

		}
		try {
			Expr expr = sqlAnalyzer.parse(sql);

			// Expr expr = sqlAnalyzer.parse(sql);
			System.out.println("Query: " + sql);
			System.out.println("Parsed:" + expr.toJson());
			planner = new LogicalPlanner((CatalogService) catalog);

			// Session session = LocalTajoTestingUtility
			// .createDummySession();
			LogicalPlan newPlan = planner.createPlan(session, expr);
			LogicalNode plan = newPlan.getRootBlock().getRoot();

			try {
				PrintWriter writer = new PrintWriter("plan.json", "UTF-8");
				writer.println("Query: " + sql);
				writer.println(CoreGsonHelper.getPrettyInstance().toJson(plan));
				writer.close();

				System.out.println("Plan0: \n" + newPlan.toString());// CoreGsonHelper.getPrettyInstance().toJson(newPlan));
				System.out.println("Run optimization ");
				optimizer.optimize(newPlan);

				writer = new PrintWriter("optimized" + ".json", "UTF-8");
				writer.println("Query: " + sql);
				writer.println(CoreGsonHelper.getPrettyInstance().toJson(
						newPlan.getRootBlock().getRoot()));
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("END");
			// return newPlan;

			return CoreGsonHelper.getPrettyInstance().toJson(
					newPlan.getRootBlock().getRoot());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLSyntaxError e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw new PlanningException("Parse Error");
		}
		return null;

	}

	public static Expr parseQuery(String sql) {
		System.out.print(sql.length());
		ANTLRInputStream input = new ANTLRInputStream(sql);
		SQLLexer lexer = new SQLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LeadsSQLParser parser = new LeadsSQLParser(tokens);
		parser.setBuildParseTree(true);
		SQLAnalyzer visitor = new SQLAnalyzer();
		SqlContext context = parser.sql();
		if (context.statement() != null)
			// System.out.print(context + " dsf ");
			return visitor.visitSql(context);
		// return null;
		return null;
	}

}
