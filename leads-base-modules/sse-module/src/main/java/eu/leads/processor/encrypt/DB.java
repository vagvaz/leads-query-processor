/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.leads.processor.encrypt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *
 * @author John Demertzis
 */

public class DB {

    //private static String dbURL = "jdbc:derby://localhost:1527/myDB;create=true;user=me;password=mine";
    private String dbURL = "jdbc:derby:/home/john/DB/MyDbTest";
    private String tableName = "EmployeeRecords";
    // jdbc Connection
    private Connection conn = null;
    private Statement stmt = null;

    public DB(String dbURL, String tableName) {
        this.dbURL = dbURL;
        this.tableName = tableName;
        createConnection();
    }

    public DB() {
        createConnection();
    }

    public void intilialize() {
        createExampleTable();
        selectStar();
        shutdown();
    }

    public void dropTable() {
        try {
            stmt = conn.createStatement();
            stmt.execute("drop table EmployeeRecords");
            stmt.close();
        } catch (Exception except) {
        }

    }

    public void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            conn = DriverManager.getConnection(dbURL);
        } catch (Exception except) {
            except.printStackTrace();
        }
    }

    private void createExampleTable() {
        String line = "";
        int id = 0;
        try {
            dropTable();
            stmt.execute("create table EmployeeRecords(fname varchar(40), lname varchar(40), job varchar(40), salary int, yearOfBirth varchar(40), birthCity varchar(40), currentCity varchar(40),stateAb varchar(10), postCode varchar(40))");

            String fileName = "/home/john/Dropbox/master/LEADS/Implementations/SSE/Dataset.txt";
            String lineArray[];

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            stmt = conn.createStatement();
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replace("'", " ");
                lineArray = line.split(",");
                stmt.execute("insert into " + tableName + " values ("
                        + "'" + lineArray[0] + "','"
                        + lineArray[1] + "','"
                        + lineArray[2] + "',"
                        + Integer.parseInt(lineArray[3]) + ",'"
                        + lineArray[4] + "','"
                        + lineArray[5] + "','"
                        + lineArray[6] + "','"
                        + lineArray[7] + "','"
                        + lineArray[8] + "')");
                id++;

            }
            stmt.close();
        } catch (Exception except) {
            System.out.println(id);
            System.out.println(line);
            except.printStackTrace();
        }
    }

    public void selectStar() {
        try {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + tableName + " T0 order by salary");
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
            for (int i = 1; i <= numberCols; i++) {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(i) + "\t");
            }

            System.out.println("\n-------------------------------------------------------------------------------------------------------");
            int id;
            while (results.next()) {
                for (int i = 1; i <= numberCols; i++) {
                    System.out.print(results.getString(i) + "\t");
                }
                System.out.println();
            }
            results.close();
            stmt.close();
        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                DriverManager.getConnection(dbURL + ";shutdown=true");
                conn.close();
            }
        } catch (SQLException sqlExcept) {
        }
    }

    public Connection getConnection() {
        return this.conn;
    }
}
