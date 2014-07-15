package leads.tajo.module;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.tajo.engine.parser.SQLSyntaxError;
import org.apache.tajo.master.session.Session;

import static org.apache.tajo.TajoConstants.DEFAULT_DATABASE_NAME;

import java.io.IOException;


public class TajoModuleTest {
	private static   TaJoModule Mymodule;
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Session session = new Session("0", "LeadsTestModule",
							DEFAULT_DATABASE_NAME);
		
		Mymodule = new TaJoModule();
		Mymodule.init_connection("127.0.0.1", 5998);
		BufferedReader in = null;
		try {
			
			in = new BufferedReader(new InputStreamReader(System.in));
			String line = "";
			{	System.out.print("Please enter your SQL query: ");		
				line =  in.readLine();
				do {
					
					try {
						System.out.println(line);

						String res = TaJoModule.Optimize(session, line);
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
