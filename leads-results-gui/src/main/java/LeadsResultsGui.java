import eu.leads.processor.core.Tuple;
import eu.leads.processor.web.QueryResults;
import eu.leads.processor.web.QueryStatus;
import eu.leads.processor.web.WebServiceClient;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.jdom.JDOMException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.*;

public class LeadsResultsGui extends JPanel {
    transient protected static Random r;
    private static String host;
    private static int port;
    private static String username;
    private static Vector<Vector> rowdata;
    private static Vector<String> columnNames;

    protected long rowsC = 60;
    protected String[] loc = {"a", "b", "c", "d"};
    private boolean DEBUG = false;

    static XMLConfiguration config =null;


    public LeadsResultsGui(Vector<Vector> data, Vector<String> columnNames) {
        super(new GridBagLayout());


        final JTable table = new JTable(data, columnNames);

        table.setPreferredScrollableViewportSize(new Dimension(1000, 220));
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }
        //table.getColumnModel().getColumn(1).setCellRenderer(new DecimalFormatRenderer());
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        JTextArea textField = new JTextArea(5, 20);
        //Add the scroll pane to this panel.
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        //add(textField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
        table.setAutoResizeMode(JTable.WIDTH);
    }

    static Vector<String> TestColumnNames() {
        Vector<String> columnNames = new Vector<String>();

        columnNames.add("domainName");
        columnNames.add("avg(pagerank)");
        columnNames.add("avg(sentimentScore)");


        return columnNames;
    }

    static Vector<Vector> TestData() {


        String[] domainnames = {"www.twitter.com",
                "www.bbc.co.uk",
                "www.amazon.com",
                "www.ebay.com",
                "www.adidas-group.com",
                "www.sportsdirect.com/adidas",
                "www.jdsports.co.uk",
                "www.size.co.uk",
                "www.endclothing.co.uk",

        };
        double[] avgPageRank;
        avgPageRank = new double[]{0.000010,
                0.000009f,
                0.000008f,
                0.000008,
                0.000006f,
                0.000005,
                0.000005f,
                0.000004,
                0.000004f};
        double[] avgSentimentScore;
        avgSentimentScore = new double[]{0.693,
                0.725,
                0.589,
                0.658,
                0.902,
                0.962,
                0.754,
                0.854,
                0.654};

        Vector<Object> row;// = new Vector<Object>();

//        for(int i=0;i<rowsC;i++){
//            row = new Vector<Object>();
//            row.addElement(getRandomDomain());
//            row.addElement(new Float(nextFloat(0,0.01f)));
//            row.addElement(new Float(nextFloat(-1f,1f)));
//            data.add(row);
//        }
        Vector<Vector> data = new Vector<Vector>();
        for (int i = 0; i < domainnames.length; i++) {
            row = new Vector<Object>();
            row.addElement(domainnames[i]);
            row.addElement(new Double(avgPageRank[i]));
            row.addElement(new Double(avgSentimentScore[i]));
            data.add(row);
        }
        return data;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI(Vector<Vector> data, Vector<String> columnNames) {
        r = new Random(0);
        String title =" LEADS - RESULTS ";

                    title +="user: " + username;


        //Create and set up the window.
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JPanel p = new JPanel(new BorderLayout()); //PREFERRED!
        LeadsResultsGui newContentPane = new LeadsResultsGui(data, columnNames);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws JDOMException, InterruptedException {
        username ="Leads-gui";
         //Read xml
        if(args.length<1) {
            System.err.println("Not enought arguments Exiting");

            return;
        }
        try {
            config = new XMLConfiguration("leads-results-gui-settings.xml");
            System.err.println("Found Xml settings file");

        } catch (ConfigurationException e) {
           System.err.print("Xml error: " + e.getMessage());
        }


        File xmlFile = new File(args[0]);

        System.out.println("Trying to open file: " + args[0]);
        if(xmlFile.exists()){
            System.out.println("File Exists");
        }
        else {
            System.err.println("File DOES NOT Exists");
            return ;
        }
        //Send Expr Json for execution
        //Wait for results
        try {
            Apatar2Tajo.init_string_maps();
            convert_results(send_query_and_wait(Apatar2Tajo.xml2tajo(xmlFile).toJson()));
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }

        // Display Data
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //
        if(rowdata!=null)
        if(rowdata.size()>0)
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(rowdata,  columnNames);
            }
        });
        else{
            System.err.println("No results");
        }
    }

    private static void InitializeWebClient(String args[]) {
        host = "http://localhost";
        port = 8080;

        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        if(config!=null){
            System.err.print("Xml file ");

            if(config.containsKey("server.host"))
                host = "http://"+config.getString("server.host");

            if(config.containsKey("server.port"))
                port = config.getInt("server.port");

            if(config.containsKey("server.username"))
                    if(config.getString("server.username").length()>0)
                        username =config.getString("server.username");
        }


        try {
            WebServiceClient.initialize(host, port);
            System.out.println("Connected at " + host + ":" + port + " Successful");
        } catch (MalformedURLException e) {
            System.err.println("Unable to connect at " + host + ":" + port + " . Exiting");
            e.printStackTrace();
            System.exit(-1);
        }


    }
    //the actionPerformed method in this class
    //when the user presses the start button

    static QueryResults send_query_and_wait(String json) throws IOException, InterruptedException {
        if(json==null){
            JOptionPane.showMessageDialog(null,"Bad Apatar format.","Leads Results",JOptionPane.ERROR_MESSAGE);
            return null;
        }

        System.out.print("json: " + json.toString());
        JButton cancelButton;
        JFrame f = new JFrame("Progress");
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(new ButtonListener());
        cancelButton.setPreferredSize(new Dimension(80, 30));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        Container content = f.getContentPane();
        JProgressBar progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        Border border = BorderFactory.createTitledBorder("Sending...");
        Thread.sleep(200);
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
        content.add(cancelButton, BorderLayout.SOUTH);
        f.setSize(300, 100);
        f.setVisible(true);

        InitializeWebClient(new String[]{});
        progressBar.setValue(10);
        QueryStatus  currentStatus = WebServiceClient.submitWorkflow(username, json);
        progressBar.setValue(20);
        int value = 20;
        System.out.print("Waiting for results: ");
        progressBar.setBorder(BorderFactory.createTitledBorder("Waiting for results ... "));
        while(!currentStatus.getStatus().equals("COMPLETED") && !currentStatus.getStatus().equals("FAILED")){
            try {
                value = (87 - value )/3 + value;
                progressBar.setValue(value);
                Thread.sleep(2000);
                System.out.print(" - ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentStatus = WebServiceClient.getQueryStatus(currentStatus.getId());

        }
        Thread.sleep(200);
        progressBar.setValue(95);
        Thread.sleep(200);
        if(currentStatus.getStatus().equals("COMPLETED")) {
            QueryResults results = WebServiceClient.getQueryResults(currentStatus.getId(), 0, -1);
            System.out.println("Workflow query results size: " + results.getResult().size());
            progressBar.setBorder(BorderFactory.createTitledBorder("Completed..."));
            progressBar.setValue(100);
            Thread.sleep(200);
            f.setVisible(false);
            f.dispose();
            return results;
        }else{
            JOptionPane.showMessageDialog(null,"Workflow query " + currentStatus.getStatus().toString(),"Leads Results",JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Workflow query " + currentStatus.getStatus().toString());
        }
        return null;
    }

    private static void convert_results(QueryResults data) {
        if (data==null) {
            JOptionPane.showMessageDialog(null,"Error no data received, Failed","Leads Results",JOptionPane.WARNING_MESSAGE);
            System.out.println("Error no data received, Failed");
            System.exit(0);
//            return;
        }
        ArrayList<Tuple> resultSet = new ArrayList<Tuple>();
        for (String s : data.getResult()) {
            if (s== null || s.equals("") )
                continue;
            resultSet.add(new Tuple(s));
        }


        boolean firstTuple = true;
        if (resultSet.size() == 0) {
            JOptionPane.showMessageDialog(null,"EMPTY RESULTS","Leads Results",JOptionPane.WARNING_MESSAGE);
            System.out.println("EMPTY RESULTS");
            return;
        }
        int length = resultSet.size();
        int width = resultSet.get(0).getFieldSet().size();
        Set<String> fields = resultSet.get(0).getFieldSet();

        columnNames = new Vector<String>();

        //Read fields
        for (String field : fields) {
             String[] splField = field.split("\\.");

            columnNames.add(splField[splField.length-1]);
            System.out.println("Column:" + splField[splField.length-1]) ;
        }

        rowdata = new Vector<Vector>();
        Vector<Object> row;
        Locale.setDefault(new Locale("en", "US"));
        for (Tuple t : resultSet) {
            row = new Vector<Object>();
            for (String field : fields){
                Object value = t.getGenericAttribute(field);
                //System.out.print("Class: " + value.getClass());

                if(value != null ) {
                    //if(value.getClass() == Double.class)

                    System.out.print("| " + value.toString()) ;
                    row.addElement(new String(value.toString()));

                }
                else
                    row.addElement("(NULL)");
            }
            rowdata.add(row);
            System.out.println("| " ) ;
        }

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

    private void printDebugData(JTable table) {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        System.out.println("Value of data: ");
        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

    static class DecimalFormatRenderer extends DefaultTableCellRenderer {
        private static final DecimalFormat formatter = new DecimalFormat("0.000000");

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // First format the cell value as required
            value = formatter.format((Number) value);
            // And pass it on to parent class
            return super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
        }
    }
}

class ButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
        System.exit(0);
    }
}