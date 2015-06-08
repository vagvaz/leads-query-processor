package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.infinispan.LeadsReducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.util.*;

/**
 * Created by vagvaz on 11/21/14.
 */
public class JoinReducer extends LeadsReducer<String,Tuple> {
    //   transient JsonObject conf;
    //   String configString;
    private transient String prefix;
    transient Logger profilerLog;
    protected ProfileEvent profCallable;

    public JoinReducer(String s) {
        super(s);
        configString = s;
        profilerLog  = LoggerFactory.getLogger("###PROF###" + this.getClass().toString());
        profCallable = new ProfileEvent("JoinReducer Construct" + this.getClass().toString(),profilerLog);
    }

    @Override
    public void initialize() {
//        profilerLog  = LoggerFactory.getLogger("###PROF###" +  this.getClass().toString());
//        profCallable.setProfileLogger(profilerLog);
//        if(profCallable!=null) {
//            profCallable.end("reduce init ");
//        } else {
//            profCallable = new ProfileEvent("reduce init " + this.getClass().toString(), profilerLog);
//        }
//        profCallable.start("reduce init ");
        super.initialize();
        isInitialized = true;
        conf = new JsonObject(configString);
        prefix = outputCacheName+":";
        //      prefix = outputCacheName+":";
        //      outputCache = (Cache) InfinispanClusterSingleton.getInstance().getManager().getPersisentCache(conf.getString("output"));
//        profCallable.end("reduce init");
    }

    @Override
    public void reduce(String reducedKey, Iterator<Tuple> iter,LeadsCollector collector) {

        profilerLog  = LoggerFactory.getLogger("###PROF###" +  this.getClass().toString());
        profCallable.setProfileLogger(profilerLog);
        if(profCallable!=null) {
            profCallable.end("reduce reduce ");
        } else {
            profCallable = new ProfileEvent("reduce reduce " + this.getClass().toString(), profilerLog);
        }

        if(!isInitialized)
            initialize();
        Map<String,List<Tuple>> relations = new HashMap<>();

        profCallable.start("reduce hasNext ");
        while(true){
            //         String jsonTuple = iter.next();
            //         Tuple t = new Tuple(jsonTuple);
            try {
                Tuple t = null;

                ProfileEvent tmpprofCallable = new ProfileEvent("JoinReducer Manager " + this.getClass().toString(),
                    profilerLog);
                tmpprofCallable.start("reduce next");
                Object c = iter.next();
                tmpprofCallable.end("reduce next");
                //            if(c instanceof Tuple )
                t = (Tuple) c;
                //            else{
                //                continue;
                //            }


                String table = t.getAttribute("__table");
                t.removeAttribute("__table");
                List<Tuple> tuples = relations.get(table);
                if (tuples == null) {
                    tuples = new ArrayList<>();
                    relations.put(table, tuples);
                }
                assert (t.hasField("__tupleKey"));

                tmpprofCallable.start("reduce add");
                tuples.add(t);
                tmpprofCallable.end("reduce add");
            }catch (Exception e){
                if(e instanceof NoSuchElementException){
                    profilerLog.info("End of LeadsIntermediateIterator");
                    break;
                }
                else{

                    profilerLog.error("EXCEPTION WHILE updating agg value");
                    profilerLog.error(e.getClass() + " " + e.getMessage());
                    profilerLog.error(iter.toString());
                }
            }
        }
        profCallable.end("reduce iter.hasNext ");

        profilerLog  = LoggerFactory.getLogger("###PROF###" +  this.getClass().toString());
        profCallable.setProfileLogger(profilerLog);
        if(profCallable!=null) {
            profCallable.end("reduce reduce ");
        } else {
            profCallable = new ProfileEvent("reduce reduce " + this.getClass().toString(), profilerLog);
        }

        profCallable.start("reduce proc ");
        if(relations.size() < 2)
            return;
        ArrayList<List<Tuple>> arrays = new ArrayList<>(2);
        for(List<Tuple> a : relations.values()){
            arrays.add(a);
        }

        for(int i = 0; i < arrays.get(0).size(); i++){
            Tuple outerTuple = arrays.get(0).get(i);
            assert(outerTuple.hasField("__tupleKey"));
//            System.err.println("outer " + outerTuple.toString());
            String outerKey = outerTuple.getAttribute("__tupleKey");
            if(outerKey == null){
                System.out.println("outerTuple " + outerTuple.toString());
            }
            for(int j = 0; j <  arrays.get(1).size(); j++){
                Tuple innerTuple = arrays.get(1).get(j);
//                outerTuple.removeAttribute("__tupleKey");
                String outerKey2 = innerTuple.getAttribute("__tupleKey");
                if(outerKey2 == null){
                    System.out.println("innerTuple " + innerTuple.toString());
                }
                assert(innerTuple.hasField("__tupleKey"));
                Tuple resultTuple = new Tuple(innerTuple, outerTuple,null);
//                resultTuple.removeAttribute("__tupleKey");
                String combinedKey = outerKey + "-" + outerKey2;
                resultTuple = prepareOutput(resultTuple);
//                resultTuple = prepareOutput(resultTuple);
                //            outputCache.put(combinedKey, resultTuple.asJsonObject().toString());
                collector.emit(prefix+combinedKey,resultTuple);
            }
        }
        profCallable.end("reduce proc ");
        return ;
    }
}
