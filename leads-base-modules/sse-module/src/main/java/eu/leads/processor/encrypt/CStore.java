package eu.leads.processor.encrypt;

import eu.leads.processor.common.infinispan.InfinispanManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author John Demertzis
 */
public class CStore {
    Map<String, Etuple> EDB; //The encrypted Database
    Map<Integer, Record[]> TSet; //the inverted index
    int Bvalue;
    int Svalue;
    InfinispanManager manager;
    public CStore(InfinispanManager manager){
        this.manager = manager;
        this.EDB = manager.getPersisentCache("tmp.edb.cache");
        this.TSet = manager.getPersisentCache("tmp.tset.cache");
    }
    public CStore(){
        this.EDB = new HashMap<String, Etuple>();
        this.TSet = new HashMap<Integer, Record[]>();
    }
    
    public CStore(Map<String, Etuple> EDB, Map<Integer, Record[]> TSet){
        this.EDB = EDB;
        this.TSet = TSet;
    }
    
    public CStore(Map<String, Etuple> EDB, Map<Integer, Record[]> TSet, int Bvalue, int Svalue){
        this.EDB = EDB;
        this.TSet = TSet;
        this.Bvalue = Bvalue;
        this.Svalue = Svalue;
    }
    
    public Map<String, Etuple> getEDB(){
        return this.EDB;
    }
    public Map<Integer, Record[]> getTSet(){
        return this.TSet;
    }
    public  void setEDB(Map<String, Etuple> EDB){
        this.EDB=EDB;
    }
    public  void setTSet(Map<Integer, Record[]> TSet){
        this.TSet=TSet;
    }
    
    
    public void setBvalue(int Bvalue){
        this.Bvalue  =Bvalue;
    }
    public void setSvalue(int Svalue){
        this.Svalue  =Svalue;
    }
    
    public int getBvalue(){
        return  this.Bvalue;
    }
    
    public int getSvalue(){
        return  this.Svalue;
    }
}
