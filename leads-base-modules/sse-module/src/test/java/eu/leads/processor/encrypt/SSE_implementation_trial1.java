/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.leads.processor.encrypt;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SSE_implementation_trial1 {
    
    public static CStore encrypted_upload(int Svalue, double k , String sk_fileName, String inputFileName) throws Exception{
        ClientSide client = new ClientSide(Svalue, k,sk_fileName);
        CStore store = client.Setup(inputFileName, 3);
    return store;
    }
    
    
    public static CStore encrypted_upload(int Svalue, double k , String sk_fileName, Connection conn, String tableName, String columnName) throws Exception{
        
        ClientSide client = new ClientSide(Svalue, k, sk_fileName);
        CStore store = client.Setup(conn,"EmployeeRecords","salary");
        
    return store;
    }
    
    public static CStore get(CStore store, String value,  String sk_fileName) throws Exception{
        //Client
        ClientSide client = new ClientSide(sk_fileName);
        String token = client.TSetGetTag(value);
        //Server
        ServerSide server = new ServerSide(store.getSvalue(), store.getBvalue());
        //Inform finish
        //Client REad
        Map<String, ArrayList<Etuple>> resultDB = new HashMap<>();
                server.TSetRetrieve(store, token,resultDB);
        client.Decrypt_Answer(resultDB);
    return null;
    }
    
    public static void main(String[] args) throws Exception {

        //String fileName = "C:\\Users\\John\\Dropbox\\master\\LEADS\\Implementations\\SSE\\Dataset.txt";
        String fileName = "src/main/resources/Dataset.txt";
        double k = 1.1;
        int N = 389032;
        int Svalue = 6000;
        int Bvalue = 10;
        int maximum_tuple_size = 120;
        String sk_fileName = "src/main/resources/Keys";
        String keyfilename = "/home/vagvaz/keys.txt";
//        CStore store = encrypted_upload(Svalue, k, sk_fileName,fileName);
        CStore store = encrypted_upload(Svalue,k,keyfilename,"/home/vagvaz/Dataset.txt");
        get(store, "55574",keyfilename);
        
        DB db = new DB();
        //db.intilialize();
        CStore storeDB = encrypted_upload(Svalue, k, keyfilename,db.getConnection(),"EmployeeRecords","salary");
        get(storeDB, "88911",sk_fileName);
        //ClientSide clientDB = new ClientSide(Svalue, k, "/home/john/Dropbox/master/LEADS/Implementations/SSE/Keys");
        //CStore storeDB = clientDB.Setup(db.getConnection(),"EmployeeRecords","salary");
        //CStore storeDB = clientDB.Setup(db.getConnection(),"EmployeeRecords","salary", N, maximum_tuple_size);
        db.shutdown();
        /*
        String tokenDB = clientDB.TSetGetTag("88911");
        ServerSide serverDB = new ServerSide(clientDB.getSvalue(), clientDB.getBvalue());
        
        HashMap<String, ArrayList<Etuple>> resultDB = serverDB.TSetRetrieve(storeDB, tokenDB);
        clientDB.Decrypt_Answer(resultDB);*/

    }
}
