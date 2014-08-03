/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;

/**
 * @author John Demertzis
 */
public class SSE_implementation_trial1 {

   public static void main(String[] args) throws Exception {

      String fileName = "src/main/resources/Dataset.txt";
      double k = 1.1;
      int N = 389032;
      int Svalue = 6000;
      int maximum_tuple_size = 120;

      System.out.println("Working Directory = " +
                                 System.getProperty("user.dir"));

      ClientSide client = new ClientSide(Svalue, k, N, 128, maximum_tuple_size);
      CStore store = client.DB_preprossesing(fileName, N);

      HashMap<Integer, Record[]> TSet = client.TSetSetup(store);
      String token = client.TSetGetTag("88911");

      ServerSide server = new ServerSide(Svalue, k, N);
      client.Decrypt_Answer(store.getEDB(), server.TSetRetrieve(token, TSet));
   }
}
