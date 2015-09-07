package sse_implementation_trial1;

import java.util.HashMap;

/**
 * @author John Demertzis
 */
public class CStore {
    HashMap<String, Etuple> EDB; //The encrypted Database
    HashMap<Integer, Record[]> TSet; //the inverted index
    int Bvalue;
    int Svalue;

    public CStore() {
        this.EDB = new HashMap<String, Etuple>();
        this.TSet = new HashMap<Integer, Record[]>();
    }

    public CStore(HashMap<String, Etuple> EDB, HashMap<Integer, Record[]> TSet) {
        this.EDB = EDB;
        this.TSet = TSet;
    }

    public CStore(HashMap<String, Etuple> EDB, HashMap<Integer, Record[]> TSet, int Bvalue, int Svalue) {
        this.EDB = EDB;
        this.TSet = TSet;
        this.Bvalue = Bvalue;
        this.Svalue = Svalue;
    }

    public HashMap<String, Etuple> getEDB() {
        return this.EDB;
    }

    public HashMap<Integer, Record[]> getTSet() {
        return this.TSet;
    }

    public void setEDB(HashMap<String, Etuple> EDB) {
        this.EDB = EDB;
    }

    public void setTSet(HashMap<Integer, Record[]> TSet) {
        this.TSet = TSet;
    }


    public void setBvalue(int Bvalue) {
        this.Bvalue = Bvalue;
    }

    public void setSvalue(int Svalue) {
        this.Svalue = Svalue;
    }

    public int getBvalue() {
        return this.Bvalue;
    }

    public int getSvalue() {
        return this.Svalue;
    }
}
