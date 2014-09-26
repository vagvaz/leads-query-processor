/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author John Demertzis
 */
public class CStore {
    HashMap<String, Etuple> EDB; //The encrypted Database
    HashMap<String, ArrayList<String>> TSet; //the inverted index

    public CStore() {
        this.EDB = new HashMap<String, Etuple>();
        this.TSet = new HashMap<String, ArrayList<String>>();
    }

    public CStore(HashMap<String, Etuple> EDB, HashMap<String, ArrayList<String>> TSet) {
        this.EDB = EDB;
        this.TSet = TSet;
    }

    public HashMap<String, Etuple> getEDB() {
        return this.EDB;
    }

    public void setEDB(HashMap<String, Etuple> EDB) {
        this.EDB = EDB;
    }

    public HashMap<String, ArrayList<String>> getTSet() {
        return this.TSet;
    }

    public void setTSet(HashMap<String, ArrayList<String>> TSet) {
        this.TSet = TSet;
    }
}
