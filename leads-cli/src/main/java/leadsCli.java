import eu.leads.processor.common.utils.PrettyPrinter;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.web.QueryResults;
import eu.leads.processor.web.QueryStatus;
import eu.leads.processor.web.WebServiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        System.out.println("=== Leads Command Line Interface ===");

        ArrayList<String> sqlCmds = new ArrayList<>();
        String nonTerminalString="";
        BufferedReader in = null;
        try {

            in = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            {
                System.out.print("Using default username leads ");
//                username = in.readLine();
                username = "leads";


                do {

                    try {
                        System.out.print("\nPlease enter your SQL query: ");
                        line = in.readLine();

                        nonTerminalString+=line;
                        if (nonTerminalString.contains(";")) {
                            String[] parts = nonTerminalString.split(";",0);
                            for(int i=0;i<parts.length-1;i++) {
                                if (parts[i].length() > 3)
                                    sqlCmds.add(parts[i] + ";");
                            }
                            if(nonTerminalString.trim().endsWith(";")) {
                                sqlCmds.add(parts[parts.length - 1]);
                                nonTerminalString = "";
                            }else{
                                nonTerminalString +=parts[parts.length - 1];
                            }
                            if(sqlCmds.size()>0){
                                int count=0;

                                for(String sql:sqlCmds)
                                {
                                    count++;
                                    if(sql.toLowerCase().equals("quit")) {
                                        System.out.println("Exiting, Thank you");
                                        System.exit(0);
                                    }
                                    System.out.println("#"+count+"/"+ sqlCmds.size() + " Executing command: " + sql);
                                    send_query_and_wait(sql);
                                }
                                sqlCmds.clear();
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        if(e instanceof  java.net.ConnectException){
                            System.out.println("Exiting, Thank you");
                            System.exit(0);
                        }
                    }
                } while (true);

            }
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
            if(WebServiceClient.initialize(host, port))
                System.out.println("Connected at " + host + ":" + port);
            else
                System.exit(-1);

        }catch (Exception e1) {
                System.err.println("Unable to connect at " + host + ":" + port + " . Exiting");
                e1.printStackTrace();
                System.exit(-1);

        }
//        catch (MalformedURLException e) {
//            System.err.println("Unable to connect at " + host + ":" + port + " . Exiting");
//            e.printStackTrace();
//            System.exit(-1);
//        }


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

    static void send_query_and_wait(String sql) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        long resultArrived, resultPrinted;
        QueryStatus currentStatus = WebServiceClient.submitQuery(username, sql);
        while(!currentStatus.getStatus().equals("COMPLETED") && !currentStatus.getStatus().equals("FAILED")) {
            sleep(2000);
            currentStatus = WebServiceClient.getQueryStatus(currentStatus.getId());
//            System.out.print("s: " + currentStatus.toString());
//            System.out.println(", o: " + currentStatus.toString());
            //System.out.println("The query with id " + currentStatus.getId() + " is " + currentStatus.getStatus());
            System.out.print(".");

        }  //currentStatus.getStatus()!= QueryState.COMPLETED
        Date curr_date = new Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("\n"+df.format(curr_date)+" The query with id " + currentStatus.getId() + " " + currentStatus.getStatus());
        if(currentStatus.getStatus().equals("COMPLETED")) {
            System.out.println("Please wait ...");
            QueryResults res = WebServiceClient.getQueryResults(currentStatus.getId(), 0, -1);
            resultArrived = System.currentTimeMillis();
            print_results(res);
            resultPrinted  = System.currentTimeMillis();

            System.out.println("Found " + res.getResult().size() + " results.");
            System.out.println("Result acquisition (execution + delivery) time: " +  (resultArrived - start) + " ms.");
            System.out.println("Display time: " +  (resultPrinted-resultArrived) + " ms.");
        }
        else{
            System.err.println("Execution terminated with error : " + currentStatus.getErrorMessage());
        }

    }

    private static void print_results(QueryResults data){
        if(data==null){
            System.out.println("Error occurred!!");
            return;
        }
        ArrayList<Tuple> resultSet = new ArrayList<Tuple> ();
        for (String s : data.getResult()) {
          if (s== null || s.equals("") )
            continue;
          resultSet.add(new Tuple(s));
        }
       printResults(resultSet);



    }

    //Print the results of the query
    private static void printResults(ArrayList<Tuple> resultSet) {
        if(resultSet==null){
            System.out.println("Error occurred!!");
            return;
        }

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
                Object value = t.getGenericAttribute(field);
                if(value != null )
                outputTable[rowCount][colCount] = value.toString();
                else
                outputTable[rowCount][colCount] = "(NULL)";
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
