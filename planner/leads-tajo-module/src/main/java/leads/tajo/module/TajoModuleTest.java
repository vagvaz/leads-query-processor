package leads.tajo.module;

import org.apache.tajo.algebra.CreateIndex;
import org.apache.tajo.algebra.Expr;
import org.apache.tajo.algebra.JsonHelper;
import org.apache.tajo.algebra.OpType;
import org.apache.tajo.engine.parser.SQLSyntaxError;
import org.apache.tajo.session.Session;

import java.io.*;

import static org.apache.tajo.TajoConstants.DEFAULT_DATABASE_NAME;


public class TajoModuleTest {
	private static TaJoModule Mymodule;
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Session session = new Session("0", "LeadsTestModule",
							DEFAULT_DATABASE_NAME);
		
		Mymodule = new TaJoModule();
		Mymodule.init_connection("localhost", 5998);
		BufferedReader in = null;
		try {
			
			in = new BufferedReader(new InputStreamReader(System.in));
			String line = "";
			{	System.out.print("Please enter your SQL query OR expr Starting with {: ");
				line =  in.readLine();
				do {
					
					try {
                        Expr res_expr=null;
                        if(line.contains("{")) {
                            StringBuilder everything = new StringBuilder();
                            everything.append(line);
                            while((line = in.readLine()) != null && !line.equals("") ) {
                                everything.append(line);
                                if(line.equals("}") )
                                    break;
                            }

                            System.out.println("END2");

                            System.out.println(everything.toString());

                             res_expr= JsonHelper.fromJson(everything.toString(), Expr.class);//
                        }else{
                            line = TaJoModule.check_insert(line);
                             res_expr = TaJoModule.parseQuery(line);
                        }
                        System.out.println("END");

						if (res_expr != null )
							System.out.println("Expr: "+res_expr.toJson() +" end");
						else
							System.out.println("No Expr");
                        String res=null;
                        if(res_expr.getType().equals(OpType.CreateIndex)) {
                            res = res_expr.toJson();
                            CreateIndex newExpr= JsonHelper.fromJson(res, CreateIndex.class);//
//                            JsonArray columnNames = conf.getObject("CreateIndex").getArray("SortSpecs");
//                            JsonArray values = conf.getObject("body").getArray("exprs");
//                            JsonArray primaryArray = conf.getObject("Projection").getArray("TableName");

                        }
                        else
						    res = TaJoModule.Optimize(session,res_expr );//.Optimize(session, line);//res_expr.toJson();//;

						if (res != null){
							 
							System.out.println(res);
							try {
//								PrintWriter writer = new PrintWriter("plan.json", "UTF-8");
//								writer.println("Query: " + line);
//								writer.println(CoreGsonHelper.getPrettyInstance().toJson(plan));
//								writer.close();

								//System.out.println("Plan0: \n" + newPlan.toString());// CoreGsonHelper.getPrettyInstance().toJson(newPlan));
								//System.out.println("Run optimization ");
								
								PrintWriter writer = new PrintWriter("optimized" + ".json", "UTF-8");
								writer.println("Query: " + line);
								writer.println(res);
								writer.close();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					} catch (SQLSyntaxError e)
					{
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.print("\nPlease enter your SQL query: ");
				} while ((line = in.readLine()) != null);

			}

		} catch (IOException e) {
			System.err.println("IOException reading System.in" + e.toString());
			 
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
	}



}
