/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import eu.leads.processor.common.Tuple;
import eu.leads.processor.web.QueryResults;
import eu.leads.processor.web.QueryStatus;
import eu.leads.processor.web.WebServiceClient;
import org.jdom.JDOMException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import static java.lang.Thread.sleep;

public class leadsResultsGui extends JPanel {
    transient protected static Random r;
    private static String host;
    private static int port;
    private static String username;
    private static Vector<Vector> rowdata;
    private static Vector<String> columnNames;
    private static Apatar2Tajo converter;
    protected long rowsC = 60;
    protected String[] loc = {"a", "b", "c", "d"};
    private boolean DEBUG = false;

    public leadsResultsGui(Vector<Vector> data, Vector<String> columnNames) {
        super(new GridBagLayout());


        final JTable table = new JTable(data, columnNames);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }
        table.getColumnModel().getColumn(1).setCellRenderer(new DecimalFormatRenderer());
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

        //Create and set up the window.
        JFrame frame = new JFrame(" LEADS - RESULTS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JPanel p = new JPanel(new BorderLayout()); //PREFERRED!
        leadsResultsGui newContentPane = new leadsResultsGui(data, columnNames);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws JDOMException, InterruptedException {
        converter = new Apatar2Tajo();
        //Read xml
        if(args.length<1) {
            System.err.println("Not enought arguments Exiting");
            return;
        }

        File xmlFile = new File(args[1]);

        System.out.println("Trying to open file " + args[1]);
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
            convert_results(send_query_and_wait(Apatar2Tajo.xml2tajo_json(xmlFile)));
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }

        // Display Data
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //
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
        String host = "http://localhost";
        int port = 8080;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        try {
            WebServiceClient.initialize(host, port);

            System.err.println("Connected at " + host + ":" + port + " . Exiting");
        } catch (MalformedURLException e) {
            System.err.println("Unable to connect at " + host + ":" + port + " . Exiting");
            e.printStackTrace();
            System.exit(-1);
        }


    }

    static QueryResults send_query_and_wait(String json) throws IOException, InterruptedException {

        QueryStatus status = WebServiceClient.submitQuery("LeadsGui", json);
        QueryStatus currentStatus;
        do {
            sleep(3000);
            currentStatus = WebServiceClient.getQueryStatus(status.getId());
            System.out.print("s: " + status.toString());
            System.out.println(", o: " + currentStatus.toString());
        }
        while (currentStatus.getStatus().toLowerCase().contains("completed")); //currentStatus.getStatus()!= QueryState.COMPLETED
        QueryResults res = WebServiceClient.getQueryResults(currentStatus.getId(), 0, -1);
        return res;
    }

    private static void convert_results(QueryResults data) {
        ArrayList<Tuple> resultSet = new ArrayList<Tuple>();
        for (String s : data.getTuples())
            resultSet.add(new Tuple(s));


        boolean firstTuple = true;
        if (resultSet.size() == 0) {
            System.out.println("EMPTY RESULTS");
            return;
        }
        int length = resultSet.size();
        int width = resultSet.get(0).getFieldSet().size();
        Set<String> fields = resultSet.get(0).getFieldSet();

        columnNames = new Vector<String>();

        //Read fields
        for (String field : fields)
            columnNames.add(field);


        rowdata = new Vector<Vector>();
        Vector<Object> row;

        for (Tuple t : resultSet) {
            row = new Vector<Object>();
            for (String field : fields)
                row.addElement(t.getAttribute(field));
            rowdata.add(row);
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