import eu.leads.processor.core.Tuple;
import eu.leads.processor.common.utils.PrettyPrinter;
import eu.leads.processor.web.QueryResults;
import eu.leads.processor.web.QueryStatus;
import eu.leads.processor.web.WebServiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import static java.lang.Thread.sleep;

public class leadsCli {
    transient protected static Random r;
    private static String host;
    private static int port;
    private static String username;
    protected long rowsC = 60;
    protected String[] loc = {"a", "b", "c", "d"};
    private boolean DEBUG = false;

    public static void main(String[] args) {
        InitializeWebClient(args);
        System.out.print("=== Leads Command Line Interface ===");
        String sql = "";
        BufferedReader in = null;
        try {

            in = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            {
                System.out.print("Please enter your username: ");
                username = in.readLine();
                System.out.print("Please enter your SQL query: ");
                line = in.readLine();

                do {

                    try {

                        if (!line.contains(";")) {
                            StringBuilder everything = new StringBuilder();
                            everything.append(line);
                            while ((line = in.readLine()) != null && !line.equals("")) {
                                everything.append(line);
                                if (line.contains(";"))
                                    break;
                            }
                            System.out.println(everything.toString());
                            sql = everything.toString();
                        } else {
                            sql = line;
                        }

                        String[] parts = sql.split(";");
                        System.out.println("#Sql commands: " + parts.length);

                        //Get first command

                        sql = parts[0] + ";";

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    print_results(send_query_and_wait(sql));

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

    private static void InitializeWebClient(String args[]) {
        host = "http://localhost";
        port = 8080;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        try {
            WebServiceClient.initialize(host, port);
            System.err.println("Connected at " + host + ":" + port );
        } catch (MalformedURLException e) {
            System.err.println("Unable to connect at " + host + ":" + port + " . Exiting");
            e.printStackTrace();
            System.exit(-1);
        }


    }
    static QueryResults send_query_and_wait(String sql) throws IOException, InterruptedException {

        QueryStatus status = WebServiceClient.submitQuery(username,sql);
        QueryStatus currentStatus;
        do {
            sleep(3000);
            currentStatus = WebServiceClient.getQueryStatus(status.getId());
            System.out.print("s: " + status.toString());
            System.out.println(", o: " + currentStatus.toString());
        }while (currentStatus.getStatus().toLowerCase().contains("completed")); //currentStatus.getStatus()!= QueryState.COMPLETED
        QueryResults res =WebServiceClient.getQueryResults(currentStatus.getId(),0,-1);
        return res;
    }

    private static void print_results(QueryResults data){
        ArrayList<Tuple> resultSet = new ArrayList<Tuple> ();
        for (String s : data.getTuples())
            resultSet.add(new Tuple(s));
        printResults(resultSet);



    }
    public float nextFloat(float min, float max) {
        return min + r.nextFloat() * (max - min);
    }

    protected String getRandomDomain() {
        int l = 10;
        String result = "";
        for (int i = 0; i < l; i++) {
            result += loc[r.nextInt(loc.length)];
        }
        return "www." + result + ".com";
    }

    private void printDebugData() {
        int numRows = 0;
        int numCols = 0;

        System.out.println("Value of data: ");
        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  ");
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }



    //Print the results of the query
    private static void printResults(ArrayList<Tuple> resultSet) {
        boolean firstTuple = true;
        if (resultSet.size() == 0) {
            System.out.println("EMPTY RESULTS");
            return;
        }
        int length = resultSet.size();
        int width = resultSet.get(0).getFieldSet().size();
        String[][] outputTable = new String[length + 1][width];
        Set<String> fields = resultSet.get(0).getFieldSet();
        int rowCount = 0;
        int colCount = 0;

        //Read fields
        for (String field : fields) {
            outputTable[rowCount][colCount] = field;
            colCount++;
        }

        for (Tuple t : resultSet) {
            rowCount++;
            colCount = 0;
            for (String field : fields) {
                outputTable[rowCount][colCount] = t.getAttribute(field);
                colCount++;
            }
        }
        //Show results to System out
        PrettyPrinter printer = new PrettyPrinter(System.out);
        printer.print(outputTable);
        resultSet.clear();
        printer = null;
//        outputTable = null;

    }

}